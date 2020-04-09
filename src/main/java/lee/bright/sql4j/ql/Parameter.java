package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class Parameter implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private String content;
	private JdbcType dataType;
	
	public Parameter(int beginIndex, int endIndex, 
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
	
	public String toString() {
		return content;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
