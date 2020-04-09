package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class TablePrimary implements TableReference {
	
	private int beginIndex;
	private TableReference parentTableReference;
	private NameChain tableName;
	private Name correlationName;
	
	public TablePrimary(int beginIndex, NameChain tableName, 
			Name correlationName) {
		this.beginIndex = beginIndex;
		this.tableName = tableName;
		this.correlationName = correlationName;
	}
	
	public NameChain getTableName() {
		return tableName;
	}
	
	public Name getCorrelationName() {
		return correlationName;
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
