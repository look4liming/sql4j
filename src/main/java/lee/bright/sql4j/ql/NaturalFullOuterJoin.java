package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class NaturalFullOuterJoin implements TableReference {

	private int beginIndex;
	private TableReference parentTableReference;
	private TableReference left;
	private TableReference right;
	
	public NaturalFullOuterJoin(int beginIndex, 
			TableReference left, 
			TableReference right) {
		this.beginIndex = beginIndex;
		this.left = left;
		this.left.setParentTableReference(this);
		this.right = right;
		this.right.setParentTableReference(this);
	}
	
	public TableReference getLeft() {
		return left;
	}
	
	public TableReference getRight() {
		return right;
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
