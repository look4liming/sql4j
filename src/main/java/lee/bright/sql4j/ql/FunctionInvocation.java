package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class FunctionInvocation implements ValueExpression {
	
	private NameChain functionName;
	private List<ValueExpression> arguments;
	private JdbcType dataType;
	
	public FunctionInvocation(NameChain functionName, 
			List<ValueExpression> arguments) {
		this.functionName = functionName;
		this.arguments = arguments;
	}
	
	public int getBeginIndex() {
		return functionName.get(0).getBeginIndex();
	}
	
	public int getEndIndex() {
		return arguments.get(arguments.size() - 1).getEndIndex();
	}
	
	public NameChain getFunctionName() {
		return functionName;
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
