package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class Page {

	private int beginIndex;
	
	private NumericLiteral fromNumericLiteral;
	private Parameter fromParameter;
	private NumericLiteral toNumericLiteral;
	private Parameter toParameter;
	
	private NumericLiteral capacityNumericLiteral;
	private Parameter capacityParameter;
	private NumericLiteral indexNumericLiteral;
	private Parameter indexParameter;
	
	public Page(int beginIndex, NumericLiteral fromNumericLiteral, Parameter fromParameter,	
			NumericLiteral toNumericLiteral, Parameter toParameter, 
			NumericLiteral capacityNumericLiteral, Parameter capacityParameter, 
			NumericLiteral indexNumericLiteral, Parameter indexParameter) {
		this.beginIndex = beginIndex;
		this.fromNumericLiteral = fromNumericLiteral;
		this.fromParameter = fromParameter;
		this.toNumericLiteral = toNumericLiteral;
		this.toParameter = toParameter;
		this.capacityNumericLiteral = capacityNumericLiteral;
		this.capacityParameter = capacityParameter;
		this.indexNumericLiteral = indexNumericLiteral;
		this.indexParameter = indexParameter;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}

	public NumericLiteral getFromNumericLiteral() {
		return fromNumericLiteral;
	}

	public Parameter getFromParameter() {
		return fromParameter;
	}

	public NumericLiteral getToNumericLiteral() {
		return toNumericLiteral;
	}

	public Parameter getToParameter() {
		return toParameter;
	}

	public NumericLiteral getCapacityNumericLiteral() {
		return capacityNumericLiteral;
	}

	public Parameter getCapacityParameter() {
		return capacityParameter;
	}

	public NumericLiteral getIndexNumericLiteral() {
		return indexNumericLiteral;
	}

	public Parameter getIndexParameter() {
		return indexParameter;
	}

}
