package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ComparisonPredicate implements Predicate {
	
	private ValueExpression left;
	private CompOp compOp;
	private Quantifier quantifier;
	private ValueExpression right;
	private JdbcType dataType;
	
	public ComparisonPredicate(ValueExpression left, 
			CompOp compOp, Quantifier quantifier, 
			ValueExpression right) {
		this.left = left;
		this.compOp = compOp;
		this.quantifier = quantifier;
		this.right = right;
	}
	
	public int getBeginIndex() {
		return left.getBeginIndex();
	}
	
	public int getEndIndex() {
		return right.getEndIndex();
	}
	
	public ValueExpression getLeft() {
		return left;
	}
	
	public CompOp getCompOp() {
		return compOp;
	}
	
	public Quantifier getQuantifier() {
		return quantifier;
	}
	
	public ValueExpression getRight() {
		return right;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
