package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class GroupingColumnReference {
	
	private NameChain columnReference;
	private NameChain collationName;
	
	public GroupingColumnReference(
			NameChain columnReference, 
			NameChain collationName) {
		this.columnReference = columnReference;
		this.collationName = collationName;
	}
	
	public NameChain getColumnReference() {
		return columnReference;
	}
	
	public NameChain getCollationName() {
		return collationName;
	}

}
