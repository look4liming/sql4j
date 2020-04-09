package lee.bright.sql4j.proxy.mysql;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author Bright Lee
 */
public final class MySQLRequestDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int bytes = in.readableBytes();
		byte[] bs = new byte[bytes];
		in.readBytes(bs);
		String msg = new String(bs, "UTF-8");
		out.add(msg);
		System.out.println("MySQLRequestDecoder.decode");
		// TODO
	}

}
