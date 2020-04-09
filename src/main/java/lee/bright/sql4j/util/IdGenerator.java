package lee.bright.sql4j.util;

import java.util.UUID;

/**
 * @author Bright Lee
 */
public final class IdGenerator {
	
	public static String generateUUID() {
		String uuid = UUID.randomUUID().toString();
		StringBuilder buf = new StringBuilder(32);
		char ch = '\0';
		for (int i = 0; i < uuid.length(); i++) {
			ch = uuid.charAt(i);
			if (ch != '-') {
				buf.append(ch);
			}
		}
		uuid = buf.toString();
		return uuid;
	}

}
