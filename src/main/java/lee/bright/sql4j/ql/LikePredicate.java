package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class LikePredicate implements Predicate {
	
	private boolean not;
	private ValueExpression valueExpression;
	private ValueExpression characterPattern;
	private ValueExpression escapeCharacter;
	private JdbcType dataType;
	
	public LikePredicate(boolean not, 
			ValueExpression valueExpression, 
			ValueExpression characterPattern, 
			ValueExpression escapeCharacter) {
		this.not = not;
		this.valueExpression = valueExpression;
		this.characterPattern = characterPattern;
		this.escapeCharacter = escapeCharacter;
	}
	
	public int getBeginIndex() {
		return valueExpression.getBeginIndex();
	}
	
	public int getEndIndex() {
		if (escapeCharacter != null) {
			return escapeCharacter.getEndIndex();
		}
		return characterPattern.getEndIndex();
	}
	
	public boolean getNot() {
		return not;
	}
	
	public ValueExpression getValueExpression() {
		return valueExpression;
	}
	
	public ValueExpression getCharacterPattern() {
		return characterPattern;
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
