package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Bright Lee
 */
public final class SourceCode {
	
	private Class<?> clazz;
	private String sqlName;
	private int minRowNumber;
	private int maxRowNumber;
	private String sourceCode;
	
	public SourceCode(Class<?> clazz, String sqlName, 
			int rowNumber, String sourceCode) {
		this.clazz = clazz;
		this.sqlName = sqlName;
		this.minRowNumber = rowNumber;
		this.sourceCode = sourceCode;
		int count = 0;
		for (int i = 0; i < sourceCode.length(); i++) {
			char ch = sourceCode.charAt(i);
			if (ch == '\n') {
				count++;
			}
		}
		maxRowNumber = rowNumber + count;
	}
	
	public int getMinRowNumber() {
		return minRowNumber;
	}
	
	public int getMaxRowNumber() {
		return maxRowNumber;
	}
	
	public int length() {
		return sourceCode.length();
	}
	
	public char chatAt(int index) {
		return sourceCode.charAt(index);
	}
	
	@Override
	public String toString() {
		return sourceCode;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
	
	public String getSqlName() {
		return sqlName;
	}
	
	public String toString(List<IndexableMessage> list) {
		Collections.sort(list);
		StringBuilder buf = new StringBuilder(sourceCode.length() + 500);
		int beginIndex = 0;
		int endIndex = 0;
		int rowNumber = 0;
		while (true) {
			for (int i = beginIndex; i < sourceCode.length(); i++) {
				char ch = sourceCode.charAt(i);
				if (i == sourceCode.length() - 1 || ch == '\n') {
					endIndex = i + 1;
					rowNumber++;
					break;
				}
			}
			String row = String.valueOf(minRowNumber + rowNumber - 1);
			String max = String.valueOf(maxRowNumber);
			for (int i = 0; i < max.length() - row.length(); i++) {
				buf.append(' ');
			}
			buf.append(row).append(' ').append(':').append(' ');
			for (int i = beginIndex; i < endIndex; i++) {
				char ch = sourceCode.charAt(i);
				buf.append(ch);
			}
			for (int i = 0; i < list.size(); i++) {
				IndexableMessage msg = list.get(i);
				int index = msg.getIndex();
				String message = msg.getMessage();
				if (index >= beginIndex && index < endIndex) {
					if (buf.charAt(buf.length() - 1) != '\n') {
						buf.append('\n');
					}
					for (int j = 0; j < max.length() - 1; j++) {
						buf.append(' ');
					}
					buf.append('>').append(' ').append(':').append(' ');
					for (int j = beginIndex; j < index; j++) {
						char ch = sourceCode.charAt(j);
						buf.append(ch);
					}
					buf.append('\n');
					for (int j = 0; j < max.length() - 1; j++) {
						buf.append(' ');
					}
					buf.append('*').append(' ').append(':').append(' ');
					buf.append(message).append('\n');
				}
			}
			if (endIndex == sourceCode.length()) {
				break;
			}
			beginIndex = endIndex;
		}
		if (buf.charAt(buf.length() - 1) == '\n') {
			buf.deleteCharAt(buf.length() - 1);
		}
		String result = buf.toString();
		return result;
	}
	
	public String toString(IndexableMessage msg) {
		List<IndexableMessage> list = 
				new ArrayList<IndexableMessage>(1);
		String string = toString(list);
		return string;
	}

}
