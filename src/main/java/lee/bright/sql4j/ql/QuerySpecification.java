package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class QuerySpecification implements SelectStatement {
	
	private Statement parentStatement;
	private SourceCode sourceCode;
	private int beginIndex;
	private SetQuantifier setQuantifier;
	private List<SelectSublist> selectList;
	private List<TableReference> tableReferenceList;
	private BooleanValueExpression whereSearchCondition;
	private List<GroupingElement> groupingElementList;
	private BooleanValueExpression havingSearchCondition;
	private List<SortSpecification> sortSpecificationList;
	private Page page;
	private DerivedTable derivedTable;
	private boolean setFullyQualifiedColumnNames;
	private QuerySpecificationDecisiveEquation decisiveEquation;
	
	public QuerySpecification(SourceCode sourceCode, 
			int beginIndex,
			SetQuantifier setQuantifier,
			List<SelectSublist> selectList, 
			List<TableReference> tableReferenceList,
			BooleanValueExpression whereSearchCondition,
			List<GroupingElement> groupingElementList,
			BooleanValueExpression havingSearchCondition,
			List<SortSpecification> sortSpecificationList,
			Page page) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.setQuantifier = setQuantifier;
		this.selectList = selectList;
		this.tableReferenceList = tableReferenceList;
		this.whereSearchCondition = whereSearchCondition;
		this.groupingElementList = groupingElementList;
		this.havingSearchCondition = havingSearchCondition;
		this.sortSpecificationList = sortSpecificationList;
		this.page = page;
	}

	public StatementType getStatementType() {
		return StatementType.QUERY_SPECIFICATION;
	}
	
	public SetQuantifier getSetQuantifier() {
		return setQuantifier;
	}
	
	public List<SelectSublist> getSelectList() {
		return selectList;
	}
	
	public List<TableReference> getTableReferenceList() {
		return tableReferenceList;
	}
	
	public BooleanValueExpression getWhereSearchCondition() {
		return whereSearchCondition;
	}
	
	public List<GroupingElement> getGroupingElementList() {
		return groupingElementList;
	}
	
	public BooleanValueExpression getHavingSearchCondition() {
		return havingSearchCondition;
	}
	
	public List<SortSpecification> getSortSpecificationList() {
		return sortSpecificationList;
	}
	
	public Page getPage() {
		return page;
	}

	public SourceCode getSourceCode() {
		return sourceCode;
	}

	public int getBeginIndex() {
		return beginIndex;
	}
	
	public Statement getParentStatement() {
		return parentStatement;
	}
	
	public void setParentStatement(Statement parentStatement) {
		this.parentStatement = parentStatement;
	}
	
	public DerivedTable getDerivedTable() {
		return derivedTable;
	}
	
	public void setDerivedTable(DerivedTable derivedTable) {
		this.derivedTable = derivedTable;
	}
	
	public void setSetFullyQualifiedColumnNames(boolean setFullyQualifiedColumnNames) {
		this.setFullyQualifiedColumnNames = setFullyQualifiedColumnNames;
	}
	
	public boolean isSetFullyQualifiedColumnNames() {
		return setFullyQualifiedColumnNames;
	}
	
	void setDecisiveEquation(QuerySpecificationDecisiveEquation decisiveEquation) {
		this.decisiveEquation = decisiveEquation;
	}
	
	public QuerySpecificationDecisiveEquation getDecisiveEquation() {
		return decisiveEquation;
	}

}
