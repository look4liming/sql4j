package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class TableDefinition implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain tableName;
	private List<TableElement> tableElementList;
	
	public TableDefinition(SourceCode sourceCode, int beginIndex, 
			NameChain tableName, List<TableElement> tableElementList) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.tableName = tableName;
		this.tableElementList = tableElementList;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public StatementType getStatementType() {
		return StatementType.TABLE_DEFINITION;
	}
	
	public NameChain getTableName() {
		return tableName;
	}
	
	public List<TableElement> getTableElementList() {
		return tableElementList;
	}

}
