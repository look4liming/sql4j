package lee.bright.sql4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lee.bright.sql4j.proxy.old.ServerTask;

/**
 * @author Bright Lee
 */
public final class Sql4jProxyServer {
	
	public static void main(String[] args) {
		new Sql4jProxyServer();
	}
	
	//public static final Object lock = new Object();
	
	private static final Logger LOGGER = LoggerFactory.
			getLogger(Sql4jProxyServer.class);
	
	//private final Sql4jSessionFactory FACTORY = new Sql4jSessionFactory();
	private final ExecutorService THREAD_POOL;
	{
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		THREAD_POOL = Executors.newFixedThreadPool(availableProcessors * 2);
	}
	private final ConcurrentHashMap<SocketChannel, ServerTask> MAP2 = 
			new ConcurrentHashMap<SocketChannel, ServerTask>(1024);
	
	public Sql4jProxyServer() {
		init();
	}
	
	void putTask(SocketChannel socketChannel, ServerTask task) {
		MAP2.put(socketChannel, task);
	}
	
	ServerTask getTask(SocketChannel socketChannel) {
		ServerTask task = MAP2.get(socketChannel);
		return task;
	}
	
	void removeTask(SocketChannel socketChannel) {
		ServerTask task = MAP2.remove(socketChannel);
		task.setClose(true);
	}
	
	private void init() {
		Selector selector = null;
		ServerSocketChannel serverSocketChannel = null;
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();

			int port = getPort();
			serverSocketChannel.bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			while (true) {
				System.out.println("===processIO===");
				processIO(selector);
			}
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("IO exception.", e);
		} finally {
			close(selector, serverSocketChannel);
		}
	}
	
	private int getPort() {
		/*String value = FACTORY.getProperty("port");
		int port = 0;
		try {
			port = Integer.parseInt(value);
			if (port < 0 || port > 65535) {
				throw new Exception();
			}
		} catch (Exception e) {
			throw new Sql4jException("Proxy server port configuration error. " + value, e);
		}
		return port;*/
		return 6666;
	}
	
	private int getBlockSize() {
		/*String value = FACTORY.getProperty("blockSize");
		int blockSize = 0;
		try {
			blockSize = Integer.parseInt(value);
			if (blockSize < 0) {
				throw new Exception();
			}
		} catch (Exception e) {
			blockSize = DEFAULT_BLOCK_SIZE;
			LOGGER.warn("Block size defaults: " + DEFAULT_BLOCK_SIZE + 
					". Configuration value is " + value + ".");
		}
		return blockSize;*/
		return 1024;
	}
	
	private void processIO(Selector selector) throws IOException {
		try {
			if (selector.select() == 0) {
				//return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error selecting selection key.", e);
			return;
		}
		Set<SelectionKey> set = selector.selectedKeys();
		Iterator<SelectionKey> iterator = set.iterator();
		while (iterator.hasNext()) {
			SelectionKey selectionKey = iterator.next();
			try {
				if (!selectionKey.isValid()) {
					handlingInvalid(selectionKey);
					continue;
				}
				if (selectionKey.isAcceptable()) {
					handlingAcceptable(selectionKey);
					System.out.println("handlingAcceptable");
				}
				if (selectionKey.isReadable()) {
					handlingReadable(selectionKey);
					System.out.println("handlingReadable");
				}
				if (selectionKey.isWritable()) {
					handlingWritable(selectionKey);
					System.out.println("handlingWritable");
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (selectionKey != null) {
					SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
					socketChannel.close();
					removeTask(socketChannel);
				}
			}
		}
	}
	
	private void handlingInvalid(SelectionKey selectionKey) {
		LOGGER.warn("Discovered that selection key was invalid.");
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		SocketAddress socketAddress;
		try {
			socketAddress = socketChannel.getRemoteAddress();
			LOGGER.warn("Invalid socket channel address is: " + socketAddress);
			if (selectionKey != null) {
				socketChannel.close();
				removeTask(socketChannel);
			}
		} catch (IOException e) {
			LOGGER.error("Error getting the remote address of socket channel.", e);
		}
	}
	
	private void handlingAcceptable(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		//System.out.println("socketChannel======================>"+socketChannel);
		if (socketChannel == null) {
			return;
		}
		socketChannel.configureBlocking(false);
		socketChannel.register(selectionKey.selector(), SelectionKey.OP_WRITE);
		int blockSize = getBlockSize();
		ServerTask task = new ServerTask(this, serverSocketChannel, selectionKey, socketChannel, blockSize);
		putTask(socketChannel, task);
		task.getReadableObject().pause = true;
		THREAD_POOL.execute(task);
	}
	
	private void handlingWritable(SelectionKey selectionKey) throws IOException {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		ServerTask task = getTask(socketChannel);
		//System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<handlingWritable");
		task.setWritable(true);
		task.setReadable(false);
	}
	
	private void handlingReadable(SelectionKey selectionKey) throws IOException {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		ServerTask task = getTask(socketChannel);
		task.setWritable(false);
		task.setReadable(true);
		//System.out.println("#######################"+task.over);
	}
	
	private void close(Selector selector, ServerSocketChannel serverSocketChannel) {
		try {
			if (selector != null) {
				selector.close();
			}
		} catch (Exception e) {
			LOGGER.error("Error closing selector.", e);
		}
		try {
			if (serverSocketChannel != null) {
				serverSocketChannel.close();
			}
		} catch (Exception e) {
			LOGGER.error("Error closing server socket channel.", e);
		}
	}
	
}
