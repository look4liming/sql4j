package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ColumnConstraintDefinition {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private ConstraintNameDefinition constraintNameDefinition;
	private ColumnConstraint columnConstraint;
	
	public ColumnConstraintDefinition(SourceCode sourceCode, int beginIndex, 
			ConstraintNameDefinition constraintNameDefinition, ColumnConstraint columnConstraint) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.constraintNameDefinition = constraintNameDefinition;
		this.columnConstraint = columnConstraint;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public ConstraintNameDefinition getConstraintNameDefinition() {
		return constraintNameDefinition;
	}
	
	public ColumnConstraint getColumnConstraint() {
		return columnConstraint;
	}

}
