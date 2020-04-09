package lee.bright.sql4j.util;

/**
 * @author Bright Lee
 */
public final class SqlStringLiteralUtil {
	
	public static String toMySQLString(String javaString) {
		if (javaString == null) {
			throw new NullPointerException();
		}
		StringBuilder buf = new StringBuilder(
				javaString.length() + 2);
		buf.append('\'');
		for (int i = 0; i < javaString.length(); i++) {
			char ch = javaString.charAt(i);
			if (ch == '\'') {
				buf.append('\\').append('\'');
				continue;
			}
			if (ch == '\n') {
				buf.append('\\').append('n');
				continue;
			}
			if (ch == '\r') {
				buf.append('\\').append('r');
				continue;
			}
			if (ch == '\\') {
				buf.append('\\').append('\\');
				continue;
			}
			if (ch == '\t') {
				buf.append('\\').append('t');
				continue;
			}
			buf.append(ch);
		}
		buf.append('\'');
		String sqlString = buf.toString();
		buf.setLength(0);
		buf = null;
		return sqlString;
	}
	
	public static String toOracleString(String javaString) {
		if (javaString == null) {
			throw new NullPointerException();
		}
		StringBuilder buf = new StringBuilder(
				javaString.length() + 2);
		for (int i = 0; i < javaString.length(); i++) {
			char ch = javaString.charAt(i);
			if (i == 0) {
				if (ch == '\b' || 
					ch == '\n' || 
					ch == '\f' || 
					ch == '\r' || 
					ch == '\\') {
					buf.append('c');
					buf.append('h');
					buf.append('r');
					buf.append('(');
					buf.append((int) ch);
					buf.append(')');
				} else if (ch == '\'') {
					buf.append('\'');
					buf.append('\'').append('\'');
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				} else {
					buf.append('\'');
					buf.append(ch);
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				}
			} else {
				if (ch == '\b' || 
					ch == '\n' || 
					ch == '\f' || 
					ch == '\r' || 
					ch == '\\') {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar != '\b' && 
						beforeChar != '\n' && 
						beforeChar != '\f' && 
						beforeChar != '\r' && 
						beforeChar != '\\') {
						buf.append('\'');
					}
					buf.append('|');
					buf.append('|');
					buf.append('c');
					buf.append('h');
					buf.append('r');
					buf.append('(');
					buf.append((int) ch);
					buf.append(')');
				} else if (ch == '\'') {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar == '\b' || 
						beforeChar == '\n' || 
						beforeChar == '\f' || 
						beforeChar == '\r' || 
						beforeChar == '\\') {
						buf.append('|');
						buf.append('|');
						buf.append('\'');
					}
					buf.append('\'').append('\'');
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				} else {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar == '\b' || 
						beforeChar == '\n' || 
						beforeChar == '\f' || 
						beforeChar == '\r' || 
						beforeChar == '\\') {
						buf.append('|');
						buf.append('|');
						buf.append('\'');
					}
					buf.append(ch);
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				}
			}
		}
		if (buf.length() == 0) {
			buf.append('\'').append('\'');
		}
		String sqlString = buf.toString();
		buf.setLength(0);
		buf = null;
		return sqlString;
	}
	
	public static String toSQLServerString(String javaString) {
		if (javaString == null) {
			throw new NullPointerException();
		}
		StringBuilder buf = new StringBuilder(
				javaString.length() + 2);
		for (int i = 0; i < javaString.length(); i++) {
			char ch = javaString.charAt(i);
			if (i == 0) {
				if (ch == '\b' || 
					ch == '\n' || 
					ch == '\f' || 
					ch == '\r' || 
					ch == '\\') {
					buf.append('c');
					buf.append('h');
					buf.append('r');
					buf.append('(');
					buf.append((int) ch);
					buf.append(')');
				} else if (ch == '\'') {
					buf.append('\'');
					buf.append('\'').append('\'');
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				} else {
					buf.append('\'');
					buf.append(ch);
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				}
			} else {
				if (ch == '\b' || 
					ch == '\n' || 
					ch == '\f' || 
					ch == '\r' || 
					ch == '\\') {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar != '\b' && 
						beforeChar != '\n' && 
						beforeChar != '\f' && 
						beforeChar != '\r' && 
						beforeChar != '\\') {
						buf.append('\'');
					}
					buf.append('|');
					buf.append('|');
					buf.append('c');
					buf.append('h');
					buf.append('r');
					buf.append('(');
					buf.append((int) ch);
					buf.append(')');
				} else if (ch == '\'') {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar == '\b' || 
						beforeChar == '\n' || 
						beforeChar == '\f' || 
						beforeChar == '\r' || 
						beforeChar == '\\') {
						buf.append('|');
						buf.append('|');
						buf.append('\'');
					}
					buf.append('\'').append('\'');
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				} else {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar == '\b' || 
						beforeChar == '\n' || 
						beforeChar == '\f' || 
						beforeChar == '\r' || 
						beforeChar == '\\') {
						buf.append('|');
						buf.append('|');
						buf.append('\'');
					}
					buf.append(ch);
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				}
			}
		}
		if (buf.length() == 0) {
			buf.append('\'').append('\'');
		}
		String sqlString = buf.toString();
		buf.setLength(0);
		buf = null;
		return sqlString;
		// TODO 尚未实现
	}
	
	public static String toPostgreSQLString(String javaString) {
		if (javaString == null) {
			throw new NullPointerException();
		}
		StringBuilder buf = new StringBuilder(
				javaString.length() + 2);
		for (int i = 0; i < javaString.length(); i++) {
			char ch = javaString.charAt(i);
			if (i == 0) {
				if (ch == '\b' || 
					ch == '\n' || 
					ch == '\f' || 
					ch == '\r' || 
					ch == '\\') {
					buf.append('c');
					buf.append('h');
					buf.append('r');
					buf.append('(');
					buf.append((int) ch);
					buf.append(')');
				} else if (ch == '\'') {
					buf.append('\'');
					buf.append('\'').append('\'');
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				} else {
					buf.append('\'');
					buf.append(ch);
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				}
			} else {
				if (ch == '\b' || 
					ch == '\n' || 
					ch == '\f' || 
					ch == '\r' || 
					ch == '\\') {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar != '\b' && 
						beforeChar != '\n' && 
						beforeChar != '\f' && 
						beforeChar != '\r' && 
						beforeChar != '\\') {
						buf.append('\'');
					}
					buf.append('|');
					buf.append('|');
					buf.append('c');
					buf.append('h');
					buf.append('r');
					buf.append('(');
					buf.append((int) ch);
					buf.append(')');
				} else if (ch == '\'') {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar == '\b' || 
						beforeChar == '\n' || 
						beforeChar == '\f' || 
						beforeChar == '\r' || 
						beforeChar == '\\') {
						buf.append('|');
						buf.append('|');
						buf.append('\'');
					}
					buf.append('\'').append('\'');
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				} else {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar == '\b' || 
						beforeChar == '\n' || 
						beforeChar == '\f' || 
						beforeChar == '\r' || 
						beforeChar == '\\') {
						buf.append('|');
						buf.append('|');
						buf.append('\'');
					}
					buf.append(ch);
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				}
			}
		}
		if (buf.length() == 0) {
			buf.append('\'').append('\'');
		}
		String sqlString = buf.toString();
		buf.setLength(0);
		buf = null;
		return sqlString;
		// TODO 尚未实现
	}
	
	public static String toDB2String(String javaString) {
		if (javaString == null) {
			throw new NullPointerException();
		}
		StringBuilder buf = new StringBuilder(
				javaString.length() + 2);
		for (int i = 0; i < javaString.length(); i++) {
			char ch = javaString.charAt(i);
			if (i == 0) {
				if (ch == '\b' || 
					ch == '\n' || 
					ch == '\f' || 
					ch == '\r' || 
					ch == '\\') {
					buf.append('c');
					buf.append('h');
					buf.append('r');
					buf.append('(');
					buf.append((int) ch);
					buf.append(')');
				} else if (ch == '\'') {
					buf.append('\'');
					buf.append('\'').append('\'');
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				} else {
					buf.append('\'');
					buf.append(ch);
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				}
			} else {
				if (ch == '\b' || 
					ch == '\n' || 
					ch == '\f' || 
					ch == '\r' || 
					ch == '\\') {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar != '\b' && 
						beforeChar != '\n' && 
						beforeChar != '\f' && 
						beforeChar != '\r' && 
						beforeChar != '\\') {
						buf.append('\'');
					}
					buf.append('|');
					buf.append('|');
					buf.append('c');
					buf.append('h');
					buf.append('r');
					buf.append('(');
					buf.append((int) ch);
					buf.append(')');
				} else if (ch == '\'') {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar == '\b' || 
						beforeChar == '\n' || 
						beforeChar == '\f' || 
						beforeChar == '\r' || 
						beforeChar == '\\') {
						buf.append('|');
						buf.append('|');
						buf.append('\'');
					}
					buf.append('\'').append('\'');
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				} else {
					char beforeChar = javaString.charAt(i - 1);
					if (beforeChar == '\b' || 
						beforeChar == '\n' || 
						beforeChar == '\f' || 
						beforeChar == '\r' || 
						beforeChar == '\\') {
						buf.append('|');
						buf.append('|');
						buf.append('\'');
					}
					buf.append(ch);
					if (i == javaString.length() - 1) {
						buf.append('\'');
					}
				}
			}
		}
		if (buf.length() == 0) {
			buf.append('\'').append('\'');
		}
		String sqlString = buf.toString();
		buf.setLength(0);
		buf = null;
		return sqlString;
		// TODO 尚未实现
	}
	
	public static void main(String[] args) {
		String s = "\t'\na\rabc\r";
		String sql = toOracleString(s);
		System.out.println(sql);
	}

}
