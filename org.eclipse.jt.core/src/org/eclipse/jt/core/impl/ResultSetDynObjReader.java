package org.eclipse.jt.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.SequenceDataType;


/**
 * 使用StructField的JDBC结果集的读取器
 * 
 * @author Jeff Tang
 * 
 */
class ResultSetDynObjReader extends ResultSetReader {

	ResultSetDynObjReader(ResultSet resultSet) {
		super(resultSet);
	}

	@Override
	public final Object inBoolean(Object userData) throws SQLException {
		boolean value = this.resultSet.getBoolean(this.columnIndex);
		if (this.resultSet.wasNull()) {
			if (userData != read_only_return) {
				this.targetField.setFieldValueNullNoCheck(this.obj);
			}
		} else {
			if (userData != read_only_return) {
				this.targetField.setFieldValueAsBooleanNoCheck(this.obj, value);
			}
			if (userData != read_no_return) {
				return value;
			}
		}
		return null;
	}

	@Override
	public final Object inByte(Object userData) throws SQLException {
		byte value = this.resultSet.getByte(this.columnIndex);
		if (this.resultSet.wasNull()) {
			if (userData != read_only_return) {
				this.targetField.setFieldValueNullNoCheck(this.obj);
			}
		} else {
			if (userData != read_only_return) {
				this.targetField.setFieldValueAsByteNoCheck(this.obj, value);
			}
			if (userData != read_no_return) {
				return value;
			}
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
			if (value == null) {
				this.targetField.setFieldValueNullNoCheck(this.obj);
			} else {
				this.targetField.setFieldValueAsDateNoCheck(this.obj,
						value.getTime());
			}
		}
		return value;
	}

	@Override
	public final Object inDouble(Object userData) throws SQLException {
		double value = this.resultSet.getDouble(this.columnIndex);
		if (this.resultSet.wasNull()) {
			if (userData != read_only_return) {
				this.targetField.setFieldValueNullNoCheck(this.obj);
			}
		} else {
			if (userData != read_only_return) {
				this.targetField.setFieldValueAsDoubleNoCheck(this.obj, value);
			}
			if (userData != read_no_return) {
				return value;
			}
		}
		return null;
	}

	@Override
	public final Object inFloat(Object userData) throws SQLException {
		float value = this.resultSet.getFloat(this.columnIndex);
		if (this.resultSet.wasNull()) {
			if (userData != read_only_return) {
				this.targetField.setFieldValueNullNoCheck(this.obj);
			}
		} else {
			if (userData != read_only_return) {
				this.targetField.setFieldValueAsFloatNoCheck(this.obj, value);
			}
			if (userData != read_no_return) {
				return value;
			}
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
		if (this.resultSet.wasNull()) {
			if (userData != read_only_return) {
				this.targetField.setFieldValueNullNoCheck(this.obj);
			}
		} else {
			if (userData != read_only_return) {
				this.targetField.setFieldValueAsIntNoCheck(this.obj, value);
			}
			if (userData != read_no_return) {
				return value;
			}
		}
		return null;
	}

	@Override
	public final Object inLong(Object userData) throws SQLException {
		long value = this.resultSet.getLong(this.columnIndex);
		if (this.resultSet.wasNull()) {
			if (userData != read_only_return) {
				this.targetField.setFieldValueNullNoCheck(this.obj);
			}
		} else {
			if (userData != read_only_return) {
				this.targetField.setFieldValueAsLongNoCheck(this.obj, value);
			}
			if (userData != read_no_return) {
				return value;
			}
		}
		return null;
	}

	@Override
	public final Object inShort(Object userData) throws SQLException {
		short value = this.resultSet.getShort(this.columnIndex);
		if (this.resultSet.wasNull()) {
			if (userData != read_only_return) {
				this.targetField.setFieldValueNullNoCheck(this.obj);
			}
		} else {
			if (userData != read_only_return) {
				this.targetField.setFieldValueAsShortNoCheck(this.obj, value);
			}
			if (userData != read_no_return) {
				return value;
			}
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
		if (userData != read_no_return) {
			this.targetField.setFieldValueNullNoCheck(this.obj);
		}
		return null;
	}

	/**
	 * 结果值存放的目标对象
	 */
	DynObj obj;

	@Override
	final void setObj(Object obj) {
		this.obj = (DynObj) obj;
	}

	final void readResult(DynObj obj, StructDefineImpl struct) {
		this.columnIndex = 1;
		this.obj = obj;
		ArrayList<StructFieldDefineImpl> fields = struct.fields;
		for (int i = 0, c = fields.size(); i < c; i++) {
			StructFieldDefineImpl fd = fields.get(i);
			this.targetField = fd;
			fd.type.detect(this, null);
			this.columnIndex++;
		}
	}

	final void readRecord(DynObj record, MappingQueryStatementImpl query) {
		this.columnIndex = 1;
		this.obj = record;
		for (int i = 0, c = query.columns.size(); i < c; i++) {
			StructFieldDefineImpl sf = query.columns.get(i).field;
			this.targetField = sf;
			sf.type.detect(this, null);
			this.columnIndex++;
		}
	}

	static final void readRecords(QueryStatementImpl query,
			ResultSet resultSet, List<DynObj> records) throws SQLException {
		ResultSetDynObjReader reader = new ResultSetDynObjReader(resultSet);
		ArrayList<StructFieldDefineImpl> fields = query.mapping.fields;
		int cSize = fields.size();
		while (resultSet.next()) {
			records.add(reader.obj = query.newRecordObj(DynObj.r_db));
			reader.columnIndex = 1;
			for (int i = 0; i < cSize; i++) {
				StructFieldDefineImpl field = fields.get(i);
				reader.targetField = field;
				field.type.detect(reader, reader.obj);
				reader.columnIndex++;
			}
		}
	}
}
