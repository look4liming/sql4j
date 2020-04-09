package lee.bright.sql4j;

import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.ql.IndexableMessage;
import lee.bright.sql4j.ql.SourceCode;

/**
 * @author Bright Lee
 */
public final class Sql4jException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public Sql4jException(Exception e) {
		super(e);
	}
	
	public Sql4jException(String message) {
		super(message);
	}
	
	public Sql4jException(String message, 
			Exception e) {
		super(message, e);
	}
	
	public Sql4jException(SourceCode sourceCode, 
			List<IndexableMessage> list) {
		super(getMessage(sourceCode, list));
	}
	
	public Sql4jException(SourceCode sourceCode, 
			List<IndexableMessage> list, Throwable t) {
		super(getMessage(sourceCode, list), t);
	}
	
	private static String getMessage(SourceCode sourceCode, 
			List<IndexableMessage> list) {
		Class<?> clazz = sourceCode.getClazz();
		String sqlName = sourceCode.getSqlName();
		StringBuilder buf = new StringBuilder(1000);
		buf.append(clazz.getName());
		buf.append("--[").append(sqlName).append("]--");
		buf.append('\n');
		buf.append(sourceCode.toString(list));
		String msg = buf.toString();
		return msg;
	}
	
	public static Sql4jException getSql4jException(SourceCode sourceCode, int index, String msg) {
		IndexableMessage message = new IndexableMessage(index, msg);
		List<IndexableMessage> list = new ArrayList<IndexableMessage>(1);
		list.add(message);
		return new Sql4jException(sourceCode, list);
	}
	
	public static Sql4jException getSql4jException(SourceCode sourceCode, int index, String msg, Throwable cause) {
		IndexableMessage message = new IndexableMessage(index, msg);
		List<IndexableMessage> list = new ArrayList<IndexableMessage>(1);
		list.add(message);
		return new Sql4jException(sourceCode, list, cause);
	}

}
