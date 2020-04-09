package lee.bright.sql4j.ql;

public class SetColumnDefaultClause {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private DefaultClause defaultClause;
	
	public SetColumnDefaultClause(SourceCode sourceCode, 
			int beginIndex, DefaultClause defaultClause) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.defaultClause = defaultClause;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public DefaultClause getDefaultClause() {
		return defaultClause;
	}

}
