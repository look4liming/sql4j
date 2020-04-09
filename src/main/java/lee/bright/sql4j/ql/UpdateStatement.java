package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.Sql4jException;

/**
 * @author Bright Lee
 */
public final class UpdateStatement implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain targetTable;
	private List<SetClause> setClauseList;
	private BooleanValueExpression searchCondition;
	private List<NameChain> hashColumnNameList = new ArrayList<NameChain>();
	private List<ValueExpression> hashColumnValueList = new ArrayList<ValueExpression>();
	
	public UpdateStatement(SourceCode sourceCode, 
			int beginIndex, 
			NameChain targetTable,
			List<SetClause> setClauseList,
			BooleanValueExpression searchCondition) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.targetTable = targetTable;
		this.setClauseList = setClauseList;
		this.searchCondition = searchCondition;
	}

	public StatementType getStatementType() {
		return StatementType.UPDATE_STATEMENT;
	}
	
	public NameChain getTargetTable() {
		return targetTable;
	}
	
	public List<SetClause> getSetClauseList() {
		return setClauseList;
	}
	
	public BooleanValueExpression getSearchCondition() {
		return searchCondition;
	}

	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	void addHashColumnName(NameChain columnName) {
		if (hashColumnNameList == null) {
			hashColumnNameList = new ArrayList<NameChain>();
		}
		hashColumnNameList.add(columnName);
	}
	
	public List<NameChain> getHashColumnNameList() {
		return hashColumnNameList;
	}
	
	void addHashColumnValue(ValueExpression columnValue) {
		if (hashColumnValueList == null) {
			hashColumnValueList = new ArrayList<ValueExpression>();
		}
		hashColumnValueList.add(columnValue);
	}
	
	public List<ValueExpression> getHashColumnValueList() {
		return hashColumnValueList;
	}
	
	public static void checkUpdateSource(SourceCode sourceCode, ValueExpression updateSource) {
		if (updateSource instanceof Parameter == false &&
			updateSource instanceof StringLiteral == false &&
			updateSource instanceof NumericLiteral == false &&
			updateSource instanceof DateLiteral == false &&
			updateSource instanceof TimestampLiteral == false &&
			updateSource instanceof TimeLiteral == false &&
			updateSource instanceof CurrentDate == false &&
			updateSource instanceof CurrentTimestamp == false &&
			updateSource instanceof CurrentTime == false) {
			throw Sql4jException.getSql4jException(sourceCode, 
					updateSource.getBeginIndex(), 
					"Update source must be parameter or literal.");
		}
	}

}
