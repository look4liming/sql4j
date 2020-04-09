package lee.bright.sql4j.ql;

import java.math.BigInteger;

/**
 * @author Bright Lee
 */
public final class PageInformation {
	
	private BigInteger beginIndex;
	private BigInteger endIndex;
	
	public PageInformation(BigInteger beginIndex, BigInteger endIndex) {
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}
	
	public BigInteger getBeginIndex() {
		return beginIndex;
	}
	
	public BigInteger getEndIndex() {
		return endIndex;
	}

}
