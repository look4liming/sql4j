package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public interface SelectStatement extends Statement {
	
	public Statement getParentStatement();
	public void setParentStatement(Statement parentStatement);
	public DerivedTable getDerivedTable();
	public void setDerivedTable(DerivedTable derivedTable);
	
}
