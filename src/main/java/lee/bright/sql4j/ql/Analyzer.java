package lee.bright.sql4j.ql;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;

/**
 * @author Bright Lee
 */
public final class Analyzer {
	
	private Configuration configuration;
	private Parser parser;
	private SourceCode sourceCode;
	
	public Analyzer(Configuration configuration, SourceCode sourceCode) {
		this.configuration = configuration;
		this.parser = new Parser(this.configuration, sourceCode);
		this.sourceCode = sourceCode;
	}
	
	public Statement analyze() {
		Statement statement = parser.parse();
		if (statement == null) {
			return null;
		}
		if (statement instanceof SelectStatement) {
			SelectStatement selectStatement = (SelectStatement) statement;
			if (selectStatement instanceof QuerySpecification) {
				QuerySpecification querySpecification = 
						(QuerySpecification) selectStatement;
				QuerySpecificationCheckForThePresenceOfAbandonedStructuresAnalyzer analyzer1 = 
						new QuerySpecificationCheckForThePresenceOfAbandonedStructuresAnalyzer(
								sourceCode, querySpecification);
				analyzer1.analyze();
				QuerySpecificationDetectTableNameConflictsAnalyzer analyzer2 = 
						new QuerySpecificationDetectTableNameConflictsAnalyzer(
								sourceCode, querySpecification);
				analyzer2.analyze();
				QuerySpecificationSetParentStatementsForSubqueriesAnalyzer analyzer3 = 
						new QuerySpecificationSetParentStatementsForSubqueriesAnalyzer(
								sourceCode, querySpecification);
				analyzer3.analyze();
				QuerySpecificationSetFullyQualifiedColumnNamesAnalyzer analyzer4 = 
						new QuerySpecificationSetFullyQualifiedColumnNamesAnalyzer(configuration, 
								sourceCode, querySpecification);
				analyzer4.analyze();
				// TODO
				QuerySpecificationHashRuleAnalyzer analyzer = 
						new QuerySpecificationHashRuleAnalyzer(
								configuration, sourceCode, querySpecification);
				analyzer.analyze();
				return querySpecification;
			}
			if (selectStatement instanceof Union) {
				throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
						"Union statement is not supported.");
			}
			if (selectStatement instanceof Intersect) {
				throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
						"Intersect statement is not supported.");
			}
			if (selectStatement instanceof Except) {
				throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
						"Except statement is not supported.");
			}
			throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
					"This statement is not supported.");
		}
		if (statement instanceof UpdateStatement) {
			UpdateStatement updateStatement = (UpdateStatement) statement;
			UpdateStatementCheckForThePresenceOfAbandonedStructuresAnalyzer analyzer1 = 
					new UpdateStatementCheckForThePresenceOfAbandonedStructuresAnalyzer(
							sourceCode, updateStatement);
			analyzer1.analyze();
			UpdateStatementDetectTableNameConflictsAnalyzer analyzer2 = 
					new UpdateStatementDetectTableNameConflictsAnalyzer(
							sourceCode, updateStatement);
			analyzer2.analyze();
			UpdateStatementSetParentStatementsForSubqueriesAnalyzer analyzer3 = 
					new UpdateStatementSetParentStatementsForSubqueriesAnalyzer(
							sourceCode, updateStatement);
			analyzer3.analyze();
			UpdateStatementSetFullyQualifiedColumnNamesAnalyzer analyzer4 = 
					new UpdateStatementSetFullyQualifiedColumnNamesAnalyzer(configuration, 
							sourceCode, updateStatement);
			analyzer4.analyze();
			// TODO
			UpdateStatementHashRuleAnalyzer analyzer = 
					new UpdateStatementHashRuleAnalyzer(
							configuration, sourceCode, updateStatement);
			analyzer.analyze();
			return statement;
		}
		if (statement instanceof InsertStatement) {
			InsertStatement insertStatement = (InsertStatement) statement;
			InsertStatementCheckForThePresenceOfAbandonedStructuresAnalyzer analyzer1 = 
					new InsertStatementCheckForThePresenceOfAbandonedStructuresAnalyzer(
							sourceCode, insertStatement);
			analyzer1.analyze();
			InsertStatementDetectTableNameConflictsAnalyzer analyzer2 = 
					new InsertStatementDetectTableNameConflictsAnalyzer(
							sourceCode, insertStatement);
			analyzer2.analyze();
			InsertStatementSetParentStatementsForSubqueriesAnalyzer analyzer3 = 
					new InsertStatementSetParentStatementsForSubqueriesAnalyzer(
							sourceCode, insertStatement);
			analyzer3.analyze();
			InsertStatementSetFullyQualifiedColumnNamesAnalyzer analyzer4 = 
					new InsertStatementSetFullyQualifiedColumnNamesAnalyzer(
							configuration, sourceCode, insertStatement);
			analyzer4.analyze();
			InsertStatementNecessaryColumnsCheckAnalyzer analyzer5 = 
					new InsertStatementNecessaryColumnsCheckAnalyzer(
							configuration, sourceCode, insertStatement);
			analyzer5.analyze();
			InsertStatementHashRuleAnalyzer analyzer = 
					new InsertStatementHashRuleAnalyzer(
							configuration, sourceCode, insertStatement);
			analyzer.analyze();
			return statement;
		}
		if (statement instanceof DeleteStatement) {
			DeleteStatement deleteStatement = (DeleteStatement) statement;
			DeleteStatementCheckForThePresenceOfAbandonedStructuresAnalyzer analyzer1 = 
					new DeleteStatementCheckForThePresenceOfAbandonedStructuresAnalyzer(
							sourceCode, deleteStatement);
			analyzer1.analyze();
			DeleteStatementDetectTableNameConflictsAnalyzer analyzer2 = 
					new DeleteStatementDetectTableNameConflictsAnalyzer(
							sourceCode, deleteStatement);
			analyzer2.analyze();
			DeleteStatementSetParentStatementsForSubqueriesAnalyzer analyzer3 = 
					new DeleteStatementSetParentStatementsForSubqueriesAnalyzer(
							sourceCode, deleteStatement);
			analyzer3.analyze();
			DeleteStatementSetFullyQualifiedColumnNamesAnalyzer analyzer4 = 
					new DeleteStatementSetFullyQualifiedColumnNamesAnalyzer(configuration, 
							sourceCode, deleteStatement);
			analyzer4.analyze();
			DeleteStatementHashRuleAnalyzer analyzer = 
					new DeleteStatementHashRuleAnalyzer(
							configuration, sourceCode, deleteStatement);
			analyzer.analyze();
			return statement;
		}
		if (statement instanceof TableDefinition) {
			TableDefinition tableDefinition = (TableDefinition) statement;
			TableDefinitionNecessaryColumnsCheckAnalyzer analyzer1 = 
					new TableDefinitionNecessaryColumnsCheckAnalyzer(
							configuration, sourceCode, tableDefinition);
			analyzer1.analyze();
			// TODO
			return statement;
		}
		if (statement instanceof DropTableStatement) {
			// TODO
			return statement;
		}
		if (statement instanceof DropIndexStatement) {
			// TODO
			return statement;
		}
		if (statement instanceof CreateIndexStatement) {
			// TODO
			return statement;
		}
		if (statement instanceof AddColumnDefinition) {
			// TODO
			return statement;
		}
		if (statement instanceof DropColumnDefinition) {
			// TODO
			return statement;
		}
		if (statement instanceof AlterColumnDefinition) {
			// TODO
			return statement;
		}
		if (statement instanceof AddTableConstraintDefinition) {
			// TODO
			return statement;
		}
		if (statement instanceof DropTableConstraintDefinition) {
			// TODO
			return statement;
		}
		if (statement instanceof DropPrimaryKeyDefinition) {
			// TODO
			return statement;
		}
		if (statement instanceof AddPrimaryKeyDefinition) {
			// TODO
			return statement;
		}
		if (statement instanceof ModifyColumnDefinition) {
			// TODO
			return statement;
		}
		throw Sql4jException.getSql4jException(sourceCode, statement.getBeginIndex(), 
				"This statement is not supported.");
	}

}
