package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class BooleanValue implements BooleanValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private List<BooleanTerm> list;
	private JdbcType dataType;
	
	public BooleanValue(int beginIndex, int endIndex, 
			List<BooleanTerm> list) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.list = list;
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}
	
	public int size() {
		return list.size();
	}
	
	public BooleanTerm get(int index) {
		return list.get(index);
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
