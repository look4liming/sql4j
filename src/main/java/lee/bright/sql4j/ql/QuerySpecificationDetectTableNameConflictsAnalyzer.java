package lee.bright.sql4j.ql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lee.bright.sql4j.Sql4jException;

/**
 * @author Bright Lee
 */
public final class QuerySpecificationDetectTableNameConflictsAnalyzer {
	
	private SourceCode sourceCode;
	private QuerySpecification querySpecification;
	
	public QuerySpecificationDetectTableNameConflictsAnalyzer(
			SourceCode sourceCode, QuerySpecification querySpecification) {
		this.sourceCode = sourceCode;
		this.querySpecification = querySpecification;
	}
	
	public void analyze() {
		analyze(querySpecification);
	}
	
	private void analyze(SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = 
					(QuerySpecification) selectStatement;
			analyze(querySpecification);
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
	
	private void analyze(QuerySpecification querySpecification) {
		List<SelectSublist> selectList = querySpecification.getSelectList();
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			ValueExpression valueExpression = 
					selectSublist.getValueExpression();
			analyze(valueExpression);
		}
		List<TableReference> tableReferenceList = 
				querySpecification.getTableReferenceList();
		analyzeTableReferenceList(tableReferenceList);
		BooleanValueExpression whereSearchCondition = 
				querySpecification.getWhereSearchCondition();
		analyze(whereSearchCondition);
		List<GroupingElement> groupingElementList = 
				querySpecification.getGroupingElementList();
		analyze(groupingElementList);
		BooleanValueExpression havingSearchCondition = 
				querySpecification.getHavingSearchCondition();
		analyze(havingSearchCondition);
		List<SortSpecification> sortSpecificationList = 
				querySpecification.getSortSpecificationList();
		analyzeSortSpecificationList(sortSpecificationList);
	}
	
	private void analyzeTableReferenceList(List<TableReference> tableReferenceList) {
		Map<String, String> tableNameMap = new HashMap<String, String>();
		for (int i = 0; i < tableReferenceList.size(); i++) {
			TableReference tableReference = tableReferenceList.get(i);
			analyze(tableNameMap, tableReference);
		}
	}
	
	private void analyze(Map<String, String> tableNameMap, 
			TableReference tableReference) {
		if (tableReference instanceof TablePrimary) {
			TablePrimary tablePrimary = (TablePrimary) tableReference;
			String tableName;
			Name name = tablePrimary.getCorrelationName();
			if (name != null) {
				tableName = name.getContent().toLowerCase();
			} else {
				NameChain nameChain = tablePrimary.getTableName();
				name = nameChain.get(nameChain.size() - 1);
				tableName = name.getContent().toLowerCase();
			}
			if (tableNameMap.containsKey(tableName)) {
				throw Sql4jException.getSql4jException(sourceCode, 
						name.getBeginIndex(), "Duplicate table name.");
			}
			tableNameMap.put(tableName, tableName);
			return;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			TableReference left = leftOuterJoin.getLeft();
			analyze(tableNameMap, left);
			TableReference right = leftOuterJoin.getRight();
			analyze(tableNameMap, right);
			BooleanValueExpression joinCondition = leftOuterJoin.getJoinCondition();
			analyze(joinCondition);
			return;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			Name correlationName = derivedTable.getCorrelationName();
			String tableName = correlationName.getContent().toLowerCase();
			if (tableNameMap.containsKey(tableName)) {
				throw Sql4jException.getSql4jException(sourceCode, 
						correlationName.getBeginIndex(), 
						"Duplicate table name.");
			}
			tableNameMap.put(tableName, tableName);
			SelectStatement subquery = derivedTable.getSelectStatement();
			analyze(subquery);
			return;
		}
		if (tableReference instanceof CrossJoin) {
			CrossJoin crossJoin = (CrossJoin) tableReference;
			TableReference left = crossJoin.getLeft();
			analyze(tableNameMap, left);
			TableReference right = crossJoin.getRight();
			analyze(tableNameMap, right);
			return;
		}
		if (tableReference instanceof FullOuterJoin) {
			FullOuterJoin fullOuterJoin = (FullOuterJoin) tableReference;
			TableReference left = fullOuterJoin.getLeft();
			analyze(tableNameMap, left);
			TableReference right = fullOuterJoin.getRight();
			analyze(tableNameMap, right);
			BooleanValueExpression joinCondition = fullOuterJoin.getJoinCondition();
			analyze(joinCondition);
			return;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			analyze(tableNameMap, left);
			TableReference right = innerJoin.getRight();
			analyze(tableNameMap, right);
			BooleanValueExpression joinCondition = innerJoin.getJoinCondition();
			analyze(joinCondition);
			return;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			analyze(tableNameMap, left);
			TableReference right = rightOuterJoin.getRight();
			analyze(tableNameMap, right);
			BooleanValueExpression joinCondition = rightOuterJoin.getJoinCondition();
			analyze(joinCondition);
			return;
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			NaturalFullOuterJoin naturalFullOuterJoin = (NaturalFullOuterJoin) tableReference;
			TableReference left = naturalFullOuterJoin.getLeft();
			analyze(tableNameMap, left);
			TableReference right = naturalFullOuterJoin.getRight();
			analyze(tableNameMap, right);
			return;
		}
		if (tableReference instanceof NaturalInnerJoin) {
			NaturalInnerJoin naturalInnerJoin = (NaturalInnerJoin) tableReference;
			TableReference left = naturalInnerJoin.getLeft();
			analyze(tableNameMap, left);
			TableReference right = naturalInnerJoin.getRight();
			analyze(tableNameMap, right);
			return;
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			NaturalLeftOuterJoin naturalLeftOuterJoin = (NaturalLeftOuterJoin) tableReference;
			TableReference left = naturalLeftOuterJoin.getLeft();
			analyze(tableNameMap, left);
			TableReference right = naturalLeftOuterJoin.getRight();
			analyze(tableNameMap, right);
			return;
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			NaturalRightOuterJoin naturalRightOuterJoin = (NaturalRightOuterJoin) tableReference;
			TableReference left = naturalRightOuterJoin.getLeft();
			analyze(tableNameMap, left);
			TableReference right = naturalRightOuterJoin.getRight();
			analyze(tableNameMap, right);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"The table reference is not supported.");
	}
	
	private void analyze(List<GroupingElement> groupingElementList) {
		if (groupingElementList == null) {
			return;
		}
		for (int i = 0; i < groupingElementList.size(); i++) {
			GroupingElement groupingElement = groupingElementList.get(i);
			analyze(groupingElement);
		}
	}
	
	private void analyze(GroupingElement groupingElement) {
		if (groupingElement instanceof CubeList) {
			CubeList cubeList = (CubeList) groupingElement;
			analyze(cubeList);
			return;
		}
		if (groupingElement instanceof GrandTotal) {
			GrandTotal grandTotal = (GrandTotal) groupingElement;
			analyze(grandTotal);
			return;
		}
		if (groupingElement instanceof GroupingSetsSpecification) {
			GroupingSetsSpecification groupingSetsSpecification = 
					(GroupingSetsSpecification) groupingElement;
			analyze(groupingSetsSpecification);
			return;
		}
		if (groupingElement instanceof OrdinaryGroupingSet) {
			OrdinaryGroupingSet ordinaryGroupingSet = (OrdinaryGroupingSet) groupingElement;
			analyze(ordinaryGroupingSet);
			return;
		}
		if (groupingElement instanceof RollupList) {
			RollupList rollupList = (RollupList) groupingElement;
			analyze(rollupList);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, groupingElement.getBeginIndex(), 
				"The grouping element is not supported.");
	}
	
	private void analyze(CubeList cubeList) {
		for (int i = 0; i < cubeList.size(); i++) {
			GroupingColumnReference groupingColumnReference = cubeList.get(i);
			analyze(groupingColumnReference);
		}
	}
	
	private void analyze(GroupingColumnReference groupingColumnReference) {
	}
	
	private void analyze(GrandTotal grandTotal) {
		throw Sql4jException.getSql4jException(sourceCode, grandTotal.getBeginIndex(), 
				"Grand total is not supported.");
	}
	
	private void analyze(GroupingSetsSpecification groupingSetsSpecification) {
		for (int i = 0; i < groupingSetsSpecification.size(); i++) {
			GroupingElement groupingElement = groupingSetsSpecification.get(i);
			analyze(groupingElement);
		}
	}
	
	private void analyze(OrdinaryGroupingSet ordinaryGroupingSet) {
		for (int i = 0; i < ordinaryGroupingSet.size(); i++) {
			GroupingColumnReference groupingColumnReference = ordinaryGroupingSet.get(i);
			analyze(groupingColumnReference);
		}
	}
	
	private void analyze(RollupList rollupList) {
		for (int i = 0; i < rollupList.size(); i++) {
			GroupingColumnReference groupingColumnReference = rollupList.get(i);
			analyze(groupingColumnReference);
		}
	}
	
	private void analyzeSortSpecificationList(List<SortSpecification> sortSpecificationList) {
		if (sortSpecificationList == null) {
			return;
		}
		for (int i = 0; i < sortSpecificationList.size(); i++) {
			SortSpecification sortSpecification = sortSpecificationList.get(i);
			analyze(sortSpecification);
		}
	}
	
	private void analyze(SortSpecification sortSpecification) {
		ValueExpression sortKey = sortSpecification.getSortKey();
		analyze(sortKey);
	}
	
	private void analyze(ValueExpression valueExpression) {
		if (valueExpression == null) {
			return;
		}
		if (valueExpression instanceof Subquery) {
			Subquery subquery = (Subquery) valueExpression;
			analyze(subquery);
			return;
		}
		if (valueExpression instanceof BooleanValueExpression) {
			BooleanValueExpression booleanValueExpression = 
					(BooleanValueExpression) valueExpression;
			analyze(booleanValueExpression);
			return;
		}
		if (valueExpression instanceof AbsoluteValueExpression) {
			AbsoluteValueExpression absoluteValueExpression = 
					(AbsoluteValueExpression) valueExpression;
			analyze(absoluteValueExpression);
			return;
		}
		if (valueExpression instanceof Addition) {
			Addition addition = (Addition) valueExpression;
			analyze(addition);
			return;
		}
		if (valueExpression instanceof Any) {
			Any any = (Any) valueExpression;
			analyze(any);
			return;
		}
		if (valueExpression instanceof Avg) {
			Avg avg = (Avg) valueExpression;
			analyze(avg);
			return;
		}
		if (valueExpression instanceof BitLengthExpression) {
			BitLengthExpression bitLengthExpression = 
					(BitLengthExpression) valueExpression;
			analyze(bitLengthExpression);
			return;
		}
		if (valueExpression instanceof CardinalityExpression) {
			CardinalityExpression cardinalityExpression = 
					(CardinalityExpression) valueExpression;
			analyze(cardinalityExpression);
			return;
		}
		if (valueExpression instanceof CharLengthExpression) {
			CharLengthExpression charLengthExpression = 
					(CharLengthExpression) valueExpression;
			analyze(charLengthExpression);
			return;
		}
		if (valueExpression instanceof Coalesce) {
			Coalesce coalesce = (Coalesce) valueExpression;
			analyze(coalesce);
			return;
		}
		if (valueExpression instanceof Concatenation) {
			Concatenation concatenation = (Concatenation) valueExpression;
			analyze(concatenation);
			return;
		}
		if (valueExpression instanceof Count) {
			Count count = (Count) valueExpression;
			analyze(count);
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
			analyze(division);
			return;
		}
		if (valueExpression instanceof Every) {
			Every every = (Every) valueExpression;
			analyze(every);
			return;
		}
		if (valueExpression instanceof ExtractExpression) {
			ExtractExpression extractExpression = 
					(ExtractExpression) valueExpression;
			analyze(extractExpression);
			return;
		}
		if (valueExpression instanceof FunctionInvocation) {
			FunctionInvocation functionInvocation = 
					(FunctionInvocation) valueExpression;
			analyze(functionInvocation);
			return;
		}
		if (valueExpression instanceof Grouping) {
			return;
		}
		if (valueExpression instanceof Lower) {
			Lower lower = (Lower) valueExpression;
			analyze(lower);
			return;
		}
		if (valueExpression instanceof Max) {
			Max max = (Max) valueExpression;
			analyze(max);
			return;
		}
		if (valueExpression instanceof Min) {
			Min min = (Min) valueExpression;
			analyze(min);
			return;
		}
		if (valueExpression instanceof ModulusExpression) {
			ModulusExpression modulusExpression = 
					(ModulusExpression) valueExpression;
			analyze(modulusExpression);
			return;
		}
		if (valueExpression instanceof Multiplication) {
			Multiplication multiplication = (Multiplication) valueExpression;
			analyze(multiplication);
			return;
		}
		if (valueExpression instanceof NameChain) {
			return;
		}
		if (valueExpression instanceof NegativeExpression) {
			NegativeExpression negativeExpression = 
					(NegativeExpression) valueExpression;
			analyze(negativeExpression);
			return;
		}
		if (valueExpression instanceof NullIf) {
			NullIf nullIf = (NullIf) valueExpression;
			analyze(nullIf);
			return;
		}
		if (valueExpression instanceof NumericLiteral) {
			return;
		}
		if (valueExpression instanceof OctetLengthExpression) {
			OctetLengthExpression octetLengthExpression = 
					(OctetLengthExpression) valueExpression;
			analyze(octetLengthExpression);
			return;
		}
		if (valueExpression instanceof Parameter) {
			return;
		}
		if (valueExpression instanceof PositionExpression) {
			PositionExpression positionExpression = 
					(PositionExpression) valueExpression;
			analyze(positionExpression);
			return;
		}
		if (valueExpression instanceof PositiveExpression) {
			PositiveExpression positiveExpression = 
					(PositiveExpression) valueExpression;
			analyze(positiveExpression);
			return;
		}
		if (valueExpression instanceof SearchedCase) {
			SearchedCase searchedCase = (SearchedCase) valueExpression;
			analyze(searchedCase);
			return;
		}
		if (valueExpression instanceof SimpleCase) {
			SimpleCase simpleCase = (SimpleCase) valueExpression;
			analyze(simpleCase);
			return;
		}
		if (valueExpression instanceof Some) {
			Some some = (Some) valueExpression;
			analyze(some);
			return;
		}
		if (valueExpression instanceof StringLiteral) {
			return;
		}
		if (valueExpression instanceof ToDate) {
			ToDate toDate = (ToDate) valueExpression;
			analyze(toDate);
			return;
		}
		if (valueExpression instanceof ToChar) {
			ToChar toChar = (ToChar) valueExpression;
			analyze(toChar);
			return;
		}
		if (valueExpression instanceof Substring) {
			Substring substring = (Substring) valueExpression;
			analyze(substring);
			return;
		}
		if (valueExpression instanceof Subtraction) {
			Subtraction subtraction = (Subtraction) valueExpression;
			analyze(subtraction);
			return;
		}
		if (valueExpression instanceof Sum) {
			Sum sum = (Sum) valueExpression;
			analyze(sum);
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
			analyze(trim);
			return;
		}
		if (valueExpression instanceof Upper) {
			Upper upper = (Upper) valueExpression;
			analyze(upper);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				valueExpression.getBeginIndex(), 
				"Not support the value exrepssion.");
	}
	
	private void analyze(BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression == null) {
			return;
		}
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = 
					(BooleanValue) booleanValueExpression;
			analyze(booleanValue);
			return;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = 
					(Predicate) booleanValueExpression;
			analyze(predicate);
			return;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = 
					(BooleanFactor) booleanValueExpression;
			analyze(booleanFactor);
			return;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			analyze(booleanTerm);
			return;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			analyze(booleanTest);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				booleanValueExpression.getBeginIndex(), 
				"Not support the boolean value exrepssion.");
	}
	
	private void analyze(BooleanValue booleanValue) {
		int size = booleanValue.size();
		for (int i = 0; i < size; i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			analyze(booleanTerm);
		}
	}
	
	private void analyze(Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = 
					(ComparisonPredicate) predicate;
			analyze(comparisonPredicate);
			return;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = 
					(BetweenPredicate) predicate;
			analyze(betweenPredicate);
			return;
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = 
					(DistinctPredicate) predicate;
			analyze(distinctPredicate);
			return;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = 
					(ExistsPredicate) predicate;
			analyze(existsPredicate);
			return;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = 
					(InPredicate) predicate;
			analyze(inPredicate);
			return;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = 
					(LikePredicate) predicate;
			analyze(likePredicate);
			return;
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = 
					(MatchPredicate) predicate;
			analyze(matchPredicate);
			return;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = 
					(NullPredicate) predicate;
			analyze(nullPredicate);
			return;
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = 
					(OverlapsPredicate) predicate;
			analyze(overlapsPredicate);
			return;
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = 
					(SimilarPredicate) predicate;
			analyze(similarPredicate);
			return;
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = 
					(UniquePredicate) predicate;
			analyze(uniquePredicate);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"Not support the predicate.");
	}
	
	private void analyze(ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		analyze(left);
		ValueExpression right = comparisonPredicate.getRight();
		analyze(right);
	}
	
	private void analyze(BetweenPredicate betweenPredicate) {
		ValueExpression valueExpression = betweenPredicate.getValueExpression();
		analyze(valueExpression);
		ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
		analyze(valueExpression1);
		ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
		analyze(valueExpression2);
	}
	
	private void analyze(DistinctPredicate distinctPredicate) {
		ValueExpression left = distinctPredicate.getLeft();
		analyze(left);
		ValueExpression right = distinctPredicate.getRight();
		analyze(right);
	}
	
	private void analyze(ExistsPredicate existsPredicate) {
		Subquery subquery = existsPredicate.getSubquery();
		analyze(subquery);
	}
	
	private void analyze(InPredicate inPredicate) {
		ValueExpression valueExpression = inPredicate.getValueExpression();
		analyze(valueExpression);
		List<ValueExpression> inValueList = inPredicate.getInValueList();
		if (inValueList != null) {
			for (int i = 0; i < inValueList.size(); i++) {
				ValueExpression inValue = inValueList.get(i);
				analyze(inValue);
			}
		}
		Subquery subquery = inPredicate.getSubquery();
		if (subquery != null) {
			analyze(subquery);
		}
	}
	
	private void analyze(LikePredicate likePredicate) {
		ValueExpression valueExpression = likePredicate.getValueExpression();
		analyze(valueExpression);
		ValueExpression characterPattern = likePredicate.getCharacterPattern();
		analyze(characterPattern);
		ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
		analyze(escapeCharacter);
	}
	
	private void analyze(MatchPredicate matchPredicate) {
		ValueExpression valueExpression = matchPredicate.getValueExpression();
		analyze(valueExpression);
		Subquery subquery = matchPredicate.getSubquery();
		analyze(subquery);
	}
	
	private void analyze(NullPredicate nullPredicate) {
		ValueExpression valueExpression = nullPredicate.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(OverlapsPredicate overlapsPredicate) {
		ValueExpression left = overlapsPredicate.getLeft();
		analyze(left);
		ValueExpression right = overlapsPredicate.getRight();
		analyze(right);
	}
	
	private void analyze(SimilarPredicate similarPredicate) {
		ValueExpression valueExpression = similarPredicate.getValueExpression();
		analyze(valueExpression);
		ValueExpression similarPattern = similarPredicate.getSimilarPattern();
		analyze(similarPattern);
		ValueExpression escapeCharacter = similarPredicate.getEscapeCharacter();
		analyze(escapeCharacter);
	}
	
	private void analyze(UniquePredicate uniquePredicate) {
		Subquery subquery = uniquePredicate.getSubquery();
		analyze(subquery);
	}
	
	private void analyze(BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = 
				booleanFactor.getBooleanValueExpression();
		analyze(booleanValueExpression);
	}
	
	private void analyze(BooleanTerm booleanTerm) {
		int size = booleanTerm.size();
		for (int i = 0; i < size; i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			analyze(booleanFactor);
		}
	}
	
	private void analyze(BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = 
				booleanTest.getBooleanValueExpression();
		analyze(booleanValueExpression);
	}
	
	private void analyze(AbsoluteValueExpression absoluteValueExpression) {
		ValueExpression valueExpression = absoluteValueExpression.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(Addition addition) {
		ValueExpression left = addition.getLeft();
		analyze(left);
		ValueExpression right = addition.getRight();
		analyze(right);
	}
	
	private void analyze(Any any) {
		ValueExpression valueExpression = any.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(Avg avg) {
		ValueExpression valueExpression = avg.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(BitLengthExpression bitLengthExpression) {
		ValueExpression valueExpression = bitLengthExpression.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(CardinalityExpression cardinalityExpression) {
		ValueExpression valueExpression = cardinalityExpression.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(CharLengthExpression charLengthExpression) {
		ValueExpression valueExpression = charLengthExpression.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(Coalesce coalesce) {
		List<ValueExpression> arguments = coalesce.getArguments();
		for (int i = 0; i < arguments.size(); i++) {
			ValueExpression argument = arguments.get(i);
			analyze(argument);
		}
	}
	
	private void analyze(Concatenation concatenation) {
		ValueExpression left = concatenation.getLeft();
		analyze(left);
		ValueExpression right = concatenation.getRight();
		analyze(right);
	}
	
	private void analyze(Count count) {
		ValueExpression valueExpression = count.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(Division division) {
		ValueExpression left = division.getLeft();
		analyze(left);
		ValueExpression right = division.getRight();
		analyze(right);
	}
	
	private void analyze(Every every) {
		ValueExpression valueExpression = every.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(ExtractExpression extractExpression) {
		ValueExpression extractSource = extractExpression.getExtractSource();
		analyze(extractSource);
	}
	
	private void analyze(FunctionInvocation functionInvocation) {
		List<ValueExpression> arguments = functionInvocation.getArguments();
		for (int i = 0; i < arguments.size(); i++) {
			ValueExpression argument = arguments.get(i);
			analyze(argument);
		}
	}
	
	private void analyze(Lower lower) {
		ValueExpression argument = lower.getValueExpression();
		analyze(argument);
	}
	
	private void analyze(Max max) {
		ValueExpression valueExpression = max.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(Min min) {
		ValueExpression valueExpression = min.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(ModulusExpression modulusExpression) {
		ValueExpression dividend = modulusExpression.getDividend();
		analyze(dividend);
		ValueExpression divisor = modulusExpression.getDivisor();
		analyze(divisor);
	}
	
	private void analyze(Multiplication multiplication) {
		ValueExpression left = multiplication.getLeft();
		analyze(left);
		ValueExpression right = multiplication.getRight();
		analyze(right);
	}
	
	private void analyze(NegativeExpression negativeExpression) {
		ValueExpression valueExpression = negativeExpression.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(NullIf nullIf) {
		ValueExpression first = nullIf.getFirst();
		analyze(first);
		ValueExpression second = nullIf.getSecond();
		analyze(second);
	}
	
	private void analyze(OctetLengthExpression octetLengthExpression) {
		ValueExpression valueExpression = octetLengthExpression.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(PositionExpression positionExpression) {
		ValueExpression valueExpression1 = positionExpression.getValueExpression1();
		analyze(valueExpression1);
		ValueExpression valueExpression2 = positionExpression.getValueExpression2();
		analyze(valueExpression2);
	}
	
	private void analyze(PositiveExpression positiveExpression) {
		ValueExpression valueExpression = positiveExpression.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(SearchedCase searchedCase) {
		List<SearchedWhenClause> searchedWhenClauseList = 
				searchedCase.getSearchedWhenClauseList();
		for (int i = 0; i < searchedWhenClauseList.size(); i++) {
			SearchedWhenClause searchedWhenClause = searchedWhenClauseList.get(i);
			BooleanValueExpression searchedCondition = searchedWhenClause.getSearchedCondition();
			analyze(searchedCondition);
			ValueExpression result = searchedWhenClause.getResult();
			analyze(result);
		}
		ElseClause elseClause = searchedCase.getElseClause();
		ValueExpression result = elseClause.getResult();
		analyze(result);
	}
	
	private void analyze(SimpleCase simpleCase) {
		ValueExpression caseOperand = simpleCase.getCaseOperand();
		analyze(caseOperand);
		List<SimpleWhenClause> simpleWhenClauseList = simpleCase.getSimpleWhenClauseList();
		for (int i = 0; i < simpleWhenClauseList.size(); i++) {
			SimpleWhenClause simpleWhenClause = simpleWhenClauseList.get(i);
			ValueExpression whenOperand = simpleWhenClause.getWhenOperand();
			analyze(whenOperand);
			ValueExpression result = simpleWhenClause.getResult();
			analyze(result);
		}
		ElseClause elseClause = simpleCase.getElseClause();
		ValueExpression result = elseClause.getResult();
		analyze(result);
	}
	
	private void analyze(Some some) {
		ValueExpression valueExpression = some.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(ToDate toDate) {
		ValueExpression valueExpression = toDate.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(ToChar toChar) {
		ValueExpression valueExpression = toChar.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(Substring substring) {
		ValueExpression valueExpression = substring.getValueExpression();
		analyze(valueExpression);
		ValueExpression startPosition = substring.getStartPosition();
		analyze(startPosition);
		ValueExpression stringLength = substring.getStringLength();
		analyze(stringLength);
	}
	
	private void analyze(Subtraction subtraction) {
		ValueExpression left = subtraction.getLeft();
		analyze(left);
		ValueExpression right = subtraction.getRight();
		analyze(right);
	}
	
	private void analyze(Sum sum) {
		ValueExpression valueExpression = sum.getValueExpression();
		analyze(valueExpression);
	}
	
	private void analyze(Trim trim) {
		ValueExpression trimCharacter = trim.getTrimCharacter();
		analyze(trimCharacter);
		ValueExpression trimSource = trim.getTrimSource();
		analyze(trimSource);
	}
	
	private void analyze(Upper upper) {
		ValueExpression argument = upper.getValueExpression();
		analyze(argument);
	}
	
	private void analyze(Subquery subquery) {
		SelectStatement selectStatement = subquery.getSelectStatement();
		analyze(selectStatement);
	}

}
