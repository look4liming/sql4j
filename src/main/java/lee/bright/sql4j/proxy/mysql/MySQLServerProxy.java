package lee.bright.sql4j.proxy.mysql;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Bright Lee
 */
public final class MySQLServerProxy {
	
	private int port;
	
	public MySQLServerProxy(int port) {
		setPort(port);
	}
	
	public MySQLServerProxy() {
		this(3306);
	}
	
	public int getPort() {
		return port;
	}
	
	private void setPort(int port) {
		this.port = port;
	}
	
	public void startup() throws Exception {
		ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast("outboudHandler", new MySQLServerChannelOutboudHandler());
				ch.pipeline().addLast("encoder", new MySQLResponseEncoder());
				ch.pipeline().addLast("decoder", new MySQLRequestDecoder());
				ch.pipeline().addLast("inboundHandler", new MySQLServerChannelInboundHandler());
				System.out.println(ch.pipeline());
			}
		};
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			ServerBootstrap boot = new ServerBootstrap();
			boot.group(group);
			boot.channel(NioServerSocketChannel.class);
			boot.localAddress(getPort());
			boot.childHandler(channelInitializer);
			ChannelFuture f = boot.bind().sync();
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully().sync();
		}
	}
	
	public static void main(String[] args) throws Exception {
		MySQLServerProxy proxy = new MySQLServerProxy(6666);
		proxy.startup();
	}

}
