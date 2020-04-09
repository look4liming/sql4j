package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class SimpleWhenClause {
	
	private ValueExpression whenOperand;
	private ValueExpression result;
	
	public SimpleWhenClause(ValueExpression whenOperand, 
			ValueExpression result) {
		this.whenOperand = whenOperand;
		this.result = result;
	}
	
	public ValueExpression getWhenOperand() {
		return whenOperand;
	}
	
	public ValueExpression getResult() {
		return result;
	}

}
