package lee.bright.sql4j.conf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bright Lee
 */
public enum DatabaseType {
	
	MYSQL("com.mysql.cj.jdbc.Driver"),
	ORACLE("oracle.jdbc.driver.OracleDriver"),
	POSTGRESQL("org.postgresql.Driver"),
	DB2("com.ibm.db2.jcc.DB2Driver"),
	MARIADB("com.mysql.cj.jdbc.Driver"),;
	
	private String driverClassName;
	
	DatabaseType(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	
	public String getDriverClassName() {
		return driverClassName;
	}
	
	private static Map<String, DatabaseType> DATABASE_TYPE_MAP = 
			new HashMap<String, DatabaseType>();
	static {
		DATABASE_TYPE_MAP.put(MYSQL.getDriverClassName(), MYSQL);
		DATABASE_TYPE_MAP.put(ORACLE.getDriverClassName(), ORACLE);
		DATABASE_TYPE_MAP.put(POSTGRESQL.getDriverClassName(), POSTGRESQL);
		DATABASE_TYPE_MAP.put(DB2.getDriverClassName(), DB2);
		DATABASE_TYPE_MAP.put(MARIADB.getDriverClassName(), MARIADB);
	}
	
	public static DatabaseType getDatabaseType(String driverClassName) {
		return DATABASE_TYPE_MAP.get(driverClassName);
	}

}
