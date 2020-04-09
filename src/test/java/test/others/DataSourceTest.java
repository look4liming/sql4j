package test.others;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import lee.bright.sql4j.conf.FileUtil;

import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceTest {
	
	public static DataSource getMySQLDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/sql4j?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=TRUE&serverTimezone=GMT");
		dataSource.setUsername("root");
		dataSource.setPassword("123456");
		dataSource.setInitialSize(10);
		dataSource.setMaxActive(10);
		dataSource.setMaxWait(-1);
		dataSource.setDefaultAutoCommit(false);
		
		return dataSource;
	}
	
	public static DataSource getOracleDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");
		dataSource.setUsername("test");
		dataSource.setPassword("123456");
		dataSource.setInitialSize(10);
		dataSource.setMaxActive(10);
		dataSource.setMaxWait(-1);
		dataSource.setDefaultAutoCommit(false);
		
		return dataSource;
	}
	
	public static DataSource getSQLServerDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		
		dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		dataSource.setUrl("jdbc:sqlserver://localhost:1433;DatabaseName=sql4j");
		dataSource.setUsername("sa");
		dataSource.setPassword("123456");
		dataSource.setInitialSize(10);
		dataSource.setMaxActive(10);
		dataSource.setMaxWait(-1);
		dataSource.setDefaultAutoCommit(false);
		
		return dataSource;
	}
	
	public static DataSource getDB2DataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		
		dataSource.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
		dataSource.setUrl("jdbc:db2://localhost:50000/sample");
		dataSource.setUsername("db2admin");
		dataSource.setPassword("123456");
		dataSource.setInitialSize(10);
		dataSource.setMaxActive(10);
		dataSource.setMaxWait(-1);
		dataSource.setDefaultAutoCommit(false);
		
		return dataSource;
	}
	
	public static DataSource getPostgreSQLDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
		dataSource.setUsername("postgres");
		dataSource.setPassword("123456");
		dataSource.setInitialSize(10);
		dataSource.setMaxActive(10);
		dataSource.setMaxWait(-1);
		dataSource.setDefaultAutoCommit(false);
		
		return dataSource;
	}
	
	public static DataSource getSybaseDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		
		dataSource.setDriverClassName("com.sysbase.jdbc.SybDriver");
		dataSource.setUrl("jdbc:sybase:Tds:localhost:5007/myDB");
		dataSource.setUsername("test");
		dataSource.setPassword("123456");
		dataSource.setInitialSize(10);
		dataSource.setMaxActive(10);
		dataSource.setMaxWait(-1);
		dataSource.setDefaultAutoCommit(false);
		
		return dataSource;
	}
	
	public static DataSource getDataSource() {
		Reader reader = null;
		try {
			reader = FileUtil.getCfgFileReader("sql4j.properties");
			Properties props = new Properties();
			props.load(reader);
			String dialect = props.getProperty("dialect");
			DataSource dataSource = null;
			if ("MySQL".equalsIgnoreCase(dialect)) {
				dataSource = getMySQLDataSource();
			} else if ("Oracle".equalsIgnoreCase(dialect)) {
				dataSource = getOracleDataSource();
			} else if ("DB2".equalsIgnoreCase(dialect)) {
				dataSource = getDB2DataSource();
			} else if ("PostgreSQL".equalsIgnoreCase(dialect)) {
				dataSource = getPostgreSQLDataSource();
			} else if ("SQLServer".equalsIgnoreCase(dialect)) {
				dataSource = getSQLServerDataSource();
			} else {
				throw new RuntimeException("Dialect '" + dialect + "' not supported.");
			}
			return dataSource;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		//DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=sql4j", "sa", "123456");
		List<String> list = new ArrayList<String>();
		list.set(999, "");
	}

}
