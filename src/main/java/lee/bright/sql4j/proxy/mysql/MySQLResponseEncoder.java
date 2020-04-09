package lee.bright.sql4j.proxy.mysql;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Bright Lee
 */
public final class MySQLResponseEncoder extends MessageToByteEncoder<String> {

	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
		byte[] bs = msg.getBytes("UTF-8");
		out.writeBytes(bs);
		System.out.println("MySQLResponseEncoder.encode");
	}

}
