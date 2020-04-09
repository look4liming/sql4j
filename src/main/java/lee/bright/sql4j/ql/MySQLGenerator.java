package lee.bright.sql4j.ql;

import java.math.BigInteger;
import java.util.List;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;
import lee.bright.sql4j.util.SqlStringLiteralUtil;

/**
 * @author Bright Lee
 */
public final class MySQLGenerator extends Generator {
	
	public MySQLGenerator(Configuration configuration, 
			List<Statement> list, Object object) {
		super(configuration, list, object);
	}

	@Override
	protected void generate(QuerySpecification querySpecification) {
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
		Page page = querySpecification.getPage();
		if (page != null) {
			generate(page);
		}
	}
	
	private void generate(Page page) {
		PageInformation pageInfo = getPageInfo(page);
		BigInteger beginIndex = pageInfo.getBeginIndex();
		BigInteger endIndex = pageInfo.getEndIndex();
		append(' ');
		append("limit");
		append(' ');
		append(beginIndex.toString());
		if (endIndex != null) {
			append(',');
			append(endIndex.subtract(beginIndex).toString());
		}
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
		append("str_to_date(");
		String sqlString = toSqlString(dateString);
		append(sqlString);
		append(',');
		append("'%Y-%m-%d %H:%i:%s'");
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
		append("str_to_date(");
		String sqlString = toSqlString(dateString);
		append(sqlString);
		append(',');
		append("'%H:%i:%s'");
		append(')');
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
		append("str_to_date(");
		String sqlString = toSqlString(dateString);
		append(sqlString);
		append(',');
		append("'%Y-%m-%d'");
		append(')');
	}
	
	@Override
	protected void generate(Concatenation concatenation) {
		append("concat(");
		ValueExpression left = concatenation.getLeft();
		generate(left);
		append(',');
		ValueExpression right = concatenation.getRight();
		generate(right);
		append(')');
	}
	
	@Override
	protected void generate(FullOuterJoin fullOuterJoin) {
		throw Sql4jException.getSql4jException(sourceCode, 
				fullOuterJoin.getBeginIndex(), 
				"MySQL not support full outer join.");
	}
	
	@Override
	protected void generate(NaturalFullOuterJoin natualFullOuterJoin) {
		throw Sql4jException.getSql4jException(sourceCode, 
				natualFullOuterJoin.getBeginIndex(), 
				"MySQL not support natural full outer join.");
	}

	@Override
	protected void generate(ToDate toDate) {
		append("str_to_date(");
		ValueExpression valueExpression = 
				toDate.getValueExpression();
		generate(valueExpression);
		append(',');
		StringLiteral pattern = toDate.getPattern();
		String content = pattern.getContent();
		if ("yyyy-MM-dd HH:mm:ss".equals(content)) {
			append('\'');
			append("%Y-%m-%d %H:%i:%s");
			append('\'');
		} else if ("yyyy-MM-dd".equals(content)) {
			append('\'');
			append("%Y-%m-%d");
			append('\'');
		} else if ("HH:mm:ss".equals(content)) {
			append('\'');
			append("%H:%i:%s");
			append('\'');
		} else {
			throw Sql4jException.getSql4jException(sourceCode, pattern.getBeginIndex(), 
					"'yyyy-MM-dd HH:mm:ss', 'yyyy-MM-dd' or 'HH:mm:ss' expected here.");
		}
		append(')');
	}

	@Override
	protected void generate(ToChar toChar) {
		append("date_format(");
		ValueExpression valueExpression = 
				toChar.getValueExpression();
		generate(valueExpression);
		append(',');
		StringLiteral pattern = toChar.getPattern();
		String content = pattern.getContent();
		if ("yyyy-MM-dd HH:mm:ss".equals(content)) {
			append('\'');
			append("%Y-%m-%d %H:%i:%s");
			append('\'');
		} else if ("yyyy-MM-dd".equals(content)) {
			append('\'');
			append("%Y-%m-%d");
			append('\'');
		} else if ("HH:mm:ss".equals(content)) {
			append('\'');
			append("%H:%i:%s");
			append('\'');
		} else {
			throw Sql4jException.getSql4jException(sourceCode, pattern.getBeginIndex(), 
					"'yyyy-MM-dd HH:mm:ss', 'yyyy-MM-dd' or 'HH:mm:ss' expected here.");
		}
		append(')');
	}
	
	protected void generateCurrentTimestamp() {
		append("now()");
	}
	
	protected void generate(DropIndexStatement dropIndexStatement) {
		append(TokenType.ALTER);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain indexName = dropIndexStatement.getIndexName();
		for (int i = 0; i < indexName.size() - 1; i++) {
			Name name = indexName.get(i);
			generate(name);
			if (i < indexName.size() - 2) {
				append('.');
			}
		}
		append(' ');
		append(TokenType.DROP);
		append(' ');
		append(TokenType.INDEX);
		append(' ');
		Name name = indexName.get(indexName.size() - 1);
		generate(name);
	}
	
	protected void generate(DropPrimaryKeyDefinition dropPrimaryKeyDefinition) {
		append(TokenType.ALTER);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain tableName = dropPrimaryKeyDefinition.getTableName();
		generate(tableName);
		append(' ');
		append(TokenType.DROP);
		append(' ');
		append(TokenType.PRIMARY);
		append(' ');
		append("KEY");
	}
	
	protected void generate(AddPrimaryKeyDefinition addPrimaryKeyDefinition) {
		append(TokenType.ALTER);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain tableName = addPrimaryKeyDefinition.getTableName();
		generate(tableName);
		append(' ');
		append(TokenType.ADD);
		append(' ');
		append(TokenType.PRIMARY);
		append(' ');
		append("KEY");
		append('(');
		Name columnName = addPrimaryKeyDefinition.getColumnName();
		generate(columnName);
		append(')');
	}
	
	protected void generate(ModifyColumnDefinition modifyColumnDefinition) {
		append(TokenType.ALTER);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain tableName = modifyColumnDefinition.getTableName();
		generate(tableName);
		append(' ');
		append(TokenType.MODIFY);
		append(' ');
		ColumnDefinition columnDefinition = modifyColumnDefinition.
				getColumnDefinition();
		generate(columnDefinition);
	}
	
	protected String toSqlString(String javaString) {
		String sqlString = 
				SqlStringLiteralUtil.toMySQLString(javaString);
		return sqlString;
	}

}
