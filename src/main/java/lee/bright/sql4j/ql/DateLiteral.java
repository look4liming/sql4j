package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class DateLiteral implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private StringLiteral dateStringLiteral;
	private Parameter parameter;
	private JdbcType dataType;
	
	public DateLiteral(int beginIndex, int endIndex, 
			StringLiteral dateStringLiteral) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.dateStringLiteral = dateStringLiteral;
	}
	
	public DateLiteral(int beginIndex, int endIndex, 
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
	
	public StringLiteral getDateStringLiteral() {
		return dateStringLiteral;
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
