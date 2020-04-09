package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.Sql4jException;

/**
 * @author Bright Lee
 */
public final class BooleanValueExpressionUtil {
	
	public static List<List<BooleanValueExpression>> getAndPathList(SourceCode sourceCode, 
			BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = 
					(BooleanValue) booleanValueExpression;
			List<List<BooleanValueExpression>> andPathList = 
					getAndPathList(sourceCode, booleanValue);
			return andPathList;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = (Predicate) booleanValueExpression;
			List<List<BooleanValueExpression>> andPathList = 
					getAndPathList(sourceCode, predicate);
			return andPathList;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = (BooleanFactor) booleanValueExpression;
			List<List<BooleanValueExpression>> andPathList = 
					getAndPathList(sourceCode, booleanFactor);
			return andPathList;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			List<List<BooleanValueExpression>> andPathList = 
					getAndPathList(sourceCode, booleanTest);
			return andPathList;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			List<List<BooleanValueExpression>> andPathList = 
					getAndPathList(sourceCode, booleanTerm);
			return andPathList;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				booleanValueExpression.getBeginIndex(), 
				"Not support the boolean value exrepssion.");
	}
	
	public static List<List<BooleanValueExpression>> getAndPathList(SourceCode sourceCode, 
			BooleanValue booleanValue) {
		List<List<BooleanValueExpression>> andPathList = 
				new ArrayList<List<BooleanValueExpression>>(booleanValue.size());
		for (int i = 0; i < booleanValue.size(); i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			List<List<BooleanValueExpression>> list = getAndPathList(sourceCode, booleanTerm);
			andPathList.addAll(list);
		}
		return andPathList;
	}
	
	public static List<List<BooleanValueExpression>> getAndPathList(SourceCode sourceCode, 
			BooleanTerm booleanTerm) {
		List<List<BooleanValueExpression>> andPathList = 
				new ArrayList<List<BooleanValueExpression>>(booleanTerm.size());
		for (int i = 0; i < booleanTerm.size(); i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			List<List<BooleanValueExpression>> list2 = getAndPathList(sourceCode, booleanFactor);
			if (andPathList.isEmpty()) {
				andPathList.addAll(list2);
				continue;
			}
			List<List<BooleanValueExpression>> list1 = 
					new ArrayList<List<BooleanValueExpression>>(andPathList.size() * list2.size());
			list1.addAll(andPathList);
			andPathList.clear();
			for (int j = 0; j < list1.size(); j++) {
				List<BooleanValueExpression> list_j = list1.get(j);
				for (int k = 0; k < list2.size(); k++) {
					List<BooleanValueExpression> list_k = list2.get(k);
					List<BooleanValueExpression> list = new ArrayList<BooleanValueExpression>(
							list_j.size() + list_k.size());
					list.addAll(list_j);
					list.addAll(list_k);
					andPathList.add(list);
				}
			}
		}
		return andPathList;
	}
	
	public static List<List<BooleanValueExpression>> getAndPathList(SourceCode sourceCode, 
			BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = booleanFactor.getBooleanValueExpression();
		List<List<BooleanValueExpression>> andPathList = getAndPathList(sourceCode, booleanValueExpression);
		boolean not = booleanFactor.getNot();
		if (not == false) {
			return andPathList;
		}
		int beginIndex = booleanFactor.getBeginIndex();
		int endIndex = booleanFactor.getEndIndex();
		JdbcType jdbcType = booleanFactor.getDataType();
		for (int i = 0; i < andPathList.size(); i++) {
			List<BooleanValueExpression> andPath = andPathList.get(i);
			for (int j = 0; j < andPath.size(); j++) {
				BooleanValueExpression expression = andPath.get(j);
				BooleanFactor factor = new BooleanFactor(beginIndex, endIndex, not, expression);
				factor.setDataType(jdbcType);
				andPath.set(j, factor);
			}
		}
		return andPathList;
	}
	
	public static List<List<BooleanValueExpression>> getAndPathList(SourceCode sourceCode, 
			BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = booleanTest.getBooleanValueExpression();
		List<List<BooleanValueExpression>> andPathList = getAndPathList(sourceCode, booleanValueExpression);
		TruthValue truthValue = booleanTest.getTruthValue();
		if (truthValue == null) {
			return andPathList;
		}
		boolean not = booleanTest.getNot();
		if (not == false && truthValue == TruthValue.TRUE) {
			return andPathList;
		}
		int beginIndex = booleanTest.getBeginIndex();
		int endIndex = booleanTest.getEndIndex();
		JdbcType jdbcType = booleanTest.getDataType();
		for (int i = 0; i < andPathList.size(); i++) {
			List<BooleanValueExpression> andPath = andPathList.get(i);
			for (int j = 0; j < andPath.size(); j++) {
				BooleanValueExpression expression = andPath.get(j);
				BooleanTest test = new BooleanTest(beginIndex, endIndex, expression, not, truthValue);
				test.setDataType(jdbcType);
				andPath.set(j, test);
			}
		}
		return andPathList;
	}
	
	public static List<List<BooleanValueExpression>> getAndPathList(SourceCode sourceCode, 
			Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = (ComparisonPredicate) predicate;
			List<BooleanValueExpression> andPath = new ArrayList<BooleanValueExpression>(1);
			andPath.add(comparisonPredicate);
			List<List<BooleanValueExpression>> andPathList = new ArrayList<List<BooleanValueExpression>>(1);
			andPathList.add(andPath);
			return andPathList;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = (BetweenPredicate) predicate;
			List<BooleanValueExpression> andPath = new ArrayList<BooleanValueExpression>(1);
			andPath.add(betweenPredicate);
			List<List<BooleanValueExpression>> andPathList = new ArrayList<List<BooleanValueExpression>>(1);
			andPathList.add(andPath);
			return andPathList;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = (ExistsPredicate) predicate;
			// TODO
			List<BooleanValueExpression> andPath = new ArrayList<BooleanValueExpression>(1);
			andPath.add(existsPredicate);
			List<List<BooleanValueExpression>> andPathList = new ArrayList<List<BooleanValueExpression>>(1);
			andPathList.add(andPath);
			return andPathList;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = (InPredicate) predicate;
			List<BooleanValueExpression> andPath = new ArrayList<BooleanValueExpression>(1);
			andPath.add(inPredicate);
			List<List<BooleanValueExpression>> andPathList = new ArrayList<List<BooleanValueExpression>>(1);
			andPathList.add(andPath);
			return andPathList;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = (LikePredicate) predicate;
			List<BooleanValueExpression> andPath = new ArrayList<BooleanValueExpression>(1);
			andPath.add(likePredicate);
			List<List<BooleanValueExpression>> andPathList = new ArrayList<List<BooleanValueExpression>>(1);
			andPathList.add(andPath);
			return andPathList;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = (NullPredicate) predicate;
			List<BooleanValueExpression> andPath = new ArrayList<BooleanValueExpression>(1);
			andPath.add(nullPredicate);
			List<List<BooleanValueExpression>> andPathList = new ArrayList<List<BooleanValueExpression>>(1);
			andPathList.add(andPath);
			return andPathList;
		}
		if (predicate instanceof DistinctPredicate) {
			throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
					"Distinct predicate is not supported.");
		}
		if (predicate instanceof MatchPredicate) {
			throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
					"Match predicate is not supported.");
		}
		if (predicate instanceof OverlapsPredicate) {
			throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
					"Overlaps predicate is not supported.");
		}
		if (predicate instanceof SimilarPredicate) {
			throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
					"Similar predicate is not supported.");
		}
		if (predicate instanceof UniquePredicate) {
			throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
					"Unique predicate is not supported.");
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"This predicate is not supported.");
	}

}
