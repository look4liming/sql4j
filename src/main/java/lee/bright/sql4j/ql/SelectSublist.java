package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class SelectSublist {
	
	private ValueExpression valueExpression;
	private Name name;
	
	public SelectSublist(ValueExpression valueExpression, 
			Name name) {
		this.valueExpression = valueExpression;
		this.name = name;
	}
	
	public ValueExpression getValueExpression() {
		return valueExpression;
	}
	
	public Name getName() {
		return name;
	}

}
