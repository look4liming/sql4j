package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class Subquery implements ValueExpression {

	private int beginIndex;
	private int endIndex;
	private SelectStatement selectStatement;
	private JdbcType dataType;
	
	public Subquery(int beginIndex, int endIndex, 
			SelectStatement selectStatement) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.selectStatement = selectStatement;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public SelectStatement getSelectStatement() {
		return selectStatement;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}
	
}
