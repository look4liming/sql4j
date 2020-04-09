package lee.bright.sql4j.ql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lee.bright.sql4j.conf.Configuration;

/**
 * @author Bright Lee
 */
public final class InsertStatementNecessaryColumnsCheckAnalyzer {
	
	//private Configuration configuration;
	//private SourceCode sourceCode;
	private InsertStatement insertStatement;
	
	public InsertStatementNecessaryColumnsCheckAnalyzer(
			Configuration configuration, SourceCode sourceCode, 
			InsertStatement insertStatement) {
		//this.configuration = configuration;
		//this.sourceCode = sourceCode;
		this.insertStatement = insertStatement;
	}
	
	public void analyze() {
		Map<String, String> necessaryColumnNameMap = new HashMap<String, String>();
		List<Name> insertColumnList = insertStatement.getInsertColumnList();
		for (int i = 0; i < insertColumnList.size(); i++) {
			Name insertColumn = insertColumnList.get(i);
			String insertColumnName = insertColumn.getContent();
			if ("pk".equalsIgnoreCase(insertColumnName)) {
				necessaryColumnNameMap.put("pk", "pk");
				continue;
			}
			if ("ts".equalsIgnoreCase(insertColumnName)) {
				necessaryColumnNameMap.put("ts", "ts");
				continue;
			}
			if ("hash_foremost_db".equalsIgnoreCase(insertColumnName)) {
				necessaryColumnNameMap.put("hash_foremost_db", "hash_foremost_db");
				continue;
			}
		}
		if (!necessaryColumnNameMap.containsKey("pk")) {
			insertStatement.addLostNecessaryColumn("pk");
		}
		if (!necessaryColumnNameMap.containsKey("ts")) {
			insertStatement.addLostNecessaryColumn("ts");
		}
		if (!necessaryColumnNameMap.containsKey("hash_foremost_db")) {
			insertStatement.addLostNecessaryColumn("hash_foremost_db");
		}
	}

}
