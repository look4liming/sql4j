package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;

/**
 * @author Bright Lee
 */
public final class DeleteStatementHashRuleAnalyzer {
	
	private Configuration configuration;
	private SourceCode sourceCode;
	private DeleteStatement deleteStatement;
	
	public DeleteStatementHashRuleAnalyzer(
			Configuration configuration,
			SourceCode sourceCode,
			DeleteStatement deleteStatement) {
		this.configuration = configuration;
		this.sourceCode = sourceCode;
		this.deleteStatement = deleteStatement;
	}
	
	public void analyze() {
		NameChain targetTable = deleteStatement.getTargetTable();
		boolean b = configuration.isTableWithHashColumns(targetTable);
		if (b == false) {
			return;
		}
		BooleanValueExpression searchCondition = deleteStatement.getSearchCondition();
		if (searchCondition == null) {
			throw Sql4jException.getSql4jException(sourceCode, deleteStatement.getBeginIndex(), 
					"Please specify all hash columns for this table in where condition.");
		}
		List<List<BooleanValueExpression>> andPathList = BooleanValueExpressionUtil.
				getAndPathList(sourceCode, searchCondition);
		String targetTableName = targetTable.toLowerCaseString();
		List<BooleanValueExpression> andPath = andPathList.get(0);
		Map<String, HashKeyValue> hashColumnMap1 = getHashColumnMap(targetTableName, andPath);
		boolean consistent = isConsistent(targetTableName, hashColumnMap1);
		if (consistent == false) {
			throw Sql4jException.getSql4jException(sourceCode, searchCondition.getBeginIndex(), 
					"The and path must contains all hash column equations.");
		}
		for (int i = 1; i < andPathList.size(); i++) {
			andPath = andPathList.get(i);
			Map<String, HashKeyValue> hashColumnMap2 = getHashColumnMap(targetTableName, andPath);
			consistent = isConsistent(targetTableName, hashColumnMap1, hashColumnMap2);
			if (consistent == false) {
				throw Sql4jException.getSql4jException(sourceCode, searchCondition.getBeginIndex(), 
						"Hash columns on and paths must be consistent.");
			}
			hashColumnMap1.clear();
			hashColumnMap1 = hashColumnMap2;
		}
		for (String hashColumnName : hashColumnMap1.keySet()) {
			HashKeyValue hashKeyValue = hashColumnMap1.get(hashColumnName);
			HashUtil.checkHashColumnAndHashValue(configuration, sourceCode, hashKeyValue);
			deleteStatement.addHashColumnName(hashKeyValue.getKey());
			deleteStatement.addHashColumnValue(hashKeyValue.getValue());
		}
	}
	
	private Map<String, HashKeyValue> getHashColumnMap(String targetTableName, List<BooleanValueExpression> andPath) {
		Map<String, HashKeyValue> hashColumnMap = new HashMap<String, HashKeyValue>();
		for (int i = 0; i < andPath.size(); i++) {
			BooleanValueExpression andNode = andPath.get(i);
			addHashColumnMap(targetTableName, hashColumnMap, andNode);
		}
		return hashColumnMap;
	}
	
	private void addHashColumnMap(String targetTableName, Map<String, HashKeyValue> hashColumnMap, 
			BooleanValueExpression andNode) {
		if (andNode instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = (ComparisonPredicate) andNode;
			addHashColumnMap(targetTableName, hashColumnMap, comparisonPredicate);
			return;
		}
		if (andNode instanceof NullPredicate) {
			NullPredicate nullPredicate = (NullPredicate) andNode;
			addHashColumnMap(targetTableName, hashColumnMap, nullPredicate);
			return;
		}
		if (andNode instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) andNode;
			addHashColumnMap(targetTableName, hashColumnMap, booleanTest);
			return;
		}
		if (andNode instanceof BooleanFactor) {
			BooleanFactor booleanFactor = (BooleanFactor) andNode;
			addHashColumnMap(targetTableName, hashColumnMap, booleanFactor);
			return;
		}
	}
	
	private void addHashColumnMap(String targetTableName, Map<String, HashKeyValue> hashColumnMap, 
			ComparisonPredicate comparisonPredicate) {
		CompOp compOp = comparisonPredicate.getCompOp();
		if (compOp != CompOp.EQUALS) {
			return;
		}
		Quantifier quantifier = comparisonPredicate.getQuantifier();
		if (quantifier != null) {
			return;
		}
		NameChain column = null;
		boolean theColumnIsOnTheLeft = false;
		ValueExpression left = comparisonPredicate.getLeft();
		if (left instanceof NameChain) {
			column = (NameChain) left;
			theColumnIsOnTheLeft = true;
		}
		ValueExpression right = comparisonPredicate.getRight();
		if (right instanceof NameChain) {
			column = (NameChain) right;
			theColumnIsOnTheLeft = false;
		}
		if (column == null || column.size() != 1) {
			return;
		}
		String columnName = targetTableName + "." + column.toLowerCaseString();
		boolean b = configuration.isHashColumn(columnName);
		if (b == false) {
			return;
		}
		ValueExpression valueExpression;
		if (theColumnIsOnTheLeft == true) {
			valueExpression = right;
		} else {
			valueExpression = left;
		}
		if (!(valueExpression instanceof Parameter) &&
			!(valueExpression instanceof StringLiteral) &&
			!(valueExpression instanceof NumericLiteral) &&
			!(valueExpression instanceof DateLiteral) &&
			!(valueExpression instanceof TimestampLiteral) &&
			!(valueExpression instanceof TimeLiteral)) {
			throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
					"Value of hash columns must be parameters or literals.");
		}
		putHashColumnMap(targetTableName, hashColumnMap, column, valueExpression);
	}
	
	private void addHashColumnMap(String targetTableName, Map<String, HashKeyValue> hashColumnMap, 
			NullPredicate nullPredicate) {
		boolean not = nullPredicate.getNot();
		if (not == true) {
			return;
		}
		ValueExpression valueExpression = nullPredicate.getValueExpression();
		if (!(valueExpression instanceof NameChain)) {
			return;
		}
		NameChain column = (NameChain) valueExpression;
		String columnName = targetTableName + "." + column.toLowerCaseString();
		boolean b = configuration.isHashColumn(columnName);
		if (b == false) {
			return;
		}
		putHashColumnMap(targetTableName, hashColumnMap, column, null);
	}
	
	private void addHashColumnMap(String targetTableName, Map<String, HashKeyValue> hashColumnMap, 
			BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = booleanTest.getBooleanValueExpression();
		TruthValue truthValue = booleanTest.getTruthValue();
		if (truthValue == null) {
			addHashColumnMap(targetTableName, hashColumnMap, booleanValueExpression);
			return;
		}
		boolean not = booleanTest.getNot();
		if (not == false && truthValue == TruthValue.TRUE) {
			addHashColumnMap(targetTableName, hashColumnMap, booleanValueExpression);
			return;
		}
	}
	
	private void addHashColumnMap(String targetTableName, Map<String, HashKeyValue> hashColumnMap, 
			BooleanFactor booleanFactor) {
		boolean not = booleanFactor.getNot();
		if (not == true) {
			return;
		}
		BooleanValueExpression booleanValueExpression = booleanFactor.getBooleanValueExpression();
		addHashColumnMap(targetTableName, hashColumnMap, booleanValueExpression);
	}
	
	private boolean isConsistent(String targetTableName, Map<String, HashKeyValue> hashColumnMap) {
		Set<String> hashColumnSet = configuration.getHashColumnSet(targetTableName);
		if (hashColumnMap.size() != hashColumnSet.size()) {
			return false;
		}
		List<String> list1 = new ArrayList<String>(hashColumnMap.size());
		for (String nameChain : hashColumnMap.keySet()) {
			String columnName = nameChain;
			list1.add(columnName);
		}
		Collections.sort(list1);
		for (String hashColumn : hashColumnSet) {
			boolean containsHashColumn = hashColumnMap.containsKey(hashColumn);
			if (containsHashColumn == false) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isConsistent(String targetTableName, Map<String, HashKeyValue> hashColumnMap1, Map<String, HashKeyValue> hashColumnMap2) {
		if (hashColumnMap1.size() != hashColumnMap2.size()) {
			return false;
		}
		List<String> list1 = new ArrayList<String>(hashColumnMap1.size());
		for (String columnName : hashColumnMap1.keySet()) {
			list1.add(columnName);
		}
		Collections.sort(list1);
		List<String> list2 = new ArrayList<String>(hashColumnMap2.size());
		for (String columnName : hashColumnMap2.keySet()) {
			list2.add(columnName);
		}
		Collections.sort(list2);
		for (int i = 0; i < list1.size(); i++) {
			String columnName1 = list1.get(i);
			String columnName2 = list2.get(i);
			if (!columnName1.equals(columnName2)) {
				return false;
			}
			ValueExpression valueExpression1 = hashColumnMap1.get(columnName1).getValue();
			ValueExpression valueExpression2 = hashColumnMap2.get(columnName2).getValue();
			boolean b = equals(valueExpression1, valueExpression2);
			if (b == false) {
				return false;
			}
		}
		Set<String> hashColumnSet = configuration.getHashColumnSet(targetTableName);
		for (String hashColumn : hashColumnSet) {
			String hashColumnName = hashColumn;
			boolean containsHashColumn = hashColumnMap1.containsKey(hashColumnName);
			if (containsHashColumn == false) {
				return false;
			}
		}
		return true;
	}
	
	private boolean equals(ValueExpression valueExpression1, ValueExpression valueExpression2) {
		if (valueExpression1 == null && valueExpression2 == null) {
			return true;
		}
		if (valueExpression1 == null || valueExpression2 == null) {
			return false;
		}
		if (valueExpression1 instanceof Parameter) {
			if (!(valueExpression2 instanceof Parameter)) {
				return false;
			}
			Parameter parameter1 = (Parameter) valueExpression1;
			Parameter parameter2 = (Parameter) valueExpression2;
			if (parameter1.getContent().equals(parameter2.getContent())) {
				return true;
			}
			return false;
		}
		if (valueExpression1 instanceof StringLiteral) {
			if (!(valueExpression2 instanceof StringLiteral)) {
				return false;
			}
			StringLiteral stringLiteral1 = (StringLiteral) valueExpression1;
			StringLiteral stringLiteral2 = (StringLiteral) valueExpression2;
			if (stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return true;
			}
			return false;
		}
		if (valueExpression1 instanceof NumericLiteral) {
			if (!(valueExpression2 instanceof NumericLiteral)) {
				return false;
			}
			NumericLiteral numericLiteral1 = (NumericLiteral) valueExpression1;
			NumericLiteral numericLiteral2 = (NumericLiteral) valueExpression2;
			if (numericLiteral1.getContent().equals(numericLiteral2.getContent())) {
				return true;
			}
			return false;
		}
		if (valueExpression1 instanceof DateLiteral) {
			if (!(valueExpression2 instanceof DateLiteral)) {
				return false;
			}
			DateLiteral dateLiteral1 = (DateLiteral) valueExpression1;
			DateLiteral dateLiteral2 = (DateLiteral) valueExpression2;
			StringLiteral stringLiteral1 = dateLiteral1.getDateStringLiteral();
			StringLiteral stringLiteral2 = dateLiteral2.getDateStringLiteral();
			if (stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return true;
			}
			return false;
		}
		if (valueExpression1 instanceof TimestampLiteral) {
			if (!(valueExpression2 instanceof TimestampLiteral)) {
				return false;
			}
			TimestampLiteral dateLiteral1 = (TimestampLiteral) valueExpression1;
			TimestampLiteral dateLiteral2 = (TimestampLiteral) valueExpression2;
			StringLiteral stringLiteral1 = dateLiteral1.getTimestampStringLiteral();
			StringLiteral stringLiteral2 = dateLiteral2.getTimestampStringLiteral();
			if (stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return true;
			}
			return false;
		}
		if (valueExpression1 instanceof TimeLiteral) {
			if (!(valueExpression2 instanceof TimeLiteral)) {
				return false;
			}
			TimeLiteral dateLiteral1 = (TimeLiteral) valueExpression1;
			TimeLiteral dateLiteral2 = (TimeLiteral) valueExpression2;
			StringLiteral stringLiteral1 = dateLiteral1.getTimeStringLiteral();
			StringLiteral stringLiteral2 = dateLiteral2.getTimeStringLiteral();
			if (stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return true;
			}
			return false;
		}
		throw Sql4jException.getSql4jException(sourceCode, valueExpression1.getBeginIndex(), 
				"Value of hash columns must be parameters or literals.");
	}
	
	private void putHashColumnMap(String targetTableName, Map<String, HashKeyValue> hashColumnMap, NameChain hashColumn, ValueExpression value) {
		NameChain fullyQualifiedName = (NameChain) hashColumn.getFullyQualifiedName();
		String hashColumnName = fullyQualifiedName.toLowerCaseString();
		if (!hashColumnMap.containsKey(hashColumnName)) {
			hashColumnMap.put(hashColumnName, new HashKeyValue(hashColumn, value));
			return;
		}
		ValueExpression value2 = hashColumnMap.get(hashColumnName).getValue();
		if (value2 == null) {
			if (value == null) {
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
					"Hash column value conflict.");
		}
		if (value == null) {
			throw Sql4jException.getSql4jException(sourceCode, hashColumn.getBeginIndex(), 
					"Hash column value conflict.");
		}
		if (value2 instanceof Parameter) {
			if (!(value instanceof Parameter)) {
				throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
						"Hash column value conflict.");
			}
			Parameter parameter1 = (Parameter) value;
			Parameter parameter2 = (Parameter) value2;
			if (parameter1.getContent().equals(parameter2.getContent())) {
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
					"Hash column value conflict.");
		}
		if (value2 instanceof StringLiteral) {
			if (!(value instanceof StringLiteral)) {
				throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
						"Hash column value conflict.");
			}
			StringLiteral stringLiteral1 = (StringLiteral) value;
			StringLiteral stringLiteral2 = (StringLiteral) value2;
			if (stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
					"Hash column value conflict.");
		}
		if (value2 instanceof NumericLiteral) {
			if (!(value instanceof NumericLiteral)) {
				throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
						"Hash column value conflict.");
			}
			NumericLiteral numericLiteral1 = (NumericLiteral) value;
			NumericLiteral numericLiteral2 = (NumericLiteral) value2;
			if (numericLiteral1.getContent().equals(numericLiteral2.getContent())) {
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
					"Hash column value conflict.");
		}
		if (value2 instanceof DateLiteral) {
			if (!(value instanceof DateLiteral)) {
				throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
						"Hash column value conflict.");
			}
			DateLiteral dateLiteral1 = (DateLiteral) value;
			DateLiteral dateLiteral2 = (DateLiteral) value2;
			StringLiteral stringLiteral1 = dateLiteral1.getDateStringLiteral();
			StringLiteral stringLiteral2 = dateLiteral2.getDateStringLiteral();
			if (stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
					"Hash column value conflict.");
		}
		if (value2 instanceof TimestampLiteral) {
			if (!(value instanceof TimestampLiteral)) {
				throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
						"Hash column value conflict.");
			}
			TimestampLiteral dateLiteral1 = (TimestampLiteral) value;
			TimestampLiteral dateLiteral2 = (TimestampLiteral) value2;
			StringLiteral stringLiteral1 = dateLiteral1.getTimestampStringLiteral();
			StringLiteral stringLiteral2 = dateLiteral2.getTimestampStringLiteral();
			if (stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
					"Hash column value conflict.");
		}
		if (value2 instanceof TimeLiteral) {
			if (!(value instanceof TimeLiteral)) {
				throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
						"Hash column value conflict.");
			}
			TimeLiteral dateLiteral1 = (TimeLiteral) value;
			TimeLiteral dateLiteral2 = (TimeLiteral) value2;
			StringLiteral stringLiteral1 = dateLiteral1.getTimeStringLiteral();
			StringLiteral stringLiteral2 = dateLiteral2.getTimeStringLiteral();
			if (stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
					"Hash column value conflict.");
		}
		throw Sql4jException.getSql4jException(sourceCode, value.getBeginIndex(), 
				"Value of hash columns must be parameters or literals.");
	}
	
}
