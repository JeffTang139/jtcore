package org.eclipse.jt.core.impl;

class SystemVariables {

	private SystemVariables() {
	}

	static boolean DEBUG_SQL_DML = Boolean
			.getBoolean("org.eclipse.jt.debug.sql.dml");

	static boolean DEBUG_SQL_DDL = Boolean
			.getBoolean("org.eclipse.jt.debug.sql.ddl");

	static boolean DEBUG_SQL_DURATION = Boolean
			.getBoolean("org.eclipse.jt.debug.sql.duration");

	static boolean DEBUG_SYNC = Boolean.getBoolean("org.eclipse.jt.debug.sync");

	static final boolean ORACLE_USER_AUTO_UPPERCASE = booleanOf(
			"org.eclipse.jt.oracle-user-auto-uppercase", true);

	static final String SQLSERVER_DEFAULT_SCHEMA = stringOf(
			"org.eclipse.jt.sqlserver-default-schema", "dbo");

	static final String SQLSERVER_DEFAULT_COLLATION = stringOf(
			"org.eclipse.jt.sqlserver-default-collation", "Chinese_PRC_CS_AS");

	static final boolean REFERENCE_RENAME_ALIAS = booleanOf(
			"org.eclipse.jt.reference-rename-alias", true);

	static final String NONE_DNA_INDEX = stringOf(
			"org.eclipse.jt.none-dna-index", "UIX_");

	static final boolean VALIDATE_EXPR_DOMAIN = booleanOf(
			"org.eclipse.jt.validate-expr-domain", false);

	static final boolean VALIDATE_ASSIGN_TYPE = booleanOf(
			"com.jiuq.dna.validate-assign-type", false);

	static final boolean VALIDATE_DERIVED_DOMAIN = booleanOf(
			"org.eclipse.jt.validate-derived-domain", false);

	static final boolean FROM_DUMMY_EXCLUSIVE = booleanOf(
			"org.eclipse.jt.from-dummy-exclusive", true);

	static final boolean SUBSELECT_ORDERBY_THROW_EXCEPTION = booleanOf(
			"org.eclipse.jt.subselect-orderby-throw-exception", false);

	static final boolean CUBE_GROUPBY_THROW_EXCEPTION = booleanOf(
			"org.eclipse.jt.cube-groupby-throw-exception", true);

	static final boolean TABLE_NAME_KEYWORD_EXCEPTION = booleanOf(
			"org.eclipse.jt.table-name-keyword-exception", false);

	static final int ORM_BYRECIDS_DELETE = intOf(
			"org.eclipse.jt.orm-byrecids-delete", 10);

	static final boolean CHAR_FUNC_NO_LOB = booleanOf(
			"org.eclipse.jt.char-func-no-lob", true);

	private static final boolean booleanOf(String prop, boolean defaultValue) {
		String val = System.getProperty(prop);
		return val == null ? defaultValue : Boolean.valueOf(val);
	}

	private static final String stringOf(String prop, String defaultValue) {
		String val = System.getProperty(prop);
		return val == null ? defaultValue : val;
	}

	private static final int intOf(String prop, int defualtValue) {
		String val = System.getProperty(prop);
		return val == null ? defualtValue : Integer.valueOf(val);
	}

}
