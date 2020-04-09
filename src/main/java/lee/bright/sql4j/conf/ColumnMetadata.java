package lee.bright.sql4j.conf;

/**
 * @author Bright Lee
 */
public final class ColumnMetadata {
	
	private int columnType;
	private String columnName;
	
	public ColumnMetadata(int columnType, String columnName) {
		this.columnType = columnType;
		this.columnName = columnName.toLowerCase();
	}
	
	public int getColumnType() {
		return columnType;
	}
	
	public String getColumnName() {
		return columnName;
	}

}
