package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class DropIndexStatement implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain indexName;
	
	public DropIndexStatement(SourceCode sourceCode, int beginIndex, 
			NameChain indexName) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.indexName = indexName;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public StatementType getStatementType() {
		return StatementType.DROP_INDEX_STATEMENT;
	}
	
	public NameChain getIndexName() {
		return indexName;
	}

}
