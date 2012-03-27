package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.exception.NotDBTypeException;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.EnumType;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;

public final class SQLServerTypeFormatter implements
		TypeDetector<Object, Appendable> {

	public static final SQLServerTypeFormatter instance = new SQLServerTypeFormatter();

	private SQLServerTypeFormatter() {
	}

	public Object inBinary(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("[binary](");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(")");
		return null;
	}

	public Object inBlob(Appendable sql) throws Throwable {
		sql.append("[varbinary](max)");
		return null;
	}

	public Object inBoolean(Appendable sql) throws Throwable {
		sql.append("[bit]");
		return null;
	}

	public Object inByte(Appendable sql) throws Throwable {
		throw new NotDBTypeException("byte");
	}

	public Object inBytes(Appendable sql, SequenceDataType type)
			throws Throwable {
		throw new NotDBTypeException("bytes");
	}

	public Object inChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("[char](");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	public Object inDate(Appendable sql) throws Throwable {
		sql.append("[datetime]");
		return null;
	}

	public Object inDouble(Appendable sql) throws Throwable {
		sql.append("[float]");
		return null;
	}

	public Object inEnum(Appendable sql, EnumType<?> type) throws Throwable {
		throw new NotDBTypeException("enum");
	}

	public Object inFloat(Appendable sql) throws Throwable {
		sql.append("[real]");
		return null;
	}

	public Object inGUID(Appendable sql) throws Throwable {
		sql.append("[binary](16)");
		return null;
	}

	public Object inInt(Appendable sql) throws Throwable {
		sql.append("[int]");
		return null;
	}

	public Object inLong(Appendable sql) throws Throwable {
		sql.append("[bigint]");
		return null;
	}

	public Object inModel(Appendable sql, ModelDefine type) throws Throwable {
		throw new NotDBTypeException("模型定义");
	}

	public Object inNChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("[nchar](");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	public Object inNText(Appendable sql) throws Throwable {
		sql.append("[nvarchar](max)");
		return null;
	}

	public Object inNVarChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("[nvarchar](");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	public Object inNumeric(Appendable sql, int precision, int scale)
			throws Throwable {
		sql.append("[numeric](");
		sql.append(Convert.toString(precision));
		sql.append(',');
		sql.append(Convert.toString(scale));
		sql.append(')');
		return null;
	}

	public Object inObject(Appendable sql, ObjectDataType type)
			throws Throwable {
		throw new NotDBTypeException("对象类型");
	}

	public Object inQuery(Appendable sql, QueryStatementDefine type)
			throws Throwable {
		throw new NotDBTypeException("查询定义");
	}

	public Object inRecordSet(Appendable sql) throws Throwable {
		throw new NotDBTypeException("结果集");
	}

	public Object inResource(Appendable sql, Class<?> facadeClass,
			Object category) throws Throwable {
		throw new NotDBTypeException("资源");
	}

	public Object inShort(Appendable sql) throws Throwable {
		sql.append("[smallint]");
		return null;
	}

	public Object inString(Appendable sql, SequenceDataType type)
			throws Throwable {
		throw new NotDBTypeException("string");
	}

	public Object inStruct(Appendable sql, StructDefine type) throws Throwable {
		throw new NotDBTypeException("结构定义");
	}

	public Object inTable(Appendable sql) throws Throwable {
		throw new NotDBTypeException("表定义");
	}

	public Object inText(Appendable sql) throws Throwable {
		sql.append("[varchar](max)");
		return null;
	}

	public Object inUnknown(Appendable sql) throws Throwable {
		throw new NotDBTypeException("未知");
	}

	public Object inVarBinary(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("[varbinary](");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(")");
		return null;
	}

	public Object inVarChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("[varchar](");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	public Object inNull(Appendable userData) throws Throwable {
		throw new NotDBTypeException("空类型");
	}

	public Object inCharacter(Appendable userData) throws Throwable {
		throw new NotDBTypeException("字符类型");
	}
}
