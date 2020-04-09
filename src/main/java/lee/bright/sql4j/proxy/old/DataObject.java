package lee.bright.sql4j.proxy.old;

/**
 * @author Bright Lee
 */
public final class DataObject {
	
	private volatile boolean sendBlockSize;
	private volatile int blockSize;
	private volatile String writableData;
	private volatile String readableData;
	
	public DataObject(int blockSize) {
		this.blockSize = blockSize;
	}

	public boolean isSendBlockSize() {
		return sendBlockSize;
	}

	public void setSendBlockSize(boolean sendBlockSize) {
		this.sendBlockSize = sendBlockSize;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	public String getWritableData() {
		return writableData;
	}

	public void setWritableData(String writableData) {
		this.writableData = writableData;
	}

	public String getReadableData() {
		return readableData;
	}

	public void setReadableData(String readableData) {
		this.readableData = readableData;
	}

}
