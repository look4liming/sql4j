package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class CallStatement implements Statement {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private NameChain routineName;
	private List<InOut> inOutList;
	
	public CallStatement(SourceCode sourceCode, 
			int beginIndex, 
			NameChain routineName, 
			List<InOut> inOutList) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.routineName = routineName;
		this.inOutList = inOutList;
	}

	public StatementType getStatementType() {
		return StatementType.CALL_STATEMENT;
	}
	
	public NameChain getRoutineName() {
		return routineName;
	}
	
	public List<InOut> getInOutList() {
		return inOutList;
	}

	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}

}
