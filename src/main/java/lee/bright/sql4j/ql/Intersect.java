package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class Intersect implements SelectStatement {

	private Statement parentStatement;
	private SourceCode sourceCode;
	private int beginIndex;
	private SelectStatement left;
	private boolean all;
	private boolean distinct;
	private List<NameChain> correspondingColumnList;
	private SelectStatement right;
	private Page page;
	private DerivedTable derivedTable;
	
	public Intersect(SourceCode sourceCode, int beginIndex, 
			SelectStatement left, boolean all, boolean distinct, 
			List<NameChain> correspondingColumnList, 
			SelectStatement right, Page page) {
		this.sourceCode = sourceCode;
		this.beginIndex = beginIndex;
		this.left = left;
		this.all = all;
		this.distinct = distinct;
		this.correspondingColumnList = correspondingColumnList;
		this.right = right;
		this.page = page;
	}

	public StatementType getStatementType() {
		return StatementType.INTERSECT;
	}
	
	public SelectStatement getLeft() {
		return left;
	}
	
	public boolean getAll() {
		return all;
	}
	
	public boolean getDistinct() {
		return distinct;
	}
	
	public List<NameChain> getCorrespondingColumnList() {
		return correspondingColumnList;
	}
	
	public SelectStatement getRight() {
		return right;
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

}
