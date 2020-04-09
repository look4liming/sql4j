package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class InPredicate implements Predicate {
	
	private boolean not;
	private ValueExpression valueExpression;
	private Subquery subquery;
	private List<ValueExpression> inValueList;
	private JdbcType dataType;
	
	public InPredicate(boolean not, 
			ValueExpression valueExpression, 
			Subquery subquery, 
			List<ValueExpression> inValueList) {
		this.not = not;
		this.valueExpression = valueExpression;
		this.subquery = subquery;
		this.inValueList = inValueList;
	}
	
	public int getBeginIndex() {
		return valueExpression.getBeginIndex();
	}
	
	public int getEndIndex() {
		if (subquery != null) {
			return subquery.getEndIndex();
		}
		return inValueList.get(inValueList.size() - 1).
				getEndIndex();
	}
	
	public boolean getNot() {
		return not;
	}
	
	public ValueExpression getValueExpression() {
		return valueExpression;
	}
	
	public Subquery getSubquery() {
		return subquery;
	}
	
	public List<ValueExpression> getInValueList() {
		return inValueList;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
