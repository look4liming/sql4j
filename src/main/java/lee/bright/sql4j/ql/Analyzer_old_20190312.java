package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.ColumnMetadata;
import lee.bright.sql4j.conf.Configuration;

/**
 * @author Bright Lee
 */
public final class Analyzer_old_20190312 {
	
	private static final NameChain DEFAULT_NAME_CHAIN = new NameChain();
	
	private Configuration configuration;
	private Parser parser;
	private SourceCode sourceCode;
	
	public Analyzer_old_20190312(Configuration configuration, 
			SourceCode sourceCode) {
		this.configuration = configuration;
		this.parser = new Parser(this.configuration, sourceCode);
		this.sourceCode = sourceCode;
	}
	
	public Statement analyze() {
		Statement statement = parser.parse();
		if (statement == null) {
			return null;
		}
		settingParentStatementsForSubqueries(statement);
		detectingTableNameConflicts(statement);
		setValueExpressionDataType(statement);
		if (statement instanceof SelectStatement) {
			SelectStatement selectStatement = 
					(SelectStatement) statement;
			analyze(selectStatement);
			return statement;
		}
		if (statement instanceof UpdateStatement) {
			UpdateStatement updateStatement =
					(UpdateStatement) statement;
			analyze(updateStatement);
			return statement;
		}
		if (statement instanceof InsertStatement) {
			InsertStatement insertStatement = 
					(InsertStatement) statement;
			analyze(insertStatement);
			return statement;
		}
		if (statement instanceof DeleteStatement) {
			DeleteStatement deleteStatement = 
					(DeleteStatement) statement;
			analyze(deleteStatement);
			return statement;
		}
		if (statement instanceof CallStatement) {
			CallStatement callStatement = 
					(CallStatement) statement;
			analyze(callStatement);
			return statement;
		}
		throw Sql4jException.getSql4jException(sourceCode, statement.getBeginIndex(), 
				"This statement is not supported.");
	}
	
	private void detectingTableNameConflicts(Statement statement) {
		if (statement instanceof SelectStatement) {
			SelectStatement selectStatement = 
					(SelectStatement) statement;
			detectingTableNameConflicts(selectStatement);
			return;
		}
		if (statement instanceof UpdateStatement) {
			UpdateStatement updateStatement =
					(UpdateStatement) statement;
			detectingTableNameConflicts(updateStatement);
			return;
		}
		if (statement instanceof InsertStatement) {
			InsertStatement insertStatement = 
					(InsertStatement) statement;
			detectingTableNameConflicts(insertStatement);
			return;
		}
		if (statement instanceof DeleteStatement) {
			DeleteStatement deleteStatement = 
					(DeleteStatement) statement;
			detectingTableNameConflicts(deleteStatement);
		}
	}
	
	private void detectingTableNameConflicts(SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = 
					(QuerySpecification) selectStatement;
			detectingTableNameConflicts(querySpecification);
			return;
		}
		if (selectStatement instanceof Union) {
			Union union = (Union) selectStatement;
			detectingTableNameConflicts(union);
			return;
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
	
	private void detectingTableNameConflicts(QuerySpecification querySpecification) {
		List<SelectSublist> selectList = querySpecification.getSelectList();
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			ValueExpression valueExpression = 
					selectSublist.getValueExpression();
			detectingTableNameConflicts(valueExpression);
		}
		List<TableReference> tableReferenceList = 
				querySpecification.getTableReferenceList();
		detectingTableNameConflictsTableReferenceList(tableReferenceList);
		BooleanValueExpression whereSearchCondition = 
				querySpecification.getWhereSearchCondition();
		detectingTableNameConflicts(whereSearchCondition);
		List<GroupingElement> groupingElementList = 
				querySpecification.getGroupingElementList();
		detectingTableNameConflicts(groupingElementList);
		BooleanValueExpression havingSearchCondition = 
				querySpecification.getHavingSearchCondition();
		detectingTableNameConflicts(havingSearchCondition);
		List<SortSpecification> sortSpecificationList = 
				querySpecification.getSortSpecificationList();
		detectingTableNameConflictsSortSpecificationList(sortSpecificationList);
	}
	
	private void detectingTableNameConflictsTableReferenceList(List<TableReference> tableReferenceList) {
		Map<String, String> tableNameMap = new HashMap<String, String>();
		for (int i = 0; i < tableReferenceList.size(); i++) {
			TableReference tableReference = tableReferenceList.get(i);
			detectingTableNameConflicts(tableNameMap, tableReference);
		}
	}
	
	private void detectingTableNameConflicts(Map<String, String> tableNameMap, 
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
			detectingTableNameConflicts(tableNameMap, left);
			TableReference right = leftOuterJoin.getRight();
			detectingTableNameConflicts(tableNameMap, right);
			BooleanValueExpression joinCondition = leftOuterJoin.getJoinCondition();
			detectingTableNameConflicts(joinCondition);
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
			//SelectStatement selectStatement = subquery.getSelectStatement();
			detectingTableNameConflicts(subquery);
			setValueExpressionDataType(subquery);
			return;
		}
		if (tableReference instanceof CrossJoin) {
			CrossJoin crossJoin = (CrossJoin) tableReference;
			TableReference left = crossJoin.getLeft();
			detectingTableNameConflicts(tableNameMap, left);
			TableReference right = crossJoin.getRight();
			detectingTableNameConflicts(tableNameMap, right);
			return;
		}
		if (tableReference instanceof FullOuterJoin) {
			FullOuterJoin fullOuterJoin = (FullOuterJoin) tableReference;
			TableReference left = fullOuterJoin.getLeft();
			detectingTableNameConflicts(tableNameMap, left);
			TableReference right = fullOuterJoin.getRight();
			detectingTableNameConflicts(tableNameMap, right);
			BooleanValueExpression joinCondition = fullOuterJoin.getJoinCondition();
			detectingTableNameConflicts(joinCondition);
			return;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			detectingTableNameConflicts(tableNameMap, left);
			TableReference right = innerJoin.getRight();
			detectingTableNameConflicts(tableNameMap, right);
			BooleanValueExpression joinCondition = innerJoin.getJoinCondition();
			detectingTableNameConflicts(joinCondition);
			return;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			detectingTableNameConflicts(tableNameMap, left);
			TableReference right = rightOuterJoin.getRight();
			detectingTableNameConflicts(tableNameMap, right);
			BooleanValueExpression joinCondition = rightOuterJoin.getJoinCondition();
			detectingTableNameConflicts(joinCondition);
			return;
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			NaturalFullOuterJoin naturalFullOuterJoin = (NaturalFullOuterJoin) tableReference;
			TableReference left = naturalFullOuterJoin.getLeft();
			detectingTableNameConflicts(tableNameMap, left);
			TableReference right = naturalFullOuterJoin.getRight();
			detectingTableNameConflicts(tableNameMap, right);
			return;
		}
		if (tableReference instanceof NaturalInnerJoin) {
			NaturalInnerJoin naturalInnerJoin = (NaturalInnerJoin) tableReference;
			TableReference left = naturalInnerJoin.getLeft();
			detectingTableNameConflicts(tableNameMap, left);
			TableReference right = naturalInnerJoin.getRight();
			detectingTableNameConflicts(tableNameMap, right);
			return;
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			NaturalLeftOuterJoin naturalLeftOuterJoin = (NaturalLeftOuterJoin) tableReference;
			TableReference left = naturalLeftOuterJoin.getLeft();
			detectingTableNameConflicts(tableNameMap, left);
			TableReference right = naturalLeftOuterJoin.getRight();
			detectingTableNameConflicts(tableNameMap, right);
			return;
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			NaturalRightOuterJoin naturalRightOuterJoin = (NaturalRightOuterJoin) tableReference;
			TableReference left = naturalRightOuterJoin.getLeft();
			detectingTableNameConflicts(tableNameMap, left);
			TableReference right = naturalRightOuterJoin.getRight();
			detectingTableNameConflicts(tableNameMap, right);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"The table reference is not supported.");
	}
	
	private void detectingTableNameConflicts(List<GroupingElement> groupingElementList) {
		if (groupingElementList == null) {
			return;
		}
		for (int i = 0; i < groupingElementList.size(); i++) {
			GroupingElement groupingElement = groupingElementList.get(i);
			detectingTableNameConflicts(groupingElement);
		}
	}
	
	private void detectingTableNameConflicts(GroupingElement groupingElement) {
		if (groupingElement instanceof CubeList) {
			CubeList cubeList = (CubeList) groupingElement;
			detectingTableNameConflicts(cubeList);
			return;
		}
		if (groupingElement instanceof GrandTotal) {
			GrandTotal grandTotal = (GrandTotal) groupingElement;
			detectingTableNameConflicts(grandTotal);
			return;
		}
		if (groupingElement instanceof GroupingSetsSpecification) {
			GroupingSetsSpecification groupingSetsSpecification = 
					(GroupingSetsSpecification) groupingElement;
			detectingTableNameConflicts(groupingSetsSpecification);
			return;
		}
		if (groupingElement instanceof OrdinaryGroupingSet) {
			OrdinaryGroupingSet ordinaryGroupingSet = (OrdinaryGroupingSet) groupingElement;
			detectingTableNameConflicts(ordinaryGroupingSet);
			return;
		}
		if (groupingElement instanceof RollupList) {
			RollupList rollupList = (RollupList) groupingElement;
			detectingTableNameConflicts(rollupList);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, groupingElement.getBeginIndex(), 
				"The grouping element is not supported.");
	}
	
	private void detectingTableNameConflicts(CubeList cubeList) {
		for (int i = 0; i < cubeList.size(); i++) {
			GroupingColumnReference groupingColumnReference = cubeList.get(i);
			detectingTableNameConflicts(groupingColumnReference);
		}
	}
	
	private void detectingTableNameConflicts(GroupingColumnReference groupingColumnReference) {
	}
	
	private void detectingTableNameConflicts(GrandTotal grandTotal) {
		throw Sql4jException.getSql4jException(sourceCode, grandTotal.getBeginIndex(), 
				"Grand total is not supported.");
	}
	
	private void detectingTableNameConflicts(GroupingSetsSpecification groupingSetsSpecification) {
		for (int i = 0; i < groupingSetsSpecification.size(); i++) {
			GroupingElement groupingElement = groupingSetsSpecification.get(i);
			detectingTableNameConflicts(groupingElement);
		}
	}
	
	private void detectingTableNameConflicts(OrdinaryGroupingSet ordinaryGroupingSet) {
		for (int i = 0; i < ordinaryGroupingSet.size(); i++) {
			GroupingColumnReference groupingColumnReference = ordinaryGroupingSet.get(i);
			detectingTableNameConflicts(groupingColumnReference);
		}
	}
	
	private void detectingTableNameConflicts(RollupList rollupList) {
		for (int i = 0; i < rollupList.size(); i++) {
			GroupingColumnReference groupingColumnReference = rollupList.get(i);
			detectingTableNameConflicts(groupingColumnReference);
		}
	}
	
	private void detectingTableNameConflictsSortSpecificationList(List<SortSpecification> sortSpecificationList) {
		if (sortSpecificationList == null) {
			return;
		}
		for (int i = 0; i < sortSpecificationList.size(); i++) {
			SortSpecification sortSpecification = sortSpecificationList.get(i);
			detectingTableNameConflicts(sortSpecification);
		}
	}
	
	private void detectingTableNameConflicts(SortSpecification sortSpecification) {
		ValueExpression sortKey = sortSpecification.getSortKey();
		detectingTableNameConflicts(sortKey);
	}
	
	private void detectingTableNameConflicts(ValueExpression valueExpression) {
		if (valueExpression == null) {
			return;
		}
		if (valueExpression instanceof Subquery) {
			Subquery subquery = (Subquery) valueExpression;
			detectingTableNameConflicts(subquery);
			return;
		}
		if (valueExpression instanceof BooleanValueExpression) {
			BooleanValueExpression booleanValueExpression = 
					(BooleanValueExpression) valueExpression;
			detectingTableNameConflicts(booleanValueExpression);
			return;
		}
		if (valueExpression instanceof AbsoluteValueExpression) {
			AbsoluteValueExpression absoluteValueExpression = 
					(AbsoluteValueExpression) valueExpression;
			detectingTableNameConflicts(absoluteValueExpression);
			return;
		}
		if (valueExpression instanceof Addition) {
			Addition addition = (Addition) valueExpression;
			detectingTableNameConflicts(addition);
			return;
		}
		if (valueExpression instanceof Any) {
			Any any = (Any) valueExpression;
			detectingTableNameConflicts(any);
			return;
		}
		if (valueExpression instanceof Avg) {
			Avg avg = (Avg) valueExpression;
			detectingTableNameConflicts(avg);
			return;
		}
		if (valueExpression instanceof BitLengthExpression) {
			BitLengthExpression bitLengthExpression = 
					(BitLengthExpression) valueExpression;
			detectingTableNameConflicts(bitLengthExpression);
			return;
		}
		if (valueExpression instanceof CardinalityExpression) {
			CardinalityExpression cardinalityExpression = 
					(CardinalityExpression) valueExpression;
			detectingTableNameConflicts(cardinalityExpression);
			return;
		}
		if (valueExpression instanceof CharLengthExpression) {
			CharLengthExpression charLengthExpression = 
					(CharLengthExpression) valueExpression;
			detectingTableNameConflicts(charLengthExpression);
			return;
		}
		if (valueExpression instanceof Coalesce) {
			Coalesce coalesce = (Coalesce) valueExpression;
			detectingTableNameConflicts(coalesce);
			return;
		}
		if (valueExpression instanceof Concatenation) {
			Concatenation concatenation = (Concatenation) valueExpression;
			detectingTableNameConflicts(concatenation);
			return;
		}
		if (valueExpression instanceof Count) {
			Count count = (Count) valueExpression;
			detectingTableNameConflicts(count);
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
			detectingTableNameConflicts(division);
			return;
		}
		if (valueExpression instanceof Every) {
			Every every = (Every) valueExpression;
			detectingTableNameConflicts(every);
			return;
		}
		if (valueExpression instanceof ExtractExpression) {
			ExtractExpression extractExpression = 
					(ExtractExpression) valueExpression;
			detectingTableNameConflicts(extractExpression);
			return;
		}
		if (valueExpression instanceof FunctionInvocation) {
			FunctionInvocation functionInvocation = 
					(FunctionInvocation) valueExpression;
			detectingTableNameConflicts(functionInvocation);
			return;
		}
		if (valueExpression instanceof Grouping) {
			return;
		}
		if (valueExpression instanceof Lower) {
			Lower lower = (Lower) valueExpression;
			detectingTableNameConflicts(lower);
			return;
		}
		if (valueExpression instanceof Max) {
			Max max = (Max) valueExpression;
			detectingTableNameConflicts(max);
			return;
		}
		if (valueExpression instanceof Min) {
			Min min = (Min) valueExpression;
			detectingTableNameConflicts(min);
			return;
		}
		if (valueExpression instanceof ModulusExpression) {
			ModulusExpression modulusExpression = 
					(ModulusExpression) valueExpression;
			detectingTableNameConflicts(modulusExpression);
			return;
		}
		if (valueExpression instanceof Multiplication) {
			Multiplication multiplication = (Multiplication) valueExpression;
			detectingTableNameConflicts(multiplication);
			return;
		}
		if (valueExpression instanceof NameChain) {
			return;
		}
		if (valueExpression instanceof NegativeExpression) {
			NegativeExpression negativeExpression = 
					(NegativeExpression) valueExpression;
			detectingTableNameConflicts(negativeExpression);
			return;
		}
		if (valueExpression instanceof NullIf) {
			NullIf nullIf = (NullIf) valueExpression;
			detectingTableNameConflicts(nullIf);
			return;
		}
		if (valueExpression instanceof NumericLiteral) {
			return;
		}
		if (valueExpression instanceof OctetLengthExpression) {
			OctetLengthExpression octetLengthExpression = 
					(OctetLengthExpression) valueExpression;
			detectingTableNameConflicts(octetLengthExpression);
			return;
		}
		if (valueExpression instanceof Parameter) {
			return;
		}
		if (valueExpression instanceof PositionExpression) {
			PositionExpression positionExpression = 
					(PositionExpression) valueExpression;
			detectingTableNameConflicts(positionExpression);
			return;
		}
		if (valueExpression instanceof PositiveExpression) {
			PositiveExpression positiveExpression = 
					(PositiveExpression) valueExpression;
			detectingTableNameConflicts(positiveExpression);
			return;
		}
		if (valueExpression instanceof SearchedCase) {
			SearchedCase searchedCase = (SearchedCase) valueExpression;
			detectingTableNameConflicts(searchedCase);
			return;
		}
		if (valueExpression instanceof SimpleCase) {
			SimpleCase simpleCase = (SimpleCase) valueExpression;
			detectingTableNameConflicts(simpleCase);
			return;
		}
		if (valueExpression instanceof Some) {
			Some some = (Some) valueExpression;
			detectingTableNameConflicts(some);
			return;
		}
		if (valueExpression instanceof StringLiteral) {
			return;
		}
		if (valueExpression instanceof ToDate) {
			ToDate toDate = (ToDate) valueExpression;
			detectingTableNameConflicts(toDate);
			return;
		}
		if (valueExpression instanceof ToChar) {
			ToChar toChar = (ToChar) valueExpression;
			detectingTableNameConflicts(toChar);
			return;
		}
		if (valueExpression instanceof Substring) {
			Substring substring = (Substring) valueExpression;
			detectingTableNameConflicts(substring);
			return;
		}
		if (valueExpression instanceof Subtraction) {
			Subtraction subtraction = (Subtraction) valueExpression;
			detectingTableNameConflicts(subtraction);
			return;
		}
		if (valueExpression instanceof Sum) {
			Sum sum = (Sum) valueExpression;
			detectingTableNameConflicts(sum);
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
			detectingTableNameConflicts(trim);
			return;
		}
		if (valueExpression instanceof Upper) {
			Upper upper = (Upper) valueExpression;
			detectingTableNameConflicts(upper);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				valueExpression.getBeginIndex(), 
				"Not support the value exrepssion.");
	}
	
	private void detectingTableNameConflicts(BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression == null) {
			return;
		}
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = 
					(BooleanValue) booleanValueExpression;
			detectingTableNameConflicts(booleanValue);
			return;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = 
					(Predicate) booleanValueExpression;
			detectingTableNameConflicts(predicate);
			return;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = 
					(BooleanFactor) booleanValueExpression;
			detectingTableNameConflicts(booleanFactor);
			return;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			detectingTableNameConflicts(booleanTerm);
			return;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			detectingTableNameConflicts(booleanTest);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				booleanValueExpression.getBeginIndex(), 
				"Not support the boolean value exrepssion.");
	}
	
	private void detectingTableNameConflicts(BooleanValue booleanValue) {
		int size = booleanValue.size();
		for (int i = 0; i < size; i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			detectingTableNameConflicts(booleanTerm);
		}
	}
	
	private void detectingTableNameConflicts(Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = 
					(ComparisonPredicate) predicate;
			detectingTableNameConflicts(comparisonPredicate);
			return;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = 
					(BetweenPredicate) predicate;
			detectingTableNameConflicts(betweenPredicate);
			return;
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = 
					(DistinctPredicate) predicate;
			detectingTableNameConflicts(distinctPredicate);
			return;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = 
					(ExistsPredicate) predicate;
			detectingTableNameConflicts(existsPredicate);
			return;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = 
					(InPredicate) predicate;
			detectingTableNameConflicts(inPredicate);
			return;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = 
					(LikePredicate) predicate;
			detectingTableNameConflicts(likePredicate);
			return;
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = 
					(MatchPredicate) predicate;
			detectingTableNameConflicts(matchPredicate);
			return;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = 
					(NullPredicate) predicate;
			detectingTableNameConflicts(nullPredicate);
			return;
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = 
					(OverlapsPredicate) predicate;
			detectingTableNameConflicts(overlapsPredicate);
			return;
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = 
					(SimilarPredicate) predicate;
			detectingTableNameConflicts(similarPredicate);
			return;
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = 
					(UniquePredicate) predicate;
			detectingTableNameConflicts(uniquePredicate);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"Not support the predicate.");
	}
	
	private void detectingTableNameConflicts(ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		detectingTableNameConflicts(left);
		ValueExpression right = comparisonPredicate.getRight();
		detectingTableNameConflicts(right);
	}
	
	private void detectingTableNameConflicts(BetweenPredicate betweenPredicate) {
		ValueExpression valueExpression = betweenPredicate.getValueExpression();
		detectingTableNameConflicts(valueExpression);
		ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
		detectingTableNameConflicts(valueExpression1);
		ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
		detectingTableNameConflicts(valueExpression2);
	}
	
	private void detectingTableNameConflicts(DistinctPredicate distinctPredicate) {
		ValueExpression left = distinctPredicate.getLeft();
		detectingTableNameConflicts(left);
		ValueExpression right = distinctPredicate.getRight();
		detectingTableNameConflicts(right);
	}
	
	private void detectingTableNameConflicts(ExistsPredicate existsPredicate) {
		Subquery subquery = existsPredicate.getSubquery();
		detectingTableNameConflicts(subquery);
	}
	
	private void detectingTableNameConflicts(InPredicate inPredicate) {
		ValueExpression valueExpression = inPredicate.getValueExpression();
		detectingTableNameConflicts(valueExpression);
		List<ValueExpression> inValueList = inPredicate.getInValueList();
		if (inValueList != null) {
			for (int i = 0; i < inValueList.size(); i++) {
				ValueExpression inValue = inValueList.get(i);
				detectingTableNameConflicts(inValue);
			}
		}
		Subquery subquery = inPredicate.getSubquery();
		if (subquery != null) {
			detectingTableNameConflicts(subquery);
		}
	}
	
	private void detectingTableNameConflicts(LikePredicate likePredicate) {
		ValueExpression valueExpression = likePredicate.getValueExpression();
		detectingTableNameConflicts(valueExpression);
		ValueExpression characterPattern = likePredicate.getCharacterPattern();
		detectingTableNameConflicts(characterPattern);
		ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
		detectingTableNameConflicts(escapeCharacter);
	}
	
	private void detectingTableNameConflicts(MatchPredicate matchPredicate) {
		ValueExpression valueExpression = matchPredicate.getValueExpression();
		detectingTableNameConflicts(valueExpression);
		Subquery subquery = matchPredicate.getSubquery();
		detectingTableNameConflicts(subquery);
	}
	
	private void detectingTableNameConflicts(NullPredicate nullPredicate) {
		ValueExpression valueExpression = nullPredicate.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(OverlapsPredicate overlapsPredicate) {
		ValueExpression left = overlapsPredicate.getLeft();
		detectingTableNameConflicts(left);
		ValueExpression right = overlapsPredicate.getRight();
		detectingTableNameConflicts(right);
	}
	
	private void detectingTableNameConflicts(SimilarPredicate similarPredicate) {
		ValueExpression valueExpression = similarPredicate.getValueExpression();
		detectingTableNameConflicts(valueExpression);
		ValueExpression similarPattern = similarPredicate.getSimilarPattern();
		detectingTableNameConflicts(similarPattern);
		ValueExpression escapeCharacter = similarPredicate.getEscapeCharacter();
		detectingTableNameConflicts(escapeCharacter);
	}
	
	private void detectingTableNameConflicts(UniquePredicate uniquePredicate) {
		Subquery subquery = uniquePredicate.getSubquery();
		detectingTableNameConflicts(subquery);
	}
	
	private void detectingTableNameConflicts(BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = 
				booleanFactor.getBooleanValueExpression();
		detectingTableNameConflicts(booleanValueExpression);
	}
	
	private void detectingTableNameConflicts(BooleanTerm booleanTerm) {
		int size = booleanTerm.size();
		for (int i = 0; i < size; i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			detectingTableNameConflicts(booleanFactor);
		}
	}
	
	private void detectingTableNameConflicts(BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = 
				booleanTest.getBooleanValueExpression();
		detectingTableNameConflicts(booleanValueExpression);
	}
	
	private void detectingTableNameConflicts(AbsoluteValueExpression absoluteValueExpression) {
		ValueExpression valueExpression = absoluteValueExpression.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(Addition addition) {
		ValueExpression left = addition.getLeft();
		detectingTableNameConflicts(left);
		ValueExpression right = addition.getRight();
		detectingTableNameConflicts(right);
	}
	
	private void detectingTableNameConflicts(Any any) {
		ValueExpression valueExpression = any.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(Avg avg) {
		ValueExpression valueExpression = avg.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(BitLengthExpression bitLengthExpression) {
		ValueExpression valueExpression = bitLengthExpression.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(CardinalityExpression cardinalityExpression) {
		ValueExpression valueExpression = cardinalityExpression.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(CharLengthExpression charLengthExpression) {
		ValueExpression valueExpression = charLengthExpression.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(Coalesce coalesce) {
		List<ValueExpression> arguments = coalesce.getArguments();
		for (int i = 0; i < arguments.size(); i++) {
			ValueExpression argument = arguments.get(i);
			detectingTableNameConflicts(argument);
		}
	}
	
	private void detectingTableNameConflicts(Concatenation concatenation) {
		ValueExpression left = concatenation.getLeft();
		detectingTableNameConflicts(left);
		ValueExpression right = concatenation.getRight();
		detectingTableNameConflicts(right);
	}
	
	private void detectingTableNameConflicts(Count count) {
		ValueExpression valueExpression = count.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(Division division) {
		ValueExpression left = division.getLeft();
		detectingTableNameConflicts(left);
		ValueExpression right = division.getRight();
		detectingTableNameConflicts(right);
	}
	
	private void detectingTableNameConflicts(Every every) {
		ValueExpression valueExpression = every.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(ExtractExpression extractExpression) {
		ValueExpression extractSource = extractExpression.getExtractSource();
		detectingTableNameConflicts(extractSource);
	}
	
	private void detectingTableNameConflicts(FunctionInvocation functionInvocation) {
		List<ValueExpression> arguments = functionInvocation.getArguments();
		for (int i = 0; i < arguments.size(); i++) {
			ValueExpression argument = arguments.get(i);
			detectingTableNameConflicts(argument);
		}
	}
	
	private void detectingTableNameConflicts(Lower lower) {
		ValueExpression argument = lower.getValueExpression();
		detectingTableNameConflicts(argument);
	}
	
	private void detectingTableNameConflicts(Max max) {
		ValueExpression valueExpression = max.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(Min min) {
		ValueExpression valueExpression = min.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(ModulusExpression modulusExpression) {
		ValueExpression dividend = modulusExpression.getDividend();
		detectingTableNameConflicts(dividend);
		ValueExpression divisor = modulusExpression.getDivisor();
		detectingTableNameConflicts(divisor);
	}
	
	private void detectingTableNameConflicts(Multiplication multiplication) {
		ValueExpression left = multiplication.getLeft();
		detectingTableNameConflicts(left);
		ValueExpression right = multiplication.getRight();
		detectingTableNameConflicts(right);
	}
	
	private void detectingTableNameConflicts(NegativeExpression negativeExpression) {
		ValueExpression valueExpression = negativeExpression.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(NullIf nullIf) {
		ValueExpression first = nullIf.getFirst();
		detectingTableNameConflicts(first);
		ValueExpression second = nullIf.getSecond();
		detectingTableNameConflicts(second);
	}
	
	private void detectingTableNameConflicts(OctetLengthExpression octetLengthExpression) {
		ValueExpression valueExpression = octetLengthExpression.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(PositionExpression positionExpression) {
		ValueExpression valueExpression1 = positionExpression.getValueExpression1();
		detectingTableNameConflicts(valueExpression1);
		ValueExpression valueExpression2 = positionExpression.getValueExpression2();
		detectingTableNameConflicts(valueExpression2);
	}
	
	private void detectingTableNameConflicts(PositiveExpression positiveExpression) {
		ValueExpression valueExpression = positiveExpression.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(SearchedCase searchedCase) {
		List<SearchedWhenClause> searchedWhenClauseList = 
				searchedCase.getSearchedWhenClauseList();
		for (int i = 0; i < searchedWhenClauseList.size(); i++) {
			SearchedWhenClause searchedWhenClause = searchedWhenClauseList.get(i);
			BooleanValueExpression searchedCondition = searchedWhenClause.getSearchedCondition();
			detectingTableNameConflicts(searchedCondition);
			ValueExpression result = searchedWhenClause.getResult();
			detectingTableNameConflicts(result);
		}
		ElseClause elseClause = searchedCase.getElseClause();
		ValueExpression result = elseClause.getResult();
		detectingTableNameConflicts(result);
	}
	
	private void detectingTableNameConflicts(SimpleCase simpleCase) {
		ValueExpression caseOperand = simpleCase.getCaseOperand();
		detectingTableNameConflicts(caseOperand);
		List<SimpleWhenClause> simpleWhenClauseList = simpleCase.getSimpleWhenClauseList();
		for (int i = 0; i < simpleWhenClauseList.size(); i++) {
			SimpleWhenClause simpleWhenClause = simpleWhenClauseList.get(i);
			ValueExpression whenOperand = simpleWhenClause.getWhenOperand();
			detectingTableNameConflicts(whenOperand);
			ValueExpression result = simpleWhenClause.getResult();
			detectingTableNameConflicts(result);
		}
		ElseClause elseClause = simpleCase.getElseClause();
		ValueExpression result = elseClause.getResult();
		detectingTableNameConflicts(result);
	}
	
	private void detectingTableNameConflicts(Some some) {
		ValueExpression valueExpression = some.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(ToDate toDate) {
		ValueExpression valueExpression = toDate.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(ToChar toChar) {
		ValueExpression valueExpression = toChar.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(Substring substring) {
		ValueExpression valueExpression = substring.getValueExpression();
		detectingTableNameConflicts(valueExpression);
		ValueExpression startPosition = substring.getStartPosition();
		detectingTableNameConflicts(startPosition);
		ValueExpression stringLength = substring.getStringLength();
		detectingTableNameConflicts(stringLength);
	}
	
	private void detectingTableNameConflicts(Subtraction subtraction) {
		ValueExpression left = subtraction.getLeft();
		detectingTableNameConflicts(left);
		ValueExpression right = subtraction.getRight();
		detectingTableNameConflicts(right);
	}
	
	private void detectingTableNameConflicts(Sum sum) {
		ValueExpression valueExpression = sum.getValueExpression();
		detectingTableNameConflicts(valueExpression);
	}
	
	private void detectingTableNameConflicts(Trim trim) {
		ValueExpression trimCharacter = trim.getTrimCharacter();
		detectingTableNameConflicts(trimCharacter);
		ValueExpression trimSource = trim.getTrimSource();
		detectingTableNameConflicts(trimSource);
	}
	
	private void detectingTableNameConflicts(Upper upper) {
		ValueExpression argument = upper.getValueExpression();
		detectingTableNameConflicts(argument);
	}
	
	private void detectingTableNameConflicts(Subquery subquery) {
		SelectStatement selectStatement = subquery.getSelectStatement();
		detectingTableNameConflicts(selectStatement);
		checkValueExpressionSelectStatement(selectStatement);
	}
	
	private void checkValueExpressionSelectStatement(SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = 
					(QuerySpecification) selectStatement;
			List<SelectSublist> selectList = querySpecification.getSelectList();
			if (selectList.size() != 1) {
				throw Sql4jException.getSql4jException(sourceCode, selectStatement.getBeginIndex(), 
						"Multiple column is not supported.");
			}
			return;
		}
		if (selectStatement instanceof Union) {
			Union union = (Union) selectStatement;
			SelectStatement left = union.getLeft();
			checkValueExpressionSelectStatement(left);
			SelectStatement right = union.getRight();
			checkValueExpressionSelectStatement(right);
			return;
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
	
	private void detectingTableNameConflicts(Union union) {
		SelectStatement left = union.getLeft();
		detectingTableNameConflicts(left);
		SelectStatement right = union.getRight();
		detectingTableNameConflicts(right);
	}
	
	private void detectingTableNameConflicts(UpdateStatement updateStatement) {
		List<SetClause> setClauseList = updateStatement.getSetClauseList();
		for (int i = 0; i < setClauseList.size(); i++) {
			SetClause setClause = setClauseList.get(i);
			ValueExpression updateSource = setClause.getUpdateSource();
			detectingTableNameConflicts(updateSource);
		}
		BooleanValueExpression serachCondition = updateStatement.getSearchCondition();
		detectingTableNameConflicts(serachCondition);
	}
	
	private void detectingTableNameConflicts(InsertStatement insertStatement) {
		SelectStatement	selectStatement = insertStatement.getSelectStatement();
		if (selectStatement != null) {
			detectingTableNameConflicts(selectStatement);
			return;
		}
		List<ValueExpression> valueExpressionList = insertStatement.getValueExpressionList();
		for (int i = 0; i < valueExpressionList.size(); i++) {
			ValueExpression valueExpression = valueExpressionList.get(i);
			detectingTableNameConflicts(valueExpression);
		}
	}
	
	private void detectingTableNameConflicts(DeleteStatement deleteStatement) {
		BooleanValueExpression searchCondition = deleteStatement.getSearchCondition();
		detectingTableNameConflicts(searchCondition);
	}
	
	private void setValueExpressionDataType(Statement statement) {
		if (statement instanceof SelectStatement) {
			SelectStatement selectStatement = 
					(SelectStatement) statement;
			setValueExpressionDataType(selectStatement);
			return;
		}
		if (statement instanceof UpdateStatement) {
			UpdateStatement updateStatement =
					(UpdateStatement) statement;
			setValueExpressionDataType(updateStatement);
			return;
		}
		if (statement instanceof InsertStatement) {
			InsertStatement insertStatement = 
					(InsertStatement) statement;
			setValueExpressionDataType(insertStatement);
			return;
		}
		if (statement instanceof DeleteStatement) {
			DeleteStatement deleteStatement = 
					(DeleteStatement) statement;
			setValueExpressionDataType(deleteStatement);
			return;
		}
		if (statement instanceof CallStatement) {
			CallStatement callStatement = 
					(CallStatement) statement;
			setValueExpressionDataType(callStatement);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, statement.getBeginIndex(), 
				"This statement is not supported.");
	}
	
	private void setValueExpressionDataType(SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = 
					(QuerySpecification) selectStatement;
			setValueExpressionDataType(querySpecification);
			return;
		}
		if (selectStatement instanceof Union) {
			Union union = (Union) selectStatement;
			setValueExpressionDataType(union);
			return;
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
	
	private void setValueExpressionDataType(QuerySpecification querySpecification) {
		List<SelectSublist> selectList = querySpecification.getSelectList();
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			ValueExpression valueExpression = selectSublist.getValueExpression();
			setValueExpressionDataType(querySpecification, valueExpression);
		}
		List<TableReference> tableReferenceList = querySpecification.
				getTableReferenceList();
		if (tableReferenceList != null) {
			setValueExpressionDataTypeTableReferenceList(querySpecification, tableReferenceList);
		}
		BooleanValueExpression whereSearchCondition = 
				querySpecification.getWhereSearchCondition();
		if (whereSearchCondition != null) {
			setValueExpressionDataType(querySpecification, whereSearchCondition);
		}
		List<GroupingElement> groupingElementList = 
				querySpecification.getGroupingElementList();
		if (groupingElementList != null) {
			setValueExpressionDataType(querySpecification, groupingElementList);
		}
		BooleanValueExpression havingSearchCondition = 
				querySpecification.getHavingSearchCondition();
		if (havingSearchCondition != null) {
			setValueExpressionDataType(querySpecification, havingSearchCondition);
		}
		List<SortSpecification> sortSpecificationList = 
				querySpecification.getSortSpecificationList();
		if (sortSpecificationList != null) {
			setValueExpressionDataTypeSortSpecificationList(querySpecification, sortSpecificationList);
		}
	}
	
	private void setValueExpressionDataType(Statement statement, 
			ValueExpression valueExpression) {
		if (valueExpression instanceof NameChain) {
			NameChain nameChain = (NameChain) valueExpression;
			setValueExpressionDataType(statement, nameChain);
			return;
		}
		if (valueExpression instanceof BooleanValueExpression) {
			BooleanValueExpression booleanValueExpression = 
					(BooleanValueExpression) valueExpression;
			setValueExpressionDataType(statement, booleanValueExpression);
			return;
		}
		if (valueExpression instanceof AbsoluteValueExpression) {
			AbsoluteValueExpression absoluteValueExpression = 
					(AbsoluteValueExpression) valueExpression;
			setValueExpressionDataType(statement, absoluteValueExpression);
			return;
		}
		if (valueExpression instanceof Addition) {
			Addition addition = (Addition) valueExpression;
			setValueExpressionDataType(statement, addition);
			return;
		}
		if (valueExpression instanceof Any) {
			Any any = (Any) valueExpression;
			setValueExpressionDataType(statement, any);
			return;
		}
		if (valueExpression instanceof Avg) {
			Avg avg = (Avg) valueExpression;
			setValueExpressionDataType(statement, avg);
			return;
		}
		if (valueExpression instanceof BitLengthExpression) {
			BitLengthExpression bitLengthExpression = 
					(BitLengthExpression) valueExpression;
			setValueExpressionDataType(statement, bitLengthExpression);
			return;
		}
		if (valueExpression instanceof CardinalityExpression) {
			CardinalityExpression cardinalityExpression = 
					(CardinalityExpression) valueExpression;
			setValueExpressionDataType(statement, cardinalityExpression);
			return;
		}
		if (valueExpression instanceof CharLengthExpression) {
			CharLengthExpression charLengthExpression = 
					(CharLengthExpression) valueExpression;
			setValueExpressionDataType(statement, charLengthExpression);
			return;
		}
		if (valueExpression instanceof Coalesce) {
			Coalesce coalesce = (Coalesce) valueExpression;
			setValueExpressionDataType(statement, coalesce);
			return;
		}
		if (valueExpression instanceof Concatenation) {
			Concatenation concatenation = (Concatenation) valueExpression;
			setValueExpressionDataType(statement, concatenation);
			return;
		}
		if (valueExpression instanceof Count) {
			Count count = (Count) valueExpression;
			setValueExpressionDataType(statement, count);
			return;
		}
		if (valueExpression instanceof CurrentDate) {
			CurrentDate currentDate = (CurrentDate) valueExpression;
			setValueExpressionDataType(statement, currentDate);
			return;
		}
		if (valueExpression instanceof CurrentTime) {
			CurrentTime currentTime = (CurrentTime) valueExpression;
			setValueExpressionDataType(statement, currentTime);
			return;
		}
		if (valueExpression instanceof CurrentTimestamp) {
			CurrentTimestamp currentTimestamp = (CurrentTimestamp) valueExpression;
			setValueExpressionDataType(statement, currentTimestamp);
			return;
		}
		if (valueExpression instanceof DateLiteral) {
			DateLiteral dateLiteral = (DateLiteral) valueExpression;
			setValueExpressionDataType(statement, dateLiteral);
			return;
		}
		if (valueExpression instanceof Division) {
			Division division = (Division) valueExpression;
			setValueExpressionDataType(statement, division);
			return;
		}
		if (valueExpression instanceof Every) {
			Every every = (Every) valueExpression;
			setValueExpressionDataType(statement, every);
			return;
		}
		if (valueExpression instanceof ExtractExpression) {
			ExtractExpression extractExpression = 
					(ExtractExpression) valueExpression;
			setValueExpressionDataType(statement, extractExpression);
			return;
		}
		if (valueExpression instanceof FunctionInvocation) {
			FunctionInvocation functionInvocation = 
					(FunctionInvocation) valueExpression;
			setValueExpressionDataType(statement, functionInvocation);
			return;
		}
		if (valueExpression instanceof Grouping) {
			Grouping grouping = (Grouping) valueExpression;
			setValueExpressionDataType(statement, grouping);
			return;
		}
		if (valueExpression instanceof Lower) {
			Lower lower = (Lower) valueExpression;
			setValueExpressionDataType(statement, lower);
			return;
		}
		if (valueExpression instanceof Max) {
			Max max = (Max) valueExpression;
			setValueExpressionDataType(statement, max);
			return;
		}
		if (valueExpression instanceof Min) {
			Min min = (Min) valueExpression;
			setValueExpressionDataType(statement, min);
			return;
		}
		if (valueExpression instanceof ModulusExpression) {
			ModulusExpression modulusExpression = 
					(ModulusExpression) valueExpression;
			setValueExpressionDataType(statement, modulusExpression);
			return;
		}
		if (valueExpression instanceof Multiplication) {
			Multiplication multiplication = (Multiplication) valueExpression;
			setValueExpressionDataType(statement, multiplication);
			return;
		}
		if (valueExpression instanceof NegativeExpression) {
			NegativeExpression negativeExpression = 
					(NegativeExpression) valueExpression;
			setValueExpressionDataType(statement, negativeExpression);
			return;
		}
		if (valueExpression instanceof NullIf) {
			NullIf nullIf = (NullIf) valueExpression;
			setValueExpressionDataType(statement, nullIf);
			return;
		}
		if (valueExpression instanceof NumericLiteral) {
			NumericLiteral numericLiteral = 
					(NumericLiteral) valueExpression;
			setValueExpressionDataType(statement, numericLiteral);
			return;
		}
		if (valueExpression instanceof OctetLengthExpression) {
			OctetLengthExpression octetLengthExpression = 
					(OctetLengthExpression) valueExpression;
			setValueExpressionDataType(statement, octetLengthExpression);
			return;
		}
		if (valueExpression instanceof Parameter) {
			Parameter parameter = (Parameter) valueExpression;
			setValueExpressionDataType(statement, parameter);
			return;
		}
		if (valueExpression instanceof PositionExpression) {
			PositionExpression positionExpression = 
					(PositionExpression) valueExpression;
			setValueExpressionDataType(statement, positionExpression);
			return;
		}
		if (valueExpression instanceof PositiveExpression) {
			PositiveExpression positiveExpression = 
					(PositiveExpression) valueExpression;
			setValueExpressionDataType(statement, positiveExpression);
			return;
		}
		if (valueExpression instanceof SearchedCase) {
			SearchedCase searchedCase = (SearchedCase) valueExpression;
			setValueExpressionDataType(statement, searchedCase);
			return;
		}
		if (valueExpression instanceof SimpleCase) {
			SimpleCase simpleCase = (SimpleCase) valueExpression;
			setValueExpressionDataType(statement, simpleCase);
			return;
		}
		if (valueExpression instanceof Some) {
			Some some = (Some) valueExpression;
			setValueExpressionDataType(statement, some);
			return;
		}
		if (valueExpression instanceof StringLiteral) {
			StringLiteral stringLiteral = 
					(StringLiteral) valueExpression;
			setValueExpressionDataType(statement, stringLiteral);
			return;
		}
		if (valueExpression instanceof Subquery) {
			Subquery subquery = (Subquery) valueExpression;
			setValueExpressionDataType(statement, subquery);
			return;
		}
		if (valueExpression instanceof ToDate) {
			ToDate toDate = (ToDate) valueExpression;
			setValueExpressionDataType(statement, toDate);
			return;
		}
		if (valueExpression instanceof ToChar) {
			ToChar toChar = (ToChar) valueExpression;
			setValueExpressionDataType(statement, toChar);
			return;
		}
		if (valueExpression instanceof Substring) {
			Substring substring = (Substring) valueExpression;
			setValueExpressionDataType(statement, substring);
			return;
		}
		if (valueExpression instanceof Subtraction) {
			Subtraction subtraction = (Subtraction) valueExpression;
			setValueExpressionDataType(statement, subtraction);
			return;
		}
		if (valueExpression instanceof Sum) {
			Sum sum = (Sum) valueExpression;
			setValueExpressionDataType(statement, sum);
			return;
		}
		if (valueExpression instanceof TimeLiteral) {
			TimeLiteral timeLiteral = (TimeLiteral) valueExpression;
			setValueExpressionDataType(statement, timeLiteral);
			return;
		}
		if (valueExpression instanceof TimestampLiteral) {
			TimestampLiteral timestampLiteral = 
					(TimestampLiteral) valueExpression;
			setValueExpressionDataType(statement, timestampLiteral);
			return;
		}
		if (valueExpression instanceof Trim) {
			Trim trim = (Trim) valueExpression;
			setValueExpressionDataType(statement, trim);
			return;
		}
		if (valueExpression instanceof Upper) {
			Upper upper = (Upper) valueExpression;
			setValueExpressionDataType(statement, upper);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				valueExpression.getBeginIndex(), 
				"The value exrepssion is not supported.");
	}
	
	private void setValueExpressionDataTypeTableReferenceList(QuerySpecification querySpecification, 
			List<TableReference> tableReferenceList) {
		for (int i = 0; i < tableReferenceList.size(); i++) {
			TableReference tableReference = tableReferenceList.get(i);
			setValueExpressionDataType(querySpecification, tableReference);
		}
	}
	
	private void setValueExpressionDataType(QuerySpecification querySpecification, 
			TableReference tableReference) {
		if (tableReference instanceof TablePrimary) {
			return;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			TableReference left = leftOuterJoin.getLeft();
			setValueExpressionDataType(querySpecification, left);
			TableReference right = leftOuterJoin.getRight();
			setValueExpressionDataType(querySpecification, right);
			BooleanValueExpression joinCondition = leftOuterJoin.getJoinCondition();
			setValueExpressionDataType(querySpecification, joinCondition);
			return;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			SelectStatement subquery = derivedTable.getSelectStatement();
			setValueExpressionDataType(subquery);
			return;
		}
		if (tableReference instanceof CrossJoin) {
			CrossJoin crossJoin = (CrossJoin) tableReference;
			TableReference left = crossJoin.getLeft();
			setValueExpressionDataType(querySpecification, left);
			TableReference right = crossJoin.getRight();
			setValueExpressionDataType(querySpecification, right);
			return;
		}
		if (tableReference instanceof FullOuterJoin) {
			FullOuterJoin fullOuterJoin = (FullOuterJoin) tableReference;
			TableReference left = fullOuterJoin.getLeft();
			setValueExpressionDataType(querySpecification, left);
			TableReference right = fullOuterJoin.getRight();
			setValueExpressionDataType(querySpecification, right);
			BooleanValueExpression joinCondition = fullOuterJoin.getJoinCondition();
			setValueExpressionDataType(querySpecification, joinCondition);
			return;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			setValueExpressionDataType(querySpecification, left);
			TableReference right = innerJoin.getRight();
			setValueExpressionDataType(querySpecification, right);
			BooleanValueExpression joinCondition = innerJoin.getJoinCondition();
			setValueExpressionDataType(querySpecification, joinCondition);
			return;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			setValueExpressionDataType(querySpecification, left);
			TableReference right = rightOuterJoin.getRight();
			setValueExpressionDataType(querySpecification, right);
			BooleanValueExpression joinCondition = rightOuterJoin.getJoinCondition();
			setValueExpressionDataType(querySpecification, joinCondition);
			return;
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			NaturalFullOuterJoin naturalFullOuterJoin = (NaturalFullOuterJoin) tableReference;
			TableReference left = naturalFullOuterJoin.getLeft();
			setValueExpressionDataType(querySpecification, left);
			TableReference right = naturalFullOuterJoin.getRight();
			setValueExpressionDataType(querySpecification, right);
			return;
		}
		if (tableReference instanceof NaturalInnerJoin) {
			NaturalInnerJoin naturalInnerJoin = (NaturalInnerJoin) tableReference;
			TableReference left = naturalInnerJoin.getLeft();
			setValueExpressionDataType(querySpecification, left);
			TableReference right = naturalInnerJoin.getRight();
			setValueExpressionDataType(querySpecification, right);
			return;
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			NaturalLeftOuterJoin naturalLeftOuterJoin = (NaturalLeftOuterJoin) tableReference;
			TableReference left = naturalLeftOuterJoin.getLeft();
			setValueExpressionDataType(querySpecification, left);
			TableReference right = naturalLeftOuterJoin.getRight();
			setValueExpressionDataType(querySpecification, right);
			return;
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			NaturalRightOuterJoin naturalRightOuterJoin = (NaturalRightOuterJoin) tableReference;
			TableReference left = naturalRightOuterJoin.getLeft();
			setValueExpressionDataType(querySpecification, left);
			TableReference right = naturalRightOuterJoin.getRight();
			setValueExpressionDataType(querySpecification, right);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"The table reference is not supported.");
	}
	
	private void setValueExpressionDataType(QuerySpecification querySpecification, 
			List<GroupingElement> groupingElementList) {
		for (int i = 0; i < groupingElementList.size(); i++) {
			GroupingElement groupingElement = groupingElementList.get(i);
			setValueExpressionDataType(querySpecification, groupingElement);
		}
	}
	
	private void setValueExpressionDataType(QuerySpecification querySpecification, 
			GroupingElement groupingElement) {
		if (groupingElement instanceof CubeList) {
			CubeList cubeList = (CubeList) groupingElement;
			setValueExpressionDataType(querySpecification, cubeList);
			return;
		}
		if (groupingElement instanceof GrandTotal) {
			GrandTotal grandTotal = (GrandTotal) groupingElement;
			setValueExpressionDataType(querySpecification, grandTotal);
			return;
		}
		if (groupingElement instanceof GroupingSetsSpecification) {
			GroupingSetsSpecification groupingSetsSpecification = 
					(GroupingSetsSpecification) groupingElement;
			setValueExpressionDataType(querySpecification, groupingSetsSpecification);
			return;
		}
		if (groupingElement instanceof OrdinaryGroupingSet) {
			OrdinaryGroupingSet ordinaryGroupingSet = (OrdinaryGroupingSet) groupingElement;
			setValueExpressionDataType(querySpecification, ordinaryGroupingSet);
			return;
		}
		if (groupingElement instanceof RollupList) {
			RollupList rollupList = (RollupList) groupingElement;
			setValueExpressionDataType(querySpecification, rollupList);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, groupingElement.getBeginIndex(), 
				"The grouping element is not supported.");
	}
	
	private void setValueExpressionDataType(QuerySpecification querySpecification, 
			CubeList cubeList) {
		for (int i = 0; i < cubeList.size(); i++) {
			GroupingColumnReference groupingColumnReference = cubeList.get(i);
			setValueExpressionDataType(querySpecification, groupingColumnReference);
		}
	}
	
	private void setValueExpressionDataType(QuerySpecification querySpecification, 
			GroupingColumnReference groupingColumnReference) {
		NameChain columnReference = groupingColumnReference.getColumnReference();
		setValueExpressionDataType(querySpecification, columnReference);
	}
	
	private void setValueExpressionDataType(QuerySpecification querySpecification, 
			GrandTotal grandTotal) {
		throw Sql4jException.getSql4jException(sourceCode, grandTotal.getBeginIndex(), 
				"Grand total is not supported.");
	}
	
	private void setValueExpressionDataType(QuerySpecification querySpecification, 
			GroupingSetsSpecification groupingSetsSpecification) {
		for (int i = 0; i < groupingSetsSpecification.size(); i++) {
			GroupingElement groupingElement = groupingSetsSpecification.get(i);
			setValueExpressionDataType(querySpecification, groupingElement);
		}
	}
	
	private void setValueExpressionDataType(QuerySpecification querySpecification, 
			OrdinaryGroupingSet ordinaryGroupingSet) {
		for (int i = 0; i < ordinaryGroupingSet.size(); i++) {
			GroupingColumnReference groupingColumnReference = ordinaryGroupingSet.get(i);
			setValueExpressionDataType(querySpecification, groupingColumnReference);
		}
	}
	
	private void setValueExpressionDataType(QuerySpecification querySpecification, 
			RollupList rollupList) {
		for (int i = 0; i < rollupList.size(); i++) {
			GroupingColumnReference groupingColumnReference = rollupList.get(i);
			setValueExpressionDataType(querySpecification, groupingColumnReference);
		}
	}
	
	private void setValueExpressionDataTypeSortSpecificationList(QuerySpecification querySpecification, 
			List<SortSpecification> sortSpecificationList) {
		for (int i = 0; i < sortSpecificationList.size(); i++) {
			SortSpecification sortSpecification = sortSpecificationList.get(i);
			ValueExpression sortKey = sortSpecification.getSortKey();
			setValueExpressionDataType(querySpecification, sortKey);
		}
	}
	
	private void setValueExpressionDataType(Statement statement, BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = 
					(BooleanValue) booleanValueExpression;
			setValueExpressionDataType(statement, booleanValue);
			return;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = 
					(Predicate) booleanValueExpression;
			setValueExpressionDataType(statement, predicate);
			return;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = 
					(BooleanFactor) booleanValueExpression;
			setValueExpressionDataType(statement, booleanFactor);
			return;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			setValueExpressionDataType(statement, booleanTerm);
			return;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			setValueExpressionDataType(statement, booleanTest);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				booleanValueExpression.getBeginIndex(), 
				"The boolean value exrepssion is not supported.");
	}
	
	private void setValueExpressionDataType(Statement statement, BooleanValue booleanValue) {
		for (int i = 0; i < booleanValue.size(); i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			setValueExpressionDataType(statement, booleanTerm);
		}
		booleanValue.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = 
					(ComparisonPredicate) predicate;
			setValueExpressionDataType(statement, comparisonPredicate);
			return;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = 
					(BetweenPredicate) predicate;
			setValueExpressionDataType(statement, betweenPredicate);
			return;
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = 
					(DistinctPredicate) predicate;
			setValueExpressionDataType(statement, distinctPredicate);
			return;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = 
					(ExistsPredicate) predicate;
			setValueExpressionDataType(statement, existsPredicate);
			return;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = 
					(InPredicate) predicate;
			setValueExpressionDataType(statement, inPredicate);
			return;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = 
					(LikePredicate) predicate;
			setValueExpressionDataType(statement, likePredicate);
			return;
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = 
					(MatchPredicate) predicate;
			setValueExpressionDataType(statement, matchPredicate);
			return;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = 
					(NullPredicate) predicate;
			setValueExpressionDataType(statement, nullPredicate);
			return;
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = 
					(OverlapsPredicate) predicate;
			setValueExpressionDataType(statement, overlapsPredicate);
			return;
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = 
					(SimilarPredicate) predicate;
			setValueExpressionDataType(statement, similarPredicate);
			return;
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = 
					(UniquePredicate) predicate;
			setValueExpressionDataType(statement, uniquePredicate);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"Not support the predicate.");
	}
	
	private void setValueExpressionDataType(Statement statement, ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		setValueExpressionDataType(statement, left);
		ValueExpression right = comparisonPredicate.getRight();
		setValueExpressionDataType(statement, right);
		comparisonPredicate.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, BetweenPredicate betweenPredicate) {
		ValueExpression valueExpression = betweenPredicate.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
		setValueExpressionDataType(statement, valueExpression1);
		ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
		setValueExpressionDataType(statement, valueExpression2);
		betweenPredicate.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, DistinctPredicate distinctPredicate) {
		ValueExpression left = distinctPredicate.getLeft();
		setValueExpressionDataType(statement, left);
		ValueExpression right = distinctPredicate.getRight();
		setValueExpressionDataType(statement, right);
		distinctPredicate.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, ExistsPredicate existsPredicate) {
		Subquery subquery = existsPredicate.getSubquery();
		setValueExpressionDataType(statement, subquery);
		existsPredicate.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, InPredicate inPredicate) {
		ValueExpression valueExpression = inPredicate.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		Subquery subquery = inPredicate.getSubquery();
		if (subquery != null) {
			setValueExpressionDataType(statement, subquery);
		} else {
			List<ValueExpression> inValueList = inPredicate.getInValueList();
			for (int i = 0; i < inValueList.size(); i++) {
				ValueExpression inValue = inValueList.get(i);
				setValueExpressionDataType(statement, inValue);
			}
		}
		inPredicate.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, LikePredicate likePredicate) {
		ValueExpression valueExpression = likePredicate.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		ValueExpression characterPattern = likePredicate.getCharacterPattern();
		setValueExpressionDataType(statement, characterPattern);
		ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
		setValueExpressionDataType(statement, escapeCharacter);
		likePredicate.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, MatchPredicate matchPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, matchPredicate.getBeginIndex(), 
				"Not support match predicate.");
	}
	
	private void setValueExpressionDataType(Statement statement, NullPredicate nullPredicate) {
		ValueExpression valueExpression = nullPredicate.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		nullPredicate.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, OverlapsPredicate overlapsPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, overlapsPredicate.getBeginIndex(), 
				"Not support overlaps predicate.");
	}
	
	private void setValueExpressionDataType(Statement statement, SimilarPredicate similarPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, similarPredicate.getBeginIndex(), 
				"Not support similar predicate.");
	}
	
	private void setValueExpressionDataType(Statement statement, UniquePredicate uniquePredicate) {
		throw Sql4jException.getSql4jException(sourceCode, uniquePredicate.getBeginIndex(), 
				"Not support unique predicate.");
	}
	
	private void setValueExpressionDataType(Statement statement, BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = booleanFactor.getBooleanValueExpression();
		setValueExpressionDataType(statement, booleanValueExpression);
		booleanFactor.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, BooleanTerm booleanTerm) {
		for (int i = 0; i < booleanTerm.size(); i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			setValueExpressionDataType(statement, booleanFactor);
		}
		booleanTerm.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = booleanTest.getBooleanValueExpression();
		setValueExpressionDataType(statement, booleanValueExpression);
		booleanTest.setDataType(JdbcType.BOOLEAN);
	}
	
	private void setValueExpressionDataType(Statement statement, AbsoluteValueExpression absoluteValueExpression) {
		ValueExpression valueExpression = absoluteValueExpression.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType jdbcType = valueExpression.getDataType();
		absoluteValueExpression.setDataType(jdbcType);
	}
	
	private void setValueExpressionDataType(Statement statement, Addition addition) {
		ValueExpression left = addition.getLeft();
		setValueExpressionDataType(statement, left);
		JdbcType leftDataType = left.getDataType();
		JdbcTypeType leftDataTypeType = leftDataType.getJdbcTypeType();
		if (leftDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			leftDataTypeType == JdbcTypeType.BYTE_ARRAY) {
			throw Sql4jException.getSql4jException(sourceCode, left.getBeginIndex(), 
						leftDataType.getContent() + " is not supported.");
		}
		ValueExpression right = addition.getRight();
		setValueExpressionDataType(statement, right);
		JdbcType rightDataType = left.getDataType();
		JdbcTypeType rightDataTypeType = rightDataType.getJdbcTypeType();
		if (rightDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			rightDataTypeType == JdbcTypeType.BYTE_ARRAY) {
			throw Sql4jException.getSql4jException(sourceCode, right.getBeginIndex(), 
					rightDataType.getContent() + " is not supported.");
		}
		addition.setDataType(JdbcType.NUMERIC);
	}
	
	private void setValueExpressionDataType(Statement statement, Any any) {
		ValueExpression valueExpression = any.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType jdbcType = valueExpression.getDataType();
		any.setDataType(jdbcType);
	}
	
	private void setValueExpressionDataType(Statement statement, Avg avg) {
		ValueExpression valueExpression = avg.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType jdbcType = valueExpression.getDataType();
		avg.setDataType(jdbcType);
	}
	
	private void setValueExpressionDataType(Statement statement, BitLengthExpression bitLengthExpression) {
		ValueExpression valueExpression = bitLengthExpression.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		bitLengthExpression.setDataType(JdbcType.INTEGER);
	}
	
	private void setValueExpressionDataType(Statement statement, CardinalityExpression cardinalityExpression) {
		ValueExpression valueExpression = cardinalityExpression.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType jdbcType = valueExpression.getDataType();
		cardinalityExpression.setDataType(jdbcType);
	}
	
	private void setValueExpressionDataType(Statement statement, CharLengthExpression charLengthExpression) {
		ValueExpression valueExpression = charLengthExpression.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		charLengthExpression.setDataType(JdbcType.INTEGER);
	}
	
	private void setValueExpressionDataType(Statement statement, Coalesce coalesce) {
		List<ValueExpression> arguments = coalesce.getArguments();
		ValueExpression valueExpression = arguments.get(0);
		setValueExpressionDataType(statement, valueExpression);
		JdbcType dataType = valueExpression.getDataType();
		JdbcTypeType dataTypeType = dataType.getJdbcTypeType();
		for (int i = 1; i < arguments.size(); i++) {
			ValueExpression argument = arguments.get(i);
			setValueExpressionDataType(statement, argument);
			JdbcType argumentDataType = argument.getDataType();
			JdbcTypeType argumentDataTypeType = argumentDataType.getJdbcTypeType();
			if (argumentDataTypeType != dataTypeType) {
				throw Sql4jException.getSql4jException(sourceCode, argument.getBeginIndex(), 
						"The type of the argument must be the same as the type of the first argument.");
			}
		}
	}
	
	private void setValueExpressionDataType(Statement statement, Concatenation concatenation) {
		ValueExpression left = concatenation.getLeft();
		setValueExpressionDataType(statement, left);
		JdbcType leftDataType = left.getDataType();
		JdbcTypeType leftDataTypeType = leftDataType.getJdbcTypeType();
		if (leftDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			leftDataTypeType == JdbcTypeType.BYTE_ARRAY ||
			leftDataTypeType == JdbcTypeType.DATE ||
			leftDataTypeType == JdbcTypeType.TIME ||
			leftDataTypeType == JdbcTypeType.TIMESTAMP) {
			throw Sql4jException.getSql4jException(sourceCode, left.getBeginIndex(), 
					leftDataType.getContent() + " is not supported.");
		}
		ValueExpression right = concatenation.getRight();
		setValueExpressionDataType(statement, right);
		JdbcType rightDataType = left.getDataType();
		JdbcTypeType rightDataTypeType = rightDataType.getJdbcTypeType();
		if (rightDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			rightDataTypeType == JdbcTypeType.BYTE_ARRAY ||
			rightDataTypeType == JdbcTypeType.DATE ||
			rightDataTypeType == JdbcTypeType.TIME ||
			rightDataTypeType == JdbcTypeType.TIMESTAMP) {
			throw Sql4jException.getSql4jException(sourceCode, right.getBeginIndex(), 
					rightDataType.getContent() + " is not supported.");
		}
		concatenation.setDataType(JdbcType.VARCHAR);
	}
	
	private void setValueExpressionDataType(Statement statement, Count count) {
		count.setDataType(JdbcType.BIGINT);
	}
	
	private void setValueExpressionDataType(Statement statement, CurrentDate currentDate) {
		currentDate.setDataType(JdbcType.DATE);
	}
	
	private void setValueExpressionDataType(Statement statement, CurrentTime currentTime) {
		currentTime.setDataType(JdbcType.TIME);
	}
	
	private void setValueExpressionDataType(Statement statement, CurrentTimestamp currentTimestamp) {
		currentTimestamp.setDataType(JdbcType.TIMESTAMP);
	}
	
	private void setValueExpressionDataType(Statement statement, DateLiteral dateLiteral) {
		dateLiteral.setDataType(JdbcType.DATE);
	}
	
	private void setValueExpressionDataType(Statement statement, TimeLiteral timeLiteral) {
		timeLiteral.setDataType(JdbcType.TIME);
	}
	
	private void setValueExpressionDataType(Statement statement, TimestampLiteral timestampLiteral) {
		timestampLiteral.setDataType(JdbcType.TIMESTAMP);
	}
	
	private void setValueExpressionDataType(Statement statement, Division division) {
		ValueExpression left = division.getLeft();
		setValueExpressionDataType(statement, left);
		JdbcType leftDataType = left.getDataType();
		JdbcTypeType leftDataTypeType = leftDataType.getJdbcTypeType();
		if (leftDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			leftDataTypeType == JdbcTypeType.BYTE_ARRAY) {
			throw Sql4jException.getSql4jException(sourceCode, left.getBeginIndex(), 
						leftDataType.getContent() + " is not supported.");
		}
		ValueExpression right = division.getRight();
		setValueExpressionDataType(statement, right);
		JdbcType rightDataType = left.getDataType();
		JdbcTypeType rightDataTypeType = rightDataType.getJdbcTypeType();
		if (rightDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			rightDataTypeType == JdbcTypeType.BYTE_ARRAY) {
			throw Sql4jException.getSql4jException(sourceCode, right.getBeginIndex(), 
					rightDataType.getContent() + " is not supported.");
		}
		division.setDataType(JdbcType.NUMERIC);
	}
	
	private void setValueExpressionDataType(Statement statement, Every every) {
		ValueExpression valueExpression = every.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType dataType = valueExpression.getDataType();
		every.setDataType(dataType);
	}
	
	private void setValueExpressionDataType(Statement statement, ExtractExpression extractExpression) {
		ValueExpression extractSource = extractExpression.getExtractSource();
		setValueExpressionDataType(statement, extractSource);
		JdbcType dataType = extractSource.getDataType();
		JdbcTypeType dataTypeType = dataType.getJdbcTypeType();
		if (dataTypeType != JdbcTypeType.DATE &&
			dataTypeType != JdbcTypeType.TIMESTAMP) {
			throw Sql4jException.getSql4jException(sourceCode, extractSource.getBeginIndex(), 
					"Support only date or timestamp types.");
		}
		extractExpression.setDataType(JdbcType.INTEGER);
	}
	
	private void setValueExpressionDataType(Statement statement, FunctionInvocation functionInvocation) {
		List<ValueExpression> arguments = functionInvocation.getArguments();
		for (int i = 0; i < arguments.size(); i++) {
			ValueExpression argument = arguments.get(i);
			setValueExpressionDataType(statement, argument);
		}
		functionInvocation.setDataType(JdbcType._UNKNOWN_TYPE_);
	}
	
	private void setValueExpressionDataType(Statement statement, Grouping grouping) {
		throw Sql4jException.getSql4jException(sourceCode, grouping.getBeginIndex(), 
				"Grouping is not supported.");
	}
	
	private void setValueExpressionDataType(Statement statement, Lower lower) {
		ValueExpression argument = lower.getValueExpression();
		setValueExpressionDataType(statement, argument);
		JdbcType jdbcType = argument.getDataType();
		lower.setDataType(jdbcType);
	}
	
	private void setValueExpressionDataType(Statement statement, Max max) {
		ValueExpression valueExpression = max.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType jdbcType = valueExpression.getDataType();
		max.setDataType(jdbcType);
	}
	
	private void setValueExpressionDataType(Statement statement, Min min) {
		ValueExpression valueExpression = min.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType jdbcType = valueExpression.getDataType();
		min.setDataType(jdbcType);
	}
	
	private void setValueExpressionDataType(Statement statement, ModulusExpression modulusExpression) {
		ValueExpression dividend = modulusExpression.getDividend();
		setValueExpressionDataType(statement, dividend);
		JdbcType dividendDataType = dividend.getDataType();
		JdbcTypeType dividendDataTypeType = dividendDataType.getJdbcTypeType();
		if (dividendDataTypeType != JdbcTypeType.FLOAT &&
			dividendDataTypeType != JdbcTypeType.INTEGER) {
			throw Sql4jException.getSql4jException(sourceCode, dividend.getBeginIndex(), 
					"Support only numeric type.");
		}
		ValueExpression divisor = modulusExpression.getDivisor();
		setValueExpressionDataType(statement, divisor);
		JdbcType divisorDataType = divisor.getDataType();
		JdbcTypeType divisorDataTypeType = divisorDataType.getJdbcTypeType();
		if (divisorDataTypeType != JdbcTypeType.FLOAT &&
			divisorDataTypeType != JdbcTypeType.INTEGER) {
			throw Sql4jException.getSql4jException(sourceCode, divisor.getBeginIndex(), 
					"Support only numeric type.");
		}
		modulusExpression.setDataType(JdbcType.NUMERIC);
	}
	
	private void setValueExpressionDataType(Statement statement, Multiplication multiplication) {
		ValueExpression left = multiplication.getLeft();
		setValueExpressionDataType(statement, left);
		JdbcType leftDataType = left.getDataType();
		JdbcTypeType leftDataTypeType = leftDataType.getJdbcTypeType();
		if (leftDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			leftDataTypeType == JdbcTypeType.BYTE_ARRAY) {
			throw Sql4jException.getSql4jException(sourceCode, left.getBeginIndex(), 
						leftDataType.getContent() + " is not supported.");
		}
		ValueExpression right = multiplication.getRight();
		setValueExpressionDataType(statement, right);
		JdbcType rightDataType = left.getDataType();
		JdbcTypeType rightDataTypeType = rightDataType.getJdbcTypeType();
		if (rightDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			rightDataTypeType == JdbcTypeType.BYTE_ARRAY) {
			throw Sql4jException.getSql4jException(sourceCode, right.getBeginIndex(), 
					rightDataType.getContent() + " is not supported.");
		}
		multiplication.setDataType(JdbcType.NUMERIC);
	}
	
	private void setValueExpressionDataType(Statement statement, NameChain nameChain) {
		if (statement instanceof QuerySpecification) {
			QuerySpecification querySpecification = (QuerySpecification) statement;
			setValueExpressionDataType(querySpecification, nameChain);
			return;
		}
		if (statement instanceof InsertStatement) {
			InsertStatement insertStatement = (InsertStatement) statement;
			setValueExpressionDataType(insertStatement, nameChain);
			return;
		}
		if (statement instanceof UpdateStatement) {
			UpdateStatement updateStatement = (UpdateStatement) statement;
			setValueExpressionDataType(updateStatement, nameChain);
			return;
		}
		if (statement instanceof DeleteStatement) {
			DeleteStatement deleteStatement = (DeleteStatement) statement;
			setValueExpressionDataType(deleteStatement, nameChain);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, statement.getBeginIndex(), 
				"This statement does not support setting expression types.");
	}
	
	private void setValueExpressionDataType(QuerySpecification querySpecification, NameChain nameChain) {
		NameChain columnName = null;
		List<TableReference> tableReferenceList = querySpecification.getTableReferenceList();
		if (tableReferenceList.size() == 1 && tableReferenceList.get(0) instanceof TablePrimary && nameChain.size() == 1) {
			TableReference tableReference = tableReferenceList.get(0);
			TablePrimary tablePrimary = (TablePrimary) tableReference;
			NameChain tableName = tablePrimary.getTableName();
			List<Name> list = new ArrayList<Name>(2);
			for (int i = 0; i < tableName.size(); i++) {
				Name name = tableName.get(i);
				list.add(name);
			}
			list.add(nameChain.get(0));
			columnName = new NameChain(list);
		} else {
			for (int i = 0; i < tableReferenceList.size(); i++) {
				TableReference tableReference = tableReferenceList.get(i);
				columnName = getColumnName(tableReference, nameChain);
				if (columnName != null) {
					break;
				}
			}
		}
		if (columnName == DEFAULT_NAME_CHAIN) {
			return;
		}
		if (columnName == null) {
			Statement statement = querySpecification.getParentStatement();
			if (statement != null) {
				setValueExpressionDataType(statement, nameChain);
				return;
			}
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Table information not found.");
		}
		ColumnMetadata columnMetadata = configuration.getColumnMetadata(columnName);
		int columnType = columnMetadata.getColumnType();
		JdbcType dataType = JdbcType.getJdbcType(columnType);
		if (dataType.getJdbcTypeType() == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE) {
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					dataType.getContent() + " is not supported.");
		}
		nameChain.setDataType(dataType);
	}
	
	private NameChain getColumnName(TableReference tableReference, NameChain nameChain) {
		if (tableReference instanceof TablePrimary) {
			TablePrimary tablePrimary = (TablePrimary) tableReference;
			NameChain columnName = getColumnName(tablePrimary, nameChain);
			return columnName;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			NameChain columnName = getColumnName(leftOuterJoin, nameChain);
			return columnName;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			NameChain columnName = getColumnName(derivedTable, nameChain);
			return columnName;
		}
		if (tableReference instanceof CrossJoin) {
			CrossJoin crossJoin = (CrossJoin) tableReference;
			TableReference left = crossJoin.getLeft();
			NameChain columnName = getColumnName(left, nameChain);
			if (columnName != null) {
				return columnName;
			}
			TableReference right = crossJoin.getRight();
			columnName = getColumnName(right, nameChain);
			return columnName;
		}
		if (tableReference instanceof FullOuterJoin) {
			FullOuterJoin fullOuterJoin = (FullOuterJoin) tableReference;
			TableReference left = fullOuterJoin.getLeft();
			NameChain columnName = getColumnName(left, nameChain);
			if (columnName != null) {
				return columnName;
			}
			TableReference right = fullOuterJoin.getRight();
			columnName = getColumnName(right, nameChain);
			return columnName;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			NameChain columnName = getColumnName(left, nameChain);
			if (columnName != null) {
				return columnName;
			}
			TableReference right = innerJoin.getRight();
			columnName = getColumnName(right, nameChain);
			return columnName;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			NameChain columnName = getColumnName(left, nameChain);
			if (columnName != null) {
				return columnName;
			}
			TableReference right = rightOuterJoin.getRight();
			columnName = getColumnName(right, nameChain);
			return columnName;
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			NaturalFullOuterJoin naturalFullOuterJoin = (NaturalFullOuterJoin) tableReference;
			TableReference left = naturalFullOuterJoin.getLeft();
			NameChain columnName = getColumnName(left, nameChain);
			if (columnName != null) {
				return columnName;
			}
			TableReference right = naturalFullOuterJoin.getRight();
			columnName = getColumnName(right, nameChain);
			return columnName;
		}
		if (tableReference instanceof NaturalInnerJoin) {
			NaturalInnerJoin naturalInnerJoin = (NaturalInnerJoin) tableReference;
			TableReference left = naturalInnerJoin.getLeft();
			NameChain columnName = getColumnName(left, nameChain);
			if (columnName != null) {
				return columnName;
			}
			TableReference right = naturalInnerJoin.getRight();
			columnName = getColumnName(right, nameChain);
			return columnName;
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			NaturalLeftOuterJoin naturalLeftOuterJoin = (NaturalLeftOuterJoin) tableReference;
			TableReference left = naturalLeftOuterJoin.getLeft();
			NameChain columnName = getColumnName(left, nameChain);
			if (columnName != null) {
				return columnName;
			}
			TableReference right = naturalLeftOuterJoin.getRight();
			columnName = getColumnName(right, nameChain);
			return columnName;
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			NaturalRightOuterJoin naturalRightOuterJoin = (NaturalRightOuterJoin) tableReference;
			TableReference left = naturalRightOuterJoin.getLeft();
			NameChain columnName = getColumnName(left, nameChain);
			if (columnName != null) {
				return columnName;
			}
			TableReference right = naturalRightOuterJoin.getRight();
			columnName = getColumnName(right, nameChain);
			return columnName;
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"The table reference is not supported.");
	}
	
	private NameChain getColumnName(TablePrimary tablePrimary, NameChain nameChain) {
		Name name = tablePrimary.getCorrelationName();
		boolean target = false;
		if (name != null) {
			if (nameChain.size() == 2) {
				String tableName1 = name.getContent().toLowerCase();
				String tableName2 = nameChain.get(0).getContent().toLowerCase();
				if (tableName1.equals(tableName2)) {
					target = true;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			NameChain tableName1 = tablePrimary.getTableName();
			if (tableName1.size() + 1 != nameChain.size()) {
				return null;
			} else {
				for (int i = 0; i < tableName1.size(); i++) {
					String name1 = tableName1.get(i).getContent().toLowerCase();
					String name2 = nameChain.get(i).getContent().toLowerCase();
					if (!name1.equals(name2)) {
						return null;
					}
				}
				target = true;
			}
		}
		if (target == false) {
			return null;
		}
		NameChain tableName = tablePrimary.getTableName();
		List<Name> list = new ArrayList<Name>(tableName.size() + 1);
		for (int i = 0; i < tableName.size(); i++) {
			Name _name = tableName.get(i);
			list.add(_name);
		}
		list.add(nameChain.get(nameChain.size() - 1));
		NameChain columnName = new NameChain(list);
		return columnName;
	}
	
	private NameChain getColumnName(LeftOuterJoin leftOuterJoin, NameChain nameChain) {
		TableReference left = leftOuterJoin.getLeft();
		NameChain columnName = getColumnName(left, nameChain);
		if (columnName != null) {
			return columnName;
		}
		TableReference right = leftOuterJoin.getRight();
		columnName = getColumnName(right, nameChain);
		return columnName;
	}
	
	private NameChain getColumnName(DerivedTable derivedTable, NameChain nameChain) {
		if (nameChain.size() != 2) {
			return null;
		}
		Name correlationName = derivedTable.getCorrelationName();
		String tableName1 = correlationName.getContent().toLowerCase();
		String tableName2 = nameChain.get(0).getContent().toLowerCase();
		if (!tableName1.equals(tableName2)) {
			return null;
		}
		SelectStatement subquery = derivedTable.getSelectStatement();
		//SelectStatement selectStatement = subquery.getSelectStatement();
		boolean b = setDerivedTableValueExpressionDataType(subquery, nameChain);
		if (b == false) {
			return null;
		}
		return DEFAULT_NAME_CHAIN;
	}
	
	private boolean setDerivedTableValueExpressionDataType(SelectStatement selectStatement, NameChain nameChain) {
		Name name = nameChain.get(nameChain.size() - 1);
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = 
					(QuerySpecification) selectStatement;
			List<SelectSublist> selectList = querySpecification.getSelectList();
			for (int i = 0; i < selectList.size(); i++) {
				SelectSublist selectSublist = selectList.get(i);
				Name name_ = selectSublist.getName();
				if (name_ != null) {
					if (name_.getContent().equalsIgnoreCase(name.getContent())) {
						JdbcType dataType = selectSublist.getValueExpression().getDataType();
						nameChain.setDataType(dataType);
						return true;
					}
				} else {
					ValueExpression valueExpression = selectSublist.getValueExpression();
					if (!(valueExpression instanceof NameChain)) {
						continue;
					}
					NameChain columnName = (NameChain) valueExpression;
					String name1 = name.getContent();
					String name2 = columnName.get(columnName.size() - 1).getContent();
					if (!name1.equalsIgnoreCase(name2)) {
						continue;
					}
					JdbcType dataType = columnName.getDataType();
					nameChain.setDataType(dataType);
					return true;
				}
			}
			return false;
		}
		if (selectStatement instanceof Union) {
			Union union = (Union) selectStatement;
			SelectStatement left = union.getLeft();
			boolean b = setDerivedTableValueExpressionDataType(left, nameChain);
			if (b == true) {
				return true;
			}
			SelectStatement right = union.getRight();
			b = setDerivedTableValueExpressionDataType(right, nameChain);
			return b;
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
	
	private void setValueExpressionDataType(Statement statement, NegativeExpression negativeExpression) {
		ValueExpression valueExpression = negativeExpression.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		negativeExpression.setDataType(JdbcType.NUMERIC);
	}
	
	private void setValueExpressionDataType(Statement statement, NullIf nullIf) {
		ValueExpression first = nullIf.getFirst();
		setValueExpressionDataType(statement, first);
		JdbcType firstDataType = first.getDataType();
		JdbcTypeType firstDataTypeType = firstDataType.getJdbcTypeType();
		if (firstDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			firstDataTypeType == JdbcTypeType.BYTE_ARRAY) {
			throw Sql4jException.getSql4jException(sourceCode, first.getBeginIndex(), 
					firstDataType.getContent() + " is not supported.");
		}
		ValueExpression second = nullIf.getSecond();
		setValueExpressionDataType(statement, second);
		JdbcType secondDataType = second.getDataType();
		JdbcTypeType secondDataTypeType = secondDataType.getJdbcTypeType();
		if (secondDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			secondDataTypeType == JdbcTypeType.BYTE_ARRAY) {
			throw Sql4jException.getSql4jException(sourceCode, second.getBeginIndex(), 
					secondDataType.getContent() + " is not supported.");
		}
		if (secondDataTypeType != firstDataTypeType) {
			throw Sql4jException.getSql4jException(sourceCode, second.getBeginIndex(), 
					"The type of the operand must be the same as that of the first operand.");
		}
		nullIf.setDataType(firstDataType);
	}
	
	private void setValueExpressionDataType(Statement statement, NumericLiteral numericLiteral) {
		numericLiteral.setDataType(JdbcType.NUMERIC);
	}
	
	private void setValueExpressionDataType(Statement statement, OctetLengthExpression octetLengthExpression) {
		ValueExpression valueExpression = octetLengthExpression.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		octetLengthExpression.setDataType(JdbcType.INTEGER);
	}
	
	private void setValueExpressionDataType(Statement statement, Parameter parameter) {
		parameter.setDataType(JdbcType._UNKNOWN_TYPE_);
	}
	
	private void setValueExpressionDataType(Statement statement, PositionExpression positionExpression) {
		ValueExpression valueExpression1 = positionExpression.getValueExpression1();
		setValueExpressionDataType(statement, valueExpression1);
		ValueExpression valueExpression2 = positionExpression.getValueExpression2();
		setValueExpressionDataType(statement, valueExpression2);
		positionExpression.setDataType(JdbcType.INTEGER);
	}
	
	private void setValueExpressionDataType(Statement statement, PositiveExpression positiveExpression) {
		ValueExpression valueExpression = positiveExpression.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		positiveExpression.setDataType(JdbcType.NUMERIC);
	}
	
	private void setValueExpressionDataType(Statement statement, SearchedCase searchedCase) {
		List<SearchedWhenClause> list = searchedCase.getSearchedWhenClauseList();
		SearchedWhenClause searchedWhenClause = list.get(0);
		ValueExpression result = searchedWhenClause.getResult();
		setValueExpressionDataType(statement, result);
		JdbcType resultDataType = result.getDataType();
		JdbcTypeType resultDataTypeType = resultDataType.getJdbcTypeType();
		for (int i = 0; i < list.size(); i++) {
			SearchedWhenClause _searchedWhenClause = list.get(i);
			BooleanValueExpression searchedCondition = 
					_searchedWhenClause.getSearchedCondition();
			setValueExpressionDataType(statement, searchedCondition);
			JdbcType _dataType = searchedCondition.getDataType();
			if (_dataType != JdbcType.BOOLEAN) {
				throw Sql4jException.getSql4jException(sourceCode, searchedCondition.getBeginIndex(), 
						"Support only boolean type.");
			}
			ValueExpression _result = _searchedWhenClause.getResult();
			setValueExpressionDataType(statement, _result);
			JdbcType _resultDataType = _result.getDataType();
			JdbcTypeType _resultDataTypeType = _resultDataType.getJdbcTypeType();
			if (_resultDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE) {
				throw Sql4jException.getSql4jException(sourceCode, _result.getBeginIndex(), 
						_resultDataType.getContent() + " is not supported.");
			}
			if (_resultDataTypeType != resultDataTypeType) {
				throw Sql4jException.getSql4jException(sourceCode, _result.getBeginIndex(), 
						"The type of the operand must be the same as the type of the first result operand.");
			}
		}
		ElseClause elseClause = searchedCase.getElseClause();
		if (elseClause == null) {
			searchedCase.setDataType(resultDataType);
			return;
		}
		ValueExpression result2 = elseClause.getResult();
		setValueExpressionDataType(statement, result2);
		JdbcType resultDataType2 = result2.getDataType();
		JdbcTypeType resultDataTypeType2 = resultDataType2.getJdbcTypeType();
		if (resultDataTypeType2 == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE) {
			throw Sql4jException.getSql4jException(sourceCode, result2.getBeginIndex(), 
					resultDataType2.getContent() + " is not supported.");
		}
		if (resultDataTypeType2 != resultDataTypeType) {
			throw Sql4jException.getSql4jException(sourceCode, result2.getBeginIndex(), 
					"The type of the operand must be the same as the type of the first result operand.");
		}
		searchedCase.setDataType(resultDataType);
	}
	
	private void setValueExpressionDataType(Statement statement, SimpleCase simpleCase) {
		ValueExpression caseOperand = simpleCase.getCaseOperand();
		setValueExpressionDataType(statement, caseOperand);
		JdbcType caseOperandDataType = caseOperand.getDataType();
		JdbcTypeType caseOperandDataTypeType = caseOperandDataType.getJdbcTypeType();
		if (caseOperandDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE) {
			throw Sql4jException.getSql4jException(sourceCode, caseOperand.getBeginIndex(), 
					caseOperandDataType.getContent() + " is not supported.");
		}
		List<SimpleWhenClause> simpleWhenClauseList = simpleCase.getSimpleWhenClauseList();
		ValueExpression result = simpleWhenClauseList.get(0).getResult();
		setValueExpressionDataType(statement, result);
		JdbcType resultDataType = result.getDataType();
		JdbcTypeType resultDataTypeType = resultDataType.getJdbcTypeType();
		for (int i = 0; i < simpleWhenClauseList.size(); i++) {
			SimpleWhenClause simpleWhenClause = simpleWhenClauseList.get(i);
			ValueExpression whenOperand = simpleWhenClause.getWhenOperand();
			setValueExpressionDataType(statement, whenOperand);
			JdbcType whenOperandDataType = whenOperand.getDataType();
			JdbcTypeType whenOperandDataTypeType = whenOperandDataType.getJdbcTypeType();
			if (whenOperandDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE) {
				throw Sql4jException.getSql4jException(sourceCode, whenOperand.getBeginIndex(), 
						whenOperandDataType.getContent() + " is not supported.");
			}
			if (whenOperandDataTypeType != caseOperandDataTypeType) {
				throw Sql4jException.getSql4jException(sourceCode, whenOperand.getBeginIndex(), 
						"The type of the operand must be the same as the type of the case operand.");
			}
			ValueExpression result2 = simpleWhenClause.getResult();
			setValueExpressionDataType(statement, result2);
			JdbcType resultDataType2 = result2.getDataType();
			JdbcTypeType resultDataTypeType2 = resultDataType2.getJdbcTypeType();
			if (resultDataTypeType2 == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE) {
				throw Sql4jException.getSql4jException(sourceCode, result2.getBeginIndex(), 
						resultDataType2.getContent() + " is not supported.");
			}
			if (resultDataTypeType2 != resultDataTypeType) {
				throw Sql4jException.getSql4jException(sourceCode, whenOperand.getBeginIndex(), 
						"The type of the operand must be the same as the type of the first result operand.");
			}
		}
		ElseClause elseClause = simpleCase.getElseClause();
		if (elseClause == null) {
			simpleCase.setDataType(resultDataType);
			return;
		}
		ValueExpression result2 = elseClause.getResult();
		setValueExpressionDataType(statement, result2);
		JdbcType resultDataType2 = result2.getDataType();
		JdbcTypeType resultDataTypeType2 = resultDataType2.getJdbcTypeType();
		if (resultDataTypeType2 == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE) {
			throw Sql4jException.getSql4jException(sourceCode, result2.getBeginIndex(), 
					resultDataType2.getContent() + " is not supported.");
		}
		if (resultDataTypeType2 != resultDataTypeType) {
			throw Sql4jException.getSql4jException(sourceCode, result2.getBeginIndex(), 
					"The type of the operand must be the same as the type of the first result operand.");
		}
		simpleCase.setDataType(resultDataType);
	}
	
	private void setValueExpressionDataType(Statement statement, Some some) {
		ValueExpression valueExpression = some.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType dataType = valueExpression.getDataType();
		some.setDataType(dataType);
	}
	
	private void setValueExpressionDataType(Statement statement, StringLiteral stringLiteral) {
		stringLiteral.setDataType(JdbcType.VARCHAR);
	}
	
	private void setValueExpressionDataType(InsertStatement insertStatement, NameChain nameChain) {
		if (nameChain.size() != 1) {
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Column name cannot be prefixed.");
		}
		NameChain insertionTarget = insertStatement.getInsertionTarget();
		List<Name> list = new ArrayList<Name>(insertionTarget.size() + 1);
		for (int i = 0; i < insertionTarget.size(); i++) {
			Name name = insertionTarget.get(i);
			list.add(name);
		}
		for (int i = 0; i < nameChain.size(); i++) {
			Name name = nameChain.get(i);
			list.add(name);
		}
		NameChain columnName = new NameChain(list);
		ColumnMetadata columnMetadata = configuration.getColumnMetadata(columnName);
		int columnType = columnMetadata.getColumnType();
		JdbcType dataType = JdbcType.getJdbcType(columnType);
		JdbcTypeType dataTypeType = dataType.getJdbcTypeType();
		if (dataTypeType != JdbcTypeType.BOOLEAN &&
			dataTypeType != JdbcTypeType.BYTE_ARRAY &&
			dataTypeType != JdbcTypeType.DATE &&
			dataTypeType != JdbcTypeType.FLOAT &&
			dataTypeType != JdbcTypeType.INTEGER &&
			dataTypeType != JdbcTypeType.STRING &&
			dataTypeType != JdbcTypeType.TIME &&
			dataTypeType != JdbcTypeType.TIMESTAMP) {
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					dataType.getContent() + " is not supported.");
		}
	}
	
	private void setValueExpressionDataType(UpdateStatement updateStatement, NameChain nameChain) {
		if (nameChain.size() != 1) {
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Column name cannot be prefixed.");
		}
		NameChain targetTable = updateStatement.getTargetTable();
		List<Name> list = new ArrayList<Name>(targetTable.size() + 1);
		for (int i = 0; i < targetTable.size(); i++) {
			Name name = targetTable.get(i);
			list.add(name);
		}
		for (int i = 0; i < nameChain.size(); i++) {
			Name name = nameChain.get(i);
			list.add(name);
		}
		NameChain columnName = new NameChain(list);
		ColumnMetadata columnMetadata = configuration.getColumnMetadata(columnName);
		int columnType = columnMetadata.getColumnType();
		JdbcType dataType = JdbcType.getJdbcType(columnType);
		JdbcTypeType dataTypeType = dataType.getJdbcTypeType();
		if (dataTypeType != JdbcTypeType.BOOLEAN &&
			dataTypeType != JdbcTypeType.BYTE_ARRAY &&
			dataTypeType != JdbcTypeType.DATE &&
			dataTypeType != JdbcTypeType.FLOAT &&
			dataTypeType != JdbcTypeType.INTEGER &&
			dataTypeType != JdbcTypeType.STRING &&
			dataTypeType != JdbcTypeType.TIME &&
			dataTypeType != JdbcTypeType.TIMESTAMP) {
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					dataType.getContent() + " is not supported.");
		}
	}
	
	private void setValueExpressionDataType(DeleteStatement deleteStatement, NameChain nameChain) {
		if (nameChain.size() != 1) {
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					"Column name cannot be prefixed.");
		}
		NameChain targetTable = deleteStatement.getTargetTable();
		List<Name> list = new ArrayList<Name>(targetTable.size() + 1);
		for (int i = 0; i < targetTable.size(); i++) {
			Name name = targetTable.get(i);
			list.add(name);
		}
		for (int i = 0; i < nameChain.size(); i++) {
			Name name = nameChain.get(i);
			list.add(name);
		}
		NameChain columnName = new NameChain(list);
		ColumnMetadata columnMetadata = configuration.getColumnMetadata(columnName);
		int columnType = columnMetadata.getColumnType();
		JdbcType dataType = JdbcType.getJdbcType(columnType);
		JdbcTypeType dataTypeType = dataType.getJdbcTypeType();
		if (dataTypeType != JdbcTypeType.BOOLEAN &&
			dataTypeType != JdbcTypeType.BYTE_ARRAY &&
			dataTypeType != JdbcTypeType.DATE &&
			dataTypeType != JdbcTypeType.FLOAT &&
			dataTypeType != JdbcTypeType.INTEGER &&
			dataTypeType != JdbcTypeType.STRING &&
			dataTypeType != JdbcTypeType.TIME &&
			dataTypeType != JdbcTypeType.TIMESTAMP) {
			throw Sql4jException.getSql4jException(sourceCode, nameChain.getBeginIndex(), 
					dataType.getContent() + " is not supported.");
		}
	}
	
	private void setValueExpressionDataType(Statement statement, Subquery subquery) {
		SelectStatement selectStatement = subquery.getSelectStatement();
		setValueExpressionDataType(selectStatement);
		// TODO
	}
	
	private void setValueExpressionDataType(InsertStatement insertStatement, SelectStatement selectStatement) {
		setValueExpressionDataType(selectStatement);
	}
	
	private void setValueExpressionDataType(Statement statement, ToDate toDate) {
		ValueExpression valueExpression = toDate.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType dataType = valueExpression.getDataType();
		JdbcTypeType dataTypeType = dataType.getJdbcTypeType();
		if (dataTypeType != JdbcTypeType.STRING) {
			throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
					"Support only string type.");
		}
		toDate.setDataType(JdbcType.DATE);
	}
	
	private void setValueExpressionDataType(Statement statement, ToChar toChar) {
		ValueExpression valueExpression = toChar.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType dataType = valueExpression.getDataType();
		JdbcTypeType dataTypeType = dataType.getJdbcTypeType();
		if (dataTypeType != JdbcTypeType.DATE &&
			dataTypeType != JdbcTypeType.TIMESTAMP) {
			throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
					"Support only date or timestamp types.");
		}
		toChar.setDataType(JdbcType.VARCHAR);
	}
	
	private void setValueExpressionDataType(Statement statement, Substring substring) {
		ValueExpression valueExpression = substring.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType valueExpressionDataType = valueExpression.getDataType();
		JdbcTypeType valueExpressionDataTypeType = valueExpressionDataType.getJdbcTypeType();
		if (valueExpressionDataTypeType != JdbcTypeType.STRING) {
			throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
					"Support only string type.");
		}
		ValueExpression startPosition = substring.getStartPosition();
		setValueExpressionDataType(statement, startPosition);
		JdbcType startPositionDataType = startPosition.getDataType();
		if (startPositionDataType != JdbcType.NUMERIC) {
			throw Sql4jException.getSql4jException(sourceCode, startPosition.getBeginIndex(), 
					"Support only numeric type.");
		}
		ValueExpression stringLength = substring.getStringLength();
		setValueExpressionDataType(statement, stringLength);
		JdbcType stringLengthDataType = stringLength.getDataType();
		if (stringLengthDataType != JdbcType.NUMERIC) {
			throw Sql4jException.getSql4jException(sourceCode, stringLength.getBeginIndex(), 
					"Support only numeric type.");
		}
		substring.setDataType(JdbcType.VARCHAR);
	}
	
	private void setValueExpressionDataType(Statement statement, Subtraction subtraction) {
		ValueExpression left = subtraction.getLeft();
		setValueExpressionDataType(statement, left);
		JdbcType leftDataType = left.getDataType();
		JdbcTypeType leftDataTypeType = leftDataType.getJdbcTypeType();
		if (leftDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			leftDataTypeType == JdbcTypeType.BYTE_ARRAY) {
			throw Sql4jException.getSql4jException(sourceCode, left.getBeginIndex(), 
						leftDataType.getContent() + " is not supported.");
		}
		ValueExpression right = subtraction.getRight();
		setValueExpressionDataType(statement, right);
		JdbcType rightDataType = left.getDataType();
		JdbcTypeType rightDataTypeType = rightDataType.getJdbcTypeType();
		if (rightDataTypeType == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE ||
			rightDataTypeType == JdbcTypeType.BYTE_ARRAY) {
			throw Sql4jException.getSql4jException(sourceCode, right.getBeginIndex(), 
					rightDataType.getContent() + " is not supported.");
		}
		subtraction.setDataType(JdbcType.NUMERIC);
	}
	
	private void setValueExpressionDataType(Statement statement, Sum sum) {
		ValueExpression valueExpression = sum.getValueExpression();
		setValueExpressionDataType(statement, valueExpression);
		JdbcType dataType = valueExpression.getDataType();
		JdbcTypeType dataTypeType = dataType.getJdbcTypeType();
		if (dataTypeType != JdbcTypeType.FLOAT &&
			dataTypeType != JdbcTypeType.INTEGER) {
			throw Sql4jException.getSql4jException(sourceCode, valueExpression.getBeginIndex(), 
					dataType.getContent() + " is not supported.");
		}
		sum.setDataType(JdbcType.NUMERIC);
	}
	
	private void setValueExpressionDataType(Statement statement, Trim trim) {
		ValueExpression trimCharacter = trim.getTrimCharacter();
		if (trimCharacter != null) {
			setValueExpressionDataType(statement, trimCharacter);
		}
		ValueExpression trimSource = trim.getTrimSource();
		setValueExpressionDataType(statement, trimSource);
		trim.setDataType(JdbcType.VARCHAR);
	}
	
	private void setValueExpressionDataType(Statement statement, Upper upper) {
		ValueExpression argument = upper.getValueExpression();
		setValueExpressionDataType(statement, argument);
		JdbcType dataType = argument.getDataType();
		JdbcTypeType dataTypeType = dataType.getJdbcTypeType();
		if (dataTypeType != JdbcTypeType.STRING) {
			throw Sql4jException.getSql4jException(sourceCode, argument.getBeginIndex(), 
					dataType.getContent() + " is not supported.");
		}
		upper.setDataType(JdbcType.VARCHAR);
	}
	
	private void setValueExpressionDataType(Union union) {
		SelectStatement left = union.getLeft();
		setValueExpressionDataType(left);
		SelectStatement right = union.getRight();
		setValueExpressionDataType(right);
	}
	
	private void setValueExpressionDataType(UpdateStatement updateStatement) {
		List<SetClause> setClauseList = updateStatement.getSetClauseList();
		for (int i = 0; i < setClauseList.size(); i++) {
			SetClause setClause = setClauseList.get(i);
			Name updateTarget = setClause.getUpdateTarget();
			setValueExpressionDataType(updateStatement, updateTarget);
			ValueExpression updateSource = setClause.getUpdateSource();
			setValueExpressionDataType(updateStatement, updateSource);
		}
		BooleanValueExpression searchCondition = updateStatement.getSearchCondition();
		if (searchCondition != null) {
			setValueExpressionDataType(updateStatement, searchCondition);
		}
	}
	
	private void setValueExpressionDataType(UpdateStatement updateStatement, Name name) {
		NameChain targetTable = updateStatement.getTargetTable();
		List<Name> list = new ArrayList<Name>(targetTable.size() + 1);
		for (int i = 0; i < targetTable.size(); i++) {
			Name _name = targetTable.get(i);
			list.add(_name);
		}
		list.add(name);
		NameChain columnName = new NameChain(list);
		ColumnMetadata columnMetadata = configuration.getColumnMetadata(columnName);
		int columnType = columnMetadata.getColumnType();
		JdbcType dataType = JdbcType.getJdbcType(columnType);
		if (dataType.getJdbcTypeType() == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE) {
			throw Sql4jException.getSql4jException(sourceCode, name.getBeginIndex(), 
					dataType.getContent() + " is not supported.");
		}
		name.setDataType(dataType);
	}
	
	private void setValueExpressionDataType(InsertStatement insertStatement) {
		NameChain insertionTarget = insertStatement.getInsertionTarget();
		List<Name> list = new ArrayList<Name>(insertionTarget.size() + 1);
		List<Name> insertColumnList = insertStatement.getInsertColumnList();
		for (int i = 0; i < insertColumnList.size(); i++) {
			Name insertColumn = insertColumnList.get(i);
			list.clear();
			for (int j = 0; j < insertionTarget.size(); j++) {
				Name name = insertionTarget.get(j);
				list.add(name);
			}
			list.add(insertColumn);
			NameChain columnName = new NameChain(list);
			ColumnMetadata columnMetadata = configuration.getColumnMetadata(columnName);
			int columnType = columnMetadata.getColumnType();
			JdbcType dataType = JdbcType.getJdbcType(columnType);
			if (dataType.getJdbcTypeType() == JdbcTypeType.NOT_SUPPORTED_JDBC_TYPE) {
				throw Sql4jException.getSql4jException(sourceCode, insertColumn.getBeginIndex(), 
						dataType.getContent() + " is not supported.");
			}
			insertColumn.setDataType(dataType);
		}
		SelectStatement subquery = insertStatement.getSelectStatement();
		if (subquery != null) {
			setValueExpressionDataType(insertStatement, subquery);
			return;
		}
		List<ValueExpression> valueExpressionList = insertStatement.getValueExpressionList();
		for (int i = 0; i < valueExpressionList.size(); i++) {
			ValueExpression valueExpression = valueExpressionList.get(i);
			setValueExpressionDataType(insertStatement, valueExpression);
		}
	}
	
	private void setValueExpressionDataType(DeleteStatement deleteStatement) {
		BooleanValueExpression searchCondition = deleteStatement.getSearchCondition();
		setValueExpressionDataType(deleteStatement, searchCondition);
	}
	
	private void setValueExpressionDataType(CallStatement callStatement) {
	}

	private void analyze(SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = 
					(QuerySpecification) selectStatement;
			analyze(querySpecification);
			return;
		}
		if (selectStatement instanceof Union) {
			Union union = (Union) selectStatement;
			analyze(union);
			return;
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

	private void analyze(Union union) {
		List<NameChain> correspondingColumnList = 
				union.getCorrespondingColumnList();
		if (correspondingColumnList != null) {
			throw Sql4jException.getSql4jException(sourceCode, union.getBeginIndex(), 
					"Corresponding spec is not supported.");
		}
		SelectStatement left = union.getLeft();
		analyze(left);
		SelectStatement right = union.getRight();
		analyze(right);
	}

	private void analyze(QuerySpecification querySpecification) {
		List<SelectSublist> selectList = querySpecification.getSelectList();
		List<IndexableMessage> msgList = checkSelectList(selectList);
		if (msgList != null && !msgList.isEmpty()) {
			throw new Sql4jException(sourceCode, msgList);
		}
	}

	private void analyze(UpdateStatement updateStatement) {
		// TODO
	}

	private void analyze(InsertStatement insertStatement) {
		// TODO
	}

	private void analyze(DeleteStatement deleteStatement) {
		// TODO
	}

	private void analyze(CallStatement callStatement) {
		// TODO
	}
	
	private List<IndexableMessage> checkSelectList(List<SelectSublist> selectList) {
		Map<String, SelectSublist> map = 
				new HashMap<String, SelectSublist>(selectList.size());
		List<IndexableMessage> list = new ArrayList<IndexableMessage>();
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			ValueExpression valueExpression = 
					selectSublist.getValueExpression();
			Name name = selectSublist.getName();
			if (name != null) {
				String _name = name.getContent().toLowerCase();
				if (map.containsKey(_name)) {
					IndexableMessage msg = new IndexableMessage(
							name.getBeginIndex(), "Duplicate column name.");
					list.add(msg);
				} else {
					map.put(_name, selectSublist);
				}
				continue;
			}
			if (valueExpression instanceof NameChain) {
				NameChain nameChain = (NameChain) valueExpression;
				String _name = nameChain.get(nameChain.size() - 1).getContent();
				if (map.containsKey(_name)) {
					IndexableMessage msg = new IndexableMessage(
							nameChain.get(nameChain.size() - 1).getBeginIndex(), 
							"Duplicate column name.");
					list.add(msg);
				} else {
					map.put(_name, selectSublist);
				}
				continue;
			}
			IndexableMessage msg = new IndexableMessage(
					valueExpression.getBeginIndex(), "The value expression lack of name.");
			list.add(msg);
		}
		return list;
	}

	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	
	private void settingParentStatementsForSubqueries(Statement statement) {
		if (statement instanceof SelectStatement) {
			SelectStatement selectStatement = (SelectStatement) statement;
			settingParentStatementsForSubqueries(selectStatement);
			return;
		}
		if (statement instanceof UpdateStatement) {
			UpdateStatement updateStatement = (UpdateStatement) statement;
			settingParentStatementsForSubqueries(updateStatement);
			return;
		}
		if (statement instanceof InsertStatement) {
			InsertStatement insertStatement = (InsertStatement) statement;
			settingParentStatementsForSubqueries(insertStatement);
			return;
		}
		if (statement instanceof DeleteStatement) {
			DeleteStatement deleteStatement = (DeleteStatement) statement;
			settingParentStatementsForSubqueries(deleteStatement);
			return;
		}
		if (statement instanceof CallStatement) {
			CallStatement callStatement = (CallStatement) statement;
			settingParentStatementsForSubqueries(callStatement);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, statement.getBeginIndex(), 
				"This statement is not supported.");
	}
	
	private void settingParentStatementsForSubqueries(SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = 
					(QuerySpecification) selectStatement;
			settingParentStatementsForSubqueries(querySpecification);
			return;
		}
		if (selectStatement instanceof Union) {
			Union union = (Union) selectStatement;
			settingParentStatementsForSubqueries(union);
			return;
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
	
	private void settingParentStatementsForSubqueries(QuerySpecification querySpecification) {
		List<SelectSublist> selectList = querySpecification.getSelectList();
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			ValueExpression valueExpression = selectSublist.getValueExpression();
			settingParentStatementsForSubqueries(querySpecification, valueExpression);
		}
		List<TableReference> tableReferenceList = querySpecification.getTableReferenceList();
		settingParentStatementsForSubqueriesTableReferenceList(querySpecification, tableReferenceList);
		BooleanValueExpression whereSearchCondition = querySpecification.getWhereSearchCondition();
		if (whereSearchCondition != null) {
			settingParentStatementsForSubqueries(querySpecification, whereSearchCondition);
		}
		List<GroupingElement> groupingElementList = querySpecification.getGroupingElementList();
		if (groupingElementList != null) {
			settingParentStatementsForSubqueries(querySpecification, groupingElementList);
		}
		BooleanValueExpression havingSearchCondition = querySpecification.getHavingSearchCondition();
		if (havingSearchCondition != null) {
			settingParentStatementsForSubqueries(querySpecification, havingSearchCondition);
		}
		List<SortSpecification> sortSpecificationList = querySpecification.getSortSpecificationList();
		if (sortSpecificationList != null) {
			settingParentStatementsForSubqueriesSortSpecificationList(querySpecification, sortSpecificationList);
		}
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, 
			ValueExpression valueExpression) {
		if (valueExpression instanceof NameChain) {
			NameChain nameChain = (NameChain) valueExpression;
			settingParentStatementsForSubqueries(statement, nameChain);
			return;
		}
		if (valueExpression instanceof BooleanValueExpression) {
			BooleanValueExpression booleanValueExpression = 
					(BooleanValueExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, booleanValueExpression);
			return;
		}
		if (valueExpression instanceof AbsoluteValueExpression) {
			AbsoluteValueExpression absoluteValueExpression = 
					(AbsoluteValueExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, absoluteValueExpression);
			return;
		}
		if (valueExpression instanceof Addition) {
			Addition addition = (Addition) valueExpression;
			settingParentStatementsForSubqueries(statement, addition);
			return;
		}
		if (valueExpression instanceof Any) {
			Any any = (Any) valueExpression;
			settingParentStatementsForSubqueries(statement, any);
			return;
		}
		if (valueExpression instanceof Avg) {
			Avg avg = (Avg) valueExpression;
			settingParentStatementsForSubqueries(statement, avg);
			return;
		}
		if (valueExpression instanceof BitLengthExpression) {
			BitLengthExpression bitLengthExpression = 
					(BitLengthExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, bitLengthExpression);
			return;
		}
		if (valueExpression instanceof CardinalityExpression) {
			CardinalityExpression cardinalityExpression = 
					(CardinalityExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, cardinalityExpression);
			return;
		}
		if (valueExpression instanceof CharLengthExpression) {
			CharLengthExpression charLengthExpression = 
					(CharLengthExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, charLengthExpression);
			return;
		}
		if (valueExpression instanceof Coalesce) {
			Coalesce coalesce = (Coalesce) valueExpression;
			settingParentStatementsForSubqueries(statement, coalesce);
			return;
		}
		if (valueExpression instanceof Concatenation) {
			Concatenation concatenation = (Concatenation) valueExpression;
			settingParentStatementsForSubqueries(statement, concatenation);
			return;
		}
		if (valueExpression instanceof Count) {
			Count count = (Count) valueExpression;
			settingParentStatementsForSubqueries(statement, count);
			return;
		}
		if (valueExpression instanceof CurrentDate) {
			CurrentDate currentDate = (CurrentDate) valueExpression;
			settingParentStatementsForSubqueries(statement, currentDate);
			return;
		}
		if (valueExpression instanceof CurrentTime) {
			CurrentTime currentTime = (CurrentTime) valueExpression;
			settingParentStatementsForSubqueries(statement, currentTime);
			return;
		}
		if (valueExpression instanceof CurrentTimestamp) {
			CurrentTimestamp currentTimestamp = (CurrentTimestamp) valueExpression;
			settingParentStatementsForSubqueries(statement, currentTimestamp);
			return;
		}
		if (valueExpression instanceof DateLiteral) {
			DateLiteral dateLiteral = (DateLiteral) valueExpression;
			settingParentStatementsForSubqueries(statement, dateLiteral);
			return;
		}
		if (valueExpression instanceof Division) {
			Division division = (Division) valueExpression;
			settingParentStatementsForSubqueries(statement, division);
			return;
		}
		if (valueExpression instanceof Every) {
			Every every = (Every) valueExpression;
			settingParentStatementsForSubqueries(statement, every);
			return;
		}
		if (valueExpression instanceof ExtractExpression) {
			ExtractExpression extractExpression = 
					(ExtractExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, extractExpression);
			return;
		}
		if (valueExpression instanceof FunctionInvocation) {
			FunctionInvocation functionInvocation = 
					(FunctionInvocation) valueExpression;
			settingParentStatementsForSubqueries(statement, functionInvocation);
			return;
		}
		if (valueExpression instanceof Grouping) {
			Grouping grouping = (Grouping) valueExpression;
			settingParentStatementsForSubqueries(statement, grouping);
			return;
		}
		if (valueExpression instanceof Lower) {
			Lower lower = (Lower) valueExpression;
			settingParentStatementsForSubqueries(statement, lower);
			return;
		}
		if (valueExpression instanceof Max) {
			Max max = (Max) valueExpression;
			settingParentStatementsForSubqueries(statement, max);
			return;
		}
		if (valueExpression instanceof Min) {
			Min min = (Min) valueExpression;
			settingParentStatementsForSubqueries(statement, min);
			return;
		}
		if (valueExpression instanceof ModulusExpression) {
			ModulusExpression modulusExpression = 
					(ModulusExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, modulusExpression);
			return;
		}
		if (valueExpression instanceof Multiplication) {
			Multiplication multiplication = (Multiplication) valueExpression;
			settingParentStatementsForSubqueries(statement, multiplication);
			return;
		}
		if (valueExpression instanceof NegativeExpression) {
			NegativeExpression negativeExpression = 
					(NegativeExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, negativeExpression);
			return;
		}
		if (valueExpression instanceof NullIf) {
			NullIf nullIf = (NullIf) valueExpression;
			settingParentStatementsForSubqueries(statement, nullIf);
			return;
		}
		if (valueExpression instanceof NumericLiteral) {
			NumericLiteral numericLiteral = 
					(NumericLiteral) valueExpression;
			settingParentStatementsForSubqueries(statement, numericLiteral);
			return;
		}
		if (valueExpression instanceof OctetLengthExpression) {
			OctetLengthExpression octetLengthExpression = 
					(OctetLengthExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, octetLengthExpression);
			return;
		}
		if (valueExpression instanceof Parameter) {
			Parameter parameter = (Parameter) valueExpression;
			settingParentStatementsForSubqueries(statement, parameter);
			return;
		}
		if (valueExpression instanceof PositionExpression) {
			PositionExpression positionExpression = 
					(PositionExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, positionExpression);
			return;
		}
		if (valueExpression instanceof PositiveExpression) {
			PositiveExpression positiveExpression = 
					(PositiveExpression) valueExpression;
			settingParentStatementsForSubqueries(statement, positiveExpression);
			return;
		}
		if (valueExpression instanceof SearchedCase) {
			SearchedCase searchedCase = (SearchedCase) valueExpression;
			settingParentStatementsForSubqueries(statement, searchedCase);
			return;
		}
		if (valueExpression instanceof SimpleCase) {
			SimpleCase simpleCase = (SimpleCase) valueExpression;
			settingParentStatementsForSubqueries(statement, simpleCase);
			return;
		}
		if (valueExpression instanceof Some) {
			Some some = (Some) valueExpression;
			settingParentStatementsForSubqueries(statement, some);
			return;
		}
		if (valueExpression instanceof StringLiteral) {
			StringLiteral stringLiteral = 
					(StringLiteral) valueExpression;
			settingParentStatementsForSubqueries(statement, stringLiteral);
			return;
		}
		if (valueExpression instanceof Subquery) {
			Subquery subquery = (Subquery) valueExpression;
			settingParentStatementsForSubqueries(statement, subquery);
			return;
		}
		if (valueExpression instanceof ToDate) {
			ToDate toDate = (ToDate) valueExpression;
			settingParentStatementsForSubqueries(statement, toDate);
			return;
		}
		if (valueExpression instanceof ToChar) {
			ToChar toChar = (ToChar) valueExpression;
			settingParentStatementsForSubqueries(statement, toChar);
			return;
		}
		if (valueExpression instanceof Substring) {
			Substring substring = (Substring) valueExpression;
			settingParentStatementsForSubqueries(statement, substring);
			return;
		}
		if (valueExpression instanceof Subtraction) {
			Subtraction subtraction = (Subtraction) valueExpression;
			settingParentStatementsForSubqueries(statement, subtraction);
			return;
		}
		if (valueExpression instanceof Sum) {
			Sum sum = (Sum) valueExpression;
			settingParentStatementsForSubqueries(statement, sum);
			return;
		}
		if (valueExpression instanceof TimeLiteral) {
			TimeLiteral timeLiteral = (TimeLiteral) valueExpression;
			settingParentStatementsForSubqueries(statement, timeLiteral);
			return;
		}
		if (valueExpression instanceof TimestampLiteral) {
			TimestampLiteral timestampLiteral = 
					(TimestampLiteral) valueExpression;
			settingParentStatementsForSubqueries(statement, timestampLiteral);
			return;
		}
		if (valueExpression instanceof Trim) {
			Trim trim = (Trim) valueExpression;
			settingParentStatementsForSubqueries(statement, trim);
			return;
		}
		if (valueExpression instanceof Upper) {
			Upper upper = (Upper) valueExpression;
			settingParentStatementsForSubqueries(statement, upper);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				valueExpression.getBeginIndex(), 
				"The value exrepssion is not supported.");
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, NameChain nameChain) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, AbsoluteValueExpression absoluteValueExpression) {
		ValueExpression valueExpression = absoluteValueExpression.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Addition addition) {
		ValueExpression left = addition.getLeft();
		settingParentStatementsForSubqueries(statement, left);
		ValueExpression right = addition.getRight();
		settingParentStatementsForSubqueries(statement, right);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Any any) {
		ValueExpression valueExpression = any.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Avg avg) {
		ValueExpression valueExpression = avg.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, BitLengthExpression bitLengthExpression) {
		ValueExpression valueExpression = bitLengthExpression.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, CardinalityExpression cardinalityExpression) {
		ValueExpression valueExpression = cardinalityExpression.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, CharLengthExpression charLengthExpression) {
		ValueExpression valueExpression = charLengthExpression.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Coalesce coalesce) {
		List<ValueExpression> arguments = coalesce.getArguments();
		for (int i = 0; i < arguments.size(); i++) {
			ValueExpression argument = arguments.get(i);
			settingParentStatementsForSubqueries(statement, argument);
		}
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Concatenation concatenation) {
		ValueExpression left = concatenation.getLeft();
		settingParentStatementsForSubqueries(statement, left);
		ValueExpression right = concatenation.getRight();
		settingParentStatementsForSubqueries(statement, right);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Count count) {
		ValueExpression valueExpression = count.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, CurrentDate currentDate) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, CurrentTime currentTime) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, CurrentTimestamp currentTimestamp) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, DateLiteral dateLiteral) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Division division) {
		ValueExpression left = division.getLeft();
		settingParentStatementsForSubqueries(statement, left);
		ValueExpression right = division.getRight();
		settingParentStatementsForSubqueries(statement, right);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Every every) {
		ValueExpression valueExpression = every.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, ExtractExpression extractExpression) {
		ValueExpression extractSource = extractExpression.getExtractSource();
		settingParentStatementsForSubqueries(statement, extractSource);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, FunctionInvocation functionInvocation) {
		List<ValueExpression> arguments = functionInvocation.getArguments();
		for (int i = 0; i < arguments.size(); i++) {
			ValueExpression argument = arguments.get(i);
			settingParentStatementsForSubqueries(statement, argument);
		}
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Grouping grouping) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Lower lower) {
		ValueExpression argument = lower.getValueExpression();
		settingParentStatementsForSubqueries(statement, argument);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Max max) {
		ValueExpression valueExpression = max.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Min min) {
		ValueExpression valueExpression = min.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, ModulusExpression modulusExpression) {
		ValueExpression dividend = modulusExpression.getDividend();
		settingParentStatementsForSubqueries(statement, dividend);
		ValueExpression divisor = modulusExpression.getDivisor();
		settingParentStatementsForSubqueries(statement, divisor);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Multiplication multiplication) {
		ValueExpression left = multiplication.getLeft();
		settingParentStatementsForSubqueries(statement, left);
		ValueExpression right = multiplication.getRight();
		settingParentStatementsForSubqueries(statement, right);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, NegativeExpression negativeExpression) {
		ValueExpression valueExpression = negativeExpression.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, NullIf nullIf) {
		ValueExpression first = nullIf.getFirst();
		settingParentStatementsForSubqueries(statement, first);
		ValueExpression second = nullIf.getSecond();
		settingParentStatementsForSubqueries(statement, second);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, NumericLiteral numericLiteral) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, OctetLengthExpression octetLengthExpression) {
		ValueExpression valueExpression = octetLengthExpression.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Parameter parameter) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, PositionExpression positionExpression) {
		ValueExpression valueExpression1 = positionExpression.getValueExpression1();
		settingParentStatementsForSubqueries(statement, valueExpression1);
		ValueExpression valueExpression2 = positionExpression.getValueExpression2();
		settingParentStatementsForSubqueries(statement, valueExpression2);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, PositiveExpression positiveExpression) {
		ValueExpression valueExpression = positiveExpression.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, SearchedCase searchedCase) {
		List<SearchedWhenClause> searchedWhenClauseList = searchedCase.getSearchedWhenClauseList();
		for (int i = 0; i < searchedWhenClauseList.size(); i++) {
			SearchedWhenClause searchedWhenClause  = searchedWhenClauseList.get(i);
			BooleanValueExpression searchedCondition = searchedWhenClause.getSearchedCondition();
			settingParentStatementsForSubqueries(statement, searchedCondition);
			ValueExpression result = searchedWhenClause.getResult();
			settingParentStatementsForSubqueries(statement, result);
		}
		ElseClause elseClause = searchedCase.getElseClause();
		if (elseClause != null) {
			ValueExpression result = elseClause.getResult();
			settingParentStatementsForSubqueries(statement, result);
		}
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, SimpleCase simpleCase) {
		ValueExpression caseOperand = simpleCase.getCaseOperand();
		settingParentStatementsForSubqueries(statement, caseOperand);
		List<SimpleWhenClause> simpleWhenClauseList = simpleCase.getSimpleWhenClauseList();
		for (int i = 0; i < simpleWhenClauseList.size(); i++) {
			SimpleWhenClause simpleWhenClause = simpleWhenClauseList.get(i);
			ValueExpression whenOperand = simpleWhenClause.getWhenOperand();
			settingParentStatementsForSubqueries(statement, whenOperand);
			ValueExpression result = simpleWhenClause.getResult();
			settingParentStatementsForSubqueries(statement, result);
		}
		ElseClause elseClause = simpleCase.getElseClause();
		if (elseClause != null) {
			ValueExpression result = elseClause.getResult();
			settingParentStatementsForSubqueries(statement, result);
		}
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Some some) {
		ValueExpression valueExpression = some.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, StringLiteral stringLiteral) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Subquery subquery) {
		SelectStatement selectStatement = subquery.getSelectStatement();
		selectStatement.setParentStatement(statement);
		settingParentStatementsForSubqueries(selectStatement);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, ToDate toDate) {
		ValueExpression valueExpression = toDate.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, ToChar toChar) {
		ValueExpression valueExpression = toChar.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Substring substring) {
		ValueExpression valueExpression = substring.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
		ValueExpression startPosition = substring.getStartPosition();
		settingParentStatementsForSubqueries(statement, startPosition);
		ValueExpression stringLength = substring.getStringLength();
		settingParentStatementsForSubqueries(statement, stringLength);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Subtraction subtraction) {
		ValueExpression left = subtraction.getLeft();
		settingParentStatementsForSubqueries(statement, left);
		ValueExpression right = subtraction.getRight();
		settingParentStatementsForSubqueries(statement, right);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Sum sum) {
		ValueExpression valueExpression = sum.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, TimeLiteral timeLiteral) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, TimestampLiteral timestampLiteral) {
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Trim trim) {
		ValueExpression trimCharacter = trim.getTrimCharacter();
		if (trimCharacter != null) {
			settingParentStatementsForSubqueries(statement, trimCharacter);
		}
		ValueExpression trimSource = trim.getTrimSource();
		settingParentStatementsForSubqueries(statement, trimSource);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Upper upper) {
		ValueExpression argument = upper.getValueExpression();
		settingParentStatementsForSubqueries(statement, argument);
	}
	
	private void settingParentStatementsForSubqueriesTableReferenceList(QuerySpecification querySpecification, 
			List<TableReference> tableReferenceList) {
		for (int i = 0; i < tableReferenceList.size(); i++) {
			TableReference tableReference = tableReferenceList.get(i);
			settingParentStatementsForSubqueries(querySpecification, tableReference);
		}
	}
	
	private void settingParentStatementsForSubqueries(QuerySpecification querySpecification, 
			TableReference tableReference) {
		if (tableReference instanceof TablePrimary) {
			return;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = (LeftOuterJoin) tableReference;
			TableReference left = leftOuterJoin.getLeft();
			settingParentStatementsForSubqueries(querySpecification, left);
			TableReference right = leftOuterJoin.getRight();
			settingParentStatementsForSubqueries(querySpecification, right);
			BooleanValueExpression joinCondition = leftOuterJoin.getJoinCondition();
			settingParentStatementsForSubqueries(querySpecification, joinCondition);
			return;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) tableReference;
			SelectStatement subquery = derivedTable.getSelectStatement();
			//SelectStatement selectStatement = subquery.getSelectStatement();
			subquery.setParentStatement(querySpecification);
			settingParentStatementsForSubqueries(subquery);
			return;
		}
		if (tableReference instanceof CrossJoin) {
			CrossJoin crossJoin = (CrossJoin) tableReference;
			TableReference left = crossJoin.getLeft();
			settingParentStatementsForSubqueries(querySpecification, left);
			TableReference right = crossJoin.getRight();
			settingParentStatementsForSubqueries(querySpecification, right);
			return;
		}
		if (tableReference instanceof FullOuterJoin) {
			FullOuterJoin fullOuterJoin = (FullOuterJoin) tableReference;
			TableReference left = fullOuterJoin.getLeft();
			settingParentStatementsForSubqueries(querySpecification, left);
			TableReference right = fullOuterJoin.getRight();
			settingParentStatementsForSubqueries(querySpecification, right);
			BooleanValueExpression joinCondition = fullOuterJoin.getJoinCondition();
			settingParentStatementsForSubqueries(querySpecification, joinCondition);
			return;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = (InnerJoin) tableReference;
			TableReference left = innerJoin.getLeft();
			settingParentStatementsForSubqueries(querySpecification, left);
			TableReference right = innerJoin.getRight();
			settingParentStatementsForSubqueries(querySpecification, right);
			BooleanValueExpression joinCondition = innerJoin.getJoinCondition();
			settingParentStatementsForSubqueries(querySpecification, joinCondition);
			return;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = (RightOuterJoin) tableReference;
			TableReference left = rightOuterJoin.getLeft();
			settingParentStatementsForSubqueries(querySpecification, left);
			TableReference right = rightOuterJoin.getRight();
			settingParentStatementsForSubqueries(querySpecification, right);
			BooleanValueExpression joinCondition = rightOuterJoin.getJoinCondition();
			settingParentStatementsForSubqueries(querySpecification, joinCondition);
			return;
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			NaturalFullOuterJoin naturalFullOuterJoin = (NaturalFullOuterJoin) tableReference;
			TableReference left = naturalFullOuterJoin.getLeft();
			settingParentStatementsForSubqueries(querySpecification, left);
			TableReference right = naturalFullOuterJoin.getRight();
			settingParentStatementsForSubqueries(querySpecification, right);
			return;
		}
		if (tableReference instanceof NaturalInnerJoin) {
			NaturalInnerJoin naturalInnerJoin = (NaturalInnerJoin) tableReference;
			TableReference left = naturalInnerJoin.getLeft();
			settingParentStatementsForSubqueries(querySpecification, left);
			TableReference right = naturalInnerJoin.getRight();
			settingParentStatementsForSubqueries(querySpecification, right);
			return;
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			NaturalLeftOuterJoin naturalLeftOuterJoin = (NaturalLeftOuterJoin) tableReference;
			TableReference left = naturalLeftOuterJoin.getLeft();
			settingParentStatementsForSubqueries(querySpecification, left);
			TableReference right = naturalLeftOuterJoin.getRight();
			settingParentStatementsForSubqueries(querySpecification, right);
			return;
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			NaturalRightOuterJoin naturalRightOuterJoin = (NaturalRightOuterJoin) tableReference;
			TableReference left = naturalRightOuterJoin.getLeft();
			settingParentStatementsForSubqueries(querySpecification, left);
			TableReference right = naturalRightOuterJoin.getRight();
			settingParentStatementsForSubqueries(querySpecification, right);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, tableReference.getBeginIndex(), 
				"The table reference is not supported.");
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, 
			BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = 
					(BooleanValue) booleanValueExpression;
			settingParentStatementsForSubqueries(statement, booleanValue);
			return;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = 
					(Predicate) booleanValueExpression;
			settingParentStatementsForSubqueries(statement, predicate);
			return;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = 
					(BooleanFactor) booleanValueExpression;
			settingParentStatementsForSubqueries(statement, booleanFactor);
			return;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			settingParentStatementsForSubqueries(statement, booleanTerm);
			return;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			settingParentStatementsForSubqueries(statement, booleanTest);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				booleanValueExpression.getBeginIndex(), 
				"The boolean value exrepssion is not supported.");
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, BooleanValue booleanValue) {
		for (int i = 0; i < booleanValue.size(); i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			settingParentStatementsForSubqueries(statement, booleanTerm);
		}
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = 
					(ComparisonPredicate) predicate;
			settingParentStatementsForSubqueries(statement, comparisonPredicate);
			return;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = 
					(BetweenPredicate) predicate;
			settingParentStatementsForSubqueries(statement, betweenPredicate);
			return;
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = 
					(DistinctPredicate) predicate;
			settingParentStatementsForSubqueries(statement, distinctPredicate);
			return;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = 
					(ExistsPredicate) predicate;
			settingParentStatementsForSubqueries(statement, existsPredicate);
			return;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = 
					(InPredicate) predicate;
			settingParentStatementsForSubqueries(statement, inPredicate);
			return;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = 
					(LikePredicate) predicate;
			settingParentStatementsForSubqueries(statement, likePredicate);
			return;
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = 
					(MatchPredicate) predicate;
			settingParentStatementsForSubqueries(statement, matchPredicate);
			return;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = 
					(NullPredicate) predicate;
			settingParentStatementsForSubqueries(statement, nullPredicate);
			return;
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = 
					(OverlapsPredicate) predicate;
			settingParentStatementsForSubqueries(statement, overlapsPredicate);
			return;
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = 
					(SimilarPredicate) predicate;
			settingParentStatementsForSubqueries(statement, similarPredicate);
			return;
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = 
					(UniquePredicate) predicate;
			settingParentStatementsForSubqueries(statement, uniquePredicate);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"Not support the predicate.");
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		settingParentStatementsForSubqueries(statement, left);
		ValueExpression right = comparisonPredicate.getRight();
		settingParentStatementsForSubqueries(statement, right);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, BetweenPredicate betweenPredicate) {
		ValueExpression valueExpression = betweenPredicate.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
		ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
		settingParentStatementsForSubqueries(statement, valueExpression1);
		ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
		settingParentStatementsForSubqueries(statement, valueExpression2);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, DistinctPredicate distinctPredicate) {
		ValueExpression left = distinctPredicate.getLeft();
		settingParentStatementsForSubqueries(statement, left);
		ValueExpression right = distinctPredicate.getRight();
		settingParentStatementsForSubqueries(statement, right);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, ExistsPredicate existsPredicate) {
		Subquery subquery = existsPredicate.getSubquery();
		SelectStatement selectStatement = subquery.getSelectStatement();
		selectStatement.setParentStatement(statement);
		settingParentStatementsForSubqueries(selectStatement);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, InPredicate inPredicate) {
		ValueExpression valueExpression = inPredicate.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
		Subquery subquery = inPredicate.getSubquery();
		if (subquery != null) {
			SelectStatement selectStatement = subquery.getSelectStatement();
			selectStatement.setParentStatement(statement);
			settingParentStatementsForSubqueries(selectStatement);
		} else {
			List<ValueExpression> inValueList = inPredicate.getInValueList();
			for (int i = 0; i < inValueList.size(); i++) {
				ValueExpression inValue = inValueList.get(i);
				settingParentStatementsForSubqueries(statement, inValue);
			}
		}
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, LikePredicate likePredicate) {
		ValueExpression valueExpression = likePredicate.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
		ValueExpression characterPattern = likePredicate.getCharacterPattern();
		settingParentStatementsForSubqueries(statement, characterPattern);
		ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
		settingParentStatementsForSubqueries(statement, escapeCharacter);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, MatchPredicate matchPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, matchPredicate.getBeginIndex(), 
				"Not support match predicate.");
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, NullPredicate nullPredicate) {
		ValueExpression valueExpression = nullPredicate.getValueExpression();
		settingParentStatementsForSubqueries(statement, valueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, OverlapsPredicate overlapsPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, overlapsPredicate.getBeginIndex(), 
				"Not support overlaps predicate.");
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, SimilarPredicate similarPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, similarPredicate.getBeginIndex(), 
				"Not support similar predicate.");
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, UniquePredicate uniquePredicate) {
		throw Sql4jException.getSql4jException(sourceCode, uniquePredicate.getBeginIndex(), 
				"Not support unique predicate.");
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = booleanFactor.getBooleanValueExpression();
		settingParentStatementsForSubqueries(statement, booleanValueExpression);
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, BooleanTerm booleanTerm) {
		for (int i = 0; i < booleanTerm.size(); i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			settingParentStatementsForSubqueries(statement, booleanFactor);
		}
	}
	
	private void settingParentStatementsForSubqueries(Statement statement, BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = booleanTest.getBooleanValueExpression();
		settingParentStatementsForSubqueries(statement, booleanValueExpression);
	}
	
	private void settingParentStatementsForSubqueries(QuerySpecification querySpecification, 
			List<GroupingElement> groupingElementList) {
		for (int i = 0; i < groupingElementList.size(); i++) {
			GroupingElement groupingElement = groupingElementList.get(i);
			settingParentStatementsForSubqueries(querySpecification, groupingElement);
		}
	}
	
	private void settingParentStatementsForSubqueries(QuerySpecification querySpecification, 
			GroupingElement groupingElement) {
		if (groupingElement instanceof CubeList) {
			CubeList cubeList = (CubeList) groupingElement;
			settingParentStatementsForSubqueries(querySpecification, cubeList);
			return;
		}
		if (groupingElement instanceof GrandTotal) {
			GrandTotal grandTotal = (GrandTotal) groupingElement;
			settingParentStatementsForSubqueries(querySpecification, grandTotal);
			return;
		}
		if (groupingElement instanceof GroupingSetsSpecification) {
			GroupingSetsSpecification groupingSetsSpecification = 
					(GroupingSetsSpecification) groupingElement;
			settingParentStatementsForSubqueries(querySpecification, groupingSetsSpecification);
			return;
		}
		if (groupingElement instanceof OrdinaryGroupingSet) {
			OrdinaryGroupingSet ordinaryGroupingSet = (OrdinaryGroupingSet) groupingElement;
			settingParentStatementsForSubqueries(querySpecification, ordinaryGroupingSet);
			return;
		}
		if (groupingElement instanceof RollupList) {
			RollupList rollupList = (RollupList) groupingElement;
			settingParentStatementsForSubqueries(querySpecification, rollupList);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, groupingElement.getBeginIndex(), 
				"The grouping element is not supported.");
	}
	
	private void settingParentStatementsForSubqueries(QuerySpecification querySpecification, CubeList cubeList) {
	}
	
	private void settingParentStatementsForSubqueries(QuerySpecification querySpecification, GrandTotal grandTotal) {
		throw Sql4jException.getSql4jException(sourceCode, grandTotal.getBeginIndex(), 
				"Grand total is not supported.");
	}
	
	private void settingParentStatementsForSubqueries(QuerySpecification querySpecification, GroupingSetsSpecification groupingSetsSpecification) {
		for (int i = 0; i < groupingSetsSpecification.size(); i++) {
			GroupingElement groupingElement = groupingSetsSpecification.get(i);
			settingParentStatementsForSubqueries(querySpecification, groupingElement);
		}
	}
	
	private void settingParentStatementsForSubqueries(QuerySpecification querySpecification, OrdinaryGroupingSet ordinaryGroupingSet) {
	}
	
	private void settingParentStatementsForSubqueries(QuerySpecification querySpecification, RollupList rollupList) {
	}
	
	private void settingParentStatementsForSubqueriesSortSpecificationList(QuerySpecification querySpecification, 
			List<SortSpecification> sortSpecificationList) {
		for (int i = 0; i < sortSpecificationList.size(); i++) {
			SortSpecification sortSpecification = sortSpecificationList.get(i);
			settingParentStatementsForSubqueries(querySpecification, sortSpecification);
		}
	}
	
	private void settingParentStatementsForSubqueries(QuerySpecification querySpecification, 
			SortSpecification sortSpecification) {
		ValueExpression sortKey = sortSpecification.getSortKey();
		settingParentStatementsForSubqueries(querySpecification, sortKey);
	}
	
	private void settingParentStatementsForSubqueries(Union union) {
		SelectStatement left = union.getLeft();
		settingParentStatementsForSubqueries(left);
		SelectStatement right = union.getRight();
		settingParentStatementsForSubqueries(right);
	}
	
	private void settingParentStatementsForSubqueries(UpdateStatement updateStatement) {
		List<SetClause> setClauseList = updateStatement.getSetClauseList();
		for (int i = 0; i < setClauseList.size(); i++) {
			SetClause setClause = setClauseList.get(i);
			ValueExpression updateSource = setClause.getUpdateSource();
			settingParentStatementsForSubqueries(updateStatement, updateSource);
		}
		BooleanValueExpression searchCondition = updateStatement.getSearchCondition();
		if (searchCondition != null) {
			settingParentStatementsForSubqueries(updateStatement, searchCondition);
		}
	}
	
	private void settingParentStatementsForSubqueries(DeleteStatement deleteStatement) {
		BooleanValueExpression searchCondition = deleteStatement.getSearchCondition();
		if (searchCondition != null) {
			settingParentStatementsForSubqueries(deleteStatement, searchCondition);
		}
	}
	
	private void settingParentStatementsForSubqueries(InsertStatement insertStatement) {
		SelectStatement selectStatement = insertStatement.getSelectStatement();
		if (selectStatement != null) {
			selectStatement.setParentStatement(insertStatement);
			settingParentStatementsForSubqueries(selectStatement);
			return;
		}
		List<ValueExpression> list = insertStatement.getValueExpressionList();
		for (int i = 0; i < list.size(); i++) {
			ValueExpression valueExpression = list.get(i);
			settingParentStatementsForSubqueries(insertStatement, valueExpression);
		}
	}
	
	private void settingParentStatementsForSubqueries(CallStatement callStatement) {
	}

}
