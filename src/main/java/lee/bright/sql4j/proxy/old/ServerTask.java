package lee.bright.sql4j.proxy.old;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lee.bright.sql4j.Sql4jProxyServer;

/**
 * @author Bright Lee
 */
public final class ServerTask implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.
			getLogger(ServerTask.class);
	
	private Sql4jProxyServer server;
	private SelectionKey selectionKey;
	private ServerSocketChannel serverSocketChannel;
	private SocketChannel socketChannel;
	private volatile boolean sendedBlockSize;
	private volatile int blockSize;
	private volatile WritableObject writableObject;
	private volatile ReadableObject readableObject;
	private volatile boolean close;
	
	public volatile boolean over = false;
	
	public ServerTask(Sql4jProxyServer server, ServerSocketChannel serverSocketChannel, SelectionKey selectionKey, SocketChannel socketChannel, int blockSize) {
		this.server = server;
		this.serverSocketChannel = serverSocketChannel;
		this.selectionKey = selectionKey;
		this.socketChannel = socketChannel;
		this.blockSize = blockSize;
		writableObject = new WritableObject(blockSize);
		readableObject = new ReadableObject(blockSize);
	}
	
	public void run() {
		while (true) {
			//System.out.println("readableObject.pause---&&&&&&&&&&&&&&&&&&&&&&&&--->"+readableObject.pause);
			if (readableObject.pause == true) {
				//continue;
			}
			if (close == true) {
				try {
					throw new RuntimeException("TODO----------------");
				} finally {
					System.exit(0);
				}
			}
			System.out.println("@@@===writableObject.isWritable()===>"+writableObject.isWritable());
			System.out.println("%%%===readableObject.isReadable()===>"+readableObject.isReadable());
			if (writableObject.isWritable()) {
				try {
					if (sendedBlockSize == false) {
						sendBlockSize();
						sendedBlockSize = true;
						//synchronized (Sql4jProxyServer.lock) {
						readableObject.pause = true;
							socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
							//selectionKey.selector().wakeup();
						//}
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
					try {
						socketChannel.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						System.out.println("------------TODO");
						System.out.println("------------TODO");
						System.out.println("------------TODO");
					} finally {
						System.exit(0);
					}
					break;
				}
				try {
					write();
					//socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
					//writableObject.setWritable(false);
					//readableObject.setReadable(true);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						socketChannel.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						System.out.println("1------------TODO");
						System.out.println("1------------TODO");
						System.out.println("1------------TODO");
					} finally {
						System.exit(0);
					}
					break;
				}
				continue;
			}
			if (readableObject.isReadable()) {
				try {
					read();
					//socketChannel.register(selectionKey.selector(), SelectionKey.OP_WRITE);
					//writableObject.setWritable(true);
					//readableObject.setReadable(false);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						socketChannel.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						System.out.println("2------------TODO");
						System.out.println("2------------TODO");
						System.out.println("2------------TODO");
					} finally {
						System.exit(0);
					}
					break;
				}
				continue;
			}
			//Thread.yield();
		}
		try {
			System.out.println("3------------TODO");
			System.out.println("3------------TODO");
			System.out.println("3------------TODO");
		} finally {
			System.exit(0);
		}
		over = true;
	}
	
	int getBlockSize() {
		return blockSize;
	}
	
	void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	
	public void setWritable(boolean writable) {
		writableObject.setWritable(writable);
	}
	
	public void setReadable(boolean readable) {
		readableObject.setReadable(readable);
		readableObject.pause = false;
	}
	
	private void sendBlockSize() {
		try {
			ObjectWriter.write(socketChannel, blockSize);
			//socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
			//writableObject.setWritable(false);
			//readableObject.setReadable(true);
		} catch (Exception e) {
			LOGGER.error("Synchronization block size error. Close socket channel...", e);
			try {
				socketChannel.close();
			} catch (Exception e1) {
				LOGGER.error("Close socket channel error.", e1);
				return;
			}
			LOGGER.info("Socket channel is closed.");
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
				ByteBuffer byteBuffer = writableObject.getByteBuffer();
				byteBuffer.clear();
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
			//synchronized (Sql4jProxyServer.lock) {
			readableObject.pause = true;
				socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
				//selectionKey.selector().wakeup();
			//}
			//writableObject.setWritable(false);
			//readableObject.setReadable(true);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// TODO 发送数据
	}
	
	private volatile int i = 0;
	
	private void read() throws Exception {
		ByteBuffer byteBuffer = readableObject.getByteBuffer();
		//InputStream in = socketChannel.socket().getInputStream();
		//ReadableByteChannel readCh = Channels.newChannel(in);
		int count = socketChannel.read(byteBuffer);
		System.out.println("read(byteBuffer)==========>"+count);
		System.out.println("byteBuffer================>"+byteBuffer);
		System.out.println(System.currentTimeMillis() + "   remaining>>>>>>>>>>>"+byteBuffer.remaining()+">>>writable>>>"+this.getWritableObject().isWritable()+">>>readable>>>"+this.getReadableObject().isReadable());
		if (byteBuffer.capacity() - byteBuffer.remaining() < 4) {
			//synchronized (Sql4jProxyServer.lock) {
			readableObject.pause = true;
				socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
				//selectionKey.selector().wakeup();
			//}
			//writableObject.setWritable(false);
			//readableObject.setReadable(true);
			return;
		}
		byteBuffer.flip();
		int limit = byteBuffer.limit();
		/*if (limit < 4) {
			byteBuffer.clear();
			for (int i = 0; i < limit; i++) {
				byteBuffer.get();
			}
			socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
			return;
		}*/
		int size = ObjectReader.readInt(byteBuffer);
		System.out.println("limit:::::::::::::::::::::::"+limit);
		System.out.println("size::::::::::::::::::::::::"+size);
		if (size < 0 || size > (blockSize - 4)) {
			throw new Exception("Size error. " + size);
		}
		if (limit < size + 4) {
			byteBuffer.clear();
			for (int i = 0; i < limit; i++) {
				byteBuffer.get();
			}
			//synchronized (Sql4jProxyServer.lock) {
			readableObject.pause = true;
				socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
				//selectionKey.selector().wakeup();
			//}
			//writableObject.setWritable(false);
			//readableObject.setReadable(true);
			return;
		} else if (limit > size + 4) {
			for (int i = 0; i < 1000; i++) {
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$--->"+limit);
			}
			ByteArrayOutputStream out = readableObject.getOut();
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
			System.out.println("size-------------------333333-------------------->"+(size));
			if (size < blockSize - 4) {
				//socketChannel.register(selectionKey.selector(), SelectionKey.OP_WRITE);
				//writableObject.setWritable(true);
				//readableObject.setReadable(false);
				byte[] bs = out.toByteArray();
				readableObject.clearOut();
				readed(bs);
				//synchronized (Sql4jProxyServer.lock) {
				readableObject.pause = true;
					socketChannel.register(selectionKey.selector(), SelectionKey.OP_WRITE);
					//selectionKey.selector().wakeup();
				//}
				System.out.println("============OP_WRITE=================");
				//setReadable(false);
				//setWritable(false);
				i++;
				if (i >= 24699) {
					System.out.println("--------------->"+i);
				}
				return;
			} else {
				//synchronized (Sql4jProxyServer.lock) {
				readableObject.pause = true;
					socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
					//selectionKey.selector().wakeup();
				//}
				//writableObject.setWritable(false);
				//readableObject.setReadable(true);
				return;
			}
		} else {
			ByteArrayOutputStream out = readableObject.getOut();
			for (int i = 0; i < size; i++) {
				byte b = byteBuffer.get();
				out.write(b);
			}
			System.out.println("size--------------------------------------->"+(size));
			if (size < blockSize - 4) {
				//socketChannel.register(selectionKey.selector(), SelectionKey.OP_WRITE);
				//writableObject.setWritable(true);
				//readableObject.setReadable(false);
				byte[] bs = out.toByteArray();
				readableObject.clearOut();
				readed(bs);
				byteBuffer.clear();
				//synchronized (Sql4jProxyServer.lock) {
				readableObject.pause = true;
					socketChannel.register(selectionKey.selector(), SelectionKey.OP_WRITE);
					//selectionKey.selector().wakeup();
				//}
				System.out.println("============OP_WRITE=================2222");
				//setReadable(false);
				//setWritable(false);
				System.out.println("--------------->"+i++);
				return;
			} else {
				byteBuffer.clear();
				//synchronized (Sql4jProxyServer.lock) {
				readableObject.pause = true;
					socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
					//selectionKey.selector().wakeup();
				//}
				//writableObject.setWritable(false);
				//readableObject.setReadable(true);
				return;
			}
		}
	}
	
	private void readed(byte[] bs) throws ClosedChannelException {
		String s = null;
		try {
			s = new String(bs, "UTF-8");
			//writableObject.setWritable(true);
			//readableObject.setReadable(false);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (s.indexOf("你好，我是客户端。timestamp:") < 0) {
			System.exit(0);
		}
		System.out.println("======>"+s);
		// TODO
	}
	
	public WritableObject getWritableObject() {
		return writableObject;
	}
	
	public ReadableObject getReadableObject() {
		return readableObject;
	}
	
	public void setClose(boolean close) {
		this.close = close;
	}
	
}