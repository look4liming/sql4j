package lee.bright.sql4j.conf;

/**
 * @author Bright Lee
 */
public final class RemoteProxyServerConfiguration {
	
	private int index;
	private String ip;
	private int port;
	
	public RemoteProxyServerConfiguration() {
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

}
