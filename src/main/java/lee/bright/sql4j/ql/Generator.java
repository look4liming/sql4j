package lee.bright.sql4j.ql;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;
import lee.bright.sql4j.conf.NameValuePairs;
import lee.bright.sql4j.util.IdGenerator;

/**
 * @author Bright Lee
 */
public abstract class Generator {
	
	protected Configuration configuration;
	protected SourceCode sourceCode;

	private List<Statement> list;
	protected NameValuePairs argument;

	protected List<JdbcType> resultJdbcTypeList = new ArrayList<JdbcType>();
	protected List<Integer> parameterNumberList = new ArrayList<Integer>();
	protected List<Object> parameterValueList = new ArrayList<Object>();
	private List<Integer> resultNumberList = new ArrayList<Integer>();
	private List<String> resultNameList = new ArrayList<String>();
	
	private List<SemifinishedStatement> semifinishedStatements;
	
	private StringBuilder buf = new StringBuilder(2048);
	
	protected Generator(Configuration configuration, 
			List<Statement> list, Object object) {
		this.configuration = configuration;
		this.list = list;
		List<NameValuePairs> arguments;
		if (object == null || object instanceof Map) {
			NameValuePairs arg = configuration.newNameValuePairs(object);
			arguments = new ArrayList<NameValuePairs>(1);
			arguments.add(arg);
		} else if (object instanceof Collection) {
			Collection<?> collection = (Collection<?>) object;
			arguments = new ArrayList<NameValuePairs>(collection.size());
			for (Object obj : collection) {
				NameValuePairs arg = configuration.newNameValuePairs(obj);
				arguments.add(arg);
			}
		} else {
			NameValuePairs arg = configuration.newNameValuePairs(object);
			arguments = new ArrayList<NameValuePairs>(1);
			arguments.add(arg);
		}
		semifinishedStatements = new ArrayList<SemifinishedStatement>(
				arguments.size() * list.size());
		init(arguments);
	}
	
	public SemifinishedStatement generate() {
		if (semifinishedStatements.isEmpty()) {
			return null;
		}
		SemifinishedStatement semifinishedStatement = 
				semifinishedStatements.remove(0);
		return semifinishedStatement;
	}
	
	private void init(List<NameValuePairs> arguments) {
		for (NameValuePairs arg : arguments) {
			argument = arg;
			for (Statement statement : list) {
				sourceCode = statement.getSourceCode();
				clear();
				List<Integer> dataSourceIndexList = new ArrayList<Integer>();
				if (statement instanceof SelectStatement) {
					SelectStatement selectStatement = (SelectStatement) statement;
					selectStatement = erase(selectStatement);
					QuerySpecification querySpecification = (QuerySpecification) selectStatement;
					clear();
					generate(querySpecification, true);
					List<Integer> dataSourceIndexList2 = new ArrayList<Integer>(1);
					int dataSourceIndex;
					QuerySpecificationDecisiveEquation decisiveEquation = querySpecification.getDecisiveEquation();
					if (decisiveEquation != null) {
						NameChain decisiveHashColumn = decisiveEquation.getDecisiveHashColumn();
						ValueExpression hashColumnValue = decisiveEquation.getDecisiveHashValue();
						Object hashValue = getHashValue(decisiveHashColumn, hashColumnValue);
						dataSourceIndex = configuration.getDataSourceIndex(hashValue);
						dataSourceIndexList2.add(dataSourceIndex);
					}
					String sql = buf.toString();
					List<Integer> resultNumberList = new ArrayList<Integer>(
							this.resultNumberList.size());
					for (Integer obj : this.resultNumberList) {
						resultNumberList.add(obj);
					}
					List<String> resultNameList = new ArrayList<String>(
							this.resultNameList.size());
					for (String obj : this.resultNameList) {
						resultNameList.add(obj);
					}
					SemifinishedStatement semifinishedStatement = 
							new SemifinishedStatement(statement.getStatementType(), 
									sql, null, null, 
									resultNumberList, resultNameList, 
									null, dataSourceIndexList2);
					semifinishedStatements.add(semifinishedStatement);
					continue;
				} else if (statement instanceof UpdateStatement) {
					UpdateStatement updateStatement = (UpdateStatement) statement;
					updateStatement = erase(updateStatement);
					if (updateStatement == null) {
						continue;
					}
					generate(updateStatement);
					List<NameChain> hashColumnNameList = updateStatement.getHashColumnNameList();
					List<ValueExpression> hashColumnValueList = updateStatement.getHashColumnValueList();
					Map<Integer, Integer> map = new HashMap<Integer, Integer>();
					for (int i = 0; i < hashColumnValueList.size(); i++) {
						NameChain hashColumn = hashColumnNameList.get(i);
						ValueExpression hashColumnValue = hashColumnValueList.get(i);
						Object hashValue = getHashValue(hashColumn, hashColumnValue);
						int dataSourceIndex = configuration.getDataSourceIndex(hashValue);
						map.put(dataSourceIndex, dataSourceIndex);
					}
					dataSourceIndexList.addAll(map.keySet());
				} else if (statement instanceof InsertStatement) {
					InsertStatement insertStatement = (InsertStatement) statement;
					insertStatement = erase(insertStatement);
					List<NameChain> hashColumnNameList = insertStatement.getHashColumnNameList();
					List<ValueExpression> hashColumnValueList = insertStatement.getHashColumnValueList();
					Map<Integer, Integer> map = new HashMap<Integer, Integer>();
					for (int i = 0; i < hashColumnValueList.size(); i++) {
						NameChain hashColumn = hashColumnNameList.get(i);
						ValueExpression hashColumnValue = hashColumnValueList.get(i);
						Object hashValue = getHashValue(hashColumn, hashColumnValue);
						int dataSourceIndex = configuration.getDataSourceIndex(hashValue);
						map.put(dataSourceIndex, dataSourceIndex);
					}
					List<Integer> list = new ArrayList<Integer>(map.keySet());
					Collections.sort(list);
					if (list.isEmpty()) {
						int size = configuration.getDataSourceListSize();
						for (int i = 0; i < size; i++) {
							list.add(i);
						}
					}
					generate(insertStatement, true);
					String sql = buf.toString();
					SemifinishedStatement semifinishedStatement = 
							new SemifinishedStatement(statement.getStatementType(), 
									sql, null, null, null, null, null, list);
					semifinishedStatements.add(semifinishedStatement);
					continue;
				} else if (statement instanceof DeleteStatement) {
					DeleteStatement deleteStatement = (DeleteStatement) statement;
					generate(deleteStatement);
					List<NameChain> hashColumnList = deleteStatement.getHashColumnNameList();
					List<ValueExpression> hashColumnValueList = deleteStatement.getHashColumnValueList();
					Map<Integer, Integer> map = new HashMap<Integer, Integer>();
					for (int i = 0; i < hashColumnValueList.size(); i++) {
						NameChain hashColumn = hashColumnList.get(i);
						ValueExpression hashColumnValue = hashColumnValueList.get(i);
						Object hashValue = getHashValue(hashColumn, hashColumnValue);
						int dataSourceIndex = configuration.getDataSourceIndex(hashValue);
						map.put(dataSourceIndex, dataSourceIndex);
					}
					dataSourceIndexList.addAll(map.keySet());
				} else if (statement instanceof TableDefinition) {
					TableDefinition tableDefinition = 
							(TableDefinition) statement;
					generate(tableDefinition);
				} else if (statement instanceof DropTableStatement) {
					DropTableStatement dropTableStatement = 
							(DropTableStatement) statement;
					generate(dropTableStatement);
				} else if (statement instanceof DropIndexStatement) {
					DropIndexStatement dropIndexStatement = 
							(DropIndexStatement) statement;
					generate(dropIndexStatement);
				} else if (statement instanceof CreateIndexStatement) {
					CreateIndexStatement createIndexStatement = 
							(CreateIndexStatement) statement;
					generate(createIndexStatement);
				} else if (statement instanceof AddColumnDefinition) {
					AddColumnDefinition addColumnDefinition = 
							(AddColumnDefinition) statement;
					generate(addColumnDefinition);
				} else if (statement instanceof DropColumnDefinition) {
					DropColumnDefinition dropColumnDefinition = 
							(DropColumnDefinition) statement;
					generate(dropColumnDefinition);
				} else if (statement instanceof AlterColumnDefinition) {
					AlterColumnDefinition alterColumnDefinition = 
							(AlterColumnDefinition) statement;
					generate(alterColumnDefinition);
				} else if (statement instanceof AddTableConstraintDefinition) {
					AddTableConstraintDefinition addTableConstraintDefinition = 
							(AddTableConstraintDefinition) statement;
					generate(addTableConstraintDefinition);
				} else if (statement instanceof DropTableConstraintDefinition) {
					DropTableConstraintDefinition dropTableConstraintDefinition = 
							(DropTableConstraintDefinition) statement;
					generate(dropTableConstraintDefinition);
				} else if (statement instanceof DropPrimaryKeyDefinition) {
					DropPrimaryKeyDefinition dropPrimaryKeyDefinition = 
							(DropPrimaryKeyDefinition) statement;
					generate(dropPrimaryKeyDefinition);
				} else if (statement instanceof AddPrimaryKeyDefinition) {
					AddPrimaryKeyDefinition addPrimaryKeyDefinition = 
							(AddPrimaryKeyDefinition) statement;
					generate(addPrimaryKeyDefinition);
				} else if (statement instanceof ModifyColumnDefinition) {
					ModifyColumnDefinition modifyColumnDefinition = 
							(ModifyColumnDefinition) statement;
					generate(modifyColumnDefinition);
				} else if (statement instanceof CallStatement) {
					CallStatement callStatement = 
							(CallStatement) statement;
					generate(callStatement);
				} else {
					throw new Sql4jException(statement + " is not supported");
				}
				String sql = buf.toString();
				List<JdbcType> resultJdbcTypeList = new ArrayList<JdbcType>(
						this.resultJdbcTypeList.size());
				for (JdbcType jdbcType : this.resultJdbcTypeList) {
					resultJdbcTypeList.add(jdbcType);
				}
				List<Object> parameterValueList = new ArrayList<Object>(
						this.parameterValueList.size());
				for (Object obj : this.parameterValueList) {
					parameterValueList.add(obj);
				}
				List<Integer> parameterNumberList = new ArrayList<Integer>(
						this.parameterNumberList.size());
				for (Integer obj : this.parameterNumberList) {
					parameterNumberList.add(obj);
				}
				List<Integer> resultNumberList = new ArrayList<Integer>(
						this.resultNumberList.size());
				for (Integer obj : this.resultNumberList) {
					resultNumberList.add(obj);
				}
				List<String> resultNameList = new ArrayList<String>(
						this.resultNameList.size());
				for (String obj : this.resultNameList) {
					resultNameList.add(obj);
				}
				SemifinishedStatement semifinishedStatement = 
						new SemifinishedStatement(statement.getStatementType(), 
								sql, parameterNumberList, parameterValueList, 
								resultNumberList, resultNameList, 
								resultJdbcTypeList, dataSourceIndexList);
				semifinishedStatements.add(semifinishedStatement);
			}
		}
	}
	
	
	
	private SelectStatement erase(SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = (QuerySpecification) selectStatement;
			querySpecification = erase(querySpecification);
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
	
	private QuerySpecification erase(QuerySpecification querySpecification) {
		BooleanValueExpression whereSearchCondition = 
				querySpecification.getWhereSearchCondition();
		if (whereSearchCondition == null) {
			return querySpecification;
		}
		QuerySpecificationDecisiveEquation decisiveEquation = querySpecification.getDecisiveEquation();
		BooleanValueExpression whereSearchCondition2 = erase(decisiveEquation, whereSearchCondition);
		if (whereSearchCondition == whereSearchCondition2) {
			return querySpecification;
		}
		SourceCode sourceCode = querySpecification.getSourceCode();
		int beginIndex = querySpecification.getBeginIndex();
		SetQuantifier setQuantifier = querySpecification.getSetQuantifier();
		List<SelectSublist> selectList = querySpecification.getSelectList();
		List<TableReference> tableReferenceList = querySpecification.getTableReferenceList();
		List<GroupingElement> groupingElementList = querySpecification.getGroupingElementList();
		BooleanValueExpression havingSearchCondition = querySpecification.getHavingSearchCondition();
		List<SortSpecification> sortSpecificationList = querySpecification.getSortSpecificationList();
		Page page = querySpecification.getPage();
		QuerySpecification querySpecification2 = new QuerySpecification(sourceCode, 
				beginIndex,
				setQuantifier,
				selectList, 
				tableReferenceList,
				whereSearchCondition2,
				groupingElementList,
				havingSearchCondition,
				sortSpecificationList,
				page);
		querySpecification2.setDecisiveEquation(decisiveEquation);
		return querySpecification2;
	}
	
	private BooleanValueExpression erase(QuerySpecificationDecisiveEquation decisiveEquation, 
			BooleanValueExpression booleanValueExpression) {
		BooleanValue booleanValue = (BooleanValue) booleanValueExpression;
		boolean equal = true;
		List<BooleanTerm> list = new ArrayList<BooleanTerm>(booleanValue.size());
		for (int i = 0; i < booleanValue.size(); i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			BooleanTerm booleanTerm2 = erase(decisiveEquation, booleanTerm);
			equal = equal && (booleanTerm == booleanTerm2);
			if (booleanTerm2 == null) {
				continue;
			}
			list.add(booleanTerm2);
		}
		if (equal == true) {
			list.clear();
			return booleanValue;
		}
		if (list.isEmpty()) {
			return null;
		}
		int beginIndex = booleanValue.getBeginIndex();
		int endIndex = booleanValue.getEndIndex();
		BooleanValue booleanValue2 = new BooleanValue(
				beginIndex, endIndex, list);
		return booleanValue2;
	}
	
	private BooleanTerm erase(QuerySpecificationDecisiveEquation decisiveEquation, BooleanTerm booleanTerm) {
		boolean equal = true;
		List<BooleanFactor> list = new ArrayList<BooleanFactor>(booleanTerm.size());
		for (int i = 0; i < booleanTerm.size(); i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			BooleanFactor booleanFactor2 = erase(decisiveEquation, booleanFactor);
			equal = equal && (booleanFactor == booleanFactor2);
			if (booleanFactor2 == null) {
				continue;
			}
			list.add(booleanFactor2);
		}
		if (equal == true) {
			list.clear();
			return booleanTerm;
		}
		if (list.isEmpty()) {
			return null;
		}
		int beginIndex = booleanTerm.getBeginIndex();
		int endIndex = booleanTerm.getEndIndex();
		BooleanTerm booleanTerm2 = new BooleanTerm(
				beginIndex, endIndex, list);
		return booleanTerm2;
	}
	
	private BooleanFactor erase(QuerySpecificationDecisiveEquation decisiveEquation, BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = 
				booleanFactor.getBooleanValueExpression();
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			BooleanTest booleanTest2 = erase(decisiveEquation, booleanTest);
			if (booleanTest2 == null) {
				return null;
			}
			if (booleanTest == booleanTest2) {
				return booleanFactor;
			}
			int beginIndex = booleanFactor.getBeginIndex();
			int endIndex = booleanFactor.getEndIndex();
			boolean not = booleanFactor.getNot();
			BooleanFactor booleanFactor2 = new BooleanFactor(
					beginIndex, endIndex, not, booleanTest2);
			return booleanFactor2;
		}
		BooleanValueExpression booleanValueExpression2 = 
				erase(decisiveEquation, booleanValueExpression);
		if (booleanValueExpression2 == null) {
			return null;
		}
		if (booleanValueExpression == booleanValueExpression2) {
			return booleanFactor;
		}
		int beginIndex = booleanFactor.getBeginIndex();
		int endIndex = booleanFactor.getEndIndex();
		boolean not = booleanFactor.getNot();
		BooleanFactor booleanFactor2 = new BooleanFactor(
				beginIndex, endIndex, not, booleanValueExpression2);
		return booleanFactor2;
	}
	
	private BooleanTest erase(QuerySpecificationDecisiveEquation decisiveEquation, BooleanTest booleanTest) {
		int beginIndex = booleanTest.getBeginIndex();
		int endIndex = booleanTest.getEndIndex();
		BooleanValueExpression booleanValueExpression = 
				booleanTest.getBooleanValueExpression();
		boolean not = booleanTest.getNot();
		TruthValue truthValue = booleanTest.getTruthValue();
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = (Predicate) booleanValueExpression;
			Predicate predicate2 = erase(decisiveEquation, predicate);
			if (predicate2 == null) {
				return null;
			}
			if (predicate == predicate2) {
				return booleanTest;
			}
			BooleanTest booleanTest2 = new BooleanTest(beginIndex, endIndex, 
					predicate2, not, truthValue);
			return booleanTest2;
		}
		BooleanValueExpression booleanValueExpression2 = 
				erase(decisiveEquation, booleanValueExpression);
		if (booleanValueExpression2 == null) {
			return null;
		}
		if (booleanValueExpression == booleanValueExpression2) {
			return booleanTest;
		}
		BooleanTest booleanTest2 = new BooleanTest(beginIndex, endIndex, 
				booleanValueExpression2, not, truthValue);
		return booleanTest2;
	}
	
	private Predicate erase(QuerySpecificationDecisiveEquation decisiveEquation, Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = 
					(ComparisonPredicate) predicate;
			ComparisonPredicate comparisonPredicate2 = 
					erase(decisiveEquation, comparisonPredicate);
			return comparisonPredicate2;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = 
					(BetweenPredicate) predicate;
			BetweenPredicate betweenPredicate2 = 
					erase(betweenPredicate);
			return betweenPredicate2;
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = 
					(DistinctPredicate) predicate;
			DistinctPredicate distinctPredicate2 =
					erase(distinctPredicate);
			return distinctPredicate2;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = 
					(ExistsPredicate) predicate;
			ExistsPredicate existsPredicate2 =
					erase(existsPredicate);
			return existsPredicate2;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = 
					(InPredicate) predicate;
			InPredicate inPredicate2 =
					erase(inPredicate);
			return inPredicate2;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = 
					(LikePredicate) predicate;
			LikePredicate likePredicate2 =
					erase(likePredicate);
			return likePredicate2;
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = 
					(MatchPredicate) predicate;
			MatchPredicate matchPredicate2 =
					erase(matchPredicate);
			return matchPredicate2;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = 
					(NullPredicate) predicate;
			NullPredicate nullPredicate2 =
					erase(nullPredicate);
			return nullPredicate2;
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = 
					(OverlapsPredicate) predicate;
			OverlapsPredicate overlapsPredicate2 =
					erase(overlapsPredicate);
			return overlapsPredicate2;
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = 
					(SimilarPredicate) predicate;
			SimilarPredicate similarPredicate2 =
					erase(similarPredicate);
			return similarPredicate2;
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = 
					(UniquePredicate) predicate;
			UniquePredicate uniquePredicate2 =
					erase(uniquePredicate);
			return uniquePredicate2;
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"Not support the predicate.");
	}
	
	private ComparisonPredicate erase(QuerySpecificationDecisiveEquation decisiveEquation, ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		ValueExpression right = comparisonPredicate.getRight();
		if (decisiveEquation != null && comparisonPredicate.getCompOp() == CompOp.EQUALS) {
			NameChain column = null;
			Parameter parameter = null;
			if (left instanceof NameChain && right instanceof Parameter) {
				column = (NameChain) left;
				parameter = (Parameter) right;
			}
			if (right instanceof NameChain && left instanceof Parameter) {
				column = (NameChain) right;
				parameter = (Parameter) left;
			}
			if (column != null) {
				String columnName = ((NameChain) column.getFullyQualifiedName()).toLowerCaseString();
				NameChain decisiveHashColumn = decisiveEquation.getDecisiveHashColumn();
				String decisiveHashColumnName = ((NameChain) decisiveHashColumn.getFullyQualifiedName()).toLowerCaseString();
				if (columnName.equals(decisiveHashColumnName)) {
					if (right instanceof Parameter) {
						String parameterName = parameter.getContent();
						if (!argument.containsName(parameterName)) {
							throw Sql4jException.getSql4jException(sourceCode, parameter.getBeginIndex(), 
									"The decisive equation cannot be erased.");
						}
					}
				}
			}
		}
		if (isErasable(left)) {
			return null;
		}
		if (isErasable(right)) {
			return null;
		}
		return comparisonPredicate;
	}
	
	private BetweenPredicate erase(BetweenPredicate betweenPredicate) {
		ValueExpression valueExpression = betweenPredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return null;
		}
		ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
		if (isErasable(valueExpression1)) {
			return null;
		}
		ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
		if (isErasable(valueExpression2)) {
			return null;
		}
		return betweenPredicate;
	}
	
	private DistinctPredicate erase(DistinctPredicate distinctPredicate) {
		ValueExpression left = distinctPredicate.getLeft();
		if (isErasable(left)) {
			return null;
		}
		ValueExpression right = distinctPredicate.getRight();
		if (isErasable(right)) {
			return null;
		}
		return distinctPredicate;
	}

	private ExistsPredicate erase(ExistsPredicate existsPredicate) {
		return existsPredicate;
	}
	
	private InPredicate erase(InPredicate inPredicate) {
		ValueExpression valueExpression = inPredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return null;
		}
		Subquery subquery = inPredicate.getSubquery();
		if (subquery == null) {
			List<ValueExpression> list = inPredicate.getInValueList();
			for (int i = 0; i < list.size(); i++) {
				ValueExpression e = list.get(i);
				if (isErasable(e)) {
					return null;
				}
			}
		}
		return inPredicate;
	}
	
	private LikePredicate erase(LikePredicate likePredicate) {
		ValueExpression valueExpression = likePredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return null;
		}
		ValueExpression characterPattern = likePredicate.getCharacterPattern();
		if (isErasable(characterPattern)) {
			return null;
		}
		ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
		if (isErasable(escapeCharacter)) {
			return null;
		}
		return likePredicate;
	}
	
	private MatchPredicate erase(MatchPredicate matchPredicate) {
		ValueExpression valueExpression = matchPredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return null;
		}
		return matchPredicate;
	}
	
	private NullPredicate erase(NullPredicate nullPredicate) {
		ValueExpression valueExpression = nullPredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return null;
		}
		return nullPredicate;
	}
	
	private OverlapsPredicate erase(OverlapsPredicate overlapsPredicate) {
		ValueExpression left = overlapsPredicate.getLeft();
		if (isErasable(left)) {
			return null;
		}
		ValueExpression right = overlapsPredicate.getRight();
		if (isErasable(right)) {
			return null;
		}
		return overlapsPredicate;
	}
	
	private SimilarPredicate erase(SimilarPredicate similarPredicate) {
		ValueExpression valueExpression = similarPredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return null;
		}
		ValueExpression similarPattern = similarPredicate.getSimilarPattern();
		if (isErasable(similarPattern)) {
			return null;
		}
		ValueExpression escapeCharacter = similarPredicate.getEscapeCharacter();
		if (isErasable(escapeCharacter)) {
			return null;
		}
		return similarPredicate;
	}
	
	private UniquePredicate erase(UniquePredicate uniquePredicate) {
		return uniquePredicate;
	}
	
	private boolean isErasable(ValueExpression valueExpression) {
		if (valueExpression == null) {
			return false;
		}
		if (valueExpression instanceof BooleanValueExpression) {
			BooleanValueExpression booleanValueExpression = 
					(BooleanValueExpression) valueExpression;
			return isErasable(booleanValueExpression);
		}
		if (valueExpression instanceof AbsoluteValueExpression) {
			AbsoluteValueExpression absoluteValueExpression = 
					(AbsoluteValueExpression) valueExpression;
			return isErasable(absoluteValueExpression);
		}
		if (valueExpression instanceof Addition) {
			Addition addition = (Addition) valueExpression;
			return isErasable(addition);
		}
		if (valueExpression instanceof Any) {
			Any any = (Any) valueExpression;
			return isErasable(any);
		}
		if (valueExpression instanceof Avg) {
			Avg avg = (Avg) valueExpression;
			return isErasable(avg);
		}
		if (valueExpression instanceof BitLengthExpression) {
			BitLengthExpression bitLengthExpression = 
					(BitLengthExpression) valueExpression;
			return isErasable(bitLengthExpression);
		}
		if (valueExpression instanceof CardinalityExpression) {
			CardinalityExpression cardinalityExpression = 
					(CardinalityExpression) valueExpression;
			return isErasable(cardinalityExpression);
		}
		if (valueExpression instanceof CharLengthExpression) {
			CharLengthExpression charLengthExpression = 
					(CharLengthExpression) valueExpression;
			return isErasable(charLengthExpression);
		}
		if (valueExpression instanceof Coalesce) {
			Coalesce coalesce = (Coalesce) valueExpression;
			return isErasable(coalesce);
		}
		if (valueExpression instanceof Concatenation) {
			Concatenation concatenation = (Concatenation) valueExpression;
			return isErasable(concatenation);
		}
		if (valueExpression instanceof Count) {
			Count count = (Count) valueExpression;
			return isErasable(count);
		}
		if (valueExpression instanceof CurrentDate) {
			CurrentDate currentDate = (CurrentDate) valueExpression;
			return isErasable(currentDate);
		}
		if (valueExpression instanceof CurrentTime) {
			CurrentTime currentTime = (CurrentTime) valueExpression;
			return isErasable(currentTime);
		}
		if (valueExpression instanceof CurrentTimestamp) {
			CurrentTimestamp currentTimestamp = (CurrentTimestamp) valueExpression;
			return isErasable(currentTimestamp);
		}
		if (valueExpression instanceof DateLiteral) {
			DateLiteral dateLiteral = (DateLiteral) valueExpression;
			return isErasable(dateLiteral);
		}
		if (valueExpression instanceof Division) {
			Division division = (Division) valueExpression;
			return isErasable(division);
		}
		if (valueExpression instanceof Every) {
			Every every = (Every) valueExpression;
			return isErasable(every);
		}
		if (valueExpression instanceof ExtractExpression) {
			ExtractExpression extractExpression = 
					(ExtractExpression) valueExpression;
			return isErasable(extractExpression);
		}
		if (valueExpression instanceof FunctionInvocation) {
			FunctionInvocation functionInvocation = 
					(FunctionInvocation) valueExpression;
			return isErasable(functionInvocation);
		}
		if (valueExpression instanceof Grouping) {
			Grouping grouping = (Grouping) valueExpression;
			return isErasable(grouping);
		}
		if (valueExpression instanceof Lower) {
			Lower lower = (Lower) valueExpression;
			return isErasable(lower);
		}
		if (valueExpression instanceof Max) {
			Max max = (Max) valueExpression;
			return isErasable(max);
		}
		if (valueExpression instanceof Min) {
			Min min = (Min) valueExpression;
			return isErasable(min);
		}
		if (valueExpression instanceof ModulusExpression) {
			ModulusExpression modulusExpression = 
					(ModulusExpression) valueExpression;
			return isErasable(modulusExpression);
		}
		if (valueExpression instanceof Multiplication) {
			Multiplication multiplication = (Multiplication) valueExpression;
			return isErasable(multiplication);
		}
		if (valueExpression instanceof NameChain) {
			NameChain nameChain = (NameChain) valueExpression;
			return isErasable(nameChain);
		}
		if (valueExpression instanceof NegativeExpression) {
			NegativeExpression negativeExpression = 
					(NegativeExpression) valueExpression;
			return isErasable(negativeExpression);
		}
		if (valueExpression instanceof NullIf) {
			NullIf nullIf = (NullIf) valueExpression;
			return isErasable(nullIf);
		}
		if (valueExpression instanceof NumericLiteral) {
			NumericLiteral numericLiteral = 
					(NumericLiteral) valueExpression;
			return isErasable(numericLiteral);
		}
		if (valueExpression instanceof OctetLengthExpression) {
			OctetLengthExpression octetLengthExpression = 
					(OctetLengthExpression) valueExpression;
			return isErasable(octetLengthExpression);
		}
		if (valueExpression instanceof Parameter) {
			Parameter parameter = (Parameter) valueExpression;
			return isErasable(parameter);
		}
		if (valueExpression instanceof PositionExpression) {
			PositionExpression positionExpression = 
					(PositionExpression) valueExpression;
			return isErasable(positionExpression);
		}
		if (valueExpression instanceof PositiveExpression) {
			PositiveExpression positiveExpression = 
					(PositiveExpression) valueExpression;
			return isErasable(positiveExpression);
		}
		if (valueExpression instanceof SearchedCase) {
			SearchedCase searchedCase = (SearchedCase) valueExpression;
			return isErasable(searchedCase);
		}
		if (valueExpression instanceof SimpleCase) {
			SimpleCase simpleCase = (SimpleCase) valueExpression;
			return isErasable(simpleCase);
		}
		if (valueExpression instanceof Some) {
			Some some = (Some) valueExpression;
			return isErasable(some);
		}
		if (valueExpression instanceof StringLiteral) {
			StringLiteral stringLiteral = 
					(StringLiteral) valueExpression;
			return isErasable(stringLiteral);
		}
		if (valueExpression instanceof Subquery) {
			Subquery subquery = (Subquery) valueExpression;
			return isErasable(subquery);
		}
		if (valueExpression instanceof ToDate) {
			ToDate toDate = (ToDate) valueExpression;
			return isErasable(toDate);
		}
		if (valueExpression instanceof ToChar) {
			ToChar toChar = (ToChar) valueExpression;
			return isErasable(toChar);
		}
		if (valueExpression instanceof Substring) {
			Substring substring = (Substring) valueExpression;
			return isErasable(substring);
		}
		if (valueExpression instanceof Subtraction) {
			Subtraction subtraction = (Subtraction) valueExpression;
			return isErasable(subtraction);
		}
		if (valueExpression instanceof Sum) {
			Sum sum = (Sum) valueExpression;
			return isErasable(sum);
		}
		if (valueExpression instanceof TimeLiteral) {
			TimeLiteral timeLiteral = (TimeLiteral) valueExpression;
			return isErasable(timeLiteral);
		}
		if (valueExpression instanceof TimestampLiteral) {
			TimestampLiteral timestampLiteral = 
					(TimestampLiteral) valueExpression;
			return isErasable(timestampLiteral);
		}
		if (valueExpression instanceof Trim) {
			Trim trim = (Trim) valueExpression;
			return isErasable(trim);
		}
		if (valueExpression instanceof Upper) {
			Upper upper = (Upper) valueExpression;
			return isErasable(upper);
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				valueExpression.getBeginIndex(), 
				"Not support the value exrepssion.");
	}
	
	private boolean isErasable(BooleanValueExpression booleanValueExpression) {
		BooleanValue booleanValue = (BooleanValue) booleanValueExpression;
		for (int i = 0; i < booleanValue.size(); i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			if (isErasable(booleanTerm)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isErasable(BooleanTerm booleanTerm) {
		for (int i = 0; i < booleanTerm.size(); i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			if (isErasable(booleanFactor)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isErasable(BooleanFactor booleanFactor) {
		BooleanValueExpression booleanValueExpression = 
				booleanFactor.getBooleanValueExpression();
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			if (isErasable(booleanTest)) {
				return true;
			}
		}
		return isErasable(booleanValueExpression);
	}
	
	private boolean isErasable(BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = 
				booleanTest.getBooleanValueExpression();
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = (Predicate) booleanValueExpression;
			return isErasable(predicate);
		}
		return isErasable(booleanValueExpression);
	}
	
	private boolean isErasable(Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = 
					(ComparisonPredicate) predicate;
			return isErasable(comparisonPredicate);
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = 
					(BetweenPredicate) predicate;
			return isErasable(betweenPredicate);
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = 
					(DistinctPredicate) predicate;
			return isErasable(distinctPredicate);
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = 
					(ExistsPredicate) predicate;
			return isErasable(existsPredicate);
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = 
					(InPredicate) predicate;
			return isErasable(inPredicate);
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = 
					(LikePredicate) predicate;
			return isErasable(likePredicate);
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = 
					(MatchPredicate) predicate;
			return isErasable(matchPredicate);
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = 
					(NullPredicate) predicate;
			return isErasable(nullPredicate);
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = 
					(OverlapsPredicate) predicate;
			return isErasable(overlapsPredicate);
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = 
					(SimilarPredicate) predicate;
			return isErasable(similarPredicate);
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = 
					(UniquePredicate) predicate;
			return isErasable(uniquePredicate);
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"Not support the predicate.");
	}
	
	private boolean isErasable(ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		if (isErasable(left)) {
			return true;
		}
		ValueExpression right = comparisonPredicate.getRight();
		if (isErasable(right)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(BetweenPredicate betweenPredicate) {
		ValueExpression valueExpression = betweenPredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return true;
		}
		ValueExpression valueExpression1 = betweenPredicate.getValueExpression1();
		if (isErasable(valueExpression1)) {
			return true;
		}
		ValueExpression valueExpression2 = betweenPredicate.getValueExpression2();
		if (isErasable(valueExpression2)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(DistinctPredicate distinctPredicate) {
		ValueExpression left = distinctPredicate.getLeft();
		if (isErasable(left)) {
			return true;
		}
		ValueExpression right = distinctPredicate.getRight();
		if (isErasable(right)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(ExistsPredicate existsPredicate) {
		return false;
	}
	
	private boolean isErasable(InPredicate inPredicate) {
		ValueExpression valueExpression = inPredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return true;
		}
		Subquery subquery = inPredicate.getSubquery();
		if (subquery == null) {
			List<ValueExpression> list = inPredicate.getInValueList();
			for (int i = 0; i < list.size(); i++) {
				ValueExpression e = list.get(i);
				if (isErasable(e)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isErasable(LikePredicate likePredicate) {
		ValueExpression valueExpression = likePredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return true;
		}
		ValueExpression characterPattern = likePredicate.getCharacterPattern();
		if (isErasable(characterPattern)) {
			return true;
		}
		ValueExpression escapeCharacter = likePredicate.getEscapeCharacter();
		if (isErasable(escapeCharacter)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(MatchPredicate matchPredicate) {
		ValueExpression valueExpression = matchPredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(NullPredicate nullPredicate) {
		ValueExpression valueExpression = nullPredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(OverlapsPredicate overlapsPredicate) {
		ValueExpression left = overlapsPredicate.getLeft();
		if (isErasable(left)) {
			return true;
		}
		ValueExpression right = overlapsPredicate.getRight();
		if (isErasable(right)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(SimilarPredicate similarPredicate) {
		ValueExpression valueExpression = similarPredicate.getValueExpression();
		if (isErasable(valueExpression)) {
			return true;
		}
		ValueExpression similarPattern = similarPredicate.getSimilarPattern();
		if (isErasable(similarPattern)) {
			return true;
		}
		ValueExpression escapeCharacter = similarPredicate.getEscapeCharacter();
		if (isErasable(escapeCharacter)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(UniquePredicate uniquePredicate) {
		return false;
	}
	
	private boolean isErasable(AbsoluteValueExpression absoluteValueExpression) {
		ValueExpression valueExpression = absoluteValueExpression.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(Addition addition) {
		ValueExpression left = addition.getLeft();
		if (isErasable(left)) {
			return true;
		}
		ValueExpression right = addition.getRight();
		return isErasable(right);
	}
	
	private boolean isErasable(Any any) {
		ValueExpression valueExpression = any.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(Avg avg) {
		ValueExpression valueExpression = avg.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(BitLengthExpression bitLengthExpression) {
		ValueExpression valueExpression = bitLengthExpression.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(CardinalityExpression cardinalityExpression) {
		ValueExpression valueExpression = cardinalityExpression.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(CharLengthExpression charLengthExpression) {
		ValueExpression valueExpression = charLengthExpression.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(Coalesce coalesce) {
		List<ValueExpression> arguments = coalesce.getArguments();
		for (ValueExpression argument : arguments) {
			if (isErasable(argument)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isErasable(Concatenation concatenation) {
		ValueExpression left = concatenation.getLeft();
		if (isErasable(left)) {
			return true;
		}
		ValueExpression right = concatenation.getRight();
		return isErasable(right);
	}
	
	private boolean isErasable(Count count) {
		ValueExpression valueExpression = count.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(CurrentDate currentDate) {
		return false;
	}
	
	private boolean isErasable(CurrentTime currentTime) {
		return false;
	}
	
	private boolean isErasable(CurrentTimestamp currentTimestamp) {
		return false;
	}
	
	private boolean isErasable(DateLiteral dateLiteral) {
		StringLiteral dateStringLiteral = dateLiteral.getDateStringLiteral();
		if (dateStringLiteral != null) {
			return false;
		}
		Parameter parameter = dateLiteral.getParameter();
		return isErasable(parameter);
	}
	
	private boolean isErasable(TimeLiteral timeLiteral) {
		StringLiteral timeStringLiteral = timeLiteral.getTimeStringLiteral();
		if (timeStringLiteral != null) {
			return false;
		}
		Parameter parameter = timeLiteral.getParameter();
		return isErasable(parameter);
	}
	
	private boolean isErasable(TimestampLiteral timestampLiteral) {
		StringLiteral timestampStringLiteral = timestampLiteral.getTimestampStringLiteral();
		if (timestampStringLiteral != null) {
			return false;
		}
		Parameter parameter = timestampLiteral.getParameter();
		return isErasable(parameter);
	}
	
	private boolean isErasable(Division division) {
		ValueExpression left = division.getLeft();
		if (isErasable(left)) {
			return true;
		}
		ValueExpression right = division.getRight();
		return isErasable(right);
	}
	
	private boolean isErasable(Every every) {
		ValueExpression valueExpression = every.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(ExtractExpression extractExpression) {
		ValueExpression extractSource = extractExpression.getExtractSource();
		return isErasable(extractSource);
	}
	
	private boolean isErasable(FunctionInvocation functionInvocation) {
		List<ValueExpression> arguments = functionInvocation.getArguments();
		for (ValueExpression argument : arguments) {
			if (isErasable(argument)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isErasable(Grouping grouping) {
		return false;
	}
	
	private boolean isErasable(Lower lower) {
		ValueExpression argument = lower.getValueExpression();
		return isErasable(argument);
	}
	
	private boolean isErasable(Max max) {
		ValueExpression valueExpression = max.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(Min min) {
		ValueExpression valueExpression = min.getValueExpression();
		return isErasable(valueExpression);
	}

	private boolean isErasable(ModulusExpression modulusExpression) {
		ValueExpression dividend = modulusExpression.getDividend();
		if (isErasable(dividend)) {
			return true;
		}
		ValueExpression divisor = modulusExpression.getDivisor();
		return isErasable(divisor);
	}

	private boolean isErasable(Multiplication multiplication) {
		ValueExpression left = multiplication.getLeft();
		if (isErasable(left)) {
			return true;
		}
		ValueExpression right = multiplication.getRight();
		return isErasable(right);
	}

	private boolean isErasable(NameChain nameChain) {
		return false;
	}
	
	private boolean isErasable(NegativeExpression negativeExpression) {
		ValueExpression valueExpression = negativeExpression.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(NullIf nullIf) {
		ValueExpression first = nullIf.getFirst();
		if (isErasable(first)) {
			return true;
		}
		ValueExpression second = nullIf.getSecond();
		return isErasable(second);
	}
	
	private boolean isErasable(NumericLiteral numericLiteral) {
		return false;
	}
	
	private boolean isErasable(OctetLengthExpression octetLengthExpression) {
		ValueExpression valueExpression = octetLengthExpression.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(Parameter parameter) {
		String parameterName = parameter.getContent();
		if (!argument.containsName(parameterName)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(PositionExpression positionExpression) {
		ValueExpression valueExpression1 = positionExpression.getValueExpression1();
		if (isErasable(valueExpression1)) {
			return true;
		}
		ValueExpression valueExpression2 = positionExpression.getValueExpression2();
		return isErasable(valueExpression2);
	}
	
	private boolean isErasable(PositiveExpression positiveExpression) {
		ValueExpression valueExpression = positiveExpression.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(SearchedCase searchedCase) {
		List<SearchedWhenClause> searchedWhenClauseList = 
				searchedCase.getSearchedWhenClauseList();
		for (SearchedWhenClause searchedWhenClause : searchedWhenClauseList) {
			BooleanValueExpression searchedCondition = 
					searchedWhenClause.getSearchedCondition();
			if (isErasable(searchedCondition)) {
				return true;
			}
			ValueExpression result = searchedWhenClause.getResult();
			if (isErasable(result)) {
				return true;
			}
		}
		ElseClause elseClause = searchedCase.getElseClause();
		ValueExpression result = elseClause.getResult();
		return isErasable(result);
	}
	
	private boolean isErasable(SimpleCase simpleCase) {
		ValueExpression caseOperand = simpleCase.getCaseOperand();
		if (isErasable(caseOperand)) {
			return true;
		}
		List<SimpleWhenClause> simpleWhenClauseList = 
				simpleCase.getSimpleWhenClauseList();
		for (SimpleWhenClause simpleWhenClause : simpleWhenClauseList) {
			ValueExpression whenOperand = simpleWhenClause.getWhenOperand();
			if (isErasable(whenOperand)) {
				return true;
			}
			ValueExpression result = simpleWhenClause.getResult();
			if (isErasable(result)) {
				return true;
			}
		}
		ElseClause elseClause = simpleCase.getElseClause();
		ValueExpression result = elseClause.getResult();
		return isErasable(result);
	}
	
	private boolean isErasable(Some some) {
		ValueExpression valueExpression = some.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(StringLiteral stringLiteral) {
		return false;
	}
	
	private boolean isErasable(Subquery subquery) {
		return false;
	}
	
	private boolean isErasable(ToDate toDate) {
		ValueExpression valueExpression = toDate.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(ToChar toChar) {
		ValueExpression valueExpression = toChar.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(Substring substring) {
		ValueExpression valueExpression = substring.getValueExpression();
		if (isErasable(valueExpression)) {
			return true;
		}
		ValueExpression startPosition = substring.getStartPosition();
		if (isErasable(startPosition)) {
			return true;
		}
		ValueExpression stringLength = substring.getStringLength();
		if (isErasable(stringLength)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(Subtraction subtraction) {
		ValueExpression left = subtraction.getLeft();
		if (isErasable(left)) {
			return true;
		}
		ValueExpression right = subtraction.getRight();
		return isErasable(right);
	}
	
	private boolean isErasable(Sum sum) {
		ValueExpression valueExpression = sum.getValueExpression();
		return isErasable(valueExpression);
	}
	
	private boolean isErasable(Trim trim) {
		ValueExpression trimSource = trim.getTrimSource();
		if (isErasable(trimSource)) {
			return true;
		}
		ValueExpression trimCharacter = trim.getTrimCharacter();
		if (isErasable(trimCharacter)) {
			return true;
		}
		return false;
	}
	
	private boolean isErasable(Upper upper) {
		ValueExpression argument = upper.getValueExpression();
		return isErasable(argument);
	}
	
	private UpdateStatement erase(UpdateStatement updateStatement) {
		List<SetClause> setClauseList = updateStatement.getSetClauseList();
		boolean erased = false;
		NameChain targetTable = updateStatement.getTargetTable();
		List<SetClause> list = new ArrayList<SetClause>(setClauseList.size());
		for (int i = 0; i < setClauseList.size(); i++) {
			SetClause setClause = setClauseList.get(i);
			Name updateTarget = setClause.getUpdateTarget();
			ValueExpression updateSource = setClause.getUpdateSource();
			if (updateSource instanceof Parameter) {
				Parameter parameter = (Parameter) updateSource;
				String parameterName = parameter.getContent();
				if (!argument.containsName(parameterName)) {
					String columnName = targetTable.toLowerCaseString() + "." + 
							updateTarget.getContent().toLowerCase();
					if (configuration.isHashColumn(columnName)) {
						throw Sql4jException.getSql4jException(sourceCode, parameter.getBeginIndex(), 
								"Hash parameter cannot be erased.");
					}
					erased = true;
					continue;
				}
			}
			list.add(setClause);
		}
		if (erased == false) {
			return updateStatement;
		}
		if (list.isEmpty()) {
			return null;
		}
		SourceCode sourceCode = updateStatement.getSourceCode();
		int beginIndex = updateStatement.getBeginIndex();
		setClauseList = new ArrayList<SetClause>(list);
		BooleanValueExpression searchCondition = updateStatement.getSearchCondition();
		List<NameChain> hashColumnNameList = updateStatement.getHashColumnNameList();
		List<ValueExpression> hashColumnValueList = updateStatement.getHashColumnValueList();
		updateStatement = new UpdateStatement(sourceCode, beginIndex, 
				targetTable, setClauseList,	searchCondition);
		if (hashColumnNameList != null && !hashColumnNameList.isEmpty()) {
			for (int i = 0; i < hashColumnNameList.size(); i++) {
				NameChain columnName = hashColumnNameList.get(i);
				ValueExpression columnValue = hashColumnValueList.get(i);
				updateStatement.addHashColumnName(columnName);
				updateStatement.addHashColumnValue(columnValue);
			}
		}
		return updateStatement;
	}
	
	private InsertStatement erase(InsertStatement insertStatement) {
		List<ValueExpression> valueExpressionList = insertStatement.getValueExpressionList();
		if (valueExpressionList == null || valueExpressionList.isEmpty()) {
			return insertStatement;
		}
		NameChain insertionTarget = insertStatement.getInsertionTarget();
		List<Name> insertColumnList = insertStatement.getInsertColumnList();
		List<Name> insertColumnList2 = new ArrayList<Name>(valueExpressionList.size());
		List<ValueExpression> valueExpressionList2 = 
				new ArrayList<ValueExpression>(valueExpressionList.size());
		boolean erased = false;
		for (int i = 0; i < valueExpressionList.size(); i++) {
			Name insertColumn = insertColumnList.get(i);
			ValueExpression valueExpression = valueExpressionList.get(i);
			if (valueExpression instanceof Parameter) {
				Parameter parameter = (Parameter) valueExpression;
				String parameterName = parameter.getContent();
				if (!argument.containsName(parameterName)) {
					String columnName = insertionTarget.toLowerCaseString() + "." + 
							insertColumn.getContent().toLowerCase();
					if (configuration.isHashColumn(columnName)) {
						throw Sql4jException.getSql4jException(sourceCode, parameter.getBeginIndex(), 
								"Hash parameter cannot be erased.");
					}
					erased = true;
					continue;
				}
			}
			valueExpressionList2.add(valueExpression);
			insertColumnList2.add(insertColumn);
		}
		if (erased == false) {
			return insertStatement;
		}
		SourceCode sourceCode = insertStatement.getSourceCode();
		int beginIndex = insertStatement.getBeginIndex();
		SelectStatement subquery = insertStatement.getSelectStatement();
		List<NameChain> hashColumnNameList = insertStatement.getHashColumnNameList();
		List<ValueExpression> hashColumnValueList = insertStatement.getHashColumnValueList();
		List<String> lostNecessaryColumnList = insertStatement.getLostNecessaryColumnList();
		insertStatement = new InsertStatement(sourceCode, beginIndex, insertionTarget, 
				insertColumnList2, valueExpressionList2, subquery);
		if (hashColumnNameList != null && !hashColumnNameList.isEmpty()) {
			for (int i = 0; i < hashColumnNameList.size(); i++) {
				NameChain columnName = hashColumnNameList.get(i);
				ValueExpression columnValue = hashColumnValueList.get(i);
				insertStatement.addHashColumnName(columnName);
				insertStatement.addHashColumnValue(columnValue);
			}
		}
		if (lostNecessaryColumnList != null && !lostNecessaryColumnList.isEmpty()) {
			for (int i = 0; i < lostNecessaryColumnList.size(); i++) {
				String lostNecessaryColumn = lostNecessaryColumnList.get(i);
				insertStatement.addLostNecessaryColumn(lostNecessaryColumn);
			}
		}
		return insertStatement;
	}
	
	protected void clear() {
		resultJdbcTypeList.clear();
		parameterNumberList.clear();
		parameterValueList.clear();
		resultNumberList.clear();
		resultNameList.clear();
		buf.setLength(0);
	}
	
	protected void generate(TableDefinition tableDefinition) {
		append(TokenType.CREATE);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain tableName = tableDefinition.getTableName();
		generate(tableName);
		append('(');
		List<TableElement> tableElementList = 
				tableDefinition.getTableElementList();
		for (int i = 0; i < tableElementList.size(); i++) {
			TableElement tableElement = tableElementList.get(i);
			generate(tableElement);
			if (i < tableElementList.size() - 1) {
				append(',');
			}
		}
		append(')');
	}
	
	protected void generate(TableElement tableElement) {
		if (tableElement instanceof ColumnDefinition) {
			ColumnDefinition columnDefinition = 
					(ColumnDefinition) tableElement;
			generate(columnDefinition);
			return;
		}
		if (tableElement instanceof TableConstraintDefinition) {
			TableConstraintDefinition tableConstraintDefinition = 
					(TableConstraintDefinition) tableElement;
			generate(tableConstraintDefinition);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, tableElement.getBeginIndex(), 
				"Table element of this type is not supported.");
	}
	
	protected void generate(ColumnDefinition columnDefinition) {
		Name columnName = columnDefinition.getColumnName();
		generate(columnName);
		append(' ');
		DataType dataType = columnDefinition.getDataType();
		generate(dataType);
		DefaultClause defaultClause = columnDefinition.getDefaultClause();
		if (defaultClause != null) {
			append(' ');
			generate(defaultClause);
		}
		ColumnConstraintDefinition columnConstraintDefinition = 
				columnDefinition.getColumnConstraintDefinition();
		if (columnConstraintDefinition != null) {
			append(' ');
			ConstraintNameDefinition constraintNameDefinition = 
					columnConstraintDefinition.getConstraintNameDefinition();
			if (constraintNameDefinition != null) {
				append(TokenType.CONSTRAINT);
				append(' ');
				NameChain constraintName = constraintNameDefinition.
						getConstraintName();
				generate(constraintName);
				append(' ');
			}
			ColumnConstraint columnConstraint = columnConstraintDefinition.
					getColumnConstraint();
			ColumnConstraintEnum columnConstraintEnum = 
					columnConstraint.getColumnConstraintEnum();
			if (columnConstraintEnum == ColumnConstraintEnum.NOT_NULL) {
				append(TokenType.NOT);
				append(' ');
				append(TokenType.NULL);
			} else if (columnConstraintEnum == ColumnConstraintEnum.NULL) {
				append(TokenType.NULL);
			} else if (columnConstraintEnum == ColumnConstraintEnum.PRIMARY_KEY) {
				append(TokenType.PRIMARY);
				append(' ');
				append("KEY");
			} else if (columnConstraintEnum == ColumnConstraintEnum.UNIQUE) {
				append(TokenType.UNIQUE);
			} else {
				throw Sql4jException.getSql4jException(sourceCode, 
						columnConstraint.getBeginIndex(), 
						"This constraint type is not supported.");
			}
		}
	}
	
	protected void generate(DataType dataType) {
		DataTypeEnum dataTypeEnum = dataType.getDataTypeEnum();
		if (dataTypeEnum == DataTypeEnum.VARCHAR) {
			append(TokenType.VARCHAR);
			append('(');
			int length = dataType.getLength();
			append(String.valueOf(length));
			append(')');
			return;
		}
		if (dataTypeEnum == DataTypeEnum.CHAR) {
			append(TokenType.CHAR);
			append('(');
			int length = dataType.getLength();
			append(String.valueOf(length));
			append(')');
			return;
		}
		if (dataTypeEnum == DataTypeEnum.NCHAR) {
			append(TokenType.NCHAR);
			append('(');
			int length = dataType.getLength();
			append(String.valueOf(length));
			append(')');
			return;
		}
		if (dataTypeEnum == DataTypeEnum.INT) {
			append(TokenType.INT);
			return;
		}
		if (dataTypeEnum == DataTypeEnum.SMALLINT) {
			append(TokenType.SMALLINT);
			return;
		}
		if (dataTypeEnum == DataTypeEnum.REAL) {
			append(TokenType.REAL);
			return;
		}
		if (dataTypeEnum == DataTypeEnum.DATE) {
			// TODO 目前仅支持MySQL
			append("datetime");
			return;
		}
		if (dataTypeEnum == DataTypeEnum.TIMESTAMP) {
			// TODO 目前仅支持MySQL
			append("datetime");
			return;
		}
		if (dataTypeEnum == DataTypeEnum.NUMERIC) {
			// TODO 目前仅支持MySQL
			append(TokenType.NUMERIC);
			append('(');
			int precision = dataType.getPrecision();
			append(String.valueOf(precision));
			int scale = dataType.getScale();
			if (scale > -1) {
				append(',');
				append(String.valueOf(scale));
			}
			append(')');
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				dataType.getBeginIndex(), "This data type is not supported.");
	}
	
	protected void generate(DefaultClause defaultClause) {
		append(TokenType.DEFAULT);
		append(' ');
		ValueExpression defaultValue = defaultClause.getDefaultValue();
		generate(defaultValue);
	}
	
	protected void generate(TableConstraintDefinition tableConstraintDefinition) {
		NameChain constraintName = tableConstraintDefinition.getConstraintName();
		if (constraintName != null) {
			append(TokenType.CONSTRAINT);
			append(' ');
			generate(constraintName);
			append(' ');
		}
		TableConstraint tableConstraint = tableConstraintDefinition.getTableConstraint();
		TableConstraintType tableConstraintType = tableConstraint.getTableConstraintType();
		if (tableConstraintType == TableConstraintType.PRIMARY_KEY) {
			append(TokenType.PRIMARY);
			append(' ');
			append("KEY");
		} else if (tableConstraintType == TableConstraintType.UNIQUE) {
			append(TokenType.UNIQUE);
		} else {
			throw Sql4jException.getSql4jException(sourceCode, 
					tableConstraintDefinition.getBeginIndex(), 
					"This constraint type is not supported.");
		}
		append('(');
		List<Name> columnNameList = tableConstraint.getColumnNameList();
		for (int i = 0; i < columnNameList.size(); i++) {
			Name columnName = columnNameList.get(i);
			generate(columnName);
			if (i < columnNameList.size() - 1) {
				append(',');
			}
		}
		append(')');
	}
	
	protected void generate(DropTableStatement dropTableStatement) {
		append(TokenType.DROP);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain tableName = dropTableStatement.getTableName();
		generate(tableName);
	}
	
	protected abstract void generate(DropIndexStatement dropIndexStatement);
	
	protected void generate(CreateIndexStatement createIndexStatement) {
		append(TokenType.CREATE);
		append(' ');
		boolean unique = createIndexStatement.isUnique();
		if (unique) {
			append(TokenType.UNIQUE);
			append(' ');
		}
		append(TokenType.INDEX);
		append(' ');
		NameChain indexName = createIndexStatement.getIndexName();
		generate(indexName);
		append(' ');
		append(TokenType.ON);
		append(' ');
		NameChain tableName = createIndexStatement.getTableName();
		generate(tableName);
		append('(');
		List<Name> columnNameList = createIndexStatement.getColumnNameList();
		for (int i = 0; i < columnNameList.size(); i++) {
			Name columnName = columnNameList.get(i);
			generate(columnName);
			if (i < columnNameList.size() - 1) {
				append(',');
			}
		}
		append(')');
	}
	
	protected void generate(AddColumnDefinition addColumnDefinition) {
		append(TokenType.ALTER);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain tableName = addColumnDefinition.getTableName();
		generate(tableName);
		append(' ');
		append(TokenType.ADD);
		append(' ');
		ColumnDefinition columnDefinition = addColumnDefinition.getColumnDefinition();
		generate(columnDefinition);
	}
	
	protected void generate(DropColumnDefinition dropColumnDefinition) {
		append(TokenType.ALTER);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain tableName = dropColumnDefinition.getTableName();
		generate(tableName);
		append(' ');
		append(TokenType.DROP);
		append(' ');
		Name columnName = dropColumnDefinition.getColumnName();
		generate(columnName);
	}
	
	protected void generate(AlterColumnDefinition alterColumnDefinition) {
		append(TokenType.ALTER);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain tableName = alterColumnDefinition.getTableName();
		generate(tableName);
		append(' ');
		append(TokenType.ALTER);
		append(' ');
		Name columnName = alterColumnDefinition.getColumnName();
		generate(columnName);
		append(' ');
		SetColumnDefaultClause setColumnDefaultClause = 
				alterColumnDefinition.getSetColumnDefaultClause();
		if (setColumnDefaultClause != null) {
			append(TokenType.SET);
			append(' ');
			DefaultClause defaultClause = setColumnDefaultClause.getDefaultClause();
			generate(defaultClause);
		}
		DropColumnDefaultClause dropColumnDefaultClause = 
				alterColumnDefinition.getDropColumnDefaultClause();
		if (dropColumnDefaultClause != null) {
			append(TokenType.DROP);
			append(' ');
			append(TokenType.DEFAULT);
		}
	}
	
	protected void generate(AddTableConstraintDefinition addTableConstraintDefinition) {
		append(TokenType.ALTER);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain tableName = addTableConstraintDefinition.getTableName();
		generate(tableName);
		append(' ');
		append(TokenType.ADD);
		append(' ');
		TableConstraintDefinition tableConstraintDefinition = 
				addTableConstraintDefinition.getTableConstraintDefinition();
		generate(tableConstraintDefinition);
	}
	
	private void generate(DropTableConstraintDefinition dropTableConstraintDefinition) {
		append(TokenType.ALTER);
		append(' ');
		append(TokenType.TABLE);
		append(' ');
		NameChain tableName = dropTableConstraintDefinition.getTableName();
		generate(tableName);
		append(' ');
		append(TokenType.DROP);
		append(' ');
		append(TokenType.CONSTRAINT);
		append(' ');
		Name constraintName = 
				dropTableConstraintDefinition.getConstraintName();
		generate(constraintName);
	}
	
	protected abstract void generate(DropPrimaryKeyDefinition dropPrimaryKeyDefinition);
	
	protected abstract void generate(AddPrimaryKeyDefinition addPrimaryKeyDefinition);
	
	protected abstract void generate(ModifyColumnDefinition modifyColumnDefinition);
	
	protected void generate(CallStatement callStatement) {
		append('{');
		append(TokenType.CALL);
		append(' ');
		NameChain routineName = callStatement.getRoutineName();
		generate(routineName);
		append('(');
		List<InOut> list = callStatement.getInOutList();
		for (int i = 0; i < list.size(); i++) {
			InOut inOut = list.get(i);
			String in = inOut.getIn();
			if (in != null) {
				parameterNumberList.add(i + 1);
				if (!argument.containsName(in)) {
					throw Sql4jException.getSql4jException(sourceCode, 
							inOut.getInBeginIndex(), 
							"Parameter '" + in + "' not found.");
				}
				Object parameterValue = argument.getValue(in);
				parameterValueList.add(parameterValue);
			}
			String out = inOut.getOut();
			if (out != null) {
				resultNumberList.add(i + 1);
				resultNameList.add(out);
				resultJdbcTypeList.add(inOut.getJdbcType());
			}
			append('?');
			if (i < list.size() - 1) {
				append(',');
			}
		}
		append(')');
		append('}');
	}
	
	protected void generate(UpdateStatement updateStatement) {
		append(TokenType.UPDATE);
		append(' ');
		NameChain targetTable = updateStatement.getTargetTable();
		generate(targetTable);
		append(' ');
		append(TokenType.SET);
		append(' ');
		List<SetClause> setClauseList = updateStatement.getSetClauseList();
		for (int i = 0; i < setClauseList.size(); i++) {
			SetClause setClause = setClauseList.get(i);
			generate(setClause);
			if (i < setClauseList.size() - 1) {
				append(',');
			}
		}
		BooleanValueExpression searchCondition = 
				updateStatement.getSearchCondition();
		if (searchCondition != null) {
			append(' ');
			append(TokenType.WHERE);
			append(' ');
			generateCondition(searchCondition);
		}
	}
	
	protected void generate(SetClause setClause) {
		Name updateTarget = setClause.getUpdateTarget();
		generate(updateTarget);
		append('=');
		ValueExpression updateSource = setClause.getUpdateSource();
		generate(updateSource);
	}
	
	protected void generate(InsertStatement insertStatement, boolean hashForemostDb) {
		append(TokenType.INSERT);
		append(' ');
		append(TokenType.INTO);
		append(' ');
		NameChain insertionTarget = 
				insertStatement.getInsertionTarget();
		generate(insertionTarget);
		append('(');
		List<String> lostNecessaryColumnList = insertStatement.getLostNecessaryColumnList();
		for (int i = 0; i < lostNecessaryColumnList.size(); i++) {
			String lostNecessaryColumn = lostNecessaryColumnList.get(i);
			append(lostNecessaryColumn);
			if (i < lostNecessaryColumnList.size() - 1) {
				append(',');
			}
		}
		List<Name> insertColumnList = 
				insertStatement.getInsertColumnList();
		if (lostNecessaryColumnList.size() > 0 && insertColumnList.size() > 0) {
			append(',');
		}
		for (int i = 0; i < insertColumnList.size(); i++) {
			Name insertColumn = insertColumnList.get(i);
			generate(insertColumn);
			if (i < insertColumnList.size() - 1) {
				append(',');
			}
		}
		append(')');
		append(' ');
		append(TokenType.VALUES);
		append(' ');
		append('(');
		for (int i = 0; i < lostNecessaryColumnList.size(); i++) {
			String lostNecessaryColumn = lostNecessaryColumnList.get(i);
			if ("pk".equalsIgnoreCase(lostNecessaryColumn)) {
				String pk = getArgumentPk();
				append(pk);
			} else if ("ts".equalsIgnoreCase(lostNecessaryColumn)) {
				generateCurrentTimestamp();
			} else if ("hash_foremost_db".equalsIgnoreCase(lostNecessaryColumn)) {
				if (hashForemostDb) {
					append("===\nhash_foremost_db\n===");
				} else {
					append("===\nhash_foremost_db\n===");
				}
			} else {
				throw Sql4jException.getSql4jException(sourceCode, insertStatement.getBeginIndex(), "Necessary column '"+lostNecessaryColumn+"' is not supported.");
			}
			if (i < lostNecessaryColumnList.size() - 1) {
				append(',');
			}
		}
		List<ValueExpression> valueExpressionList = 
				insertStatement.getValueExpressionList();
		if (lostNecessaryColumnList.size() > 0 && valueExpressionList.size() > 0) {
			append(',');
		}
		for (int i = 0; i < valueExpressionList.size(); i++) {
			ValueExpression valueExpression = valueExpressionList.get(i);
			Name insertColumn = insertColumnList.get(i);
			String insertColumnName = insertColumn.getContent();
			if ("pk".equalsIgnoreCase(insertColumnName)) {
				generate(valueExpression);
			} else if ("ts".equalsIgnoreCase(insertColumnName)) {
				generateCurrentTimestamp();
			} else if ("hash_foremost_db".equalsIgnoreCase(insertColumnName)) {
				if (hashForemostDb) {
					append("===\nhash_foremost_db\n===");
				} else {
					append("===\nhash_foremost_db\n===");
				}
			} else {
				generate(valueExpression);
			}
			if (i < valueExpressionList.size() - 1) {
				append(',');
			}
		}
		append(')');
	}
	
	protected void generate(SelectStatement selectStatement, boolean top) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = 
					(QuerySpecification) selectStatement;
			if (top == true) {
				getColumnNameList(querySpecification);
			}
			generate(querySpecification);
		} else if (selectStatement instanceof Union) {
			Union union = (Union) selectStatement;
			if (top == true) {
				getColumnNameList(union);
			}
			generate(union);
		} else if (selectStatement instanceof Intersect) {
			Intersect intersect = (Intersect) selectStatement;
			if (top == true) {
				getColumnNameList(intersect);
			}
			generate(intersect);
		} else if (selectStatement instanceof Except) {
			Except except = (Except) selectStatement;
			if (top == true) {
				getColumnNameList(except);
			}
			generate(except);
		} else {
			throw Sql4jException.getSql4jException(sourceCode, 
					selectStatement.getBeginIndex(), 
					"Not support. " + selectStatement);
		}
	}
	
	protected void getColumnNameList(SelectStatement selectStatement) {
		if (selectStatement instanceof QuerySpecification) {
			QuerySpecification querySpecification = 
					(QuerySpecification) selectStatement;
			getColumnNameList(querySpecification);
		} else if (selectStatement instanceof Union) {
			Union union = (Union) selectStatement;
			getColumnNameList(union);
		} else if (selectStatement instanceof Intersect) {
			Intersect intersect = (Intersect) selectStatement;
			getColumnNameList(intersect);
		} else if (selectStatement instanceof Except) {
			Except except = (Except) selectStatement;
			getColumnNameList(except);
		} else {
			throw Sql4jException.getSql4jException(sourceCode, 
					selectStatement.getBeginIndex(), 
					"Not support. " + selectStatement);
		}
	}
	
	protected void getColumnNameList(QuerySpecification querySpecification) {
		List<SelectSublist> selectList = querySpecification.getSelectList();
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			int number = i + 1;
			Name name = selectSublist.getName();
			if (name != null) {
				resultNumberList.add(number);
				resultNameList.add(name.getContent());
				continue;
			}
			ValueExpression valueExpression = selectSublist.
					getValueExpression();
			if (valueExpression instanceof NameChain) {
				NameChain nameChain = (NameChain) valueExpression;
				resultNumberList.add(number);
				resultNameList.add(nameChain.
						get(nameChain.size() - 1).getContent());
			}
		}
	}
	
	protected void getColumnNameList(Union union) {
		SelectStatement selectStatment = union.getLeft();
		getColumnNameList(selectStatment);
	}
	
	protected void getColumnNameList(Intersect intersect) {
		SelectStatement selectStatment = intersect.getLeft();
		getColumnNameList(selectStatment);
	}
	
	protected void getColumnNameList(Except except) {
		SelectStatement selectStatment = except.getLeft();
		getColumnNameList(selectStatment);
	}
	
	protected void generate(Except except) {
		throw Sql4jException.getSql4jException(sourceCode, 
				except.getBeginIndex(), 
				"Not support except.");
	}
	
	protected void generate(Intersect intersect) {
		throw Sql4jException.getSql4jException(sourceCode, 
				intersect.getBeginIndex(), 
				"Not support intersect.");
	}
	
	protected void generate(Union union) {
		SelectStatement left = union.getLeft();
		generate(left, false);
		append(' ');
		append(TokenType.UNION);
		append(' ');
		if (union.getAll() == true) {
			append(TokenType.ALL);
			append(' ');
		}
		if (union.getDistinct() == true) {
			append(TokenType.DISTINCT);
			append(' ');
		}
		List<NameChain> correspondingColumnList = 
				union.getCorrespondingColumnList();
		if (correspondingColumnList != null && 
				!correspondingColumnList.isEmpty()) {
			append(TokenType.CORRESPONDING);
			append(' ');
			append(TokenType.BY);
			append(' ');
			append('(');
			for (int i = 0; i < list.size(); i++) {
				NameChain correspondingColumn = 
						correspondingColumnList.get(i);
				generate(correspondingColumn);
				if (i < list.size() - 1) {
					append(',');
				}
			}
			append(')');
			append(' ');
		}
		SelectStatement right = union.getRight();
		generate(right, false);
	}
	
	protected void generate(DeleteStatement deleteStatement) {
		append(TokenType.DELETE);
		append(' ');
		append(TokenType.FROM);
		append(' ');
		NameChain targetTable = 
				deleteStatement.getTargetTable();
		generate(targetTable);
		BooleanValueExpression searchCondition = 
				deleteStatement.getSearchCondition();
		if (searchCondition != null) {
			append(' ');
			append(TokenType.WHERE);
			append(' ');
			generateCondition(searchCondition);
		}
	}
	
	protected void generate(Name name) {
		String content = name.getContent();
		append(content);
	}
	
	protected abstract void generate(QuerySpecification querySpecification);
	
	protected void generate(SetQuantifier setQuantifier) {
		if (setQuantifier == null) {
			return;
		}
		if (setQuantifier.isDistinct()) {
			append(TokenType.DISTINCT.toString());
		}
	}
	
	protected void generate(List<SelectSublist> selectList) {
		for (int i = 0; i < selectList.size(); i++) {
			SelectSublist selectSublist = selectList.get(i);
			generate(i + 1, selectSublist);
			if (i != selectList.size() - 1) {
				append(',');
			}
		}
	}
	
	private void generate(int number, SelectSublist selectSublist) {
		ValueExpression valueExpression = selectSublist.
				getValueExpression();
		Name name = selectSublist.getName();
		/*if (name != null) {
			resultNumberList.add(number);
			resultNameList.add(name.getContent());
		} else {
			if (valueExpression instanceof NameChain) {
				NameChain nameChain = (NameChain) valueExpression;
				resultNumberList.add(number);
				resultNameList.add(nameChain.
						get(nameChain.size() - 1).getContent());
			}
		}*/
		generate(valueExpression);
		if (name != null) {
			append(' ').append(name.getContent());
		}
	}
	
	protected void generate(ValueExpression valueExpression) {
		if (valueExpression instanceof BooleanValueExpression) {
			BooleanValueExpression booleanValueExpression = 
					(BooleanValueExpression) valueExpression;
			generate(booleanValueExpression);
			return;
		}
		if (valueExpression instanceof AbsoluteValueExpression) {
			AbsoluteValueExpression absoluteValueExpression = 
					(AbsoluteValueExpression) valueExpression;
			generate(absoluteValueExpression);
			return;
		}
		if (valueExpression instanceof Addition) {
			Addition addition = (Addition) valueExpression;
			generate(addition);
			return;
		}
		if (valueExpression instanceof Any) {
			Any any = (Any) valueExpression;
			generate(any);
			return;
		}
		if (valueExpression instanceof Avg) {
			Avg avg = (Avg) valueExpression;
			generate(avg);
			return;
		}
		if (valueExpression instanceof BitLengthExpression) {
			BitLengthExpression bitLengthExpression = 
					(BitLengthExpression) valueExpression;
			generate(bitLengthExpression);
			return;
		}
		if (valueExpression instanceof CardinalityExpression) {
			CardinalityExpression cardinalityExpression = 
					(CardinalityExpression) valueExpression;
			generate(cardinalityExpression);
			return;
		}
		if (valueExpression instanceof CharLengthExpression) {
			CharLengthExpression charLengthExpression = 
					(CharLengthExpression) valueExpression;
			generate(charLengthExpression);
			return;
		}
		if (valueExpression instanceof Coalesce) {
			Coalesce coalesce = (Coalesce) valueExpression;
			generate(coalesce);
			return;
		}
		if (valueExpression instanceof Concatenation) {
			Concatenation concatenation = (Concatenation) valueExpression;
			generate(concatenation);
			return;
		}
		if (valueExpression instanceof Count) {
			Count count = (Count) valueExpression;
			generate(count);
			return;
		}
		if (valueExpression instanceof CurrentDate) {
			CurrentDate currentDate = (CurrentDate) valueExpression;
			generate(currentDate);
			return;
		}
		if (valueExpression instanceof CurrentTime) {
			CurrentTime currentTime = (CurrentTime) valueExpression;
			generate(currentTime);
			return;
		}
		if (valueExpression instanceof CurrentTimestamp) {
			CurrentTimestamp currentTimestamp = (CurrentTimestamp) valueExpression;
			generate(currentTimestamp);
			return;
		}
		if (valueExpression instanceof DateLiteral) {
			DateLiteral dateLiteral = (DateLiteral) valueExpression;
			generate(dateLiteral);
			return;
		}
		if (valueExpression instanceof Division) {
			Division division = (Division) valueExpression;
			generate(division);
			return;
		}
		if (valueExpression instanceof Every) {
			Every every = (Every) valueExpression;
			generate(every);
			return;
		}
		if (valueExpression instanceof ExtractExpression) {
			ExtractExpression extractExpression = 
					(ExtractExpression) valueExpression;
			generate(extractExpression);
			return;
		}
		if (valueExpression instanceof FunctionInvocation) {
			FunctionInvocation functionInvocation = 
					(FunctionInvocation) valueExpression;
			generate(functionInvocation);
			return;
		}
		if (valueExpression instanceof Grouping) {
			Grouping grouping = (Grouping) valueExpression;
			generate(grouping);
			return;
		}
		if (valueExpression instanceof Lower) {
			Lower lower = (Lower) valueExpression;
			generate(lower);
			return;
		}
		if (valueExpression instanceof Max) {
			Max max = (Max) valueExpression;
			generate(max);
			return;
		}
		if (valueExpression instanceof Min) {
			Min min = (Min) valueExpression;
			generate(min);
			return;
		}
		if (valueExpression instanceof ModulusExpression) {
			ModulusExpression modulusExpression = 
					(ModulusExpression) valueExpression;
			generate(modulusExpression);
			return;
		}
		if (valueExpression instanceof Multiplication) {
			Multiplication multiplication = (Multiplication) valueExpression;
			generate(multiplication);
			return;
		}
		if (valueExpression instanceof NameChain) {
			NameChain nameChain = (NameChain) valueExpression;
			generate(nameChain);
			return;
		}
		if (valueExpression instanceof NegativeExpression) {
			NegativeExpression negativeExpression = 
					(NegativeExpression) valueExpression;
			generate(negativeExpression);
			return;
		}
		if (valueExpression instanceof NullIf) {
			NullIf nullIf = (NullIf) valueExpression;
			generate(nullIf);
			return;
		}
		if (valueExpression instanceof NumericLiteral) {
			NumericLiteral numericLiteral = 
					(NumericLiteral) valueExpression;
			generate(numericLiteral);
			return;
		}
		if (valueExpression instanceof OctetLengthExpression) {
			OctetLengthExpression octetLengthExpression = 
					(OctetLengthExpression) valueExpression;
			generate(octetLengthExpression);
			return;
		}
		if (valueExpression instanceof Parameter) {
			Parameter parameter = (Parameter) valueExpression;
			generate(parameter);
			return;
		}
		if (valueExpression instanceof PositionExpression) {
			PositionExpression positionExpression = 
					(PositionExpression) valueExpression;
			generate(positionExpression);
			return;
		}
		if (valueExpression instanceof PositiveExpression) {
			PositiveExpression positiveExpression = 
					(PositiveExpression) valueExpression;
			generate(positiveExpression);
			return;
		}
		if (valueExpression instanceof SearchedCase) {
			SearchedCase searchedCase = (SearchedCase) valueExpression;
			generate(searchedCase);
			return;
		}
		if (valueExpression instanceof SimpleCase) {
			SimpleCase simpleCase = (SimpleCase) valueExpression;
			generate(simpleCase);
			return;
		}
		if (valueExpression instanceof Some) {
			Some some = (Some) valueExpression;
			generate(some);
			return;
		}
		if (valueExpression instanceof StringLiteral) {
			StringLiteral stringLiteral = 
					(StringLiteral) valueExpression;
			generate(stringLiteral);
			return;
		}
		if (valueExpression instanceof Subquery) {
			Subquery subquery = (Subquery) valueExpression;
			generate(subquery);
			return;
		}
		if (valueExpression instanceof ToDate) {
			ToDate toDate = (ToDate) valueExpression;
			generate(toDate);
			return;
		}
		if (valueExpression instanceof ToChar) {
			ToChar toChar = (ToChar) valueExpression;
			generate(toChar);
			return;
		}
		if (valueExpression instanceof Substring) {
			Substring substring = (Substring) valueExpression;
			generate(substring);
			return;
		}
		if (valueExpression instanceof Subtraction) {
			Subtraction subtraction = (Subtraction) valueExpression;
			generate(subtraction);
			return;
		}
		if (valueExpression instanceof Sum) {
			Sum sum = (Sum) valueExpression;
			generate(sum);
			return;
		}
		if (valueExpression instanceof TimeLiteral) {
			TimeLiteral timeLiteral = (TimeLiteral) valueExpression;
			generate(timeLiteral);
			return;
		}
		if (valueExpression instanceof TimestampLiteral) {
			TimestampLiteral timestampLiteral = 
					(TimestampLiteral) valueExpression;
			generate(timestampLiteral);
			return;
		}
		if (valueExpression instanceof Trim) {
			Trim trim = (Trim) valueExpression;
			generate(trim);
			return;
		}
		if (valueExpression instanceof Upper) {
			Upper upper = (Upper) valueExpression;
			generate(upper);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				valueExpression.getBeginIndex(), 
				"Not support the value exrepssion.");
	}
	
	protected abstract void generate(DateLiteral dateLiteral);
	
	protected void generate(CurrentTimestamp currentTimestamp) {
		append(TokenType.CURRENT_TIMESTAMP);
	}
	
	protected void generate(CurrentTime currentTime) {
		append(TokenType.CURRENT_TIME);
	}
	
	protected void generate(CurrentDate currentDate) {
		append(TokenType.CURRENT_DATE);
	}
	
	protected void generate(Division division) {
		ValueExpression left = division.getLeft();
		if (left instanceof Addition ||
			left instanceof Concatenation ||
			left instanceof Division ||
			left instanceof Multiplication ||
			left instanceof Subquery ||
			left instanceof Subtraction) {
			append('(');
			generate(left);
			append(')');
		} else {
			generate(left);
		}
		append('/');
		ValueExpression right = division.getRight();
		if (right instanceof Addition ||
			right instanceof Concatenation ||
			right instanceof Division ||
			right instanceof Multiplication ||
			right instanceof Subquery ||
			right instanceof Subtraction) {
			append('(');
			generate(right);
			append(')');
		} else {
			generate(right);
		}
	}
	
	protected void generate(Concatenation concatenation) {
		ValueExpression left = concatenation.getLeft();
		if (left instanceof Addition ||
			left instanceof Concatenation ||
			left instanceof Division ||
			left instanceof Multiplication ||
			left instanceof Subquery ||
			left instanceof Subtraction) {
			append('(');
			generate(left);
			append(')');
		} else {
			generate(left);
		}
		append('|').append('|');
		ValueExpression right = concatenation.getRight();
		if (right instanceof Addition ||
			right instanceof Concatenation ||
			right instanceof Division ||
			right instanceof Multiplication ||
			right instanceof NegativeExpression ||
			right instanceof Subquery ||
			right instanceof Subtraction) {
			append('(');
			generate(right);
			append(')');
		} else {
			generate(right);
		}
	}
	
	protected void generate(Coalesce coalesce) {
		append("coalesce(");
		List<ValueExpression> list = coalesce.getArguments();
		for (int i = 0; i < list.size(); i++) {
			ValueExpression arg = list.get(i);
			generate(arg);
			if (i < list.size() - 1) {
				append(',');
			}
		}
		append(')');
	}
	
	protected void generate(CharLengthExpression charLengthExpression) {
		append("char_length(");
		ValueExpression valueExpression = 
				charLengthExpression.getValueExpression();
		generate(valueExpression);
		append(')');
	}
	
	protected void generate(CardinalityExpression cardinalityExpression) {
		throw Sql4jException.getSql4jException(sourceCode, 
				cardinalityExpression.getBeginIndex(), 
				"Not support cardinality expression.");
	}
	
	protected void generate(BitLengthExpression bitLengthExpression) {
		throw Sql4jException.getSql4jException(sourceCode, 
				bitLengthExpression.getBeginIndex(), 
				"Not support bit length expression.");
	}
	
	protected void generateCondition(BetweenPredicate betweenPredicate) {
		ValueExpression valueExpression = 
				betweenPredicate.getValueExpression();
		generate(valueExpression);
		append(' ');
		boolean not = betweenPredicate.getNot();
		if (not == true) {
			append(TokenType.NOT);
			append(' ');
		}
		append(TokenType.BETWEEN);
		append(' ');
		ValueExpression valueExpression1 = 
				betweenPredicate.getValueExpression1();
		generate(valueExpression1);
		append(' ');
		append(TokenType.AND);
		append(' ');
		ValueExpression valueExpression2 = 
				betweenPredicate.getValueExpression2();
		generate(valueExpression2);
	}
	
	protected void generate(ExtractExpression extractExpression) {
		append("extract(");
		ExtractField extractField = extractExpression.getExtractField();
		append(extractField.toString());
		append(' ');
		append(TokenType.FROM);
		append(' ');
		ValueExpression extractSource = extractExpression.getExtractSource();
		generate(extractSource);
		append(')');
	}
	
	protected void generate(Grouping grouping) {
		append(TokenType.GROUPING);
		append('(');
		NameChain columnReference = grouping.getColumnReference();
		generate(columnReference);
		append(')');
	}
	
	protected void generate(Multiplication multiplication) {
		ValueExpression left = 
				multiplication.getLeft();
		if (left instanceof Addition ||
			left instanceof Concatenation ||
			left instanceof Division ||
			left instanceof Multiplication ||
			left instanceof Subquery ||
			left instanceof Subtraction) {
			append('(');
			generate(left);
			append(')');
		} else {
			generate(left);
		}
		append('*');
		ValueExpression right = 
				multiplication.getRight();
		if (right instanceof Addition ||
			right instanceof Concatenation ||
			right instanceof Division ||
			right instanceof Multiplication ||
			right instanceof Subquery ||
			right instanceof Subtraction) {
			append('(');
			generate(right);
			append(')');
		} else {
			generate(right);
		}
	}
	
	protected void generate(NumericLiteral numericLiteral) {
		String content = numericLiteral.getContent();
		append(content);
	}
	
	protected void generate(OctetLengthExpression octetLengthExpression) {
		throw Sql4jException.getSql4jException(sourceCode, 
				octetLengthExpression.getBeginIndex(), 
				"Not support octet length expression.");
	}
	
	protected void generate(Parameter parameter) {
		String parameterName = parameter.getContent();
		if (!argument.containsName(parameterName)) {
			throw Sql4jException.getSql4jException(sourceCode, parameter.getBeginIndex(), 
					"Parameter '" + parameterName + "' not found.");
		}
		Object parameterValue = argument.getValue(parameterName);
		if (parameterValue == null) {
			append("NULL");
			return;
		}
		if (parameterValue instanceof String) {
			String javaString = (String) parameterValue;
			String sqlString = toSqlString(javaString);
			append(sqlString);
			return;
		}
		if (parameterValue instanceof Number) {
			String javaString = parameterValue.toString();
			String sqlString = toSqlString(javaString);
			append(sqlString);
			return;
		}
		append('?');
		parameterValueList.add(parameterValue);
		// TODO 有些参数要拼接进SQL语句
	}
	
	protected void generate(PositionExpression positionExpression) {
		throw Sql4jException.getSql4jException(sourceCode, 
				positionExpression.getBeginIndex(), 
				"Not support position expression.");
	}
	
	protected void generate(PositiveExpression positiveExpression) {
		ValueExpression valueExpression = 
				positiveExpression.getValueExpression();
		if (valueExpression instanceof Addition ||
			valueExpression instanceof Concatenation ||
			valueExpression instanceof Division ||
			valueExpression instanceof Multiplication ||
			valueExpression instanceof PositiveExpression ||
			valueExpression instanceof Subquery ||
			valueExpression instanceof Subtraction) {
			if (valueExpression instanceof PositiveExpression) {
				PositiveExpression positiveExpression2 = 
						(PositiveExpression) valueExpression;
				ValueExpression valueExpression2 = 
						positiveExpression2.getValueExpression();
				generate(valueExpression2);
			} else {
				append('(');
				generate(valueExpression);
				append(')');
			}
		} else {
			generate(valueExpression);
		}
	}
	
	protected void generate(SearchedCase searchedCase) {
		append(TokenType.CASE);
		append(' ');
		List<SearchedWhenClause> list = 
				searchedCase.getSearchedWhenClauseList();
		for (int i = 0; i < list.size(); i++) {
			SearchedWhenClause clause = list.get(i);
			append(TokenType.WHEN);
			append(' ');
			BooleanValueExpression searchCondition = 
					clause.getSearchedCondition();
			generateCondition(searchCondition);
			append(' ');
			append(TokenType.THEN);
			append(' ');
			ValueExpression result = clause.getResult();
			generate(result);
			append(' ');
		}
		ElseClause elseClause = searchedCase.getElseClause();
		if (elseClause != null) {
			append(TokenType.ELSE);
			append(' ');
			ValueExpression result = elseClause.getResult();
			generate(result);
			append(' ');
		}
		append(TokenType.END);
	}
	
	protected void generate(SimpleCase simpleCase) {
		append(TokenType.CASE);
		append(' ');
		ValueExpression caseOperand = 
				simpleCase.getCaseOperand();
		generate(caseOperand);
		append(' ');
		List<SimpleWhenClause> list = 
				simpleCase.getSimpleWhenClauseList();
		for (int i = 0; i < list.size(); i++) {
			SimpleWhenClause clause = list.get(i);
			append(TokenType.WHEN);
			append(' ');
			ValueExpression whenOperand = 
					clause.getWhenOperand();
			generate(whenOperand);
			append(' ');
			append(TokenType.THEN);
			append(' ');
			ValueExpression result = 
					clause.getResult();
			generate(result);
			append(' ');
		}
		ElseClause elseClause = simpleCase.getElseClause();
		if (elseClause != null) {
			append(TokenType.ELSE);
			append(' ');
			ValueExpression result = elseClause.getResult();
			generate(result);
			append(' ');
		}
		append(TokenType.END);
	}
	
	protected void generate(NegativeExpression negativeExpression) {
		append('-');
		ValueExpression valueExpression = 
				negativeExpression.getValueExpression();
		if (valueExpression instanceof Addition ||
			valueExpression instanceof Concatenation ||
			valueExpression instanceof Division ||
			valueExpression instanceof Multiplication ||
			valueExpression instanceof NegativeExpression ||
			valueExpression instanceof PositiveExpression ||
			valueExpression instanceof Subquery ||
			valueExpression instanceof Subtraction) {
			if (valueExpression instanceof PositiveExpression) {
				PositiveExpression positiveExpression = 
						(PositiveExpression) valueExpression;
				ValueExpression valueExpression2 = 
						positiveExpression.getValueExpression();
				generate(valueExpression2);
			} else {
				append('(');
				generate(valueExpression);
				append(')');
			}
		} else {
			generate(valueExpression);
		}
	}
	
	protected void generate(Subquery subquery) {
		SelectStatement selectStatement = 
				subquery.getSelectStatement();
		append('(');
		generate(selectStatement, false);
		append(')');
	}
	
	protected void generate(ModulusExpression modulusExpression) {
		append("mod(");
		ValueExpression dividend = modulusExpression.getDividend();
		generate(dividend);
		append(',');
		ValueExpression divisor = modulusExpression.getDivisor();
		generate(divisor);
		append(')');
	}
	
	protected void generate(Subtraction subtraction) {
		ValueExpression left = subtraction.getLeft();
		if (left instanceof Addition ||
			left instanceof Concatenation ||
			left instanceof Division ||
			left instanceof Multiplication ||
			left instanceof Subquery ||
			left instanceof Subtraction) {
			append('(');
			generate(left);
			append(')');
		} else {
			generate(left);
		}
		append('+');
		ValueExpression right = subtraction.getRight();
		if (right instanceof Addition ||
			right instanceof Concatenation ||
			right instanceof Division ||
			right instanceof Multiplication ||
			right instanceof NegativeExpression ||
			right instanceof Subquery ||
			right instanceof Subtraction) {
			append('(');
			generate(right);
			append(')');
		} else {
			generate(right);
		}
	}
	
	protected void generate(Addition addition) {
		ValueExpression left = addition.getLeft();
		if (left instanceof Addition ||
			left instanceof Concatenation ||
			left instanceof Division ||
			left instanceof Multiplication ||
			left instanceof Subquery ||
			left instanceof Subtraction) {
			append('(');
			generate(left);
			append(')');
		} else {
			generate(left);
		}
		append('+');
		ValueExpression right = addition.getRight();
		if (right instanceof Addition ||
			right instanceof Concatenation ||
			right instanceof Division ||
			right instanceof Multiplication ||
			right instanceof NegativeExpression ||
			right instanceof Subquery ||
			right instanceof Subtraction) {
			append('(');
			generate(right);
			append(')');
		} else {
			generate(right);
		}
	}
	
	protected void generate(AbsoluteValueExpression absoluteValueExpression) {
		append("ABS(");
		ValueExpression valueExpression = 
				absoluteValueExpression.getValueExpression();
		generate(valueExpression);
		append(')');
	}
	
	protected abstract void generate(ToDate toDate);
	
	protected abstract void generate(ToChar toChar);
	
	protected void generate(Substring substring) {
		append("substring(");
		ValueExpression valueExpression = 
				substring.getValueExpression();
		generate(valueExpression);
		append(' ');
		ValueExpression startPosition = 
				substring.getStartPosition();
		append(TokenType.FROM);
		append(' ');
		generate(startPosition);
		ValueExpression stringLength = 
				substring.getStringLength();
		if (stringLength != null) {
			append(' ');
			append(TokenType.FOR);
			append(' ');
			generate(stringLength);
		}
		append(')');
	}
	
	protected abstract void generate(TimeLiteral timeLiteral);
	
	protected abstract void generate(TimestampLiteral timestampLiteral);
	
	protected void generate(NameChain nameChain) {
		for (int i = 0; i < nameChain.size(); i++) {
			Name name = nameChain.get(i);
			append(name.getContent());
			if (i != nameChain.size() - 1) {
				append('.');
			}
		}
	}
	
	protected void generate(BooleanValueExpression booleanValueExpression) {
		append(TokenType.CASE).append(' ').
		append(TokenType.WHEN).append(' ');
		generateCondition(booleanValueExpression);
		append(' ').
		append(TokenType.THEN).append(' ').append('1').append(' ').
		append(TokenType.ELSE).append(' ').append('0').append(' ').
		append(TokenType.END);
	}
	
	protected void generate(StringLiteral stringLiteral) {
		String content = stringLiteral.getContent();
		String sqlString = toSqlString(content);
		append(sqlString);
	}
	
	protected void generate(Trim trim) {
		append("TRIM(");
		TrimSpecification trimSpecification = 
				trim.getTrimSpecification();
		if (trimSpecification == TrimSpecification.LEADING) {
			append(' ').append(TokenType.LEADING);
		} else if (trimSpecification == TrimSpecification.TRAILING) {
			append(' ').append(TokenType.TRAILING);
		} else if (trimSpecification == TrimSpecification.BOTH) {
			append(' ').append(TokenType.BOTH);
		}
		ValueExpression trimCharacter = trim.getTrimCharacter();
		if (trimCharacter != null) {
			if (trimSpecification != null) {
				append(' ');
			}
			generate(trimCharacter);
		}
		if (trimSpecification != null || trimCharacter != null) {
			append(' ').append(TokenType.FROM).append(' ');
		}
		ValueExpression trimSource = trim.getTrimSource();
		generate(trimSource);
		append(')');
	}
	
	protected void generate(Upper upper) {
		append("upper(");
		ValueExpression argument = upper.getValueExpression();
		generate(argument);
		append(')');
	}
	
	protected void generate(Lower lower) {
		append("lower(");
		ValueExpression argument = lower.getValueExpression();
		generate(argument);
		append(')');
	}
	
	protected void generate(Max max) {
		append("max(");
		Distinct distinct = max.getDistinct();
		if (distinct != null) {
			append(' ').append(TokenType.DISTINCT).append(' ');
		}
		ValueExpression argument = max.getValueExpression();
		generate(argument);
		append(')');
	}
	
	protected void generate(Min min) {
		append("min(");
		Distinct distinct = min.getDistinct();
		if (distinct != null) {
			append(' ').append(TokenType.DISTINCT).append(' ');
		}
		ValueExpression argument = min.getValueExpression();
		generate(argument);
		append(')');
	}
	
	protected void generate(Sum sum) {
		append("sum(");
		Distinct distinct = sum.getDistinct();
		if (distinct != null) {
			append(' ').append(TokenType.DISTINCT).append(' ');
		}
		ValueExpression argument = sum.getValueExpression();
		generate(argument);
		append(')');
	}
	
	protected void generate(Count count) {
		append("count(");
		Distinct distinct = count.getDistinct();
		if (distinct != null) {
			append(' ').append(TokenType.DISTINCT).append(' ');
		}
		ValueExpression argument = count.getValueExpression();
		generate(argument);
		append(')');
	}
	
	protected void generate(Some some) {
		append(TokenType.SOME);
		append('(');
		Distinct distinct = some.getDistinct();
		if (distinct != null) {
			append(' ').append(TokenType.DISTINCT).append(' ');
		}
		ValueExpression argument = some.getValueExpression();
		generate(argument);
		append(')');
	}
	
	protected void generate(Every every) {
		append("every(");
		Distinct distinct = every.getDistinct();
		if (distinct != null) {
			append(' ').append(TokenType.DISTINCT).append(' ');
		}
		ValueExpression argument = every.getValueExpression();
		generate(argument);
		append(')');
	}
	
	protected void generate(Any any) {
		append(TokenType.ANY);
		append('(');
		Distinct distinct = any.getDistinct();
		if (distinct != null) {
			append(' ').append(TokenType.DISTINCT).append(' ');
		}
		ValueExpression argument = any.getValueExpression();
		generate(argument);
		append(')');
	}
	
	protected void generate(Avg avg) {
		append("avg(");
		Distinct distinct = avg.getDistinct();
		if (distinct != null) {
			append(' ').append(TokenType.DISTINCT).append(' ');
		}
		ValueExpression argument = avg.getValueExpression();
		generate(argument);
		append(')');
	}
	
	protected void generate(NullIf nullIf) {
		append("nullif(");
		ValueExpression first = nullIf.getFirst();
		generate(first);
		append(',');
		ValueExpression second = nullIf.getSecond();
		generate(second);
		append(')');
	}
	
	protected void generate(FunctionInvocation functionInvocation) {
		NameChain functionName = functionInvocation.getFunctionName();
		generate(functionName);
		append('(');
		List<ValueExpression> arguments = functionInvocation.getArguments();
		for (int i = 0; i < arguments.size(); i++) {
			ValueExpression argument = arguments.get(i);
			generate(argument);
			if (i != arguments.size() - 1) {
				append(',');
			}
		}
		append(')');
	}
	
	protected void generateTableReferenceList(List<TableReference> tableReferenceList) {
		for (int i = 0; i < tableReferenceList.size(); i++) {
			TableReference tableReference = tableReferenceList.get(i);
			generate(tableReference);
			if (i < tableReferenceList.size() - 1) {
				append(',');
			}
		}
	}
	
	protected void generate(TableReference tableReference) {
		if (tableReference instanceof TablePrimary) {
			TablePrimary tablePrimary = (TablePrimary) tableReference;
			generate(tablePrimary);
			return;
		}
		if (tableReference instanceof DerivedTable) {
			DerivedTable derivedTable = (DerivedTable) 
					tableReference;
			generate(derivedTable);
			return;
		}
		if (tableReference instanceof LeftOuterJoin) {
			LeftOuterJoin leftOuterJoin = 
					(LeftOuterJoin) tableReference;
			generate(leftOuterJoin);
			return;
		}
		if (tableReference instanceof RightOuterJoin) {
			RightOuterJoin rightOuterJoin = 
					(RightOuterJoin) tableReference;
			generate(rightOuterJoin);
			return;
		}
		if (tableReference instanceof InnerJoin) {
			InnerJoin innerJoin = 
					(InnerJoin) tableReference;
			generate(innerJoin);
			return;
		}
		if (tableReference instanceof FullOuterJoin) {
			FullOuterJoin fullOuterJoin = 
					(FullOuterJoin) tableReference;
			generate(fullOuterJoin);
			return;
		}
		if (tableReference instanceof NaturalInnerJoin) {
			NaturalInnerJoin naturalInnerJoin = 
					(NaturalInnerJoin) tableReference;
			generate(naturalInnerJoin);
			return;
		}
		if (tableReference instanceof NaturalLeftOuterJoin) {
			NaturalLeftOuterJoin naturalLeftOuterJoin = 
					(NaturalLeftOuterJoin) tableReference;
			generate(naturalLeftOuterJoin);
			return;
		}
		if (tableReference instanceof NaturalRightOuterJoin) {
			NaturalRightOuterJoin naturalRightOuterJoin = 
					(NaturalRightOuterJoin) tableReference;
			generate(naturalRightOuterJoin);
			return;
		}
		if (tableReference instanceof NaturalFullOuterJoin) {
			NaturalFullOuterJoin naturalFullOuterJoin = 
					(NaturalFullOuterJoin) tableReference;
			generate(naturalFullOuterJoin);
			return;
		}
		if (tableReference instanceof CrossJoin) {
			CrossJoin crossJoin = (CrossJoin) tableReference;
			generate(crossJoin);
			return;
		}
		if (tableReference instanceof UnionJoin) {
			UnionJoin unionJoin = (UnionJoin) tableReference;
			generate(unionJoin);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				tableReference.getBeginIndex(), 
				"Not support the table reference, " + tableReference);
	}
	
	protected void generate(NaturalInnerJoin natualInnerJoin) {
		TableReference left = natualInnerJoin.getLeft();
		if (left instanceof TablePrimary ||
			left instanceof DerivedTable) {
			generate(left);
		} else {
			append('(');
			generate(left);
			append(')');
		}
		append(' ');
		append(TokenType.NATURAL);
		append(' ');
		append(TokenType.INNER);
		append(' ');
		append(TokenType.JOIN);
		append(' ');
		TableReference right = natualInnerJoin.getRight();
		if (right instanceof TablePrimary ||
			right instanceof DerivedTable) {
			generate(right);
		} else {
			append('(');
			generate(right);
			append(')');
		}
	}
	
	protected void generate(NaturalLeftOuterJoin natualLeftOuterJoin) {
		TableReference left = natualLeftOuterJoin.getLeft();
		if (left instanceof TablePrimary ||
			left instanceof DerivedTable) {
			generate(left);
		} else {
			append('(');
			generate(left);
			append(')');
		}
		append(' ');
		append(TokenType.NATURAL);
		append(' ');
		append(TokenType.LEFT);
		append(' ');
		append(TokenType.JOIN);
		append(' ');
		TableReference right = natualLeftOuterJoin.getRight();
		if (right instanceof TablePrimary ||
			right instanceof DerivedTable) {
			generate(right);
		} else {
			append('(');
			generate(right);
			append(')');
		}
	}
	
	protected void generate(NaturalRightOuterJoin natualRightOuterJoin) {
		TableReference left = natualRightOuterJoin.getLeft();
		if (left instanceof TablePrimary ||
			left instanceof DerivedTable) {
			generate(left);
		} else {
			append('(');
			generate(left);
			append(')');
		}
		append(' ');
		append(TokenType.NATURAL);
		append(' ');
		append(TokenType.RIGHT);
		append(' ');
		append(TokenType.JOIN);
		append(' ');
		TableReference right = natualRightOuterJoin.getRight();
		if (right instanceof TablePrimary ||
			right instanceof DerivedTable) {
			generate(right);
		} else {
			append('(');
			generate(right);
			append(')');
		}
	}
	
	protected void generate(NaturalFullOuterJoin natualFullOuterJoin) {
		TableReference left = natualFullOuterJoin.getLeft();
		if (left instanceof TablePrimary ||
			left instanceof DerivedTable) {
			generate(left);
		} else {
			append('(');
			generate(left);
			append(')');
		}
		append(' ');
		append(TokenType.NATURAL);
		append(' ');
		append(TokenType.FULL);
		append(' ');
		append(TokenType.JOIN);
		append(' ');
		TableReference right = natualFullOuterJoin.getRight();
		if (right instanceof TablePrimary ||
			right instanceof DerivedTable) {
			generate(right);
		} else {
			append('(');
			generate(right);
			append(')');
		}
	}
	
	protected void generate(InnerJoin innerJoin) {
		TableReference left = innerJoin.getLeft();
		if (left instanceof TablePrimary ||
			left instanceof DerivedTable) {
			generate(left);
		} else {
			append('(');
			generate(left);
			append(')');
		}
		append(' ');
		append(TokenType.INNER);
		append(' ');
		append(TokenType.JOIN);
		append(' ');
		TableReference right = innerJoin.getRight();
		if (right instanceof TablePrimary ||
			right instanceof DerivedTable) {
			generate(right);
		} else {
			append('(');
			generate(right);
			append(')');
		}
		BooleanValueExpression joinCondition = 
				innerJoin.getJoinCondition();
		if (joinCondition != null) {
			append(' ');
			append(TokenType.ON);
			append(' ');
			generateCondition(joinCondition);
		}
	}
	
	protected void generate(RightOuterJoin rightOuterJoin) {
		TableReference left = rightOuterJoin.getLeft();
		if (left instanceof TablePrimary ||
			left instanceof DerivedTable) {
			generate(left);
		} else {
			append('(');
			generate(left);
			append(')');
		}
		append(' ');
		append(TokenType.RIGHT);
		append(' ');
		append(TokenType.JOIN);
		append(' ');
		TableReference right = rightOuterJoin.getRight();
		if (right instanceof TablePrimary ||
			right instanceof DerivedTable) {
			generate(right);
		} else {
			append('(');
			generate(right);
			append(')');
		}
		BooleanValueExpression joinCondition = 
				rightOuterJoin.getJoinCondition();
		if (joinCondition != null) {
			append(' ');
			append(TokenType.ON);
			append(' ');
			generateCondition(joinCondition);
		}
	}
	
	protected void generate(LeftOuterJoin leftOuterJoin) {
		TableReference left = leftOuterJoin.getLeft();
		if (left instanceof TablePrimary ||
			left instanceof DerivedTable) {
			generate(left);
		} else {
			append('(');
			generate(left);
			append(')');
		}
		append(' ');
		append(TokenType.LEFT);
		append(' ');
		append(TokenType.JOIN);
		append(' ');
		TableReference right = leftOuterJoin.getRight();
		if (right instanceof TablePrimary ||
			right instanceof DerivedTable) {
			generate(right);
		} else {
			append('(');
			generate(right);
			append(')');
		}
		BooleanValueExpression joinCondition = 
				leftOuterJoin.getJoinCondition();
		if (joinCondition != null) {
			append(' ');
			append(TokenType.ON);
			append(' ');
			generateCondition(joinCondition);
		}
	}
	
	protected void generate(FullOuterJoin fullOuterJoin) {
		// TODO MySQL not support full outer join.
		TableReference left = fullOuterJoin.getLeft();
		if (left instanceof TablePrimary ||
			left instanceof DerivedTable) {
			generate(left);
		} else {
			append('(');
			generate(left);
			append(')');
		}
		append(' ');
		append(TokenType.FULL);
		append(' ');
		append(TokenType.JOIN);
		append(' ');
		TableReference right = fullOuterJoin.getRight();
		if (right instanceof TablePrimary ||
			right instanceof DerivedTable) {
			generate(right);
		} else {
			append('(');
			generate(right);
			append(')');
		}
		BooleanValueExpression joinCondition = 
				fullOuterJoin.getJoinCondition();
		if (joinCondition != null) {
			append(' ');
			append(TokenType.ON);
			append(' ');
			generateCondition(joinCondition);
		}
	}
	
	protected void generate(CrossJoin crossJoin) {
		TableReference left = crossJoin.getLeft();
		if (left instanceof TablePrimary ||
			left instanceof DerivedTable) {
			generate(left);
		} else {
			append('(');
			generate(left);
			append(')');
		}
		append(' ');
		append(TokenType.CROSS);
		append(' ');
		append(TokenType.JOIN);
		append(' ');
		TableReference right = crossJoin.getRight();
		if (right instanceof TablePrimary ||
			right instanceof DerivedTable) {
			generate(right);
		} else {
			append('(');
			generate(right);
			append(')');
		}
	}
	
	protected void generate(UnionJoin unionJoin) {
		throw Sql4jException.getSql4jException(sourceCode, 
				unionJoin.getBeginIndex(), 
				"Not support union join.");
	}
	
	protected void generate(DerivedTable derivedTable) {
		SelectStatement subquery = derivedTable.getSelectStatement();
		append('(');
		generate(subquery, false);
		append(')');
		append(' ');
		Name correlationName = derivedTable.getCorrelationName();
		generate(correlationName);
	}
	
	protected void generate(TablePrimary tablePrimary) {
		NameChain tableName = tablePrimary.getTableName();
		generate(tableName);
		Name correlationName = 
				tablePrimary.getCorrelationName();
		if (correlationName != null) {
			append(' ');
			generate(correlationName);
		}
	}
	
	protected void generateCondition(
			BooleanValueExpression booleanValueExpression) {
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = 
					(BooleanValue) booleanValueExpression;
			generateCondition(booleanValue);
			return;
		}
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = 
					(Predicate) booleanValueExpression;
			generateCondition(predicate);
			return;
		}
		if (booleanValueExpression instanceof BooleanFactor) {
			BooleanFactor booleanFactor = 
					(BooleanFactor) booleanValueExpression;
			generateCondition(booleanFactor);
			return;
		}
		if (booleanValueExpression instanceof BooleanTerm) {
			BooleanTerm booleanTerm = (BooleanTerm) booleanValueExpression;
			generateCondition(booleanTerm);
			return;
		}
		if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			generateCondition(booleanTest);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, 
				booleanValueExpression.getBeginIndex(), 
				"Not support the boolean value exrepssion.");
	}
	
	protected void generateCondition(Predicate predicate) {
		if (predicate instanceof ComparisonPredicate) {
			ComparisonPredicate comparisonPredicate = 
					(ComparisonPredicate) predicate;
			generateCondition(comparisonPredicate);
			return;
		}
		if (predicate instanceof BetweenPredicate) {
			BetweenPredicate betweenPredicate = 
					(BetweenPredicate) predicate;
			generateCondition(betweenPredicate);
			return;
		}
		if (predicate instanceof DistinctPredicate) {
			DistinctPredicate distinctPredicate = 
					(DistinctPredicate) predicate;
			generateCondition(distinctPredicate);
			return;
		}
		if (predicate instanceof ExistsPredicate) {
			ExistsPredicate existsPredicate = 
					(ExistsPredicate) predicate;
			generateCondition(existsPredicate);
			return;
		}
		if (predicate instanceof InPredicate) {
			InPredicate inPredicate = 
					(InPredicate) predicate;
			generateCondition(inPredicate);
			return;
		}
		if (predicate instanceof LikePredicate) {
			LikePredicate likePredicate = 
					(LikePredicate) predicate;
			generateCondition(likePredicate);
			return;
		}
		if (predicate instanceof MatchPredicate) {
			MatchPredicate matchPredicate = 
					(MatchPredicate) predicate;
			generateCondition(matchPredicate);
			return;
		}
		if (predicate instanceof NullPredicate) {
			NullPredicate nullPredicate = 
					(NullPredicate) predicate;
			generateCondition(nullPredicate);
			return;
		}
		if (predicate instanceof OverlapsPredicate) {
			OverlapsPredicate overlapsPredicate = 
					(OverlapsPredicate) predicate;
			generateCondition(overlapsPredicate);
			return;
		}
		if (predicate instanceof SimilarPredicate) {
			SimilarPredicate similarPredicate = 
					(SimilarPredicate) predicate;
			generateCondition(similarPredicate);
			return;
		}
		if (predicate instanceof UniquePredicate) {
			UniquePredicate uniquePredicate = 
					(UniquePredicate) predicate;
			generateCondition(uniquePredicate);
			return;
		}
		throw Sql4jException.getSql4jException(sourceCode, predicate.getBeginIndex(), 
				"Not support the predicate.");
	}
	
	protected void generateCondition(
			DistinctPredicate distinctPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, 
				distinctPredicate.getBeginIndex(), 
				"Not support distinct predicate.");
	}
	
	protected void generateCondition(
			ExistsPredicate existsPredicate) {
		append(TokenType.EXISTS);
		append(' ');
		Subquery subquery = existsPredicate.getSubquery();
		generate(subquery);
	}
	
	protected void generateCondition(
			LikePredicate likePredicate) {
		ValueExpression valueExpression = 
				likePredicate.getValueExpression();
		generate(valueExpression);
		append(' ');
		boolean not = likePredicate.getNot();
		if (not == true) {
			append(TokenType.NOT);
			append(' ');
		}
		append(TokenType.LIKE);
		append(' ');
		ValueExpression characterPattern = 
				likePredicate.getCharacterPattern();
		generate(characterPattern);
		ValueExpression escapeCharacter = 
				likePredicate.getEscapeCharacter();
		if (escapeCharacter != null) {
			append(' ');
			append(TokenType.ESCAPE);
			append(' ');
			generate(escapeCharacter);
		}
	}
	
	protected void generateCondition(
			MatchPredicate matchPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, 
				matchPredicate.getBeginIndex(), 
				"Not support match predicate.");
	}
	
	protected void generateCondition(
			NullPredicate nullPredicate) {
		ValueExpression valueExpression = 
				nullPredicate.getValueExpression();
		generate(valueExpression);
		append(' ');
		append(TokenType.IS);
		append(' ');
		boolean not = nullPredicate.getNot();
		if (not == true) {
			append(TokenType.NOT);
			append(' ');
		}
		append(TokenType.NULL);
	}
	
	protected void generateCondition(
			OverlapsPredicate overlapsPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, 
				overlapsPredicate.getBeginIndex(), 
				"Not support overlaps predicate.");
	}
	
	protected void generateCondition(
			SimilarPredicate similarPredicate) {
		throw Sql4jException.getSql4jException(sourceCode, 
				similarPredicate.getBeginIndex(), 
				"Not support similar predicate.");
	}
	
	protected void generateCondition(
			UniquePredicate uniquePredicate) {
		throw Sql4jException.getSql4jException(sourceCode, 
				uniquePredicate.getBeginIndex(), 
				"Not support unique predicate.");
	}
	
	protected void generateCondition(InPredicate inPredicate) {
		ValueExpression valueExpression = 
				inPredicate.getValueExpression();
		generate(valueExpression);
		append(' ');
		boolean not = inPredicate.getNot();
		if (not == true) {
			append(TokenType.NOT);
			append(' ');
		}
		append(TokenType.IN);
		append(' ');
		Subquery subquery = inPredicate.getSubquery();
		if (subquery != null) {
			append('(');
			generate(subquery);
			append(')');
		}
		List<ValueExpression> inValueList = 
				inPredicate.getInValueList();
		if (inValueList != null) {
			append('(');
			for (int i = 0; i < inValueList.size(); i++) {
				ValueExpression inValue = inValueList.get(i);
				generate(inValue);
				if (i < inValueList.size() - 1) {
					append(',');
				}
			}
			append(')');
		}
	}
	
	protected void generateCondition(BooleanValue booleanValue) {
		int size = booleanValue.size();
		for (int i = 0; i < size; i++) {
			BooleanTerm booleanTerm = booleanValue.get(i);
			generateCondition(booleanTerm);
			if (i < size - 1) {
				append(' ');
				append(TokenType.OR);
				append(' ');
			}
		}
	}
	
	protected void generateCondition(BooleanTerm booleanTerm) {
		int size = booleanTerm.size();
		for (int i = 0; i < size; i++) {
			BooleanFactor booleanFactor = booleanTerm.get(i);
			generateCondition(booleanFactor);
			if (i < size - 1) {
				append(' ');
				append(TokenType.AND);
				append(' ');
			}
		}
	}
	
	protected void generateCondition(BooleanFactor booleanFactor) {
		boolean not = booleanFactor.getNot();
		if (not == true) {
			append(TokenType.NOT);
			append(' ');
		}
		BooleanValueExpression booleanValueExpression = 
				booleanFactor.getBooleanValueExpression();
		if (booleanValueExpression instanceof Predicate) {
			Predicate predicate = (Predicate) booleanValueExpression;
			generateCondition(predicate);
		} else if (booleanValueExpression instanceof BooleanTest) {
			BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
			generateCondition(booleanTest);
		} else {
			boolean parensAreNeeded = isParensAreNeeded(booleanValueExpression);
			if (parensAreNeeded) {
				append('(');
			}
			generateCondition(booleanValueExpression);
			if (parensAreNeeded) {
				append(')');
			}
		}
	}
	
	private boolean isParensAreNeeded(BooleanValueExpression booleanValueExpression) {
		boolean parensAreNeeded = true;
		if (booleanValueExpression instanceof BooleanValue) {
			BooleanValue booleanValue = (BooleanValue) booleanValueExpression;
			if (booleanValue.size() == 1) {
				BooleanTerm booleanTerm = booleanValue.get(0);
				if (booleanTerm.size() == 1) {
					return false;
				}
			}
		}
		return parensAreNeeded;
	}
	
	protected void generateCondition(BooleanTest booleanTest) {
		BooleanValueExpression booleanValueExpression = 
				booleanTest.getBooleanValueExpression();
		if (!(booleanValueExpression instanceof Predicate)) {
			append('(');
		}
		generateCondition(booleanValueExpression);
		if (!(booleanValueExpression instanceof Predicate)) {
			append(')');
		}
		TruthValue truthValue = booleanTest.getTruthValue();
		if (truthValue != null) {
			append(' ');
			append(TokenType.IS);
			append(' ');
			boolean not = booleanTest.getNot();
			if (not == true) {
				append(TokenType.NOT);
				append(' ');
			}
			if (truthValue == TruthValue.FALSE) {
				append(TokenType.FALSE);
			} else if (truthValue == TruthValue.TRUE) {
				append(TokenType.TRUE);
			} else {
				append(TokenType.UNKNOWN);
			}
		}
	}
	
	protected void generateCondition(ComparisonPredicate comparisonPredicate) {
		ValueExpression left = comparisonPredicate.getLeft();
		generate(left);
		CompOp compOp = comparisonPredicate.getCompOp();
		if (compOp == CompOp.EQUALS) {
			append('=');
		} else if (compOp == CompOp.GREATER_THAN) {
			append('>');
		} else if (compOp == CompOp.GREATER_THAN_OR_EQUALS) {
			append(">=");
		} else if (compOp == CompOp.LESS_THAN) {
			append('<');
		} else if (compOp == CompOp.LESS_THAN_OR_EQUALS) {
			append("<=");
		} else {
			append("<>");
		}
		Quantifier quantifier = comparisonPredicate.getQuantifier();
		if (quantifier != null) {
			append(' ');
			append(quantifier.getContent());
			append(' ');
		}
		ValueExpression right = comparisonPredicate.getRight();
		generate(right);
	}
	
	protected void generateGroupingElementList(List<GroupingElement> groupingElementList) {
		int size = groupingElementList.size();
		for (int i = 0; i < size; i++) {
			GroupingElement groupingElement = 
					groupingElementList.get(i);
			generate(groupingElement);
			if (i < size - 1) {
				append(',');
			}
		}
	}
	
	protected void generate(GroupingElement groupingElement) {
		if (groupingElement instanceof CubeList) {
			CubeList cubeList = (CubeList) groupingElement;
			append("CUBE(");
			int size = cubeList.size();
			for (int i = 0; i < size; i++) {
				GroupingColumnReference groupingColumnReference = 
						cubeList.get(i);
				generate(groupingColumnReference);
				if (i < size - 1) {
					append(',');
				}
			}
			append(')');
		} else if (groupingElement instanceof GrandTotal) {
			append("()");
		} else if (groupingElement instanceof GroupingSetsSpecification) {
			GroupingSetsSpecification groupingSetsSpecification = 
					(GroupingSetsSpecification) groupingElement;
			append(TokenType.GROUPING);
			append(" SETS(");
			int size = groupingSetsSpecification.size();
			for (int i = 0; i < size; i++) {
				GroupingElement groupingElement2 = 
						groupingSetsSpecification.get(i);
				generate(groupingElement2);
				if (i < size - 1) {
					append(',');
				}
			}
			append(')');
		} else if (groupingElement instanceof OrdinaryGroupingSet) {
			OrdinaryGroupingSet ordinaryGroupingSet = 
					(OrdinaryGroupingSet) groupingElement;
			int size = ordinaryGroupingSet.size();
			if (size < 2) {
				GroupingColumnReference groupingColumnReference = 
						ordinaryGroupingSet.get(0);
				generate(groupingColumnReference);
			} else {
				append('(');
				for (int i = 0; i < size; i++) {
					GroupingColumnReference groupingColumnReference = 
							ordinaryGroupingSet.get(i);
					generate(groupingColumnReference);
					if (i < size - 1) {
						append(',');
					}
				}
				append(')');
			}
		} else {
			RollupList rollupList = (RollupList) groupingElement;
			append(TokenType.ROLLUP);
			append('(');
			int size = rollupList.size();
			for (int i = 0; i < size; i++) {
				GroupingColumnReference groupingColumnReference = 
						rollupList.get(i);
				generate(groupingColumnReference);
				if (i < size - 1) {
					append(',');
				}
			}
			append(')');
		}
	}
	
	protected void generate(GroupingColumnReference groupingColumnReference) {
		NameChain columnReference = groupingColumnReference.
				getColumnReference();
		generate(columnReference);
		NameChain collationName = groupingColumnReference.
				getCollationName();
		if (collationName != null) {
			append(' ');
			append(TokenType.COLLATE);
			append(' ');
			generate(collationName);
		}
	}
	
	protected void generateSortSpecificationList(List<SortSpecification> sortSpecificationList) {
		int size = sortSpecificationList.size();
		for (int i = 0; i < size; i++) {
			SortSpecification sortSpecification = 
					sortSpecificationList.get(i);
			generate(sortSpecification);
			if (i < size - 1) {
				append(',');
			}
		}
	}
	
	protected void generate(SortSpecification sortSpecification) {
		ValueExpression sortKey = sortSpecification.getSortKey();
		generate(sortKey);
		OrderingSpecification orderingSpecification = 
				sortSpecification.getOrderingSpecification();
		if (orderingSpecification != null) {
			append(' ');
			if (orderingSpecification == OrderingSpecification.ASC) {
				append("ASC");
			} else {
				append("DESC");
			}
		}
	}
	
	protected StringBuilder append(String s) {
		return buf.append(s);
	}
	
	protected StringBuilder append(char c) {
		return buf.append(c);
	}
	
	protected StringBuilder append(TokenType t) {
		return buf.append(t.toString());
	}
	
	protected List<Object> getParameterValueList() {
		return parameterValueList;
	}
	
	protected List<Integer> getResultNumberList() {
		return resultNumberList;
	}
	
	protected List<String> getResultNameList() {
		return resultNameList;
	}
	
	protected boolean hasArgument(String name) {
		return argument.containsName(name);
	}
	
	protected Object getArgument(String name) {
		return argument.getValue(name);
	}
	
	protected void addSemifinishedStatement(
			SemifinishedStatement semifinishedStatement) {
		semifinishedStatements.add(semifinishedStatement);
	}
	
	protected Sql4jException getSql4jException(SourceCode sourceCode, int index, String msg) {
		IndexableMessage message = new IndexableMessage(
				index, msg);
		List<IndexableMessage> list = 
				new ArrayList<IndexableMessage>(1);
		list.add(message);
		return new Sql4jException(sourceCode, list, (Exception) null);
	}
	
	protected Sql4jException getSql4jException(SourceCode sourceCode, int index, String msg, Throwable t) {
		IndexableMessage message = new IndexableMessage(
				index, msg);
		List<IndexableMessage> list = 
				new ArrayList<IndexableMessage>(1);
		list.add(message);
		return new Sql4jException(sourceCode, list, t);
	}
	
	protected PageInformation getPageInfo(Page page) {
		BigInteger beginIndex = null;
		BigInteger endIndex = null;
		Parameter fromParameter = page.getFromParameter();
		NumericLiteral fromNumericLiteral = page.getFromNumericLiteral();
		if (fromParameter != null ||
			fromNumericLiteral != null) {
			if (fromParameter != null) {
				String fromParameterName = fromParameter.getContent();
				if (!argument.containsName(fromParameterName)) {
					throw Sql4jException.getSql4jException(sourceCode, 
							fromParameter.getBeginIndex(), 
							"Parameter '" + fromParameterName + "' not found.");
				}
				Object fromParameterValue = argument.getValue(fromParameterName);
				try {
					beginIndex = new BigInteger(fromParameterValue.toString());
				} catch (Exception e) {
					throw Sql4jException.getSql4jException(sourceCode, 
							fromParameter.getBeginIndex(), 
							"Can't convert to java.math.BigInteger, " + fromParameterValue);
				}
				if (beginIndex.compareTo(BigInteger.ZERO) < 0) {
					throw Sql4jException.getSql4jException(sourceCode, 
							fromParameter.getBeginIndex(), 
							"Can't be less than 0, " + fromParameterValue);
				}
			} else {
				try {
					beginIndex = new BigInteger(fromNumericLiteral.toString());
				} catch (Exception e) {
					throw Sql4jException.getSql4jException(sourceCode, 
							fromNumericLiteral.getBeginIndex(), 
							"Can't convert to java.math.BigInteger.");
				}
				if (beginIndex.compareTo(BigInteger.ZERO) < 0) {
					throw Sql4jException.getSql4jException(sourceCode, 
							fromNumericLiteral.getBeginIndex(), 
							"Can't be less than 0.");
				}
			}
			Parameter toParameter = page.getToParameter();
			NumericLiteral toNumericLiteral = page.getToNumericLiteral();
			if (toParameter != null ||
				toNumericLiteral != null) {
				if (toParameter != null) {
					String toParameterName = toParameter.getContent();
					if (!argument.containsName(toParameterName)) {
						throw Sql4jException.getSql4jException(sourceCode, 
								toParameter.getBeginIndex(), 
								"Parameter '" + toParameterName + "' not found.");
					}
					Object toParameterValue = argument.getValue(toParameterName);
					try {
						endIndex = new BigInteger(toParameterValue.toString());
					} catch (Exception e) {
						throw Sql4jException.getSql4jException(sourceCode, 
								toParameter.getBeginIndex(), 
								"Can't convert to java.math.BigInteger, " + toParameterValue);
					}
					if (endIndex.compareTo(beginIndex) <= 0) {
						throw Sql4jException.getSql4jException(sourceCode, 
								toParameter.getBeginIndex(), 
								"The to can't be less than or equals the from, " + toParameterValue);
					}
				} else {
					try {
						endIndex = new BigInteger(toNumericLiteral.toString());
					} catch (Exception e) {
						throw Sql4jException.getSql4jException(sourceCode, 
								toNumericLiteral.getBeginIndex(), 
								"Can't convert to java.math.BigInteger.");
					}
					if (endIndex.compareTo(beginIndex) <= 0) {
						throw Sql4jException.getSql4jException(sourceCode, 
								toNumericLiteral.getBeginIndex(), 
								"The to can't be less than or equals the from.");
					}
				}
			}
		} else {
			Parameter capacityParameter = page.getCapacityParameter();
			NumericLiteral capacityNumericLiteral = page.getCapacityNumericLiteral();
			BigInteger capacity;
			BigInteger index;
			if (capacityParameter != null) {
				String capacityParameterName = capacityParameter.getContent();
				if (!argument.containsName(capacityParameterName)) {
					throw Sql4jException.getSql4jException(sourceCode, 
							capacityParameter.getBeginIndex(), 
							"Parameter '" + capacityParameterName + "' not found.");
				}
				Object capacityParameterValue = argument.getValue(capacityParameterName);
				try {
					capacity = new BigInteger(capacityParameterValue.toString());
				} catch (Exception e) {
					throw Sql4jException.getSql4jException(sourceCode, 
							capacityParameter.getBeginIndex(), 
							"Can't convert to java.math.BigInteger, " + capacityParameterValue);
				}
				if (capacity.compareTo(new BigInteger("1")) < 0) {
					throw Sql4jException.getSql4jException(sourceCode, 
							capacityParameter.getBeginIndex(), 
							"Can't be less than 1, " + capacityParameterValue);
				}
			} else {
				try {
					capacity = new BigInteger(capacityNumericLiteral.toString());
				} catch (Exception e) {
					throw Sql4jException.getSql4jException(sourceCode, 
							capacityNumericLiteral.getBeginIndex(), 
							"Can't convert to java.math.BigInteger.");
				}
				if (capacity.compareTo(new BigInteger("1")) < 0) {
					throw Sql4jException.getSql4jException(sourceCode, 
							capacityNumericLiteral.getBeginIndex(), 
							"Can't be less than 1.");
				}
			}
			Parameter indexParameter = page.getIndexParameter();
			NumericLiteral indexNumericLiteral = page.getIndexNumericLiteral();
			if (indexParameter != null) {
				String indexParameterName = indexParameter.getContent();
				if (!argument.containsName(indexParameterName)) {
					throw Sql4jException.getSql4jException(sourceCode, 
							indexParameter.getBeginIndex(), 
							"Parameter '" + indexParameterName + "' not found.");
				}
				Object indexParameterValue = argument.getValue(indexParameterName);
				try {
					index = new BigInteger(indexParameterValue.toString());
				} catch (Exception e) {
					throw Sql4jException.getSql4jException(sourceCode, 
							indexParameter.getBeginIndex(), 
							"Can't convert to java.math.BigInteger, " + indexParameterValue);
				}
				if (capacity.compareTo(BigInteger.ZERO) < 0) {
					throw Sql4jException.getSql4jException(sourceCode, 
							indexParameter.getBeginIndex(), 
							"Can't be less than 0, " + indexParameterValue);
				}
			} else {
				try {
					index = new BigInteger(indexNumericLiteral.toString());
				} catch (Exception e) {
					throw Sql4jException.getSql4jException(sourceCode, 
							indexNumericLiteral.getBeginIndex(), 
							"Can't convert to java.math.BigInteger, " + indexNumericLiteral);
				}
				if (index.compareTo(new BigInteger("0")) < 0) {
					throw Sql4jException.getSql4jException(sourceCode, 
							indexNumericLiteral.getBeginIndex(), 
							"Can't be less than 0.");
				}
			}
			beginIndex = capacity.multiply(index);
			endIndex = index.add(BigInteger.ONE).multiply(capacity);
		}
		PageInformation pageInfo = new PageInformation(beginIndex, endIndex);
		return pageInfo;
	}
	
	protected void generateCurrentTimestamp() {
		append("===TODO===");
	}
	
	protected abstract String toSqlString(String javaString);
	
	private Object getHashValue(NameChain hashColumn, ValueExpression hashColumnValue) {
		if (hashColumnValue instanceof Parameter) {
			Parameter parameter = (Parameter) hashColumnValue;
			String parameterName = parameter.getContent();
			if (!argument.containsName(parameterName)) {
				throw Sql4jException.getSql4jException(sourceCode, parameter.getBeginIndex(), 
						"Parameter not found.");
			}
			HashUtil.checkHashColumnAndHashValue(configuration, sourceCode, 
					hashColumn, parameter, argument);
			Object value = argument.getValue(parameter.getContent());
			return value;
		}
		HashUtil.checkHashColumnAndHashValue(configuration, sourceCode, 
				hashColumn, hashColumnValue);
		if (hashColumnValue instanceof StringLiteral) {
			StringLiteral stringLiteral = (StringLiteral) hashColumnValue;
			return stringLiteral.getContent();
		}
		if (hashColumnValue instanceof NumericLiteral) {
			NumericLiteral numericLiteral = (NumericLiteral) hashColumnValue;
			return numericLiteral.getContent();
		}
		throw Sql4jException.getSql4jException(sourceCode, hashColumnValue.getBeginIndex(), 
				"It is not supported to use this value expression as hash value.");
	}
	
	protected String getArgumentPk() {
		String pk;
		if (argument.containsName("pk") && argument.getValue("pk") != null) {
			pk = String.valueOf(argument.getValue("pk"));
		} else {
			pk = IdGenerator.generateUUID();
			argument.setValue("pk", pk);
		}
		pk = toSqlString(pk);
		return pk;
	}

}
