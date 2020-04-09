package lee.bright.sql4j;

import lee.bright.sql4j.conf.Configuration;

/**
 * @author Bright Lee
 */
public final class Sql4jSessionFactory {
	
	private Configuration configuration;
	
	public Sql4jSessionFactory() {
		configuration = new Configuration();
	}
	
	public Sql4jSession newSession() {
		final Sql4jSession session = new Sql4jSession(configuration);
		return session;
	}

}
