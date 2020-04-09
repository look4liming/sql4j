package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class IndexableMessage implements 
		Comparable<IndexableMessage> {
	
	private int index;
	private String message;
	
	public IndexableMessage(int index, 
			String message) {
		this.index = index;
		this.message = message;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int compareTo(IndexableMessage o) {
		if (index < o.index) {
			return -1;
		} else if (index > o.index) {
			return 1;
		} else {
			return 0;
		}
	}

}
