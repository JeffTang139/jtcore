package org.eclipse.jt.core.impl;

import java.sql.Types;

/**
 * SqlTypes的包装类,包装java.sql.Types
 * 
 * @author Jeff Tang
 * 
 */
public final class SQLTypesWrapper {

	public final static Integer BIT = Types.BIT;

	public final static Integer TINYINT = Types.TINYINT;

	public final static Integer SMALLINT = Types.SMALLINT;

	public final static Integer INTEGER = Types.INTEGER;

	public final static Integer BIGINT = Types.BIGINT;

	public final static Integer FLOAT = Types.FLOAT;

	public final static Integer REAL = Types.REAL;

	public final static Integer DOUBLE = Types.DOUBLE;

	public final static Integer NUMERIC = Types.NUMERIC;

	public final static Integer DECIMAL = Types.DECIMAL;

	public final static Integer CHAR = Types.CHAR;

	public final static Integer VARCHAR = Types.VARCHAR;

	public final static Integer LONGVARCHAR = Types.LONGVARCHAR;

	public final static Integer DATE = Types.DATE;

	public final static Integer TIME = Types.TIME;

	public final static Integer TIMESTAMP = Types.TIMESTAMP;

	public final static Integer BINARY = Types.BINARY;

	public final static Integer VARBINARY = Types.VARBINARY;

	public final static Integer LONGVARBINARY = Types.LONGVARBINARY;

	public final static Integer NULL = Types.NULL;

	public final static Integer OTHER = Types.OTHER;

	public final static Integer JAVA_OBJECT = Types.JAVA_OBJECT;

	public final static Integer DISTINCT = Types.DISTINCT;

	public final static Integer STRUCT = Types.STRUCT;

	public final static Integer ARRAY = Types.ARRAY;

	public final static Integer BLOB = Types.BLOB;

	public final static Integer CLOB = Types.CLOB;

	public final static Integer REF = Types.REF;

	public final static Integer DATALINK = Types.DATALINK;

	public final static Integer BOOLEAN = Types.BOOLEAN;

	// ------------------------- JDBC 4.0 -----------------------------------

	// public final static Integer ROWID = -8;
	//
	// public static final Integer NCHAR = -15;
	//	
	// public static final Integer NVARCHAR = -9;
	//
	// public static final Integer LONGNVARCHAR = -16;
	//
	// public static final Integer NCLOB = 2011;
	//
	// public static final Integer SQLXML = 2009;
}
