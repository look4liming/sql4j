package lee.bright.sql4j.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.ql.ColumnDefinition;
import lee.bright.sql4j.ql.DB2Generator;
import lee.bright.sql4j.ql.DataType;
import lee.bright.sql4j.ql.DataTypeEnum;
import lee.bright.sql4j.ql.Generator;
import lee.bright.sql4j.ql.JdbcType;
import lee.bright.sql4j.ql.MySQLGenerator;
import lee.bright.sql4j.ql.Name;
import lee.bright.sql4j.ql.NameChain;
import lee.bright.sql4j.ql.Optimizer;
import lee.bright.sql4j.ql.OracleGenerator;
import lee.bright.sql4j.ql.PostgreSQLGenerator;
import lee.bright.sql4j.ql.SourceCode;
import lee.bright.sql4j.ql.Statement;
import lee.bright.sql4j.ql.TableDefinition;
import lee.bright.sql4j.ql.TableElement;

/**
 * @author Bright Lee
 */
public final class Configuration {

	private static final Logger LOGGER = LoggerFactory.
			getLogger(Configuration.class);
	private static final String CFG_FILE_NAME = "sql4j.cfg";
	
	private boolean dev = false;
	private boolean showSql = true;
	private final ProxyServerConfiguration PROXY_SERVER_CONFIGURATION = 
			new ProxyServerConfiguration();
	private final List<RemoteProxyServerConfiguration> REMOTE_PROXY_SERVER_CONFIGURATION_LIST = 
			new ArrayList<RemoteProxyServerConfiguration>(128);
	private final List<List<DataSourceConfiguration>> DATA_SOURCE_CONFIGURATION_LIST = 
			new ArrayList<List<DataSourceConfiguration>>(4096);
	private final Map<Class<?>, Map<String, List<Statement>>> CLASS_STATMENT_MAP = 
			new HashMap<Class<?>, Map<String, List<Statement>>>(2048);
	private final Map<String, String> HASH_COLUMN_MAP = new HashMap<String, String>(2048);
	private final Map<String, Set<String>> HASH_TABLE_MAP = new HashMap<String, Set<String>>(2048);
	private final List<RemoteProxyServerConfiguration> REMOTE_PROXY_SERVER_CONFIGURATION = 
			new ArrayList<RemoteProxyServerConfiguration>(256);
	private final Map<String, TableMetadata> TABLE_METADATA_MAP = 
			new HashMap<String, TableMetadata>(1024);
	private final List<Class<?>> BEAN_CLASS_LIST = new ArrayList<Class<?>>(2048);
	
	public Configuration() {
		reload();
	}
	
	public void reload() {
		long time = System.currentTimeMillis();
		reset();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(FileUtil.getCfgFileReader(CFG_FILE_NAME), 4096);
			String line = null;
			while ((line = reader.readLine()) != null) {
				processLine(line);
			}
		} catch (Sql4jException e) {
			throw e;
		} catch (Exception e) {
			throw new Sql4jException("Loading configuration file error.", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new Sql4jException("Closing configuration file error.", e);
				}
			}
		}
		completeHashColumnMap();
		initDataSourceConfigurationList();
		loadBeanConfiguration();
		LOGGER.info("Initialization time-consuming: " + (System.currentTimeMillis() - time) + " milliseconds.");
	}
	
	public int getDataSourceListSize() {
		return DATA_SOURCE_CONFIGURATION_LIST.size();
	}
	
	private void initDataSourceConfigurationList() {
		for (int i = 0; i < DATA_SOURCE_CONFIGURATION_LIST.size(); i++) {
			List<DataSourceConfiguration> list = DATA_SOURCE_CONFIGURATION_LIST.get(i);
			if (list == null || list.isEmpty()) {
				throw new Sql4jException(i + ".* data source configuration missing.");
			}
			for (int j = 0; j < list.size(); j++) {
				DataSourceConfiguration config = list.get(j);
				if (config == null) {
					throw new Sql4jException(i + "." + j + " data source configuration missing.");
				}
				config.init();
			}
		}
	}
	
	private void loadBeanConfiguration() {
		for (Class<?> clazz : BEAN_CLASS_LIST) {
			Map<String, List<Statement>> statementListMap = 
					new HashMap<String, List<Statement>>();
			Map<String, SourceCode> map = SourceCodeUtil.getSourceCodeMap(clazz);
			for (Entry<String, SourceCode> entry : map.entrySet()) {
				String sqlName = entry.getKey();
				SourceCode sourceCode = entry.getValue();
				Optimizer optimizer = new Optimizer(this, sourceCode);
				List<Statement> list = new ArrayList<Statement>();
				Statement statement = null;
				while ((statement = optimizer.optimize()) != null) {
					list.add(statement);
				}
				statementListMap.put(sqlName, list);
			}
			CLASS_STATMENT_MAP.put(clazz, statementListMap);
			NameValuePairs.configNameValuePairsClass(clazz);
		}
		BEAN_CLASS_LIST.clear();
	}
	
	private void completeHashColumnMap() {
		List<String> columnNameList = new ArrayList<String>();
		for (String columnName : HASH_COLUMN_MAP.values()) {
			if (!HASH_COLUMN_MAP.containsKey(columnName)) {
				columnNameList.add(columnName);
			}
		}
		for (String columnName : columnNameList) {
			HASH_COLUMN_MAP.put(columnName, null);
		}
	}
	
	public int getColumnType(DataType dataType) throws SQLException {
		DataTypeEnum dataTypeEnum = dataType.getDataTypeEnum();
		if (dataTypeEnum == DataTypeEnum.CHAR) {
			return java.sql.Types.CHAR;
		}
		if (dataTypeEnum == DataTypeEnum.DATE) {
			return java.sql.Types.DATE;
		}
		if (dataTypeEnum == DataTypeEnum.INT) {
			return java.sql.Types.INTEGER;
		}
		if (dataTypeEnum == DataTypeEnum.NCHAR) {
			return java.sql.Types.NCHAR;
		}
		if (dataTypeEnum == DataTypeEnum.NUMERIC) {
			return java.sql.Types.NUMERIC;
		}
		if (dataTypeEnum == DataTypeEnum.REAL) {
			return java.sql.Types.REAL;
		}
		if (dataTypeEnum == DataTypeEnum.SMALLINT) {
			return java.sql.Types.SMALLINT;
		}
		if (dataTypeEnum == DataTypeEnum.TIMESTAMP) {
			return java.sql.Types.TIMESTAMP;
		}
		if (dataTypeEnum == DataTypeEnum.VARCHAR) {
			return java.sql.Types.VARCHAR;
		}
		throw new SQLException(dataTypeEnum + " is not supproted.");
	}
	
	public void addTableDefinition(TableDefinition tableDefinition) throws SQLException {
		if (tableDefinition == null) {
			return;
		}
		List<ColumnMetadata> list = new ArrayList<ColumnMetadata>();
		List<TableElement> tableElementList = tableDefinition.getTableElementList();
		for (int i = 0; i < tableElementList.size(); i++) {
			TableElement tableElement = tableElementList.get(i);
			if (!(tableElement instanceof ColumnDefinition)) {
				continue;
			}
			ColumnDefinition columnDefinition = (ColumnDefinition) tableElement;
			Name columnName = columnDefinition.getColumnName();
			String _columnName = columnName.getContent();
			DataType dataType = columnDefinition.getDataType();
			int columnType = getColumnType(dataType);
			ColumnMetadata columnMetadata = new ColumnMetadata(columnType, _columnName);
			list.add(columnMetadata);
		}
		TableMetadata tableMetadata = new TableMetadata(list);
		String _tableName = tableDefinition.getTableName().toLowerCaseString();
		TABLE_METADATA_MAP.put(_tableName, tableMetadata);
	}
	
	public void addTableName(NameChain tableName) throws SQLException {
		if (tableName == null) {
			return;
		}
		String _tableName = tableName.toLowerCaseString();
		if (_tableName.length() == 0) {
			return;
		}
		if (TABLE_METADATA_MAP.containsKey(_tableName)) {
			return;
		}
		Connection connection = null;
		try {
			connection = getConnection(0);
			java.sql.Statement stmt = null;
			java.sql.ResultSet rset = null;
			try {
				String sql = "SELECT * FROM " + _tableName;
				stmt = connection.createStatement();
				rset = stmt.executeQuery(sql);
				ResultSetMetaData metadata = rset.getMetaData();
				int count = metadata.getColumnCount();
				List<ColumnMetadata> list = 
						new ArrayList<ColumnMetadata>(count);
				StringBuilder buf = new StringBuilder(512);
				for (int i = 0; i < count; i++) {
					int column = i + 1;
					int columnType = metadata.getColumnType(column);
					String columnName = metadata.getColumnName(column);
					ColumnMetadata columnMetadata = 
							new ColumnMetadata(columnType, columnName);
					list.add(columnMetadata);
					JdbcType dataType = JdbcType.getJdbcType(columnType);
					buf.setLength(0);
					buf.append("table:").append(_tableName).append(", ");
					buf.append("column:").append(columnName).append(", ");
					buf.append("type:").append(dataType);
					LOGGER.info(buf.toString());
					buf.setLength(0);
				}
				TableMetadata tableMetadata = new TableMetadata(list);
				TABLE_METADATA_MAP.put(_tableName, tableMetadata);
			} finally {
				close(rset, stmt);
			}
		} finally {
			close(connection);
		}
	}
	
	public TableMetadata getTableMetadata(String tableName) {
		TableMetadata tableMetadata = TABLE_METADATA_MAP.get(tableName);
		return tableMetadata;
	}
	
	private void close(java.sql.ResultSet rset, java.sql.Statement stmt) throws SQLException {
		try {
			if (rset != null) {
				rset.close();
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
	
	private void close(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			throw new Sql4jException(e);
		}
	}
	
	public Connection getConnection(int index) {
		DataSourceConfiguration config = DATA_SOURCE_CONFIGURATION_LIST.get(index).get(0);
		Connection connection;
		try {
			connection = config.getDataSource().getConnection();
		} catch (SQLException e) {
			throw new Sql4jException("Failed to get database connection.", e);
		}
		return connection;
	}
	
	List<Connection> getConnectionList(int index) {
		List<DataSourceConfiguration> dataSourceConfigurationList = 
				DATA_SOURCE_CONFIGURATION_LIST.get(index);
		List<Connection> list = new ArrayList<Connection>(dataSourceConfigurationList.size()); 
		for (int i = 0; i < dataSourceConfigurationList.size(); i++) {
			DataSourceConfiguration dataSourceConfiguration = dataSourceConfigurationList.get(i);
			Connection connection;
			try {
				connection = dataSourceConfiguration.getDataSource().getConnection();
			} catch (SQLException e) {
				throw new Sql4jException("Get connection error.", e);
			}
			list.add(connection);
		}
		return list;
	}
	
	public boolean isDev() {
		return dev;
	}
	
	public boolean isShowSql() {
		return showSql;
	}
	
	public int getProxyPort() {
		return PROXY_SERVER_CONFIGURATION.getPort();
	}
	
	public int getProxyBlockSize() {
		return PROXY_SERVER_CONFIGURATION.getBlockSize();
	}
	
	public List<Statement> getStatementList(Class<?> clazz, String sqlName) {
		Map<String, List<Statement>> map = 
				CLASS_STATMENT_MAP.get(clazz);
		if (map == null) {
			throw new Sql4jException("\"bean=" + clazz.getName() + 
					"\" not in " + FileUtil.getCofigurationFilePath(CFG_FILE_NAME));
		}
		List<Statement> list = map.get(sqlName);
		if (list == null) {
			throw new Sql4jException("--[" + sqlName + "]-- not in " + FileUtil.getSqlFilePath(clazz));
		}
		return list;
	}
	
	private void reset() {
		dev = false;
		showSql = true;
		PROXY_SERVER_CONFIGURATION.clear();
		REMOTE_PROXY_SERVER_CONFIGURATION_LIST.clear();
		closeDataSources();
		DATA_SOURCE_CONFIGURATION_LIST.clear();
		CLASS_STATMENT_MAP.clear();
		HASH_COLUMN_MAP.clear();
		HASH_TABLE_MAP.clear();
		REMOTE_PROXY_SERVER_CONFIGURATION.clear();
		TABLE_METADATA_MAP.clear();
		BEAN_CLASS_LIST.clear();
	}
	
	private void closeDataSources() {
		for (int i = 0; i < DATA_SOURCE_CONFIGURATION_LIST.size(); i++) {
			List<DataSourceConfiguration> list = DATA_SOURCE_CONFIGURATION_LIST.get(i);
			if (list == null || list.isEmpty()) {
				continue;
			}
			for (int j = 0; j < list.size(); j++) {
				DataSourceConfiguration config = list.get(j);
				try {
					config.close();
				} catch (Exception e) {
					LOGGER.error("Close data source error. " + i + "." + j, e);
				}
			}
		}
	}
	
	private void processLine(String line) {
		if (line == null) {
			return;
		}
		line = line.trim();
		if (line.length() == 0) {
			return;
		}
		if (line.startsWith("#")) {
			return;
		}
		int index = line.indexOf('=');
		if (index <= 0) {
			return;
		}
		String name = line.substring(0, index);
		name = trim(name);
		if (name.length() == 0) {
			return;
		}
		String value = line.substring(index + 1);
		value = trim(value);
		if (value.length() == 0) {
			return;
		}
		if ("dev".equalsIgnoreCase(name)) {
			processDev(line, name, value);
			return;
		}
		if ("showSql".equalsIgnoreCase(name)) {
			processShowSql(line, name, value);
			return;
		}
		if ("proxy.port".equalsIgnoreCase(name)) {
			processProxyPort(line, name, value);
			return;
		}
		if ("proxy.blockSize".equalsIgnoreCase(name)) {
			processProxyBlockSize(line, name, value);
			return;
		}
		if ("hash".equalsIgnoreCase(name)) {
			processHash(line, name, value);
			return;
		}
		if ("bean".equalsIgnoreCase(name)) {
			processBean(line, name, value);
		}
		char ch = name.charAt(0);
		if (ch >= '0' && ch <= '9') {
			processIndexableConfig(line, name, value);
			return;
		}
	}
	
	private void processDev(String line, String name, String value) {
		boolean dev;
		try {
			dev = Boolean.parseBoolean(value);
		} catch (Exception e) {
			throw new Sql4jException("Configuration error. 'true' or 'false' expected. " + line, e);
		}
		this.dev = dev;
	}
	
	private void processShowSql(String line, String name, String value) {
		boolean showSql;
		try {
			showSql = Boolean.parseBoolean(value);
		} catch (Exception e) {
			throw new Sql4jException("Configuration error. 'true' or 'false' expected. " + line, e);
		}
		this.showSql = showSql;
	}
	
	private void processProxyPort(String line, String name, String value) {
		int proxyPort;
		try {
			proxyPort = Integer.parseInt(value);
		} catch (Exception e) {
			throw new Sql4jException("Configuration error. Integer expected. " + line, e);
		}
		if (proxyPort < 0 || proxyPort > 65535) {
			throw new Sql4jException("Configuration error. 0 to 65535 expected. " + line);
		}
		PROXY_SERVER_CONFIGURATION.setPort(proxyPort);
	}
	
	private void processProxyBlockSize(String line, String name, String value) {
		int proxyBlockSize;
		try {
			proxyBlockSize = Integer.parseInt(value);
		} catch (Exception e) {
			throw new Sql4jException("Configuration error. Integer expected. " + line, e);
		}
		if (proxyBlockSize < 0 || proxyBlockSize > 2147483647) {
			throw new Sql4jException("Configuration error. 5 to 2147483647 expected. " + line);
		}
		PROXY_SERVER_CONFIGURATION.setBlockSize(proxyBlockSize);
	}
	
	private void processBean(String line, String name, String value) {
		Class<?> clazz;
		try {
			clazz = Class.forName(value);
		} catch (ClassNotFoundException e) {
			throw new Sql4jException("Bean class not found. " + line, e);
		}
		BEAN_CLASS_LIST.add(clazz);
	}
	
	private void processHash(String line, String name, String value) {
		value = value.toLowerCase();
		int index = value.indexOf("->");
		if (index < 0) {
			String columnName = value;
			if (HASH_COLUMN_MAP.containsKey(columnName)) {
				String columnName2 = HASH_COLUMN_MAP.get(columnName);
				if (columnName2 != null) {
					throw new Sql4jException("Duplicate hash column, '" + columnName + "'. " + line);
				}
			} else {
				HASH_COLUMN_MAP.put(columnName, null);
			}
			addToHashTableMap(columnName);
			return;
		}
		int firstIndex = value.indexOf("->");
		int lastIndex = value.lastIndexOf("->");
		if (firstIndex <= 0 || firstIndex > value.length() - 2 || firstIndex != lastIndex) {
			throw new Sql4jException("columnName1->columnName2 expected. " + line);
		}
		String columnName1 = value.substring(0, firstIndex);
		String columnName2 = value.substring(firstIndex + 2);
		if (isColumnName(columnName1) == false || isColumnName(columnName2) == false) {
			throw new Sql4jException("Contains illegal column names. " + line);
		}
		if (HASH_COLUMN_MAP.containsKey(columnName1)) {
			String oldColumnName2 = HASH_COLUMN_MAP.get(columnName1);
			if (!columnName2.equals(oldColumnName2)) {
				throw new Sql4jException("Duplicate hash column, '" + columnName1 + "'. " + line);
			}
			return;
		}
		HASH_COLUMN_MAP.put(columnName1, columnName2);
		addToHashTableMap(columnName1);
		addToHashTableMap(columnName2);
	}
	
	private void addToHashTableMap(String hashColumnName) {
		String tableName = hashColumnName.substring(0, hashColumnName.lastIndexOf('.'));
		Set<String> hashColumnSet = HASH_TABLE_MAP.get(tableName);
		if (hashColumnSet == null) {
			hashColumnSet = new HashSet<String>();
			HASH_TABLE_MAP.put(tableName, hashColumnSet);
		}
		hashColumnSet.add(hashColumnName);
	}
	
	public boolean isTableWithHashColumns(NameChain tableName) {
		String _tableName = tableName.toLowerCaseString();
		boolean b = HASH_TABLE_MAP.containsKey(_tableName);
		if (b == false) {
			return false;
		}
		Set<String> hashColumnSet = HASH_TABLE_MAP.get(_tableName);
		if (hashColumnSet == null || hashColumnSet.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public Set<String> getHashColumnSet(String tableName) {
		Set<String> hashColumnSet = HASH_TABLE_MAP.get(tableName);
		return hashColumnSet;
	}
	
	public boolean isHashColumn(NameChain columnName) {
		String _columnName = columnName.toLowerCaseString();
		return isHashColumn(_columnName);
	}
	
	public boolean isHashColumn(String columnName) {
		columnName = columnName.toLowerCase();
		boolean result = HASH_COLUMN_MAP.containsKey(columnName);
		return result;
	}
	
	public boolean isHashColumnPair(NameChain columnName1, NameChain columnName2) {
		if (isHashColumnMapping(columnName1, columnName2)) {
			return true;
		}
		return isHashColumnMapping(columnName2, columnName1);
	}
	
	private boolean isHashColumnMapping(NameChain columnName1, NameChain columnName2) {
		String _columnName1 = columnName1.toLowerCaseString();
		String _columnName2 = columnName2.toLowerCaseString();
		if (_columnName1.equals(_columnName2)) {
			return true;
		}
		while (true) {
			boolean contains = HASH_COLUMN_MAP.containsKey(_columnName1);
			if (contains == false) {
				return false;
			}
			String columnName = HASH_COLUMN_MAP.get(_columnName1);
			if (columnName == null) {
				return false;
			}
			if (_columnName2.equals(columnName)) {
				return true;
			}
			_columnName1 = columnName;
		}
	}
	
	private void processIndexableConfig(String line, String name, String value) {
		if (name.indexOf('.') < 0) {
			return;
		}
		String[] a = name.split("\\.");
		if (a.length == 2) {
			processRemoteProxyServerConfiguration(line, name, value);
			return;
		}
		if (a.length == 3) {
			processDataSourceConfiguration(line, name, value);
			return;
		}
	}
	
	private void processRemoteProxyServerConfiguration(String line, String name, String value) {
		String[] a = name.split("\\.");
		String _index = a[0];
		long index;
		try {
			index = Long.parseLong(_index);
		} catch (Exception e) {
			return;
		}
		if (index < 0 || index > Integer.MAX_VALUE) {
			throw new Sql4jException("The index must be 0 to 2147483647. " + line);
		}
		String item = a[1];
		if ("ip".equalsIgnoreCase(item)) {
			String ip = value;
			RemoteProxyServerConfiguration config = initRemoteProxyServerConfigurationList((int) index);
			config.setIp(ip);
			return;
		}
		if ("port".equalsIgnoreCase(item)) {
			String _port = value;
			long port;
			try {
				port = Long.parseLong(_port);
			} catch (Exception e) {
				throw new Sql4jException("The port number must be an integer. " + line, e);
			}
			if (port < 0 || port > 65535) {
				throw new Sql4jException("The port number must be 0 to 65535. " + line);
			}
			RemoteProxyServerConfiguration config = initRemoteProxyServerConfigurationList((int) index);
			config.setPort((int) port);
			return;
		}
	}
	
	private RemoteProxyServerConfiguration initRemoteProxyServerConfigurationList(int index) {
		if (index < REMOTE_PROXY_SERVER_CONFIGURATION_LIST.size()) {
			RemoteProxyServerConfiguration config = REMOTE_PROXY_SERVER_CONFIGURATION_LIST.get(index);
			if (config == null) {
				config = new RemoteProxyServerConfiguration();
			}
			config.setIndex(index);
			REMOTE_PROXY_SERVER_CONFIGURATION_LIST.set(index, config);
			return config;
		}
		int count = index - REMOTE_PROXY_SERVER_CONFIGURATION_LIST.size();
		for (int i = 0; i < count; i++) {
			REMOTE_PROXY_SERVER_CONFIGURATION_LIST.add(null);
		}
		RemoteProxyServerConfiguration config = new RemoteProxyServerConfiguration();
		config.setIndex(index);
		REMOTE_PROXY_SERVER_CONFIGURATION_LIST.add(config);
		return config;
	}
	
	private void processDataSourceConfiguration(String line, String name, String value) {
		String[] a = name.split("\\.");
		String _firstIndex = a[0];
		long firstIndex;
		try {
			firstIndex = Long.parseLong(_firstIndex);
		} catch (Exception e) {
			return;
		}
		if (firstIndex < 0 || firstIndex > Integer.MAX_VALUE) {
			throw new Sql4jException("The first index must be 0 to 2147483647. " + line);
		}
		String _secondIndex = a[1];
		long secondIndex;
		try {
			secondIndex = Long.parseLong(_secondIndex);
		} catch (Exception e) {
			return;
		}
		if (secondIndex < 0 || secondIndex > Integer.MAX_VALUE) {
			throw new Sql4jException("The second index must be 0 to 2147483647. " + line);
		}
		String propertyName = a[2];
		DataSourceConfiguration config = initDataSourceConfiguration((int) firstIndex, (int) secondIndex);
		config.setProperties(line, propertyName, value);
	}
	
	private DataSourceConfiguration initDataSourceConfiguration(int firstIndex, int secondIndex) {
		List<DataSourceConfiguration> list;
		if (firstIndex < DATA_SOURCE_CONFIGURATION_LIST.size()) {
			list = DATA_SOURCE_CONFIGURATION_LIST.get(firstIndex);
			if (list == null) {
				list = new ArrayList<DataSourceConfiguration>(5);
			}
			DATA_SOURCE_CONFIGURATION_LIST.set(firstIndex, list);
		} else {
			int count = firstIndex - DATA_SOURCE_CONFIGURATION_LIST.size();
			for (int i = 0; i < count; i++) {
				DATA_SOURCE_CONFIGURATION_LIST.add(null);
			}
			list = new ArrayList<DataSourceConfiguration>(5);
			DATA_SOURCE_CONFIGURATION_LIST.add(list);
		}
		DataSourceConfiguration config;
		if (secondIndex < list.size()) {
			config = list.get(secondIndex);
			if (config == null) {
				config = new DataSourceConfiguration();
			}
			list.set(secondIndex, config);
		} else {
			int count = secondIndex - list.size();
			for (int i = 0; i < count; i++) {
				list.add(null);
			}
			config = new DataSourceConfiguration();
			list.add(config);
		}
		return config;
	}
	
	private boolean isColumnName(String columnName) {
		int index = columnName.indexOf('.');
		if (index < 1 || index > columnName.length() - 2) {
			return false;
		}
		char firstCh = columnName.charAt(0);
		if (!(firstCh >= 'a' && firstCh <= 'z' || 
			  firstCh >= 'A' && firstCh <= 'Z')) {
			return false;
		}
		for (int i = 0; i < columnName.length(); i++) {
			char ch = columnName.charAt(i);
			if (!(ch >= 'a' && ch <= 'z' || 
				  ch >= 'A' && ch <= 'Z' || 
				  ch == '_' || 
				  ch >= '0' && ch <= '9' ||
				  ch == '.')) {
				return false;
			}
		}
		return true;
	}
	
	private String trim(String value) {
		if (value == null || value.length() == 0) {
			return value;
		}
		StringBuilder buf = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			if (Character.isWhitespace(ch)) {
				continue;
			}
			buf.append(ch);
		}
		value = buf.toString();
		return value;
	}
	
	public Generator newGenerator(Class<?> clazz, String sqlName, 
			Object argument) {
		List<Statement> list = getStatementList(clazz, sqlName);
		if (list == null) {
			throw new Sql4jException(clazz.getName() + 
					" --[" + sqlName + "]-- not found.");
		}
		if (list.isEmpty()) {
			throw new Sql4jException(clazz.getName() + 
					" --[" + sqlName + "]-- is empty.");
		}
		Generator generator = null;
		DataSourceConfiguration config = DATA_SOURCE_CONFIGURATION_LIST.get(0).get(0);
		DatabaseType databaseType = config.getDatabaseType();
		if (databaseType == DatabaseType.MYSQL) {
			generator = new MySQLGenerator(this, list, argument);
		} else if (databaseType == DatabaseType.ORACLE) {
			generator = new OracleGenerator(this, list, argument);
		} else if (databaseType == DatabaseType.DB2) {
			generator = new DB2Generator(this, list, argument);
		} else if (databaseType == DatabaseType.POSTGRESQL) {
			generator = new PostgreSQLGenerator(this, list, argument);
		} else if (databaseType == DatabaseType.MARIADB) {
			generator = new MySQLGenerator(this, list, argument);
		} else {
			throw new Sql4jException("Database type '" + 
					databaseType + "' not supported.");
		}
		return generator;
	}
	
	public ColumnMetadata getColumnMetadata(NameChain nameChain) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < nameChain.size() - 1; i++) {
			Name name = nameChain.get(i);
			buf.append(name.getContent());
			if (i < nameChain.size() - 2) {
				buf.append('.');
			}
		}
		String tableName = buf.toString().toLowerCase();
		TableMetadata tableMetadata = TABLE_METADATA_MAP.get(tableName);
		Name column = nameChain.get(nameChain.size() - 1);
		String columnName = column.getContent().toLowerCase();
		ColumnMetadata columnMetadata = tableMetadata.getColumnMetadata(columnName);
		return columnMetadata;
	}
	
	public ColumnMetadata getColumnMetadata(String columnName) {
		String tableName = columnName.substring(0, columnName.lastIndexOf('.')).toString().toLowerCase();
		TableMetadata tableMetadata = TABLE_METADATA_MAP.get(tableName);
		String _columnName = columnName.substring(columnName.lastIndexOf('.') + 1).toLowerCase();
		ColumnMetadata columnMetadata = tableMetadata.getColumnMetadata(_columnName);
		return columnMetadata;
	}
	
	public NameValuePairs newNameValuePairs(Object object) {
		NameValuePairs nameValuePairs = NameValuePairs.newNameValuePairs(object);
		return nameValuePairs;
	}
	
	public int getDataSourceIndex(Object value) {
		if (value == null) {
			return 0;
		}
		String val = String.valueOf(value);
		int hashCode = val.hashCode();
		if (hashCode < 0) {
			hashCode = -hashCode;
		}
		int size = DATA_SOURCE_CONFIGURATION_LIST.size();
		int index = hashCode % size;
		return index;
	}

}
