package test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lee.bright.sql4j.Sql4jSession;
import lee.bright.sql4j.Sql4jSessionFactory;

public final class Test {
	
	private static final Logger LOGGER = LoggerFactory.
			getLogger(Test.class);
	
	public static void main(String[] args) {
		final Sql4jSessionFactory factory = new Sql4jSessionFactory();
		/*for (int i = 0; i < 1; i++) {
			new Thread() {
				public void run() {
					doSomethingToDatabase(factory);
				}
			}.start();
		}*/
		doSomethingToDatabase(factory);
	}
	
	private static void doSomethingToDatabase(Sql4jSessionFactory factory) {
		Sql4jSession session = factory.newSession();
		
		long time = -1;
		long millis = -1;
		
		/*LOGGER.info("数据库初始化--[init_all]--：");
		time = System.currentTimeMillis();
		session.execute(Init.class, "init_all");
		LOGGER.info("耗时（毫秒）：" + (System.currentTimeMillis() - time));*/
		LOGGER.info("");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", "user1");
		params.put("departmentPk", "department-1");
		
		LOGGER.info("通过username查询Person列表--[select_person_by_username]--：");
		time = System.currentTimeMillis();
		List<Person> list = session.list(Person.class, "select_person_by_username", params);
		for (Person p : list) {
			LOGGER.info("===>" + String.valueOf(p));
		}
		millis = System.currentTimeMillis() - time;
		LOGGER.info("耗时（毫秒）：" + millis);
		LOGGER.info("");
		
		LOGGER.info("通过部门pk查询Person列表--[select_person_by_department_pk]--：");
		time = System.currentTimeMillis();
		list = session.list(Person.class, "select_person_by_department_pk", params);
		for (Person p : list) {
			LOGGER.info("===>" + String.valueOf(p));
		}
		millis = System.currentTimeMillis() - time;
		LOGGER.info("耗时（毫秒）：" + millis);
		LOGGER.info("");
		
		LOGGER.info("表连接查询Person列表--[select_joined_person_department]--：");
		time = System.currentTimeMillis();
		list = session.list(Person.class, "select_joined_person_department", params);
		for (Person p : list) {
			LOGGER.info("===>" + String.valueOf(p));
		}
		millis = System.currentTimeMillis() - time;
		LOGGER.info("耗时（毫秒）：" + millis);
		LOGGER.info("");
		
		LOGGER.info("执行更新部门信息--[update_department_5]--：");
		time = System.currentTimeMillis();
		session.execute(Department.class, "update_department_5");
		millis = System.currentTimeMillis() - time;
		LOGGER.info("耗时（毫秒）：" + millis);
		LOGGER.info("");
		
		LOGGER.info("查询部门信息--[select_department_5]--：");
		time = System.currentTimeMillis();
		List<Department> list2 = session.list(Department.class, "select_department_5");
		for (Department d : list2) {
			LOGGER.info("===>" + String.valueOf(d));
		}
		millis = System.currentTimeMillis() - time;
		LOGGER.info("耗时（毫秒）：" + millis);
		LOGGER.info("");
		
		LOGGER.info("插入配置信息--[insert_new_config]--：");
		Map<String, Object> configParams = new HashMap<String, Object>();
		configParams.put("configKey", "sys_name");
		configParams.put("configValue", "sql4j");
		session.execute(Config.class, "insert_new_config", configParams);
		time = System.currentTimeMillis();
		millis = System.currentTimeMillis() - time;
		LOGGER.info("耗时（毫秒）：" + millis);
		LOGGER.info("");
		
		LOGGER.info("查询配置信息--[select_sys_name]--：");
		List<Config> configList = session.list(Config.class, "select_sys_name");
		for (Config c : configList) {
			LOGGER.info("===>" + String.valueOf(c));
		}
		time = System.currentTimeMillis();
		millis = System.currentTimeMillis() - time;
		LOGGER.info("耗时（毫秒）：" + millis);
		LOGGER.info("");
		
		session.commit();
		session.close();
	}

}
