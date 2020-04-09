package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class DerivedTable implements TableReference {
	
	private int beginIndex;
	private TableReference parentTableReference;
	private SelectStatement selectStatement;
	private Name correlationName;
	
	public DerivedTable(int beginIndex, 
			SelectStatement selectStatement, Name correlationName) {
		this.beginIndex = beginIndex;
		this.selectStatement = selectStatement;
		this.correlationName = correlationName;
		this.selectStatement.setDerivedTable(this);
	}
	
	public SelectStatement getSelectStatement() {
		return selectStatement;
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
