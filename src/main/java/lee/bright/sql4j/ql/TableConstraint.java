package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class TableConstraint {
	
	private TableConstraintType tableConstraintType;
	private List<Name> columnNameList;
	
	public TableConstraint(TableConstraintType tableConstraintType, 
			List<Name> columnNameList) {
		this.tableConstraintType = tableConstraintType;
		this.columnNameList = columnNameList;
	}
	
	public TableConstraintType getTableConstraintType() {
		return tableConstraintType;
	}
	
	public List<Name> getColumnNameList() {
		return columnNameList;
	}

}
