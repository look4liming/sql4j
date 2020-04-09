package lee.bright.sql4j.conf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lee.bright.sql4j.Sql4jException;

/**
 * @author Bright Lee
 */
public final class Connection {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(Connection.class);
	
	private final Configuration configuration;
	private final int index;
	private final List<java.sql.Connection> list;
	
	public Connection(Configuration configuration, int index) {
		this.configuration = configuration;
		this.index = index;
		this.list = this.configuration.getConnectionList(index);
	}
	
	public long executeUpdate(String sql) {
		long count = 0;
		for (int i = 0; i < list.size(); i++) {
			if (configuration.isShowSql()) {
				LOGGER.info("IDX: {}.{}, SQL: {}", index, i, sql);
			}
			java.sql.Connection connection = list.get(i);
			java.sql.Statement st = null;
			try {
				st = connection.createStatement();
				count += st.executeUpdate(sql);
			} catch (Exception e) {
				throw new Sql4jException("Execute SQL error.", e);
			} finally {
				if (st != null) {
					try {
						st.close();
					} catch (Exception e) {
						throw new Sql4jException("Close statement error.", e);
					}
				}
			}
		}
		return count;
	}
	
	public <T> List<T> list(Class<T> clazz, String sql, List<Integer> columnNumberList, List<String> columnNameList) {
		int size = list.size();
		int length = String.valueOf(size).length();
		long random = (long) (Math.random() * Math.pow(10, length));
		if (random < 0) {
			random = -random;
		}
		int index = (int) (random % size);
		if (configuration.isShowSql()) {
			LOGGER.info("IDX: {}.{}, SQL: {}", this.index, index, sql);
		}
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;
		List<T> resultList = new ArrayList<T>();
		try {
			java.sql.Connection connection = list.get(index);
			st = connection.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				T obj;
				try {
					obj = (T) clazz.newInstance();
				} catch (Exception e) {
					throw new Sql4jException(e);
				}
				NameValuePairs result = configuration.newNameValuePairs(obj);
				for (int i = 0; i < columnNumberList.size(); i++) {
					int num = columnNumberList.get(i);
					String name = columnNameList.get(i);
					Object value = rs.getObject(num);
					result.setValue(name, value);
				}
				resultList.add(obj);
			}
		} catch (SQLException e) {
			throw new Sql4jException(e);
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
		return resultList;
	}
	
	public void executeQuery(String sql) {
		for (int i = 0; i < list.size(); i++) {
			if (configuration.isShowSql()) {
				LOGGER.info("IDX: {}.{}, SQL: {}", index, i, sql);
			}
			java.sql.Connection connection = list.get(i);
			java.sql.Statement st = null;
			java.sql.ResultSet rs = null;
			try {
				st = connection.createStatement();
				rs = st.executeQuery(sql);
				while (rs.next()) {
				}
			} catch (Exception e) {
				throw new Sql4jException("Execute SQL error.", e);
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception e) {
						throw new Sql4jException("Close resultset error.", e);
					}
				}
				if (st != null) {
					try {
						st.close();
					} catch (Exception e) {
						throw new Sql4jException("Close statement error.", e);
					}
				}
			}
		}
	}
	
	public void rollback() {
		List<Exception> elist = new ArrayList<Exception>(list.size());
		for (java.sql.Connection connection : list) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				LOGGER.error("Rollback transaction error.", e);
				elist.add(e);
			}
		}
		if (!elist.isEmpty()) {
			throw new Sql4jException(elist.get(0));
		}
	}
	
	public void commit() {
		List<Exception> elist = new ArrayList<Exception>(list.size());
		for (java.sql.Connection connection : list) {
			try {
				connection.commit();
			} catch (SQLException e) {
				LOGGER.error("Commit transaction error.", e);
				elist.add(e);
			}
		}
		if (!elist.isEmpty()) {
			throw new Sql4jException(elist.get(0));
		}
	}
	
	public void close() {
		List<Exception> elist = new ArrayList<Exception>(list.size());
		for (java.sql.Connection connection : list) {
			try {
				connection.close();
			} catch (SQLException e) {
				LOGGER.error("Close connection error.", e);
				elist.add(e);
			}
		}
		if (!elist.isEmpty()) {
			throw new Sql4jException(elist.get(0));
		}
	}

}
