package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class DropTableConstraintDefinition implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain tableName;
	private Name constraintName;
	
	public DropTableConstraintDefinition(SourceCode sourceCode, int beginIndex, 
			NameChain tableName, Name constraintName) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.tableName = tableName;
		this.constraintName = constraintName;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public StatementType getStatementType() {
		return StatementType.DROP_TABLE_CONSTRAINT_DEFINITION;
	}
	
	public NameChain getTableName() {
		return tableName;
	}
	
	public Name getConstraintName() {
		return constraintName;
	}

}
