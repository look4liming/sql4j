package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class InnerJoin implements TableReference, JoinedTable {
	
	private int beginIndex;
	private TableReference parentTableReference;
	private TableReference left;
	private TableReference right;
	private BooleanValueExpression joinCondition;
	
	public InnerJoin(int beginIndex, 
			TableReference left, 
			TableReference right, 
			BooleanValueExpression joinCondition) {
		this.beginIndex = beginIndex;
		this.left = left;
		this.left.setParentTableReference(this);
		this.right = right;
		this.right.setParentTableReference(this);
		this.joinCondition = joinCondition;
	}
	
	public TableReference getLeft() {
		return left;
	}
	
	public TableReference getRight() {
		return right;
	}
	
	public BooleanValueExpression getJoinCondition() {
		return joinCondition;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public TableReference getParentTableReference() {
		return parentTableReference;
	}
	
	public void setParentTableReference(TableReference parentTableReference) {
		this.parentTableReference = parentTableReference;
	}

}
