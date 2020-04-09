package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class InOut {
	
	private JdbcType jdbcType;
	private int inBeginIndex;
	private int inEndIndex;
	private String in;
	private int outBeginIndex;
	private int outEndIndex;
	private String out;
	
	public InOut(JdbcType jdbcType, 
			int inBeginIndex, int inEndIndex, 
			String in, int outBeginIndex, 
			int outEndIndex, String out) {
		this.jdbcType = jdbcType;
		this.inBeginIndex = inBeginIndex;
		this.inEndIndex = inEndIndex;
		this.in = in;
		this.outBeginIndex = outBeginIndex;
		this.outEndIndex = outEndIndex;
		this.out = out;
	}
	
	public JdbcType getJdbcType() {
		return jdbcType;
	}
	
	public int getInBeginIndex() {
		return inBeginIndex;
	}
	
	public int getInEndIndex() {
		return inEndIndex;
	}
	
	public String getIn() {
		return in;
	}
	
	public int getOutBeginIndex() {
		return outBeginIndex;
	}
	
	public int getOutEndIndex() {
		return outEndIndex;
	}
	
	public String getOut() {
		return out;
	}

}
