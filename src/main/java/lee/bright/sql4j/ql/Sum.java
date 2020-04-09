package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class Sum implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private Distinct distinct;
	private ValueExpression valueExpression;
	private JdbcType dataType;
	
	public Sum(int beginIndex, int endIndex, Distinct distinct, 
			ValueExpression valueExpression) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.distinct = distinct;
		this.valueExpression = valueExpression;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public Distinct getDistinct() {
		return distinct;
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
