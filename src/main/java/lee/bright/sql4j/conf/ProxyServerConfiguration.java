package lee.bright.sql4j.conf;

/**
 * @author Bright Lee
 */
public final class ProxyServerConfiguration {
	
	private int port = 6666;
	private int blockSize = 1024;
	
	public ProxyServerConfiguration() {
	}
	
	public void clear() {
		port = 6666;
		blockSize = 1024;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getBlockSize() {
		return blockSize;
	}
	
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

}
