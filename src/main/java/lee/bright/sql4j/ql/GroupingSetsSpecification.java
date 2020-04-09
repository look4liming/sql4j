package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class GroupingSetsSpecification implements GroupingElement {
	
	private int beginIndex;
	private List<GroupingElement> groupingElementList;
	
	public GroupingSetsSpecification(int beginIndex, List<GroupingElement> 
			groupingElementList) {
		this.beginIndex = beginIndex;
		this.groupingElementList = groupingElementList;
	}
	
	public int size() {
		return groupingElementList.size();
	}
	
	public GroupingElement get(int index) {
		return groupingElementList.get(index);
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}

}
