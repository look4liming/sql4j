package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class TimestampLiteral implements ValueExpression {

	private int beginIndex;
	private int endIndex;
	private StringLiteral timestampStringLiteral;
	private Parameter parameter;
	private JdbcType dataType;
	
	public TimestampLiteral(int beginIndex, int endIndex, 
			StringLiteral timestampStringLiteral) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.timestampStringLiteral = timestampStringLiteral;
	}
	
	public TimestampLiteral(int beginIndex, int endIndex, 
			Parameter parameter) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.parameter = parameter;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public StringLiteral getTimestampStringLiteral() {
		return timestampStringLiteral;
	}
	
	public Parameter getParameter() {
		return parameter;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
