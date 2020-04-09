package lee.bright.sql4j.ql;

import java.util.List;

import lee.bright.sql4j.Sql4jException;

/**
 * @author Bright Lee
 */
public final class QuerySpecificationSetParentStatementsForSubqueriesAnalyzer {
	
	private SourceCode sourceCode;
	private QuerySpecification querySpecification;
	
	public QuerySpecificationSetParentStatementsForSubqueriesAnalyzer(
			SourceCode sourceCode, QuerySpecification querySpecification) {
		this.sourceCode = sourceCode;
		this.querySpecification = querySpecification;
	}
	
	public void analyze() {
		analyze(querySpecification);
	}
	
	public void analyze(QuerySpecification querySpecification) {
		List<SelectSublist> selectList = querySpecification.getSelectList();
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			ValueExpression valueExpression = selectSublist.getValueExpression();
			analyze(querySpecification, valueExpression);
		}
		List<TableReference> tableReferenceList = querySpecification.getTableReferenceList();
		analyze(querySpecification, tableReferenceList);
		BooleanValueExpression whereSearchCondition = querySpecification.getWhereSearchCondition();
		if (whereSearchCondition != null) {
			analyze(querySpecification, whereSearchCondition);
		}
		List<GroupingElement> goupingElementList = querySpecification.getGroupingElementList();
		if (goupingElementList != null) {
			for (int i = 0; i < goupingElementList.size(); i++) {
				GroupingElement groupingElement = goupingElementList.get(i);
				analyze(querySpecification, groupingElement);
			}
		}
		BooleanValueExpression havingSearchCondition = querySpecification.getHavingSearchCondition();
		if (havingSearchCondition != null) {
			analyze(querySpecification, havingSearchCondition);
		}
		List<SortSpecification> sortSpecificationList = querySpecification.getSortSpecificationList();
		if (sortSpecificationList != null) {
			for (int i = 0; i < sortSpecificationList.size(); i++) {
				SortSpecification sortSpecification = sortSpecificationList.get(i);
				ValueExpression sortKey = sortSpecification.getSortKey();
				analyze(querySpecification, sortKey);
			}
		}
	}
	
	private void analyze(Statement parentStatement, SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = (QuerySpecification) selectStatement;
			querySpecification.setParentStatement(parentStatement);
			return;
		}
		if (selectStatement instanceof Union) {
			throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
					"Union statement is not supported.");
		}
		if (selectStatement instanceof Intersect) {
			throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
					"Intersect statement is not supported.");
		}
		if (selectStatement instanceof Except) {
			throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
					"Except statement is not supported.");
		}
		throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
				"This statement is not supported.");
	}
	
	private void analyze(Statement parentStatement, ValueExpression valueExpression) {
		if (valueExpression instanceof NameChain) {
			return;
		}
		if (valueExpression instanceof BooleanValueExpression) {
			BooleanValueExpression booleanValueExpression = 
					(BooleanValueExpression) valueExpression;
			analyze(parentStatement, booleanValueExpression);
			return;
		}
		if (valueExpression instanceof AbsoluteValueExpression) {
			AbsoluteValueExpression absoluteValueExpression = 
					(AbsoluteValueExpression) valueExpression;
			ValueExpression valueExpression_ = absoluteValueExpression.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof Addition) {
			Addition addition = (Addition) valueExpression;
			ValueExpression left = addition.getLeft();
			analyze(parentStatement, left);
			ValueExpression right = addition.getRight();
			analyze(parentStatement, right);
			return;
		}
		if (valueExpression instanceof Any) {
			Any any = (Any) valueExpression;
			ValueExpression valueExpression_ = any.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof Avg) {
			Avg avg = (Avg) valueExpression;
			ValueExpression valueExpression_ = avg.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof CharLengthExpression) {
			CharLengthExpression charLengthExpression = (CharLengthExpression) valueExpression;
			ValueExpression valueExpression_ = charLengthExpression.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof CardinalityExpression) {
			CardinalityExpression cardinalityExpression = 
					(CardinalityExpression) valueExpression;
			ValueExpression valueExpression_ = cardinalityExpression.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof Coalesce) {
			Coalesce coalesce = (Coalesce) valueExpression;
			List<ValueExpression> arguments = coalesce.getArguments();
			for (int i = 0; i < arguments.size(); i++) {
				ValueExpression argument = arguments.get(i);
				analyze(parentStatement, argument);
			}
			return;
		}
		if (valueExpression instanceof Concatenation) {
			Concatenation concatenation = (Concatenation) valueExpression;
			ValueExpression left = concatenation.getLeft();
			analyze(parentStatement, left);
			ValueExpression right = concatenation.getRight();
			analyze(parentStatement, right);
			return;
		}
		if (valueExpression instanceof Count) {
			Count count = (Count) valueExpression;
			ValueExpression valueExpression_ = count.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof CurrentDate) {
			return;
		}
		if (valueExpression instanceof CurrentTime) {
			return;
		}
		if (valueExpression instanceof CurrentTimestamp) {
			return;
		}
		if (valueExpression instanceof DateLiteral) {
			return;
		}
		if (valueExpression instanceof Division) {
			Division division = (Division) valueExpression;
			ValueExpression left = division.getLeft();
			analyze(parentStatement, left);
			ValueExpression right = division.getRight();
			analyze(parentStatement, right);
			return;
		}
		if (valueExpression instanceof Every) {
			Every every = (Every) valueExpression;
			ValueExpression valueExpression_ = every.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof ExtractExpression) {
			ExtractExpression extractExpression = (ExtractExpression) valueExpression;
			ValueExpression extractSource = extractExpression.getExtractSource();
			analyze(parentStatement, extractSource);
			return;
		}
		if (valueExpression instanceof FunctionInvocation) {
			FunctionInvocation functionInvocation = (FunctionInvocation) valueExpression;
			List<ValueExpression> arguments = functionInvocation.getArguments();
			for (int i = 0; i < arguments.size(); i++) {
				ValueExpression argument = arguments.get(i);
				analyze(parentStatement, argument);
			}
			return;
		}
		if (valueExpression instanceof Grouping) {
			return;
		}
		if (valueExpression instanceof Lower) {
			Lower lower = (Lower) valueExpression;
			ValueExpression valueExpression_ = lower.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof Max) {
			Max max = (Max) valueExpression;
			ValueExpression valueExpression_ = max.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof Min) {
			Min min = (Min) valueExpression;
			ValueExpression valueExpression_ = min.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof ModulusExpression) {
			ModulusExpression modulusExpression = (ModulusExpression) valueExpression;
			ValueExpression dividend = modulusExpression.getDividend();
			analyze(parentStatement, dividend);
			ValueExpression divisor = modulusExpression.getDivisor();
			analyze(parentStatement, divisor);
			return;
		}
		if (valueExpression instanceof Multiplication) {
			Multiplication multiplication = (Multiplication) valueExpression;
			ValueExpression left = multiplication.getLeft();
			analyze(parentStatement, left);
			ValueExpression right = multiplication.getRight();
			analyze(parentStatement, right);
			return;
		}
		if (valueExpression instanceof NegativeExpression) {
			NegativeExpression negativeExpression = (NegativeExpression) valueExpression;
			ValueExpression valueExpression_ = negativeExpression.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof NullIf) {
			NullIf nullIf = (NullIf) valueExpression;
			ValueExpression first = nullIf.getFirst();
			analyze(parentStatement, first);
			ValueExpression second = nullIf.getSecond();
			analyze(parentStatement, second);
			return;
		}
		if (valueExpression instanceof NumericLiteral) {
			return;
		}
		if (valueExpression instanceof Parameter) {
			return;
		}
		if (valueExpression instanceof PositionExpression) {
			PositionExpression positionExpression = (PositionExpression) valueExpression;
			ValueExpression valueExpression1 = positionExpression.getValueExpression1();
			analyze(parentStatement, valueExpression1);
			ValueExpression valueExpression2 = positionExpression.getValueExpression2();
			analyze(parentStatement, valueExpression2);
			return;
		}
		if (valueExpression instanceof PositiveExpression) {
			PositiveExpression positiveExpression = (PositiveExpression) valueExpression;
			ValueExpression valueExpression_ = positiveExpression.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof SearchedCase) {
			SearchedCase searchedCase = (SearchedCase) valueExpression;
			List<SearchedWhenClause> searchedWhenClauseList = searchedCase.getSearchedWhenClauseList();
			for (int i = 0; i < searchedWhenClauseList.size(); i++) {
				SearchedWhenClause searchedWhenClause = searchedWhenClauseList.get(i);
				BooleanValueExpression searchedCondition = searchedWhenClause.getSearchedCondition();
				analyze(parentStatement, searchedCondition);
				ValueExpression result = searchedWhenClause.getResult();
				analyze(parentStatement, result);
			}
			ElseClause elseClause = searchedCase.getElseClause();
			ValueExpression result = elseClause.getResult();
			analyze(parentStatement, result);
			return;
		}
		if (valueExpression instanceof SimpleCase) {
			SimpleCase simpleCase = (SimpleCase) valueExpression;
			ValueExpression caseOperand = simpleCase.getCaseOperand();
			analyze(parentStatement, caseOperand);
			List<SimpleWhenClause> simpleWhenClauseList = simpleCase.getSimpleWhenClauseList();
			for (int i = 0; i < simpleWhenClauseList.size(); i++) {
				SimpleWhenClause simpleWhenClause = simpleWhenClauseList.get(i);
				ValueExpression whenOperand = simpleWhenClause.getWhenOperand();
				analyze(parentStatement, whenOperand);
				ValueExpression result = simpleWhenClause.getResult();
				analyze(parentStatement, result);
			}
			ElseClause elseClause = simpleCase.getElseClause();
			ValueExpression result = elseClause.getResult();
			analyze(parentStatement, result);
			return;
		}
		if (valueExpression instanceof Some) {
			Some some = (Some) valueExpression;
			ValueExpression valueExpression_ = some.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof StringLiteral) {
			return;
		}
		if (valueExpression instanceof Subquery) {
			Subquery subquery = (Subquery) valueExpression;
			SelectStatement selectStatement = subquery.getSelectStatement();
			selectStatement.setParentStatement(parentStatement);
			return;
		}
		if (valueExpression instanceof ToDate) {
			ToDate toDate = (ToDate) valueExpression;
			ValueExpression valueExpression_ = toDate.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof ToChar) {
			ToChar toChar = (ToChar) valueExpression;
			ValueExpression valueExpression_ = toChar.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof Substring) {
			Substring substring = (Substring) valueExpression;
			ValueExpression valueExpression_ = substring.getValueExpression();
			analyze(parentStatement, valueExpression_);
			ValueExpression startPosition = substring.getStartPosition();
			analyze(parentStatement, startPosition);
			ValueExpression stringLength = substring.getStringLength();
			if (stringLength != null) {
				analyze(parentStatement, stringLength);
			}
			return;
		}
		if (valueExpression instanceof Subtraction) {
			Subtraction subtraction = (Subtraction) valueExpression;
			ValueExpression left = subtraction.getLeft();
			analyze(parentStatement, left);
			ValueExpression right = subtraction.getRight();
			analyze(parentStatement, right);
			return;
		}
		if (valueExpression instanceof Sum) {
			Sum sum = (Sum) valueExpression;
			ValueExpression valueExpression_ = sum.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof TimeLiteral) {
			return;
		}
		if (valueExpression instanceof TimestampLiteral) {
			return;
		}
		if (valueExpression instanceof Trim) {
			Trim trim = (Trim) valueExpression;
			ValueExpression trimCharacter = trim.getTrimCharacter();
			if (trimCharacter != null) {
				analyze(parentStatement, trimCharacter);
			}
			ValueExpression trimSource = trim.getTrimSource();
			analyze(parentStatement, trimSource);
			return;
		}
		if (valueExpression instanceof Upper) {
			Upper upper = (Upper) valueExpression;
			ValueExpression valueExpression_ = upper.getValueExpression();
			analyze(parentStatement, valueExpression_);
			return;
		}
		if (valueExpression instanceof BitLengthExpression) {
			throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
					"Bit length expression is not supported.");
		}
		if (valueExpression instanceof OctetLengthExpression) {
			throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
					"Octet length expression is not supported.");
		}
		throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
				"This value exrepssion is not supported.");
	}
	
	private void analyze(Statement parentStatement, List<TableReference> tableReferenceList) {
		if (tableReferenceList.size() > 1) {
			TableReference tableReference = tableReferenceList.get(0);
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"',' join is not supported.");
		}
		TableReference tableReference = tableReferenceList.get(0);
		analyze(parentStatement, tableReference);
	}
	
	private void analyze(Statement parentStatement, TableReference tableReference) {
		if (tableReference instanceof TablePrimary) {
			return;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			TableReference left = leftOuterJoin.getLeft();
			analyze(parentStatement, left);
			TableReference right = leftOuterJoin.getRight();
			analyze(parentStatement, right);
			BooleanValueExpression joinCondition = leftOuterJoin.getJoinCondition();
			analyze(parentStatement, joinCondition);
			return;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			SelectStatement selectStatement = derivedTable.getSelectStatement();
			analyze(parentStatement, selectStatement);
			return;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			analyze(parentStatement, left);
			TableReference right = rightOuterJoin.getRight();
			analyze(parentStatement, right);
			BooleanValueExpression joinCondition = rightOuterJoin.getJoinCondition();
			analyze(parentStatement, joinCondition);
			return;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			analyze(parentStatement, left);
			TableReference right = innerJoin.getRight();
			analyze(parentStatement, right);
			BooleanValueExpression joinCondition = innerJoin.getJoinCondition();
			analyze(parentStatement, joinCondition);
			return;
		}
		if (tableReference instanceof FullOuterJoin) {
			FullOuterJoin fullOuterJoin = (FullOuterJoin) tableReference;
			TableReference left = fullOuterJoin.getLeft();
			analyze(parentStatement, left);
			TableReference right = fullOuterJoin.getRight();
			analyze(parentStatement, right);
			BooleanValueExpression joinCondition = fullOuterJoin.getJoinCondition();
			analyze(parentStatement, joinCondition);
			return;
		}
		if (tableReference instanceof CrossJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Cross join is not supported.");
		}
		if (tableReference instanceof NaturalInnerJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural inner join is not supported.");
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural left outer join is not supported.");
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural right outer join is not supported.");
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Natural full outer join is not supported.");
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"This table reference is not supported.");
	}
	
	private void analyze(Statement parentStatement, BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue) booleanValueExpression;
			analyze(parentStatement, booleanValue);
			return;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = (Predicate) booleanValueExpression;
			analyze(parentStatement, predicate);
			return;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = (BooleanFactor) booleanValueExpression;
			analyze(parentStatement, booleanFactor);
			return;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			analyze(parentStatement, booleanTerm);
			return;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			analyze(parentStatement, booleanTest);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, booleanValueExpression.getBeginIndex(), 
				"This boolean value exrepssion is not supported.");
	}
	
	private void analyze(Statement parentStatement, BooleanValue booleanValue) {
		for (int i = 0; i < booleanValue.size(); i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			analyze(parentStatement, booleanTerm);
		}
	}
	
	private void analyze(Statement parentStatement, Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = (ComparisonPredicate) predicate;
			ValueExpression left = comparisonPredicate.getLeft();
			analyze(parentStatement, left);
			ValueExpression right = comparisonPredicate.getRight();
			analyze(parentStatement, right);
			return;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = (BetweenPredicate) predicate;
			ValueExpression valueExpression = betweenPredicate.getValueExpression();
			analyze(parentStatement, valueExpression);
			ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
			analyze(parentStatement, valueExpression1);
			ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
			analyze(parentStatement, valueExpression2);
			return;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = (ExistsPredicate) predicate;
			Subquery subquery = existsPredicate.getSubquery();
			analyze(parentStatement, subquery);
			return;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = (InPredicate) predicate;
			ValueExpression valueExpression = inPredicate.getValueExpression();
			analyze(parentStatement, valueExpression);
			List<ValueExpression> inValueList = inPredicate.getInValueList();
			if (inValueList != null) {
				for (int i = 0; i < inValueList.size(); i++) {
					ValueExpression valueExpression_ = inValueList.get(i);
					analyze(parentStatement, valueExpression_);
				}
				return;
			}
			Subquery subquery = inPredicate.getSubquery();
			if (subquery != null) {
				analyze(parentStatement, subquery);
			}
			return;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = (LikePredicate) predicate;
			ValueExpression valueExpression = likePredicate.getValueExpression();
			analyze(parentStatement, valueExpression);
			ValueExpression characterPattern = likePredicate.getCharacterPattern();
			analyze(parentStatement, characterPattern);
			ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
			analyze(parentStatement, escapeCharacter);
			return;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = (NullPredicate) predicate;
			ValueExpression valueExpression = nullPredicate.getValueExpression();
			analyze(parentStatement, valueExpression);
			return;
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
	
	private void analyze(Statement parentStatement, BooleanTerm booleanTerm) {
		for (int i = 0; i < booleanTerm.size(); i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			analyze(parentStatement, booleanFactor);
		}
	}
	
	private void analyze(Statement parentStatement, BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = booleanFactor.getBooleanValueExpression();
		analyze(parentStatement, booleanValueExpression);
	}
	
	private void analyze(Statement parentStatement, BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = booleanTest.getBooleanValueExpression();
		analyze(parentStatement, booleanValueExpression);
	}
	
	private void analyze(Statement parentStatement, GroupingElement groupingElement) {
		if (groupingElement instanceof OrdinaryGroupingSet) {
			OrdinaryGroupingSet ordinaryGroupingSet = (OrdinaryGroupingSet) groupingElement;
			for (int i = 0; i < ordinaryGroupingSet.size(); i++) {
				GroupingColumnReference groupingColumnReference = ordinaryGroupingSet.get(i);
				analyze(parentStatement, groupingColumnReference);
			}
			return;
		}
		if (groupingElement instanceof CubeList) {
			CubeList cubeList = (CubeList) groupingElement;
			for (int i = 0; i < cubeList.size(); i++) {
				GroupingColumnReference groupingColumnReference = cubeList.get(i);
				analyze(parentStatement, groupingColumnReference);
			}
			return;
		}
		if (groupingElement instanceof RollupList) {
			RollupList rollupList = (RollupList) groupingElement;
			for (int i = 0; i < rollupList.size(); i++) {
				GroupingColumnReference groupingColumnReference = rollupList.get(i);
				analyze(parentStatement, groupingColumnReference);
			}
			return;
		}
		if (groupingElement instanceof GroupingSetsSpecification) {
			GroupingSetsSpecification groupingSetsSpecification = 
					(GroupingSetsSpecification) groupingElement;
			for (int i = 0; i < groupingSetsSpecification.size(); i++) {
				GroupingElement groupingElement_ = groupingSetsSpecification.get(i);
				analyze(parentStatement, groupingElement_);
			}
			return;
		}
		if (groupingElement instanceof GrandTotal) {
			throw Sql4jException.getSql4jException(sourceCode, groupingElement.getBeginIndex(), 
					"Grand total is not supported.");
		}
		throw Sql4jException.getSql4jException(sourceCode, groupingElement.getBeginIndex(), 
				"This grouping element is not supported.");
	}
	
	private void analyze(Statement parentStatement, GroupingColumnReference groupingColumnReference) {
		NameChain collationName = groupingColumnReference.getCollationName();
		if (collationName != null) {
			throw Sql4jException.getSql4jException(sourceCode, collationName.getBeginIndex(), 
					"Collation name is not supported.");
		}
	}
	
}
