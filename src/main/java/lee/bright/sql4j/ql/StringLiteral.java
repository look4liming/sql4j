package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class StringLiteral implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private String content;
	private JdbcType dataType;
	
	public StringLiteral(int beginIndex, int endIndex, 
			String content) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.content = content;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public String getContent() {
		return content;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
