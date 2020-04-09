package lee.bright.sql4j.conf;

/**
 * @author Bright Lee
 */
final class NullNameValuePairs extends NameValuePairs {
	
	NullNameValuePairs() {
	}
	
	@Override
	public Object getObject() {
		return null;
	}

	@Override
	public boolean containsName(String name) {
		return false;
	}

	@Override
	public Object getValue(String name) {
		return null;
	}

	@Override
	public void setValue(String name, Object value) {
	}

	@Override
	public Class<?> getGetterType(String name) {
		return null;
	}

	@Override
	public Class<?> getSetterType(String name) {
		return null;
	}

}
