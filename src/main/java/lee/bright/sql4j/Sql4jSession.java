package lee.bright.sql4j;

import java.util.List;

import lee.bright.sql4j.conf.Configuration;
import lee.bright.sql4j.conf.ConnectionManager;
import lee.bright.sql4j.ql.Executor;

/**
 * @author Bright Lee
 */
public final class Sql4jSession {
	
	private Configuration configuration;
	private ConnectionManager connectionManager;
	
	public Sql4jSession(Configuration configuration) {
		this.configuration = configuration;
		this.connectionManager = new ConnectionManager(configuration);
	}
	
	public void rollback() {
		connectionManager.rollback();
	}
	
	public void commit() {
		connectionManager.commit();
	}
	
	public void close() {
		connectionManager.close();
	}
	
	public <T> T get(Class<T> clazz, String sqlName, Object argument) {
		List<T> list = list(clazz, sqlName, argument);
		if (list.isEmpty()) {
			return null;
		}
		if (list.size() > 1) {
			throw new Sql4jException("Fetch too many records. " + list.size());
		}
		T bean = list.get(0);
		return bean;
	}
	
	public <T> T get(Class<T> clazz, String sqlName) {
		T bean = get(clazz, sqlName, null);
		return bean;
	}
	
	public <T> List<T> list(Class<T> clazz, String sqlName, Object argument) {
		Executor executor = new Executor(configuration, this, connectionManager);
		List<T> list = executor.list(clazz, sqlName, argument);
		return list;
	}
	
	public <T> List<T> list(Class<T> clazz, String sqlName) {
		List<T> list = list(clazz, sqlName, null);
		return list;
	}
	
	public long execute(Class<?> clazz, String sqlName, Object argument) {
		Executor executor = new Executor(configuration, this, connectionManager);
		long count = executor.execute(clazz, sqlName, argument);
		return count;
	}
	
	public long execute(Class<?> clazz, String sqlName) {
		long count = execute(clazz, sqlName, null);
		return count;
	}

}
