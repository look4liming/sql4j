package lee.bright.sql4j.proxy.old;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bright Lee
 */
public final class ReadableObject {
	
	private static final Logger LOGGER = LoggerFactory.
			getLogger(ReadableObject.class);
	
	private volatile boolean readable;
	private volatile List<Object> data;
	private volatile ByteBuffer byteBuffer;
	private volatile ByteArrayOutputStream out = new ByteArrayOutputStream();

	public volatile boolean pause = false;
	
	public ReadableObject(int blockSize) {
		byteBuffer = ByteBuffer.allocate(blockSize);
	}
	
	public boolean isReadable() {
		return readable;
	}
	
	public void setReadable(boolean readable) {
		this.readable = readable;
		//System.out.println("1=readable==========================>"+this.readable);
		//System.out.println("2=readable==========================>"+isReadable());
		this.pause = false;
	}
	
	public List<Object> getData() {
		return data;
	}
	
	public void setData(List<Object> data) {
		this.data = data;
	}
	
	public ByteBuffer getByteBuffer() {
		return byteBuffer;
	}
	
	public ByteArrayOutputStream getOut() {
		return out;
	}
	
	public void clearOut() {
		try {
			out.close();
		} catch (IOException e) {
			LOGGER.error("Close byte array output stream error.", e);
		}
		out = new ByteArrayOutputStream();
	}

}
