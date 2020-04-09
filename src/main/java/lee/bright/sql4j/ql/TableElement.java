package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public abstract class TableElement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	
	public TableElement(SourceCode sourceCode, int beginIndex) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
	}
	
	public final SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public final int getBeginIndex() {
		return beginIndex;
	}

}
