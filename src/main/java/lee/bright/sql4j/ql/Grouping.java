package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class Grouping implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private NameChain columnReference;
	private JdbcType dataType;
	
	public Grouping(int beginIndex, int endIndex, NameChain columnReference) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.columnReference = columnReference;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public NameChain getColumnReference() {
		return columnReference;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
