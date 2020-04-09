package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class CreateIndexStatement implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private boolean unique;
	private NameChain indexName;
	private NameChain tableName;
	private List<Name> columnNameList;
	
	public CreateIndexStatement(SourceCode sourceCode, int beginIndex, boolean unique, 
			NameChain indexName, NameChain tableName, List<Name> columnNameList) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.unique = unique;
		this.indexName = indexName;
		this.tableName = tableName;
		this.columnNameList = columnNameList;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public StatementType getStatementType() {
		return StatementType.CREATE_INDEX_STATEMENT;
	}
	
	public boolean isUnique() {
		return unique;
	}
	
	public NameChain getIndexName() {
		return indexName;
	}
	
	public NameChain getTableName() {
		return tableName;
	}
	
	public List<Name> getColumnNameList() {
		return columnNameList;
	}

}
