package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;

/**
 * @author Bright Lee
 */
public final class QuerySpecificationHashRuleAnalyzer {
	
	private Configuration configuration;
	private SourceCode sourceCode;
	private QuerySpecification querySpecification;
	
	public QuerySpecificationHashRuleAnalyzer(
			Configuration configuration,
			SourceCode sourceCode,
			QuerySpecification querySpecification) {
		this.configuration = configuration;
		this.sourceCode = sourceCode;
		this.querySpecification = querySpecification;
	}
	
	public void analyze() {
		analyze(null, querySpecification);
	}
	
	private void analyze(QuerySpecification parentQuerySpecification, QuerySpecification querySpecification) {
		boolean b = isTableWithHashColumns(querySpecification);
		if (b == false) {
			return;
		}
		BooleanValueExpression whereSearchCondition = querySpecification.getWhereSearchCondition();
		if (whereSearchCondition == null) {
			throw Sql4jException.getSql4jException(sourceCode, querySpecification.getBeginIndex(), 
					"Please specify one hash column for this table in where condition.");
		}
		List<List<BooleanValueExpression>> whereSearchConditionAndPathList = BooleanValueExpressionUtil.
				getAndPathList(sourceCode, whereSearchCondition);
		QuerySpecificationDecisiveEquation decisiveEquation = getDecisiveEquationAndPathList(whereSearchConditionAndPathList);
		if (parentQuerySpecification != null) {
			QuerySpecificationDecisiveEquation parentDecisiveEquation = parentQuerySpecification.getDecisiveEquation();
			if (isEquals(parentDecisiveEquation, decisiveEquation) == false) {
				NameChain parentHashColumn = parentDecisiveEquation.getDecisiveHashColumn();
				NameChain hashColumn = decisiveEquation.getDecisiveHashColumn();
				NameChain parentHashColumnFullyQualifiedName = (NameChain) parentHashColumn.getFullyQualifiedName();
				NameChain hashColumnFullyQualifiedName = (NameChain) hashColumn.getFullyQualifiedName();
				if (configuration.isHashColumnPair(parentHashColumnFullyQualifiedName, 
						hashColumnFullyQualifiedName) == false) {
					throw Sql4jException.getSql4jException(sourceCode, hashColumn.getBeginIndex(), 
							"Not consistent with the hash column of the parent decisive hash equation.");
				}
				ValueExpression parentHashValue = parentDecisiveEquation.getDecisiveHashValue();
				ValueExpression hashValue = decisiveEquation.getDecisiveHashValue();
				if (isHashValueEquals(parentHashValue, hashValue) == false) {
					throw Sql4jException.getSql4jException(sourceCode, hashValue.getBeginIndex(), 
							"Not consistent with the hash value of the parent decisive hash equation.");
				}
			}
		}
		querySpecification.setDecisiveEquation(decisiveEquation);
		//if (this.querySpecification.getDecisiveEquation() == null) {
		//	this.querySpecification.setDecisiveEquation(decisiveEquation);
		//}
		checkJoinCondition(querySpecification);
		setDecisiveEquationForDerivedTable(querySpecification);
		// TODO
	}
	
	private List<List<BooleanValueExpression>> getAndPathList(TableReference tableReference) {
		if (tableReference instanceof TablePrimary) {
			return null;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			TableReference left = leftOuterJoin.getLeft();
			List<List<BooleanValueExpression>> leftAndPathList = getAndPathList(left);
			TableReference right = leftOuterJoin.getRight();
			List<List<BooleanValueExpression>> rightAndPathList = getAndPathList(right);
			List<List<BooleanValueExpression>> joinConditionAndPathList = BooleanValueExpressionUtil.
					getAndPathList(sourceCode, leftOuterJoin.getJoinCondition());
			List<List<BooleanValueExpression>> andPathList = getAndPathList(leftAndPathList, 
					rightAndPathList);
			andPathList = getAndPathList(andPathList, joinConditionAndPathList);
			return andPathList;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			QuerySpecification querySpecification = getQuerySpecification(derivedTable);
			TableReference tableReference2 = getTableReference(querySpecification);
			List<List<BooleanValueExpression>> andPathList = getAndPathList(tableReference2);
			// TODO
			return andPathList;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			List<List<BooleanValueExpression>> leftAndPathList = getAndPathList(left);
			TableReference right = rightOuterJoin.getRight();
			List<List<BooleanValueExpression>> rightAndPathList = getAndPathList(right);
			List<List<BooleanValueExpression>> joinConditionAndPathList = BooleanValueExpressionUtil.
					getAndPathList(sourceCode, rightOuterJoin.getJoinCondition());
			List<List<BooleanValueExpression>> andPathList = getAndPathList(leftAndPathList, 
					rightAndPathList);
			andPathList = getAndPathList(andPathList, joinConditionAndPathList);
			return andPathList;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			List<List<BooleanValueExpression>> leftAndPathList = getAndPathList(left);
			TableReference right = innerJoin.getRight();
			List<List<BooleanValueExpression>> rightAndPathList = getAndPathList(right);
			List<List<BooleanValueExpression>> joinConditionAndPathList = BooleanValueExpressionUtil.
					getAndPathList(sourceCode, innerJoin.getJoinCondition());
			List<List<BooleanValueExpression>> andPathList = getAndPathList(leftAndPathList, 
					rightAndPathList);
			andPathList = getAndPathList(andPathList, joinConditionAndPathList);
			return andPathList;
		}
		if (tableReference instanceof FullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Full outer join is not supported.");
		}
		if (tableReference instanceof CrossJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Cross join is not supported.");
		}
		if (tableReference instanceof NaturalInnerJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural inner join is not supported.");
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural left outer join is not supported.");
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural right outer join is not supported.");
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural full outer join is not supported.");
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"This table reference is not supported.");
	}
	
	private List<List<BooleanValueExpression>> getAndPathList(
			List<List<BooleanValueExpression>> andPathList1, 
			List<List<BooleanValueExpression>> andPathList2) {
		if (andPathList1 == null) {
			return andPathList2;
		}
		if (andPathList2 == null) {
			return andPathList1;
		}
		List<List<BooleanValueExpression>> andPathList = 
				new ArrayList<List<BooleanValueExpression>>(andPathList1.size() * andPathList2.size());
		for (int i = 0; i < andPathList1.size(); i++) {
			List<BooleanValueExpression> andPath1 = andPathList1.get(i);
			for (int j = 0; j < andPathList2.size(); j++) {
				List<BooleanValueExpression> andPath2 = andPathList2.get(j);
				List<BooleanValueExpression> andPath = new ArrayList<BooleanValueExpression>(
						andPath1.size() + andPath2.size());
				andPath.addAll(andPath1);
				andPath.addAll(andPath2);
				andPathList.add(andPath);
			}
		}
		return andPathList;
	}
	
	private boolean isTableWithHashColumns(QuerySpecification querySpecification) {
		TableReference tableReference = getTableReference(querySpecification);
		boolean b = isTableWithHashColumns(tableReference);
		return b;
	}
	
	private boolean isTableWithHashColumns(TableReference tableReference) {
		if (tableReference instanceof TablePrimary) {
			TablePrimary tablePrimary = (TablePrimary) tableReference;
			NameChain tableName = tablePrimary.getTableName();
			boolean b = configuration.isTableWithHashColumns(tableName);
			return b;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			TableReference left = leftOuterJoin.getLeft();
			boolean b1 = isTableWithHashColumns(left);
			if (b1 == true) {
				return true;
			}
			TableReference right = leftOuterJoin.getRight();
			boolean b2 = isTableWithHashColumns(right);
			if (b2 == true) {
				return true;
			}
			return false;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			SelectStatement selectStatement = derivedTable.getSelectStatement();
			if (selectStatement instanceof QuerySpecification == false) {
				throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
						"This statement is not supported.");
			}
			QuerySpecification querySpecification = (QuerySpecification) selectStatement;
			boolean b = isTableWithHashColumns(querySpecification);
			return b;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			boolean b1 = isTableWithHashColumns(left);
			if (b1 == true) {
				return true;
			}
			TableReference right = rightOuterJoin.getRight();
			boolean b2 = isTableWithHashColumns(right);
			if (b2 == true) {
				return true;
			}
			return false;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			boolean b1 = isTableWithHashColumns(left);
			if (b1 == true) {
				return true;
			}
			TableReference right = innerJoin.getRight();
			boolean b2 = isTableWithHashColumns(right);
			if (b2 == true) {
				return true;
			}
			return false;
		}
		if (tableReference instanceof FullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Full outer join is not supported.");
		}
		if (tableReference instanceof CrossJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Cross join is not supported.");
		}
		if (tableReference instanceof NaturalInnerJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural inner join is not supported.");
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural left outer join is not supported.");
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural right outer join is not supported.");
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural full outer join is not supported.");
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"This table reference is not supported.");
	}
	
	private QuerySpecification getQuerySpecification(DerivedTable derivedTable) {
		SelectStatement selectStatement = derivedTable.getSelectStatement();
		QuerySpecification querySpecification = getQuerySpecification(selectStatement);
		return querySpecification;
	}
	
	private QuerySpecification getQuerySpecification(SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = (QuerySpecification) selectStatement;
			return querySpecification;
		}
		if (selectStatement instanceof Union) {
			throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
					"Union statement is not supported.");
		}
		if (selectStatement instanceof Intersect) {
			throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
					"Intersect statement is not supported.");
		}
		if (selectStatement instanceof Except) {
			throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
					"Except statement is not supported.");
		}
		throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
				"This statement is not supported.");
	}
	
	private TableReference getTableReference(QuerySpecification querySpecification) {
		List<TableReference> tableReferenceList = querySpecification.getTableReferenceList();
		TableReference tableReference = getTableReference(tableReferenceList);
		return tableReference;
	}
	
	private TableReference getTableReference(List<TableReference> tableReferenceList) {
		TableReference tableReference = tableReferenceList.get(0);
		if (tableReferenceList.size() > 1) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"',' join is not supported.");
		}
		if (tableReference instanceof TablePrimary) {
			return tableReference;
		}
		if (tableReference instanceof LeftOuterJoin) {
			return tableReference;
		}
		if (tableReference instanceof DerivedTable) {
			return tableReference;
		}
		if (tableReference instanceof RightOuterJoin) {
			return tableReference;
		}
		if (tableReference instanceof InnerJoin) {
			return tableReference;
		}
		if (tableReference instanceof FullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Full outer join is not supported.");
		}
		if (tableReference instanceof CrossJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Cross join is not supported.");
		}
		if (tableReference instanceof NaturalInnerJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural inner join is not supported.");
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural left outer join is not supported.");
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural right outer join is not supported.");
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural full outer join is not supported.");
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"This table reference is not supported.");
	}
	
	private QuerySpecificationDecisiveEquation getDecisiveEquationAndPathList(List<List<BooleanValueExpression>> andPathList) {
		QuerySpecificationDecisiveEquation decisiveEquation = null;
		for (int i = 0; i < andPathList.size(); i++) {
			List<BooleanValueExpression> andPath = andPathList.get(i);
			QuerySpecificationDecisiveEquation decisiveEquation2 = getDecisiveEquationByAndPath(andPath);
			if (decisiveEquation == null) {
				decisiveEquation = decisiveEquation2;
				continue;
			}
			if (isEquals(decisiveEquation, decisiveEquation2) == false) {
				NameChain decisiveHashColumn = decisiveEquation2.getDecisiveHashColumn();
				throw Sql4jException.getSql4jException(sourceCode, decisiveHashColumn.getBeginIndex(), 
						"Conflict value of hash column.");
			}
		}
		return decisiveEquation;
	}
	
	private boolean isEquals(QuerySpecificationDecisiveEquation decisiveEquation1, 
			QuerySpecificationDecisiveEquation decisiveEquation2) {
		ValueExpression valueExpression1 = decisiveEquation1.getDecisiveHashColumn().getFullyQualifiedName();
		ValueExpression valueExpression2 = decisiveEquation2.getDecisiveHashColumn().getFullyQualifiedName();
		NameChain hashColumn1 = (NameChain) valueExpression1;
		NameChain hashColumn2 = (NameChain) valueExpression2;
		if (isEquals(hashColumn1, hashColumn2) == false) {
			return false;
		}
		ValueExpression hashValue1 = decisiveEquation1.getDecisiveHashValue();
		ValueExpression hashValue2 = decisiveEquation2.getDecisiveHashValue();
		return isHashValueEquals(hashValue1, hashValue2);
	}
	
	private boolean isHashValueEquals(ValueExpression valueExpression1, ValueExpression valueExpression2) {
		if (valueExpression1 instanceof Parameter && valueExpression2 instanceof Parameter) {
			Parameter parameter1 = (Parameter) valueExpression1;
			Parameter parameter2 = (Parameter) valueExpression2;
			if (parameter1.getContent().equals(parameter2.getContent())) {
				return true;
			}
			return false;
		}
		if (valueExpression1 instanceof StringLiteral && valueExpression2 instanceof StringLiteral) {
			StringLiteral stringLiteral1 = (StringLiteral) valueExpression1;
			StringLiteral stringLiteral2 = (StringLiteral) valueExpression2;
			if (stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return true;
			}
			return false;
		}
		if (valueExpression1 instanceof NumericLiteral && valueExpression2 instanceof NumericLiteral) {
			NumericLiteral numericLiteral1 = (NumericLiteral) valueExpression1;
			NumericLiteral numericLiteral2 = (NumericLiteral) valueExpression2;
			if (numericLiteral1.getContent().equals(numericLiteral2.getContent())) {
				return true;
			}
			return false;
		}
		if (valueExpression1 instanceof DateLiteral && valueExpression2 instanceof DateLiteral) {
			DateLiteral dateLiteral1 = (DateLiteral) valueExpression1;
			DateLiteral dateLiteral2 = (DateLiteral) valueExpression2;
			StringLiteral stringLiteral1 = dateLiteral1.getDateStringLiteral();
			StringLiteral stringLiteral2 = dateLiteral2.getDateStringLiteral();
			if (stringLiteral1 != null && 
				stringLiteral2 != null && 
				stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return true;
			}
			Parameter parameter1 = dateLiteral1.getParameter();
			Parameter parameter2 = dateLiteral2.getParameter();
			if (parameter1 != null && 
				parameter2 != null && 
				parameter1.getContent().equals(parameter2.getContent())) {
				return true;
			}
			return false;
		}
		if (valueExpression1 instanceof TimestampLiteral && valueExpression2 instanceof TimestampLiteral) {
			TimestampLiteral timestampLiteral1 = (TimestampLiteral) valueExpression1;
			TimestampLiteral timestampLiteral2 = (TimestampLiteral) valueExpression2;
			StringLiteral stringLiteral1 = timestampLiteral1.getTimestampStringLiteral();
			StringLiteral stringLiteral2 = timestampLiteral2.getTimestampStringLiteral();
			if (stringLiteral1 != null && 
				stringLiteral2 != null && 
				stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return true;
			}
			Parameter parameter1 = timestampLiteral1.getParameter();
			Parameter parameter2 = timestampLiteral2.getParameter();
			if (parameter1 != null && 
				parameter2 != null && 
				parameter1.getContent().equals(parameter2.getContent())) {
				return true;
			}
			return false;
		}
		if (valueExpression1 instanceof TimeLiteral && valueExpression2 instanceof TimeLiteral) {
			TimeLiteral timeLiteral1 = (TimeLiteral) valueExpression1;
			TimeLiteral timeLiteral2 = (TimeLiteral) valueExpression2;
			StringLiteral stringLiteral1 = timeLiteral1.getTimeStringLiteral();
			StringLiteral stringLiteral2 = timeLiteral2.getTimeStringLiteral();
			if (stringLiteral1 != null && 
				stringLiteral2 != null && 
				stringLiteral1.getContent().equals(stringLiteral2.getContent())) {
				return true;
			}
			Parameter parameter1 = timeLiteral1.getParameter();
			Parameter parameter2 = timeLiteral2.getParameter();
			if (parameter1 != null && 
				parameter2 != null && 
				parameter1.getContent().equals(parameter2.getContent())) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	private QuerySpecificationDecisiveEquation getDecisiveEquationByAndPath(List<BooleanValueExpression> andPath) {
		QuerySpecificationDecisiveEquation decisiveEquation = null;
		for (int i = 0; i < andPath.size(); i++) {
			BooleanValueExpression booleanValueExpression = andPath.get(i);
			QuerySpecificationDecisiveEquation decisiveEquation2 = getDecisiveEquationByAndNode(booleanValueExpression);
			if (decisiveEquation2 == null) {
				continue;
			}
			if (decisiveEquation == null) {
				decisiveEquation = decisiveEquation2;
				continue;
			}
			if (isEquals(decisiveEquation, decisiveEquation2) == false) {
				ValueExpression decisiveHashValue = decisiveEquation2.getDecisiveHashValue();
				throw Sql4jException.getSql4jException(sourceCode, decisiveHashValue.getBeginIndex(), 
						"Not consistent with the value of the previous decisive hash equation.");
			}
		}
		if (decisiveEquation == null) {
			throw Sql4jException.getSql4jException(sourceCode, andPath.get(0).getBeginIndex(), 
					"Unspecified decisive hash equation.");
		}
		return decisiveEquation;
	}
	
	private QuerySpecificationDecisiveEquation getDecisiveEquationByAndNode(BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression instanceof Predicate == false && 
			booleanValueExpression instanceof BooleanTest == false) {
			throw Sql4jException.getSql4jException(sourceCode, booleanValueExpression.getBeginIndex(), 
					"The boolean value expression must be a predicate.");
		}
		if (booleanValueExpression instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = (ComparisonPredicate) booleanValueExpression;
			CompOp compOp = comparisonPredicate.getCompOp();
			if (compOp != CompOp.EQUALS) {
				return null;
			}
			ValueExpression left = comparisonPredicate.getLeft();
			ValueExpression right = comparisonPredicate.getRight();
			if (isHashColumn(left)) {
				if (!(right instanceof Parameter) &&
					!(right instanceof StringLiteral) &&
					!(right instanceof NumericLiteral) &&
					!(right instanceof DateLiteral) &&
					!(right instanceof TimestampLiteral) &&
					!(right instanceof TimeLiteral)) {
					return null;
				}
				NameChain leftNameChain = (NameChain) left;
				QuerySpecificationDecisiveEquation decisiveEquation = 
						new QuerySpecificationDecisiveEquation(leftNameChain, right);
				return decisiveEquation;
			}
			if (isHashColumn(right)) {
				if (!(left instanceof Parameter) &&
					!(left instanceof StringLiteral) &&
					!(left instanceof NumericLiteral) &&
					!(left instanceof DateLiteral) &&
					!(left instanceof TimestampLiteral) &&
					!(left instanceof TimeLiteral)) {
					return null;
				}
				NameChain rightNameChain = (NameChain) right;
				QuerySpecificationDecisiveEquation decisiveEquation = 
						new QuerySpecificationDecisiveEquation(rightNameChain, left);
				return decisiveEquation;
			}
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			TruthValue truthValue = booleanTest.getTruthValue();
			if (truthValue == null) {
				return getDecisiveEquationByAndNode(booleanTest);
			}
			boolean not = booleanTest.getNot();
			if (not == false && truthValue == TruthValue.TRUE) {
				return getDecisiveEquationByAndNode(booleanTest);
			}
		}
		return null;
	}
	
	private boolean isHashColumn(ValueExpression valueExpression) {
		if (valueExpression == null) {
			return false;
		}
		if (valueExpression instanceof NameChain == false) {
			return false;
		}
		NameChain nameChain = (NameChain) valueExpression;
		ValueExpression fullyQualifiedName = nameChain.getFullyQualifiedName();
		if (fullyQualifiedName instanceof NameChain == false) {
			return false;
		}
		NameChain fullQualifiedNameChain = (NameChain) fullyQualifiedName;
		boolean b = configuration.isHashColumn(fullQualifiedNameChain);
		return b;
	}
	
	private boolean isEquals(NameChain hashColumn1, NameChain hashColumn2) {
		if (hashColumn1 == null || hashColumn2 == null) {
			return false;
		}
		if (hashColumn1.size() != hashColumn2.size()) {
			return false;
		}
		for (int i = 0; i < hashColumn1.size(); i++) {
			Name name1 = hashColumn1.get(i);
			Name name2 = hashColumn2.get(i);
			if (name1.getContent().equalsIgnoreCase(name2.getContent()) == false) {
				return false;
			}
		}
		return true;
	}
	
	private void checkJoinCondition(QuerySpecification querySpecification) {
		TableReference tableReference = getTableReference(querySpecification);
		
		// TODO
	}
	
	private void setDecisiveEquationForDerivedTable(QuerySpecification parentQuerySpecification) {
		TableReference tableReference = getTableReference(parentQuerySpecification);
		setDecisiveEquationForDerivedTable(parentQuerySpecification, tableReference);
	}
	
	private void setDecisiveEquationForDerivedTable(QuerySpecification parentQuerySpecification, 
			TableReference tableReference) {
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			QuerySpecification querySpecification = getQuerySpecification(derivedTable);
			analyze(parentQuerySpecification, querySpecification);
			return;
		}
		if (tableReference instanceof TablePrimary) {
			return;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			TableReference left = leftOuterJoin.getLeft();
			setDecisiveEquationForDerivedTable(parentQuerySpecification, left);
			TableReference right = leftOuterJoin.getRight();
			setDecisiveEquationForDerivedTable(parentQuerySpecification, right);
			return;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			setDecisiveEquationForDerivedTable(parentQuerySpecification, left);
			TableReference right = rightOuterJoin.getRight();
			setDecisiveEquationForDerivedTable(parentQuerySpecification, right);
			return;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			setDecisiveEquationForDerivedTable(parentQuerySpecification, left);
			TableReference right = innerJoin.getRight();
			setDecisiveEquationForDerivedTable(parentQuerySpecification, right);
			return;
		}
		if (tableReference instanceof FullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Full outer join is not supported.");
		}
		if (tableReference instanceof CrossJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Cross join is not supported.");
		}
		if (tableReference instanceof NaturalInnerJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural inner join is not supported.");
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural left outer join is not supported.");
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural right outer join is not supported.");
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural full outer join is not supported.");
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"This table reference is not supported.");
	}

}
