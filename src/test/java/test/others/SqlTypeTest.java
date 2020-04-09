package test.others;

import java.lang.reflect.Field;

public class SqlTypeTest {

	public static void main(String[] args) {
		System.out.println("int i = 0;");
		Field[] fs = java.sql.Types.class.getDeclaredFields();
		for (Field f : fs) {
			System.out.println("i = java.sql.Types."+f.getName()+";");
			System.out.println("System.out.println(\"java.sql.Types."+f.getName()+"=\"+i);");
		}
		
		
		int i = 0;
		i = java.sql.Types.BIT;
		System.out.println("java.sql.Types.BIT="+i);
		i = java.sql.Types.TINYINT;
		System.out.println("java.sql.Types.TINYINT="+i);
		i = java.sql.Types.SMALLINT;
		System.out.println("java.sql.Types.SMALLINT="+i);
		i = java.sql.Types.INTEGER;
		System.out.println("java.sql.Types.INTEGER="+i);
		i = java.sql.Types.BIGINT;
		System.out.println("java.sql.Types.BIGINT="+i);
		i = java.sql.Types.FLOAT;
		System.out.println("java.sql.Types.FLOAT="+i);
		i = java.sql.Types.REAL;
		System.out.println("java.sql.Types.REAL="+i);
		i = java.sql.Types.DOUBLE;
		System.out.println("java.sql.Types.DOUBLE="+i);
		i = java.sql.Types.NUMERIC;
		System.out.println("java.sql.Types.NUMERIC="+i);
		i = java.sql.Types.DECIMAL;
		System.out.println("java.sql.Types.DECIMAL="+i);
		i = java.sql.Types.CHAR;
		System.out.println("java.sql.Types.CHAR="+i);
		i = java.sql.Types.VARCHAR;
		System.out.println("java.sql.Types.VARCHAR="+i);
		i = java.sql.Types.LONGVARCHAR;
		System.out.println("java.sql.Types.LONGVARCHAR="+i);
		i = java.sql.Types.DATE;
		System.out.println("java.sql.Types.DATE="+i);
		i = java.sql.Types.TIME;
		System.out.println("java.sql.Types.TIME="+i);
		i = java.sql.Types.TIMESTAMP;
		System.out.println("java.sql.Types.TIMESTAMP="+i);
		i = java.sql.Types.BINARY;
		System.out.println("java.sql.Types.BINARY="+i);
		i = java.sql.Types.VARBINARY;
		System.out.println("java.sql.Types.VARBINARY="+i);
		i = java.sql.Types.LONGVARBINARY;
		System.out.println("java.sql.Types.LONGVARBINARY="+i);
		i = java.sql.Types.NULL;
		System.out.println("java.sql.Types.NULL="+i);
		i = java.sql.Types.OTHER;
		System.out.println("java.sql.Types.OTHER="+i);
		i = java.sql.Types.JAVA_OBJECT;
		System.out.println("java.sql.Types.JAVA_OBJECT="+i);
		i = java.sql.Types.DISTINCT;
		System.out.println("java.sql.Types.DISTINCT="+i);
		i = java.sql.Types.STRUCT;
		System.out.println("java.sql.Types.STRUCT="+i);
		i = java.sql.Types.ARRAY;
		System.out.println("java.sql.Types.ARRAY="+i);
		i = java.sql.Types.BLOB;
		System.out.println("java.sql.Types.BLOB="+i);
		i = java.sql.Types.CLOB;
		System.out.println("java.sql.Types.CLOB="+i);
		i = java.sql.Types.REF;
		System.out.println("java.sql.Types.REF="+i);
		i = java.sql.Types.DATALINK;
		System.out.println("java.sql.Types.DATALINK="+i);
		i = java.sql.Types.BOOLEAN;
		System.out.println("java.sql.Types.BOOLEAN="+i);
		i = java.sql.Types.ROWID;
		System.out.println("java.sql.Types.ROWID="+i);
		i = java.sql.Types.NCHAR;
		System.out.println("java.sql.Types.NCHAR="+i);
		i = java.sql.Types.NVARCHAR;
		System.out.println("java.sql.Types.NVARCHAR="+i);
		i = java.sql.Types.LONGNVARCHAR;
		System.out.println("java.sql.Types.LONGNVARCHAR="+i);
		i = java.sql.Types.NCLOB;
		System.out.println("java.sql.Types.NCLOB="+i);
		i = java.sql.Types.SQLXML;
		System.out.println("java.sql.Types.SQLXML="+i);
		i = java.sql.Types.REF_CURSOR;
		System.out.println("java.sql.Types.REF_CURSOR="+i);
		i = java.sql.Types.TIME_WITH_TIMEZONE;
		System.out.println("java.sql.Types.TIME_WITH_TIMEZONE="+i);
		i = java.sql.Types.TIMESTAMP_WITH_TIMEZONE;
		System.out.println("java.sql.Types.TIMESTAMP_WITH_TIMEZONE="+i);
	}

}
