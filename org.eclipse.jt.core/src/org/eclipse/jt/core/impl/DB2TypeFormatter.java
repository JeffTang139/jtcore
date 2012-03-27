package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.EnumType;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;

final class DB2TypeFormatter implements TypeDetector<Object, Appendable> {

	static final DB2TypeFormatter INSTANCE = new DB2TypeFormatter();

	private DB2TypeFormatter() {
	}

	public Object inBoolean(Appendable str) throws Throwable {
		str.append("smallint");
		return null;
	}

	public Object inByte(Appendable str) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inShort(Appendable str) throws Throwable {
		str.append("smallint");
		return null;
	}

	public Object inInt(Appendable str) throws Throwable {
		str.append("integer");
		return null;
	}

	public Object inLong(Appendable str) throws Throwable {
		str.append("bigint");
		return null;
	}

	public Object inDate(Appendable str) throws Throwable {
		str.append("timestamp");
		return null;
	}

	public Object inFloat(Appendable str) throws Throwable {
		str.append("real");
		return null;
	}

	public Object inDouble(Appendable str) throws Throwable {
		str.append("double");
		return null;
	}

	public Object inNumeric(Appendable str, int precision, int scale)
			throws Throwable {
		str.append("decimal(");
		str.append(Convert.toString(precision));
		str.append(',');
		str.append(Convert.toString(scale));
		str.append(')');
		return null;
	}

	public Object inString(Appendable str, SequenceDataType type)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inObject(Appendable str, ObjectDataType type)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inChar(Appendable str, SequenceDataType type)
			throws Throwable {
		str.append("varchar(");
		str.append(Convert.toString(type.getMaxLength()));
		str.append(')');
		return null;
	}

	public Object inVarChar(Appendable str, SequenceDataType type)
			throws Throwable {
		str.append("varchar(");
		str.append(Convert.toString(type.getMaxLength()));
		str.append(')');
		return null;
	}

	public Object inText(Appendable str) throws Throwable {
		str.append("clob");
		return null;
	}

	public Object inNVarChar(Appendable str, SequenceDataType type)
			throws Throwable {
		str.append("varchar(");
		str.append(Convert.toString(type.getMaxLength() * 2));
		str.append(')');
		return null;
	}

	public Object inNChar(Appendable str, SequenceDataType type)
			throws Throwable {
		str.append("varchar(");
		str.append(Convert.toString(type.getMaxLength() * 2));
		str.append(')');
		return null;
	}

	public Object inNText(Appendable str) throws Throwable {
		str.append("clob");
		return null;
	}

	public Object inBytes(Appendable str, SequenceDataType type)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inBinary(Appendable str, SequenceDataType type)
			throws Throwable {
		str.append("varchar(");
		str.append(Convert.toString(type.getMaxLength()));
		str.append(") for bit data");
		return null;
	}

	public Object inVarBinary(Appendable str, SequenceDataType type)
			throws Throwable {
		str.append("varchar(");
		str.append(Convert.toString(type.getMaxLength()));
		str.append(") for bit data");
		return null;
	}

	public Object inBlob(Appendable str) throws Throwable {
		str.append("blob");
		return null;
	}

	public Object inGUID(Appendable str) throws Throwable {
		str.append("char(16) for bit data");
		return null;
	}

	public Object inEnum(Appendable str, EnumType<?> type) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inResource(Appendable str, Class<?> facadeClass,
			Object category) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inUnknown(Appendable str) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inStruct(Appendable str, StructDefine type) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inModel(Appendable str, ModelDefine type) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inQuery(Appendable str, QueryStatementDefine type)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inTable(Appendable str) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inRecordSet(Appendable str) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inNull(Appendable str) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public Object inCharacter(Appendable userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

}
