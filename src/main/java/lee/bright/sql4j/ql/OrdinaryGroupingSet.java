package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bright Lee
 */
public class OrdinaryGroupingSet implements GroupingElement {
	
	private int beginIndex;
	private List<GroupingColumnReference> list;
	
	public OrdinaryGroupingSet(int beginIndex, List<GroupingColumnReference> list) {
		this.beginIndex = beginIndex;
		this.list = list;
	}
	
	public OrdinaryGroupingSet(int beginIndex, GroupingColumnReference 
			groupingColumnReference) {
		this.beginIndex = beginIndex;
		List<GroupingColumnReference> list = 
				new ArrayList<GroupingColumnReference>(1);
		list.add(groupingColumnReference);
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
