package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class AddTableConstraintDefinition implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain tableName;
	private TableConstraintDefinition tableConstraintDefinition;
	
	public AddTableConstraintDefinition(SourceCode sourceCode, int beginIndex, 
			NameChain tableName, TableConstraintDefinition tableConstraintDefinition) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.tableName = tableName;
		this.tableConstraintDefinition = tableConstraintDefinition;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public StatementType getStatementType() {
		return StatementType.ADD_TABLE_CONSTRAINT_DEFINITION;
	}
	
	public NameChain getTableName() {
		return tableName;
	}
	
	public TableConstraintDefinition getTableConstraintDefinition() {
		return tableConstraintDefinition;
	}

}
