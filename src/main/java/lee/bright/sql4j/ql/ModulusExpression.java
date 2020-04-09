package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ModulusExpression implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private ValueExpression dividend;
	private ValueExpression divisor;
	private JdbcType dataType;
	
	public ModulusExpression(int beginIndex, int endIndex, 
			ValueExpression dividend, ValueExpression divisor) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.dividend = dividend;
		this.divisor = divisor;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public ValueExpression getDividend() {
		return dividend;
	}
	
	public ValueExpression getDivisor() {
		return divisor;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
