package lee.bright.sql4j.proxy.old;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Bright Lee
 */
public final class WritableObject {
	
	private volatile boolean writable;
	private volatile List<Object> data;
	private volatile int blockSize;
	private volatile ByteBuffer byteBuffer;
	
	public WritableObject(int blockSize) {
		this.blockSize = blockSize;
		this.byteBuffer = ByteBuffer.allocate(blockSize);
	}
	
	public boolean isWritable() {
		return writable;
	}
	
	public void setWritable(boolean writable) {
		this.writable = writable;
		System.out.println("1=writable==========================>"+this.writable);
		System.out.println("2=writable==========================>"+isWritable());
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

}
