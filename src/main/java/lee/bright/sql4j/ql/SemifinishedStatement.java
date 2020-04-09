package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class SemifinishedStatement {
	
	private StatementType statementType;
	private String statement;
	private List<Integer> parameterNumberList;
	private List<Object> parameterValueList;
	private List<Integer> resultNumberList;
	private List<String> resultNameList;
	private List<JdbcType> resultJdbcTypeList;
	private List<Integer> dataSourceIndexList;
	private int dataSourceIndex = -1;
	
	public SemifinishedStatement(StatementType statementType, 
			String statement, List<Integer> parameterNumberList, 
			List<Object> parameterValueList, 
			List<Integer> resultNumberList, 
			List<String> resultNameList, 
			List<JdbcType> resultJdbcTypeList, 
			List<Integer> dataSourceIndexList) {
		this.statementType = statementType;
		this.statement = statement;
		this.parameterNumberList = parameterNumberList;
		this.parameterValueList = parameterValueList;
		this.resultNumberList = resultNumberList;
		this.resultNameList = resultNameList;
		this.resultJdbcTypeList = resultJdbcTypeList;
		this.dataSourceIndexList = dataSourceIndexList;
	}
	
	public StatementType getStatementType() {
		return statementType;
	}

	public String getStatement() {
		return statement;
	}

	public List<Integer> getParameterNumberList() {
		return parameterNumberList;
	}

	public List<Object> getParameterValueList() {
		return parameterValueList;
	}

	public List<Integer> getResultNumberList() {
		return resultNumberList;
	}

	public List<String> getResultNameList() {
		return resultNameList;
	}
	
	public List<JdbcType> getResultJdbcTypeList() {
		return resultJdbcTypeList;
	}
	
	public List<Integer> getDataSourceIndexList() {
		return dataSourceIndexList;
	}
	
	void setDataSourceIndex(int dataSourceIndex) {
		this.dataSourceIndex = dataSourceIndex;
	}
	
	public int getDataSourceIndex() {
		return dataSourceIndex;
	}

}
