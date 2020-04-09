package lee.bright.sql4j.ql;

/**
 * @author Bright Lee
 */
public enum StatementType {
	
	QUERY_SPECIFICATION,
	INTERSECT,
	UNION,
	EXCEPT,
	INSERT_STATEMENT,
	UPDATE_STATEMENT,
	DELETE_STATEMENT,
	TABLE_DEFINITION,
	DROP_TABLE_STATEMENT,
	DROP_INDEX_STATEMENT,
	CREATE_INDEX_STATEMENT,
	ADD_COLUMN_DEFINITION,
	DROP_COLUMN_DEFINITION,
	ALTER_COLUMN_DEFINITION,
	ADD_TABLE_CONSTRAINT_DEFINITION,
	DROP_TABLE_CONSTRAINT_DEFINITION,
	DROP_PRIMARY_KEY_DEFINITION,
	ADD_PRIMARY_KEY_DEFINITION,
	MODIFY_COLUMN_DEFINITION,
	CALL_STATEMENT;

}