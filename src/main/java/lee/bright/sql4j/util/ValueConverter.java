package lee.bright.sql4j.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import lee.bright.sql4j.Sql4jException;

/**
 * @author Bright Lee
 */
public final class ValueConverter {
	
	private static final String DATE_FORMAT = 
			"yyyy-MM-dd HH:mm:ss";
	
	public static String toString(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Date) {
			Date d = (Date) value;
			SimpleDateFormat f = 
					new SimpleDateFormat(DATE_FORMAT);
			String s = f.format(d);
			return s;
		}
		String s = value.toString();
		return s;
	}
	
	public static Boolean toBooleanObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			return b;
		}
		if (value instanceof String) {
			String s = (String) value;
			if (s.length() == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			if (b == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		if (value instanceof Short) {
			Short s = (Short) value;
			if (s == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		if (value instanceof Integer) {
			Integer i = (Integer) value;
			if (i == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		if (value instanceof Long) {
			Long l = (Long) value;
			if (l == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		if (value instanceof BigInteger) {
			BigInteger i = (BigInteger) value;
			if (i.compareTo(BigInteger.ZERO) == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		if (value instanceof Float) {
			Float f = (Float) value;
			if (f == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		if (value instanceof Double) {
			Double d = (Double) value;
			if (d == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		if (value instanceof BigDecimal) {
			BigDecimal d = (BigDecimal) value;
			if (d.compareTo(BigDecimal.ZERO) == 0) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
		String s = value.toString();
		if (s == null) {
			return null;
		} else if (s.length() == 0) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
	
	public static boolean toBoolean(Object value) {
		Boolean b = toBooleanObject(value);
		if (b == null) {
			return false;
		} else {
			return b;
		}
	}
	
	public static Byte toByteObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			return b;
		}
		if (value instanceof String) {
			String s = (String) value;
			try {
				Byte b = Byte.valueOf(s);
				return b;
			} catch (Exception e) {
				throw new Sql4jException(e);
			}
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				return 1;
			} else {
				return 0;
			}
		}
		throw new Sql4jException("Can't convert " + 
				value.getClass().getSimpleName() + 
				" to Byte.");
	}
	
	public static byte toByte(Object value) {
		Byte b = toByteObject(value);
		if (b == null) {
			return 0;
		} else {
			return b;
		}
	}
	
	public static Short toShortObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Short) {
			Short s = (Short) value;
			return s;
		}
		if (value instanceof String) {
			String s = (String) value;
			try {
				Short s2 = Short.valueOf(s);
				return s2;
			} catch (Exception e) {
				throw new Sql4jException(e);
			}
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				return 1;
			} else {
				return 0;
			}
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			return b.shortValue();
		}
		throw new Sql4jException("Can't convert " + 
				value.getClass().getSimpleName() + 
				" to Short.");
	}
	
	public static short toShort(Object value) {
		Short s = toShortObject(value);
		if (s == null) {
			return 0;
		} else {
			return s;
		}
	}
	
	public static Integer toIntObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Integer) {
			Integer i = (Integer) value;
			return i;
		}
		if (value instanceof String) {
			String s = (String) value;
			try {
				Integer i = Integer.valueOf(s);
				return i;
			} catch (Exception e) {
				throw new Sql4jException(e);
			}
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				return 1;
			} else {
				return 0;
			}
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			return b.intValue();
		}
		if (value instanceof Short) {
			Short s = (Short) value;
			return s.intValue();
		}
		if (value instanceof Integer) {
			Integer i = (Integer) value;
			return i;
		}
		if (value instanceof Long) {
			Long l = (Long) value;
			return l.intValue();
		}
		// TODO
		throw new Sql4jException("Can't convert " + 
				value.getClass().getSimpleName() + 
				" to Integer.");
	}
	
	public static int toInt(Object value) {
		Integer i = toIntObject(value);
		if (i == null) {
			return 0;
		} else {
			return i;
		}
	}
	
	public static Long toLongObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Long) {
			Long l = (Long) value;
			return l;
		}
		if (value instanceof String) {
			String s = (String) value;
			try {
				Long l = Long.valueOf(s);
				return l;
			} catch (Exception e) {
				throw new Sql4jException(e);
			}
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				return 1L;
			} else {
				return 0L;
			}
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			return b.longValue();
		}
		if (value instanceof Short) {
			Short s = (Short) value;
			return s.longValue();
		}
		if (value instanceof Integer) {
			Integer i = (Integer) value;
			return i.longValue();
		}
		if (value instanceof Date) {
			Date d = (Date) value;
			return d.getTime();
		}
		throw new Sql4jException("Can't convert " + 
				value.getClass().getSimpleName() + 
				" to Long.");
	}
	
	public static long toLong(Object value) {
		Long l = toLongObject(value);
		if (l == null) {
			return 0L;
		} else {
			return l;
		}
	}
	
	public static Float toFloatObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Float) {
			Float f = (Float) value;
			return f;
		}
		if (value instanceof String) {
			String s = (String) value;
			try {
				Float f = Float.valueOf(s);
				return f;
			} catch (Exception e) {
				throw new Sql4jException(e);
			}
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				return 1F;
			} else {
				return 0F;
			}
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			return b.floatValue();
		}
		if (value instanceof Short) {
			Short s = (Short) value;
			return s.floatValue();
		}
		throw new Sql4jException("Can't convert " + 
				value.getClass().getSimpleName() + 
				" to Float.");
	}
	
	public static float toFloat(Object value) {
		Float f = toFloatObject(value);
		if (f == null) {
			return 0F;
		} else {
			return f;
		}
	}
	
	public static Double toDoubleObject(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Double) {
			Double d = (Double) value;
			return d;
		}
		if (value instanceof String) {
			String s = (String) value;
			try {
				Double d = Double.valueOf(s);
				return d;
			} catch (Exception e) {
				throw new Sql4jException(e);
			}
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				return 1D;
			} else {
				return 0D;
			}
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			return b.doubleValue();
		}
		if (value instanceof Short) {
			Short s = (Short) value;
			return s.doubleValue();
		}
		if (value instanceof Integer) {
			Integer i = (Integer) value;
			return i.doubleValue();
		}
		if (value instanceof Float) {
			Float f = (Float) value;
			return f.doubleValue();
		}
		throw new Sql4jException("Can't convert " + 
				value.getClass().getSimpleName() + 
				" to Double.");
	}
	
	public static double toDouble(Object value) {
		Double d = toDoubleObject(value);
		if (d == null) {
			return 0D;
		} else {
			return d;
		}
	}
	
	public static BigDecimal toBigDecimal(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof BigDecimal) {
			BigDecimal d = (BigDecimal) value;
			return d;
		}
		if (value instanceof String) {
			String s = (String) value;
			try {
				BigDecimal d = new BigDecimal(s);
				return d;
			} catch (Exception e) {
				throw new Sql4jException(e);
			}
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				return BigDecimal.ONE;
			} else {
				return BigDecimal.ZERO;
			}
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			return BigDecimal.valueOf(b.byteValue());
		}
		if (value instanceof Short) {
			Short s = (Short) value;
			return BigDecimal.valueOf(s.shortValue());
		}
		if (value instanceof Integer) {
			Integer i = (Integer) value;
			return BigDecimal.valueOf(i.intValue());
		}
		if (value instanceof Long) {
			Long l = (Long) value;
			return BigDecimal.valueOf(l.longValue());
		}
		if (value instanceof BigInteger) {
			BigInteger i = (BigInteger) value;
			return new BigDecimal(i.toString());
		}
		if (value instanceof Float) {
			Float f = (Float) value;
			return new BigDecimal(f.toString());
		}
		if (value instanceof Double) {
			Double d = (Double) value;
			return new BigDecimal(d.toString());
		}
		if (value instanceof Date) {
			Date d = (Date) value;
			BigDecimal d2 = BigDecimal.valueOf(d.getTime());
			return d2;
		}
		throw new Sql4jException("Can't convert " + 
				value.getClass().getSimpleName() + 
				" to BigDecimal.");
	}
	
	public static BigInteger toBigInteger(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof BigInteger) {
			BigInteger i = (BigInteger) value;
			return i;
		}
		if (value instanceof String) {
			String s = (String) value;
			try {
				BigInteger i = new BigInteger(s);
				return i;
			} catch (Exception e) {
				throw new Sql4jException(e);
			}
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				return BigInteger.ONE;
			} else {
				return BigInteger.ZERO;
			}
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			return BigInteger.valueOf(b.byteValue());
		}
		if (value instanceof Short) {
			Short s = (Short) value;
			return BigInteger.valueOf(s.shortValue());
		}
		if (value instanceof Integer) {
			Integer i = (Integer) value;
			return BigInteger.valueOf(i.intValue());
		}
		if (value instanceof Long) {
			Long l = (Long) value;
			return BigInteger.valueOf(l.longValue());
		}
		if (value instanceof Date) {
			Date d = (Date) value;
			BigInteger d2 = BigInteger.valueOf(d.getTime());
			return d2;
		}
		throw new Sql4jException("Can't convert " + 
				value.getClass().getSimpleName() + 
				" to BigInteger.");
	}
	
	public static Date toDate(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Date) {
			Date d = (Date) value;
			return d;
		}
		if (value instanceof String) {
			String s = (String) value;
			try {
				SimpleDateFormat f = new SimpleDateFormat(
						DATE_FORMAT);
				Date d = f.parse(s);
				return d;
			} catch (Exception e) {
				throw new Sql4jException(e);
			}
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				Date d = new Date(1);
				return d;
			} else {
				Date d = new Date(0);
				return d;
			}
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			Date d = new Date(b.byteValue());
			return d;
		}
		if (value instanceof Short) {
			Short s = (Short) value;
			Date d = new Date(s.shortValue());
			return d;
		}
		if (value instanceof Integer) {
			Integer i = (Integer) value;
			Date d = new Date(i.intValue());
			return d;
		}
		if (value instanceof Long) {
			Long l = (Long) value;
			Date d = new Date(l.longValue());
			return d;
		}
		throw new Sql4jException("Can't convert " + 
				value.getClass().getSimpleName() + 
				" to Date.");
	}
	
	public static Timestamp toTimestamp(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Timestamp) {
			Timestamp t = (Timestamp) value;
			return t;
		}
		if (value instanceof Date) {
			Date d = (Date) value;
			return new Timestamp(d.getTime());
		}
		if (value instanceof String) {
			String s = (String) value;
			try {
				SimpleDateFormat f = new SimpleDateFormat(
						DATE_FORMAT);
				Date d = f.parse(s);
				return new Timestamp(d.getTime());
			} catch (Exception e) {
				throw new Sql4jException(e);
			}
		}
		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			if (b) {
				Timestamp t = new Timestamp(1);
				return t;
			} else {
				Timestamp t = new Timestamp(0);
				return t;
			}
		}
		if (value instanceof Byte) {
			Byte b = (Byte) value;
			Timestamp t = new Timestamp(b.byteValue());
			return t;
		}
		if (value instanceof Short) {
			Short s = (Short) value;
			Timestamp t = new Timestamp(s.shortValue());
			return t;
		}
		if (value instanceof Integer) {
			Integer i = (Integer) value;
			Timestamp t = new Timestamp(i.intValue());
			return t;
		}
		if (value instanceof Long) {
			Long l = (Long) value;
			Timestamp t = new Timestamp(l.longValue());
			return t;
		}
		throw new Sql4jException("Can't convert " + 
				value.getClass().getSimpleName() + 
				" to Timestamp.");
	}

}
