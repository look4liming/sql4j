package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class BooleanFactor implements BooleanValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private boolean not;
	private BooleanValueExpression booleanValueExpression;
	private JdbcType dataType;
	
	public BooleanFactor(int beginIndex, int endIndex, 
			boolean not, BooleanValueExpression booleanValueExpression) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.not = not;
		this.booleanValueExpression = booleanValueExpression;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public boolean getNot() {
		return not;
	}
	
	public BooleanValueExpression getBooleanValueExpression() {
		return booleanValueExpression;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
