package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class SimilarPredicate implements Predicate {

	private boolean not;
	private ValueExpression valueExpression;
	private ValueExpression similarPattern;
	private ValueExpression escapeCharacter;
	private JdbcType dataType;
	
	public SimilarPredicate(boolean not, 
			ValueExpression valueExpression, 
			ValueExpression similarPattern, 
			ValueExpression escapeCharacter) {
		this.not = not;
		this.valueExpression = valueExpression;
		this.similarPattern = similarPattern;
		this.escapeCharacter = escapeCharacter;
	}
	
	public int getBeginIndex() {
		return valueExpression.getBeginIndex();
	}
	
	public int getEndIndex() {
		if (escapeCharacter != null) {
			return escapeCharacter.getEndIndex();
		}
		return similarPattern.getEndIndex();
	}
	
	public boolean getNot() {
		return not;
	}
	
	public ValueExpression getValueExpression() {
		return valueExpression;
	}
	
	public ValueExpression getSimilarPattern() {
		return similarPattern;
	}
	
	public ValueExpression getEscapeCharacter() {
		return escapeCharacter;
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}

}
