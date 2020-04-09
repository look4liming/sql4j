package lee.bright.sql4j.ql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.Sql4jSession;
import lee.bright.sql4j.conf.Configuration;
import lee.bright.sql4j.conf.ConnectionManager;

/**
 * @author Bright Lee
 */
public final class Executor {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
	
	private Configuration configuration;
	//private Sql4jSession session;
	private ConnectionManager connectionManager;
	
	public Executor(Configuration configuration, 
			Sql4jSession session, ConnectionManager connectionManager) {
		this.configuration = configuration;
		//this.session = session;
		this.connectionManager = connectionManager;
	}
	
	public <T> List<T> list(Class<T> clazz, String sqlName, Object argument) {
		Generator generator = configuration.newGenerator(clazz, sqlName, argument);
		SemifinishedStatement semifinishedStatement = null;
		List<T> resultList = new ArrayList<T>();
		while ((semifinishedStatement = generator.generate()) != null) {
			StatementType statementType = semifinishedStatement.getStatementType();
			if (statementType == StatementType.QUERY_SPECIFICATION) {
				String sql = semifinishedStatement.getStatement();
				List<Integer> columnNumberList = semifinishedStatement.getResultNumberList();
				List<String> columnNameList = semifinishedStatement.getResultNameList();
				List<Integer> indexList = semifinishedStatement.getDataSourceIndexList();
				List<T> list;
				if (indexList == null || indexList.isEmpty()) {
					list = connectionManager.list(clazz, sql, columnNumberList, columnNameList);
				} else {
					int index = indexList.get(0);
					list = connectionManager.list(clazz, sql, index, columnNumberList, columnNameList);
				}
				resultList.addAll(list);
			} else {
				executeUpdate(semifinishedStatement);
			}
		}
		return resultList;
	}
	
	public long execute(Class<?> clazz, String sqlName, Object argument) {
		Generator generator = configuration.newGenerator(clazz, sqlName, argument);
		long count = 0;
		SemifinishedStatement semifinishedStatement = null;
		while ((semifinishedStatement = generator.generate()) != null) {
			StatementType statementType = semifinishedStatement.getStatementType();
			if (statementType == StatementType.QUERY_SPECIFICATION) {
				executeQuery(semifinishedStatement);
				continue;
			}
			if (statementType == StatementType.INSERT_STATEMENT ||
				statementType == StatementType.DELETE_STATEMENT ||
				statementType == StatementType.UPDATE_STATEMENT ||
				statementType == StatementType.TABLE_DEFINITION ||
				statementType == StatementType.DROP_TABLE_STATEMENT ||
				statementType == StatementType.DROP_INDEX_STATEMENT ||
				statementType == StatementType.CREATE_INDEX_STATEMENT ||
				statementType == StatementType.ADD_COLUMN_DEFINITION ||
				statementType == StatementType.DROP_COLUMN_DEFINITION ||
				statementType == StatementType.ALTER_COLUMN_DEFINITION ||
				statementType == StatementType.ADD_TABLE_CONSTRAINT_DEFINITION ||
				statementType == StatementType.DROP_TABLE_CONSTRAINT_DEFINITION ||
				statementType == StatementType.DROP_PRIMARY_KEY_DEFINITION ||
				statementType == StatementType.ADD_PRIMARY_KEY_DEFINITION ||
				statementType == StatementType.MODIFY_COLUMN_DEFINITION) {
				count += executeUpdate(semifinishedStatement);
				continue;
			}
			throw new Sql4jException(statementType + " is not supported.");
		}
		return count;
	}
	
	private void executeQuery(SemifinishedStatement semifinishedStatement) {
		String sql = semifinishedStatement.getStatement();
		List<Integer> dataSourceIndexList = semifinishedStatement.getDataSourceIndexList();
		int size = configuration.getDataSourceListSize();
		if (dataSourceIndexList == null) {
			dataSourceIndexList = new ArrayList<Integer>(1);
		}
		if (dataSourceIndexList.isEmpty()) {
			int length = String.valueOf(size).length();
			long random = (long) (Math.random() * Math.pow(10, length));
			if (random < 0) {
				random = -random;
			}
			int index = (int) (random % size);
			dataSourceIndexList.add(index);
		}
		for (int j = 0; j < dataSourceIndexList.size(); j++) {
			int index = dataSourceIndexList.get(j);
			java.sql.Statement st = null;
			java.sql.ResultSet rs = null;
			try {
				connectionManager.executeQuery(sql, index);
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						throw new Sql4jException(e);
					}
				}
				if (st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						throw new Sql4jException(e);
					}
				}
			}
		}
	}
	
	private long executeUpdate(SemifinishedStatement semifinishedStatement) {
		String sql = semifinishedStatement.getStatement();
		List<Integer> dataSourceIndexList = semifinishedStatement.getDataSourceIndexList();
		int size = configuration.getDataSourceListSize();
		if (dataSourceIndexList == null) {
			dataSourceIndexList = new ArrayList<Integer>(size);
		}
		if (dataSourceIndexList.isEmpty()) {
			for (int i = 0; i < size; i++) {
				dataSourceIndexList.add(i);
			}
		}
		String sql1 = null;
		String sql0 = null;
		if (semifinishedStatement.getStatementType() == StatementType.INSERT_STATEMENT) {
			sql1 = sql.replaceAll("===\nhash_foremost_db\n===", "1");
			sql0 = sql.replaceAll("===\nhash_foremost_db\n===", "0");
		}
		long count = 0;
		for (int i = 0; i < dataSourceIndexList.size(); i++) {
			int index = dataSourceIndexList.get(i);
			if (semifinishedStatement.getStatementType() == StatementType.INSERT_STATEMENT) {
				if (i == 0) {
					count += connectionManager.executeUpdate(sql1, index);
				} else {
					count += connectionManager.executeUpdate(sql0, index);
				}
				continue;
			}
			count += connectionManager.executeUpdate(sql, index);
		}
		return count;
	}

}
