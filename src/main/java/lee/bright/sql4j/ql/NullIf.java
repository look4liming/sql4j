package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class NullIf implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private ValueExpression first;
	private ValueExpression second;
	private JdbcType dataType;
	
	public NullIf(int beginIndex, int endIndex, ValueExpression first, 
			ValueExpression second) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.first = first;
		this.second = second;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public ValueExpression getFirst() {
		return first;
	}
	
	public ValueExpression getSecond() {
		return second;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
