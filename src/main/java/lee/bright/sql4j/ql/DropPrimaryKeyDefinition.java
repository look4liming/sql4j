package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class DropPrimaryKeyDefinition implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain tableName;
	
	public DropPrimaryKeyDefinition(SourceCode sourceCode, int beginIndex, 
			NameChain tableName) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.tableName = tableName;
	}

	public SourceCode getSourceCode() {
		return sourceCode;
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public StatementType getStatementType() {
		return StatementType.DROP_PRIMARY_KEY_DEFINITION;
	}
	
	public NameChain getTableName() {
		return tableName;
	}

}
