package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class Substring implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private ValueExpression valueExpression;
	private ValueExpression startPosition;
	private ValueExpression stringLength;
	private JdbcType dataType;
	
	public Substring(int beginIndex, int endIndex, 
			ValueExpression valueExpression, 
			ValueExpression startPosition, 
			ValueExpression stringLength) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.valueExpression = valueExpression;
		this.startPosition = startPosition;
		this.stringLength = stringLength;
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
	
	public ValueExpression getStartPosition() {
		return startPosition;
	}
	
	public ValueExpression getStringLength() {
		return stringLength;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
