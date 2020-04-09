package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class TableConstraintDefinition extends TableElement {
	
	private NameChain constraintName;
	private TableConstraint tableConstraint;
	
	public TableConstraintDefinition(SourceCode sourceCode, int beginIndex, 
			NameChain constraintName, TableConstraint tableConstraint) {
		super(sourceCode, beginIndex);
		this.constraintName = constraintName;
		this.tableConstraint = tableConstraint;
	}
	
	public NameChain getConstraintName() {
		return constraintName;
	}
	
	public TableConstraint getTableConstraint() {
		return tableConstraint;
	}

}
