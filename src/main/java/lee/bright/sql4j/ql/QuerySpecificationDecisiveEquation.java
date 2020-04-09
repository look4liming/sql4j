package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class QuerySpecificationDecisiveEquation {
	
	private NameChain decisiveHashColumn;
	private ValueExpression decisiveHashValue;
	
	public QuerySpecificationDecisiveEquation(NameChain decisiveHashColumn, ValueExpression decisiveHashValue) {
		this.decisiveHashColumn = decisiveHashColumn;
		this.decisiveHashValue = decisiveHashValue;
	}
	
	public NameChain getDecisiveHashColumn() {
		return decisiveHashColumn;
	}
	
	public ValueExpression getDecisiveHashValue() {
		return decisiveHashValue;
	}
	
}