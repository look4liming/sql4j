package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public interface TableReference {
	
	public int getBeginIndex();
	public TableReference getParentTableReference();
	public void setParentTableReference(TableReference parentTableReference);
	
}
