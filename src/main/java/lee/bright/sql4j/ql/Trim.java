package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class Trim implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private TrimSpecification trimSpecification;
	private ValueExpression trimCharacter;
	private ValueExpression trimSource;
	private JdbcType dataType;
	
	public Trim(int beginIndex, int endIndex, 
			TrimSpecification trimSpecification, 
			ValueExpression trimCharacter, 
			ValueExpression trimSource) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.trimSpecification = trimSpecification;
		this.trimCharacter = trimCharacter;
		this.trimSource = trimSource;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public TrimSpecification getTrimSpecification() {
		return trimSpecification;
	}
	
	public ValueExpression getTrimCharacter() {
		return trimCharacter;
	}
	
	public ValueExpression getTrimSource() {
		return trimSource;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
