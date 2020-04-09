package test.others;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CountLine {
	
	private static final String ABSOLUTE_ROOT_PATH = 
			"D:\\install\\jee-2018-12\\workspace\\2019-07-07\\sql4j\\src";

	public static void main(String[] args) throws IOException {
		System.out.println("Java源代码总行数：" + countFile(new File(ABSOLUTE_ROOT_PATH)));
	}
	
	private static int countFile(File file) throws IOException {
		if (file.isFile()) {
			if (file.getName().endsWith(".java")) {
				return countJava(file);
			}
			return 0;
		}
		File[] fs = file.listFiles();
		if (fs == null || fs.length == 0) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < fs.length; i++) {
			count += countFile(fs[i]);
		}
		return count;
	}
	
	private static int countJava(File file) throws IOException {
		int count = 0;
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), 4096);
			String line = null;
			while (true) {
				line = r.readLine();
				if (line == null) {
					break;
				}
				count += 1;
			}
		} finally {
			if (r != null) {
				r.close();
			}
		}
		return count;
	}

}
