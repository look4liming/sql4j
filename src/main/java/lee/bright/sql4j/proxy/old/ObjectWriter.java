package lee.bright.sql4j.proxy.old;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Bright Lee
 */
public final class ObjectWriter {
	
	public static void write(SocketChannel socketChannel, int value) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		for (int i = 0; i < 4; i++) {
			int v = value >> ((4 - i - 1) * 8);
			byte b = (byte) (0x00FF & v);
			buffer.put(b);
		}
		buffer.flip();
		int limit = buffer.limit();
		int count = 0;
		while (true) {
			int size = socketChannel.write(buffer);
			count += size;
			if (count == limit) {
				break;
			}
		}
	}

}
