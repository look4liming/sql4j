package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public interface Statement {
	
	SourceCode getSourceCode();
	int getBeginIndex();
	StatementType getStatementType();

}
