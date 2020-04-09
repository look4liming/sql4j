package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ElseClause {
	
	private ValueExpression result;
	
	public ElseClause(ValueExpression result) {
		this.result = result;
	}
	
	public ValueExpression getResult() {
		return result;
	}

}
