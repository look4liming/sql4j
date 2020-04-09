package lee.bright.sql4j.ql;

import java.util.List;

/**
 * @author Bright Lee
 */
public final class RollupList implements GroupingElement {
	
	private int beginIndex;
	private List<GroupingColumnReference> list;
	
	public RollupList(int beginIndex, List<GroupingColumnReference> list) {
		this.beginIndex = beginIndex;
		this.list = list;
	}
	
	public int size() {
		return list.size();
	}
	
	public GroupingColumnReference get(int index) {
		return list.get(index);
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}

}
