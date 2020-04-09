package lee.bright.sql4j.proxy.old;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bright Lee
 */
public final class ReadTask implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.
			getLogger(ReadTask.class);
	
	private Sql4jProxyServer server;
	private SelectionKey selectionKey;
	private SocketChannel socketChannel;
	private volatile int blockSize;
	
	public ReadTask(Sql4jProxyServer server, SelectionKey selectionKey, SocketChannel socketChannel, int blockSize) {
		this.server = server;
		this.selectionKey = selectionKey;
		this.socketChannel = socketChannel;
		this.blockSize = blockSize;
	}
	
	public void run() {
		try {
			read();
			DataObject dataObject = Sql4jProxyServer.DATA_MAP.get(socketChannel);
			dataObject.setWritableData("你好");
			socketChannel.register(selectionKey.selector(), SelectionKey.OP_WRITE);
			//selectionKey.interestOps(SelectionKey.OP_WRITE);
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
	
	int getBlockSize() {
		return blockSize;
	}
	
	void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	private void read() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(blockSize);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while (true) {
			do {
				int count = socketChannel.read(byteBuffer);
				if (count < 0) {
					Sql4jProxyServer.INVALID_MAP.put(socketChannel, true);
					return;
				}
			} while (byteBuffer.capacity() - byteBuffer.remaining() < 4);
			byteBuffer.flip();
			int limit = byteBuffer.limit();
			int size = ObjectReader.readInt(byteBuffer);
			if (size < 0 || size > (blockSize - 4)) {
				throw new Exception("Size error. " + size);
			}
			while (limit < size + 4) {
				reset(byteBuffer);
				socketChannel.read(byteBuffer);
				byteBuffer.flip();
				size = ObjectReader.readInt(byteBuffer);
				if (size < 0 || size > (blockSize - 4)) {
					throw new Exception("Size error. " + size);
				}
				limit = byteBuffer.limit();
			}
			if (limit > size + 4) {
				// limit > size + 4
				//ByteArrayOutputStream out = new ByteArrayOutputStream();
				for (int i = 0; i < size; i++) {
					byte b = byteBuffer.get();
					out.write(b);
				}
				ByteArrayOutputStream out2 = new ByteArrayOutputStream();
				for (int i = 0; i < (limit - size - 4); i++) {
					byte b = byteBuffer.get();
					out2.write(b);
				}
				byteBuffer.clear();
				byteBuffer.put(out2.toByteArray());
				
				if (size < blockSize - 4) {
					byte[] bs = out.toByteArray();
					readed(bs);
					return;
				} else {
					continue;
				}
			} else {
				// limit == size + 4
				
				for (int i = 0; i < size; i++) {
					byte b = byteBuffer.get();
					out.write(b);
				}
				byteBuffer.clear();
				if (size < blockSize - 4) {
					byte[] bs = out.toByteArray();
					readed(bs);
					//byteBuffer.clear();
					return;
				} else {
					//byteBuffer.clear();
					continue;
				}
			}
		}
	}
	
	private void reset(ByteBuffer byteBuffer) {
		int limit = byteBuffer.limit();
		byteBuffer.clear();
		for (int i = 0; i < limit; i++) {
			byteBuffer.get();
		}
	}
	
	private void readed(byte[] bs) throws Exception {
		String s = null;
		try {
			s = new String(bs, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//if (s.indexOf("你好，我是客户端。timestamp:") < 0) {
		//	System.exit(0);
		//}
		if ("close".equals(s)) {
			selectionKey.cancel();
			socketChannel.close();
		}
	}
	
}