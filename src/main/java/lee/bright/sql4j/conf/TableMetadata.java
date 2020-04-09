package lee.bright.sql4j.conf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bright Lee
 */
public final class TableMetadata {
	
	private final Map<String, ColumnMetadata> map = 
			new HashMap<String, ColumnMetadata>();
	
	public TableMetadata(List<ColumnMetadata> list) {
		for (ColumnMetadata columnMetadata : list) {
			map.put(columnMetadata.getColumnName(), 
					columnMetadata);
		}
	}
	
	public ColumnMetadata getColumnMetadata(String columnName) {
		ColumnMetadata columnMetadata = map.get(columnName);
		return columnMetadata;
	}
	
	public boolean hasColumnMetadata(String columnName) {
		boolean b = map.containsKey(columnName);
		return b;
	}

}
