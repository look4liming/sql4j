package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ColumnDefinition extends TableElement {
	
	private Name columnName;
	private DataType dataType;
	private DefaultClause defaultClause;
	private ColumnConstraintDefinition columnConstraintDefinition;

	public ColumnDefinition(SourceCode sourceCode, int beginIndex, Name columnName, 
			DataType dataType, DefaultClause defaultClause, 
			ColumnConstraintDefinition columnConstraintDefinition) {
		super(sourceCode, beginIndex);
		this.columnName = columnName;
		this.dataType = dataType;
		this.defaultClause = defaultClause;
		this.columnConstraintDefinition = columnConstraintDefinition;
	}
	
	public Name getColumnName() {
		return columnName;
	}
	
	public DataType getDataType() {
		return dataType;
	}
	
	public DefaultClause getDefaultClause() {
		return defaultClause;
	}
	
	public ColumnConstraintDefinition getColumnConstraintDefinition() {
		return columnConstraintDefinition;
	}

}
