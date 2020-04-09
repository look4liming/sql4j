package lee.bright.sql4j.conf;

import java.lang.reflect.Field;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.DatabaseType;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author Bright Lee
 */
public final class DataSourceConfiguration {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfiguration.class);
	
	private boolean accessToUnderlyingConnectionAllowed;
	private boolean asyncCloseConnectionEnable;
	private boolean asyncInit;
	private boolean breakAfterAcquireFailure;
	private boolean clearFiltersEnable;
	private int connectionErrorRetryAttempts;
	private String connectionProperties;
	private String dbType;
	private boolean defaultAutoCommit;
	private String defaultCatalog;
	private Integer defaultTransactionIsolation;
	private ClassLoader driverClassLoader;
	private String driverClassName;
	private boolean dupCloseLogEnable;
	//private String exceptionSorter;
	//private String exceptionSorterClassName;
	private boolean failFast;
	//private String filters;
	private boolean initExceptionThrow;
	private boolean initGlobalVariants;
	private boolean initVariants;
	private int initialSize;
	private boolean keepAlive;
	private boolean killWhenSocketReadTimeout;
	private boolean logAbandoned;
	private boolean logDifferentThread;
	private int loginTimeout;
	private int maxActive;
	private int maxCreateTaskCount;
	private long maxEvictableIdleTimeMillis;
	//private int maxIdle;
	private int maxOpenPreparedStatements;
	private int maxPoolPreparedStatementPerConnectionSize;
	private long maxWait;
	private int maxWaitThreadCount;
	private long minEvictableIdleTimeMillis;
	private int minIdle;
	private String name;
	private int notFullTimeoutRetryCount;
	//private int numTestsPerEvictionRun;
	private int onFatalErrorMaxActive;
	private String password;
	private long phyMaxUseCount;
	private long phyTimeoutMillis;
	private boolean poolPreparedStatements;
	private int queryTimeout;
	private boolean removeAbandoned;
	private int removeAbandonedTimeout;
	private long removeAbandonedTimeoutMillis;
	private boolean resetStatEnable;
	private boolean sharePreparedStatements;
	//private String statLoggerClassName;
	private boolean testOnBorrow;
	private boolean testOnReturn;
	private boolean testWhileIdle;
	private long timeBetweenConnectErrorMillis;
	private long timeBetweenEvictionRunsMillis;
	private long timeBetweenLogStatsMillis;
	private int transactionQueryTimeout;
	private long transactionThresholdMillis;
	private String url;
	private boolean useGlobalDataSourceStat;
	private boolean useLocalSessionState;
	private boolean useOracleImplicitCache;
	private boolean useUnfairLock;
	private String username;
	//private String validConnectionCheckerClassName;
	//private String validationQuery;
	private int validationQueryTimeout;
	
	private DruidDataSource dataSource;
	
	public DataSourceConfiguration() {
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	
	public DatabaseType getDatabaseType() {
		DatabaseType databaseType = DatabaseType.
				getDatabaseType(driverClassName);
		return databaseType;
	}
	
	public void close() {
		if (dataSource != null) {
			try {
				dataSource.close();
			} catch (Exception e) {
				LOGGER.error("Closing data source error.", e);
			}
		}
	}
	
	void init() {
		dataSource = new DruidDataSource();
		dataSource.setEnable(false);
		dataSource.setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
		dataSource.setAsyncCloseConnectionEnable(asyncCloseConnectionEnable);
		dataSource.setAsyncInit(asyncInit);
		dataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
		dataSource.setClearFiltersEnable(clearFiltersEnable);
		dataSource.setConnectionErrorRetryAttempts(connectionErrorRetryAttempts);
		dataSource.setConnectionProperties(connectionProperties);
		dataSource.setDbType(dbType);
		dataSource.setDefaultAutoCommit(defaultAutoCommit);
		dataSource.setDefaultCatalog(defaultCatalog);
		dataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
		dataSource.setDriverClassLoader(driverClassLoader);
		dataSource.setDriverClassName(driverClassName);
		dataSource.setDupCloseLogEnable(dupCloseLogEnable);
		dataSource.setFailFast(failFast);
		dataSource.setInitExceptionThrow(initExceptionThrow);
		dataSource.setInitGlobalVariants(initGlobalVariants);
		dataSource.setInitVariants(initVariants);
		dataSource.setInitialSize(initialSize);
		dataSource.setKeepAlive(keepAlive);
		dataSource.setKillWhenSocketReadTimeout(killWhenSocketReadTimeout);
		dataSource.setLogAbandoned(logAbandoned);
		dataSource.setLogDifferentThread(logDifferentThread);
		dataSource.setLoginTimeout(loginTimeout);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxCreateTaskCount(maxCreateTaskCount);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		dataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
		dataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		dataSource.setMaxWait(maxWait);
		dataSource.setMaxWaitThreadCount(maxWaitThreadCount);
		dataSource.setMinIdle(minIdle);
		dataSource.setName(name);
		dataSource.setNotFullTimeoutRetryCount(notFullTimeoutRetryCount);
		dataSource.setOnFatalErrorMaxActive(onFatalErrorMaxActive);
		dataSource.setPassword(password);
		dataSource.setPhyMaxUseCount(phyMaxUseCount);
		dataSource.setPhyTimeoutMillis(phyTimeoutMillis);
		dataSource.setPoolPreparedStatements(poolPreparedStatements);
		dataSource.setQueryTimeout(queryTimeout);
		dataSource.setRemoveAbandoned(removeAbandoned);
		dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		dataSource.setRemoveAbandonedTimeoutMillis(removeAbandonedTimeoutMillis);
		dataSource.setResetStatEnable(resetStatEnable);
		dataSource.setSharePreparedStatements(sharePreparedStatements);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setTimeBetweenLogStatsMillis(timeBetweenLogStatsMillis);
		dataSource.setTransactionQueryTimeout(transactionQueryTimeout);
		dataSource.setTransactionThresholdMillis(transactionThresholdMillis);
		dataSource.setUrl(url);
		dataSource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);
		dataSource.setUseLocalSessionState(useLocalSessionState);
		dataSource.setUseOracleImplicitCache(useOracleImplicitCache);
		dataSource.setUseUnfairLock(useUnfairLock);
		dataSource.setUsername(username);
		//dataSource.setValidationQuery(validationQuery);
		DatabaseType databaseType = getDatabaseType();
		if (databaseType == DatabaseType.ORACLE) {
			dataSource.setValidationQuery("SELECT 1 FROM dual");
		} else {
			dataSource.setValidationQuery("SELECT 1");
		}
		dataSource.setValidationQueryTimeout(validationQueryTimeout);
		dataSource.setEnable(true);
		try {
			dataSource.init();
		} catch (SQLException e) {
			throw new Sql4jException("Initialize data source error.", e);
		}
	}
	
	public void setProperties(String line, String name, String value) {
		if ("accessToUnderlyingConnectionAllowed".equals(name)) {
			try {
				accessToUnderlyingConnectionAllowed = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("asyncCloseConnectionEnable".equals(name)) {
			try {
				asyncCloseConnectionEnable = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("asyncInit".equals(name)) {
			try {
				asyncInit = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("breakAfterAcquireFailure".equals(name)) {
			try {
				breakAfterAcquireFailure = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("clearFiltersEnable".equals(name)) {
			try {
				clearFiltersEnable = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("connectionErrorRetryAttempts".equals(name)) {
			try {
				connectionErrorRetryAttempts = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("connectionProperties".equals(name)) {
			try {
				connectionProperties = value;
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("dbType".equals(name)) {
			try {
				dbType = value;
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("defaultAutoCommit".equals(name)) {
			try {
				defaultAutoCommit = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("defaultCatalog".equals(name)) {
			try {
				defaultCatalog = value;
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("defaultTransactionIsolation".equals(name)) {
			try {
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("driverClassLoader".equals(name)) {
			try {
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("driverClassName".equals(name)) {
			try {
				driverClassName = value;
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("dupCloseLogEnable".equals(name)) {
			try {
				dupCloseLogEnable = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("failFast".equals(name)) {
			try {
				failFast = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("initExceptionThrow".equals(name)) {
			try {
				initExceptionThrow = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("initGlobalVariants".equals(name)) {
			try {
				initGlobalVariants = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("initVariants".equals(name)) {
			try {
				initVariants = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("initialSize".equals(name)) {
			try {
				initialSize = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("keepAlive".equals(name)) {
			try {
				keepAlive = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("killWhenSocketReadTimeout".equals(name)) {
			try {
				killWhenSocketReadTimeout = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("logAbandoned".equals(name)) {
			try {
				logAbandoned = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("logDifferentThread".equals(name)) {
			try {
				logDifferentThread = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("loginTimeout".equals(name)) {
			try {
				loginTimeout = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("maxActive".equals(name)) {
			try {
				maxActive = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("maxCreateTaskCount".equals(name)) {
			try {
				maxCreateTaskCount = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("maxEvictableIdleTimeMillis".equals(name)) {
			try {
				maxEvictableIdleTimeMillis = Long.parseLong(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("maxOpenPreparedStatements".equals(name)) {
			try {
				maxOpenPreparedStatements = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("maxPoolPreparedStatementPerConnectionSize".equals(name)) {
			try {
				maxPoolPreparedStatementPerConnectionSize = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("maxWait".equals(name)) {
			try {
				maxWait = Long.parseLong(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("maxWaitThreadCount".equals(name)) {
			try {
				maxWaitThreadCount = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("minEvictableIdleTimeMillis".equals(name)) {
			try {
				minEvictableIdleTimeMillis = Long.parseLong(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("minIdle".equals(name)) {
			try {
				minIdle = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("name".equals(name)) {
			try {
				name = value;
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("notFullTimeoutRetryCount".equals(name)) {
			try {
				notFullTimeoutRetryCount = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("onFatalErrorMaxActive".equals(name)) {
			try {
				onFatalErrorMaxActive = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("password".equals(name)) {
			try {
				password = value;
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("phyMaxUseCount".equals(name)) {
			try {
				phyMaxUseCount = Long.parseLong(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("phyTimeoutMillis".equals(name)) {
			try {
				phyTimeoutMillis = Long.parseLong(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("poolPreparedStatements".equals(name)) {
			try {
				poolPreparedStatements = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("queryTimeout".equals(name)) {
			try {
				queryTimeout = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("removeAbandoned".equals(name)) {
			try {
				removeAbandoned = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("removeAbandonedTimeout".equals(name)) {
			try {
				removeAbandonedTimeout = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("removeAbandonedTimeoutMillis".equals(name)) {
			try {
				removeAbandonedTimeoutMillis = Long.parseLong(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("resetStatEnable".equals(name)) {
			try {
				resetStatEnable = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("sharePreparedStatements".equals(name)) {
			try {
				sharePreparedStatements = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("testOnBorrow".equals(name)) {
			try {
				testOnBorrow = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("testOnReturn".equals(name)) {
			try {
				testOnReturn = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("testWhileIdle".equals(name)) {
			try {
				testWhileIdle = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("timeBetweenConnectErrorMillis".equals(name)) {
			try {
				timeBetweenConnectErrorMillis = Long.parseLong(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("timeBetweenEvictionRunsMillis".equals(name)) {
			try {
				timeBetweenEvictionRunsMillis = Long.parseLong(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("timeBetweenLogStatsMillis".equals(name)) {
			try {
				timeBetweenLogStatsMillis = Long.parseLong(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("transactionQueryTimeout".equals(name)) {
			try {
				transactionQueryTimeout = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("transactionThresholdMillis".equals(name)) {
			try {
				transactionThresholdMillis = Long.parseLong(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("url".equals(name)) {
			try {
				url = value;
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("useGlobalDataSourceStat".equals(name)) {
			try {
				useGlobalDataSourceStat = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("useLocalSessionState".equals(name)) {
			try {
				useLocalSessionState = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("useOracleImplicitCache".equals(name)) {
			try {
				useOracleImplicitCache = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("useUnfairLock".equals(name)) {
			try {
				useUnfairLock = Boolean.parseBoolean(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("username".equals(name)) {
			try {
				username = value;
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
		if ("validationQueryTimeout".equals(name)) {
			try {
				validationQueryTimeout = Integer.parseInt(value);
			} catch (Exception e) {
				throw new Sql4jException("Data source configuration error. " + line, e);
			}
			return;
		}
	}
	
	public static void main(String[] args) {
		Field[] fs = DataSourceConfiguration.class.getDeclaredFields();
		for (Field f : fs) {
			Class<?> clazz = f.getType();
			String name = f.getName();
			System.out.println("if (\""+name+"\".equals(name)) {");
			System.out.println("	try {");
			if (clazz == int.class) {
				System.out.println("		"+name+" = Integer.parseInt(value);");
			} else if (clazz == long.class) {
				System.out.println("		"+name+" = Long.parseLong(value);");
			} else if (clazz == String.class) {
				System.out.println("		"+name+" = value;");
			} else if (clazz == boolean.class) {
				System.out.println("		"+name+" = Boolean.parseBoolean(value);");
			}
			System.out.println("	} catch (Exception e) {");
			System.out.println("		throw new Sql4jException(\"Data source configuration error. \" + line, e);");
			System.out.println("	}");
			System.out.println("	return;");
			System.out.println("}");
		}
	}

}
