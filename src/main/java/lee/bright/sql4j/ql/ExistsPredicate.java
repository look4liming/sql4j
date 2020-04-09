package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class ExistsPredicate implements Predicate {
	
	private int beginIndex;
	private int endIndex;
	private Subquery subquery;
	private JdbcType dataType;
	
	public ExistsPredicate(int beginIndex, int endIndex, 
			Subquery subquery) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.subquery = subquery;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public Subquery getSubquery() {
		return subquery;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
