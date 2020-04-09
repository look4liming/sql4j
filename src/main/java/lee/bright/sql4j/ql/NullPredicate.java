package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class NullPredicate implements Predicate {
	
	private ValueExpression valueExpression;
	private boolean not;
	private JdbcType dataType;
	
	public NullPredicate(ValueExpression valueExpression, 
			boolean not) {
		this.valueExpression = valueExpression;
		this.not = not;
	}
	
	public int getBeginIndex() {
		return valueExpression.getBeginIndex();
	}
	
	public int getEndIndex() {
		return valueExpression.getEndIndex();
	}
	
	public ValueExpression getValueExpression() {
		return valueExpression;
	}
	
	public boolean getNot() {
		return not;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
