package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public final class SortSpecification {
	
	private ValueExpression sortKey;
	private OrderingSpecification orderingSpecification;
	
	public SortSpecification(ValueExpression sortKey,
			OrderingSpecification orderingSpecification) {
		this.sortKey = sortKey;
		this.orderingSpecification = orderingSpecification;
	}
	
	public ValueExpression getSortKey() {
		return sortKey;
	}
	
	public OrderingSpecification getOrderingSpecification() {
		return orderingSpecification;
	}

}
