package lee.bright.sql4j.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Bright Lee
 */
public final class JdbcDataUtil {
	
	public static Object convertToJdbcTypeData(int jdbcType, Object value) throws Exception {
		if (value == null) {
			return null;
		}
		if (jdbcType == java.sql.Types.BIT ||
			jdbcType == java.sql.Types.TINYINT ||
			jdbcType == java.sql.Types.SMALLINT ||
			jdbcType == java.sql.Types.INTEGER ||
			jdbcType == java.sql.Types.BIGINT) {
			if (value instanceof Byte ||
				value instanceof Short ||
				value instanceof Integer ||
				value instanceof Long ||
				value instanceof BigInteger) {
				return value;
			}
			if (value instanceof String) {
				BigInteger i = new BigInteger(value.toString());
				return i;
			}
			if (value instanceof Timestamp) {
				Timestamp timestamp = (Timestamp) value;
				return timestamp.getTime();
			}
			if (value instanceof Date) {
				Date date = (Date) value;
				return date.getTime();
			}
			if (value instanceof Time) {
				Time time = (Time) value;
				return time.getTime();
			}
			throw new Exception("Value cannot be converted to JDBC integer type. '" + 
					value + "', " + jdbcType + ".");
		}
		if (jdbcType == java.sql.Types.FLOAT ||
			jdbcType == java.sql.Types.REAL ||
			jdbcType == java.sql.Types.DOUBLE ||
			jdbcType == java.sql.Types.NUMERIC ||
			jdbcType == java.sql.Types.DECIMAL) {
			if (value instanceof Byte ||
				value instanceof Short ||
				value instanceof Integer ||
				value instanceof Long ||
				value instanceof BigInteger ||
				value instanceof Float ||
				value instanceof Double ||
				value instanceof BigDecimal) {
				return value;
			}
			if (value instanceof String) {
				BigDecimal d = new BigDecimal(value.toString());
				return d;
			}
			if (value instanceof Timestamp) {
				Timestamp timestamp = (Timestamp) value;
				return timestamp.getTime();
			}
			if (value instanceof Date) {
				Date date = (Date) value;
				return date.getTime();
			}
			if (value instanceof Time) {
				Time time = (Time) value;
				return time.getTime();
			}
			throw new Exception("Value cannot be converted to JDBC float type. '" + 
					value + "', " + jdbcType + ".");
		}
		if (jdbcType == java.sql.Types.CHAR ||
			jdbcType == java.sql.Types.VARCHAR ||
			jdbcType == java.sql.Types.LONGVARCHAR ||
			jdbcType == java.sql.Types.NCHAR ||
			jdbcType == java.sql.Types.NVARCHAR ||
			jdbcType == java.sql.Types.LONGNVARCHAR) {
			if (value instanceof String) {
				return value;
			}
			if (value instanceof Byte ||
				value instanceof Short ||
				value instanceof Integer ||
				value instanceof Long ||
				value instanceof BigInteger ||
				value instanceof Float ||
				value instanceof Double ||
				value instanceof BigDecimal) {
				return value.toString();
			}
			if (value instanceof Timestamp) {
				Timestamp timestamp = (Timestamp) value;
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String s = f.format(timestamp);
				return s;
			}
			if (value instanceof Date) {
				Date date = (Date) value;
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String s = f.format(date);
				return s;
			}
			if (value instanceof Time) {
				Time time = (Time) value;
				SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
				String s = f.format(time);
				return s;
			}
			return value.toString();
		}
		if (jdbcType == java.sql.Types.DATE) {
			if (value instanceof Timestamp) {
				Timestamp timestamp = (Timestamp) value;
				return timestamp;
			}
			if (value instanceof Date) {
				Date date = (Date) value;
				return date;
			}
			if (value instanceof String) {
				String s = value.toString();
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = f.parse(s);
				return date;
			}
			if (value instanceof Byte ||
				value instanceof Short ||
				value instanceof Integer ||
				value instanceof Long ||
				value instanceof BigInteger) {
				Number n = (Number) value;
				long l = n.longValue();
				Date date = new Date(l);
				return date;
			}
			if (value instanceof Time) {
				Time time = (Time) value;
				SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
				String s = f.format(time);
				return s;
			}
			throw new Exception("Value cannot be converted to JDBC date type. '" + 
					value + "', " + jdbcType + ".");
		}
		if (jdbcType == java.sql.Types.TIMESTAMP) {
			if (value instanceof Timestamp) {
				return value;
			}
			if (value instanceof Date) {
				Date date = (Date) value;
				Timestamp timestamp = new Timestamp(date.getTime());
				return timestamp;
			}
			if (value instanceof String) {
				String s = value.toString();
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = f.parse(s);
				Timestamp timestamp = new Timestamp(date.getTime());
				return timestamp;
			}
			if (value instanceof Byte ||
				value instanceof Short ||
				value instanceof Integer ||
				value instanceof Long ||
				value instanceof BigInteger) {
				Number n = (Number) value;
				long l = n.longValue();
				Timestamp timestamp = new Timestamp(l);
				return timestamp;
			}
			if (value instanceof Time) {
				Time time = (Time) value;
				Timestamp timestamp = new Timestamp(time.getTime());
				return timestamp;
			}
			throw new Exception("Value cannot be converted to JDBC timestamp type. '" + 
					value + "', " + jdbcType + ".");
		}
		if (jdbcType == java.sql.Types.TIME) {
			if (value instanceof Timestamp) {
				Timestamp timestamp = (Timestamp) value;
				Time time = new Time(timestamp.getTime());
				return time;
			}
			if (value instanceof Date) {
				Date date = (Date) value;
				Time time = new Time(date.getTime());
				return time;
			}
			if (value instanceof String) {
				String s = value.toString();
				SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
				Date date = f.parse(s);
				Time time = new Time(date.getTime());
				return time;
			}
			if (value instanceof Byte ||
				value instanceof Short ||
				value instanceof Integer ||
				value instanceof Long ||
				value instanceof BigInteger) {
				Number n = (Number) value;
				long l = n.longValue();
				Time time = new Time(l);
				return time;
			}
			if (value instanceof Time) {
				Time time = (Time) value;
				return time;
			}
			throw new Exception("Value cannot be converted to JDBC time type. '" + 
					value + "', " + jdbcType + ".");
		}
		throw new Exception("This database type is not supported. " + jdbcType);
	}

}
