package lee.bright.sql4j.ql;

public final class AddPrimaryKeyDefinition implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain tableName;
	private Name columnName;
	
	public AddPrimaryKeyDefinition(SourceCode sourceCode, int beginIndex, 
			NameChain tableName, Name columnName) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.tableName = tableName;
		this.columnName = columnName;
	}

	public SourceCode getSourceCode() {
		return sourceCode;
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public StatementType getStatementType() {
		return StatementType.ADD_PRIMARY_KEY_DEFINITION;
	}
	
	public NameChain getTableName() {
		return tableName;
	}
	
	public Name getColumnName() {
		return columnName;
	}

}
