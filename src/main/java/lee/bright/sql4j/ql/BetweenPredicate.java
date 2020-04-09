package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class BetweenPredicate implements Predicate {
	
	private boolean not;
	private ValueExpression valueExpression;
	private ValueExpression valueExpression1;
	private ValueExpression valueExpression2;
	private JdbcType dataType;
	
	public BetweenPredicate(boolean not, 
			ValueExpression valueExpression, 
			ValueExpression valueExpression1, 
			ValueExpression valueExpression2) {
		this.not = not;
		this.valueExpression = valueExpression;
		this.valueExpression1 = valueExpression1;
		this.valueExpression2 = valueExpression2;
	}
	
	public int getBeginIndex() {
		return valueExpression.getBeginIndex();
	}
	
	public int getEndIndex() {
		return valueExpression2.getEndIndex();
	}
	
	public boolean getNot() {
		return not;
	}
	
	public ValueExpression getValueExpression() {
		return valueExpression;
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
