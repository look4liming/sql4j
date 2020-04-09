package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class Distinct {
	
	private int beginIndex;
	private int endIndex;
	
	public Distinct(int beginIndex, int endIndex) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}

}
