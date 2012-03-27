package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetectorBase;

public final class OracleTypeFormatter extends
		TypeDetectorBase<Object, Appendable> {

	public static final OracleTypeFormatter instance = new OracleTypeFormatter();

	private OracleTypeFormatter() {
	}

	@Override
	public Object inBoolean(Appendable sql) throws Throwable {
		sql.append("number(1)");
		return null;
	}

	@Override
	public Object inShort(Appendable sql) throws Throwable {
		sql.append("number(5)");
		return null;
	}

	@Override
	public Object inInt(Appendable sql) throws Throwable {
		sql.append("number(10)");
		return null;
	}

	@Override
	public Object inLong(Appendable sql) throws Throwable {
		sql.append("number(19)");
		return null;
	}

	@Override
	public Object inFloat(Appendable sql) throws Throwable {
		sql.append("real");
		return null;
	}

	@Override
	public Object inDouble(Appendable sql) throws Throwable {
		sql.append("float");
		return null;
	}

	@Override
	public Object inNumeric(Appendable sql, int precision, int scale)
			throws Throwable {
		sql.append("number(");
		sql.append(Convert.toString(precision));
		sql.append(',');
		sql.append(Convert.toString(scale));
		sql.append(')');
		return null;
	}

	@Override
	public Object inChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("char(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	@Override
	public Object inVarChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("varchar2(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	@Override
	public Object inText(Appendable sql) throws Throwable {
		sql.append("clob");
		return null;
	}

	@Override
	public Object inNChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("nchar(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	@Override
	public Object inNVarChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("nvarchar2(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	@Override
	public Object inNText(Appendable sql) throws Throwable {
		sql.append("nclob");
		return null;
	}

	@Override
	public Object inBinary(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("raw(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(")");
		return null;
	}

	@Override
	public Object inVarBinary(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("raw(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(")");
		return null;
	}

	@Override
	public Object inBlob(Appendable sql) throws Throwable {
		sql.append("blob");
		return null;
	}

	@Override
	public Object inDate(Appendable sql) throws Throwable {
		sql.append("timestamp(3)");
		return null;
	}

	@Override
	public Object inGUID(Appendable sql) throws Throwable {
		sql.append("raw(16)");
		return null;
	}

}