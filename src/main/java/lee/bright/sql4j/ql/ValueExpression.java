package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public interface ValueExpression {
	
	public int getBeginIndex();
	public int getEndIndex();
	public JdbcType getDataType();
	
}
