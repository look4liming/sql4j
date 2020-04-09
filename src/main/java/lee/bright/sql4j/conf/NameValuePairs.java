package lee.bright.sql4j.conf;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.util.IdGenerator;
import lee.bright.sql4j.util.ValueConverter;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * @author Bright Lee
 */
public abstract class NameValuePairs {
	
	private static final Logger LOGGER = LoggerFactory.
			getLogger(NameValuePairs.class);
	
	private static final Map<Class<?>, Class<? extends NameValuePairs>> NAME_VALUE_PAIRS_CLASSE_MAP = 
			new HashMap<Class<?>, Class<? extends NameValuePairs>>(500);
	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
	
	public static void config(Properties props) {
		long time = System.currentTimeMillis();
		for (Entry<Object, Object> entry : props.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			if (!"bean".equalsIgnoreCase(value)) {
				continue;
			}
			Class<?> clazz;
			try {
				clazz = Class.forName(key);
			} catch (ClassNotFoundException e) {
				throw new Sql4jException(e);
			}
			configNameValuePairsClass(clazz);
		}
		LOGGER.info("Generating proxy classes takes {} milliseconds.", 
				(System.currentTimeMillis() - time));
	}
	
	public static NameValuePairs newNameValuePairs(Object object) {
		if (object == null) {
			final NullNameValuePairs nameValuePairs = 
					new NullNameValuePairs();
			return nameValuePairs;
		}
		if (object instanceof Map) {
			@SuppressWarnings("rawtypes")
			Map map = (Map) object;
			final MapNameValuePairs nameValuePairs = 
					new MapNameValuePairs(map);
			return nameValuePairs;
		}
		Class<?> clazz = object.getClass();
		Class<? extends NameValuePairs> nameValuePairsClass = 
				configNameValuePairsClass(clazz);
		Constructor<? extends NameValuePairs> constructor;
		try {
			constructor = nameValuePairsClass.
					getConstructor(Object.class);
			NameValuePairs nameValuePairs = 
					constructor.newInstance(object);
			return nameValuePairs;
		} catch (Exception e) {
			throw new Sql4jException("", e);
		}
	}
	
	public static Class<? extends NameValuePairs> configNameValuePairsClass(Class<?> clazz) {
		Class<? extends NameValuePairs> nameValuePairsClass;
		try {
			LOCK.readLock().lock();
			nameValuePairsClass = 
					NAME_VALUE_PAIRS_CLASSE_MAP.get(clazz);
		} finally {
			LOCK.readLock().unlock();
		}
		if (nameValuePairsClass == null) {
			try {
				LOCK.writeLock().lock();
				nameValuePairsClass = 
						NAME_VALUE_PAIRS_CLASSE_MAP.get(clazz);
				if (nameValuePairsClass == null) {
					nameValuePairsClass = NAME_VALUE_PAIRS_CLASSE_MAP.get(
							clazz);
					if (nameValuePairsClass == null) {
						nameValuePairsClass = 
								buildNameValuePairsClass(
										clazz);
					}
					NAME_VALUE_PAIRS_CLASSE_MAP.put(clazz, nameValuePairsClass);
				}
			} finally {
				LOCK.writeLock().unlock();
			}
		}
		return nameValuePairsClass;
	}
	
	private static Class<? extends NameValuePairs> buildNameValuePairsClass(
			Class<?> clazz) {
		Map<String, Method> getters = new HashMap<String, Method>();
		Map<String, Method> setters = new HashMap<String, Method>();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			String getterName = getGetterName(method);
			if (getterName != null) {
				Method m = getters.get(getterName);
				if (m != null) {
					String message = "Duplicate getter in type " + 
							clazz.getName() + ", " + method.getName() + 
							"() and " + m.getName() + "().";
					throw new Sql4jException(message);
				}
				getters.put(getterName, method);
			}
			String setterName = getSetterName(method);
			if (setterName != null) {
				Method m = setters.get(setterName);
				if (m != null) {
					String message = "Duplicate setter in type " + 
							clazz.getName() + ", " + method.getName() + 
							"(" + method.getParameterTypes()[0].getSimpleName() + 
							") and " + m.getName() + "(" + 
							m.getParameterTypes()[0].getSimpleName() + ").";
					throw new Sql4jException(message);
				}
				setters.put(setterName, method);
			}
		}
		Class<? extends NameValuePairs> newClass = buildNameValuePairsClass(
				clazz, getters, setters);
		return newClass;
	}
	
	@SuppressWarnings("unchecked")
	private static Class<? extends NameValuePairs> buildNameValuePairsClass(
			Class<?> clazz, Map<String, Method> getters, 
			Map<String, Method> setters) {
		ClassPool classPool = ClassPool.getDefault();
		CtClass parentCtClass = classPool.makeClass(
				NameValuePairs.class.getName());
		CtClass ctClass = classPool.makeClass(
				NameValuePairs.class.getName() + '_' + 
				"proxy_" + IdGenerator.generateUUID());
		try {
			ctClass.setSuperclass(parentCtClass);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		CtField ctField;
		try {
			ctField = CtField.make("private Object object;", ctClass);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		try {
			ctClass.addField(ctField);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		CtConstructor ctConstructor;
		try {
			ctConstructor = new CtConstructor(new CtClass[] {
					classPool.get("java.lang.Object")}, ctClass);
			ctConstructor.setBody("{this.object = $1;}");
		} catch (NotFoundException e) {
			throw new Sql4jException(e);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		try {
			ctClass.addConstructor(ctConstructor);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		CtMethod getObjectCtMethod;
		try {
			getObjectCtMethod = CtMethod.make(
					"public Object getObject() {return this.object;}", 
					ctClass);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		try {
			ctClass.addMethod(getObjectCtMethod);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		CtMethod containsNameCtMethod;
		try {
			String containsNameCtMethodBody = 
					buildContainsNameCtMethodBody(getters);
			containsNameCtMethod = CtMethod.make(
					containsNameCtMethodBody, 
					ctClass);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		try {
			ctClass.addMethod(containsNameCtMethod);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		CtMethod getValueCtMethod;
		try {
			String getValueCtMethodBody = 
					buildGetValueCtMethodBody(clazz, getters);
			getValueCtMethod = CtMethod.make(
					getValueCtMethodBody, 
					ctClass);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		try {
			ctClass.addMethod(getValueCtMethod);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		CtMethod setValueCtMethod;
		try {
			String setValueCtMethodBody = 
					buildSetValueCtMethodBody(clazz, setters);
			setValueCtMethod = CtMethod.make(
					setValueCtMethodBody, 
					ctClass);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		try {
			ctClass.addMethod(setValueCtMethod);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		CtMethod getGetterTypeCtMethod;
		try {
			String getGetterTypeCtMethodBody = 
					buildGetGetterTypeCtMethodBody(clazz, getters);
			getGetterTypeCtMethod = CtMethod.make(
					getGetterTypeCtMethodBody, 
					ctClass);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		try {
			ctClass.addMethod(getGetterTypeCtMethod);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		CtMethod getSetterTypeCtMethod;
		try {
			String getSetterTypeCtMethodBody = 
					buildSetGetterTypeCtMethodBody(clazz, setters);
			getSetterTypeCtMethod = CtMethod.make(
					getSetterTypeCtMethodBody, 
					ctClass);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		try {
			ctClass.addMethod(getSetterTypeCtMethod);
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		Class<? extends NameValuePairs> newClass;
		try {
			newClass = (Class<? extends NameValuePairs>) ctClass.toClass();
		} catch (CannotCompileException e) {
			throw new Sql4jException(e);
		}
		ctClass.detach();
		return newClass;
	}
	
	private static String buildContainsNameCtMethodBody(
			Map<String, Method> getters) {
		StringBuilder buf = new StringBuilder(3000);
		buf.append("public boolean containsName(String name) {");
		for (Map.Entry<String, Method> e : getters.entrySet()) {
			buf.append("if (\""+e.getKey()+"\".equals(name)) {return true;}");
		}
		buf.append("return false;");
		buf.append("}");
		String body = buf.toString();
		return body;
	}
	
	private static String buildGetValueCtMethodBody(
			Class<?> clazz, Map<String, Method> getters) {
		StringBuilder buf = new StringBuilder(3000);
		buf.append("public Object getValue(String name) {");
		buf.append(clazz.getName()).append(" obj = (").append(clazz.getName()).append(") object;");
		for (Map.Entry<String, Method> e : getters.entrySet()) {
			buf.append("if (\""+e.getKey()+"\".equals(name)) {");
			Class<?> returnType = e.getValue().getReturnType();
			if (returnType == boolean.class) {
				buf.append("boolean val = obj.").append(e.getValue().getName()).append("();");
				buf.append("Object value = Boolean.valueOf(val);");
				buf.append("return value;");
			} else if (returnType == char.class) {
				buf.append("char val = obj.").append(e.getValue().getName()).append("();");
				buf.append("Object value = Character.valueOf(val);");
				buf.append("return value;");
			} else if (returnType == byte.class) {
				buf.append("byte val = obj.").append(e.getValue().getName()).append("();");
				buf.append("Object value = Byte.valueOf(val);");
				buf.append("return value;");
			} else if (returnType == short.class) {
				buf.append("short val = obj.").append(e.getValue().getName()).append("();");
				buf.append("Object value = Short.valueOf(val);");
				buf.append("return value;");
			} else if (returnType == int.class) {
				buf.append("int val = obj.").append(e.getValue().getName()).append("();");
				buf.append("Object value = Integer.valueOf(val);");
				buf.append("return value;");
			} else if (returnType == long.class) {
				buf.append("long val = obj.").append(e.getValue().getName()).append("();");
				buf.append("Object value = Long.valueOf(val);");
				buf.append("return value;");
			} else if (returnType == float.class) {
				buf.append("float val = obj.").append(e.getValue().getName()).append("();");
				buf.append("Object value = Float.valueOf(val);");
				buf.append("return value;");
			} else if (returnType == double.class) {
				buf.append("double val = obj.").append(e.getValue().getName()).append("();");
				buf.append("Object value = Double.valueOf(val);");
				buf.append("return value;");
			} else {
				buf.append("Object value = obj.").append(e.getValue().getName()).append("();");
				buf.append("return value;");
			}
			buf.append("}");
		}
		buf.append("return null;");
		buf.append("}");
		String body = buf.toString();
		return body;
	}
	
	private static String buildSetValueCtMethodBody(
			Class<?> clazz, Map<String, Method> setters) {
		StringBuilder buf = new StringBuilder(3000);
		buf.append("public void setValue(String name, Object value) {");
		buf.append(clazz.getName()).append(" obj = (").append(clazz.getName()).append(") object;");
		for (Map.Entry<String, Method> e : setters.entrySet()) {
			buf.append("if (\""+e.getKey()+"\".equals(name)) {");
			Class<?> type = e.getValue().getParameterTypes()[0];
			if (type == String.class) {
				buf.append("String val = ").append(ValueConverter.class.getName()).append(".toString(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == Boolean.class) {
				buf.append("Boolean val = ").append(ValueConverter.class.getName()).append(".toBooleanObject(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == boolean.class) {
				buf.append("boolean val = ").append(ValueConverter.class.getName()).append(".toBoolean(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == Byte.class) {
				buf.append("Byte val = ").append(ValueConverter.class.getName()).append(".toByteObject(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == byte.class) {
				buf.append("byte val = ").append(ValueConverter.class.getName()).append(".toByte(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == Short.class) {
				buf.append("Short val = ").append(ValueConverter.class.getName()).append(".toShortObject(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == short.class) {
				buf.append("short val = ").append(ValueConverter.class.getName()).append(".toShort(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == Integer.class) {
				buf.append("Integer val = ").append(ValueConverter.class.getName()).append(".toIntObject(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == int.class) {
				buf.append("int val = ").append(ValueConverter.class.getName()).append(".toInt(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == Long.class) {
				buf.append("Long val = ").append(ValueConverter.class.getName()).append(".toLongObject(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == long.class) {
				buf.append("long val = ").append(ValueConverter.class.getName()).append(".toLong(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == Float.class) {
				buf.append("Float val = ").append(ValueConverter.class.getName()).append(".toFloatObject(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == float.class) {
				buf.append("float val = ").append(ValueConverter.class.getName()).append(".toFloat(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == Double.class) {
				buf.append("Double val = ").append(ValueConverter.class.getName()).append(".toDoubleObject(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == double.class) {
				buf.append("double val = ").append(ValueConverter.class.getName()).append(".toDouble(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == BigDecimal.class) {
				buf.append("java.math.BigDecimal val = ").append(ValueConverter.class.getName()).append(".toBigDecimal(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == BigInteger.class) {
				buf.append("java.math.BigInteger val = ").append(ValueConverter.class.getName()).append(".toBigInteger(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == Date.class) {
				buf.append("java.util.Date val = ").append(ValueConverter.class.getName()).append(".toDate(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else if (type == Timestamp.class) {
				buf.append("java.sql.Timestamp val = ").append(ValueConverter.class.getName()).append(".toTimestamp(value);");
				buf.append("obj.").append(e.getValue().getName()).append("(val);");
			} else {
			}
			buf.append("}");
		}
		buf.append("}");
		String body = buf.toString();
		return body;
	}
	
	private static String buildGetGetterTypeCtMethodBody(
			Class<?> clazz, Map<String, Method> getters) {
		StringBuilder buf = new StringBuilder(3000);
		buf.append("public Class getGetterType(String name) {");
		buf.append(clazz.getName()).append(" obj = (").append(clazz.getName()).append(") object;");
		for (Map.Entry<String, Method> e : getters.entrySet()) {
			buf.append("if (\""+e.getKey()+"\".equals(name)) {");
			Class<?> type = e.getValue().getReturnType();
			buf.append("return "+type.getName()+".class;");
			buf.append("}");
		}
		buf.append("return null;");
		buf.append("}");
		String body = buf.toString();
		return body;
	}
	
	private static String buildSetGetterTypeCtMethodBody(
			Class<?> clazz, Map<String, Method> setters) {
		StringBuilder buf = new StringBuilder(3000);
		buf.append("public Class getSetterType(String name) {");
		buf.append(clazz.getName()).append(" obj = (").append(clazz.getName()).append(") object;");
		for (Map.Entry<String, Method> e : setters.entrySet()) {
			buf.append("if (\""+e.getKey()+"\".equals(name)) {");
			Class<?> type = e.getValue().getParameterTypes()[0];
			buf.append("return "+type.getName()+".class;");
			buf.append("}");
		}
		buf.append("return null;");
		buf.append("}");
		String body = buf.toString();
		return body;
	}
	
	private static String getGetterName(Method method) {
		if ((method.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC ||
				(method.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
			return null;
		}
		if (method.getParameterTypes().length > 0) {
			return null;
		}
		if (method.getReturnType() == null) {
			return null;
		}
		if (!method.getName().startsWith("is") &&
				!method.getName().startsWith("get")) {
			return null;
		}
		if (method.getName().startsWith("is")) {
			if (method.getReturnType() != boolean.class) {
				return null;
			}
			if (method.getName().length() <= 2) {
				return null;
			}
			String propertyName = getPropertyName(
					method.getName().substring(2));
			return propertyName;
		} else {
			if (method.getName().length() <= 3) {
				return null;
			}
			String propertyName = getPropertyName(
					method.getName().substring(3));
			return propertyName;
		}
	}
	
	private static String getSetterName(Method method) {
		if ((method.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC ||
				(method.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
			return null;
		}
		if (method.getParameterTypes().length != 1) {
			return null;
		}
		if (!method.getName().startsWith("set")) {
			return null;
		}
		if (method.getName().length() <= 3) {
			return null;
		}
		String propertyName = getPropertyName(
				method.getName().substring(3));
		return propertyName;
	}
	
	private static String getPropertyName(String s) {
		if (s.length() <= 0) {
			return null;
		}
		char ch = s.charAt(0);
		if (s.length() == 1) {
			if (Character.isUpperCase(ch)) {
				return s.toLowerCase();
			} else {
				return null;
			}
		} else {
			if (Character.isUpperCase(ch)) {
				return Character.toLowerCase(ch) + s.substring(1);
			} else {
				if (Character.isLowerCase(ch)) {
					return s;
				} else {
					return null;
				}
			}
		}
	}
	
	public abstract Object getObject();
	
	public abstract boolean containsName(String name);
	
	public abstract Object getValue(String name);
	
	public abstract void setValue(String name, Object value);
	
	public abstract Class<?> getGetterType(String name);
	
	public abstract Class<?> getSetterType(String name);

}
