package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class SearchedWhenClause {
	
	private BooleanValueExpression searchedCondition;
	private ValueExpression result;
	
	public SearchedWhenClause(
			BooleanValueExpression searchedCondition, 
			ValueExpression result) {
		this.searchedCondition = searchedCondition;
		this.result = result;
	}
	
	public BooleanValueExpression getSearchedCondition() {
		return searchedCondition;
	}
	
	public ValueExpression getResult() {
		return result;
	}

}
