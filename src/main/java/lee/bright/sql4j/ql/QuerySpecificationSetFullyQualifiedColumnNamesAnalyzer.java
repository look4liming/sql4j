package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;
import lee.bright.sql4j.conf.TableMetadata;

/**
 * @author Bright Lee
 */
public final class QuerySpecificationSetFullyQualifiedColumnNamesAnalyzer {
	
	private Configuration configuration;
	private SourceCode sourceCode;
	private QuerySpecification querySpecification;
	
	public QuerySpecificationSetFullyQualifiedColumnNamesAnalyzer(Configuration configuration, 
			SourceCode sourceCode, QuerySpecification querySpecification) {
		this.configuration = configuration;
		this.sourceCode = sourceCode;
		this.querySpecification = querySpecification;
	}
	
	public void analyze() {
		analyze(querySpecification);
	}
	
	private void analyze(QuerySpecification querySpecification) {
		List<SelectSublist> selectList = querySpecification.getSelectList();
		analyzeValueExpressionInSelectSublist(querySpecification, selectList);
		if (isSingleTableQuery(querySpecification)  == false) {
			TableReference tableReference = getTableReference(querySpecification);
			analyzeTableReference(tableReference);
		}
		BooleanValueExpression whereSearchCondition = querySpecification.getWhereSearchCondition();
		if (whereSearchCondition != null) {
			analyzeWhereSearchCondition(querySpecification, whereSearchCondition);
		}
		List<GroupingElement> groupingElementList = querySpecification.getGroupingElementList();
		if (groupingElementList != null) {
			analyzeGroupingElementList(querySpecification, groupingElementList);
		}
		BooleanValueExpression havingSearchCondition = querySpecification.getHavingSearchCondition();
		if (havingSearchCondition != null) {
			analyzeWhereSearchCondition(querySpecification, havingSearchCondition);
		}
		List<SortSpecification> sortSpecificationList = querySpecification.getSortSpecificationList();
		if (sortSpecificationList != null) {
			analyzeSortSpecificationList(querySpecification, sortSpecificationList);
		}
		querySpecification.setSetFullyQualifiedColumnNames(true);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, List<SelectSublist> selectList) {
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			ValueExpression valueExpression = selectSublist.getValueExpression();
			analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
		}
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			ValueExpression valueExpression) {
		if (valueExpression instanceof NameChain) {
			NameChain nameChain = (NameChain) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, nameChain);
			return;
		}
		if (valueExpression instanceof BooleanValueExpression) {
			BooleanValueExpression booleanValueExpression = 
					(BooleanValueExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, booleanValueExpression);
			return;
		}
		if (valueExpression instanceof AbsoluteValueExpression) {
			AbsoluteValueExpression absoluteValueExpression = 
					(AbsoluteValueExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, absoluteValueExpression);
			return;
		}
		if (valueExpression instanceof Addition) {
			Addition addition = (Addition) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, addition);
			return;
		}
		if (valueExpression instanceof Any) {
			Any any = (Any) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, any);
			return;
		}
		if (valueExpression instanceof Avg) {
			Avg avg = (Avg) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, avg);
			return;
		}
		if (valueExpression instanceof BitLengthExpression) {
			BitLengthExpression bitLengthExpression = 
					(BitLengthExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, bitLengthExpression);
			return;
		}
		if (valueExpression instanceof CardinalityExpression) {
			CardinalityExpression cardinalityExpression = 
					(CardinalityExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, cardinalityExpression);
			return;
		}
		if (valueExpression instanceof CharLengthExpression) {
			CharLengthExpression charLengthExpression = 
					(CharLengthExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, charLengthExpression);
			return;
		}
		if (valueExpression instanceof Coalesce) {
			Coalesce coalesce = (Coalesce) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, coalesce);
			return;
		}
		if (valueExpression instanceof Concatenation) {
			Concatenation concatenation = (Concatenation) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, concatenation);
			return;
		}
		if (valueExpression instanceof Count) {
			Count count = (Count) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, count);
			return;
		}
		if (valueExpression instanceof CurrentDate) {
			CurrentDate currentDate = (CurrentDate) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, currentDate);
			return;
		}
		if (valueExpression instanceof CurrentTime) {
			CurrentTime currentTime = (CurrentTime) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, currentTime);
			return;
		}
		if (valueExpression instanceof CurrentTimestamp) {
			CurrentTimestamp currentTimestamp = (CurrentTimestamp) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, currentTimestamp);
			return;
		}
		if (valueExpression instanceof DateLiteral) {
			DateLiteral dateLiteral = (DateLiteral) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, dateLiteral);
			return;
		}
		if (valueExpression instanceof Division) {
			Division division = (Division) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, division);
			return;
		}
		if (valueExpression instanceof Every) {
			Every every = (Every) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, every);
			return;
		}
		if (valueExpression instanceof ExtractExpression) {
			ExtractExpression extractExpression = 
					(ExtractExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, extractExpression);
			return;
		}
		if (valueExpression instanceof FunctionInvocation) {
			FunctionInvocation functionInvocation = 
					(FunctionInvocation) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, functionInvocation);
			return;
		}
		if (valueExpression instanceof Grouping) {
			Grouping grouping = (Grouping) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, grouping);
			return;
		}
		if (valueExpression instanceof Lower) {
			Lower lower = (Lower) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, lower);
			return;
		}
		if (valueExpression instanceof Max) {
			Max max = (Max) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, max);
			return;
		}
		if (valueExpression instanceof Min) {
			Min min = (Min) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, min);
			return;
		}
		if (valueExpression instanceof ModulusExpression) {
			ModulusExpression modulusExpression = 
					(ModulusExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, modulusExpression);
			return;
		}
		if (valueExpression instanceof Multiplication) {
			Multiplication multiplication = (Multiplication) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, multiplication);
			return;
		}
		if (valueExpression instanceof NegativeExpression) {
			NegativeExpression negativeExpression = 
					(NegativeExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, negativeExpression);
			return;
		}
		if (valueExpression instanceof NullIf) {
			NullIf nullIf = (NullIf) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, nullIf);
			return;
		}
		if (valueExpression instanceof NumericLiteral) {
			NumericLiteral numericLiteral = 
					(NumericLiteral) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, numericLiteral);
			return;
		}
		if (valueExpression instanceof OctetLengthExpression) {
			OctetLengthExpression octetLengthExpression = 
					(OctetLengthExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, octetLengthExpression);
			return;
		}
		if (valueExpression instanceof Parameter) {
			Parameter parameter = (Parameter) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, parameter);
			return;
		}
		if (valueExpression instanceof PositionExpression) {
			PositionExpression positionExpression = 
					(PositionExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, positionExpression);
			return;
		}
		if (valueExpression instanceof PositiveExpression) {
			PositiveExpression positiveExpression = 
					(PositiveExpression) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, positiveExpression);
			return;
		}
		if (valueExpression instanceof SearchedCase) {
			SearchedCase searchedCase = (SearchedCase) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, searchedCase);
			return;
		}
		if (valueExpression instanceof SimpleCase) {
			SimpleCase simpleCase = (SimpleCase) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, simpleCase);
			return;
		}
		if (valueExpression instanceof Some) {
			Some some = (Some) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, some);
			return;
		}
		if (valueExpression instanceof StringLiteral) {
			StringLiteral stringLiteral = 
					(StringLiteral) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, stringLiteral);
			return;
		}
		if (valueExpression instanceof Subquery) {
			Subquery subquery = (Subquery) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, subquery);
			return;
		}
		if (valueExpression instanceof ToDate) {
			ToDate toDate = (ToDate) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, toDate);
			return;
		}
		if (valueExpression instanceof ToChar) {
			ToChar toChar = (ToChar) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, toChar);
			return;
		}
		if (valueExpression instanceof Substring) {
			Substring substring = (Substring) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, substring);
			return;
		}
		if (valueExpression instanceof Subtraction) {
			Subtraction subtraction = (Subtraction) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, subtraction);
			return;
		}
		if (valueExpression instanceof Sum) {
			Sum sum = (Sum) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, sum);
			return;
		}
		if (valueExpression instanceof TimeLiteral) {
			TimeLiteral timeLiteral = (TimeLiteral) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, timeLiteral);
			return;
		}
		if (valueExpression instanceof TimestampLiteral) {
			TimestampLiteral timestampLiteral = 
					(TimestampLiteral) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, timestampLiteral);
			return;
		}
		if (valueExpression instanceof Trim) {
			Trim trim = (Trim) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, trim);
			return;
		}
		if (valueExpression instanceof Upper) {
			Upper upper = (Upper) valueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, upper);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
				"Not support the value exrepssion.");
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue) booleanValueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, booleanValue);
			return;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = (Predicate) booleanValueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, predicate);
			return;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = (BooleanFactor) booleanValueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, booleanFactor);
			return;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, booleanTerm);
			return;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			analyzeValueExpressionInSelectSublist(querySpecification, booleanTest);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, booleanValueExpression.getBeginIndex(), 
				"This boolean value exrepssion is not supported.");
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			BooleanValue booleanValue) {
		for (int i = 0; i < booleanValue.size(); i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			analyzeValueExpressionInSelectSublist(querySpecification, booleanTerm);
		}
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = (ComparisonPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, comparisonPredicate);
			return;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = (BetweenPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, betweenPredicate);
			return;
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = (DistinctPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, distinctPredicate);
			return;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = (ExistsPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, existsPredicate);
			return;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = (InPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, inPredicate);
			return;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = (LikePredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, likePredicate);
			return;
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = (MatchPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, matchPredicate);
			return;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = (NullPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, nullPredicate);
			return;
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = (OverlapsPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, overlapsPredicate);
			return;
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = (SimilarPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, similarPredicate);
			return;
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = (UniquePredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, uniquePredicate);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"This predicate is not supported.");
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		analyzeValueExpressionInSelectSublist(querySpecification, left);
		ValueExpression right = comparisonPredicate.getRight();
		analyzeValueExpressionInSelectSublist(querySpecification, right);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			BetweenPredicate betweenPredicate) {
		ValueExpression valueExpression = betweenPredicate.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
		ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression1);
		ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression2);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			DistinctPredicate distinctPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, distinctPredicate.getBeginIndex(), 
				"Distinct predicate is not supported in select list.");
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			ExistsPredicate existsPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, existsPredicate.getBeginIndex(), 
				"Exists predicate is not supported in select list.");
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			InPredicate inPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, inPredicate.getBeginIndex(), 
				"In predicate is not supported in select list.");
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			LikePredicate likePredicate) {
		ValueExpression valueExpression = likePredicate.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
		ValueExpression characterPattern = likePredicate.getCharacterPattern();
		analyzeValueExpressionInSelectSublist(querySpecification, characterPattern);
		ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
		if (escapeCharacter != null) {
			analyzeValueExpressionInSelectSublist(querySpecification, escapeCharacter);
		}
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			MatchPredicate matchPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, matchPredicate.getBeginIndex(), 
				"Match predicate is not supported in select list.");
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			NullPredicate nullPredicate) {
		ValueExpression valueExpression = nullPredicate.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			OverlapsPredicate overlapsPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, overlapsPredicate.getBeginIndex(), 
				"Overlaps predicate is not supported in select list.");
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			SimilarPredicate similarPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, similarPredicate.getBeginIndex(), 
				"Similar predicate is not supported in select list.");
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			UniquePredicate uniquePredicate) {
		throw Sql4jException.getSql4jException(sourceCode, uniquePredicate.getBeginIndex(), 
				"Unique predicate is not supported in select list.");
	}
		
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = booleanFactor.getBooleanValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, booleanValueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
		BooleanTerm booleanTerm) {
		for (int i = 0; i < booleanTerm.size(); i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			analyzeValueExpressionInSelectSublist(querySpecification, booleanFactor);
		}
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = booleanTest.getBooleanValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, booleanValueExpression);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			AbsoluteValueExpression absoluteValueExpression) {
		ValueExpression valueExpression = absoluteValueExpression.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Addition addition) {
		ValueExpression left = addition.getLeft();
		analyzeValueExpressionInSelectSublist(querySpecification, left);
		ValueExpression right = addition.getRight();
		analyzeValueExpressionInSelectSublist(querySpecification, right);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Any any) {
		ValueExpression valueExpression = any.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Avg avg) {
		ValueExpression valueExpression = avg.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			BitLengthExpression bitLengthExpression) {
		ValueExpression valueExpression = bitLengthExpression.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			CardinalityExpression cardinalityExpression) {
		ValueExpression valueExpression = cardinalityExpression.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			CharLengthExpression charLengthExpression) {
		ValueExpression valueExpression = charLengthExpression.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Coalesce coalesce) {
		List<ValueExpression> list = coalesce.getArguments();
		for (int i = 0; i < list.size(); i++) {
			ValueExpression valueExpression = list.get(i);
			analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
		}
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Concatenation concatenation) {
		ValueExpression left = concatenation.getLeft();
		analyzeValueExpressionInSelectSublist(querySpecification, left);
		ValueExpression right = concatenation.getRight();
		analyzeValueExpressionInSelectSublist(querySpecification, right);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Count count) {
		ValueExpression valueExpression = count.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			CurrentDate currentDate) {
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			CurrentTime currentTime) {
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			CurrentTimestamp currentTimestamp) {
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			DateLiteral dateLiteral) {
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Division division) {
		ValueExpression left = division.getLeft();
		analyzeValueExpressionInSelectSublist(querySpecification, left);
		ValueExpression right = division.getRight();
		analyzeValueExpressionInSelectSublist(querySpecification, right);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Every every) {
		ValueExpression valueExpression = every.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			ExtractExpression extractExpression) {
		ValueExpression extractSource = extractExpression.getExtractSource();
		analyzeValueExpressionInSelectSublist(querySpecification, extractSource);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			FunctionInvocation functionInvocation) {
		List<ValueExpression> list = functionInvocation.getArguments();
		for (int i = 0; i < list.size(); i++) {
			ValueExpression valueExpression = list.get(i);
			analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
		}
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Grouping grouping) {
		NameChain columnReference = grouping.getColumnReference();
		analyzeValueExpressionInSelectSublist(querySpecification, columnReference);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Lower lower) {
		ValueExpression valueExpression = lower.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Max max) {
		ValueExpression valueExpression = max.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Min min) {
		ValueExpression valueExpression = min.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			ModulusExpression modulusExpression) {
		ValueExpression dividend = modulusExpression.getDividend();
		analyzeValueExpressionInSelectSublist(querySpecification, dividend);
		ValueExpression divisor = modulusExpression.getDivisor();
		analyzeValueExpressionInSelectSublist(querySpecification, divisor);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Multiplication multiplication) {
		ValueExpression left = multiplication.getLeft();
		analyzeValueExpressionInSelectSublist(querySpecification, left);
		ValueExpression right = multiplication.getRight();
		analyzeValueExpressionInSelectSublist(querySpecification, right);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			NameChain nameChain) {
		if (nameChain.size() <= 1) {
			analyzeOneSizeNameChainInSelectSublist(querySpecification, nameChain);
			return;
		}
		analyzeMoreThanOneSizeNameChainInSelectSublist(querySpecification, nameChain);
	}
	
	private void analyzeOneSizeNameChainInSelectSublist(QuerySpecification querySpecification, 
			NameChain nameChain) {
		if (isSingleTableQuery(querySpecification) == false) {
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Can't find the owner table of the column.");
		}
		TableReference tableReference = getTableReference(querySpecification);
		if (tableReference instanceof TablePrimary) {
			TablePrimary tablePrimary = (TablePrimary) tableReference;
			NameChain table = tablePrimary.getTableName();
			String tableName = table.toLowerCaseString();
			TableMetadata tableMetadata = configuration.getTableMetadata(tableName);
			Name column = nameChain.get(0);
			String columnName = column.getContent().toLowerCase();
			if (tableMetadata.hasColumnMetadata(columnName) == true) {
				NameChain fullyQualifiedColumnName = genFullyQualifiedColumnNames(table, column);
				nameChain.setFullyQualifiedName(fullyQualifiedColumnName);
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Can't find the owner table of the column.");
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			QuerySpecification derivedQuerySpecification = getQuerySpecification(derivedTable);
			if (derivedQuerySpecification.isSetFullyQualifiedColumnNames() == false) {
				analyze(derivedQuerySpecification);
			}
			List<SelectSublist> selectList = derivedQuerySpecification.getSelectList();
			ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnNameFromSelectList(selectList, nameChain.get(0));
			if (fullyQualifiedColumnName != null) {
				nameChain.setFullyQualifiedName(fullyQualifiedColumnName);
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Can't find the owner table of the column.");
		}
		throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
				"Can't find the owner table of the column.");
	}
	
	private void analyzeMoreThanOneSizeNameChainInSelectSublist(QuerySpecification querySpecification, 
			NameChain nameChain) {
		if (isSingleTableQuery(querySpecification) == true) {
			analyzeMoreThanOneSizeNameChainInSelectSublistWithSingleTableQuery(querySpecification, nameChain);
			return;
		}
		analyzeMoreThanOneSizeNameChainInSelectSublistWithMultipleTableQuery(querySpecification, nameChain);
	}
	
	private void analyzeMoreThanOneSizeNameChainInSelectSublistWithSingleTableQuery(QuerySpecification querySpecification, 
			NameChain nameChain) {
		String tableName = getTableNameByColumnName(nameChain);
		List<TableReference> tableReferenceList = querySpecification.getTableReferenceList();
		TableReference tableReference = getTableReference(tableReferenceList);
		if (tableReference instanceof TablePrimary) {
			TablePrimary tablePrimary = (TablePrimary) tableReference;
			Name correlationName = tablePrimary.getCorrelationName();
			NameChain table = tablePrimary.getTableName();
			String tablePrimaryTableName = table.toLowerCaseString();
			if (correlationName != null) {
				if (nameChain.size() > 3) {
					throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
							"Can't find the owner table of the column.");
				}
				if (tableName.equalsIgnoreCase(correlationName.getContent()) == false) {
					throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
							"Can't find the owner table of the column.");
				}
			} else {
				if (nameChain.size() != table.size() + 1) {
					throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
							"Can't find the owner table of the column.");
				}
				if (tableName.equalsIgnoreCase(tablePrimaryTableName) == false) {
					throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
							"Can't find the owner table of the column.");
				}
			}
			TableMetadata tableMetadata = configuration.getTableMetadata(tablePrimaryTableName);
			Name column = nameChain.get(nameChain.size() - 1);
			String columnName = column.getContent().toLowerCase();
			if (tableMetadata.hasColumnMetadata(columnName) == true) {
				NameChain fullyQualifiedColumnName = genFullyQualifiedColumnNames(table, column);
				nameChain.setFullyQualifiedName(fullyQualifiedColumnName);
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Can't find the owner table of the column.");
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			Name correlationName = derivedTable.getCorrelationName();
			if (correlationName == null) {
				throw Sql4jException.getSql4jException(sourceCode, derivedTable.getBeginIndex(), 
						"Lack of correlation name.");
			}
			if (nameChain.size() > 2) {
				throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
						"Can't find the owner table of the column.");
			}
			if (tableName.equalsIgnoreCase(correlationName.getContent()) == false) {
				throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
						"Can't find the owner table of the column.");
			}
			QuerySpecification derivedQuerySpecification = getQuerySpecification(derivedTable);
			if (derivedQuerySpecification.isSetFullyQualifiedColumnNames() == false) {
				analyze(derivedQuerySpecification);
			}
			List<SelectSublist> selectList = derivedQuerySpecification.getSelectList();
			ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnNameFromSelectList(selectList, nameChain.get(nameChain.size() - 1));
			if (fullyQualifiedColumnName != null) {
				nameChain.setFullyQualifiedName(fullyQualifiedColumnName);
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Can't find the owner table of the column.");
		}
		throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
				"Can't find the owner table of the column.");
	}
	
	private void analyzeMoreThanOneSizeNameChainInSelectSublistWithMultipleTableQuery(QuerySpecification querySpecification, 
			NameChain nameChain) {
		List<TableReference> tableReferenceList = querySpecification.getTableReferenceList();
		TableReference tableReference = getTableReference(tableReferenceList);
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(leftOuterJoin, nameChain);
			if (fullyQualifiedColumnName != null) {
				nameChain.setFullyQualifiedName(fullyQualifiedColumnName);
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Can't find the owner table of the column.");
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(rightOuterJoin, nameChain);
			if (fullyQualifiedColumnName != null) {
				nameChain.setFullyQualifiedName(fullyQualifiedColumnName);
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Can't find the owner table of the column.");
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(innerJoin, nameChain);
			if (fullyQualifiedColumnName != null) {
				nameChain.setFullyQualifiedName(fullyQualifiedColumnName);
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Can't find the owner table of the column.");
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"This table reference is not supported.");
	}
	
	private ValueExpression getFullyQualifiedColumnName(TableReference tableReference, NameChain nameChain) {
		if (tableReference instanceof TablePrimary) {
			TablePrimary tablePrimary = (TablePrimary) tableReference;
			NameChain fullyQualifiedColumnName = getFullyQualifiedColumnName(tablePrimary, nameChain);
			return fullyQualifiedColumnName;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(leftOuterJoin, nameChain);
			return fullyQualifiedColumnName;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(derivedTable, nameChain);
			return fullyQualifiedColumnName;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(rightOuterJoin, nameChain);
			return fullyQualifiedColumnName;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(innerJoin, nameChain);
			return fullyQualifiedColumnName;
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"This table reference is not supported.");
	}
	
	private NameChain getFullyQualifiedColumnName(TablePrimary tablePrimary, NameChain nameChain) {
		String tableName = getTableNameByColumnName(nameChain);
		Name correlationName = tablePrimary.getCorrelationName();
		if (correlationName != null) {
			if (correlationName.getContent().equalsIgnoreCase(tableName)) {
				String tablePrimaryTableName = getTableNameByTablePrimary(tablePrimary);
				TableMetadata tableMetadata = configuration.getTableMetadata(tablePrimaryTableName);
				String columnName = nameChain.get(nameChain.size() - 1).getContent().toLowerCase();
				if (tableMetadata.hasColumnMetadata(columnName) == false) {
					throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
							"Can't find the owner table of the column.");
				}
				List<Name> list = new ArrayList<Name>();
				for (int i = 0; i < tablePrimary.getTableName().size(); i++) {
					list.add(tablePrimary.getTableName().get(i));
				}
				list.add(nameChain.get(nameChain.size() - 1));
				NameChain fullyQualifiedColumnName = new NameChain(list);
				return fullyQualifiedColumnName;
			}
			return null;
		}
		String tablePrimaryTableName = getTableNameByTablePrimary(tablePrimary);
		if (tablePrimaryTableName.equalsIgnoreCase(tableName) == false) {
			return null;
		}
		List<Name> list = new ArrayList<Name>();
		for (int i = 0; i < tablePrimary.getTableName().size(); i++) {
			list.add(tablePrimary.getTableName().get(i));
		}
		list.add(nameChain.get(nameChain.size() - 1));
		NameChain fullyQualifiedColumnName = new NameChain(list);
		return fullyQualifiedColumnName;
	}
	
	private ValueExpression getFullyQualifiedColumnName(DerivedTable derivedTable, NameChain nameChain) {
		Name correlationName = derivedTable.getCorrelationName();
		if (correlationName == null) {
			throw Sql4jException.getSql4jException(sourceCode, derivedTable.getBeginIndex(), 
					"Missing correlation name.");
		}
		String tableName = getTableNameByColumnName(nameChain);
		if (correlationName.getContent().equalsIgnoreCase(tableName) == false) {
			return null;
		}
		QuerySpecification querySpecification = getQuerySpecification(derivedTable);
		if (querySpecification.isSetFullyQualifiedColumnNames() == false) {
			analyze(querySpecification);
		}
		String columnName = nameChain.get(nameChain.size() - 1).getContent().toLowerCase();
		List<SelectSublist> selectList = querySpecification.getSelectList();
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			Name name = selectSublist.getName();
			if (name != null) {
				if (columnName.equalsIgnoreCase(name.getContent())) {
					ValueExpression valueExpression = selectSublist.getValueExpression();
					return valueExpression;
				}
				continue;
			}
			ValueExpression valueExpression = selectSublist.getValueExpression();
			if (valueExpression instanceof NameChain) {
				NameChain value = (NameChain) valueExpression;
				String derivedColumnName = value.get(value.size() - 1).getContent();
				if (columnName.equalsIgnoreCase(derivedColumnName)) {
					ValueExpression fullyQualifiedName = value.getFullyQualifiedName();
					return fullyQualifiedName;
				}
			}
		}
		throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
				"Can't find the owner table of the column.");
	}
	
	private ValueExpression getFullyQualifiedColumnName(LeftOuterJoin leftOuterJoin, NameChain nameChain) {
		TableReference left = leftOuterJoin.getLeft();
		ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(left, nameChain);
		if (fullyQualifiedColumnName != null) {
			return fullyQualifiedColumnName;
		}
		TableReference right = leftOuterJoin.getRight();
		fullyQualifiedColumnName = getFullyQualifiedColumnName(right, nameChain);
		return fullyQualifiedColumnName;
	}
	
	private ValueExpression getFullyQualifiedColumnName(RightOuterJoin rightOuterJoin, NameChain nameChain) {
		TableReference left = rightOuterJoin.getLeft();
		ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(left, nameChain);
		if (fullyQualifiedColumnName != null) {
			return fullyQualifiedColumnName;
		}
		TableReference right = rightOuterJoin.getRight();
		fullyQualifiedColumnName = getFullyQualifiedColumnName(right, nameChain);
		return fullyQualifiedColumnName;
	}
	
	private ValueExpression getFullyQualifiedColumnName(InnerJoin innerJoin, NameChain nameChain) {
		TableReference left = innerJoin.getLeft();
		ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(left, nameChain);
		if (fullyQualifiedColumnName != null) {
			return fullyQualifiedColumnName;
		}
		TableReference right = innerJoin.getRight();
		fullyQualifiedColumnName = getFullyQualifiedColumnName(right, nameChain);
		return fullyQualifiedColumnName;
	}
	
	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			NegativeExpression negativeExpression) {
		ValueExpression valueExpression = negativeExpression.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			NullIf nullIf) {
		ValueExpression first = nullIf.getFirst();
		analyzeValueExpressionInSelectSublist(querySpecification, first);
		ValueExpression second = nullIf.getSecond();
		analyzeValueExpressionInSelectSublist(querySpecification, second);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			NumericLiteral numericLiteral) {
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			OctetLengthExpression octetLengthExpression) {
		ValueExpression valueExpression = octetLengthExpression.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Parameter parameter) {
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			PositionExpression positionExpression) {
		ValueExpression valueExpression1 = positionExpression.getValueExpression1();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression1);
		ValueExpression valueExpression2 = positionExpression.getValueExpression2();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression2);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			PositiveExpression positiveExpression) {
		ValueExpression valueExpression = positiveExpression.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			SearchedCase searchedCase) {
		List<SearchedWhenClause> searchedWhenClauseList = searchedCase.getSearchedWhenClauseList();
		for (int i = 0; i < searchedWhenClauseList.size(); i++) {
			SearchedWhenClause searchedWhenClause = searchedWhenClauseList.get(i);
			BooleanValueExpression searchedCondition = searchedWhenClause.getSearchedCondition();
			analyzeValueExpressionInSelectSublist(querySpecification, searchedCondition);
			ValueExpression result = searchedWhenClause.getResult();
			analyzeValueExpressionInSelectSublist(querySpecification, result);
		}
		ElseClause elseClause = searchedCase.getElseClause();
		if (elseClause != null) {
			ValueExpression result = elseClause.getResult();
			analyzeValueExpressionInSelectSublist(querySpecification, result);
		}
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			SimpleCase simpleCase) {
		ValueExpression caseOperand = simpleCase.getCaseOperand();
		analyzeValueExpressionInSelectSublist(querySpecification, caseOperand);
		List<SimpleWhenClause> simpleWhenClauseList = simpleCase.getSimpleWhenClauseList();
		for (int i = 0; i < simpleWhenClauseList.size(); i++) {
			SimpleWhenClause simpleWhenClause = simpleWhenClauseList.get(i);
			ValueExpression whenOperand = simpleWhenClause.getWhenOperand();
			analyzeValueExpressionInSelectSublist(querySpecification, whenOperand);
			ValueExpression result = simpleWhenClause.getResult();
			analyzeValueExpressionInSelectSublist(querySpecification, result);
		}
		ElseClause elseClause = simpleCase.getElseClause();
		if (elseClause != null) {
			ValueExpression result = elseClause.getResult();
			analyzeValueExpressionInSelectSublist(querySpecification, result);
		}
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Some some) {
		ValueExpression valueExpression = some.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			StringLiteral stringLiteral) {
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Subquery subquery) {
		throw Sql4jException.getSql4jException(sourceCode, subquery.getBeginIndex(), 
				"Subqueries are not supported in select list.");
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			ToDate toDate) {
		ValueExpression valueExpression = toDate.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
		StringLiteral pattern = toDate.getPattern();
		analyzeValueExpressionInSelectSublist(querySpecification, pattern);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			ToChar toChar) {
		ValueExpression valueExpression = toChar.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
		StringLiteral pattern = toChar.getPattern();
		analyzeValueExpressionInSelectSublist(querySpecification, pattern);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Substring substring) {
		ValueExpression valueExpression = substring.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
		ValueExpression startPosition = substring.getStartPosition();
		analyzeValueExpressionInSelectSublist(querySpecification, startPosition);
		ValueExpression stringLength = substring.getStringLength();
		if (stringLength != null) {
			analyzeValueExpressionInSelectSublist(querySpecification, stringLength);
		}
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Subtraction subtraction) {
		ValueExpression left = subtraction.getLeft();
		analyzeValueExpressionInSelectSublist(querySpecification, left);
		ValueExpression right = subtraction.getRight();
		analyzeValueExpressionInSelectSublist(querySpecification, right);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Sum sum) {
		ValueExpression valueExpression = sum.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			TimeLiteral timeLiteral) {
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			TimestampLiteral timestampLiteral) {
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Trim trim) {
		ValueExpression trimCharacter = trim.getTrimCharacter();
		if (trimCharacter != null) {
			analyzeValueExpressionInSelectSublist(querySpecification, trimCharacter);
		}
		ValueExpression trimSource = trim.getTrimSource();
		analyzeValueExpressionInSelectSublist(querySpecification, trimSource);
	}

	private void analyzeValueExpressionInSelectSublist(QuerySpecification querySpecification, 
			Upper upper) {
		ValueExpression valueExpression = upper.getValueExpression();
		analyzeValueExpressionInSelectSublist(querySpecification, valueExpression);
	}
	
	////////////////////////////////////////////////
	
	private void analyzeTableReference(TableReference tableReference) {
		// 外层的可以访问内层的table，但是内层的table不能访问外层的table！！！
		if (tableReference instanceof TablePrimary) {
			return;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			BooleanValueExpression joinCondition = leftOuterJoin.getJoinCondition();
			analyzeJoinConditionValueExpressionByTableReference(leftOuterJoin, joinCondition);
			return;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			QuerySpecification querySpecification = getQuerySpecification(derivedTable);
			TableReference tableReference2 = getTableReference(querySpecification);
			analyzeTableReference(tableReference2);
			return;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			BooleanValueExpression joinCondition = rightOuterJoin.getJoinCondition();
			analyzeJoinConditionValueExpressionByTableReference(rightOuterJoin, joinCondition);
			return;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			BooleanValueExpression joinCondition = innerJoin.getJoinCondition();
			analyzeJoinConditionValueExpressionByTableReference(innerJoin, joinCondition);
			return;
		}
		if (tableReference instanceof FullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Full outer join is not supported.");
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
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, ValueExpression valueExpression) {
		if (valueExpression instanceof NameChain) {
			NameChain nameChain = (NameChain) valueExpression;
			boolean b = analyzeJoinConditionValueExpressionByTableReference2(tableReference, nameChain);
			if (b == false) {
				throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
						"Can't find the owner table of the column.");
			}
			return;
		}
		if (valueExpression instanceof BooleanValueExpression) {
			BooleanValueExpression booleanValueExpression = (BooleanValueExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, booleanValueExpression);
			return;
		}
		if (valueExpression instanceof AbsoluteValueExpression) {
			AbsoluteValueExpression absoluteValueExpression = (AbsoluteValueExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, absoluteValueExpression);
			return;
		}
		if (valueExpression instanceof Addition) {
			Addition addition = (Addition) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, addition);
			return;
		}
		if (valueExpression instanceof Any) {
			Any any = (Any) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, any);
			return;
		}
		if (valueExpression instanceof Avg) {
			Avg avg = (Avg) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, avg);
			return;
		}
		if (valueExpression instanceof BitLengthExpression) {
			BitLengthExpression bitLengthExpression = (BitLengthExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, bitLengthExpression);
			return;
		}
		if (valueExpression instanceof CardinalityExpression) {
			CardinalityExpression cardinalityExpression = (CardinalityExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, cardinalityExpression);
			return;
		}
		if (valueExpression instanceof CharLengthExpression) {
			CharLengthExpression charLengthExpression = (CharLengthExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, charLengthExpression);
			return;
		}
		if (valueExpression instanceof Coalesce) {
			Coalesce coalesce = (Coalesce) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, coalesce);
			return;
		}
		if (valueExpression instanceof Concatenation) {
			Concatenation concatenation = (Concatenation) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, concatenation);
			return;
		}
		if (valueExpression instanceof Count) {
			Count count = (Count) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, count);
			return;
		}
		if (valueExpression instanceof CurrentDate) {
			CurrentDate currentDate = (CurrentDate) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, currentDate);
			return;
		}
		if (valueExpression instanceof CurrentTime) {
			CurrentTime currentTime = (CurrentTime) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, currentTime);
			return;
		}
		if (valueExpression instanceof CurrentTimestamp) {
			CurrentTimestamp currentTimestamp = (CurrentTimestamp) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, currentTimestamp);
			return;
		}
		if (valueExpression instanceof DateLiteral) {
			DateLiteral dateLiteral = (DateLiteral) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, dateLiteral);
			return;
		}
		if (valueExpression instanceof Division) {
			Division division = (Division) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, division);
			return;
		}
		if (valueExpression instanceof Every) {
			Every every = (Every) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, every);
			return;
		}
		if (valueExpression instanceof ExtractExpression) {
			ExtractExpression extractExpression = (ExtractExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, extractExpression);
			return;
		}
		if (valueExpression instanceof FunctionInvocation) {
			FunctionInvocation functionInvocation = (FunctionInvocation) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, functionInvocation);
			return;
		}
		if (valueExpression instanceof Grouping) {
			Grouping grouping = (Grouping) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, grouping);
			return;
		}
		if (valueExpression instanceof Lower) {
			Lower lower = (Lower) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, lower);
			return;
		}
		if (valueExpression instanceof Max) {
			Max max = (Max) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, max);
			return;
		}
		if (valueExpression instanceof Min) {
			Min min = (Min) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, min);
			return;
		}
		if (valueExpression instanceof ModulusExpression) {
			ModulusExpression modulusExpression = (ModulusExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, modulusExpression);
			return;
		}
		if (valueExpression instanceof Multiplication) {
			Multiplication multiplication = (Multiplication) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, multiplication);
			return;
		}
		if (valueExpression instanceof NegativeExpression) {
			NegativeExpression negativeExpression = (NegativeExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, negativeExpression);
			return;
		}
		if (valueExpression instanceof NullIf) {
			NullIf nullIf = (NullIf) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, nullIf);
			return;
		}
		if (valueExpression instanceof NumericLiteral) {
			NumericLiteral numericLiteral = (NumericLiteral) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, numericLiteral);
			return;
		}
		if (valueExpression instanceof OctetLengthExpression) {
			OctetLengthExpression octetLengthExpression = (OctetLengthExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, octetLengthExpression);
			return;
		}
		if (valueExpression instanceof Parameter) {
			Parameter parameter = (Parameter) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, parameter);
			return;
		}
		if (valueExpression instanceof PositionExpression) {
			PositionExpression positionExpression = (PositionExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, positionExpression);
			return;
		}
		if (valueExpression instanceof PositiveExpression) {
			PositiveExpression positiveExpression = (PositiveExpression) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, positiveExpression);
			return;
		}
		if (valueExpression instanceof SearchedCase) {
			SearchedCase searchedCase = (SearchedCase) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, searchedCase);
			return;
		}
		if (valueExpression instanceof SimpleCase) {
			SimpleCase simpleCase = (SimpleCase) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, simpleCase);
			return;
		}
		if (valueExpression instanceof Some) {
			Some some = (Some) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, some);
			return;
		}
		if (valueExpression instanceof StringLiteral) {
			StringLiteral stringLiteral = (StringLiteral) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, stringLiteral);
			return;
		}
		if (valueExpression instanceof Subquery) {
			Subquery subquery = (Subquery) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, subquery);
			return;
		}
		if (valueExpression instanceof ToDate) {
			ToDate toDate = (ToDate) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, toDate);
			return;
		}
		if (valueExpression instanceof ToChar) {
			ToChar toChar = (ToChar) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, toChar);
			return;
		}
		if (valueExpression instanceof Substring) {
			Substring substring = (Substring) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, substring);
			return;
		}
		if (valueExpression instanceof Subtraction) {
			Subtraction subtraction = (Subtraction) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, subtraction);
			return;
		}
		if (valueExpression instanceof Sum) {
			Sum sum = (Sum) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, sum);
			return;
		}
		if (valueExpression instanceof TimeLiteral) {
			TimeLiteral timeLiteral = (TimeLiteral) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, timeLiteral);
			return;
		}
		if (valueExpression instanceof TimestampLiteral) {
			TimestampLiteral timestampLiteral = (TimestampLiteral) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, timestampLiteral);
			return;
		}
		if (valueExpression instanceof Trim) {
			Trim trim = (Trim) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, trim);
			return;
		}
		if (valueExpression instanceof Upper) {
			Upper upper = (Upper) valueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, upper);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
				"Not support the value exrepssion.");
	}
	
	private boolean analyzeJoinConditionValueExpressionByTableReference2(TableReference tableReference, NameChain nameChain) {
		if (tableReference instanceof TablePrimary) {
			TablePrimary tablePrimary = (TablePrimary) tableReference;
			boolean b = analyzeJoinConditionValueExpressionByTablePrimary(tablePrimary, nameChain);
			return b;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			TableReference left = leftOuterJoin.getLeft();
			analyzeTableReference(left);
			boolean b = analyzeJoinConditionValueExpressionByTableReference2(left, nameChain);
			if (b == true) {
				return b;
			}
			TableReference right = leftOuterJoin.getRight();
			analyzeTableReference(right);
			b = analyzeJoinConditionValueExpressionByTableReference2(right, nameChain);
			return b;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			boolean b = analyzeJoinConditionValueExpressionByDerivedTable(derivedTable, nameChain);
			return b;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			analyzeTableReference(left);
			boolean b = analyzeJoinConditionValueExpressionByTableReference2(left, nameChain);
			if (b == true) {
				return b;
			}
			TableReference right = rightOuterJoin.getRight();
			analyzeTableReference(right);
			b = analyzeJoinConditionValueExpressionByTableReference2(right, nameChain);
			return b;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			analyzeTableReference(left);
			boolean b = analyzeJoinConditionValueExpressionByTableReference2(left, nameChain);
			if (b == true) {
				return b;
			}
			TableReference right = innerJoin.getRight();
			analyzeTableReference(right);
			b = analyzeJoinConditionValueExpressionByTableReference2(right, nameChain);
			return b;
		}
		if (tableReference instanceof FullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Full outer join is not supported.");
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
	
	private boolean analyzeJoinConditionValueExpressionByTablePrimary(TablePrimary tablePrimary, NameChain nameChain) {
		String nameChainTableNameContent = getTableNameByColumnName(nameChain);
		if (nameChainTableNameContent == null || nameChainTableNameContent.length() == 0) {
			return false;
		}
		Name correlationName = tablePrimary.getCorrelationName();
		if (correlationName != null) {
			String correlationNameContent = correlationName.getContent();
			if (nameChainTableNameContent.equalsIgnoreCase(correlationNameContent) == false) {
				return false;
			}
			NameChain tablePrimaryTableName = tablePrimary.getTableName();
			String tablePrimaryTableNameContent = tablePrimaryTableName.toLowerCaseString();
			TableMetadata tableMetadata = configuration.getTableMetadata(tablePrimaryTableNameContent);
			Name simpleColumnName = nameChain.get(nameChain.size() - 1);
			String simpleColumnNameContent = simpleColumnName.getContent();
			if (tableMetadata.hasColumnMetadata(simpleColumnNameContent) == false) {
				return false;
			}
			NameChain fullyQualifiedColumnNames = genFullyQualifiedColumnNames(tablePrimaryTableName, simpleColumnName);
			nameChain.setFullyQualifiedName(fullyQualifiedColumnNames);
			return true;
		}
		NameChain tablePrimaryTableName = tablePrimary.getTableName();
		String tablePrimaryTableNameContent = tablePrimaryTableName.toLowerCaseString();
		if (nameChainTableNameContent.equalsIgnoreCase(tablePrimaryTableNameContent) == false) {
			return false;
		}
		TableMetadata tableMetadata = configuration.getTableMetadata(tablePrimaryTableNameContent);
		Name simpleColumnName = nameChain.get(nameChain.size() - 1);
		String simpleColumnNameContent = simpleColumnName.getContent();
		if (tableMetadata.hasColumnMetadata(simpleColumnNameContent) == false) {
			return false;
		}
		NameChain fullyQualifiedColumnNames = genFullyQualifiedColumnNames(tablePrimaryTableName, simpleColumnName);
		nameChain.setFullyQualifiedName(fullyQualifiedColumnNames);
		return true;
	}
	
	private boolean analyzeJoinConditionValueExpressionByDerivedTable(DerivedTable derivedTable, NameChain nameChain) {
		Name correlationName = derivedTable.getCorrelationName();
		String correlationNameContent = correlationName.getContent();
		String nameChainTableNameContent = getTableNameByColumnName(nameChain);
		if (nameChainTableNameContent == null || nameChainTableNameContent.length() == 0) {
			return false;
		}
		if (correlationNameContent.equalsIgnoreCase(nameChainTableNameContent) == false) {
			return false;
		}
		Name simpleColumnName = nameChain.get(nameChain.size() - 1);
		String simpleColumnNameContent = simpleColumnName.getContent();
		QuerySpecification querySpecification = getQuerySpecification(derivedTable);
		List<SelectSublist> selectList = querySpecification.getSelectList();
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			Name name = selectSublist.getName();
			if (name != null) {
				String columnName = name.getContent();
				if (simpleColumnNameContent.equalsIgnoreCase(columnName) == false) {
					continue;
				}
				ValueExpression valueExpression = selectSublist.getValueExpression();
				ValueExpression fullyQualifiedColumnNames;
				if (valueExpression instanceof NameChain) {
					NameChain column = (NameChain) valueExpression;
					fullyQualifiedColumnNames = column.getFullyQualifiedName();
				} else {
					fullyQualifiedColumnNames = valueExpression;
				}
				nameChain.setFullyQualifiedName(fullyQualifiedColumnNames);
				return true;
			}
			ValueExpression valueExpression = selectSublist.getValueExpression();
			if (valueExpression instanceof NameChain == false) {
				continue;
			}
			NameChain column = (NameChain) valueExpression;
			String columnName = column.get(column.size() - 1).getContent();
			if (simpleColumnNameContent.equalsIgnoreCase(columnName) == false) {
				continue;
			}
			ValueExpression fullyQualifiedColumnNames = column.getFullyQualifiedName();
			nameChain.setFullyQualifiedName(fullyQualifiedColumnNames);
			return true;
		}
		return false;
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue) booleanValueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, booleanValue);
			return;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = (Predicate) booleanValueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, predicate);
			return;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = (BooleanFactor) booleanValueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, booleanFactor);
			return;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, booleanTerm);
			return;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, booleanTest);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, booleanValueExpression.getBeginIndex(), 
				"Not support the boolean value exrepssion.");
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, BooleanValue booleanValue) {
		for (int i = 0; i < booleanValue.size(); i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			analyzeJoinConditionValueExpressionByTableReference(tableReference, booleanTerm);
		}
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = (ComparisonPredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, comparisonPredicate);
			return;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = (BetweenPredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, betweenPredicate);
			return;
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = (DistinctPredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, distinctPredicate);
			return;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = (ExistsPredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, existsPredicate);
			return;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = (InPredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, inPredicate);
			return;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = (LikePredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, likePredicate);
			return;
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = (MatchPredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, matchPredicate);
			return;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = (NullPredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, nullPredicate);
			return;
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = (OverlapsPredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, overlapsPredicate);
			return;
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = (SimilarPredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, similarPredicate);
			return;
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = (UniquePredicate) predicate;
			analyzeJoinConditionValueExpressionByTableReference(tableReference, uniquePredicate);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"Not support the predicate.");
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, left);
		ValueExpression right = comparisonPredicate.getRight();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, right);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, BetweenPredicate betweenPredicate) {
		ValueExpression valueExpression = betweenPredicate.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
		ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression1);
		ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression2);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, DistinctPredicate distinctPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, distinctPredicate.getBeginIndex(), "Distinct predicate is not supported.");
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, ExistsPredicate existsPredicate) {
		Subquery subquery = existsPredicate.getSubquery();
		SelectStatement selectStatement = subquery.getSelectStatement();
		QuerySpecification querySpecification = getQuerySpecification(selectStatement);
		analyze(querySpecification);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, InPredicate inPredicate) {
		ValueExpression valueExpression = inPredicate.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
		List<ValueExpression> inValueList = inPredicate.getInValueList();
		if (inValueList != null) {
			for (int i = 0; i < inValueList.size(); i++) {
				ValueExpression valueExpression_ = inValueList.get(i);
				analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression_);
			}
			return;
		}
		Subquery subquery = inPredicate.getSubquery();
		if (subquery != null) {
			analyzeJoinConditionValueExpressionByTableReference(tableReference, subquery);
		}
		return;
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, LikePredicate likePredicate) {
		ValueExpression valueExpression = likePredicate.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
		ValueExpression characterPattern = likePredicate.getCharacterPattern();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, characterPattern);
		ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
		if (escapeCharacter != null) {
			analyzeJoinConditionValueExpressionByTableReference(tableReference, escapeCharacter);
		}
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, MatchPredicate matchPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, matchPredicate.getBeginIndex(), "Match predicate is not supported.");
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, NullPredicate nullPredicate) {
		ValueExpression valueExpression = nullPredicate.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, OverlapsPredicate overlapsPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, overlapsPredicate.getBeginIndex(), "Overlaps predicate is not supported.");
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, SimilarPredicate similarPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, similarPredicate.getBeginIndex(), "Similar predicate is not supported.");
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, UniquePredicate uniquePredicate) {
		throw Sql4jException.getSql4jException(sourceCode, uniquePredicate.getBeginIndex(), "Unique predicate is not supported.");
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = booleanFactor.getBooleanValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, booleanValueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, BooleanTerm booleanTerm) {
		for (int i = 0; i < booleanTerm.size(); i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			analyzeJoinConditionValueExpressionByTableReference(tableReference, booleanFactor);
		}
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = booleanTest.getBooleanValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, booleanValueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, AbsoluteValueExpression absoluteValueExpression) {
		ValueExpression valueExpression = absoluteValueExpression.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Addition addition) {
		ValueExpression left = addition.getLeft();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, left);
		ValueExpression right = addition.getRight();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, right);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Any any) {
		ValueExpression valueExpression = any.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Avg avg) {
		ValueExpression valueExpression = avg.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, BitLengthExpression bitLengthExpression) {
		ValueExpression valueExpression = bitLengthExpression.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, CardinalityExpression cardinalityExpression) {
		ValueExpression valueExpression = cardinalityExpression.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, CharLengthExpression charLengthExpression) {
		ValueExpression valueExpression = charLengthExpression.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Coalesce coalesce) {
		List<ValueExpression> list = coalesce.getArguments();
		for (int i = 0; i < list.size(); i++) {
			ValueExpression valueExpression = list.get(i);
			analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
		}
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Concatenation concatenation) {
		ValueExpression left = concatenation.getLeft();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, left);
		ValueExpression right = concatenation.getRight();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, right);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Count count) {
		ValueExpression valueExpression = count.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, CurrentDate currentDate) {
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, CurrentTime currentTime) {
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, CurrentTimestamp currentTimestamp) {
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, DateLiteral dateLiteral) {
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Division division) {
		ValueExpression left = division.getLeft();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, left);
		ValueExpression right = division.getRight();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, right);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Every every) {
		ValueExpression valueExpression = every.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, ExtractExpression extractExpression) {
		ValueExpression extractSource = extractExpression.getExtractSource();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, extractSource);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, FunctionInvocation functionInvocation) {
		List<ValueExpression> list = functionInvocation.getArguments();
		for (int i = 0; i < list.size(); i++) {
			ValueExpression valueExpression = list.get(i);
			analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
		}
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Grouping grouping) {
		NameChain columnReference = grouping.getColumnReference();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, columnReference);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Lower lower) {
		ValueExpression valueExpression = lower.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Max max) {
		ValueExpression valueExpression = max.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Min min) {
		ValueExpression valueExpression = min.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, ModulusExpression modulusExpression) {
		ValueExpression dividend = modulusExpression.getDividend();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, dividend);
		ValueExpression divisor = modulusExpression.getDivisor();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, divisor);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Multiplication multiplication) {
		ValueExpression left = multiplication.getLeft();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, left);
		ValueExpression right = multiplication.getRight();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, right);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, NegativeExpression negativeExpression) {
		ValueExpression valueExpression = negativeExpression.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, NullIf nullIf) {
		ValueExpression first = nullIf.getFirst();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, first);
		ValueExpression second = nullIf.getSecond();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, second);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, NumericLiteral numericLiteral) {
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, OctetLengthExpression octetLengthExpression) {
		ValueExpression valueExpression = octetLengthExpression.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Parameter parameter) {
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, PositionExpression positionExpression) {
		ValueExpression valueExpression1 = positionExpression.getValueExpression1();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression1);
		ValueExpression valueExpression2 = positionExpression.getValueExpression2();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression2);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, PositiveExpression positiveExpression) {
		ValueExpression valueExpression = positiveExpression.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, SearchedCase searchedCase) {
		List<SearchedWhenClause> searchedWhenClauseList = searchedCase.getSearchedWhenClauseList();
		for (int i = 0; i < searchedWhenClauseList.size(); i++) {
			SearchedWhenClause searchedWhenClause = searchedWhenClauseList.get(i);
			BooleanValueExpression searchedCondition = searchedWhenClause.getSearchedCondition();
			analyzeJoinConditionValueExpressionByTableReference(tableReference, searchedCondition);
			ValueExpression result = searchedWhenClause.getResult();
			analyzeJoinConditionValueExpressionByTableReference(tableReference, result);
		}
		ElseClause elseClause = searchedCase.getElseClause();
		if (elseClause != null) {
			ValueExpression result = elseClause.getResult();
			analyzeJoinConditionValueExpressionByTableReference(tableReference, result);
		}
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, SimpleCase simpleCase) {
		ValueExpression caseOperand = simpleCase.getCaseOperand();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, caseOperand);
		List<SimpleWhenClause> simpleWhenClauseList = simpleCase.getSimpleWhenClauseList();
		for (int i = 0; i < simpleWhenClauseList.size(); i++) {
			SimpleWhenClause simpleWhenClause = simpleWhenClauseList.get(i);
			ValueExpression whenOperand = simpleWhenClause.getWhenOperand();
			analyzeJoinConditionValueExpressionByTableReference(tableReference, whenOperand);
			ValueExpression result = simpleWhenClause.getResult();
			analyzeJoinConditionValueExpressionByTableReference(tableReference, result);
		}
		ElseClause elseClause = simpleCase.getElseClause();
		if (elseClause != null) {
			ValueExpression result = elseClause.getResult();
			analyzeJoinConditionValueExpressionByTableReference(tableReference, result);
		}
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Some some) {
		ValueExpression valueExpression = some.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, StringLiteral stringLiteral) {
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Subquery subquery) {
		throw Sql4jException.getSql4jException(sourceCode, subquery.getBeginIndex(), 
				"Subqueries are not supported in join condition.");
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, ToDate toDate) {
		ValueExpression valueExpression = toDate.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, ToChar toChar) {
		ValueExpression valueExpression = toChar.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Substring substring) {
		ValueExpression valueExpression = substring.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
		ValueExpression startPosition = substring.getStartPosition();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, startPosition);
		ValueExpression stringLength = substring.getStringLength();
		if (stringLength != null) {
			analyzeJoinConditionValueExpressionByTableReference(tableReference, stringLength);
		}
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Subtraction subtraction) {
		ValueExpression left = subtraction.getLeft();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, left);
		ValueExpression right = subtraction.getRight();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, right);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Sum sum) {
		ValueExpression valueExpression = sum.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, TimeLiteral timeLiteral) {
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, TimestampLiteral timestampLiteral) {
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Trim trim) {
		ValueExpression trimCharacter = trim.getTrimCharacter();
		if (trimCharacter != null) {
			analyzeJoinConditionValueExpressionByTableReference(tableReference, trimCharacter);
		}
		ValueExpression trimSource = trim.getTrimSource();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, trimSource);
	}
	
	private void analyzeJoinConditionValueExpressionByTableReference(TableReference tableReference, Upper upper) {
		ValueExpression valueExpression = upper.getValueExpression();
		analyzeJoinConditionValueExpressionByTableReference(tableReference, valueExpression);
	}
	
	////////////////////////////////////////////////
	
	private void analyzeWhereSearchCondition(QuerySpecification querySpecification, BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue) booleanValueExpression;
			analyzeWhereSearchCondition(querySpecification, booleanValue);
			return;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = (Predicate) booleanValueExpression;
			analyzeWhereSearchCondition(querySpecification, predicate);
			return;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = (BooleanFactor) booleanValueExpression;
			analyzeWhereSearchCondition(querySpecification, booleanFactor);
			return;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			analyzeWhereSearchCondition(querySpecification, booleanTerm);
			return;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			analyzeWhereSearchCondition(querySpecification, booleanTest);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, booleanValueExpression.getBeginIndex(), 
				"Not support the boolean value exrepssion.");
	}
	
	private void analyzeWhereSearchCondition(QuerySpecification querySpecification, BooleanValue booleanValue) {
		for (int i = 0; i < booleanValue.size(); i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			analyzeWhereSearchCondition(querySpecification, booleanTerm);
		}
	}
	
	private void analyzeWhereSearchCondition(QuerySpecification querySpecification, Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = (ComparisonPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, comparisonPredicate);
			return;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = (BetweenPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, betweenPredicate);
			return;
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = (DistinctPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, distinctPredicate);
			return;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = (ExistsPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, existsPredicate);
			return;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = (InPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, inPredicate);
			return;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = (LikePredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, likePredicate);
			return;
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = (MatchPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, matchPredicate);
			return;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = (NullPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, nullPredicate);
			return;
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = (OverlapsPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, overlapsPredicate);
			return;
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = (SimilarPredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, similarPredicate);
			return;
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = (UniquePredicate) predicate;
			analyzeValueExpressionInSelectSublist(querySpecification, uniquePredicate);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"Not support the predicate.");
	}
	
	private void analyzeWhereSearchCondition(QuerySpecification querySpecification, BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = booleanFactor.getBooleanValueExpression();
		analyzeWhereSearchCondition(querySpecification, booleanValueExpression);
	}
	
	private void analyzeWhereSearchCondition(QuerySpecification querySpecification, BooleanTerm booleanTerm) {
		for (int i = 0; i < booleanTerm.size(); i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			analyzeWhereSearchCondition(querySpecification, booleanFactor);
		}
	}
	
	private void analyzeWhereSearchCondition(QuerySpecification querySpecification, BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = booleanTest.getBooleanValueExpression();
		analyzeWhereSearchCondition(querySpecification, booleanValueExpression);
	}
	
	////////////////////////////////////////////////
	
	private void analyzeGroupingElementList(QuerySpecification querySpecification, List<GroupingElement> groupingElementList) {
		for (int i = 0; i < groupingElementList.size(); i++) {
			GroupingElement groupingElement = groupingElementList.get(i);
			analyzeGroupingElement(querySpecification, groupingElement);
		}
	}
	
	private void analyzeGroupingElement(QuerySpecification querySpecification, GroupingElement groupingElement) {
		if (groupingElement instanceof OrdinaryGroupingSet) {
			OrdinaryGroupingSet ordinaryGroupingSet = (OrdinaryGroupingSet) groupingElement;
			analyzeOrdinaryGroupingSet(querySpecification, ordinaryGroupingSet);
			return;
		}
		if (groupingElement instanceof CubeList) {
			CubeList cubeList = (CubeList) groupingElement;
			analyzeCubeList(querySpecification, cubeList);
			return;
		}
		if (groupingElement instanceof RollupList) {
			RollupList rollupList = (RollupList) groupingElement;
			analyzeRollupList(querySpecification, rollupList);
			return;
		}
		if (groupingElement instanceof GroupingSetsSpecification) {
			GroupingSetsSpecification groupingSetsSpecification = 
					(GroupingSetsSpecification) groupingElement;
			analyzeGroupingSetsSpecification(querySpecification, groupingSetsSpecification);
			return;
		}
		if (groupingElement instanceof GrandTotal) {
			GrandTotal grandTotal = (GrandTotal) groupingElement;
			analyzeGrandTotal(querySpecification, grandTotal);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, groupingElement.getBeginIndex(), 
				"This grouping element is not supported.");
	}
	
	private void analyzeOrdinaryGroupingSet(QuerySpecification querySpecification, OrdinaryGroupingSet ordinaryGroupingSet) {
		for (int i = 0; i < ordinaryGroupingSet.size(); i++) {
			GroupingColumnReference groupingColumnReference = ordinaryGroupingSet.get(i);
			analyzeGroupingColumnReference(querySpecification, groupingColumnReference);
		}
	}
	
	private void analyzeGroupingColumnReference(QuerySpecification querySpecification, GroupingColumnReference groupingColumnReference) {
		List<SelectSublist> selectList = querySpecification.getSelectList();
		NameChain columnReference = groupingColumnReference.getColumnReference();
		ValueExpression valueExpression = getGroupingColumnReferenceValueExpression(selectList, columnReference);
		if (valueExpression != null && valueExpression instanceof NameChain) {
			NameChain nameChain = (NameChain) valueExpression;
			ValueExpression fullyQualifiedColumnName = nameChain.getFullyQualifiedName();
			columnReference.setFullyQualifiedName(fullyQualifiedColumnName);
			return;
		}
		TableReference tableReference = getTableReference(querySpecification);
		ValueExpression valueExpression2 = getGroupingColumnReferenceValueExpression(tableReference, columnReference);
		if (valueExpression2 != null && valueExpression2 instanceof NameChain) {
			NameChain nameChain = (NameChain) valueExpression2;
			ValueExpression fullyQualifiedColumnName = nameChain.getFullyQualifiedName();
			columnReference.setFullyQualifiedName(fullyQualifiedColumnName);
		}
	}
	
	private void analyzeCubeList(QuerySpecification querySpecification, CubeList cubeList) {
		for (int i = 0; i < cubeList.size(); i++) {
			GroupingColumnReference groupingColumnReference = cubeList.get(i);
			analyzeGroupingColumnReference(querySpecification, groupingColumnReference);
		}
	}
	
	private void analyzeRollupList(QuerySpecification querySpecification, RollupList rollupList) {
		for (int i = 0; i < rollupList.size(); i++) {
			GroupingColumnReference groupingColumnReference = rollupList.get(i);
			analyzeGroupingColumnReference(querySpecification, groupingColumnReference);
		}
	}
	
	private void analyzeGroupingSetsSpecification(QuerySpecification querySpecification, GroupingSetsSpecification groupingSetsSpecification) {
		for (int i = 0; i < groupingSetsSpecification.size(); i++) {
			GroupingElement groupingElement = groupingSetsSpecification.get(i);
			analyzeGroupingElement(querySpecification, groupingElement);
		}
	}
	
	private void analyzeGrandTotal(QuerySpecification querySpecification, GrandTotal grandTotal) {
		throw Sql4jException.getSql4jException(sourceCode, grandTotal.getBeginIndex(), 
				"Grand total is not supported.");
	}
	
	////////////////////////////////////////////////
	
	private void analyzeSortSpecificationList(QuerySpecification querySpecification, List<SortSpecification> sortSpecificationList) {
		for (int i = 0; i < sortSpecificationList.size(); i++) {
			SortSpecification sortSpecification = sortSpecificationList.get(i);
			analyzeSortSpecification(querySpecification, sortSpecification);
		}
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, SortSpecification sortSpecification) {
		ValueExpression sortKey = sortSpecification.getSortKey();
		analyzeSortSpecification(querySpecification, sortKey);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, ValueExpression valueExpression) {
		if (valueExpression instanceof NameChain) {
			NameChain nameChain = (NameChain) valueExpression;
			analyzeSortSpecification(querySpecification, nameChain);
			return;
		}
		if (valueExpression instanceof BooleanValueExpression) {
			BooleanValueExpression booleanValueExpression = (BooleanValueExpression) valueExpression;
			analyzeSortSpecification(querySpecification, booleanValueExpression);
			return;
		}
		if (valueExpression instanceof AbsoluteValueExpression) {
			AbsoluteValueExpression absoluteValueExpression = (AbsoluteValueExpression) valueExpression;
			analyzeSortSpecification(querySpecification, absoluteValueExpression);
			return;
		}
		if (valueExpression instanceof Addition) {
			Addition addition = (Addition) valueExpression;
			analyzeSortSpecification(querySpecification, addition);
			return;
		}
		if (valueExpression instanceof Any) {
			Any any = (Any) valueExpression;
			analyzeSortSpecification(querySpecification, any);
			return;
		}
		if (valueExpression instanceof Avg) {
			Avg avg = (Avg) valueExpression;
			analyzeSortSpecification(querySpecification, avg);
			return;
		}
		if (valueExpression instanceof BitLengthExpression) {
			BitLengthExpression bitLengthExpression = (BitLengthExpression) valueExpression;
			analyzeSortSpecification(querySpecification, bitLengthExpression);
			return;
		}
		if (valueExpression instanceof CardinalityExpression) {
			CardinalityExpression cardinalityExpression = (CardinalityExpression) valueExpression;
			analyzeSortSpecification(querySpecification, cardinalityExpression);
			return;
		}
		if (valueExpression instanceof CharLengthExpression) {
			CharLengthExpression charLengthExpression = (CharLengthExpression) valueExpression;
			analyzeSortSpecification(querySpecification, charLengthExpression);
			return;
		}
		if (valueExpression instanceof Coalesce) {
			Coalesce coalesce = (Coalesce) valueExpression;
			analyzeSortSpecification(querySpecification, coalesce);
			return;
		}
		if (valueExpression instanceof Concatenation) {
			Concatenation concatenation = (Concatenation) valueExpression;
			analyzeSortSpecification(querySpecification, concatenation);
			return;
		}
		if (valueExpression instanceof Count) {
			Count count = (Count) valueExpression;
			analyzeSortSpecification(querySpecification, count);
			return;
		}
		if (valueExpression instanceof CurrentDate) {
			CurrentDate currentDate = (CurrentDate) valueExpression;
			analyzeSortSpecification(querySpecification, currentDate);
			return;
		}
		if (valueExpression instanceof CurrentTime) {
			CurrentTime currentTime = (CurrentTime) valueExpression;
			analyzeSortSpecification(querySpecification, currentTime);
			return;
		}
		if (valueExpression instanceof CurrentTimestamp) {
			CurrentTimestamp currentTimestamp = (CurrentTimestamp) valueExpression;
			analyzeSortSpecification(querySpecification, currentTimestamp);
			return;
		}
		if (valueExpression instanceof DateLiteral) {
			DateLiteral dateLiteral = (DateLiteral) valueExpression;
			analyzeSortSpecification(querySpecification, dateLiteral);
			return;
		}
		if (valueExpression instanceof Division) {
			Division division = (Division) valueExpression;
			analyzeSortSpecification(querySpecification, division);
			return;
		}
		if (valueExpression instanceof Every) {
			Every every = (Every) valueExpression;
			analyzeSortSpecification(querySpecification, every);
			return;
		}
		if (valueExpression instanceof ExtractExpression) {
			ExtractExpression extractExpression = (ExtractExpression) valueExpression;
			analyzeSortSpecification(querySpecification, extractExpression);
			return;
		}
		if (valueExpression instanceof FunctionInvocation) {
			FunctionInvocation functionInvocation = (FunctionInvocation) valueExpression;
			analyzeSortSpecification(querySpecification, functionInvocation);
			return;
		}
		if (valueExpression instanceof Grouping) {
			Grouping grouping = (Grouping) valueExpression;
			analyzeSortSpecification(querySpecification, grouping);
			return;
		}
		if (valueExpression instanceof Lower) {
			Lower lower = (Lower) valueExpression;
			analyzeSortSpecification(querySpecification, lower);
			return;
		}
		if (valueExpression instanceof Max) {
			Max max = (Max) valueExpression;
			analyzeSortSpecification(querySpecification, max);
			return;
		}
		if (valueExpression instanceof Min) {
			Min min = (Min) valueExpression;
			analyzeSortSpecification(querySpecification, min);
			return;
		}
		if (valueExpression instanceof ModulusExpression) {
			ModulusExpression modulusExpression = (ModulusExpression) valueExpression;
			analyzeSortSpecification(querySpecification, modulusExpression);
			return;
		}
		if (valueExpression instanceof Multiplication) {
			Multiplication multiplication = (Multiplication) valueExpression;
			analyzeSortSpecification(querySpecification, multiplication);
			return;
		}
		if (valueExpression instanceof NegativeExpression) {
			NegativeExpression negativeExpression = (NegativeExpression) valueExpression;
			analyzeSortSpecification(querySpecification, negativeExpression);
			return;
		}
		if (valueExpression instanceof NullIf) {
			NullIf nullIf = (NullIf) valueExpression;
			analyzeSortSpecification(querySpecification, nullIf);
			return;
		}
		if (valueExpression instanceof NumericLiteral) {
			NumericLiteral numericLiteral = (NumericLiteral) valueExpression;
			analyzeSortSpecification(querySpecification, numericLiteral);
			return;
		}
		if (valueExpression instanceof OctetLengthExpression) {
			OctetLengthExpression octetLengthExpression = (OctetLengthExpression) valueExpression;
			analyzeSortSpecification(querySpecification, octetLengthExpression);
			return;
		}
		if (valueExpression instanceof Parameter) {
			Parameter parameter = (Parameter) valueExpression;
			analyzeSortSpecification(querySpecification, parameter);
			return;
		}
		if (valueExpression instanceof PositionExpression) {
			PositionExpression positionExpression = (PositionExpression) valueExpression;
			analyzeSortSpecification(querySpecification, positionExpression);
			return;
		}
		if (valueExpression instanceof PositiveExpression) {
			PositiveExpression positiveExpression = (PositiveExpression) valueExpression;
			analyzeSortSpecification(querySpecification, positiveExpression);
			return;
		}
		if (valueExpression instanceof SearchedCase) {
			SearchedCase searchedCase = (SearchedCase) valueExpression;
			analyzeSortSpecification(querySpecification, searchedCase);
			return;
		}
		if (valueExpression instanceof SimpleCase) {
			SimpleCase simpleCase = (SimpleCase) valueExpression;
			analyzeSortSpecification(querySpecification, simpleCase);
			return;
		}
		if (valueExpression instanceof Some) {
			Some some = (Some) valueExpression;
			analyzeSortSpecification(querySpecification, some);
			return;
		}
		if (valueExpression instanceof StringLiteral) {
			StringLiteral stringLiteral = (StringLiteral) valueExpression;
			analyzeSortSpecification(querySpecification, stringLiteral);
			return;
		}
		if (valueExpression instanceof Subquery) {
			Subquery subquery = (Subquery) valueExpression;
			analyzeSortSpecification(querySpecification, subquery);
			return;
		}
		if (valueExpression instanceof ToDate) {
			ToDate toDate = (ToDate) valueExpression;
			analyzeSortSpecification(querySpecification, toDate);
			return;
		}
		if (valueExpression instanceof ToChar) {
			ToChar toChar = (ToChar) valueExpression;
			analyzeSortSpecification(querySpecification, toChar);
			return;
		}
		if (valueExpression instanceof Substring) {
			Substring substring = (Substring) valueExpression;
			analyzeSortSpecification(querySpecification, substring);
			return;
		}
		if (valueExpression instanceof Subtraction) {
			Subtraction subtraction = (Subtraction) valueExpression;
			analyzeSortSpecification(querySpecification, subtraction);
			return;
		}
		if (valueExpression instanceof Sum) {
			Sum sum = (Sum) valueExpression;
			analyzeSortSpecification(querySpecification, sum);
			return;
		}
		if (valueExpression instanceof TimeLiteral) {
			TimeLiteral timeLiteral = (TimeLiteral) valueExpression;
			analyzeSortSpecification(querySpecification, timeLiteral);
			return;
		}
		if (valueExpression instanceof TimestampLiteral) {
			TimestampLiteral timestampLiteral = (TimestampLiteral) valueExpression;
			analyzeSortSpecification(querySpecification, timestampLiteral);
			return;
		}
		if (valueExpression instanceof Trim) {
			Trim trim = (Trim) valueExpression;
			analyzeSortSpecification(querySpecification, trim);
			return;
		}
		if (valueExpression instanceof Upper) {
			Upper upper = (Upper) valueExpression;
			analyzeSortSpecification(querySpecification, upper);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
				"This value exrepssion is not supported.");
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, NameChain nameChain) {
		List<SelectSublist> selectList = querySpecification.getSelectList();
		ValueExpression fullyQualifiedColumnName = getFullyQualifiedColumnName(selectList, nameChain);
		if (fullyQualifiedColumnName == null) {
			TableReference tableReference = getTableReference(querySpecification);
			fullyQualifiedColumnName = getFullyQualifiedColumnName(tableReference, nameChain);
		}
		nameChain.setFullyQualifiedName(fullyQualifiedColumnName);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, BooleanValueExpression booleanValueExpression) {
		throw Sql4jException.getSql4jException(sourceCode, booleanValueExpression.getBeginIndex(), 
				"Boolean value expression is not supported in sort specification list.");
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, AbsoluteValueExpression absoluteValueExpression) {
		ValueExpression valueExpression = absoluteValueExpression.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Addition addition) {
		ValueExpression left = addition.getLeft();
		analyzeSortSpecification(querySpecification, left);
		ValueExpression right = addition.getRight();
		analyzeSortSpecification(querySpecification, right);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Any any) {
		ValueExpression valueExpression = any.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Avg avg) {
		ValueExpression valueExpression = avg.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, BitLengthExpression bitLengthExpression) {
		ValueExpression valueExpression = bitLengthExpression.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, CardinalityExpression cardinalityExpression) {
		ValueExpression valueExpression = cardinalityExpression.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, CharLengthExpression charLengthExpression) {
		ValueExpression valueExpression = charLengthExpression.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Coalesce coalesce) {
		List<ValueExpression> list = coalesce.getArguments();
		for (int i = 0; i < list.size(); i++) {
			ValueExpression valueExpression = list.get(i);
			analyzeSortSpecification(querySpecification, valueExpression);
		}
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Concatenation concatenation) {
		ValueExpression left = concatenation.getLeft();
		analyzeSortSpecification(querySpecification, left);
		ValueExpression right = concatenation.getRight();
		analyzeSortSpecification(querySpecification, right);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Count count) {
		ValueExpression valueExpression = count.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, CurrentDate currentDate) {
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, CurrentTime currentTime) {
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, CurrentTimestamp currentTimestamp) {
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, DateLiteral dateLiteral) {
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Division division) {
		ValueExpression left = division.getLeft();
		analyzeSortSpecification(querySpecification, left);
		ValueExpression right = division.getRight();
		analyzeSortSpecification(querySpecification, right);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Every every) {
		ValueExpression valueExpression = every.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, ExtractExpression extractExpression) {
		ValueExpression extractSource = extractExpression.getExtractSource();
		analyzeSortSpecification(querySpecification, extractSource);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, FunctionInvocation functionInvocation) {
		List<ValueExpression> list = functionInvocation.getArguments();
		for (int i = 0; i < list.size(); i++) {
			ValueExpression valueExpression = list.get(i);
			analyzeSortSpecification(querySpecification, valueExpression);
		}
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Grouping grouping) {
		NameChain columnReference = grouping.getColumnReference();
		analyzeSortSpecification(querySpecification, columnReference);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Lower lower) {
		ValueExpression valueExpression = lower.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Max max) {
		ValueExpression valueExpression = max.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Min min) {
		ValueExpression valueExpression = min.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, ModulusExpression modulusExpression) {
		ValueExpression dividend = modulusExpression.getDividend();
		analyzeSortSpecification(querySpecification, dividend);
		ValueExpression divisor = modulusExpression.getDivisor();
		analyzeSortSpecification(querySpecification, divisor);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Multiplication multiplication) {
		ValueExpression left = multiplication.getLeft();
		analyzeSortSpecification(querySpecification, left);
		ValueExpression right = multiplication.getRight();
		analyzeSortSpecification(querySpecification, right);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, NegativeExpression negativeExpression) {
		ValueExpression valueExpression = negativeExpression.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, NullIf nullIf) {
		ValueExpression first = nullIf.getFirst();
		analyzeSortSpecification(querySpecification, first);
		ValueExpression second = nullIf.getSecond();
		analyzeSortSpecification(querySpecification, second);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, NumericLiteral numericLiteral) {
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, OctetLengthExpression octetLengthExpression) {
		ValueExpression valueExpression = octetLengthExpression.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Parameter parameter) {
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, PositionExpression positionExpression) {
		ValueExpression valueExpression1 = positionExpression.getValueExpression1();
		analyzeSortSpecification(querySpecification, valueExpression1);
		ValueExpression valueExpression2 = positionExpression.getValueExpression2();
		analyzeSortSpecification(querySpecification, valueExpression2);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, PositiveExpression positiveExpression) {
		ValueExpression valueExpression = positiveExpression.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, SearchedCase searchedCase) {
		List<SearchedWhenClause> searchedWhenClauseList = searchedCase.getSearchedWhenClauseList();
		for (int i = 0; i < searchedWhenClauseList.size(); i++) {
			SearchedWhenClause searchedWhenClause = searchedWhenClauseList.get(i);
			BooleanValueExpression searchedCondition = searchedWhenClause.getSearchedCondition();
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, searchedCondition);
			ValueExpression result = searchedWhenClause.getResult();
			analyzeSortSpecification(querySpecification, result);
		}
		ElseClause elseClause = searchedCase.getElseClause();
		if (elseClause != null) {
			ValueExpression result = elseClause.getResult();
			analyzeSortSpecification(querySpecification, result);
		}
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue) booleanValueExpression;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, booleanValue);
			return;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = (Predicate) booleanValueExpression;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, predicate);
			return;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = (BooleanFactor) booleanValueExpression;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, booleanFactor);
			return;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, booleanTerm);
			return;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, booleanTest);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, booleanValueExpression.getBeginIndex(), 
				"This boolean value exrepssion is not supported.");
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, BooleanValue booleanValue) {
		for (int i = 0; i < booleanValue.size(); i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, booleanTerm);
		}
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = (ComparisonPredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, comparisonPredicate);
			return;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = (BetweenPredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, betweenPredicate);
			return;
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = (DistinctPredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, distinctPredicate);
			return;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = (ExistsPredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, existsPredicate);
			return;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = (InPredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, inPredicate);
			return;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = (LikePredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, likePredicate);
			return;
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = (MatchPredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, matchPredicate);
			return;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = (NullPredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, nullPredicate);
			return;
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = (OverlapsPredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, overlapsPredicate);
			return;
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = (SimilarPredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, similarPredicate);
			return;
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = (UniquePredicate) predicate;
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, uniquePredicate);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"This predicate is not supported.");
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		analyzeSortSpecification(querySpecification, left);
		ValueExpression right = comparisonPredicate.getRight();
		analyzeSortSpecification(querySpecification, right);
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, BetweenPredicate betweenPredicate) {
		ValueExpression valueExpression = betweenPredicate.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
		ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
		analyzeSortSpecification(querySpecification, valueExpression1);
		ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
		analyzeSortSpecification(querySpecification, valueExpression2);
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, DistinctPredicate distinctPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, distinctPredicate.getBeginIndex(), 
				"Distinct predicate is not supported in sort specification list.");
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, ExistsPredicate existsPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, existsPredicate.getBeginIndex(), 
				"Exists predicate is not supported in sort specification list.");
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, InPredicate inPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, inPredicate.getBeginIndex(), 
				"In predicate is not supported in sort specification list.");
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, LikePredicate likePredicate) {
		ValueExpression valueExpression = likePredicate.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
		ValueExpression characterPattern = likePredicate.getCharacterPattern();
		analyzeSortSpecification(querySpecification, characterPattern);
		ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
		if (escapeCharacter != null) {
			analyzeSortSpecification(querySpecification, escapeCharacter);
		}
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, MatchPredicate matchPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, matchPredicate.getBeginIndex(), 
				"Match predicate is not supported in sort specification list.");
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, NullPredicate nullPredicate) {
		ValueExpression valueExpression = nullPredicate.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, OverlapsPredicate overlapsPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, overlapsPredicate.getBeginIndex(), 
				"Overlaps predicate is not supported in sort specification list.");
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, SimilarPredicate similarPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, similarPredicate.getBeginIndex(), 
				"Similar predicate is not supported in sort specification list.");
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, UniquePredicate uniquePredicate) {
		throw Sql4jException.getSql4jException(sourceCode, uniquePredicate.getBeginIndex(), 
				"Unique predicate is not supported in sort specification list.");
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = booleanFactor.getBooleanValueExpression();
		analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, booleanValueExpression);
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, BooleanTerm booleanTerm) {
		for (int i = 0; i < booleanTerm.size(); i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, booleanFactor);
		}
	}
	
	private void analyzeSortSpecificationInternalBooleanValueExpression(QuerySpecification querySpecification, BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = booleanTest.getBooleanValueExpression();
		analyzeSortSpecificationInternalBooleanValueExpression(querySpecification, booleanValueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, SimpleCase simpleCase) {
		ValueExpression caseOperand = simpleCase.getCaseOperand();
		analyzeSortSpecification(querySpecification, caseOperand);
		List<SimpleWhenClause> simpleWhenClauseList = simpleCase.getSimpleWhenClauseList();
		for (int i = 0; i < simpleWhenClauseList.size(); i++) {
			SimpleWhenClause simpleWhenClause = simpleWhenClauseList.get(i);
			ValueExpression whenOperand = simpleWhenClause.getWhenOperand();
			analyzeSortSpecification(querySpecification, whenOperand);
			ValueExpression result = simpleWhenClause.getResult();
			analyzeSortSpecification(querySpecification, result);
		}
		ElseClause elseClause = simpleCase.getElseClause();
		if (elseClause != null) {
			ValueExpression result = elseClause.getResult();
			analyzeSortSpecification(querySpecification, result);
		}
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Some some) {
		ValueExpression valueExpression = some.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, StringLiteral stringLiteral) {
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Subquery subquery) {
		throw Sql4jException.getSql4jException(sourceCode, subquery.getBeginIndex(), 
				"Subquery is not supported in sort specification list.");
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, ToDate toDate) {
		ValueExpression valueExpression = toDate.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, ToChar toChar) {
		ValueExpression valueExpression = toChar.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Substring substring) {
		ValueExpression valueExpression = substring.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
		ValueExpression startPosition = substring.getStartPosition();
		analyzeSortSpecification(querySpecification, startPosition);
		ValueExpression stringLength = substring.getStringLength();
		if (stringLength != null) {
			analyzeSortSpecification(querySpecification, stringLength);
		}
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Subtraction subtraction) {
		ValueExpression left = subtraction.getLeft();
		analyzeSortSpecification(querySpecification, left);
		ValueExpression right = subtraction.getRight();
		analyzeSortSpecification(querySpecification, right);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Sum sum) {
		ValueExpression valueExpression = sum.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, TimeLiteral timeLiteral) {
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, TimestampLiteral timestampLiteral) {
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Trim trim) {
		ValueExpression trimCharacter = trim.getTrimCharacter();
		if (trimCharacter != null) {
			analyzeSortSpecification(querySpecification, trimCharacter);
		}
		ValueExpression trimSource = trim.getTrimSource();
		analyzeSortSpecification(querySpecification, trimSource);
	}
	
	private void analyzeSortSpecification(QuerySpecification querySpecification, Upper upper) {
		ValueExpression valueExpression = upper.getValueExpression();
		analyzeSortSpecification(querySpecification, valueExpression);
	}
	
	////////////////////////////////////////////////
	
	private boolean isSingleTableQuery(QuerySpecification querySpecification) {
		List<TableReference> tableReferenceList = querySpecification.getTableReferenceList();
		TableReference tableReference = getTableReference(tableReferenceList);
		if (tableReference instanceof TablePrimary || 
			tableReference instanceof DerivedTable) {
			return true;
		}
		return false;
	}
	
	private TableReference getTableReference(QuerySpecification querySpecification) {
		List<TableReference> tableReferenceList = querySpecification.getTableReferenceList();
		TableReference tableReference = getTableReference(tableReferenceList);
		return tableReference;
	}
	
	private NameChain genFullyQualifiedColumnNames(NameChain table, Name column) {
		List<Name> list = new ArrayList<Name>(table.size() + 1);
		for (int i = 0; i < table.size(); i++) {
			Name name = table.get(i);
			list.add(name);
		}
		list.add(column);
		NameChain tableName = new NameChain(list);
		return tableName;
	}
	
	private QuerySpecification getQuerySpecification(DerivedTable derivedTable) {
		SelectStatement selectStatement = derivedTable.getSelectStatement();
		QuerySpecification querySpecification = getQuerySpecification(selectStatement);
		return querySpecification;
	}
	
	private QuerySpecification getQuerySpecification(SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = (QuerySpecification) selectStatement;
			return querySpecification;
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
	
	private ValueExpression getFullyQualifiedColumnNameFromSelectList(List<SelectSublist> selectList, Name columnName) {
		ValueExpression result = null;
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			Name name = selectSublist.getName();
			ValueExpression valueExpression = selectSublist.getValueExpression();
			if (name != null) {
				if (name.getContent().equalsIgnoreCase(columnName.getContent())) {
					result = valueExpression;
				}
				continue;
			}
			if ((valueExpression instanceof NameChain) == false) {
				continue;
			}
			NameChain column = (NameChain) valueExpression;
			String value = column.get(column.size() - 1).getContent();
			if (value.equalsIgnoreCase(columnName.getContent()) == false) {
				continue;
			}
			result = column.getFullyQualifiedName();
		}
		if (result == null) {
			throw Sql4jException.getSql4jException(sourceCode, columnName.getBeginIndex(), 
					"Can't find the owner table of the column.");
		}
		return result;
	}
	
	private TableReference getTableReference(List<TableReference> tableReferenceList) {
		TableReference tableReference = tableReferenceList.get(0);
		if (tableReferenceList.size() > 1) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"',' join is not supported.");
		}
		if (tableReference instanceof TablePrimary) {
			return tableReference;
		}
		if (tableReference instanceof LeftOuterJoin) {
			return tableReference;
		}
		if (tableReference instanceof DerivedTable) {
			return tableReference;
		}
		if (tableReference instanceof RightOuterJoin) {
			return tableReference;
		}
		if (tableReference instanceof InnerJoin) {
			return tableReference;
		}
		if (tableReference instanceof FullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Full outer join is not supported.");
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
	
	private String getTableNameByColumnName(NameChain columnName) {
		if (columnName.size() < 2) {
			return null;
		}
		StringBuilder buf = new StringBuilder(200);
		for (int i = 0; i < columnName.size() - 1; i++) {
			Name name = columnName.get(i);
			buf.append(name.getContent());
			if (i < columnName.size() - 2) {
				buf.append('.');
			}
		}
		String tableName = buf.toString();
		return tableName;
	}
	
	private String getTableNameByTablePrimary(TablePrimary tablePrimary) {
		NameChain tableName = tablePrimary.getTableName();
		StringBuilder buf = new StringBuilder(200);
		for (int i = 0; i < tableName.size(); i++) {
			Name name = tableName.get(i);
			buf.append(name.getContent());
			if (i < tableName.size() - 1) {
				buf.append('.');
			}
		}
		String tableNameStr = buf.toString();
		return tableNameStr;
	}
	
	private ValueExpression getGroupingColumnReferenceValueExpression(List<SelectSublist> selectList, NameChain nameChain) {
		String nameChainContent = nameChain.toLowerCaseString();
		ValueExpression groupingColumnReferenceValueExpression = null;
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			ValueExpression valueExpression = selectSublist.getValueExpression();
			Name name = selectSublist.getName();
			if (name != null) {
				String nameContent = name.getContent();
				if (nameChainContent.equalsIgnoreCase(nameContent)) {
					if (groupingColumnReferenceValueExpression != null) {
						throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
								"There are multiple column with the same name as this column name.");
					}
					groupingColumnReferenceValueExpression = valueExpression;
				}
				continue;
			}
			if (valueExpression instanceof NameChain == false) {
				continue;
			}
			NameChain valueExpressionNameChain = (NameChain) valueExpression;
			if (nameChain.size() == 1) {
				Name lastName = valueExpressionNameChain.get(valueExpressionNameChain.size() - 1);
				String lastNameContent = lastName.getContent();
				if (nameChainContent.equalsIgnoreCase(lastNameContent)) {
					if (groupingColumnReferenceValueExpression != null) {
						throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
								"There are multiple column with the same name as this column name.");
					}
					groupingColumnReferenceValueExpression = valueExpression;
				}
				continue;
			}
			String valueExpressionNameChainContent = valueExpressionNameChain.toLowerCaseString();
			if (nameChainContent.equalsIgnoreCase(valueExpressionNameChainContent)) {
				if (groupingColumnReferenceValueExpression != null) {
					throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
							"There are multiple column with the same name as this column name.");
				}
				groupingColumnReferenceValueExpression = valueExpression;
			}
			continue;
		}
		return groupingColumnReferenceValueExpression;
	}
	
	private ValueExpression getGroupingColumnReferenceValueExpression(TableReference tableReference, NameChain nameChain) {
		String nameChainTableNameContent = getTableNameByColumnName(nameChain);
		if (tableReference instanceof TablePrimary) {
			TablePrimary tablePrimary = (TablePrimary) tableReference;
			NameChain tableName = tablePrimary.getTableName();
			Name correlationName = tablePrimary.getCorrelationName();
			if (correlationName != null) {
				String correlationNameContent = correlationName.getContent();
				if (correlationNameContent.equalsIgnoreCase(nameChainTableNameContent)) {
					NameChain columnName = getFullyQualifiedColumnName(tableName, nameChain.get(nameChain.size() - 1));
					return columnName;
				}
				return null;
			}
			String tableNameContent = tableName.toLowerCaseString();
			if (tableNameContent.equalsIgnoreCase(nameChainTableNameContent)) {
				return nameChain;
			}
			return null;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			TableReference left = leftOuterJoin.getLeft();
			TableReference right = leftOuterJoin.getRight();
			ValueExpression valueExpression = getGroupingColumnReferenceValueExpression(left, nameChain);
			if (valueExpression == null) {
				valueExpression = getGroupingColumnReferenceValueExpression(right, nameChain);
			}
			return valueExpression;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			QuerySpecification querySpecification = getQuerySpecification(derivedTable);
			List<SelectSublist> selectList = querySpecification.getSelectList();
			ValueExpression valueExpression = getGroupingColumnReferenceValueExpression(selectList, nameChain);
			return valueExpression;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			TableReference right = rightOuterJoin.getRight();
			ValueExpression valueExpression = getGroupingColumnReferenceValueExpression(left, nameChain);
			if (valueExpression == null) {
				valueExpression = getGroupingColumnReferenceValueExpression(right, nameChain);
			}
			return valueExpression;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			TableReference right = innerJoin.getRight();
			ValueExpression valueExpression = getGroupingColumnReferenceValueExpression(left, nameChain);
			if (valueExpression == null) {
				valueExpression = getGroupingColumnReferenceValueExpression(right, nameChain);
			}
			return valueExpression;
		}
		if (tableReference instanceof FullOuterJoin) {
			throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
					"Full outer join is not supported.");
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
	
	private NameChain getFullyQualifiedColumnName(NameChain tableName, Name columnName) {
		String tableNameContent = tableName.toLowerCaseString();
		String columnNameContent = columnName.getContent();
		TableMetadata tableMetadata = configuration.getTableMetadata(tableNameContent);
		if (tableMetadata.hasColumnMetadata(columnNameContent)) {
			List<Name> list = new ArrayList<Name>(tableName.size() + 1);
			for (int i = 0; i < tableName.size(); i++) {
				Name name = tableName.get(i);
				list.add(name);
			}
			list.add(columnName);
			NameChain table = new NameChain(list);
			return table;
		}
		return null;
	}
	
	private ValueExpression getFullyQualifiedColumnName(List<SelectSublist> selectList, NameChain nameChain) {
		ValueExpression fullyQualifiedColumnName = null;
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			ValueExpression valueExpression = selectSublist.getValueExpression();
			Name name = selectSublist.getName();
			if (name != null) {
				if (equals(nameChain, name) == false) {
					continue;
				}
				if (valueExpression instanceof NameChain == false) {
					continue;
				}
				if (fullyQualifiedColumnName != null) {
					throw Sql4jException.getSql4jException(sourceCode, name.getBeginIndex(), 
							"Duplicate name.");
				}
				NameChain nameChain_ = (NameChain) valueExpression;
				fullyQualifiedColumnName = nameChain_.getFullyQualifiedName();
				continue;
			}
			if (valueExpression instanceof NameChain == false) {
				continue;
			}
			NameChain nameChain_ = (NameChain) valueExpression;
			if (equals(nameChain, nameChain_) == false) {
				continue;
			}
			if (fullyQualifiedColumnName != null) {
				throw Sql4jException.getSql4jException(sourceCode, nameChain_.getBeginIndex(), 
						"Duplicate name.");
			}
			fullyQualifiedColumnName = nameChain_.getFullyQualifiedName();
		}
		return fullyQualifiedColumnName;
	}
	
	private boolean equals(NameChain nameChain, Name name) {
		if (nameChain.size() != 1) {
			return false;
		}
		Name name_ = nameChain.get(0);
		if (name_.getContent().equalsIgnoreCase(name.getContent())) {
			return true;
		}
		return false;
	}
	
	private boolean equals(NameChain nameChain1, NameChain nameChain2) {
		if (nameChain1.size() != nameChain2.size()) {
			return false;
		}
		for (int i = 0; i < nameChain1.size(); i++) {
			Name name1 = nameChain1.get(i);
			Name name2 = nameChain2.get(i);
			if (name1.getContent().equalsIgnoreCase(name2.getContent()) == false) {
				return false;
			}
		}
		return true;
	}
	
}
