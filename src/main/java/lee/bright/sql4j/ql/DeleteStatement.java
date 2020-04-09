package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bright Lee
 */
public final class DeleteStatement implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain targetTable;
	private BooleanValueExpression searchCondition;
	private List<NameChain> hashColumnNameList = new ArrayList<NameChain>();
	private List<ValueExpression> hashColumnValueList = new ArrayList<ValueExpression>();
	
	public DeleteStatement(SourceCode sourceCode, 
			int beginIndex, 
			NameChain targetTable,
			BooleanValueExpression searchCondition) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.targetTable = targetTable;
		this.searchCondition = searchCondition;
	}
	
	public StatementType getStatementType() {
		return StatementType.DELETE_STATEMENT;
	}
	
	public NameChain getTargetTable() {
		return targetTable;
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

}
