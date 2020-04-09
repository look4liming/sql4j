package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class BooleanTerm implements BooleanValueExpression {
	
	private int beginIndex;
	private int endIndex;
	private List<BooleanFactor> list;
	private JdbcType dataType;
	
	public BooleanTerm(int beginIndex, int endIndex, 
			List<BooleanFactor> list) {
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
	
	public BooleanFactor get(int index) {
		return list.get(index);
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
