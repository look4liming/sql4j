package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;
import lee.bright.sql4j.conf.TableMetadata;

/**
 * @author Bright Lee
 */
public final class DeleteStatementSetFullyQualifiedColumnNamesAnalyzer {

	private Configuration configuration;
	private SourceCode sourceCode;
	private DeleteStatement deleteStatement;
	
	public DeleteStatementSetFullyQualifiedColumnNamesAnalyzer(Configuration configuration, 
			SourceCode sourceCode, DeleteStatement deleteStatement) {
		this.configuration = configuration;
		this.sourceCode = sourceCode;
		this.deleteStatement = deleteStatement;
	}
	
	public void analyze() {
		BooleanValueExpression searchCondition = deleteStatement.getSearchCondition();
		analyzeSearchCondition(searchCondition);
	}
	
	private void analyzeSearchCondition(BooleanValueExpression searchCondition) {
		if (searchCondition == null) {
			return;
		}
		List<List<BooleanValueExpression>> andPathList = BooleanValueExpressionUtil.
				getAndPathList(sourceCode, searchCondition);
		for (int i = 0; i < andPathList.size(); i++) {
			List<BooleanValueExpression> andPath = andPathList.get(i);
			for (int j = 0; j < andPath.size(); j++) {
				BooleanValueExpression andNode = andPath.get(j);
				checkAndNode(andNode);
			}
		}
	}
	
	private void checkAndNode(BooleanValueExpression andNode) {
		if (andNode instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = (ComparisonPredicate) andNode;
			checkAndNode(comparisonPredicate);
			return;
		}
		if (andNode instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = (BetweenPredicate) andNode;
			ValueExpression valueExpression = betweenPredicate.getValueExpression();
			if (valueExpression instanceof NameChain == false) {
				throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
						"The expression must be a column name.");
			}
			checkColumnInSearchCondition((NameChain) valueExpression);
			ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
			checkValueExpression(valueExpression1);
			ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
			checkValueExpression(valueExpression2);
			return;
		}
		if (andNode instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = (ExistsPredicate) andNode;
			throw Sql4jException.getSql4jException(sourceCode, existsPredicate.getBeginIndex(), 
					"The exists predicate is not supported in the search condition of the update statement.");
		}
		if (andNode instanceof InPredicate) {
			InPredicate inPredicate = (InPredicate) andNode;
			ValueExpression valueExpression = inPredicate.getValueExpression();
			if (valueExpression instanceof NameChain == false) {
				throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
						"The expression must be a column name.");
			}
			checkColumnInSearchCondition((NameChain) valueExpression);
			List<ValueExpression> inValueList = inPredicate.getInValueList();
			if (inValueList == null) {
				Subquery subquery = inPredicate.getSubquery();
				throw Sql4jException.getSql4jException(sourceCode, subquery.getBeginIndex(), 
						"Subquery is not supported.");
			}
			for (int i = 0; i < inValueList.size(); i++) {
				ValueExpression inValue = inValueList.get(i);
				checkValueExpression(inValue);
			}
			return;
		}
		if (andNode instanceof LikePredicate) {
			LikePredicate likePredicate = (LikePredicate) andNode;
			ValueExpression valueExpression = likePredicate.getValueExpression();
			if (valueExpression instanceof NameChain == false) {
				throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
						"The expression must be a column name.");
			}
			checkColumnInSearchCondition((NameChain) valueExpression);
			ValueExpression characterPattern = likePredicate.getCharacterPattern();
			checkValueExpression(characterPattern);
			ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
			if (escapeCharacter != null) {
				checkValueExpression(escapeCharacter);
			}
			return;
		}
		if (andNode instanceof NullPredicate) {
			NullPredicate nullPredicate = (NullPredicate) andNode;
			ValueExpression valueExpression = nullPredicate.getValueExpression();
			if (valueExpression instanceof NameChain == false) {
				throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
						"The expression must be a column name.");
			}
			checkColumnInSearchCondition((NameChain) valueExpression);
			return;
		}
		if (andNode instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) andNode;
			BooleanValueExpression booleanValueExpression = booleanTest.getBooleanValueExpression();
			checkAndNode(booleanValueExpression);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, andNode.getBeginIndex(), 
				"This boolean value expression is not supported.");
	}
	
	private void checkAndNode(ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		ValueExpression right = comparisonPredicate.getRight();
		if (left instanceof NameChain == false &&
			right instanceof NameChain == false) {
			throw Sql4jException.getSql4jException(sourceCode, left.getBeginIndex(), 
					"The expression must be a column name.");
		}
		if (left instanceof NameChain) {
			checkColumnInSearchCondition((NameChain) left);
		}
		if (right instanceof NameChain) {
			checkColumnInSearchCondition((NameChain) right);
		}
		ValueExpression valueExpression;
		if (left instanceof NameChain == false) {
			valueExpression = left;
		} else if (right instanceof NameChain == false) {
			valueExpression = right;
		} else {
			valueExpression = null;
		}
		if (valueExpression != null) {
			checkValueExpression(valueExpression);
		}
	}
	
	public void checkValueExpression(ValueExpression valueExpression) {
		if (valueExpression instanceof Parameter == false &&
			valueExpression instanceof StringLiteral == false &&
			valueExpression instanceof NumericLiteral == false &&
			valueExpression instanceof DateLiteral == false &&
			valueExpression instanceof TimestampLiteral == false &&
			valueExpression instanceof TimeLiteral == false &&
			valueExpression instanceof CurrentDate == false &&
			valueExpression instanceof CurrentTimestamp == false &&
			valueExpression instanceof CurrentTime == false) {
			throw Sql4jException.getSql4jException(sourceCode, 
					valueExpression.getBeginIndex(), 
					"The value expression must be parameter or literal.");
		}
	}
	
	private void checkColumnInSearchCondition(NameChain column) {
		if (column.size() > 1) {
			throw Sql4jException.getSql4jException(sourceCode, column.getBeginIndex(), 
					"The column name must be a simple name chain, not a fully qualified name.");
		}
		String columnName = column.toLowerCaseString();
		if ("hash_foremost_db".equals(columnName) ||
			"ts".equals(columnName)) {
			throw Sql4jException.getSql4jException(sourceCode, column.getBeginIndex(), 
					"The column cannot appear in the search condition.");
		}
		NameChain targetTable = deleteStatement.getTargetTable();
		TableMetadata tableMetadata = configuration.getTableMetadata(targetTable.toLowerCaseString());
		if (!tableMetadata.hasColumnMetadata(columnName)) {
			throw Sql4jException.getSql4jException(sourceCode, column.getBeginIndex(), 
					"This column does not exist in the table.");
		}
		NameChain fullyQualifiedName = getFullyQualifiedName(deleteStatement.getTargetTable(), column);
		column.setFullyQualifiedName(fullyQualifiedName);
	}
	
	private static NameChain getFullyQualifiedName(NameChain targetTable, NameChain column) {
		List<Name> list = new ArrayList<Name>(targetTable.size() + column.size());
		for (int i = 0; i < targetTable.size(); i++) {
			list.add(targetTable.get(i));
		}
		for (int i = 0; i < column.size(); i++) {
			list.add(column.get(i));
		}
		NameChain fullyQualifiedName = new NameChain(list);
		return fullyQualifiedName;
	}
	
}
