package lee.bright.sql4j.ql;

import java.math.BigInteger;
import java.sql.Types;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.ColumnMetadata;
import lee.bright.sql4j.conf.Configuration;
import lee.bright.sql4j.conf.NameValuePairs;

/**
 * @author Bright Lee
 */
public final class HashUtil {
	
	private static final int TYPES_INTEGER = 0;
	private static final int TYPES_STRING = 1;
	
	private static int getType(ColumnMetadata columnMetadata) {
		int columnType = columnMetadata.getColumnType();
		int type;
		if (columnType == Types.BIGINT ||
			columnType == Types.INTEGER ||
			columnType == Types.SMALLINT ||
			columnType == Types.TINYINT) {
			type = TYPES_INTEGER;
		} else if (columnType != Types.CHAR ||
			columnType == Types.LONGNVARCHAR ||
			columnType == Types.LONGVARCHAR ||
			columnType == Types.NCHAR ||
			columnType == Types.NVARCHAR ||
			columnType == Types.VARCHAR) {
			type = TYPES_STRING;
		} else {
			type = -1;
		}
		return type;
	}
	
	public static void checkHashColumnAndHashValue(Configuration configuration, SourceCode sourceCode, 
			HashKeyValue hashKeyValue) {
		NameChain hasnColumn = hashKeyValue.getKey();
		ValueExpression hashValue = hashKeyValue.getValue();
		checkHashColumnAndHashValue(configuration, sourceCode, hasnColumn, hashValue);
	}
	
	public static void checkHashColumnAndHashValue(Configuration configuration, SourceCode sourceCode, 
			NameChain hashColumn, ValueExpression hashValue) {
		NameChain fullyQualifiedName = (NameChain) hashColumn.getFullyQualifiedName();
		ColumnMetadata columnMetadata = configuration.getColumnMetadata(fullyQualifiedName);
		int type = getType(columnMetadata);
		if (type != TYPES_INTEGER && type != TYPES_STRING) {
			throw Sql4jException.getSql4jException(sourceCode, hashColumn.getBeginIndex(), 
					"Hash columns can only be strings or integers.");
		}
		if (type == TYPES_INTEGER) {
			if (!(hashValue instanceof Parameter) &&
				!(hashValue instanceof NumericLiteral)) {
				throw Sql4jException.getSql4jException(sourceCode, hashValue.getBeginIndex(), 
						"Hash values can only be parameter or numeric literal.");
			}
			if (hashValue instanceof NumericLiteral) {
				NumericLiteral numericLiteral = (NumericLiteral) hashValue;
				String content = numericLiteral.getContent();
				if (content.indexOf(".") > -1) {
					throw Sql4jException.getSql4jException(sourceCode, hashValue.getBeginIndex(), 
							"Hash values cannot be floating-point numbers.");
				}
				if (content.indexOf("e") > -1 || content.indexOf("E") > -1) {
					throw Sql4jException.getSql4jException(sourceCode, hashValue.getBeginIndex(), 
							"Hash values cannot be numbers represented by scientific counting.");
				}
			}
			return;
		}
		if (type == TYPES_STRING) {
			if (!(hashValue instanceof Parameter) &&
				!(hashValue instanceof StringLiteral)) {
				throw Sql4jException.getSql4jException(sourceCode, hashValue.getBeginIndex(), 
						"Hash values can only be parameter or string literal.");
			}
		}
	}
	
	public static void checkHashColumnAndHashValue(Configuration configuration, SourceCode sourceCode, 
			NameChain hashColumn, Parameter parameter, NameValuePairs arguments) {
		String parameterName = parameter.getContent();
		if (!arguments.containsName(parameterName)) {
			throw Sql4jException.getSql4jException(sourceCode, parameter.getBeginIndex(), 
					"Parameter not found.");
		}
		Object value = arguments.getValue(parameter.getContent());
		NameChain fullyQualifiedName = (NameChain) hashColumn.getFullyQualifiedName();
		ColumnMetadata columnMetadata = configuration.getColumnMetadata(fullyQualifiedName);
		int type = getType(columnMetadata);
		if (type != TYPES_INTEGER && type != TYPES_STRING) {
			throw Sql4jException.getSql4jException(sourceCode, hashColumn.getBeginIndex(), 
					"Hash columns can only be strings or integers.");
		}
		if (type == TYPES_INTEGER) {
			if (!(value instanceof Byte) &&
				!(value instanceof Short) &&
				!(value instanceof Integer) &&
				!(value instanceof Long) &&
				!(value instanceof BigInteger)) {
				throw Sql4jException.getSql4jException(sourceCode, parameter.getBeginIndex(), 
						"Hash values can only be integers. " + value);
			}
			return;
		}
		if (type == TYPES_STRING) {
			if (!(value instanceof String)) {
				throw Sql4jException.getSql4jException(sourceCode, parameter.getBeginIndex(), 
						"Hash values can only be strings. " + value);
			}
		}
	}

}
