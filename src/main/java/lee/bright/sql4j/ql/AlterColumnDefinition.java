package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class AlterColumnDefinition implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain tableName;
	private Name columnName;
	private SetColumnDefaultClause setColumnDefaultClause;
	private DropColumnDefaultClause dropColumnDefaultClause;
	
	private AlterColumnDefinition(SourceCode sourceCode, int beginIndex, 
			NameChain tableName, Name columnName) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.tableName = tableName;
		this.columnName = columnName;
	}
	
	public AlterColumnDefinition(SourceCode sourceCode, int beginIndex, 
			NameChain tableName, Name columnName, SetColumnDefaultClause setColumnDefaultClause) {
		this(sourceCode, beginIndex, tableName, columnName);
		this.setColumnDefaultClause = setColumnDefaultClause;
	}
	
	public AlterColumnDefinition(SourceCode sourceCode, int beginIndex, 
			NameChain tableName, Name columnName, DropColumnDefaultClause dropColumnDefaultClause) {
		this(sourceCode, beginIndex, tableName, columnName);
		this.dropColumnDefaultClause = dropColumnDefaultClause;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public StatementType getStatementType() {
		return StatementType.ALTER_COLUMN_DEFINITION;
	}
	
	public NameChain getTableName() {
		return tableName;
	}
	
	public Name getColumnName() {
		return columnName;
	}
	
	public SetColumnDefaultClause getSetColumnDefaultClause() {
		return setColumnDefaultClause;
	}
	
	public DropColumnDefaultClause getDropColumnDefaultClause() {
		return dropColumnDefaultClause;
	}

}
