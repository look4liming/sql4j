package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class SetClause {
	
	private Name updateTarget;
	private ValueExpression updateSource;
	
	public SetClause(Name updateTarget, 
			ValueExpression updateSource) {
		this.updateTarget = updateTarget;
		this.updateSource = updateSource;
	}
	
	public Name getUpdateTarget() {
		return updateTarget;
	}
	
	public ValueExpression getUpdateSource() {
		return updateSource;
	}

}
