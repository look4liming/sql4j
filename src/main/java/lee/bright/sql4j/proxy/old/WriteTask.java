package lee.bright.sql4j.proxy.old;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bright Lee
 */
public final class WriteTask implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.
			getLogger(WriteTask.class);
	
	private Sql4jProxyServer server;
	private SelectionKey selectionKey;
	private SocketChannel socketChannel;
	private volatile int blockSize;
	
	public WriteTask(Sql4jProxyServer server, SelectionKey selectionKey, SocketChannel socketChannel, int blockSize) {
		this.server = server;
		this.selectionKey = selectionKey;
		this.socketChannel = socketChannel;
		this.blockSize = blockSize;
	}
	
	public void run() {
		try {
			write();
			DataObject dataObject = Sql4jProxyServer.DATA_MAP.get(socketChannel);
			dataObject.setReadableData("你好");
			socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
			//selectionKey.interestOps(SelectionKey.OP_READ);
			selectionKey.selector().wakeup();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				selectionKey.cancel();
				socketChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void write() throws IOException {
		String s = "你好，我是服务器。timestamp: " + System.currentTimeMillis();
		byte[] bs;
		try {
			bs = s.getBytes("UTF-8");
			int blockCount = (bs.length / (blockSize - 4)) + 1;
			for (int i = 0; i < blockCount; i++) {
				int beginIndex = i * (blockSize - 4);
				int endIndex = (i + 1) * (blockSize - 4);
				if (endIndex > bs.length) {
					endIndex = bs.length;
				}
				int size = endIndex - beginIndex;
				ObjectWriter.write(socketChannel, size);
				System.out.println("size=====================================>"+size);
				ByteBuffer byteBuffer = ByteBuffer.allocate(blockSize);
				byteBuffer.put(bs, beginIndex, size);
				byteBuffer.flip();
				int count = 0;
				while (true) {
					int count2 = socketChannel.write(byteBuffer);
					count += count2;
					if (count == byteBuffer.limit()) {
						break;
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
}