package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class TimeLiteral implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private StringLiteral timeStringLiteral;
	private Parameter parameter;
	private JdbcType dataType;
	
	public TimeLiteral(int beginIndex, int endIndex, 
			StringLiteral timeStringLiteral) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.timeStringLiteral = timeStringLiteral;
	}
	
	public TimeLiteral(int beginIndex, int endIndex, 
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
	
	public StringLiteral getTimeStringLiteral() {
		return timeStringLiteral;
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
