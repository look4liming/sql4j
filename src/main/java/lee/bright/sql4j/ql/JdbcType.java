package lee.bright.sql4j.ql;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bright Lee
 */
public enum JdbcType {

	_UNKNOWN_TYPE_(Integer.MIN_VALUE, "UNKNOWN_TYPE", null),
	
	ARRAY(Types.ARRAY, "ARRAY", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	BIGINT(Types.BIGINT, "BIGINT", JdbcTypeType.INTEGER),
	BINARY(Types.BINARY, "BINARY", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	BIT(Types.BIT, "BIT", JdbcTypeType.INTEGER),
	BLOB(Types.BLOB, "BLOB", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	BOOLEAN(Types.BOOLEAN, "BOOLEAN", JdbcTypeType.BOOLEAN),
	CHAR(Types.CHAR, "CHAR", JdbcTypeType.STRING),
	CLOB(Types.CLOB, "CLOB", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	DATALINK(Types.DATALINK, "DATALINK", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	DATE(Types.DATE, "DATE", JdbcTypeType.DATE),
	DECIMAL(Types.DECIMAL, "DECIMAL", JdbcTypeType.FLOAT),
	DISTINCT(Types.DISTINCT, "DISTINCT", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	DOUBLE(Types.DOUBLE, "DOUBLE", JdbcTypeType.FLOAT),
	FLOAT(Types.FLOAT, "FLOAT", JdbcTypeType.FLOAT),
	INTEGER(Types.INTEGER, "INTEGER", JdbcTypeType.INTEGER),
	JAVA_OBJECT(Types.JAVA_OBJECT, "JAVA_OBJECT", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	LONGNVARCHAR(Types.LONGNVARCHAR, "LONGNVARCHAR", JdbcTypeType.STRING),
	LONGVARBINARY(Types.LONGVARBINARY, "LONGVARBINARY", JdbcTypeType.BYTE_ARRAY),
	LONGVARCHAR(Types.LONGVARCHAR, "LONGVARCHAR", JdbcTypeType.STRING),
	NCHAR(Types.NCHAR, "NCHAR", JdbcTypeType.STRING),
	NCLOB(Types.NCLOB, "NCLOB", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	NULL(Types.NULL, "NULL", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	NUMERIC(Types.NUMERIC, "NUMERIC", JdbcTypeType.FLOAT),
	NVARCHAR(Types.NVARCHAR, "NVARCHAR", JdbcTypeType.STRING),
	OTHER(Types.OTHER, "OTHER", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	REAL(Types.REAL, "REAL", JdbcTypeType.FLOAT),
	REF(Types.REF, "REF", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	REF_CURSOR(Types.REF_CURSOR, "REF_CURSOR", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	ROWID(Types.ROWID, "ROWID", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	SMALLINT(Types.SMALLINT, "SMALLINT", JdbcTypeType.INTEGER),
	SQLXML(Types.SQLXML, "SQLXML", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	STRUCT(Types.STRUCT, "STRUCT", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	TIME(Types.TIME, "TIME", JdbcTypeType.TIME),
	TIMESTAMP(Types.TIMESTAMP, "TIMESTAMP", JdbcTypeType.TIMESTAMP),
	TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE, "TIMESTAMP_WITH_TIMEZONE", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE, "TIME_WITH_TIMEZONE", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	TINYINT(Types.TINYINT, "TINYINT", JdbcTypeType.INTEGER),
	VARBINARY(Types.VARBINARY, "VARBINARY", JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE),
	VARCHAR(Types.VARCHAR, "VARCHAR", JdbcTypeType.STRING);
	
	private static final Map<String, JdbcType> NAME_JDBC_TYPE_MAP = 
			new HashMap<String, JdbcType>();
	static {
		NAME_JDBC_TYPE_MAP.put(ARRAY.getContent(), ARRAY);
		NAME_JDBC_TYPE_MAP.put(BIGINT.getContent(), BIGINT);
		NAME_JDBC_TYPE_MAP.put(BINARY.getContent(), BINARY);
		NAME_JDBC_TYPE_MAP.put(BIT.getContent(), BIT);
		NAME_JDBC_TYPE_MAP.put(BLOB.getContent(), BLOB);
		NAME_JDBC_TYPE_MAP.put(BOOLEAN.getContent(), BOOLEAN);
		NAME_JDBC_TYPE_MAP.put(CHAR.getContent(), CHAR);
		NAME_JDBC_TYPE_MAP.put(CLOB.getContent(), CLOB);
		NAME_JDBC_TYPE_MAP.put(DATALINK.getContent(), DATALINK);
		NAME_JDBC_TYPE_MAP.put(DATE.getContent(), DATE);
		NAME_JDBC_TYPE_MAP.put(DECIMAL.getContent(), DECIMAL);
		NAME_JDBC_TYPE_MAP.put(DISTINCT.getContent(), DISTINCT);
		NAME_JDBC_TYPE_MAP.put(DOUBLE.getContent(), DOUBLE);
		NAME_JDBC_TYPE_MAP.put(FLOAT.getContent(), FLOAT);
		NAME_JDBC_TYPE_MAP.put(INTEGER.getContent(), INTEGER);
		NAME_JDBC_TYPE_MAP.put(JAVA_OBJECT.getContent(), JAVA_OBJECT);
		NAME_JDBC_TYPE_MAP.put(LONGNVARCHAR.getContent(), LONGNVARCHAR);
		NAME_JDBC_TYPE_MAP.put(LONGVARBINARY.getContent(), LONGVARBINARY);
		NAME_JDBC_TYPE_MAP.put(LONGVARCHAR.getContent(), LONGVARCHAR);
		NAME_JDBC_TYPE_MAP.put(NCHAR.getContent(), NCHAR);
		NAME_JDBC_TYPE_MAP.put(NCLOB.getContent(), NCLOB);
		NAME_JDBC_TYPE_MAP.put(NULL.getContent(), NULL);
		NAME_JDBC_TYPE_MAP.put(NUMERIC.getContent(), NUMERIC);
		NAME_JDBC_TYPE_MAP.put(NVARCHAR.getContent(), NVARCHAR);
		NAME_JDBC_TYPE_MAP.put(OTHER.getContent(), OTHER);
		NAME_JDBC_TYPE_MAP.put(REAL.getContent(), REAL);
		NAME_JDBC_TYPE_MAP.put(REF.getContent(), REF);
		NAME_JDBC_TYPE_MAP.put(REF_CURSOR.getContent(), REF_CURSOR);
		NAME_JDBC_TYPE_MAP.put(ROWID.getContent(), ROWID);
		NAME_JDBC_TYPE_MAP.put(SMALLINT.getContent(), SMALLINT);
		NAME_JDBC_TYPE_MAP.put(SQLXML.getContent(), SQLXML);
		NAME_JDBC_TYPE_MAP.put(STRUCT.getContent(), STRUCT);
		NAME_JDBC_TYPE_MAP.put(TIME.getContent(), TIME);
		NAME_JDBC_TYPE_MAP.put(TIMESTAMP.getContent(), TIMESTAMP);
		NAME_JDBC_TYPE_MAP.put(TIMESTAMP_WITH_TIMEZONE.getContent(), TIMESTAMP_WITH_TIMEZONE);
		NAME_JDBC_TYPE_MAP.put(TIME_WITH_TIMEZONE.getContent(), TIME_WITH_TIMEZONE);
		NAME_JDBC_TYPE_MAP.put(TINYINT.getContent(), TINYINT);
		NAME_JDBC_TYPE_MAP.put(VARBINARY.getContent(), VARBINARY);
		NAME_JDBC_TYPE_MAP.put(VARCHAR.getContent(), VARCHAR);
	}
	private static final Map<Integer, JdbcType> VALUE_JDBC_TYPE_MAP = 
			new HashMap<Integer, JdbcType>();
	static {
		VALUE_JDBC_TYPE_MAP.put(ARRAY.getValue(), ARRAY);
		VALUE_JDBC_TYPE_MAP.put(BIGINT.getValue(), BIGINT);
		VALUE_JDBC_TYPE_MAP.put(BINARY.getValue(), BINARY);
		VALUE_JDBC_TYPE_MAP.put(BIT.getValue(), BIT);
		VALUE_JDBC_TYPE_MAP.put(BLOB.getValue(), BLOB);
		VALUE_JDBC_TYPE_MAP.put(BOOLEAN.getValue(), BOOLEAN);
		VALUE_JDBC_TYPE_MAP.put(CHAR.getValue(), CHAR);
		VALUE_JDBC_TYPE_MAP.put(CLOB.getValue(), CLOB);
		VALUE_JDBC_TYPE_MAP.put(DATALINK.getValue(), DATALINK);
		VALUE_JDBC_TYPE_MAP.put(DATE.getValue(), DATE);
		VALUE_JDBC_TYPE_MAP.put(DECIMAL.getValue(), DECIMAL);
		VALUE_JDBC_TYPE_MAP.put(DISTINCT.getValue(), DISTINCT);
		VALUE_JDBC_TYPE_MAP.put(DOUBLE.getValue(), DOUBLE);
		VALUE_JDBC_TYPE_MAP.put(FLOAT.getValue(), FLOAT);
		VALUE_JDBC_TYPE_MAP.put(INTEGER.getValue(), INTEGER);
		VALUE_JDBC_TYPE_MAP.put(JAVA_OBJECT.getValue(), JAVA_OBJECT);
		VALUE_JDBC_TYPE_MAP.put(LONGNVARCHAR.getValue(), LONGNVARCHAR);
		VALUE_JDBC_TYPE_MAP.put(LONGVARBINARY.getValue(), LONGVARBINARY);
		VALUE_JDBC_TYPE_MAP.put(LONGVARCHAR.getValue(), LONGVARCHAR);
		VALUE_JDBC_TYPE_MAP.put(NCHAR.getValue(), NCHAR);
		VALUE_JDBC_TYPE_MAP.put(NCLOB.getValue(), NCLOB);
		VALUE_JDBC_TYPE_MAP.put(NULL.getValue(), NULL);
		VALUE_JDBC_TYPE_MAP.put(NUMERIC.getValue(), NUMERIC);
		VALUE_JDBC_TYPE_MAP.put(NVARCHAR.getValue(), NVARCHAR);
		VALUE_JDBC_TYPE_MAP.put(OTHER.getValue(), OTHER);
		VALUE_JDBC_TYPE_MAP.put(REAL.getValue(), REAL);
		VALUE_JDBC_TYPE_MAP.put(REF.getValue(), REF);
		VALUE_JDBC_TYPE_MAP.put(REF_CURSOR.getValue(), REF_CURSOR);
		VALUE_JDBC_TYPE_MAP.put(ROWID.getValue(), ROWID);
		VALUE_JDBC_TYPE_MAP.put(SMALLINT.getValue(), SMALLINT);
		VALUE_JDBC_TYPE_MAP.put(SQLXML.getValue(), SQLXML);
		VALUE_JDBC_TYPE_MAP.put(STRUCT.getValue(), STRUCT);
		VALUE_JDBC_TYPE_MAP.put(TIME.getValue(), TIME);
		VALUE_JDBC_TYPE_MAP.put(TIMESTAMP.getValue(), TIMESTAMP);
		VALUE_JDBC_TYPE_MAP.put(TIMESTAMP_WITH_TIMEZONE.getValue(), TIMESTAMP_WITH_TIMEZONE);
		VALUE_JDBC_TYPE_MAP.put(TIME_WITH_TIMEZONE.getValue(), TIME_WITH_TIMEZONE);
		VALUE_JDBC_TYPE_MAP.put(TINYINT.getValue(), TINYINT);
		VALUE_JDBC_TYPE_MAP.put(VARBINARY.getValue(), VARBINARY);
		VALUE_JDBC_TYPE_MAP.put(VARCHAR.getValue(), VARCHAR);
	}
	
	public static JdbcType getJdbcType(String name) {
		if (name == null) {
			return null;
		}
		name = name.trim();
		name = name.toUpperCase();
		JdbcType jdbcType = NAME_JDBC_TYPE_MAP.get(name);
		return jdbcType;
	}
	
	public static JdbcType getJdbcType(int value) {
		JdbcType jdbcType = VALUE_JDBC_TYPE_MAP.get(value);
		return jdbcType;
	}
	
	private final int value;
	private String content;
	private JdbcTypeType jdbcTypeType;
	
	JdbcType(int value, String content, JdbcTypeType jdbcTypeType) {
		this.value = value;
		this.content = content;
		this.jdbcTypeType = jdbcTypeType;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getContent() {
		return content;
	}
	
	public JdbcTypeType getJdbcTypeType() {
		return jdbcTypeType;
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append('{');
		buf.append("content:");
		buf.append(content);
		buf.append(',');
		buf.append(' ');
		buf.append("value:");
		buf.append(value);
		buf.append('}');
		String str = buf.toString();
		return str;
	}
	
	public static void main(String[] args) {
		Class<?> clazz = Types.class;
		Field[] fs = clazz.getFields();
		List<String> list = new ArrayList<String>(fs.length);
		for (Field f : fs) {
			list.add(f.getName());
		}
		Collections.sort(list);
		for (String s : list) {
			System.out.println("VALUE_JDBC_TYPE_MAP.put("+s+".getValue(), "+s+");");
		}
	}

}
