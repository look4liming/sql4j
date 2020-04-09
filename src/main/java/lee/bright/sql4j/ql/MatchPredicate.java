package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class MatchPredicate implements Predicate {
	
	private ValueExpression valueExpression;
	private boolean unique;
	private boolean simple;
	private boolean partial;
	private boolean full;
	private Subquery subquery;
	private JdbcType dataType;
	
	public MatchPredicate(ValueExpression valueExpression, 
			boolean unique, boolean simple, boolean partial, 
			boolean full, Subquery subquery) {
		this.valueExpression = valueExpression;
		this.unique = unique;
		this.simple = simple;
		this.partial = partial;
		this.full = full;
		this.subquery = subquery;
	}
	
	public int getBeginIndex() {
		return valueExpression.getBeginIndex();
	}
	
	public int getEndIndex() {
		return subquery.getEndIndex();
	}
	
	public ValueExpression getValueExpression() {
		return valueExpression;
	}
	
	public boolean getUnique() {
		return unique;
	}
	
	public boolean getSimple() {
		return simple;
	}
	
	public boolean getPartial() {
		return partial;
	}
	
	public boolean getFull() {
		return full;
	}
	
	public Subquery getSubquery() {
		return subquery;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
