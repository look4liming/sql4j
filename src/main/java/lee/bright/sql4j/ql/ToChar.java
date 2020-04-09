package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ToChar implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private ValueExpression valueExpression;
	private StringLiteral pattern;
	private JdbcType dataType;
	
	public ToChar(int beginIndex, int endIndex, 
			ValueExpression valueExpression, 
			StringLiteral pattern) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.valueExpression = valueExpression;
		this.pattern = pattern;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public ValueExpression getValueExpression() {
		return valueExpression;
	}
	
	public StringLiteral getPattern() {
		return pattern;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
