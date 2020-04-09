package lee.bright.sql4j.ql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lee.bright.sql4j.Sql4jException;

/**
 * @author Bright Lee
 */
public final class InsertStatementCheckForThePresenceOfAbandonedStructuresAnalyzer {
	
	private SourceCode sourceCode;
	private InsertStatement insertStatement;
	
	public InsertStatementCheckForThePresenceOfAbandonedStructuresAnalyzer(
			SourceCode sourceCode, InsertStatement insertStatement) {
		this.sourceCode = sourceCode;
		this.insertStatement = insertStatement;
	}
	
	public void analyze() {
		analyze(insertStatement);
	}
	
	private void analyze(InsertStatement insertStatement) {
		SelectStatement selectStatement = insertStatement.getSelectStatement();
		if (selectStatement != null) {
			throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
					"Select statement is not allowed to appear here.");
		}
		List<Name> insertColumnList = insertStatement.getInsertColumnList();
		Map<String, String> map = new HashMap<String, String>(insertColumnList.size());
		for (Name insertColumn : insertColumnList) {
			String insertColumnName = insertColumn.getContent().toLowerCase();
			if (map.containsKey(insertColumnName)) {
				throw Sql4jException.getSql4jException(sourceCode, insertColumn.getBeginIndex(), 
						"Duplicate insert column.");
			}
			map.put(insertColumnName, insertColumnName);
		}
		map.clear();
		List<ValueExpression> valueExpressionList = insertStatement.getValueExpressionList();
		if (insertColumnList.size() != valueExpressionList.size()) {
			if (insertColumnList.size() > valueExpressionList.size()) {
				throw Sql4jException.getSql4jException(sourceCode, insertColumnList.get(valueExpressionList.size()).getBeginIndex(), 
						"The size of insert column list is not equal to value expression list.");
			} else {
				throw Sql4jException.getSql4jException(sourceCode, valueExpressionList.get(insertColumnList.size()).getBeginIndex(), 
						"The size of insert column list is not equal to value expression list.");
			}
		}
		if (valueExpressionList != null) {
			for (int i = 0; i < valueExpressionList.size(); i++) {
				ValueExpression valueExpression = valueExpressionList.get(i);
				analyze(valueExpression);
			}
		}
	}
	
	private void analyze(ValueExpression valueExpression) {
		if (valueExpression instanceof Parameter == false &&
			valueExpression instanceof StringLiteral == false &&
			valueExpression instanceof NumericLiteral == false &&
			valueExpression instanceof DateLiteral == false &&
			valueExpression instanceof TimestampLiteral == false &&
			valueExpression instanceof TimeLiteral == false &&
			valueExpression instanceof CurrentDate == false &&
			valueExpression instanceof CurrentTimestamp == false &&
			valueExpression instanceof CurrentTime == false) {
			StringBuilder buf = new StringBuilder(512);
			buf.append("The type of the expression can only be ");
			buf.append("parameter, string literal, numeric literal, ");
			buf.append("date literal, timestamp literal, time literal, ");
			buf.append("current_date, current_timestamp or current_time.");
			throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
					buf.toString());
		}
	}

}
