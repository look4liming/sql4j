package lee.bright.sql4j.ql;

import lee.bright.sql4j.conf.Configuration;

/**
 * @author Bright Lee
 */
public final class Optimizer {
	
	private Configuration configuration;
	private Analyzer analyzer;
	
	public Optimizer(Configuration configuration, 
			SourceCode sourceCode) {
		this.configuration = configuration;
		analyzer = new Analyzer(this.configuration, 
				sourceCode);
	}
	
	public Statement optimize() {
		Statement statement = analyzer.analyze();
		// TODO
		return statement;
	}

}
