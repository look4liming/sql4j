package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.Sql4jException;
import lee.bright.sql4j.conf.Configuration;

/**
 * @author Bright Lee
 */
public final class Parser {
	
	private Configuration configuration;
	private Scanner scanner;
	
	public Parser(Configuration configuration, 
			SourceCode sourceCode) {
		this.configuration = configuration;
		scanner = new Scanner(sourceCode);
		scanner.scan();
	}
	
	public SourceCode getSourceCode() {
		return scanner.getSourceCode();
	}
	
	public Statement parse() {
		if (scanner.getTokenType() == null) {
			return null;
		}
		if (scanner.getTokenType() == TokenType.SELECT) {
			SelectStatement selectStatement = parseSelectStatement();
			return selectStatement;
		}
		if (scanner.getTokenType() == TokenType.UPDATE) {
			UpdateStatement updateStatement = parseUpdateStatement();
			return updateStatement;
		}
		if (scanner.getTokenType() == TokenType.DELETE) {
			DeleteStatement deleteStatement = parseDeleteStatement();
			return deleteStatement;
		}
		if (scanner.getTokenType() == TokenType.INSERT) {
			InsertStatement insertStatement = parseInsertStatement();
			return insertStatement;
		}
		if (scanner.getTokenType() == TokenType.CREATE) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() == TokenType.TABLE) {
				scanner.scan();
				TableDefinition tableDefinition = parseTableDefinition(beginIndex);
				return tableDefinition;
			}
			boolean unique = false;
			if (scanner.getTokenType() == TokenType.UNIQUE) {
				unique = true;
				scanner.scan();
			}
			if (scanner.getTokenType() == TokenType.INDEX) {
				scanner.scan();
				CreateIndexStatement createIndexStatement = 
						parseCreateIndexStatement(beginIndex, unique);
				return createIndexStatement;
			}
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
					"This 'CREATE' statement is not supported.");
		}
		if (scanner.getTokenType() == TokenType.DROP) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() == TokenType.TABLE) {
				scanner.scan();
				DropTableStatement dropTableStatement = parseDropTableStatement(beginIndex);
				return dropTableStatement;
			}
			if (scanner.getTokenType() == TokenType.INDEX) {
				scanner.scan();
				DropIndexStatement dropIndexStatement = parseDropIndexStatement(beginIndex);
				return dropIndexStatement;
			}
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
					"This 'DROP' statement is not supported.");
		}
		if (scanner.getTokenType() == TokenType.ALTER) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() != TokenType.TABLE) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
						"'TABLE' expected.");
			}
			scanner.scan();
			Statement statement = parseAlterTableStatement(beginIndex);
			return statement;
		}
		if (scanner.getTokenType() == TokenType.CALL) {
			CallStatement callStatement = 
					parseCallStatement();
			return callStatement;
		}
		throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
				"Error token: " + scanner.getTokenType());
	}
	
	private SelectStatement parseSelectStatement() {
		SelectStatement selectStatement = parseIntersect();
		int beginIndex_ = selectStatement.getBeginIndex();
		TokenType tokenType = scanner.getTokenType();
		int beginIndex = scanner.getBeginIndex();
		if (tokenType != TokenType.UNION &&
			tokenType != TokenType.EXCEPT) {
			if (scanner.getTokenType() != TokenType._SEMICOLON_ &&
				scanner.getTokenType() != null) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"';' expected here.");
			}
			scanner.scan();
			return selectStatement;
		}
		scanner.scan();
		boolean all = false;
		boolean distinct = false;
		if (scanner.getTokenType() == TokenType.ALL) {
			all = true;
			scanner.scan();
		} else if (scanner.getTokenType() == TokenType.DISTINCT) {
			distinct = true;
			scanner.scan();
		}
		List<NameChain> correspondingColumnList = null;
		if (scanner.getTokenType() == TokenType.CORRESPONDING) {
			scanner.scan();
			if (scanner.getTokenType() == TokenType.BY) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'(' expected here.");
				}
				scanner.scan();
				correspondingColumnList = new ArrayList<NameChain>();
				while (true) {
					NameChain correspondingColumn = parseNameChain(false);
					correspondingColumnList.add(correspondingColumn);
					if (scanner.getTokenType() != TokenType._COMMA_) {
						break;
					}
					scanner.scan();
				}
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
			}
		}
		SelectStatement right = null;
		if (scanner.getTokenType() == TokenType._LEFT_PAREN_) {
			scanner.scan();
			right = parseSelectStatement();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
		} else if (scanner.getTokenType() == TokenType.SELECT) {
			right = parseIntersect();
		} else {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Select statement expected here.");
		}
		if (tokenType == TokenType.UNION) {
			selectStatement = new Union(scanner.getSourceCode(), beginIndex_, 
					selectStatement, all, distinct, correspondingColumnList, right);
		} else if (tokenType == TokenType.EXCEPT) {
			selectStatement = new Except(scanner.getSourceCode(), beginIndex_,
					selectStatement, all, distinct, 
					correspondingColumnList, right);
		} else {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
					"Error token.");
		}
		while (true) {
			if (scanner.getTokenType() != TokenType.UNION &&
				scanner.getTokenType() != TokenType.EXCEPT) {
				break;
			}
			tokenType = scanner.getTokenType();
			beginIndex = scanner.getBeginIndex();
			scanner.scan();
			all = false;
			distinct = false;
			if (scanner.getTokenType() == TokenType.ALL) {
				all = true;
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType.DISTINCT) {
				distinct = true;
				scanner.scan();
			}
			correspondingColumnList = null;
			if (scanner.getTokenType() == TokenType.CORRESPONDING) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType.BY) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'BY' expected here.");
				}
				scanner.scan();
				correspondingColumnList = new ArrayList<NameChain>();
				while (true) {
					NameChain correspondingColumn = parseNameChain(false);
					correspondingColumnList.add(correspondingColumn);
					if (scanner.getTokenType() != TokenType._COMMA_) {
						break;
					}
					scanner.scan();
				}
			}
			right = null;
			if (scanner.getTokenType() == TokenType._LEFT_PAREN_) {
				scanner.scan();
				right = parseSelectStatement();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType.SELECT) {
				right = parseIntersect();
			} else {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Select statement expected here.");
			}
			if (tokenType == TokenType.UNION) {
				selectStatement = new Union(scanner.getSourceCode(), beginIndex_, 
						selectStatement, all, distinct, correspondingColumnList, right);
			} else if (tokenType == TokenType.EXCEPT) {
				selectStatement = new Except(scanner.getSourceCode(), beginIndex_, selectStatement, 
						all, distinct, correspondingColumnList, right);
			} else {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
						"Error token.");
			}
		}
		if (scanner.getTokenType() != TokenType._SEMICOLON_ &&
			scanner.getTokenType() != null) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"';' expected here.");
		}
		scanner.scan();
		return selectStatement;
	}
	
	private SelectStatement parseIntersect() {
		QuerySpecification querySpecification = 
				parseQuerySpecification();
		int beginIndex = querySpecification.getBeginIndex();
		if (scanner.getTokenType() != TokenType.INTERSECT) {
			return querySpecification;
		}
		scanner.scan();
		boolean all = false;
		boolean distinct = false;
		if (scanner.getTokenType() == TokenType.ALL) {
			all = true;
			scanner.scan();
		} else if (scanner.getTokenType() == TokenType.DISTINCT) {
			distinct = true;
			scanner.scan();
		}
		List<NameChain> correspondingColumnList = null;
		if (scanner.getTokenType() == TokenType.CORRESPONDING) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType.BY) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'BY' expected here.");
			}
			scanner.scan();
			correspondingColumnList = new ArrayList<NameChain>();
			while (true) {
				NameChain correspondingColumn = parseNameChain(false);
				correspondingColumnList.add(correspondingColumn);
				if (scanner.getTokenType() != TokenType._COMMA_) {
					break;
				}
				scanner.scan();
			}
		}
		SelectStatement right = null;
		if (scanner.getTokenType() == TokenType._LEFT_PAREN_) {
			scanner.scan();
			right = parseSelectStatement();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
		} else if (scanner.getTokenType() == TokenType.SELECT) {
			right = parseQuerySpecification();
		} else {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Select statement expected here.");
		}
		Page page = parsePage();
		Intersect intersect = new Intersect(scanner.getSourceCode(), beginIndex, 
				querySpecification, all, distinct, correspondingColumnList, right, page);
		while (true) {
			if (scanner.getTokenType() != TokenType.INTERSECT) {
				break;
			}
			scanner.scan();
			all = false;
			distinct = false;
			if (scanner.getTokenType() == TokenType.ALL) {
				all = true;
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType.DISTINCT) {
				distinct = true;
				scanner.scan();
			}
			correspondingColumnList = null;
			if (scanner.getTokenType() == TokenType.CORRESPONDING) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType.BY) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'BY' expected here.");
				}
				scanner.scan();
				correspondingColumnList = new ArrayList<NameChain>();
				while (true) {
					NameChain correspondingColumn = parseNameChain(false);
					correspondingColumnList.add(correspondingColumn);
					if (scanner.getTokenType() != TokenType._COMMA_) {
						break;
					}
					scanner.scan();
				}
			}
			right = null;
			if (scanner.getTokenType() == TokenType._LEFT_PAREN_) {
				scanner.scan();
				right = parseSelectStatement();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
			}
			page = parsePage();
			intersect = new Intersect(scanner.getSourceCode(), beginIndex, 
					intersect, all, distinct, correspondingColumnList, right, page);
		}
		return intersect;
	}
	
	private QuerySpecification parseQuerySpecification() {
		if (scanner.getTokenType() != TokenType.SELECT) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'SELECT' expected here.");
		}
		int beginIndex = scanner.getBeginIndex();
		scanner.scan();
		SetQuantifier setQuantifier;
		if (scanner.getTokenType() == TokenType.DISTINCT) {
			setQuantifier = new SetQuantifier(
					scanner.getBeginIndex(), 
					scanner.getEndIndex(), true);
			scanner.scan();
		} else if (scanner.getTokenType() == TokenType.ALL) {
			setQuantifier = new SetQuantifier(
					scanner.getBeginIndex(), 
					scanner.getEndIndex(), false);
			scanner.scan();
		} else {
			setQuantifier = null;
		}
		List<SelectSublist> selectList = parseSelectList();
		if (scanner.getTokenType() != TokenType.FROM) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'FROM' expected here.");
		}
		scanner.scan();
		List<TableReference> tableReferenceList = 
				parseTableReferenceList();
		BooleanValueExpression whereSearchCondition = null;
		if (scanner.getTokenType() == TokenType.WHERE) {
			scanner.scan();
			whereSearchCondition = parseBooleanValueExpression();
		}
		List<GroupingElement> groupingElementList = null;
		if (scanner.getTokenType() == TokenType.GROUP) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType.BY) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'BY' expected here.");
			}
			scanner.scan();
			groupingElementList = parseGroupingElementList();
		}
		BooleanValueExpression havingSearchCondition = null;
		if (scanner.getTokenType() == TokenType.HAVING) {
			scanner.scan();
			havingSearchCondition = parseBooleanValueExpression();
		}
		List<SortSpecification> sortSpecificationList = null;
		if (scanner.getTokenType() == TokenType.ORDER) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType.BY) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'BY' expected here.");
			}
			scanner.scan();
			sortSpecificationList = parseSortSpecificationList();
		}
		Page page = parsePage();
		if (page != null) {
			if (sortSpecificationList == null || 
				sortSpecificationList.isEmpty()) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), page.getBeginIndex(), 
						"The statement lack of order by clause, because it contains page clause.");
			}
		}
		QuerySpecification querySpecification = 
				new QuerySpecification(scanner.getSourceCode(), 
						beginIndex, setQuantifier,
						selectList, tableReferenceList,
						whereSearchCondition,
						groupingElementList,
						havingSearchCondition,
						sortSpecificationList,
						page);
		return querySpecification;
	}
	
	private Page parsePage() {
		if (scanner.getTokenType() != TokenType.PAGE) {
			return null;
		}
		int beginIndex = scanner.getBeginIndex();
		scanner.scan();
		if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'(' expected here.");
		}
		scanner.scan();
		if (scanner.getTokenType() == TokenType.FROM) {
			scanner.scan();
			NumericLiteral fromNumericLiteral = null;
			Parameter fromParameter = null;
			NumericLiteral toNumericLiteral = null;
			Parameter toParameter = null;
			NumericLiteral capacityNumericLiteral = null;
			Parameter capacityParameter = null;
			NumericLiteral numberNumericLiteral = null;
			Parameter numberParameter = null;
			if (scanner.getTokenType() == TokenType._NUM_) {
				fromNumericLiteral = new NumericLiteral(
						scanner.getBeginIndex(), 
						scanner.getEndIndex(), 
						scanner.getContent());
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType._PARAM_) {
				fromParameter = new Parameter(scanner.getBeginIndex(), 
						scanner.getEndIndex(), scanner.getContent());
				scanner.scan();
			} else {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Numeric literal or parameter expected here.");
			}
			if (scanner.getTokenType() != TokenType.TO) {
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Page page = new Page(beginIndex, fromNumericLiteral, fromParameter,	
						toNumericLiteral, toParameter, 
						capacityNumericLiteral, capacityParameter, 
						numberNumericLiteral, numberParameter);
				return page;
			}
			scanner.scan();
			if (scanner.getTokenType() == TokenType._NUM_) {
				toNumericLiteral = new NumericLiteral(
						scanner.getBeginIndex(), 
						scanner.getEndIndex(), 
						scanner.getContent());
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType._PARAM_) {
				toParameter = new Parameter(scanner.getBeginIndex(), 
						scanner.getEndIndex(), scanner.getContent());
				scanner.scan();
			} else {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Numeric literal or parameter expected here.");
			}
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			Page page = new Page(beginIndex, fromNumericLiteral, fromParameter,	
					toNumericLiteral, toParameter, 
					capacityNumericLiteral, capacityParameter, 
					numberNumericLiteral, numberParameter);
			return page;
		} else if (scanner.getTokenType() == TokenType._ID_ &&
			"CAPACITY".equalsIgnoreCase(scanner.getContent())) {
			scanner.scan();
			NumericLiteral fromNumericLiteral = null;
			Parameter fromParameter = null;
			NumericLiteral toNumericLiteral = null;
			Parameter toParameter = null;
			NumericLiteral capacityNumericLiteral = null;
			Parameter capacityParameter = null;
			NumericLiteral numberNumericLiteral = null;
			Parameter numberParameter = null;
			if (scanner.getTokenType() == TokenType._NUM_) {
				capacityNumericLiteral = new NumericLiteral(
						scanner.getBeginIndex(), 
						scanner.getEndIndex(), 
						scanner.getContent());
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType._PARAM_) {
				capacityParameter = new Parameter(scanner.getBeginIndex(), 
						scanner.getEndIndex(), scanner.getContent());
				scanner.scan();
			} else {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Numeric literal or parameter expected here.");
			}
			if (scanner.getTokenType() != TokenType.INDEX) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'index' expected here.");
			}
			scanner.scan();
			if (scanner.getTokenType() == TokenType._NUM_) {
				numberNumericLiteral = new NumericLiteral(
						scanner.getBeginIndex(), 
						scanner.getEndIndex(), 
						scanner.getContent());
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType._PARAM_) {
				numberParameter = new Parameter(scanner.getBeginIndex(), 
						scanner.getEndIndex(), scanner.getContent());
				scanner.scan();
			} else {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Numeric literal or parameter expected here.");
			}
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			Page page = new Page(beginIndex, fromNumericLiteral, fromParameter,	
					toNumericLiteral, toParameter, 
					capacityNumericLiteral, capacityParameter, 
					numberNumericLiteral, numberParameter);
			return page;
		} else {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'FROM' or 'CAPACITY' expected here.");
		}
	}
	
	private List<SortSpecification> parseSortSpecificationList() {
		List<SortSpecification> list = 
				new ArrayList<SortSpecification>();
		while (true) {
			SortSpecification sortSpecification = 
					parseSortSpecification();
			list.add(sortSpecification);
			if (scanner.getTokenType() != TokenType._COMMA_) {
				break;
			}
			scanner.scan();
		}
		return list;
	}
	
	private SortSpecification parseSortSpecification() {
		ValueExpression sortKey = parseValueExpression();
		OrderingSpecification orderingSpecification;
		if (scanner.getTokenType() == TokenType._ID_) {
			String content = scanner.getContent();
			if ("ASC".equalsIgnoreCase(content)) {
				orderingSpecification = OrderingSpecification.ASC;
				scanner.scan();
			} else if ("DESC".equalsIgnoreCase(content)) {
				orderingSpecification = OrderingSpecification.DESC;
				scanner.scan();
			} else {
				orderingSpecification = null;
			}
		} else {
			orderingSpecification = null;
		}
		SortSpecification sortSpecification = new SortSpecification(
				sortKey, orderingSpecification);
		return sortSpecification;
	}
	
	private List<GroupingElement> parseGroupingElementList() {
		List<GroupingElement> list = 
				new ArrayList<GroupingElement>();
		while (true) {
			GroupingElement groupingElement = parseGroupingElement();
			list.add(groupingElement);
			if (scanner.getTokenType() != TokenType._COMMA_) {
				break;
			}
			scanner.scan();
		}
		return list;
	}
	
	private GroupingElement parseGroupingElement() {
		if (scanner.getTokenType() == TokenType._ID_) {
			int beginIndex = scanner.getBeginIndex();
			GroupingColumnReference groupingColumnReference = 
					parseGroupingColumnReference();
			OrdinaryGroupingSet ordinaryGroupingSet = 
					new OrdinaryGroupingSet(beginIndex, groupingColumnReference);
			return ordinaryGroupingSet;
		}
		if (scanner.getTokenType() == TokenType._LEFT_PAREN_) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() == TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
						"Grand total is not supported.");
			}
			List<GroupingColumnReference> list = 
					parseGroupingColumnReferenceList();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			OrdinaryGroupingSet ordinaryGroupingSet = 
					new OrdinaryGroupingSet(beginIndex, list);
			return ordinaryGroupingSet;
		}
		if (scanner.getTokenType() == TokenType.ROLLUP) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			scanner.scan();
			List<GroupingColumnReference> list = 
					parseGroupingColumnReferenceList();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			RollupList rollupList = new RollupList(beginIndex, list);
			return rollupList;
		}
		if (scanner.getTokenType() == TokenType.CUBE) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			scanner.scan();
			List<GroupingColumnReference> list = 
					parseGroupingColumnReferenceList();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			CubeList cubeList = new CubeList(beginIndex, list);
			return cubeList;
		}
		if (scanner.getTokenType() == TokenType.GROUPING) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() != TokenType._ID_ && 
				!"SETS".equalsIgnoreCase(scanner.getContent())) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'SETS' expected here.");
			}
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			scanner.scan();
			List<GroupingElement> groupingElementList = 
					parseGroupingElementList();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			GroupingSetsSpecification groupingSetsSpecification = 
					new GroupingSetsSpecification(beginIndex, groupingElementList);
			return groupingSetsSpecification;
		}
		throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
				"Identifier, '(', 'ROLLUP', or 'CUBE' expected here.");
	}
	
	private List<GroupingColumnReference> parseGroupingColumnReferenceList() {
		List<GroupingColumnReference> list = 
				new ArrayList<GroupingColumnReference>();
		while (true) {
			GroupingColumnReference groupingColumnReference = 
					parseGroupingColumnReference();
			list.add(groupingColumnReference);
			if (scanner.getTokenType() != TokenType._COMMA_) {
				break;
			}
			scanner.scan();
		}
		return list;
	}
	
	private GroupingColumnReference parseGroupingColumnReference() {
		NameChain columnReference = parseNameChain(false);
		NameChain collationName = null;
		if (scanner.getTokenType() == TokenType.COLLATE) {
			scanner.scan();
			collationName = parseNameChain(false);
		}
		GroupingColumnReference groupingColumnReference = 
				new GroupingColumnReference(columnReference, 
						collationName);
		return groupingColumnReference;
	}
	
	private List<TableReference> parseTableReferenceList() {
		List<TableReference> list = 
				new ArrayList<TableReference>();
		while (true) {
			TableReference tableReference = 
					parseTableReference();
			list.add(tableReference);
			if (scanner.getTokenType() != TokenType._COMMA_) {
				break;
			}
			scanner.scan();
		}
		return list;
	}
	
	private TableReference parseTableReference() {
		TableReference tableReference = parseTablePrimary();
		int beginIndex = tableReference.getBeginIndex();
		TableReference join = parseJoin(beginIndex, 
				tableReference);
		return join;
	}
	
	private TableReference parseTablePrimary() {
		TableReference tableReference = null;
		if (scanner.getTokenType() == TokenType._ID_) {
			int beginIndex = scanner.getBeginIndex();
			NameChain tableName = parseNameChain(true);
			Name correlationName;
			if (scanner.getTokenType() == TokenType.AS) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType._ID_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"Correlation name expected here.");
				}
				correlationName = new Name(scanner.getBeginIndex(), 
						scanner.getEndIndex(), scanner.getContent());
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType._ID_) {
				correlationName = new Name(scanner.getBeginIndex(), 
						scanner.getEndIndex(), scanner.getContent());
				scanner.scan();
			} else {
				correlationName = null;
			}
			tableReference = new TablePrimary(beginIndex, 
					tableName, correlationName);
		} else if (scanner.getTokenType() == TokenType._LEFT_PAREN_) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			Subquery subquery = null;
			if (scanner.getTokenType() == TokenType.SELECT) {
				subquery = parseSubquery(beginIndex);
			} else {
				tableReference = parseTableReference();
			}
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			if (subquery != null) {
				if (scanner.getTokenType() == TokenType.AS) {
					scanner.scan();
				}
				if (scanner.getTokenType() != TokenType._ID_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"Correlation name expected here.");
				}
				Name correlationName = new Name(scanner.getBeginIndex(), 
						scanner.getEndIndex(), scanner.getContent());
				tableReference = new DerivedTable(beginIndex, 
						subquery.getSelectStatement(), correlationName);
				scanner.scan();
			}
		} else {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Error table reference.");
		}
		return tableReference;
	}
	
	private TableReference parseJoin(int beginIndex, 
			TableReference tableReference) {
		TableReference join = null;
		if (scanner.getTokenType() == TokenType.CROSS) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType.JOIN) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'JOIN' expected here.");
			}
			scanner.scan();
			TableReference tablePrimary = parseTablePrimary();
			join = new CrossJoin(beginIndex, tableReference, tablePrimary);
		} else if (scanner.getTokenType() == TokenType.INNER) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType.JOIN) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'JOIN' expected here.");
			}
			scanner.scan();
			TableReference right = parseTableReference();
			if (scanner.getTokenType() != TokenType.ON) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'ON' expected here.");
			}
			scanner.scan();
			BooleanValueExpression joinCondition = 
					parseBooleanValueExpression();
			join = new InnerJoin(beginIndex, tableReference, 
					right, joinCondition);
		} else if (scanner.getTokenType() == TokenType.LEFT) {
			scanner.scan();
			if (scanner.getTokenType() == TokenType.OUTER) {
				scanner.scan();
			}
			if (scanner.getTokenType() != TokenType.JOIN) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'JOIN' expected here.");
			}
			scanner.scan();
			TableReference right = parseTableReference();
			if (scanner.getTokenType() != TokenType.ON) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'ON' expected here.");
			}
			scanner.scan();
			BooleanValueExpression joinCondition = 
					parseBooleanValueExpression();
			join = new LeftOuterJoin(beginIndex, tableReference, 
					right, joinCondition);
		} else if (scanner.getTokenType() == TokenType.RIGHT) {
			scanner.scan();
			if (scanner.getTokenType() == TokenType.OUTER) {
				scanner.scan();
			}
			if (scanner.getTokenType() != TokenType.JOIN) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'JOIN' expected here.");
			}
			scanner.scan();
			TableReference right = parseTableReference();
			if (scanner.getTokenType() != TokenType.ON) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'ON' expected here.");
			}
			scanner.scan();
			BooleanValueExpression joinCondition = 
					parseBooleanValueExpression();
			join = new RightOuterJoin(beginIndex, tableReference, 
					right, joinCondition);
		} else if (scanner.getTokenType() == TokenType.FULL) {
			scanner.scan();
			if (scanner.getTokenType() == TokenType.OUTER) {
				scanner.scan();
			}
			if (scanner.getTokenType() != TokenType.JOIN) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'JOIN' expected here.");
			}
			scanner.scan();
			TableReference right = parseTableReference();
			if (scanner.getTokenType() != TokenType.ON) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'ON' expected here.");
			}
			scanner.scan();
			BooleanValueExpression joinCondition = 
					parseBooleanValueExpression();
			join = new FullOuterJoin(beginIndex, tableReference, 
					right, joinCondition);
		} else if (scanner.getTokenType() == TokenType.NATURAL) {
			scanner.scan();
			if (scanner.getTokenType() == TokenType.INNER) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType.JOIN) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'JOIN' expected here.");
				}
				scanner.scan();
				TableReference right = parseTableReference();
				join = new NaturalInnerJoin(beginIndex, 
						tableReference, right);
			} else if (scanner.getTokenType() == TokenType.LEFT) {
				scanner.scan();
				if (scanner.getTokenType() == TokenType.OUTER) {
					scanner.scan();
				}
				if (scanner.getTokenType() != TokenType.JOIN) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'JOIN' expected here.");
				}
				scanner.scan();
				TableReference right = parseTableReference();
				join = new NaturalLeftOuterJoin(beginIndex, 
						tableReference, right);
			} else if (scanner.getTokenType() == TokenType.RIGHT) {
				scanner.scan();
				if (scanner.getTokenType() == TokenType.OUTER) {
					scanner.scan();
				}
				if (scanner.getTokenType() != TokenType.JOIN) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'JOIN' expected here.");
				}
				scanner.scan();
				TableReference right = parseTableReference();
				join = new NaturalRightOuterJoin(beginIndex, 
						tableReference, right);
			} else if (scanner.getTokenType() == TokenType.FULL) {
				scanner.scan();
				if (scanner.getTokenType() == TokenType.OUTER) {
					scanner.scan();
				}
				if (scanner.getTokenType() != TokenType.JOIN) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'JOIN' expected here.");
				}
				scanner.scan();
				TableReference right = parseTableReference();
				join = new NaturalFullOuterJoin(beginIndex, 
						tableReference, right);
			} else {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'INNER', 'LEFT', 'RIGHT' or 'FULL' expected here.");
			}
		} else {
			return tableReference;
		}
		join = parseJoin(join.getBeginIndex(), join);
		return join;
	}
	
	private Subquery parseSubquery(int subqueryBeginIndex) {
		int beginIndex = scanner.getBeginIndex();
		SelectStatement selectStatement = parseIntersect();
		TokenType tokenType = scanner.getTokenType();
		if (tokenType != TokenType.UNION &&
			tokenType != TokenType.EXCEPT) {
			int endIndex = scanner.getBeginIndex();
			Subquery subquery = new Subquery(subqueryBeginIndex, 
					endIndex, selectStatement);
			return subquery;
		}
		scanner.scan();
		boolean all = false;
		boolean distinct = false;
		if (scanner.getTokenType() == TokenType.ALL) {
			all = true;
			scanner.scan();
		} else if (scanner.getTokenType() == TokenType.DISTINCT) {
			distinct = true;
			scanner.scan();
		}
		List<NameChain> correspondingColumnList = null;
		if (scanner.getTokenType() == TokenType.CORRESPONDING) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType.BY) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'BY' expected here.");
			}
			scanner.scan();
			correspondingColumnList = new ArrayList<NameChain>();
			while (true) {
				NameChain correspondingColumn = parseNameChain(false);
				correspondingColumnList.add(correspondingColumn);
				if (scanner.getTokenType() != TokenType._COMMA_) {
					break;
				}
				scanner.scan();
			}
		}
		SelectStatement right = null;
		if (scanner.getTokenType() == TokenType._LEFT_PAREN_) {
			scanner.scan();
			right = parseSelectStatement();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
		} else if (scanner.getTokenType() == TokenType.SELECT) {
			right = parseIntersect();
		} else {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Select statement expected here.");
		}
		if (tokenType == TokenType.UNION) {
			selectStatement = new Union(scanner.getSourceCode(), beginIndex, 
					selectStatement, all, distinct, correspondingColumnList, right);
		} else if (tokenType == TokenType.EXCEPT) {
			selectStatement = new Except(scanner.getSourceCode(), beginIndex, 
					selectStatement, all, distinct, correspondingColumnList, right);
		} else {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
					"Error token.");
		}
		while (true) {
			if (scanner.getTokenType() != TokenType.UNION &&
				scanner.getTokenType() != TokenType.EXCEPT) {
				break;
			}
			tokenType = scanner.getTokenType();
			beginIndex = scanner.getBeginIndex();
			scanner.scan();
			all = false;
			distinct = false;
			if (scanner.getTokenType() == TokenType.ALL) {
				all = true;
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType.DISTINCT) {
				distinct = true;
				scanner.scan();
			}
			correspondingColumnList = null;
			if (scanner.getTokenType() == TokenType.CORRESPONDING) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType.BY) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'BY' expected here.");
				}
				scanner.scan();
				correspondingColumnList = new ArrayList<NameChain>();
				while (true) {
					NameChain correspondingColumn = parseNameChain(false);
					correspondingColumnList.add(correspondingColumn);
					if (scanner.getTokenType() != TokenType._COMMA_) {
						break;
					}
					scanner.scan();
				}
			}
			right = null;
			if (scanner.getTokenType() == TokenType._LEFT_PAREN_) {
				scanner.scan();
				right = parseSelectStatement();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType.SELECT) {
				right = parseIntersect();
			} else {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Select statement expected here.");
			}
			if (tokenType == TokenType.UNION) {
				selectStatement = new Union(scanner.getSourceCode(), beginIndex, 
						selectStatement, all, distinct, correspondingColumnList, right);
			} else if (tokenType == TokenType.EXCEPT) {
				selectStatement = new Except(scanner.getSourceCode(), beginIndex, 
						selectStatement, all, distinct, correspondingColumnList, right);
			} else {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
						"Error token.");
			}
		}
		int endIndex = scanner.getBeginIndex();
		Subquery subquery = new Subquery(subqueryBeginIndex, 
				endIndex, selectStatement);
		return subquery;
	}
	
	private List<SelectSublist> parseSelectList() {
		List<SelectSublist> list = 
				new ArrayList<SelectSublist>();
		while (true) {
			ValueExpression value = parseValueExpression();
			if (scanner.getTokenType() == TokenType.AS) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType._ID_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"Identifier expected here.");
				}
				Name name = new Name(scanner.getBeginIndex(), 
						scanner.getEndIndex(), scanner.getContent());
				SelectSublist selectSublist = new SelectSublist(value, name);
				list.add(selectSublist);
				scanner.scan();
				if (scanner.getTokenType() != TokenType._COMMA_) {
					break;
				}
				scanner.scan();
				continue;
			}
			if (scanner.getTokenType() != TokenType._ID_) {
				Name name = null;
				SelectSublist selectSublist = new SelectSublist(value, name);
				list.add(selectSublist);
				if (scanner.getTokenType() != TokenType._COMMA_) {
					break;
				}
				scanner.scan();
				continue;
			}
			Name name = new Name(scanner.getBeginIndex(), 
					scanner.getEndIndex(), scanner.getContent());
			SelectSublist selectSublist = new SelectSublist(value, name);
			list.add(selectSublist);
			scanner.scan();
			if (scanner.getTokenType() != TokenType._COMMA_) {
				break;
			}
			scanner.scan();
		}
		return list;
	}
	
	private UpdateStatement parseUpdateStatement() {
		if (scanner.getTokenType() != TokenType.UPDATE) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'UPDATE' expected here.");
		}
		int beginIndex = scanner.getBeginIndex();
		scanner.scan();
		NameChain targetTable = parseNameChain(true);
		if (scanner.getTokenType() != TokenType.SET) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'SET' expected here.");
		}
		scanner.scan();
		List<SetClause> setClauseList = parseSetClauseList();
		BooleanValueExpression searchCondition = null;
		if (scanner.getTokenType() == TokenType.WHERE) {
			scanner.scan();
			searchCondition = parseBooleanValueExpression();
		}
		if (scanner.getTokenType() != TokenType._SEMICOLON_ &&
			scanner.getTokenType() != null) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"';' expected here.");
		}
		scanner.scan();
		UpdateStatement updateStatement = new UpdateStatement(
				scanner.getSourceCode(), beginIndex, targetTable, 
				setClauseList, searchCondition);
		return updateStatement;
	}
	
	private List<SetClause> parseSetClauseList() {
		List<SetClause> list = new ArrayList<SetClause>();
		while (true) {
			if (scanner.getTokenType() != TokenType._ID_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Identifier expected here.");
			}
			Name updateTarget = new Name(scanner.getBeginIndex(), 
					scanner.getEndIndex(), scanner.getContent());
			scanner.scan();
			if (scanner.getTokenType() != TokenType._EQUALS_OPERATOR_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'=' expected here.");
			}
			scanner.scan();
			ValueExpression updateSource = parseValueExpression();
			SetClause setClause = new SetClause(updateTarget, 
					updateSource);
			list.add(setClause);
			if (scanner.getTokenType() != TokenType._COMMA_) {
				break;
			}
			scanner.scan();
		}
		return list;
	}
	
	private DeleteStatement parseDeleteStatement() {
		if (scanner.getTokenType() != TokenType.DELETE) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'DELETE' expected here.");
		}
		int beginIndex = scanner.getBeginIndex();
		scanner.scan();
		if (scanner.getTokenType() != TokenType.FROM) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'FROM' expected here.");
		}
		scanner.scan();
		NameChain targetTable = parseNameChain(true);
		BooleanValueExpression searchCondition = null;
		if (scanner.getTokenType() == TokenType.WHERE) {
			scanner.scan();
			searchCondition = parseBooleanValueExpression();
		}
		if (scanner.getTokenType() != TokenType._SEMICOLON_ &&
			scanner.getTokenType() != null) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"';' expected here.");
		}
		scanner.scan();
		DeleteStatement deleteStatement = new DeleteStatement(
				scanner.getSourceCode(), beginIndex, targetTable, searchCondition);
		return deleteStatement;
	}
	
	private InsertStatement parseInsertStatement() {
		if (scanner.getTokenType() != TokenType.INSERT) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'INSERT' expected here.");
		}
		int beginIndex = scanner.getBeginIndex();
		scanner.scan();
		if (scanner.getTokenType() != TokenType.INTO) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'INTO' expected here.");
		}
		scanner.scan();
		NameChain insertionTarget = parseNameChain(true);
		List<Name> insertColumnList = parseInsertColumnList();
		List<ValueExpression> contextuallyTypedRowValueExpressionList = null;
		SelectStatement subquery = null;
		if (scanner.getTokenType() == TokenType.VALUES) {
			scanner.scan();
			contextuallyTypedRowValueExpressionList = 
					parseValueExpressionList();
			if (scanner.getTokenType() != TokenType._SEMICOLON_ &&
				scanner.getTokenType() != null) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"';' expected here.");
			}
			scanner.scan();
		} else if (scanner.getTokenType() == TokenType.SELECT) {
			subquery = parseSelectStatement();
		} else {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'VALUES' or 'SELECT' expected here.");
		}
		InsertStatement insertStatement = new InsertStatement(
				scanner.getSourceCode(), beginIndex, insertionTarget, insertColumnList,
				contextuallyTypedRowValueExpressionList, 
				subquery);
		return insertStatement;
	}
	
	private TableDefinition parseTableDefinition(int beginIndex) {
		NameChain tableName = parseNameChain(false);
		if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'(' expected here.");
		}
		scanner.scan();
		List<TableElement> tableElementList = new ArrayList<TableElement>();
		while (true) {
			TableElement tableElement = parseTableElement();
			tableElementList.add(tableElement);
			if (scanner.getTokenType() == TokenType._COMMA_) {
				scanner.scan();
				continue;
			}
			break;
		}
		if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"')' expected here.");
		}
		scanner.scan();
		if (scanner.getTokenType() != null) {
			if (scanner.getTokenType() != TokenType._SEMICOLON_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"';' expected here.");
			}
			scanner.scan();
		}
		TableDefinition tableDefinition = new TableDefinition(scanner.getSourceCode(), 
				beginIndex, tableName, tableElementList);
		try {
			configuration.addTableDefinition(tableDefinition);
		} catch (Exception e) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), tableDefinition.getBeginIndex(), 
					e.getMessage());
		}
		return tableDefinition;
	}
	
	private TableElement parseTableElement() {
		if (scanner.getTokenType() == TokenType._ID_) {
			ColumnDefinition columnDefinition = parseColumnDefinition();
			return columnDefinition;
		}
		int beginIndex = scanner.getBeginIndex();
		TableConstraintDefinition tableConstraintDefinition = 
				parseTableConstraintDefinition(beginIndex);
		return tableConstraintDefinition;
	}
	
	private TableConstraintDefinition parseTableConstraintDefinition(int beginIndex) {
		NameChain constraintName = null;
		if (scanner.getTokenType() == TokenType.CONSTRAINT) {
			scanner.scan();
			int constraintNameBeginIndex = scanner.getBeginIndex();
			constraintName = parseNameChain(false);
			if (constraintName.size() != 1) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), constraintNameBeginIndex, 
						"Constraint name can only be simple name.");
			}
		}
		TableConstraintDefinition tableConstraintDefinition = null;
		if (scanner.getTokenType() == TokenType.UNIQUE) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			scanner.scan();
			List<Name> columnNameList = parseUniqueColumnNameList();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			TableConstraint tableConstraint = new TableConstraint(TableConstraintType.UNIQUE, columnNameList);
			tableConstraintDefinition = new TableConstraintDefinition(scanner.getSourceCode(), beginIndex, 
					constraintName, tableConstraint);
			return tableConstraintDefinition;
		}
		if (scanner.getTokenType() == TokenType.PRIMARY) {
			scanner.scan();
			if (!(scanner.getTokenType() == TokenType._ID_ && "KEY".equalsIgnoreCase(scanner.getContent()))) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'KEY' expected here.");
			}
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			scanner.scan();
			List<Name> columnNameList = parseUniqueColumnNameList();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			TableConstraint tableConstraint = new TableConstraint(TableConstraintType.PRIMARY_KEY, columnNameList);
			tableConstraintDefinition = new TableConstraintDefinition(scanner.getSourceCode(), beginIndex, 
					constraintName, tableConstraint);
			return tableConstraintDefinition;
		}
		if (scanner.getTokenType() == TokenType.FOREIGN) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Referential constraint definition is not supported.");
		}
		if (scanner.getTokenType() == TokenType.CHECK) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Check constraint definition is not supported.");
		}
		if (constraintName == null) {
			return null;
		}
		throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
				"'UNIQUE' or 'PRIMARY' expected.");
	}
	
	private List<Name> parseUniqueColumnNameList() {
		List<Name> list = new ArrayList<Name>();
		while (true) {
			if (scanner.getTokenType() != TokenType._ID_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Column name expected here.");
			}
			Name columnName = new Name(scanner.getBeginIndex(), 
					scanner.getEndIndex(), scanner.getContent());
			list.add(columnName);
			scanner.scan();
			if (scanner.getTokenType() == TokenType._COMMA_) {
				scanner.scan();
				continue;
			}
			break;
		}
		return list;
	}
	
	private ColumnDefinition parseColumnDefinition() {
		int beginIndex = scanner.getBeginIndex();
		Name columnName = new Name(scanner.getBeginIndex(), 
				scanner.getEndIndex(), scanner.getContent());
		scanner.scan();
		DataType dataType = parseDataType();
		if (scanner.getTokenType() == TokenType.REFERENCES) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Reference scope check is not supported.");
		}
		DefaultClause defaultClause = parseDefaultClause();
		ColumnConstraintDefinition columnConstraintDefinition = parseColumnConstraintDefinition();
		if (scanner.getTokenType() == TokenType.COLLATE) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Collate clause is not supported.");
		}
		ColumnDefinition columnDefinition = new ColumnDefinition(scanner.getSourceCode(), 
				beginIndex, columnName, dataType, defaultClause, columnConstraintDefinition);
		return columnDefinition;
	}
	
	private DataType parseDataType() {
		int beginIndex = scanner.getBeginIndex();
		if (scanner.getTokenType() == TokenType.VARCHAR) {
			DataType dataType = parseDataTypeWithLength(beginIndex, DataTypeEnum.VARCHAR);
			return dataType;
		}
		if (scanner.getTokenType() == TokenType.CHAR || 
			scanner.getTokenType() == TokenType.CHARACTER) {
			DataType dataType = parseDataTypeWithLength(beginIndex, DataTypeEnum.CHAR);
			return dataType;
		}
		if (scanner.getTokenType() == TokenType.NCHAR) {
			DataType dataType = parseDataTypeWithLength(beginIndex, DataTypeEnum.NCHAR);
			return dataType;
		}
		if (scanner.getTokenType() == TokenType.INT || scanner.getTokenType() == TokenType.INTEGER) {
			scanner.scan();
			return new DataType(scanner.getSourceCode(), beginIndex, DataTypeEnum.INT);
		}
		if (scanner.getTokenType() == TokenType.SMALLINT) {
			scanner.scan();
			return new DataType(scanner.getSourceCode(), beginIndex, DataTypeEnum.SMALLINT);
		}
		if (scanner.getTokenType() == TokenType.NUMERIC ||
			scanner.getTokenType() == TokenType.DECIMAL ||
			scanner.getTokenType() == TokenType.DEC) {
			DataType dataType = parseDataTypeWithPrecisionAndScale(beginIndex, DataTypeEnum.NUMERIC);
			return dataType;
		}
		if (scanner.getTokenType() == TokenType.REAL) {
			scanner.scan();
			return new DataType(scanner.getSourceCode(), beginIndex, DataTypeEnum.REAL);
		}
		if (scanner.getTokenType() == TokenType.DATE) {
			scanner.scan();
			return new DataType(scanner.getSourceCode(), beginIndex, DataTypeEnum.DATE);
		}
		if (scanner.getTokenType() == TokenType.TIMESTAMP) {
			scanner.scan();
			return new DataType(scanner.getSourceCode(), beginIndex, DataTypeEnum.TIMESTAMP);
		}
		throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
				"This data type is not supported.");
	}
	
	private DataType parseDataTypeWithLength(int beginIndex, DataTypeEnum dataTypeEnum) {
		scanner.scan();
		if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'(' expected here.");
		}
		scanner.scan();
		int length = getCharLength();
		scanner.scan();
		if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"')' expected here.");
		}
		scanner.scan();
		return new DataType(scanner.getSourceCode(), beginIndex, dataTypeEnum, length);
	}
	
	private int getCharLength() {
		int beginIndex = scanner.getBeginIndex();
		int symbol;
		if (scanner.getTokenType() == TokenType._PLUS_SIGN_) {
			symbol = 1;
			scanner.scan();
		} else if (scanner.getTokenType() == TokenType._MINUS_SIGN_) {
			symbol = -1;
			scanner.scan();
		} else {
			symbol = 0;
		}
		if (scanner.getTokenType() != TokenType._NUM_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Numeric literal expected here.");
		}
		String content = scanner.getContent();
		if (symbol == -1) {
			content = "-" + content;
		}
		if (content.indexOf('.') > -1) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
					"Do not use decimal.");
		}
		if (content.indexOf('e') > -1 || content.indexOf('E') > -1) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
					"Do not use scientific counting.");
		}
		int i = Integer.parseInt(content);
		if (i < 0) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
					"Do not use negative number.");
		}
		return i;
	}
	
	private DataType parseDataTypeWithPrecisionAndScale(int beginIndex, DataTypeEnum dataTypeEnum) {
		scanner.scan();
		if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
			return new DataType(scanner.getSourceCode(), beginIndex, dataTypeEnum, -1, -1);
		}
		scanner.scan();
		int precision = getNumericPrecisionOrScale();
		scanner.scan();
		if (scanner.getTokenType() != TokenType._COMMA_) {
			return new DataType(scanner.getSourceCode(), beginIndex, dataTypeEnum, precision, -1);
		}
		scanner.scan();
		int scaleBeginIndex = scanner.getBeginIndex();
		int scale = getNumericPrecisionOrScale();
		if (scale >= precision) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scaleBeginIndex, 
					"Scale must be less than than precision.");
		}
		scanner.scan();
		if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"')' expected here.");
		}
		scanner.scan();
		return new DataType(scanner.getSourceCode(), beginIndex, dataTypeEnum, precision, scale);
	}
	
	private int getNumericPrecisionOrScale() {
		int beginIndex = scanner.getBeginIndex();
		int symbol;
		if (scanner.getTokenType() == TokenType._PLUS_SIGN_) {
			symbol = 1;
			scanner.scan();
		} else if (scanner.getTokenType() == TokenType._MINUS_SIGN_) {
			symbol = -1;
			scanner.scan();
		} else {
			symbol = 0;
		}
		if (scanner.getTokenType() != TokenType._NUM_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Numeric literal expected here.");
		}
		String content = scanner.getContent();
		if (symbol == -1) {
			content = "-" + content;
		}
		if (content.indexOf('.') > -1) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
					"Do not use decimal.");
		}
		if (content.indexOf('e') > -1 || content.indexOf('E') > -1) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
					"Do not use scientific counting.");
		}
		int i = Integer.parseInt(content);
		if (i < 0) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
					"Do not use negative number.");
		}
		return i;
	}
	
	private DefaultClause parseDefaultClause() {
		if (scanner.getTokenType() != TokenType.DEFAULT) {
			return null;
		}
		int beginIndex = scanner.getBeginIndex();
		ValueExpression defaultValue = null;
		scanner.scan();
		if (scanner.getTokenType() == TokenType._NUM_) {
			int beginIndex2 = scanner.getBeginIndex();
			int endIndex = scanner.getEndIndex();
			String content = scanner.getContent();
			defaultValue = new NumericLiteral(beginIndex2, endIndex, content);
		} else if (scanner.getTokenType() == TokenType._STR_) {
			int beginIndex2 = scanner.getBeginIndex();
			int endIndex = scanner.getEndIndex();
			String content = scanner.getContent();
			defaultValue = new StringLiteral(beginIndex2, endIndex, content);
		} else {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"The default value only support numeric literal or string literal.");
		}
		scanner.scan();
		DefaultClause defaultClause = new DefaultClause(scanner.getSourceCode(), beginIndex, defaultValue);
		return defaultClause;
	}
	
	private ColumnConstraintDefinition parseColumnConstraintDefinition() {
		ConstraintNameDefinition constraintNameDefinition = null;
		if (scanner.getTokenType() == TokenType.CONSTRAINT) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			int constraintNameBeginIndex = scanner.getBeginIndex();
			NameChain constraintName = parseNameChain(false);
			if (constraintName.size() != 1) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), constraintNameBeginIndex, 
						"Constraint name can only be simple name.");
			}
			constraintNameDefinition = new ConstraintNameDefinition(scanner.getSourceCode(), 
					beginIndex, constraintName);
		}
		int beginIndex = -1;
		ColumnConstraintEnum columnConstraintEnum = null;
		if (scanner.getTokenType() == TokenType.NOT) {
			beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() != TokenType.NULL) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'NULL' expected here.");
			}
			scanner.scan();
			columnConstraintEnum = ColumnConstraintEnum.NOT_NULL;
		} else if (scanner.getTokenType() == TokenType.NULL) {
			beginIndex = scanner.getBeginIndex();
			scanner.scan();
			columnConstraintEnum = ColumnConstraintEnum.NULL;
		} else if (scanner.getTokenType() == TokenType.UNIQUE) {
			beginIndex = scanner.getBeginIndex();
			scanner.scan();
			columnConstraintEnum = ColumnConstraintEnum.UNIQUE;
		} else if (scanner.getTokenType() == TokenType.PRIMARY) {
			beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (!(scanner.getTokenType() == TokenType._ID_ && "key".equalsIgnoreCase(scanner.getContent()))) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'KEY' expected here.");
			}
			scanner.scan();
			columnConstraintEnum = ColumnConstraintEnum.PRIMARY_KEY;
		} else {
			if (constraintNameDefinition == null) {
				return null;
			}
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'NOT NULL', 'UNIQUE' or 'PRIMARY KEY' expected here.");
		}
		ColumnConstraint columnConstraint = new ColumnConstraint(scanner.getSourceCode(), 
				beginIndex, columnConstraintEnum);
		ColumnConstraintDefinition columnConstraintDefinition = new ColumnConstraintDefinition(
				scanner.getSourceCode(), beginIndex, constraintNameDefinition, columnConstraint);
		return columnConstraintDefinition;
	}
	
	private DropTableStatement parseDropTableStatement(int beginIndex) {
		NameChain tableName = parseNameChain(false);
		if (scanner.getTokenType() != null) {
			if (scanner.getTokenType() != TokenType._SEMICOLON_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"';' expected here.");
			}
			scanner.scan();
		}
		DropTableStatement dropTableStatement = new DropTableStatement(
				scanner.getSourceCode(), beginIndex, tableName);
		return dropTableStatement;
	}
	
	private DropIndexStatement parseDropIndexStatement(int beginIndex) {
		NameChain indexName = parseNameChain(false);
		if (scanner.getTokenType() != null) {
			if (scanner.getTokenType() != TokenType._SEMICOLON_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"';' expected here.");
			}
			scanner.scan();
		}
		if (indexName.size() < 2) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"The length of the index name is at least two.");
		}
		DropIndexStatement dropIndexStatement = new DropIndexStatement(
				scanner.getSourceCode(), beginIndex, indexName);
		return dropIndexStatement;
	}
	
	private CreateIndexStatement parseCreateIndexStatement(int beginIndex, boolean unique) {
		NameChain indexName = parseNameChain(false);
		if (indexName.size() != 1) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), indexName.getBeginIndex(), 
					"Index name can only be simple name.");
		}
		if (scanner.getTokenType() != TokenType.ON) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'ON' expected here.");
		}
		scanner.scan();
		NameChain tableName = parseNameChain(false);
		if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'(' expected here.");
		}
		scanner.scan();
		List<Name> columnNameList = parseNameList();
		if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"')' expected here.");
		}
		scanner.scan();
		if (scanner.getTokenType() != null) {
			if (scanner.getTokenType() != TokenType._SEMICOLON_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"';' expected here.");
			}
			scanner.scan();
		}
		CreateIndexStatement createIndexStatement = new CreateIndexStatement(
				scanner.getSourceCode(), beginIndex, unique, indexName, tableName, columnNameList);
		return createIndexStatement;
	}
	
	private List<Name> parseNameList() {
		List<Name> list = new ArrayList<Name>();
		while (true) {
			if (scanner.getTokenType() != TokenType._ID_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Column name expected here.");
			}
			Name columnName = new Name(scanner.getBeginIndex(), 
					scanner.getEndIndex(), scanner.getContent());
			list.add(columnName);
			scanner.scan();
			if (scanner.getTokenType() == TokenType._COMMA_) {
				scanner.scan();
				continue;
			}
			break;
		}
		return list;
	}
	
	private Statement parseAlterTableStatement(int beginIndex) {
		NameChain tableName = parseNameChain(false);
		if (scanner.getTokenType() == TokenType.ADD) {
			scanner.scan();
			if (scanner.getTokenType() == TokenType.PRIMARY) {
				scanner.scan();
				if (!(scanner.getTokenType() == TokenType._ID_ && "KEY".equalsIgnoreCase(scanner.getContent()))) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'KEY' expected here.");
				}
				scanner.scan();
				if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'(' expected here.");
				}
				scanner.scan();
				if (scanner.getTokenType() != TokenType._ID_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"Column name expected here.");
				}
				Name columnName = new Name(scanner.getBeginIndex(), scanner.getEndIndex(), scanner.getContent());
				scanner.scan();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				if (scanner.getTokenType() != null) {
					if (scanner.getTokenType() != TokenType._SEMICOLON_) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"';' expected here.");
					}
					scanner.scan();
				}
				AddPrimaryKeyDefinition addTableConstraintDefinition = new AddPrimaryKeyDefinition(
						scanner.getSourceCode(), beginIndex, tableName, columnName);
				return addTableConstraintDefinition;
			}
			if (scanner.getTokenType() != TokenType.COLUMN && 
				scanner.getTokenType() != TokenType._ID_) {
				int tableConstraintDefinitionBeginIndex = scanner.getBeginIndex();
				TableConstraintDefinition tableConstraintDefinition = parseTableConstraintDefinition(
						tableConstraintDefinitionBeginIndex);
				if (scanner.getTokenType() != null) {
					if (scanner.getTokenType() != TokenType._SEMICOLON_) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"';' expected here.");
					}
					scanner.scan();
				}
				AddTableConstraintDefinition addTableConstraintDefinition = new AddTableConstraintDefinition(
						scanner.getSourceCode(), beginIndex, tableName, tableConstraintDefinition);
				return addTableConstraintDefinition;
			}
			if (scanner.getTokenType() == TokenType.COLUMN) {
				scanner.scan();
			}
			if (scanner.getTokenType() != TokenType._ID_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Column name expected here.");
			}
			ColumnDefinition columnDefinition = parseColumnDefinition();
			if (scanner.getTokenType() != null) {
				if (scanner.getTokenType() != TokenType._SEMICOLON_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"';' expected here.");
				}
				scanner.scan();
			}
			AddColumnDefinition addColumnDefinition = new AddColumnDefinition(
					scanner.getSourceCode(), beginIndex, tableName, columnDefinition);
			return addColumnDefinition;
		}
		if (scanner.getTokenType() == TokenType.DROP) {
			scanner.scan();
			if (scanner.getTokenType() == TokenType.PRIMARY) {
				scanner.scan();
				if (!(scanner.getTokenType() == TokenType._ID_ && scanner.getContent().equalsIgnoreCase("KEY"))) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'KEY' expected here.");
				}
				scanner.scan();
				if (scanner.getTokenType() != null) {
					if (scanner.getTokenType() != TokenType._SEMICOLON_) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"';' expected here.");
					}
					scanner.scan();
				}
				DropPrimaryKeyDefinition dropPrimaryKeyDefinition = new DropPrimaryKeyDefinition(
						scanner.getSourceCode(), beginIndex, tableName);
				return dropPrimaryKeyDefinition;
			}
			if (scanner.getTokenType() == TokenType.INDEX) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType._ID_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"Constraint name expected here.");
				}
				Name constraintName = new Name(scanner.getBeginIndex(), 
						scanner.getEndIndex(), scanner.getContent());
				scanner.scan();
				if (scanner.getTokenType() != null) {
					if (scanner.getTokenType() != TokenType._SEMICOLON_) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"';' expected here.");
					}
					scanner.scan();
				}
				List<Name> list = new ArrayList<Name>(tableName.size() + 1);
				for (int i = 0; i < tableName.size(); i++) {
					list.add(tableName.get(i));
				}
				list.add(constraintName);
				NameChain indexName = new NameChain(list);
				DropIndexStatement dropIndexStatement = new DropIndexStatement(
						scanner.getSourceCode(), beginIndex, indexName);
				return dropIndexStatement;
			}
			if (scanner.getTokenType() == TokenType.COLUMN) {
				scanner.scan();
			}
			if (scanner.getTokenType() != TokenType._ID_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Column name expected here.");
			}
			Name columnName = new Name(scanner.getBeginIndex(), scanner.getEndIndex(), scanner.getContent());
			scanner.scan();
			if (scanner.getTokenType() != null) {
				if (scanner.getTokenType() != TokenType._SEMICOLON_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"';' expected here.");
				}
				scanner.scan();
			}
			DropColumnDefinition dropColumnDefinition = new DropColumnDefinition(
					scanner.getSourceCode(), beginIndex, tableName, columnName);
			return dropColumnDefinition;
		}
		if (scanner.getTokenType() == TokenType.MODIFY) {
			scanner.scan();
			if (scanner.getTokenType() == TokenType.COLUMN) {
				scanner.scan();
			}
			if (scanner.getTokenType() != TokenType._ID_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Column name expected here.");
			}
			ColumnDefinition columnDefinition = parseColumnDefinition();
			if (scanner.getTokenType() != null) {
				if (scanner.getTokenType() != TokenType._SEMICOLON_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"';' expected here.");
				}
				scanner.scan();
			}
			ModifyColumnDefinition modifyColumnDefinition = new ModifyColumnDefinition(
					scanner.getSourceCode(), beginIndex, tableName, columnDefinition);
			return modifyColumnDefinition;
		}
		if (scanner.getTokenType() == TokenType.ALTER) {
			scanner.scan();
			int beginIndex2 = scanner.getBeginIndex();
			if (scanner.getTokenType() == TokenType.COLUMN) {
				scanner.scan();
			}
			if (scanner.getTokenType() != TokenType._ID_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Column name expected here.");
			}
			Name columnName = new Name(scanner.getBeginIndex(), scanner.getEndIndex(), scanner.getContent());
			scanner.scan();
			if (scanner.getTokenType() == TokenType.SET) {
				int setColumnDefaultClauseBeginIndex = scanner.getBeginIndex();
				scanner.scan();
				DefaultClause defaultClause = parseDefaultClause();
				SetColumnDefaultClause setColumnDefaultClause = 
						new SetColumnDefaultClause(scanner.getSourceCode(), 
								setColumnDefaultClauseBeginIndex, defaultClause);
				if (scanner.getTokenType() != null) {
					if (scanner.getTokenType() != TokenType._SEMICOLON_) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"';' expected here.");
					}
					scanner.scan();
				}
				AlterColumnDefinition alterColumnDefinition = new AlterColumnDefinition(
						scanner.getSourceCode(), beginIndex, tableName, columnName, 
						setColumnDefaultClause);
				return alterColumnDefinition;
			}
			if (scanner.getTokenType() == TokenType.DROP) {
				int dropColumnDefaultClauseBeginIndex = scanner.getBeginIndex();
				scanner.scan();
				if (scanner.getTokenType() != TokenType.DEFAULT) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'DEFAULT' expected here.");
				}
				scanner.scan();
				DropColumnDefaultClause dropColumnDefaultClause = new DropColumnDefaultClause(
						scanner.getSourceCode(), dropColumnDefaultClauseBeginIndex);
				if (scanner.getTokenType() != null) {
					if (scanner.getTokenType() != TokenType._SEMICOLON_) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"';' expected here.");
					}
					scanner.scan();
				}
				AlterColumnDefinition alterColumnDefinition = new AlterColumnDefinition(
						scanner.getSourceCode(), beginIndex, tableName, columnName, 
						dropColumnDefaultClause);
				return alterColumnDefinition;
			}
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex2, 
					"This alter column action is not supported.");
		}
		throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
				"This 'ALTER' statement is not supported.");
	}
	
	private List<ValueExpression> parseValueExpressionList() {
		if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'(' expected here.");
		}
		scanner.scan();
		List<ValueExpression> list = new ArrayList<ValueExpression>();
		while (true) {
			ValueExpression value = parseValueExpression();
			list.add(value);
			if (scanner.getTokenType() != TokenType._COMMA_) {
				break;
			}
			scanner.scan();
		}
		if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"')' expected here.");
		}
		scanner.scan();
		return list;
	}
	
	private List<Name> parseInsertColumnList() {
		if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'(' expected here.");
		}
		scanner.scan();
		List<Name> list = new ArrayList<Name>();
		while (true) {
			if (scanner.getTokenType() != TokenType._ID_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Identifier expected here.");
			}
			Name name = new Name(scanner.getBeginIndex(), 
					scanner.getEndIndex(), scanner.getContent());
			list.add(name);
			scanner.scan();
			if (scanner.getTokenType() != TokenType._COMMA_) {
				break;
			}
			scanner.scan();
		}
		if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"')' expected here.");
		}
		scanner.scan();
		return list;
	}
	
	private CallStatement parseCallStatement() {
		if (scanner.getTokenType() != TokenType.CALL) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'CALL' expected here.");
		}
		int beginIndex = scanner.getBeginIndex();
		scanner.scan();
		if (scanner.getTokenType() != TokenType._ID_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Identifier expected here.");
		}
		NameChain routineName = parseNameChain(false);
		if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'(' expected here.");
		}
		List<InOut> inOutList = 
				new ArrayList<InOut>();
		scanner.scan();
		while (true) {
			InOut inOut = parseInOut();
			inOutList.add(inOut);
			if (scanner.getTokenType() != TokenType._COMMA_) {
				break;
			}
			scanner.scan();
		}
		if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"')' expected here.");
		}
		scanner.scan();
		if (scanner.getTokenType() != TokenType._SEMICOLON_ &&
			scanner.getTokenType() != null) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"';' expected here.");
		}
		scanner.scan();
		CallStatement callStatement = new CallStatement(
				scanner.getSourceCode(), beginIndex, routineName, inOutList);
		return callStatement;
	}
	
	private InOut parseInOut() {
		if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'(' expected here.");
		}
		scanner.scan();
		String content = scanner.getContent();
		JdbcType jdbcType = JdbcType.getJdbcType(content);
		if (jdbcType == null) {
			int index = scanner.getBeginIndex();
			JdbcType[] jdbcTypes = JdbcType.values();
			StringBuilder buf = new StringBuilder(1000);
			for (int i = 0; i < jdbcTypes.length; i++) {
				JdbcType t = jdbcTypes[i];
				buf.append('\'').append(t.getContent()).append('\'');
				if (i < jdbcTypes.length - 1) {
					buf.append(',').append(' ');
				}
			}
			buf.append(' ');
			buf.append("expected here.");
			String message = buf.toString();
			buf.setLength(0);
			buf = null;
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), index, message);
		}
		scanner.scan();
		if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"')' expected here.");
		}
		scanner.scan();
		if (scanner.getTokenType() != TokenType._ID_ &&
			scanner.getTokenType() != TokenType._PARAM_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Identifier or parameter expected here.");
		}
		int inBeginIndex = -1;
		int inEndIndex = -1;
		String in = null;
		int outBeginIndex = -1;
		int outEndIndex = -1;
		String out = null;
		if (scanner.getTokenType() == TokenType._ID_) {
			outBeginIndex = scanner.getBeginIndex();
			outEndIndex = scanner.getEndIndex();
			out = scanner.getContent();
		} else {
			inBeginIndex = scanner.getBeginIndex();
			inEndIndex = scanner.getEndIndex();
			in = scanner.getContent();
		}
		scanner.scan();
		if (out != null) {
			if (scanner.getTokenType() == TokenType._VERTICAL_BAR_) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType._PARAM_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"Parameter expected here.");
				}
				inBeginIndex = scanner.getBeginIndex();
				inEndIndex = scanner.getEndIndex();
				in = scanner.getContent();
				InOut inOut = new InOut(jdbcType, inBeginIndex, inEndIndex, 
						in, outBeginIndex, outEndIndex, out);
				scanner.scan();
				return inOut;
			}
			return new InOut(jdbcType, inBeginIndex, inEndIndex, 
					in, outBeginIndex, outEndIndex, out);
		} else {
			if (scanner.getTokenType() == TokenType._VERTICAL_BAR_) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType._ID_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"Identifier expected here.");
				}
				outBeginIndex = scanner.getBeginIndex();
				outEndIndex = scanner.getEndIndex();
				out = scanner.getContent();
				InOut inOut = new InOut(jdbcType, inBeginIndex, inEndIndex, 
						in, outBeginIndex, outEndIndex, out);
				scanner.scan();
				return inOut;
			}
			return new InOut(jdbcType, inBeginIndex, inEndIndex, 
					in, outBeginIndex, outEndIndex, out);
		}
	}
	
	private BooleanValueExpression parseBooleanValueExpression() {
		ValueExpression valueExpression = parseValueExpression();
		if (!(valueExpression instanceof BooleanValueExpression)) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), valueExpression.getBeginIndex(), 
					"Boolean value expression expected here.");
		}
		BooleanValueExpression expr = (BooleanValueExpression) valueExpression;
		return expr;
	}
	
	private ValueExpression parseValueExpression() {
		int beginIndex = scanner.getBeginIndex();
		ValueExpression valueExpression = parseBooleanTerm();
		if (!(valueExpression instanceof BooleanValueExpression)) {
			return valueExpression;
		}
		BooleanTerm booleanTerm = (BooleanTerm) valueExpression;
		List<BooleanTerm> list = new ArrayList<BooleanTerm>();
		list.add(booleanTerm);
		int endIndex = -1;
		while (true) {
			if (scanner.getTokenType() != TokenType.OR) {
				endIndex = scanner.getBeginIndex();
				break;
			}
			scanner.scan();
			valueExpression = parseBooleanTerm();
			if (!(valueExpression instanceof BooleanTerm)) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Boolean term expected here.");
			}
			booleanTerm = (BooleanTerm) valueExpression;
			list.add(booleanTerm);
		}
		BooleanValue booleanValue = new BooleanValue(beginIndex, 
				endIndex, list);
		return booleanValue;
	}
	
	private ValueExpression parseBooleanTerm() {
		int beginIndex = scanner.getBeginIndex();
		ValueExpression valueExpression = parseBooleanFactor();
		if (!(valueExpression instanceof BooleanValueExpression)) {
			return valueExpression;
		}
		BooleanFactor booleanFactor = (BooleanFactor) valueExpression;
		List<BooleanFactor> list = new ArrayList<BooleanFactor>();
		list.add(booleanFactor);
		int endIndex = -1;
		while (true) {
			if (scanner.getTokenType() != TokenType.AND) {
				endIndex = scanner.getBeginIndex();
				break;
			}
			scanner.scan();
			valueExpression = parseBooleanFactor();
			if (!(valueExpression instanceof BooleanFactor)) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Boolean factor expected here.");
			}
			booleanFactor = (BooleanFactor) valueExpression;
			list.add(booleanFactor);
		}
		BooleanTerm booleanTerm = new BooleanTerm(beginIndex, 
				endIndex, list);
		return booleanTerm;
	}
	
	private ValueExpression parseBooleanFactor() {
		int beginIndex = scanner.getBeginIndex();
		boolean not = false;
		if (scanner.getTokenType() == TokenType.NOT) {
			not = true;
			scanner.scan();
			ValueExpression valueExpression = parseBooleanTest();
			if (!(valueExpression instanceof BooleanValueExpression)) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), valueExpression.getBeginIndex(), 
						"Predicate expected here.");
			}
			int endIndex = scanner.getBeginIndex();
			Predicate predicate = (Predicate) valueExpression;
			BooleanFactor booleanFactor = new BooleanFactor(beginIndex, 
					endIndex, not, predicate);
			return booleanFactor;
		} else {
			ValueExpression valueExpression = parseBooleanTest();
			int endIndex = scanner.getBeginIndex();
			if (valueExpression instanceof BooleanValueExpression) {
				BooleanValueExpression booleanValueExpression = 
						(BooleanValueExpression) valueExpression;
				BooleanFactor booleanFactor = new BooleanFactor(beginIndex, 
						endIndex, not, booleanValueExpression);
				return booleanFactor;
			}
			return valueExpression;
		}
	}
	
	private ValueExpression parseBooleanTest() {
		int beginIndex = scanner.getBeginIndex();
		ValueExpression valueExpression = parseBooleanPrimary();
		if (scanner.getTokenType() != TokenType.IS) {
			if (valueExpression instanceof Predicate) {
				int endIndex = scanner.getBeginIndex();
				boolean not = false;
				TruthValue truthValue = null;
				BooleanValueExpression b = (BooleanValueExpression) valueExpression;
				BooleanTest booleanTest = new BooleanTest(beginIndex, 
						endIndex, b, not, truthValue);
				return booleanTest;
			}
			return valueExpression;
		}
		scanner.scan();
		boolean not = false;
		if (scanner.getTokenType() == TokenType.NOT) {
			not = true;
			scanner.scan();
		}
		TruthValue truthValue = null;
		int endIndex = scanner.getEndIndex();
		if (scanner.getTokenType() == TokenType.TRUE) {
			scanner.scan();
			truthValue = TruthValue.TRUE;
		} else if (scanner.getTokenType() == TokenType.FALSE) {
			scanner.scan();
			truthValue = TruthValue.FALSE;
		} else if (scanner.getTokenType() == TokenType.UNKNOWN) {
			scanner.scan();
			truthValue = TruthValue.UNKNOWN;
		} else {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'TRUE', 'FALSE' or 'UNKNOWN' expected here.");
		}
		BooleanValueExpression b = (BooleanValueExpression) valueExpression;
		BooleanTest booleanTest = new BooleanTest(beginIndex, 
				endIndex, b, not, truthValue);
		return booleanTest;
	}
	
	private ValueExpression parseBooleanPrimary() {
		int beginIndex = scanner.getBeginIndex();
		int endIndex = scanner.getEndIndex();
		if (scanner.getTokenType() == TokenType.EXISTS) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			int subqueryBeginIndex = scanner.getBeginIndex();
			scanner.scan();
			Subquery subquery = parseSubquery(subqueryBeginIndex);
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			ExistsPredicate existsPredicate = new ExistsPredicate(
					beginIndex, endIndex, subquery);
			return existsPredicate;
		}
		if (scanner.getTokenType() == TokenType.UNIQUE) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			int subqueryBeginIndex = scanner.getBeginIndex();
			scanner.scan();
			Subquery subquery = parseSubquery(subqueryBeginIndex);
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			UniquePredicate uniquePredicate = new UniquePredicate(
					beginIndex, endIndex, subquery);
			return uniquePredicate;
		}
		ValueExpression valueExpression = parseElementaryOperation();
		if (valueExpression instanceof BooleanValueExpression) {
			return valueExpression;
		}
		final TokenType tokenType = scanner.getTokenType();
		if (tokenType == TokenType._EQUALS_OPERATOR_ ||
			tokenType == TokenType._NOT_EQUALS_OPERATOR_ ||
			tokenType == TokenType._LESS_THAN_OPERATOR_ ||
			tokenType == TokenType._GREATER_THAN_OPERATOR_ ||
			tokenType == TokenType._LESS_THAN_OR_EQUALS_OPERATOR_ ||
			tokenType == TokenType._GREATER_THAN_OR_EQUALS_OPERATOR_) {
			CompOp compOp = null;
			if (tokenType == TokenType._EQUALS_OPERATOR_) {
				compOp = CompOp.EQUALS;
			} else if (tokenType == TokenType._NOT_EQUALS_OPERATOR_) {
				compOp = CompOp.NOT_EQUALS;
			} else if (tokenType == TokenType._LESS_THAN_OPERATOR_) {
				compOp = CompOp.LESS_THAN;
			} else if (tokenType == TokenType._GREATER_THAN_OPERATOR_) {
				compOp = CompOp.GREATER_THAN;
			} else if (tokenType == TokenType._LESS_THAN_OR_EQUALS_OPERATOR_) {
				compOp = CompOp.LESS_THAN_OR_EQUALS;
			} else if (tokenType == TokenType._GREATER_THAN_OR_EQUALS_OPERATOR_) {
				compOp = CompOp.GREATER_THAN_OR_EQUALS;
			} else {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Comparison operator expected here.");
			}
			Quantifier quantifier = null;
			scanner.scan();
			if (scanner.getTokenType() == TokenType.ALL) {
				quantifier = Quantifier.ALL;
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType.SOME) {
				quantifier = Quantifier.SOME;
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType.ANY) {
				quantifier = Quantifier.ANY;
				scanner.scan();
			} else {
				quantifier = null;
			}
			ValueExpression right = null;
			right = parseElementaryOperation();
			ComparisonPredicate comparisonPredicate = 
					new ComparisonPredicate(valueExpression, 
							compOp, quantifier, right);
			return comparisonPredicate;
		}
		if (tokenType == TokenType.IS) {
			scanner.scan();
			if (scanner.getTokenType() == TokenType.DISTINCT) {
				scanner.scan();
				if (scanner.getTokenType() != TokenType.FROM) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'FROM' expected here.");
				}
				scanner.scan();
				ValueExpression right = parseValueExpression();
				DistinctPredicate distinctPredicate = 
						new DistinctPredicate(valueExpression, right);
				return distinctPredicate;
			}
			boolean not = false;
			if (scanner.getTokenType() == TokenType.NOT) {
				not = true;
				scanner.scan();
			}
			if (scanner.getTokenType() != TokenType.NULL) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'NULL' expected here.");
			}
			scanner.scan();
			NullPredicate nullPredicate = new NullPredicate(
					valueExpression, not);
			return nullPredicate;
		}
		if (tokenType == TokenType.MATCH) {
			scanner.scan();
			boolean unique = false;
			boolean simple = false;
			boolean partial = false;
			boolean full = false;
			if (scanner.getTokenType() == TokenType.UNIQUE) {
				unique = true;
				scanner.scan();
			}
			if (scanner.getTokenType() == TokenType.FULL) {
				full = true;
				scanner.scan();
			} else if (scanner.getTokenType() == TokenType._ID_) {
				String content = scanner.getContent();
				if ("SIMPLE".equalsIgnoreCase(content)) {
					simple = true;
					scanner.scan();
				} else if ("PARTIAL".equalsIgnoreCase(content)) {
					partial = true;
					scanner.scan();
				}
			}
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			int subqueryBeginIndex = scanner.getBeginIndex();
			scanner.scan();
			Subquery subquery = parseSubquery(subqueryBeginIndex);
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			MatchPredicate matchPredicate = new MatchPredicate(
					valueExpression, unique, simple, partial, 
					full, subquery);
			return matchPredicate;
		}
		if (tokenType == TokenType.OVERLAPS) {
			scanner.scan();
			ValueExpression right = parseValueExpression();
			OverlapsPredicate overlapsPredicate = 
					new OverlapsPredicate(valueExpression, right);
			return overlapsPredicate;
		}
		boolean not = false;
		if (tokenType == TokenType.NOT) {
			not = true;
			scanner.scan();
		}
		if (scanner.getTokenType() == TokenType.BETWEEN) {
			scanner.scan();
			String content = scanner.getContent();
			if ("ASYMMETRIC".equalsIgnoreCase(content)) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Not support 'asymmetric'.");
			} else if ("SYMMETRIC".equalsIgnoreCase(content)) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Not support 'symmetric'.");
			}
			ValueExpression valueExpression1 = parseValueExpression();
			if (scanner.getTokenType() != TokenType.AND) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'AND' expected here.");
			}
			scanner.scan();
			ValueExpression valueExpression2 = parseValueExpression();
			BetweenPredicate betweenPredicate = new BetweenPredicate(
					not, valueExpression, valueExpression1, valueExpression2);
			return betweenPredicate;
		}
		if (scanner.getTokenType() == TokenType.IN) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				if (not == false) {
					ValueExpression valueExpression2 = parseValueExpression();
					List<ValueExpression> inValueList = 
							new ArrayList<ValueExpression>(1);
					inValueList.add(valueExpression2);
					InPredicate inPredicate = new InPredicate(not, 
							valueExpression, null, inValueList);
					return inPredicate;
				} else {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'(' expected here.");
				}
			}
			int subqueryBeginIndex = scanner.getBeginIndex();
			scanner.scan();
			Subquery subquery = null;
			List<ValueExpression> inValueList = null;
			if (scanner.getTokenType() == TokenType.SELECT) {
				subquery = parseSubquery(subqueryBeginIndex);
			} else {
				inValueList = new ArrayList<ValueExpression>();
				while (true) {
					ValueExpression inValue = parseValueExpression();
					inValueList.add(inValue);
					if (scanner.getTokenType() != TokenType._COMMA_) {
						break;
					}
					scanner.scan();
				}
			}
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			InPredicate inPredicate = new InPredicate(not, 
					valueExpression, subquery, inValueList);
			return inPredicate;
		}
		if (scanner.getTokenType() == TokenType.LIKE) {
			scanner.scan();
			ValueExpression characterPattern = parseValueExpression();
			ValueExpression escapeCharacter = null;
			if (scanner.getTokenType() == TokenType.ESCAPE) {
				scanner.scan();
				escapeCharacter = parseValueExpression();
			}
			LikePredicate likePredicate = new LikePredicate(not, 
					valueExpression, characterPattern, escapeCharacter);
			return likePredicate;
		}
		if (scanner.getTokenType() == TokenType.SIMILAR) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType.TO) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'TO' expected here.");
			}
			scanner.scan();
			ValueExpression similarPattern = parseValueExpression();
			ValueExpression escapeCharacter = null;
			if (scanner.getTokenType() == TokenType.ESCAPE) {
				scanner.scan();
				escapeCharacter = parseValueExpression();
			}
			SimilarPredicate similarPredicate = new SimilarPredicate(
					not, valueExpression, similarPattern, 
					escapeCharacter);
			return similarPredicate;
		}
		if (not == true) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"'BETWEEN', 'IN', 'LIKE' or 'SIMILAR' expected here.");
		}
		return valueExpression;
	}
	
	private ValueExpression parseElementaryOperation() {
		ValueExpression valueExpression = parseAdvancedOperation();
		while (true) {
			if (scanner.getTokenType() == TokenType._PLUS_SIGN_) {
				scanner.scan();
				ValueExpression valueExpression2 = 
						parseAdvancedOperation();
				valueExpression = new Addition(valueExpression, 
						valueExpression2);
				continue;
			}
			if (scanner.getTokenType() == TokenType._MINUS_SIGN_) {
				scanner.scan();
				ValueExpression valueExpression2 = 
						parseAdvancedOperation();
				valueExpression = new Subtraction(valueExpression, 
						valueExpression2);
				continue;
			}
			if (scanner.getTokenType() == TokenType._CONCATENATION_OPERATOR_) {
				scanner.scan();
				ValueExpression valueExpression2 = 
						parseAdvancedOperation();
				valueExpression = new Concatenation(valueExpression, 
						valueExpression2);
				continue;
			}
			break;
		}
		return valueExpression;
	}
	
	private ValueExpression parseAdvancedOperation() {
		ValueExpression valueExpression = parseValueExpressionPrimary();
		while (true) {
			if (scanner.getTokenType() == TokenType._ASTERISK_) {
				scanner.scan();
				ValueExpression valueExpression2 = 
						parseValueExpressionPrimary();
				valueExpression = new Multiplication(valueExpression, 
						valueExpression2);
				continue;
			}
			if (scanner.getTokenType() == TokenType._SOLIDUS_) {
				scanner.scan();
				ValueExpression valueExpression2 = 
						parseValueExpressionPrimary();
				valueExpression = new Division(valueExpression, 
						valueExpression2);
				continue;
			}
			break;
		}
		return valueExpression;
	}
	
	private ValueExpression parseValueExpressionPrimary() {
		int beginIndex = scanner.getBeginIndex();
		int endIndex = scanner.getEndIndex();
		if (scanner.getTokenType() == TokenType._MINUS_SIGN_) {
			scanner.scan();
			ValueExpression valueExpression = 
					parseUnsignedValueExpressionPrimary();
			NegativeExpression negativeExpression = 
					new NegativeExpression(beginIndex, 
							endIndex, valueExpression);
			return negativeExpression;
		} else if (scanner.getTokenType() == TokenType._PLUS_SIGN_) {
			scanner.scan();
			ValueExpression valueExpression = 
					parseUnsignedValueExpressionPrimary();
			PositiveExpression positiveExpression = 
					new PositiveExpression(beginIndex, 
							endIndex, valueExpression);
			return positiveExpression;
		} else {
			ValueExpression valueExpression = 
					parseUnsignedValueExpressionPrimary();
			return valueExpression;
		}
	}
	
	private ValueExpression parseUnsignedValueExpressionPrimary() {
		if (scanner.getTokenType() == TokenType._ID_) {
			NameChain nameChain = parseNameChain(false);
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				return nameChain;
			}
			if (nameChain.size() > 1) {
				scanner.scan();
				List<ValueExpression> list = 
						new ArrayList<ValueExpression>();
				while (true) {
					ValueExpression valueExpression = 
							parseValueExpression();
					list.add(valueExpression);
					if (scanner.getTokenType() != TokenType._COMMA_) {
						break;
					}
					scanner.scan();
				}
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				FunctionInvocation functionInvocation = 
						new FunctionInvocation(nameChain, list);
				return functionInvocation;
			}
			Name name = nameChain.get(0);
			int beginIndex = name.getBeginIndex();
			int endIndex = name.getEndIndex();
			String content = name.getContent();
			if ("COUNT".equalsIgnoreCase(content)) {
				scanner.scan();
				if (scanner.getTokenType() == TokenType._ASTERISK_) {
					scanner.scan();
					if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"')' expected here.");
					}
					scanner.scan();
					Count count = new Count(beginIndex, endIndex);
					return count;
				}
				Distinct distinct = null;
				if (scanner.getTokenType() == TokenType.DISTINCT) {
					distinct = new Distinct(scanner.getBeginIndex(), 
							scanner.getEndIndex());
					scanner.scan();
				}
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Count count = new Count(beginIndex, endIndex, distinct, valueExpression);
				return count;
			}
			if ("AVG".equalsIgnoreCase(content)) {
				scanner.scan();
				Distinct distinct = null;
				if (scanner.getTokenType() == TokenType.DISTINCT) {
					distinct = new Distinct(scanner.getBeginIndex(), 
							scanner.getEndIndex());
					scanner.scan();
				}
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Avg avg = new Avg(beginIndex, endIndex, distinct, valueExpression);
				return avg;
			}
			if ("MAX".equalsIgnoreCase(content)) {
				scanner.scan();
				Distinct distinct = null;
				if (scanner.getTokenType() == TokenType.DISTINCT) {
					distinct = new Distinct(scanner.getBeginIndex(), 
							scanner.getEndIndex());
					scanner.scan();
				}
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Max max = new Max(beginIndex, endIndex, distinct, valueExpression);
				return max;
			}
			if ("MIN".equalsIgnoreCase(content)) {
				scanner.scan();
				Distinct distinct = null;
				if (scanner.getTokenType() == TokenType.DISTINCT) {
					distinct = new Distinct(scanner.getBeginIndex(), 
							scanner.getEndIndex());
					scanner.scan();
				}
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Min min = new Min(beginIndex, endIndex, distinct, valueExpression);
				return min;
			}
			if ("SUM".equalsIgnoreCase(content)) {
				scanner.scan();
				Distinct distinct = null;
				if (scanner.getTokenType() == TokenType.DISTINCT) {
					distinct = new Distinct(scanner.getBeginIndex(), 
							scanner.getEndIndex());
					scanner.scan();
				}
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Sum sum = new Sum(beginIndex, endIndex, distinct, valueExpression);
				return sum;
			}
			if ("EVERY".equalsIgnoreCase(content)) {
				scanner.scan();
				Distinct distinct = null;
				if (scanner.getTokenType() == TokenType.DISTINCT) {
					distinct = new Distinct(scanner.getBeginIndex(), 
							scanner.getEndIndex());
					scanner.scan();
				}
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Every every = new Every(beginIndex, endIndex, distinct, valueExpression);
				return every;
			}
			if ("NULLIF".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression first = parseValueExpression();
				if (scanner.getTokenType() != TokenType._COMMA_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"',' expected here.");
				}
				scanner.scan();
				ValueExpression second = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				NullIf nullIf = new NullIf(beginIndex, endIndex, first, second);
				return nullIf;
			}
			if ("COALESCE".equalsIgnoreCase(content)) {
				scanner.scan();
				List<ValueExpression> valueExpressionList = new ArrayList<ValueExpression>();
				while (true) {
					ValueExpression valueExpression = parseValueExpression();
					valueExpressionList.add(valueExpression);
					if (scanner.getTokenType() != TokenType._COMMA_) {
						break;
					}
					scanner.scan();
				}
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Coalesce coalesce = new Coalesce(beginIndex, endIndex, valueExpressionList);
				return coalesce;
			}
			if ("TO_DATE".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._COMMA_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"',' expected here.");
				}
				scanner.scan();
				if (scanner.getTokenType() != TokenType._STR_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"String literal expected here.");
				}
				int beginIndex2 = scanner.getBeginIndex();
				int endIndex2 = scanner.getEndIndex();
				String content2 = scanner.getContent();
				if (!"yyyy-MM-dd HH:mm:ss".equals(content2) &&
					!"yyyy-MM-dd".equals(content2) &&
					!"HH:mm:ss".equals(content2)) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'yyyy-MM-dd HH:mm:ss', 'yyyy-MM-dd' or 'HH:mm:ss' expected here.");
				}
				StringLiteral pattern = new StringLiteral(
						beginIndex2, endIndex2, content2);
				scanner.scan();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				ToDate toDate =  new ToDate(beginIndex, endIndex, 
						valueExpression, pattern);
				return toDate;
			}
			if ("TO_CHAR".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._COMMA_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"',' expected here.");
				}
				scanner.scan();
				if (scanner.getTokenType() != TokenType._STR_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"String literal expected here.");
				}
				int beginIndex2 = scanner.getBeginIndex();
				int endIndex2 = scanner.getEndIndex();
				String content2 = scanner.getContent();
				if (!"yyyy-MM-dd HH:mm:ss".equals(content2) &&
					!"yyyy-MM-dd".equals(content2) &&
					!"HH:mm:ss".equals(content2)) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'yyyy-MM-dd HH:mm:ss', 'yyyy-MM-dd' or 'HH:mm:ss' expected here.");
				}
				StringLiteral pattern = new StringLiteral(
						beginIndex2, endIndex2, content2);
				scanner.scan();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				ToChar toChar =  new ToChar(beginIndex, endIndex, 
						valueExpression, pattern);
				return toChar;
			}
			if ("SUBSTRING".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType.FROM) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'FROM' expected here.");
				}
				scanner.scan();
				ValueExpression startPosition = parseValueExpression();
				ValueExpression stringLength = null;
				if (scanner.getTokenType() == TokenType.FOR) {
					scanner.scan();
					stringLength = parseValueExpression();
				}
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Substring substring = new Substring(beginIndex, endIndex, 
						valueExpression, startPosition, stringLength);
				return substring;
			}
			if ("UPPER".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Upper upper = new Upper(beginIndex, endIndex, valueExpression);
				return upper;
			}
			if ("LOWER".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Lower lower = new Lower(beginIndex, endIndex, valueExpression);
				return lower;
			}
			if ("EXTRACT".equalsIgnoreCase(content)) {
				scanner.scan();
				TokenType tokenType = scanner.getTokenType();
				ExtractField extractField = null;
				if (tokenType == TokenType.YEAR) {
					extractField = ExtractField.YEAR;
				} else if (tokenType == TokenType.MONTH) {
					extractField = ExtractField.MONTH;
				} else if (tokenType == TokenType.DAY) {
					extractField = ExtractField.DAY;
				} else if (tokenType == TokenType.HOUR) {
					extractField = ExtractField.HOUR;
				} else if (tokenType == TokenType.MINUTE) {
					extractField = ExtractField.MINUTE;
				} else if (tokenType == TokenType.SECOND) {
					extractField = ExtractField.SECOND;
				} else {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'YEAR', 'MONTH', 'DAY', 'HOUR', 'MINUTE' or 'SECOND' expected here.");
				}
				scanner.scan();
				if (scanner.getTokenType() != TokenType.FROM) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'FROM' expected here.");
				}
				scanner.scan();
				ValueExpression extractSource = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				ExtractExpression extractExpression = new ExtractExpression(
						beginIndex, endIndex, extractField, extractSource);
				return extractExpression;
			}
			if ("POSITION".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression1 = parseValueExpression();
				if (!(valueExpression1 instanceof BooleanValueExpression)) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), valueExpression1.getBeginIndex(), 
							"Error expression.");
				}
				BooleanValue booleanValue = (BooleanValue) valueExpression1;
				if (booleanValue.size() > 1) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), valueExpression1.getBeginIndex(), 
							"Error expression.");
				}
				BooleanTerm booleanTerm = booleanValue.get(0);
				if (booleanTerm.size() > 1) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), valueExpression1.getBeginIndex(), 
							"Error expression.");
				}
				BooleanFactor booleanFactor = booleanTerm.get(0);
				BooleanValueExpression booleanValueExpression = 
						booleanFactor.getBooleanValueExpression();
				if (!(booleanValueExpression instanceof BooleanTest)) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), valueExpression1.getBeginIndex(), 
							"Error expression.");
				}
				BooleanTest booleanTest = (BooleanTest) booleanValueExpression;
				if (booleanTest.getNot() == true) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), valueExpression1.getBeginIndex(), 
							"Error expression.");
				}
				if (booleanTest.getTruthValue() != null) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), valueExpression1.getBeginIndex(), 
							"Error expression.");
				}
				BooleanValueExpression b2 = booleanTest.getBooleanValueExpression();
				if (!(b2 instanceof InPredicate)) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), valueExpression1.getBeginIndex(), 
							"Error expression.");
				}
				InPredicate inPredicate = (InPredicate) b2;
				if (inPredicate.getNot() == true) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), valueExpression1.getBeginIndex(), 
							"Error expression.");
				}
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				ValueExpression _valueExpression1 = inPredicate.getValueExpression();
				ValueExpression _valueExpression2 = inPredicate.getInValueList().get(0);
				PositionExpression positionExpression = 
						new PositionExpression(beginIndex, endIndex, 
								_valueExpression1, _valueExpression2);
				return positionExpression;
			}
			if ("CARDINALITY".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				CardinalityExpression cardinalityExpression = 
						new CardinalityExpression(beginIndex, endIndex, valueExpression);
				return cardinalityExpression;
			}
			if ("ABS".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				AbsoluteValueExpression absoluteValueExpression = 
						new AbsoluteValueExpression(beginIndex, endIndex, 
								valueExpression);
				return absoluteValueExpression;
			}
			if ("MOD".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression dividend = parseValueExpression();
				if (scanner.getTokenType() != TokenType._COMMA_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"',' expected here.");
				}
				scanner.scan();
				ValueExpression divisor = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				ModulusExpression modulusExpression = 
						new ModulusExpression(beginIndex, endIndex, dividend, divisor);
				return modulusExpression;
			}
			if ("CHAR_LENGTH".equalsIgnoreCase(content) || 
				"CHARACTER_LENGTH".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				CharLengthExpression charLengthExpression = 
						new CharLengthExpression(beginIndex, endIndex, valueExpression);
				return charLengthExpression;
			}
			if ("OCTET_LENGTH".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				OctetLengthExpression octetLengthExpression = 
						new OctetLengthExpression(beginIndex, endIndex, valueExpression);
				return octetLengthExpression;
			}
			if ("BIT_LENGTH".equalsIgnoreCase(content)) {
				scanner.scan();
				ValueExpression valueExpression = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				BitLengthExpression bitLengthExpression = 
						new BitLengthExpression(beginIndex, endIndex, valueExpression);
				return bitLengthExpression;
			}
			if ("CONVERT".equalsIgnoreCase(content)) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
						"Not support CONVERT.");
			}
			if ("TRIM".equalsIgnoreCase(content)) {
				scanner.scan();
				TokenType tokenType = scanner.getTokenType();
				if (tokenType == TokenType.LEADING ||
					tokenType == TokenType.TRAILING ||
					tokenType == TokenType.BOTH) {
					TrimSpecification trimSpecification = null;
					if (tokenType == TokenType.LEADING) {
						trimSpecification = TrimSpecification.LEADING;
					} else if (tokenType == TokenType.TRAILING) {
						trimSpecification = TrimSpecification.TRAILING;
					} else {
						trimSpecification = TrimSpecification.BOTH;
					}
					scanner.scan();
					if (scanner.getTokenType() == TokenType.FROM) {
						scanner.scan();
						ValueExpression trimCharacter = null;
						ValueExpression trimSource = parseValueExpression();
						if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
							throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
									"')' expected here.");
						}
						scanner.scan();
						Trim trim = new Trim(beginIndex, endIndex, trimSpecification, 
								trimCharacter, trimSource);
						return trim;
					}
					ValueExpression trimCharacter = parseValueExpression();
					if (scanner.getTokenType() != TokenType.FROM) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"'FROM' expected here.");
					}
					scanner.scan();
					ValueExpression trimSource = parseValueExpression();
					if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"')' expected here.");
					}
					scanner.scan();
					Trim trim = new Trim(beginIndex, endIndex, trimSpecification, 
							trimCharacter, trimSource);
					return trim;
				}
				if (tokenType == TokenType.FROM) {
					scanner.scan();
					TrimSpecification trimSpecification = null;
					ValueExpression trimCharacter = null;
					ValueExpression trimSource = parseValueExpression();
					if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"')' expected here.");
					}
					scanner.scan();
					Trim trim = new Trim(beginIndex, endIndex, trimSpecification, 
							trimCharacter, trimSource);
					return trim;
				}
				TrimSpecification trimSpecification = null;
				ValueExpression trimCharacter = null;
				ValueExpression trimSource = parseValueExpression();
				if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"')' expected here.");
				}
				scanner.scan();
				Trim trim = new Trim(beginIndex, endIndex, trimSpecification, 
						trimCharacter, trimSource);
				return trim;
			}
			if ("OVERLAY".equalsIgnoreCase(content)) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), beginIndex, 
						"Not support OVERLAY.");
			}
			scanner.scan();
			List<ValueExpression> list = 
					new ArrayList<ValueExpression>();
			while (true) {
				ValueExpression valueExpression = 
						parseValueExpression();
				list.add(valueExpression);
				if (scanner.getTokenType() != TokenType._COMMA_) {
					break;
				}
				scanner.scan();
			}
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			FunctionInvocation functionInvocation = 
					new FunctionInvocation(nameChain, list);
			return functionInvocation;
		}
		if (scanner.getTokenType() == TokenType.ANY) {
			int beginIndex = scanner.getBeginIndex();
			int endIndex  = scanner.getEndIndex();
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			scanner.scan();
			Distinct distinct = null;
			if (scanner.getTokenType() == TokenType.DISTINCT) {
				distinct = new Distinct(scanner.getBeginIndex(), 
						scanner.getEndIndex());
				scanner.scan();
			}
			ValueExpression valueExpression = parseValueExpression();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			Any any = new Any(beginIndex, endIndex, distinct, valueExpression);
			return any;
		}
		if (scanner.getTokenType() == TokenType.SOME) {
			int beginIndex = scanner.getBeginIndex();
			int endIndex  = scanner.getEndIndex();
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			scanner.scan();
			Distinct distinct = null;
			if (scanner.getTokenType() == TokenType.DISTINCT) {
				distinct = new Distinct(scanner.getBeginIndex(), 
						scanner.getEndIndex());
				scanner.scan();
			}
			ValueExpression valueExpression = parseValueExpression();
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			Some some = new Some(beginIndex, endIndex, distinct, valueExpression);
			return some;
		}
		if (scanner.getTokenType() == TokenType.GROUPING) {
			int beginIndex = scanner.getBeginIndex();
			int endIndex  = scanner.getEndIndex();
			scanner.scan();
			if (scanner.getTokenType() != TokenType._LEFT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"'(' expected here.");
			}
			scanner.scan();
			NameChain nameChain = parseNameChain(false);
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			Grouping grouping = new Grouping(beginIndex, endIndex, nameChain);
			return grouping;
		}
		if (scanner.getTokenType() == TokenType._STR_) {
			int beginIndex = scanner.getBeginIndex();
			int endIndex = scanner.getEndIndex();
			String content = scanner.getContent();
			StringLiteral stringLiteral = new StringLiteral(
					beginIndex, endIndex, content);
			scanner.scan();
			return stringLiteral;
		}
		if (scanner.getTokenType() == TokenType._NUM_) {
			int beginIndex = scanner.getBeginIndex();
			int endIndex = scanner.getEndIndex();
			String content = scanner.getContent();
			NumericLiteral numericLiteral = new NumericLiteral(
					beginIndex, endIndex, content);
			scanner.scan();
			return numericLiteral;
		}
		if (scanner.getTokenType() == TokenType._PARAM_) {
			int beginIndex = scanner.getBeginIndex();
			int endIndex = scanner.getEndIndex();
			String content = scanner.getContent();
			Parameter parameter = new Parameter(
					beginIndex, endIndex, content);
			scanner.scan();
			return parameter;
		}
		if (scanner.getTokenType() == TokenType.DATE) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() == TokenType._STR_) {
				int beginIndex2 = scanner.getBeginIndex();
				int endIndex = scanner.getEndIndex();
				String content = scanner.getContent();
				StringLiteral dateString = new StringLiteral(
						beginIndex2, endIndex, content);
				DateLiteral dateLiteral = new DateLiteral(beginIndex, 
						endIndex, dateString);
				scanner.scan();
				return dateLiteral;
			}
			if (scanner.getTokenType() == TokenType._PARAM_) {
				int beginIndex2 = scanner.getBeginIndex();
				int endIndex = scanner.getEndIndex();
				String content = scanner.getContent();
				Parameter parameter = new Parameter(
						beginIndex2, endIndex, content);
				DateLiteral dateLiteral = new DateLiteral(beginIndex, 
						endIndex, parameter);
				scanner.scan();
				return dateLiteral;
			}
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Date string or parameter expected here.");
		}
		if (scanner.getTokenType() == TokenType.TIME) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() == TokenType._STR_) {
				int beginIndex2 = scanner.getBeginIndex();
				int endIndex = scanner.getEndIndex();
				String content = scanner.getContent();
				StringLiteral dateString = new StringLiteral(
						beginIndex2, endIndex, content);
				TimeLiteral timeLiteral = new TimeLiteral(beginIndex, 
						endIndex, dateString);
				scanner.scan();
				return timeLiteral;
			}
			if (scanner.getTokenType() == TokenType._PARAM_) {
				int beginIndex2 = scanner.getBeginIndex();
				int endIndex = scanner.getEndIndex();
				String content = scanner.getContent();
				Parameter parameter = new Parameter(
						beginIndex2, endIndex, content);
				TimeLiteral timeLiteral = new TimeLiteral(beginIndex, 
						endIndex, parameter);
				scanner.scan();
				return timeLiteral;
			}
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Time string or parameter expected here.");
		}
		if (scanner.getTokenType() == TokenType.TIMESTAMP) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() == TokenType._STR_) {
				int beginIndex2 = scanner.getBeginIndex();
				int endIndex = scanner.getEndIndex();
				String content = scanner.getContent();
				StringLiteral dateString = new StringLiteral(
						beginIndex2, endIndex, content);
				TimestampLiteral timestampLiteral = 
						new TimestampLiteral(beginIndex, 
								endIndex, dateString);
				scanner.scan();
				return timestampLiteral;
			}
			if (scanner.getTokenType() == TokenType._PARAM_) {
				int beginIndex2 = scanner.getBeginIndex();
				int endIndex = scanner.getEndIndex();
				String content = scanner.getContent();
				Parameter parameter = new Parameter(
						beginIndex2, endIndex, content);
				TimestampLiteral timestampLiteral = 
						new TimestampLiteral(beginIndex, 
								endIndex, parameter);
				scanner.scan();
				return timestampLiteral;
			}
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Timestamp string or parameter expected here.");
		}
		if (scanner.getTokenType() == TokenType.CURRENT_DATE) {
			int beginIndex = scanner.getBeginIndex();
			int endIndex = scanner.getEndIndex();
			CurrentDate currentDate = new CurrentDate(beginIndex, endIndex);
			scanner.scan();
			return currentDate;
		}
		if (scanner.getTokenType() == TokenType.CURRENT_TIME) {
			int beginIndex = scanner.getBeginIndex();
			int endIndex = scanner.getEndIndex();
			CurrentTime currentTime = new CurrentTime(beginIndex, endIndex);
			scanner.scan();
			return currentTime;
		}
		if (scanner.getTokenType() == TokenType.CURRENT_TIMESTAMP) {
			int beginIndex = scanner.getBeginIndex();
			int endIndex = scanner.getEndIndex();
			CurrentTimestamp currentTimestamp = new CurrentTimestamp(
					beginIndex, endIndex);
			scanner.scan();
			return currentTimestamp;
		}
		if (scanner.getTokenType() == TokenType.CASE) {
			int beginIndex = scanner.getBeginIndex();
			scanner.scan();
			if (scanner.getTokenType() == TokenType.WHEN) {
				scanner.scan();
				BooleanValueExpression searchCondition = 
						parseBooleanValueExpression();
				if (scanner.getTokenType() != TokenType.THEN) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'THEN' expected here.");
				}
				scanner.scan();
				ValueExpression result = parseValueExpression();
				SearchedWhenClause searchedWhenClause = 
						new SearchedWhenClause(searchCondition, result);
				List<SearchedWhenClause> list = 
						new ArrayList<SearchedWhenClause>();
				list.add(searchedWhenClause);
				while (scanner.getTokenType() == TokenType.WHEN) {
					scanner.scan();
					searchCondition = 
							parseBooleanValueExpression();
					if (scanner.getTokenType() != TokenType.THEN) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"'THEN' expected here.");
					}
					scanner.scan();
					result = parseValueExpression();
					searchedWhenClause = 
							new SearchedWhenClause(searchCondition, result);
					list.add(searchedWhenClause);
				}
				ElseClause elseClause = null;
				if (scanner.getTokenType() == TokenType.ELSE) {
					scanner.scan();
					result = parseValueExpression();
					elseClause = new ElseClause(result);
				}
				if (scanner.getTokenType() != TokenType.END) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'END' expected here.");
				}
				int endIndex = scanner.getEndIndex();
				scanner.scan();
				SearchedCase searchedCase = new SearchedCase(beginIndex, 
						endIndex, list, elseClause);
				return searchedCase;
			} else {
				ValueExpression caseOperand = parseValueExpression();
				List<SimpleWhenClause> list = new ArrayList<SimpleWhenClause>();
				if (scanner.getTokenType() != TokenType.WHEN) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'WHEN' expected here.");
				}
				scanner.scan();
				ValueExpression whenOperand = parseValueExpression();
				if (scanner.getTokenType() != TokenType.THEN) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'THEN' expected here.");
				}
				scanner.scan();
				ValueExpression result = parseValueExpression();
				SimpleWhenClause simpleWhenClause = 
						new SimpleWhenClause(whenOperand, result);
				list.add(simpleWhenClause);
				while (scanner.getTokenType() == TokenType.WHEN) {
					scanner.scan();
					whenOperand = parseValueExpression();
					if (scanner.getTokenType() != TokenType.THEN) {
						throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
								"'THEN' expected here.");
					}
					scanner.scan();
					result = parseValueExpression();
					simpleWhenClause = 
							new SimpleWhenClause(whenOperand, result);
					list.add(simpleWhenClause);
				}
				ElseClause elseClause = null;
				if (scanner.getTokenType() == TokenType.ELSE) {
					scanner.scan();
					result = parseValueExpression();
					elseClause = new ElseClause(result);
				}
				if (scanner.getTokenType() != TokenType.END) {
					throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
							"'END' expected here.");
				}
				int endIndex = scanner.getEndIndex();
				scanner.scan();
				SimpleCase simpleCase = new SimpleCase(beginIndex, 
						endIndex, caseOperand, 
						list, elseClause);
				return simpleCase;
			}
		}
		if (scanner.getTokenType() == TokenType._LEFT_PAREN_) {
			int subqueryBeginIndex = scanner.getBeginIndex();
			scanner.scan();
			ValueExpression valueExpression = null;
			if (scanner.getTokenType() == TokenType.SELECT) {
				valueExpression = parseSubquery(subqueryBeginIndex);
			} else {
				valueExpression = parseValueExpression();
			}
			if (scanner.getTokenType() != TokenType._RIGHT_PAREN_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"')' expected here.");
			}
			scanner.scan();
			return valueExpression;
		}
		throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
				"Error token.");
	}

	private NameChain parseNameChain(boolean tableName) {
		if (scanner.getTokenType() != TokenType._ID_) {
			throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
					"Identifier expected here.");
		}
		Name name = new Name(scanner.getBeginIndex(), 
				scanner.getEndIndex(), scanner.getContent());
		List<Name> list = new ArrayList<Name>(3);
		list.add(name);
		while (true) {
			scanner.scan();
			if (scanner.getTokenType() != TokenType._PERIOD_) {
				break;
			}
			scanner.scan();
			if (scanner.getTokenType() != TokenType._ID_) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), scanner.getBeginIndex(), 
						"Identifier expected here.");
			}
			name = new Name(scanner.getBeginIndex(), 
					scanner.getEndIndex(), scanner.getContent());
			list.add(name);
		}
		NameChain nameChain = new NameChain(list);
		if (tableName == true) {
			try {
				configuration.addTableName(nameChain);
			} catch (Exception e) {
				throw Sql4jException.getSql4jException(scanner.getSourceCode(), nameChain.getBeginIndex(), 
						"Loading table structure error.", e);
			}
		}
		return nameChain;
	}

}
