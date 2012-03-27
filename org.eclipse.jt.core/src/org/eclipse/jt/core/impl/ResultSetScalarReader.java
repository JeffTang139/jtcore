package org.eclipse.jt.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetectorBase;


/**
 * 使用StructField的JDBC结果集的读取器
 * 
 * @author Jeff Tang
 * 
 */
final class ResultSetScalarReader extends TypeDetectorBase<Object, ResultSet> {

	public final static ResultSetScalarReader reader = new ResultSetScalarReader();

	private ResultSetScalarReader() {
	}

	@Override
	public final Object inBoolean(ResultSet resultSet) throws SQLException {
		boolean result = resultSet.getBoolean(1);
		if (resultSet.wasNull()) {
			return null;
		}
		return result;
	}

	@Override
	public final Object inByte(ResultSet resultSet) throws SQLException {
		byte result = resultSet.getByte(1);
		if (resultSet.wasNull()) {
			return null;
		}
		return result;
	}

	@Override
	public final Object inBytes(ResultSet resultSet, SequenceDataType type)
			throws SQLException {
		return resultSet.getBytes(1);
	}

	@Override
	public final Object inDate(ResultSet resultSet) throws SQLException {
		Timestamp ts = resultSet.getTimestamp(1);
		if (ts == null) {
			return null;
		} else {
			return ts.getTime();
		}
	}

	@Override
	public final Object inDouble(ResultSet resultSet) throws SQLException {
		double result = resultSet.getDouble(1);
		if (resultSet.wasNull()) {
			return null;
		}
		return result;
	}

	@Override
	public final Object inFloat(ResultSet resultSet) throws SQLException {
		float result = resultSet.getFloat(1);
		if (resultSet.wasNull()) {
			return null;
		}
		return result;
	}

	@Override
	public final Object inGUID(ResultSet resultSet) throws SQLException {
		return GUID.valueOf(resultSet.getBytes(1));
	}

	@Override
	public final Object inInt(ResultSet resultSet) throws SQLException {
		int result = resultSet.getInt(1);
		if (resultSet.wasNull()) {
			return null;
		}
		return result;
	}

	@Override
	public final Object inLong(ResultSet resultSet) throws SQLException {
		long result = resultSet.getLong(1);
		if (resultSet.wasNull()) {
			return null;
		}
		return result;
	}

	@Override
	public final Object inShort(ResultSet resultSet) throws SQLException {
		short result = resultSet.getShort(1);
		if (resultSet.wasNull()) {
			return null;
		}
		return result;
	}

	@Override
	public final Object inString(ResultSet resultSet, SequenceDataType type)
			throws SQLException {
		return resultSet.getString(1);
	}
}
