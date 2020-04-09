package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bright Lee
 */
public final class InsertStatement implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain insertionTarget;
	private List<Name> insertColumnList;
	private List<ValueExpression> valueExpressionList;
	private SelectStatement selectStatement;
	private List<NameChain> hashColumnNameList = new ArrayList<NameChain>();
	private List<ValueExpression> hashColumnValueList = new ArrayList<ValueExpression>();
	private List<String> lostNecessaryColumnList = new ArrayList<String>();
	
	public InsertStatement(SourceCode sourceCode, 
			int beginIndex, NameChain insertionTarget,
			List<Name> insertColumnList,
			List<ValueExpression> valueExpressionList,
			SelectStatement selectStatement) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.insertionTarget = insertionTarget;
		this.insertColumnList = insertColumnList;
		this.valueExpressionList = valueExpressionList;
		this.selectStatement = selectStatement;
	}

	public StatementType getStatementType() {
		return StatementType.INSERT_STATEMENT;
	}
	
	public NameChain getInsertionTarget() {
		return insertionTarget;
	}
	
	public List<Name> getInsertColumnList() {
		return insertColumnList;
	}
	
	public List<ValueExpression> getValueExpressionList() {
		return valueExpressionList;
	}
	
	public SelectStatement getSelectStatement() {
		return selectStatement;
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
	
	void addLostNecessaryColumn(String lostNecessaryColumn) {
		lostNecessaryColumnList.add(lostNecessaryColumn);
	}
	
	public List<String> getLostNecessaryColumnList() {
		return lostNecessaryColumnList;
	}

}
