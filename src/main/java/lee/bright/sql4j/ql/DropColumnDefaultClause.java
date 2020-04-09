package lee.bright.sql4j.ql;

public class DropColumnDefaultClause {
	
	private SourceCode sourceCode;
	private int beginIndex;
	
	public DropColumnDefaultClause(SourceCode sourceCode, int beginIndex) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}

}
