package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ConstraintNameDefinition {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain constraintName;
	
	public ConstraintNameDefinition(SourceCode sourceCode, int beginIndex, NameChain constraintName) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.constraintName = constraintName;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public NameChain getConstraintName() {
		return constraintName;
	}

}
