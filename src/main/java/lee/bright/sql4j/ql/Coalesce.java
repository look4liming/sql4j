package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class Coalesce implements ValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private List<ValueExpression> arguments;
	private JdbcType dataType;
	
	public Coalesce(int beginIndex, int endIndex, List<ValueExpression> arguments) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.arguments = arguments;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public List<ValueExpression> getArguments() {
		return arguments;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}
	
}
