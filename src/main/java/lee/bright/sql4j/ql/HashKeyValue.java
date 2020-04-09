package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class HashKeyValue {
	
	private NameChain key;
	private ValueExpression value;
	
	public HashKeyValue(NameChain key, ValueExpression value) {
		this.key = key;
		this.value = value;
	}
	
	public NameChain getKey() {
		return key;
	}
	
	public ValueExpression getValue() {
		return value;
	}

}
