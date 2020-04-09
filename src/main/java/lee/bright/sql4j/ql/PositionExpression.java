package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class PositionExpression implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private ValueExpression valueExpression1;
	private ValueExpression valueExpression2;
	private JdbcType dataType;
	
	public PositionExpression(int beginIndex, int endIndex, 
			ValueExpression	valueExpression1, ValueExpression valueExpression2) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.valueExpression1 = valueExpression1;
		this.valueExpression2 = valueExpression2;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public ValueExpression getValueExpression1() {
		return valueExpression1;
	}
	
	public ValueExpression getValueExpression2() {
		return valueExpression2;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
