package org.eclipse.jt.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.SequenceDataType;


/**
 * 使用StructField的JDBC结果集的读取器
 * 
 * @author Jeff Tang
 * 
 */
final class ResultSetStdObjReader extends ResultSetReader {

	ResultSetStdObjReader(ResultSet resultSet) {
		super(resultSet);
	}

	@Override
	public final Object inBoolean(Object userData) throws SQLException {
		boolean value = this.resultSet.getBoolean(this.columnIndex);
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsBooleanNoCheck(this.obj, value);
		}
		if (userData != read_no_return && !this.resultSet.wasNull()) {
			return value;
		}
		return null;
	}

	@Override
	public final Object inByte(Object userData) throws SQLException {
		byte value = this.resultSet.getByte(this.columnIndex);
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsByteNoCheck(this.obj, value);
		}
		if (userData != read_no_return && !this.resultSet.wasNull()) {
			return value;
		}
		return null;
	}

	@Override
	public final Object inBytes(Object userData, SequenceDataType type)
			throws SQLException {
		byte[] value = this.resultSet.getBytes(this.columnIndex);
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsBytesNoCheck(this.obj, value);
		}
		return value;
	}

	@Override
	public final Object inDate(Object userData) throws SQLException {
		Timestamp value = this.resultSet.getTimestamp(this.columnIndex);
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsDateNoCheck(this.obj,
					value != null ? value.getTime() : 0L);
		}
		return value;
	}

	@Override
	public final Object inDouble(Object userData) throws SQLException {
		double value = this.resultSet.getDouble(this.columnIndex);
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsDoubleNoCheck(this.obj, value);
		}
		if (userData != read_no_return && !this.resultSet.wasNull()) {
			return value;
		}
		return null;
	}

	@Override
	public final Object inFloat(Object userData) throws SQLException {
		float value = this.resultSet.getFloat(this.columnIndex);
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsFloatNoCheck(this.obj, value);
		}
		if (userData != read_no_return && !this.resultSet.wasNull()) {
			return value;
		}
		return null;
	}

	@Override
	public final Object inGUID(Object userData) throws SQLException {
		GUID value = GUID.valueOf(this.resultSet.getBytes(this.columnIndex));
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsGUIDNoCheck(this.obj, value);
		}
		return value;
	}

	@Override
	public final Object inInt(Object userData) throws SQLException {
		int value = this.resultSet.getInt(this.columnIndex);
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsIntNoCheck(this.obj, value);
		}
		if (userData != read_no_return && !this.resultSet.wasNull()) {
			return value;
		}
		return null;
	}

	@Override
	public final Object inLong(Object userData) throws SQLException {
		long value = this.resultSet.getLong(this.columnIndex);
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsLongNoCheck(this.obj, value);
		}
		if (userData != read_no_return && !this.resultSet.wasNull()) {
			return value;
		}
		return null;
	}

	@Override
	public final Object inShort(Object userData) throws SQLException {
		short value = this.resultSet.getShort(this.columnIndex);
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsShortNoCheck(this.obj, value);
		}
		if (userData != read_no_return && !this.resultSet.wasNull()) {
			return value;
		}
		return null;
	}

	@Override
	public final Object inString(Object userData, SequenceDataType type)
			throws SQLException {
		String value = this.resultSet.getString(this.columnIndex);
		if (userData != read_only_return) {
			this.targetField.setFieldValueAsStringNoCheck(this.obj, value);
		}
		return value;
	}

	@Override
	public final Object inUnknown(Object userData) throws Throwable {
		if (userData != read_only_return) {
			this.targetField.setFieldValueNullNoCheck(this.obj);
		}
		return null;
	}

	/**
	 * 结果值存放的目标对象
	 */
	private Object obj;

	@Override
	final void setObj(Object obj) {
		this.obj = obj;
	}
}
