package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.conf.Configuration;

/**
 * @author Bright Lee
 */
public final class InsertStatementHashRuleAnalyzer {
	
	private Configuration configuration;
	private SourceCode sourceCode;
	private InsertStatement insertStatement;
	
	public InsertStatementHashRuleAnalyzer(
			Configuration configuration,
			SourceCode sourceCode,
			InsertStatement insertStatement) {
		this.configuration = configuration;
		this.sourceCode = sourceCode;
		this.insertStatement = insertStatement;
	}
	
	public void analyze() {
		List<Name> insertColumnList = insertStatement.getInsertColumnList();
		List<ValueExpression> valueExpressionList = insertStatement.getValueExpressionList();
		if (valueExpressionList != null) {
			NameChain insertionTarget = insertStatement.getInsertionTarget();
			String insertionTargetName = insertionTarget.toLowerCaseString();
			for (int i = 0; i < valueExpressionList.size(); i++) {
				Name insertColumn = insertColumnList.get(i);
				ValueExpression valueExpression = valueExpressionList.get(i);
				String insertColumnName = insertionTargetName + '.' + 
						insertColumn.getContent().toLowerCase();
				if (configuration.isHashColumn(insertColumnName)) {
					List<Name> list = new ArrayList<Name>(1);
					list.add(insertColumn);
					NameChain key = new NameChain(list);
					key.setFullyQualifiedName(insertColumn.getFullyQualifiedName());
					HashUtil.checkHashColumnAndHashValue(configuration, sourceCode, new HashKeyValue(key, valueExpression));
					insertStatement.addHashColumnName(key);
					insertStatement.addHashColumnValue(valueExpression);
				} else {
					// TODO analyze(valueExpression);
				}
			}
		}
	}
	
}
