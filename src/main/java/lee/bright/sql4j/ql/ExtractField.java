package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public enum ExtractField {
	
	YEAR("YEAR"),
	MONTH("MONTH"),
	DAY("DAY"),
	HOUR("HOUR"),
	MINUTE("MINUTE"),
	SECOND("SECOND");
	
	private String content;
	
	private ExtractField(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return content;
	}

}
