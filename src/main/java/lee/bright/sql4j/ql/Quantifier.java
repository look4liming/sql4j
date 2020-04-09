package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public enum Quantifier {
	
	ALL("ALL"),
	SOME("SOME"),
	ANY("ANY");
	
	private String content;
	
	Quantifier(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

}
