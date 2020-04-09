package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ColumnConstraint {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private ColumnConstraintEnum columnConstraintEnum;
	
	public ColumnConstraint(SourceCode sourceCode, int beginIndex, ColumnConstraintEnum columnConstraintEnum) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.columnConstraintEnum = columnConstraintEnum;
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public ColumnConstraintEnum getColumnConstraintEnum() {
		return columnConstraintEnum;
	}

}
