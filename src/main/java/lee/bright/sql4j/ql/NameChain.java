package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bright Lee
 */
public final class NameChain implements ValueExpression {
	
	private List<Name> list;
	private JdbcType dataType;
	private ValueExpression fullyQualifiedName;
	
	public NameChain(List<Name> list) {
		this.list = list;
	}
	
	public NameChain() {
		this(new ArrayList<Name>(0));
	}
	
	public int getBeginIndex() {
		return list.get(0).getBeginIndex();
	}
	
	public int getEndIndex() {
		return list.get(list.size() - 1).getEndIndex();
	}
	
	public int size() {
		return list.size();
	}
	
	public Name get(int index) {
		return list.get(index);
	}
	
	public JdbcType getDataType() {
		return dataType;
	}
	
	void setDataType(JdbcType dataType) {
		this.dataType = dataType;
	}
	
	public ValueExpression getFullyQualifiedName() {
		return fullyQualifiedName;
	}
	
	void setFullyQualifiedName(ValueExpression fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}
	
	public String toLowerCaseString() {
		StringBuilder buf = new StringBuilder(100);
		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i).getContent().toLowerCase();
			buf.append(name);
			if (i < list.size() - 1) {
				buf.append('.');
			}
		}
		String s = buf.toString();
		s = s.toLowerCase();
		return s;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(100);
		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i).getContent();
			buf.append(name);
			if (i < list.size() - 1) {
				buf.append('.');
			}
		}
		String s = buf.toString();
		return s;
	}

}
