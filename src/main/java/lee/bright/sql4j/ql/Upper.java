package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class Upper implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private ValueExpression valueExpression;
	private JdbcType dataType;
	
	public Upper(int beginIndex, int endIndex, ValueExpression valueExpression) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.valueExpression = valueExpression;
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
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
