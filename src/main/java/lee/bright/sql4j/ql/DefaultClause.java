package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class DefaultClause {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private ValueExpression defaultValue;
	
	public DefaultClause(SourceCode sourceCode, int beginIndex, ValueExpression defaultValue) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.defaultValue = defaultValue;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public ValueExpression getDefaultValue() {
		return defaultValue;
	}

}
