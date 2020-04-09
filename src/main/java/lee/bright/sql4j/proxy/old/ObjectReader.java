package lee.bright.sql4j.proxy.old;

import java.nio.ByteBuffer;

/**
 * @author Bright Lee
 */
public final class ObjectReader {
	
	public static int readInt(ByteBuffer buffer) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			byte b = buffer.get();
			value = (value << 8) | (0x00FF & b);
		}
		// TODO
		return value;
	}

}
