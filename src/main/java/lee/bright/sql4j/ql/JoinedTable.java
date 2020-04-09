package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public interface JoinedTable {
	
	public TableReference getLeft();
	public TableReference getRight();
	public BooleanValueExpression getJoinCondition();
	
}
