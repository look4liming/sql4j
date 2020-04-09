package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class GrandTotal implements GroupingElement {
	
	private int beginIndex;
	
	public GrandTotal(int beginIndex) {
		this.beginIndex = beginIndex;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}

}
