package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class SimpleCase implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private ValueExpression caseOperand;
	private List<SimpleWhenClause> list;
	private ElseClause elseClause;
	private JdbcType dataType;
	
	public SimpleCase(int beginIndex, int endIndex, 
			ValueExpression caseOperand, 
			List<SimpleWhenClause> list, 
			ElseClause elseClause) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.caseOperand = caseOperand;
		this.list = list;
		this.elseClause = elseClause;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public ValueExpression getCaseOperand() {
		return caseOperand;
	}
	
	public List<SimpleWhenClause> getSimpleWhenClauseList() {
		return list;
	}
	
	public ElseClause getElseClause() {
		return elseClause;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
