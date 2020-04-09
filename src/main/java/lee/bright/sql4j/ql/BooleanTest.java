package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class BooleanTest implements BooleanValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private BooleanValueExpression booleanValueExpression;
	private boolean not;
	private TruthValue truthValue;
	private JdbcType dataType;
	
	public BooleanTest(int beginIndex, int endIndex, 
			BooleanValueExpression booleanValueExpression, 
			boolean not, TruthValue truthValue) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.booleanValueExpression = booleanValueExpression;
		this.not = not;
		this.truthValue = truthValue;
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}
	
	public BooleanValueExpression getBooleanValueExpression() {
		return booleanValueExpression;
	}
	
	public boolean getNot() {
		return not;
	}
	
	public TruthValue getTruthValue() {
		return truthValue;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
