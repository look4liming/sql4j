package lee.bright.sql4j.ql;

import java.math.BigInteger;
import java.util.List;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;
import lee.bright.sql4j.util.SqlStringLiteralUtil;

/**
 * @author Bright Lee
 */
public final class OracleGenerator extends Generator {
	
	public OracleGenerator(Configuration configuration, 
			List<Statement> list, Object object) {
		super(configuration, list, object);
	}

	@Override
	protected void generate(QuerySpecification querySpecification) {
		Page page = querySpecification.getPage();
		if (page != null) {
			append(TokenType.SELECT);
			append(' ');
			append('*');
			append(' ');
			append(TokenType.FROM);
			append(' ');
			append('(');
			append(TokenType.SELECT);
			append(' ');
			append("t.*,ROWNUM rn");
			append(' ');
			append(TokenType.FROM);
			append(' ');
			append('(');
		}
		append(TokenType.SELECT).append(' ');
		SetQuantifier setQuantifier = querySpecification.
				getSetQuantifier();
		generate(setQuantifier);
		if (setQuantifier != null) {
			append(' ');
		}
		List<SelectSublist> selectList = querySpecification.getSelectList();
		generate(selectList);
		List<TableReference> tableReferenceList = 
				querySpecification.getTableReferenceList();
		append(' ').append(TokenType.FROM).append(' ');
		generateTableReferenceList(tableReferenceList);
		BooleanValueExpression whereSearchCondition = 
				querySpecification.getWhereSearchCondition();
		if (whereSearchCondition != null) {
			append(' ').append(TokenType.WHERE).append(' ');
			generateCondition(whereSearchCondition);
		}
		List<GroupingElement> groupingElementList = 
				querySpecification.getGroupingElementList();
		if (groupingElementList != null) {
			append(' ').append(TokenType.GROUP).append(' ').
			append(TokenType.BY).append(' ');
			generateGroupingElementList(groupingElementList);
		}
		BooleanValueExpression havingSearchCondition = 
				querySpecification.getHavingSearchCondition();
		if (havingSearchCondition != null) {
			append(' ').append(TokenType.HAVING).append(' ');
			generateCondition(havingSearchCondition);
		}
		List<SortSpecification> sortSpecificationList = 
				querySpecification.getSortSpecificationList();
		if (sortSpecificationList != null) {
			append(' ').append(TokenType.ORDER).append(' ').
			append(TokenType.BY).append(' ');
			generateSortSpecificationList(sortSpecificationList);
		}
		if (page != null) {
			PageInformation pageInfo = getPageInfo(page);
			BigInteger beginIndex = pageInfo.getBeginIndex();
			BigInteger endIndex = pageInfo.getEndIndex();
			append(") t ");
			if (endIndex != null) {
				append(TokenType.WHERE);
				append(' ');
				append("ROWNUM<=");
				append(endIndex.toString());
			}
			append(')');
			append(' ');
			append('t');
			append(' ');
			append(TokenType.WHERE);
			append(' ');
			append("t.rn>=");
			append(beginIndex.add(BigInteger.ONE).toString());
		}
	}

	@Override
	protected void generate(DateLiteral dateLiteral) {
		String dateString;
		StringLiteral dateStringLiteral = dateLiteral.getDateStringLiteral();
		if (dateStringLiteral != null) {
			dateString = dateStringLiteral.getContent();
		} else {
			Parameter parameter = dateLiteral.getParameter();
			String parameterName = parameter.getContent();
			if (!argument.containsName(parameterName)) {
				throw Sql4jException.getSql4jException(sourceCode, 
						parameter.getBeginIndex(), 
						"Parameter '" + parameterName + "' not found.");
			}
			Object parameterValue = argument.getValue(parameterName);
			if (parameterValue == null) {
				throw Sql4jException.getSql4jException(sourceCode, 
						parameter.getBeginIndex(), 
						"Parameter '" + parameterName + "' cann't be null.");
			}
			dateString = parameterValue.toString();
			if (dateString == null) {
				throw Sql4jException.getSql4jException(sourceCode, 
						parameter.getBeginIndex(), 
						"Parameter '" + parameterName + "' cann't be null.");
			}
			dateString = dateString.replaceAll("'", "''");
		}
		append("to_date(");
		String sqlString = toSqlString(dateString);
		append(sqlString);
		append(',');
		append("'yyyy-mm-dd'");
		append(')');
	}

	@Override
	protected void generate(ToDate toDate) {
		append("to_date(");
		ValueExpression valueExpression = 
				toDate.getValueExpression();
		generate(valueExpression);
		append(',');
		StringLiteral pattern = toDate.getPattern();
		String content = pattern.getContent();
		if ("yyyy-MM-dd HH:mm:ss".equals(content)) {
			append('\'');
			append("yyyy-mm-dd hh24:mi:ss");
			append('\'');
		} else if ("yyyy-MM-dd".equals(content)) {
			append('\'');
			append("yyyy-mm-dd");
			append('\'');
		} else if ("HH:mm:ss".equals(content)) {
			append('\'');
			append("hh24:mi:ss");
			append('\'');
		} else {
			throw Sql4jException.getSql4jException(sourceCode, pattern.getBeginIndex(), 
					"'yyyy-MM-dd HH:mm:ss', 'yyyy-MM-dd' or 'HH:mm:ss' expected here.");
		}
		append(')');
	}

	@Override
	protected void generate(ToChar toChar) {
		append("to_char(");
		ValueExpression valueExpression = 
				toChar.getValueExpression();
		generate(valueExpression);
		append(',');
		StringLiteral pattern = toChar.getPattern();
		String content = pattern.getContent();
		if ("yyyy-MM-dd HH:mm:ss".equals(content)) {
			append('\'');
			append("yyyy-mm-dd hh24:mi:ss");
			append('\'');
		} else if ("yyyy-MM-dd".equals(content)) {
			append('\'');
			append("yyyy-mm-dd");
			append('\'');
		} else if ("HH:mm:ss".equals(content)) {
			append('\'');
			append("hh24:mi:ss");
			append('\'');
		} else {
			throw Sql4jException.getSql4jException(sourceCode, pattern.getBeginIndex(), 
					"'yyyy-MM-dd HH:mm:ss', 'yyyy-MM-dd' or 'HH:mm:ss' expected here.");
		}
		append(')');
	}

	@Override
	protected void generate(TimeLiteral timeLiteral) {
		String dateString;
		StringLiteral timeStringLiteral = timeLiteral.getTimeStringLiteral();
		if (timeStringLiteral != null) {
			dateString = timeStringLiteral.getContent();
		} else {
			Parameter parameter = timeLiteral.getParameter();
			String parameterName = parameter.getContent();
			if (!argument.containsName(parameterName)) {
				throw Sql4jException.getSql4jException(sourceCode, 
						parameter.getBeginIndex(), 
						"Parameter '" + parameterName + "' not found.");
			}
			Object parameterValue = argument.getValue(parameterName);
			if (parameterValue == null) {
				throw Sql4jException.getSql4jException(sourceCode, 
						parameter.getBeginIndex(), 
						"Parameter '" + parameterName + "' cann't be null.");
			}
			dateString = parameterValue.toString();
			if (dateString == null) {
				throw Sql4jException.getSql4jException(sourceCode, 
						parameter.getBeginIndex(), 
						"Parameter '" + parameterName + "' cann't be null.");
			}
			dateString = dateString.replaceAll("'", "''");
		}
		append("to_date(");
		String sqlString = toSqlString(dateString);
		append(sqlString);
		append(',');
		append("'hh24:mi:ss'");
		append(')');
	}

	@Override
	protected void generate(TimestampLiteral timestampLiteral) {
		String dateString;
		StringLiteral dateStringLiteral = timestampLiteral.getTimestampStringLiteral();
		if (dateStringLiteral != null) {
			dateString = dateStringLiteral.getContent();
		} else {
			Parameter parameter = timestampLiteral.getParameter();
			String parameterName = parameter.getContent();
			if (!argument.containsName(parameterName)) {
				throw Sql4jException.getSql4jException(sourceCode, 
						parameter.getBeginIndex(), 
						"Parameter '" + parameterName + "' not found.");
			}
			Object parameterValue = argument.getValue(parameterName);
			if (parameterValue == null) {
				throw Sql4jException.getSql4jException(sourceCode, 
						parameter.getBeginIndex(), 
						"Parameter '" + parameterName + "' cann't be null.");
			}
			dateString = parameterValue.toString();
			if (dateString == null) {
				throw Sql4jException.getSql4jException(sourceCode, 
						parameter.getBeginIndex(), 
						"Parameter '" + parameterName + "' cann't be null.");
			}
			dateString = dateString.replaceAll("'", "''");
		}
		append("to_date(");
		String sqlString = toSqlString(dateString);
		append(sqlString);
		append(',');
		append("'yyyy-mm-dd hh24:mi:ss'");
		append(')');
	}
	
	protected void generate(CharLengthExpression charLengthExpression) {
		append("length(");
		ValueExpression valueExpression = 
				charLengthExpression.getValueExpression();
		generate(valueExpression);
		append(')');
	}
	
	protected void generate(CurrentTimestamp currentTimestamp) {
		append("sysdate");
	}
	
	protected void generate(CurrentTime currentTime) {
		append("sysdate");
	}
	
	protected void generate(CurrentDate currentDate) {
		append("sysdate");
	}
	
	protected void generate(PositionExpression positionExpression) {
		append("position(");
		ValueExpression valueExpression1 = positionExpression.getValueExpression1();
		ValueExpression valueExpression2 = positionExpression.getValueExpression2();
		generate(valueExpression1);
		append(' ');
		append(TokenType.IN);
		append(' ');
		generate(valueExpression2);
		append(')');
	}
	
	protected void generate(DropIndexStatement dropIndexStatement) {
		// TODO 尚未实现。
		throw new Sql4jException("TODO 尚未实现。");
	}
	
	protected void generate(DropPrimaryKeyDefinition dropPrimaryKeyDefinition) {
		// TODO 尚未实现。
		throw new Sql4jException("TODO 尚未实现。");
	}
	
	protected void generate(AddPrimaryKeyDefinition addPrimaryKeyDefinition) {
		// TODO 尚未实现。
		throw new Sql4jException("TODO 尚未实现。");
	}
	
	protected void generate(ModifyColumnDefinition modifyColumnDefinition) {
		// TODO 尚未实现。
		throw new Sql4jException("TODO 尚未实现。");
	}
	
	protected String toSqlString(String javaString) {
		String sqlString = 
				SqlStringLiteralUtil.toOracleString(javaString);
		return sqlString;
	}

}
