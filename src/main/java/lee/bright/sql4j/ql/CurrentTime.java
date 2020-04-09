package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class CurrentTime implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private JdbcType dataType;
	
	public CurrentTime(int beginIndex, int endIndex) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
