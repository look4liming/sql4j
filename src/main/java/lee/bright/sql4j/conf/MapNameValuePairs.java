package lee.bright.sql4j.conf;

import java.util.Map;

/**
 * @author Bright Lee
 */
final class MapNameValuePairs extends NameValuePairs {
	
	private Map<String, Object> object;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	MapNameValuePairs(Map map) {
		object = (Map<String, Object>) map;
	}

	@Override
	public Object getObject() {
		return object;
	}

	@Override
	public boolean containsName(String name) {
		return object.containsKey(name);
	}

	@Override
	public Object getValue(String name) {
		return object.get(name);
	}

	@Override
	public void setValue(String name, Object value) {
		object.put(name, value);
	}

	@Override
	public Class<?> getGetterType(String name) {
		if (containsName(name)) {
			return null;
		}
		Object value = getValue(name);
		if (value == null) {
			return null;
		}
		return value.getClass();
	}

	@Override
	public Class<?> getSetterType(String name) {
		return Object.class;
	}

}
