package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class SearchedCase implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private List<SearchedWhenClause> list;
	private ElseClause elseClause;
	private JdbcType dataType;
	
	public SearchedCase(int beginIndex, int endIndex, 
			List<SearchedWhenClause> list, 
			ElseClause elseClause) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.list = list;
		this.elseClause = elseClause;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public List<SearchedWhenClause> getSearchedWhenClauseList() {
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
