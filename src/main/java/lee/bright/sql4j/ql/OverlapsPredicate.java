package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class OverlapsPredicate implements Predicate {
	
	private ValueExpression left;
	private ValueExpression right;
	private JdbcType dataType;
	
	public OverlapsPredicate(ValueExpression left, 
			ValueExpression right) {
		this.left = left;
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
