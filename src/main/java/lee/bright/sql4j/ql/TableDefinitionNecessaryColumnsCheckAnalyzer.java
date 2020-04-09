package lee.bright.sql4j.ql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;

/**
 * @author Bright Lee
 */
public final class TableDefinitionNecessaryColumnsCheckAnalyzer {
	
	//private Configuration configuration;
	//private SourceCode sourceCode;
	private TableDefinition tableDefinition;
	
	public TableDefinitionNecessaryColumnsCheckAnalyzer(
			Configuration configuration, SourceCode sourceCode, 
			TableDefinition tableDefinition) {
		//this.configuration = configuration;
		//this.sourceCode = sourceCode;
		this.tableDefinition = tableDefinition;
	}
	
	public void analyze() {
		Map<String, String> map = new HashMap<String, String>();
		List<TableElement> tableElementList = tableDefinition.getTableElementList();
		for (int i = 0; i < tableElementList.size(); i++) {
			TableElement tableElement = tableElementList.get(i);
			if (!(tableElement instanceof ColumnDefinition)) {
				continue;
			}
			ColumnDefinition columnDefinition = (ColumnDefinition) tableElement;
			String columnName = columnDefinition.getColumnName().getContent().toLowerCase();
			map.put(columnName, columnName);
		}
		if (!map.containsKey("pk")) {
			throw Sql4jException.getSql4jException(tableDefinition.getSourceCode(), tableDefinition.getBeginIndex(), 
					"'pk VARCHAR(100) PRIMARY KEY' lost.");
		}
		if (!map.containsKey("ts")) {
			throw Sql4jException.getSql4jException(tableDefinition.getSourceCode(), tableDefinition.getBeginIndex(), 
					"'ts TIMESTAMP NOT NULL' lost.");
		}
		if (!map.containsKey("hash_foremost_db")) {
			throw Sql4jException.getSql4jException(tableDefinition.getSourceCode(), tableDefinition.getBeginIndex(), 
					"'hash_foremost_db INT NOT NULL' lost.");
		}
	}

}
