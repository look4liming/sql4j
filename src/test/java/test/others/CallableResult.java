package test.others;

public class CallableResult {
	
	private String pOut;
	private String pInOut;
	private String pIn;
	
	public CallableResult() {
	}

	public String getpOut() {
		return pOut;
	}

	public void setpOut(String pOut) {
		this.pOut = pOut;
	}

	public String getpInOut() {
		return pInOut;
	}

	public void setpInOut(String pInOut) {
		this.pInOut = pInOut;
	}
	
	@Override
	public String toString() {
		String s = "CallableResult[pOut="+pOut+",pInOut="+pInOut+",pIn="+pIn+"]";
		return s;
	}

	public String getpIn() {
		return pIn;
	}

	public void setpIn(String pIn) {
		this.pIn = pIn;
	}

}
