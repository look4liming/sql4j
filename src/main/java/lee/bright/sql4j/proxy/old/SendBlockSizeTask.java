package lee.bright.sql4j.proxy.old;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bright Lee
 */
public final class SendBlockSizeTask implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.
			getLogger(SendBlockSizeTask.class);
	
	private Sql4jProxyServer server;
	private SelectionKey selectionKey;
	private SocketChannel socketChannel;
	private volatile int blockSize;
	
	public volatile boolean over = false;
	
	public SendBlockSizeTask(Sql4jProxyServer server, SelectionKey selectionKey, SocketChannel socketChannel, int blockSize) {
		this.server = server;
		this.selectionKey = selectionKey;
		this.socketChannel = socketChannel;
		this.blockSize = blockSize;
	}
	
	public void run() {
		try {
			System.out.println("#################>sendBlockSize");
			sendBlockSize();
			System.out.println("#################>sendedBlockSize");
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
		System.out.println("<><><><><><><><><><><><><><><><><><><><>");
	}
	
	int getBlockSize() {
		return blockSize;
	}
	
	void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	private void sendBlockSize() {
		try {
			ObjectWriter.write(socketChannel, blockSize);
		} catch (Exception e) {
			LOGGER.error("Synchronization block size error. Close socket channel...", e);
			try {
				selectionKey.cancel();
				socketChannel.close();
			} catch (Exception e1) {
				LOGGER.error("Close socket channel error.", e1);
				return;
			}
			LOGGER.info("Socket channel is closed.");
		}
	}
	
}