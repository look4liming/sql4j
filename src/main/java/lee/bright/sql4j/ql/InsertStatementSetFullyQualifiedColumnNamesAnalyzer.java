package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;
import lee.bright.sql4j.conf.TableMetadata;

/**
 * @author Bright Lee
 */
public final class InsertStatementSetFullyQualifiedColumnNamesAnalyzer {

	private Configuration configuration;
	private SourceCode sourceCode;
	private InsertStatement insertStatement;
	
	public InsertStatementSetFullyQualifiedColumnNamesAnalyzer(Configuration configuration, 
			SourceCode sourceCode, InsertStatement insertStatement) {
		this.configuration = configuration;
		this.sourceCode = sourceCode;
		this.insertStatement = insertStatement;
	}
	
	public void analyze() {
		NameChain insertionTarget = insertStatement.getInsertionTarget();
		String tableName = insertionTarget.toLowerCaseString();
		TableMetadata tableMetadata = configuration.getTableMetadata(tableName);
		List<Name> insertColumnList = insertStatement.getInsertColumnList();
		for (int i = 0; i < insertColumnList.size(); i++) {
			Name insertColumn = insertColumnList.get(i);
			String insertColumnName = insertColumn.getContent().toLowerCase();
			if (!tableMetadata.hasColumnMetadata(insertColumnName)) {
				throw Sql4jException.getSql4jException(sourceCode, insertColumn.getBeginIndex(), 
						"This column does not exist in the table.");
			}
			NameChain fullyQualifiedName = getFullyQualifiedName(insertionTarget, insertColumn);
			insertColumn.setFullyQualifiedName(fullyQualifiedName);
		}
		//List<ValueExpression> list = insertStatement.getValueExpressionList();
		// TODO
	}
	
	private static NameChain getFullyQualifiedName(NameChain insertionTarget, Name insertColumn) {
		List<Name> list = new ArrayList<Name>(insertionTarget.size() + 1);
		for (int i = 0; i < insertionTarget.size(); i++) {
			list.add(insertionTarget.get(i));
		}
		list.add(insertColumn);
		NameChain fullyQualifiedName = new NameChain(list);
		return fullyQualifiedName;
	}
	
}
