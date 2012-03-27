package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetectorBase;

final class MySQLTypeFormat extends TypeDetectorBase<Object, Appendable> {

	private MySQLTypeFormat() {
	}

	static final MySQLTypeFormat INSTANCE = new MySQLTypeFormat();

	@Override
	public final Object inBoolean(Appendable sql) throws Throwable {
		sql.append("bit");
		return null;
	}

	@Override
	public final Object inShort(Appendable sql) throws Throwable {
		sql.append("smallint");
		return null;
	}

	@Override
	public final Object inInt(Appendable sql) throws Throwable {
		sql.append("int");
		return null;
	}

	@Override
	public final Object inLong(Appendable sql) throws Throwable {
		sql.append("bigint");
		return null;
	}

	@Override
	public final Object inNumeric(Appendable sql, int precision, int scale)
			throws Throwable {
		sql.append("decimal(");
		sql.append(Convert.toString(precision));
		sql.append(',');
		sql.append(Convert.toString(scale));
		sql.append(')');
		return null;
	}

	@Override
	public final Object inFloat(Appendable sql) throws Throwable {
		sql.append("float");
		return null;
	}

	@Override
	public final Object inDouble(Appendable sql) throws Throwable {
		sql.append("double");
		return null;
	}

	@Override
	public final Object inChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("char(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	@Override
	public final Object inVarChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("varchar(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	@Override
	public final Object inText(Appendable sql) throws Throwable {
		sql.append("longtext");
		return null;
	}

	@Override
	public final Object inNChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("nchar(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(")");
		return null;
	}

	@Override
	public final Object inNVarChar(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("nvarchar(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(')');
		return null;
	}

	@Override
	public final Object inNText(Appendable sql) throws Throwable {
		sql.append("longtext charset utf8");
		return null;
	}

	@Override
	public final Boolean inBinary(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("binary(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(")");
		return null;
	}

	@Override
	public final Object inVarBinary(Appendable sql, SequenceDataType type)
			throws Throwable {
		sql.append("varbinary(");
		sql.append(Convert.toString(type.getMaxLength()));
		sql.append(")");
		return null;
	}

	@Override
	public final Object inBlob(Appendable sql) throws Throwable {
		sql.append("longblob");
		return null;
	}

	@Override
	public final Object inDate(Appendable sql) throws Throwable {
		sql.append("datetime");
		return null;
	}

	@Override
	public final Object inGUID(Appendable sql) throws Throwable {
		sql.append("binary(16)");
		return null;
	}
}
