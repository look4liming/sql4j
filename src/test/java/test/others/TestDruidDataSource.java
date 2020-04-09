package test.others;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.pool.DruidDataSource;

public class TestDruidDataSource {

	public static void main(String[] args) {
		Metadata metadata = new Metadata();
		
		//generateSegment(metadata);
		//generateFields(metadata);
		generateSegment2(metadata);
	}
	
	public static void generateSegment2(Metadata metadata) {
		List<String> fieldNameList = metadata.getFieldNameList();
		List<String> setterNameList = metadata.getSetterNameList();

		StringBuilder buf = new StringBuilder(1024 * 1024);
		for (int i = 0; i < fieldNameList.size(); i++) {
			String fieldName = fieldNameList.get(i);
			String setterName = setterNameList.get(i);
			buf.append("if (\""+fieldName+"\".equalsIgnoreCase(propertyName)) {\n");
			buf.append("	dataSourceInformation."+setterName+"(propertyValue);\n");
			buf.append("	return;\n");
			buf.append("}\n");
		}
		System.out.println(buf);
	}
	
	public static void generateFields(Metadata metadata) {
		List<String> fieldNameList = metadata.getFieldNameList();
		List<Class<?>> typeList = metadata.getTypeList();
		
		StringBuilder buf = new StringBuilder(1024 * 1024);
		for (int i = 0; i < fieldNameList.size(); i++) {
			String fieldName = fieldNameList.get(i);
			Class<?> clazz = typeList.get(i);
			buf.append("private " + clazz.getSimpleName() + " " + fieldName + ";\n");
		}
		System.out.println(buf);
	}

	public static void generateSegment(Metadata metadata) {
		List<String> fieldNameList = metadata.getFieldNameList();
		List<String> setterNameList = metadata.getSetterNameList();

		StringBuilder buf = new StringBuilder(1024 * 1024);
		for (int i = 0; i < fieldNameList.size(); i++) {
			String fieldName = fieldNameList.get(i);
			buf.append("buf.setLength(0);\n");
			buf.append("buf.append(index).append('.').append(i).append('.');\n");
			buf.append("buf.append(\"" + fieldName + "\");\n");
			buf.append("key = buf.toString();\n");
			buf.append("if (map.containsKey(key)) {\n");
			buf.append("	value = map.get(key);\n");
			buf.append("	dataSourceInformation." + setterNameList.get(i) + "(value);\n");
			buf.append("}\n");

		}
		System.out.println(buf);
	}

	private static class Metadata {

		private List<String> fieldNameList;
		private List<String> getterNameList;
		private List<String> setterNameList;
		private List<Class<?>> typeList;

		public Metadata() {
			Method[] ms = DruidDataSource.class.getMethods();
			fieldNameList = new ArrayList<String>(ms.length);
			getterNameList = new ArrayList<String>(ms.length);
			setterNameList = new ArrayList<String>(ms.length);
			typeList = new ArrayList<Class<?>>(ms.length);
			Map<String, Class<?>> typeMap = new HashMap<String, Class<?>>(ms.length);
			for (Method m : ms) {
				// System.out.println(m.getName());
				String methodName = m.getName();
				if ("setValidConnectionChecker".equals(methodName) || "setUserCallback".equals(methodName)
						|| "setStatLogger".equals(methodName) || "setProxyFilters".equals(methodName)
						|| "setPasswordCallbackClassName".equals(methodName) || "setPasswordCallback".equals(methodName)
						|| "setOracle".equals(methodName) || "setObjectName".equals(methodName)
						|| "setLogWriter".equals(methodName) || "setEnable".equals(methodName)
						|| "setDriverClassLoader".equals(methodName) || "setDriver".equals(methodName)
						|| "setDestroyScheduler".equals(methodName) || "setCreateScheduler".equals(methodName)
						|| "setConnectionInitSqls".equals(methodName) || "setConnectProperties".equals(methodName)
						|| "setDefaultReadOnly".equals(methodName)) {
					continue;
				}
				if (!m.getName().startsWith("set")) {
					continue;
				}
				if (m.getParameterTypes().length != 1) {
					continue;
				}
				String fieldName = m.getName().substring(3);
				char firstCh = fieldName.charAt(0);
				fieldName = Character.toLowerCase(firstCh) + fieldName.substring(1);
				typeMap.put(fieldName, m.getParameterTypes()[0]);
				fieldNameList.add(fieldName);
				setterNameList.add(m.getName());
				getterNameList.add("get" + m.getName().substring(3));
			}
			Collections.sort(fieldNameList);
			Collections.sort(getterNameList);
			Collections.sort(setterNameList);
			for (String fieldName : fieldNameList) {
				Class<?> type = typeMap.get(fieldName);
				typeList.add(type);
			}
		}

		public List<String> getFieldNameList() {
			return fieldNameList;
		}

		@SuppressWarnings("unused")
		public List<String> getGetterNameList() {
			return getterNameList;
		}

		public List<String> getSetterNameList() {
			return setterNameList;
		}
		
		public List<Class<?>> getTypeList() {
			return typeList;
		}

	}

}
