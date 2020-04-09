package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class SetQuantifier {
	
	private int beginIndex;
	private int endIndex;
	private boolean distinct;
	
	public SetQuantifier(int beginIndex, 
			int endIndex, boolean distinct) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.distinct = distinct;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public boolean isDistinct() {
		return distinct;
	}

}
