package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class DataType {
	
	private SourceCode sourceCode;
	private int beginIndex;
	private DataTypeEnum dataTypeEnum;
	private int length;
	private int precision;
	private int scale;
	
	public DataType(SourceCode sourceCode, int beginIndex, DataTypeEnum dataTypeEnum, 
			int precision, int scale) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.dataTypeEnum = dataTypeEnum;
		this.length = -1;
		this.precision = precision;
		this.scale = scale;
	}
	
	public DataType(SourceCode sourceCode, int beginIndex, DataTypeEnum dataTypeEnum, 
			int length) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.dataTypeEnum = dataTypeEnum;
		this.length = length;
		this.precision = -1;
		this.scale = -1;
	}
	
	public DataType(SourceCode sourceCode, int beginIndex, DataTypeEnum dataTypeEnum) {
		this(sourceCode, beginIndex, dataTypeEnum, -1, -1);
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public DataTypeEnum getDataTypeEnum() {
		return dataTypeEnum;
	}
	
	public int getLength() {
		return length;
	}
	
	public int getPrecision() {
		return precision;
	}
	
	public int getScale() {
		return scale;
	}

}
