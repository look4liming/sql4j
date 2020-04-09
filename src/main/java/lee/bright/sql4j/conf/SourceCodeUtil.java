package lee.bright.sql4j.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.ql.SourceCode;

/**
 * @author Bright Lee
 */
public final class SourceCodeUtil {
	
	public static Map<String, SourceCode> getSourceCodeMap(Class<?> clazz) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(FileUtil.getSqlFileReader(clazz), 4096);
			Map<String, SourceCode> map = 
					new LinkedHashMap<String, SourceCode>();
			List<Integer> rowNumberList = 
					new ArrayList<Integer>();
			int index = 0;
			String sqlName = null;
			StringBuilder buf = new StringBuilder(1000);
			while (true) {
				String line = reader.readLine();
				index++;
				if (line == null) {
					if (sqlName != null) {
						String sql = buf.toString();
						sql = sql.substring(0, sql.length() - 1);
						SourceCode sourceCode = 
								new SourceCode(clazz, sqlName, rowNumberList.
										get(rowNumberList.size() - 1), 
										sql);
						map.put(sqlName, sourceCode);
					}
					break;
				}
				String name = getSqlName(line);
				if (name != null) {
					rowNumberList.add(index + 1);
					if (sqlName == null) {
						sqlName = name;
						buf.setLength(0);
						continue;
					}
					String sql = buf.toString();
					sql = sql.substring(0, sql.length() - 1);
					int rowNumber;
					if (rowNumberList.size() >= 2) {
						rowNumber = rowNumberList.get(
								rowNumberList.size() - 2);
					} else {
						rowNumber = rowNumberList.get(
								rowNumberList.size() - 1);
					}
					SourceCode sourceCode = 
							new SourceCode(clazz, name, rowNumber, sql);
					map.put(sqlName, sourceCode);
					sqlName = name;
					buf.setLength(0);
				} else {
					buf.append(line).append('\n');
				}
			}
			return map;
		} catch (Exception e) {
			throw new Sql4jException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new Sql4jException(e);
				}
			}
		}
	}
	
	private static String getSqlName(String line) {
		if (line == null) {
			return null;
		}
		int index1 = line.indexOf('[');
		int index2 = line.lastIndexOf(']');
		if (index1 >= index2) {
			return null;
		}
		String line2 = line.trim();
		if (!line2.startsWith("--")) {
			return null;
		}
		for (int i = 0; i < index1; i++) {
			char ch = line.charAt(i);
			if (ch != '-' && ch != ' ' && ch != '\t') {
				return null;
			}
		}
		for (int i = index2 + 1; i < line.length(); i++) {
			char ch = line.charAt(i);
			if (ch != '-' && ch != ' ' && ch != '\t') {
				return null;
			}
		}
		return line.substring(index1 + 1, index2);
	}

}
