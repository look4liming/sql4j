package lee.bright.sql4j.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bright Lee
 */
public final class ConnectionManager {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);
	
	private Configuration configuration;
	private Map<Integer, Connection> connMap = new HashMap<Integer, Connection>();
	
	public ConnectionManager(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public long executeUpdate(String sql, int index) {
		Connection connection = getConnection(index);
		long count = connection.executeUpdate(sql);
		return count;
	}
	
	public void executeQuery(String sql, int index) {
		Connection connection = getConnection(index);
		connection.executeQuery(sql);
	}
	
	public <T> List<T> list(Class<T> clazz, String sql, int index, List<Integer> columnNumberList, List<String> columnNameList) {
		List<T> list = new ArrayList<T>();
		Connection connection = getConnection(index);
		List<T> sublist = connection.list(clazz, sql, columnNumberList, columnNameList);
		list.addAll(sublist);
		return list;
	}
	
	public <T> List<T> list(Class<T> clazz, String sql, List<Integer> columnNumberList, List<String> columnNameList) {
		int size = configuration.getDataSourceListSize();
		int length = String.valueOf(size).length();
		long random = (long) (Math.random() * Math.pow(10, length));
		if (random < 0) {
			random = -random;
		}
		int index = (int) (random % size);
		List<T> list = list(clazz, sql, index, columnNumberList, columnNameList);
		return list;
	}
	
	public void rollback() {
		for (Connection connection : connMap.values()) {
			connection.rollback();
		}
	}
	
	public void commit() {
		for (Connection connection : connMap.values()) {
			connection.commit();
		}
	}
	
	public void close() {
		for (Connection connection : connMap.values()) {
			connection.close();
		}
	}
	
	private Connection getConnection(int index) {
		Connection connection = connMap.get(index);
		if (connection == null) {
			connection = new Connection(configuration, index);
			connMap.put(index, connection);
		}
		return connection;
	}

}
