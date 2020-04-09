package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ExtractExpression implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private ExtractField extractField;
	private ValueExpression extractSource;
	private JdbcType dataType;
	
	public ExtractExpression(int beginIndex, 
			int endIndex, ExtractField extractField, 
			ValueExpression extractSource) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.extractField = extractField;
		this.extractSource = extractSource;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public ExtractField getExtractField() {
		return extractField;
	}
	
	public ValueExpression getExtractSource() {
		return extractSource;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
