package lee.bright.sql4j.ql;

import lee.bright.sql4j.Sql4jException;

/**
 * @author Bright Lee
 */
public final class CallStatementCheckForThePresenceOfAbandonedStructuresAnalyzer {
	
	private SourceCode sourceCode;
	private CallStatement callStatement;
	
	public CallStatementCheckForThePresenceOfAbandonedStructuresAnalyzer(
			SourceCode sourceCode, CallStatement callStatement) {
		this.sourceCode = sourceCode;
		this.callStatement = callStatement;
	}
	
	public void analyze() {
		throw Sql4jException.getSql4jException(sourceCode, callStatement.getBeginIndex(), 
				"Call statement is not supported.");
	}

}
