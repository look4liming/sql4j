package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ModifyColumnDefinition implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain tableName;
	private ColumnDefinition columnDefinition;
	
	public ModifyColumnDefinition(SourceCode sourceCode, int beginIndex, 
			NameChain tableName, ColumnDefinition columnDefinition) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.tableName = tableName;
		this.columnDefinition = columnDefinition;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public NameChain getTableName() {
		return tableName;
	}
	
	public ColumnDefinition getColumnDefinition() {
		return columnDefinition;
	}

	public StatementType getStatementType() {
		return StatementType.MODIFY_COLUMN_DEFINITION;
	}

}
