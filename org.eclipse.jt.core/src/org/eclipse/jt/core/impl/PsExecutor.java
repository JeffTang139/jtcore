package org.eclipse.jt.core.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.EnumType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.SequenceDataType;
import org.eclipse.jt.core.type.TypeDetector;
import org.eclipse.jt.core.type.TypeFactory;


/**
 * PreparedStatement的包装类
 * 
 * @author Jeff Tang
 * 
 * @param <TSql>
 */
class PsExecutor<TSql extends Sql> implements TypeDetector<Object, Object> {

	public final void unuse() {
		if (this.ps != null) {
			final PreparedStatement ps = this.ps;
			this.ps = null;
			this.adapter.freeStatement(ps);
			this.activeChanged(false);
		}
	}

	final TSql sql;

	final DBAdapterImpl adapter;

	PreparedStatement ps;

	private int parameterIndex;

	private StructFieldDefineImpl argument;

	PsExecutor(DBAdapterImpl adapter, TSql sql) {
		if (adapter == null) {
			throw new NullPointerException("数据库适配器为空");
		}
		if (sql == null) {
			throw new NullPointerException("sql信息为空");
		}
		sql.checkAvailable();
		this.adapter = adapter;
		this.sql = sql;
	}

	/**
	 * 当前包装类状态改变通知
	 * 
	 * @param active
	 *            true表示申请数据库资源,false表示释放数据库资源
	 */
	protected void activeChanged(boolean active) {
	}

	final void use(boolean forUpdate) throws SQLException {
		if (this.ps == null) {
			this.ps = this.adapter.prepareStatement(this.sql.sqlstr());
			this.activeChanged(true);
		}
		this.adapter.updateTrans(forUpdate);
	}

	private final void flushArgumentValues(DynObj dynObj) throws SQLException {
		for (int i = 0, c = this.sql.parameters.size(); i < c; i++) {
			ParameterReserver pr = this.sql.parameters.get(i);
			if (pr instanceof ArgumentReserver) {
				ArgumentReserver ar = (ArgumentReserver) pr;
				StructFieldDefineImpl arg = ar.arg;
				if (arg != null) {
					if (arg.isFieldValueNull(dynObj)) {
						this.ps.setNull(i + 1, TypeFactory.sqlTypeOf(ar.type));
					} else {
						this.argument = arg;
						this.parameterIndex = i + 1;
						ar.type.detect(this, dynObj);
					}
				}
			}
		}
	}

	private final void flushEntityValues(Object argValueObj)
			throws SQLException {
		for (int i = 0, c = this.sql.parameters.size(); i < c; i++) {
			ParameterReserver pr = this.sql.parameters.get(i);
			if (pr instanceof ArgumentReserver) {
				ArgumentReserver ar = (ArgumentReserver) pr;
				if (argValueObj == null) {
					throw new NullArgumentException("实体对象");
				}
				if (ar.type == DateType.TYPE
						&& ar.arg.getFieldValueAsLong(argValueObj) == 0) {
					this.ps.setNull(i + 1, SQLTypesWrapper.TIMESTAMP);
				} else if (ar.arg.isFieldValueNull(argValueObj)) {
					this.ps.setNull(i + 1, TypeFactory.sqlTypeOf(ar.type));
				} else {
					this.argument = ar.arg;
					this.parameterIndex = i + 1;
					ar.type.detect(this, argValueObj);
				}
			}
		}
	}

	final void flushParameters(Object argValueObj) throws SQLException {
		if (this.ps == null) {
			throw new NullPointerException();
		}
		if (argValueObj instanceof DynObj) {
			this.flushArgumentValues((DynObj) argValueObj);
		} else {
			this.flushEntityValues(argValueObj);
		}
	}

	final int executeIntScalar(Object argValueObj) {
		try {
			ResultSet rs = this.executeQuery(argValueObj);
			try {
				if (rs.next()) {
					return rs.getInt(1);
				}
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		return 0;
	}

	final long executeLongScalar(Object argValueObj) {
		try {
			ResultSet rs = this.executeQuery(argValueObj);
			try {
				if (rs.next()) {
					return rs.getLong(1);
				}
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		return 0L;
	}

	final ResultSet executeQuery(Object argValueObj) {
		try {
			this.use(false);
			this.flushParameters(argValueObj);
			return this.adapter.jdbcQuery(this);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	final int executeUpdate(Object argValueObj) {
		try {
			this.use(true);
			this.flushParameters(argValueObj);
			return this.adapter.jdbcUpdate(this);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	@Deprecated
	final void flushParameters(DynObj parameterValueObj1,
			DynObj parameterValueObj2) throws SQLException {
		if (this.ps == null) {
			throw new NullPointerException();
		}
		if (parameterValueObj1 == null) {
			throw new NullArgumentException("parameterValueObj1");
		}
		if (parameterValueObj2 == null) {
			throw new NullArgumentException("parameterValueObj2");
		}
		for (int i = 0, c = this.sql.parameters.size(); i < c; i++) {
			ParameterReserver pr = this.sql.parameters.get(i);
			if (pr instanceof ArgumentReserver) {
				ArgumentReserver ar = (ArgumentReserver) pr;
				StructFieldDefineImpl argRef = ar.arg;
				if (argRef != null) {
					DynObj argObj;
					StructDefineImpl argOwner = argRef.owner;
					if (argOwner == parameterValueObj1.define) {
						argObj = parameterValueObj1;
					} else if (argOwner == parameterValueObj2.define) {
						argObj = parameterValueObj2;
					} else {
						throw new IllegalArgumentException("无效的参数对象");
					}
					if (argRef.isFieldValueNull(argObj)) {
						this.ps.setNull(i, TypeFactory.sqlTypeOf(ar.type));
					} else {
						this.argument = argRef;
						this.parameterIndex = i + 1;
						ar.type.detect(this, argObj);
					}
				}
			}
		}
	}

	@Deprecated
	final int executeUpdate(DynObj argObj1, DynObj argObj2) {
		try {
			this.use(true);
			this.flushParameters(argObj1, argObj2);
			return this.adapter.jdbcUpdate(this.ps);
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final Object inBinary(Object obj, SequenceDataType type)
			throws SQLException {
		return this.inBytes(obj, type);
	}

	public final Object inBlob(Object obj) throws SQLException {
		byte[] b = PsExecutor.this.argument.getFieldValueAsBytes(obj);
		PsExecutor.this.ps.setBytes(PsExecutor.this.parameterIndex, b);
		return null;
	}

	public final Object inBoolean(Object obj) throws SQLException {
		boolean b = PsExecutor.this.argument.getFieldValueAsBoolean(obj);
		PsExecutor.this.ps.setBoolean(PsExecutor.this.parameterIndex, b);
		return null;
	}

	public final Object inByte(Object obj) throws SQLException {
		byte b = PsExecutor.this.argument.getFieldValueAsByte(obj);
		PsExecutor.this.ps.setByte(PsExecutor.this.parameterIndex, b);
		return null;
	}

	public final Object inBytes(Object obj, SequenceDataType type)
			throws SQLException {
		byte[] b = PsExecutor.this.argument.getFieldValueAsBytes(obj);
		PsExecutor.this.ps.setBytes(PsExecutor.this.parameterIndex, b);
		return null;
	}

	public final Object inChar(Object obj, SequenceDataType type)
			throws SQLException {
		return this.inString(obj, type);
	}

	public final Object inDate(Object obj) throws SQLException {
		Timestamp ts = new Timestamp(
				PsExecutor.this.argument.getFieldValueAsDate(obj));
		PsExecutor.this.ps.setTimestamp(PsExecutor.this.parameterIndex, ts);
		return null;
	}

	public final Object inDouble(Object obj) throws SQLException {
		double d = PsExecutor.this.argument.getFieldValueAsDouble(obj);
		PsExecutor.this.ps.setDouble(PsExecutor.this.parameterIndex, d);
		return null;
	}

	public final Object inFloat(Object obj) throws SQLException {
		float f = PsExecutor.this.argument.getFieldValueAsFloat(obj);
		PsExecutor.this.ps.setFloat(PsExecutor.this.parameterIndex, f);
		return null;
	}

	public final Object inGUID(Object obj) throws SQLException {
		GUID guid = PsExecutor.this.argument.getFieldValueAsGUID(obj);
		PsExecutor.this.ps.setBytes(PsExecutor.this.parameterIndex,
				guid.toBytes());
		return null;
	}

	public final Object inInt(Object obj) throws SQLException {
		int i = PsExecutor.this.argument.getFieldValueAsInt(obj);
		PsExecutor.this.ps.setInt(PsExecutor.this.parameterIndex, i);
		return null;
	}

	public final Object inLong(Object obj) throws SQLException {
		long l = PsExecutor.this.argument.getFieldValueAsLong(obj);
		PsExecutor.this.ps.setLong(PsExecutor.this.parameterIndex, l);
		return null;
	}

	public final Object inModel(Object obj, ModelDefine model) {
		throw new UnsupportedOperationException();
	}

	public final Object inNChar(Object obj, SequenceDataType type)
			throws SQLException {
		return this.inString(obj, type);
	}

	public final Object inNVarChar(Object obj, SequenceDataType type)
			throws SQLException {
		return this.inString(obj, type);
	}

	public final Object inNumeric(Object obj, int precision, int scale)
			throws SQLException {
		return this.inDouble(obj);
	}

	public final Object inRecordSet(Object obj) {
		throw new UnsupportedOperationException();
	}

	public final Object inResource(Object obj, Class<?> facadeClass,
			Object category) {
		throw new UnsupportedOperationException();
	}

	public final Object inShort(Object obj) throws SQLException {
		short s = PsExecutor.this.argument.getFieldValueAsShort(obj);
		PsExecutor.this.ps.setShort(PsExecutor.this.parameterIndex, s);
		return null;
	}

	public final Object inStruct(Object obj, StructDefine structDefine) {
		throw new UnsupportedOperationException();
	}

	public final Object inTable(Object obj) {
		throw new UnsupportedOperationException();
	}

	public final Object inUnknown(Object obj) {
		throw new UnsupportedOperationException();
	}

	public final Object inVarBinary(Object obj, SequenceDataType type)
			throws SQLException {
		return this.inBytes(obj, type);
	}

	public final Object inString(Object obj, SequenceDataType type)
			throws SQLException {
		String s = PsExecutor.this.argument.getFieldValueAsString(obj);
		PsExecutor.this.ps.setString(PsExecutor.this.parameterIndex, s);
		return null;
	}

	public final Object inVarChar(Object obj, SequenceDataType type)
			throws SQLException {
		return this.inString(obj, type);
	}

	public final Object inText(Object obj) throws SQLException {
		return this.inString(obj, null);
	}

	public final Object inNText(Object obj) throws SQLException {
		return this.inString(obj, null);
	}

	public final Object inEnum(Object userData, EnumType<?> type) {
		throw new UnsupportedOperationException();
	}

	public final Object inQuery(Object userData, QueryStatementDefine type) {
		throw new UnsupportedOperationException();
	}

	public final Object inObject(Object userData, ObjectDataType type)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	public final Object inNull(Object userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	public final Object inCharacter(Object userData) throws Throwable {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unused")
	private final class DebuggedSetter implements TypeDetector<Object, Object> {

		private final void set(String type, Object value) {
			System.out.println("保留变量序号[" + PsExecutor.this.parameterIndex
					+ "],类型[" + type + "],值:" + value.toString());
		}

		public final Object inBinary(Object obj, SequenceDataType type)
				throws SQLException {
			return this.inBytes(obj, type);
		}

		public final Object inBlob(Object obj) throws SQLException {
			byte[] bs = PsExecutor.this.argument.getFieldValueAsBytes(obj);
			this.set("blob", Convert.bytesToHex(bs, false, false));
			PsExecutor.this.ps.setBytes(PsExecutor.this.parameterIndex, bs);
			return null;
		}

		public final Object inBoolean(Object obj) throws SQLException {
			boolean b = PsExecutor.this.argument.getFieldValueAsBoolean(obj);
			this.set("boolean", b);
			PsExecutor.this.ps.setBoolean(PsExecutor.this.parameterIndex, b);
			return null;
		}

		public final Object inByte(Object obj) throws SQLException {
			byte b = PsExecutor.this.argument.getFieldValueAsByte(obj);
			this.set("byte", b);
			PsExecutor.this.ps.setByte(PsExecutor.this.parameterIndex, b);
			return null;
		}

		public final Object inBytes(Object obj, SequenceDataType type)
				throws SQLException {
			byte[] bs = PsExecutor.this.argument.getFieldValueAsBytes(obj);
			this.set("bytes", bs);
			PsExecutor.this.ps.setBytes(PsExecutor.this.parameterIndex, bs);
			return null;
		}

		public final Object inChar(Object obj, SequenceDataType type)
				throws SQLException {
			return this.inString(obj, type);
		}

		public final Object inDate(Object obj) throws SQLException {
			Timestamp ts = new Timestamp(
					PsExecutor.this.argument.getFieldValueAsDate(obj));
			this.set("timestamp", ts);
			PsExecutor.this.ps.setTimestamp(PsExecutor.this.parameterIndex, ts);
			return null;
		}

		public final Object inDouble(Object obj) throws SQLException {
			double d = PsExecutor.this.argument.getFieldValueAsDouble(obj);
			PsExecutor.this.ps.setDouble(PsExecutor.this.parameterIndex, d);
			return null;
		}

		public final Object inFloat(Object obj) throws SQLException {
			float f = PsExecutor.this.argument.getFieldValueAsFloat(obj);
			this.set("float", f);
			PsExecutor.this.ps.setFloat(PsExecutor.this.parameterIndex, f);
			return null;
		}

		public final Object inGUID(Object obj) throws SQLException {
			GUID guid = PsExecutor.this.argument.getFieldValueAsGUID(obj);
			this.set("guid", guid);
			PsExecutor.this.ps.setBytes(PsExecutor.this.parameterIndex,
					guid.toBytes());
			return null;
		}

		public final Object inInt(Object obj) throws SQLException {
			int i = PsExecutor.this.argument.getFieldValueAsInt(obj);
			this.set("int", i);
			PsExecutor.this.ps.setInt(PsExecutor.this.parameterIndex, i);
			return null;
		}

		public final Object inLong(Object obj) throws SQLException {
			long l = PsExecutor.this.argument.getFieldValueAsLong(obj);
			this.set("long", l);
			PsExecutor.this.ps.setLong(PsExecutor.this.parameterIndex, l);
			return null;
		}

		public final Object inModel(Object obj, ModelDefine model) {
			throw new UnsupportedOperationException();
		}

		public final Object inNChar(Object obj, SequenceDataType type)
				throws SQLException {
			return this.inString(obj, type);
		}

		public final Object inNVarChar(Object obj, SequenceDataType type)
				throws SQLException {
			return this.inString(obj, type);
		}

		public final Object inNumeric(Object obj, int precision, int scale)
				throws SQLException {
			return this.inDouble(obj);
		}

		public final Object inRecordSet(Object obj) {
			throw new UnsupportedOperationException();
		}

		public final Object inResource(Object obj, Class<?> facadeClass,
				Object category) {
			throw new UnsupportedOperationException();
		}

		public final Object inShort(Object obj) throws SQLException {
			short s = PsExecutor.this.argument.getFieldValueAsShort(obj);
			this.set("short", s);
			PsExecutor.this.ps.setShort(PsExecutor.this.parameterIndex, s);
			return null;
		}

		public final Object inStruct(Object obj, StructDefine structDefine) {
			throw new UnsupportedOperationException();
		}

		public final Object inTable(Object obj) {
			throw new UnsupportedOperationException();
		}

		public final Object inUnknown(Object obj) {
			throw new UnsupportedOperationException();
		}

		public final Object inVarBinary(Object obj, SequenceDataType type)
				throws SQLException {
			return this.inBytes(obj, type);
		}

		public final Object inString(Object obj, SequenceDataType type)
				throws SQLException {
			String s = PsExecutor.this.argument.getFieldValueAsString(obj);
			this.set("string", s);
			PsExecutor.this.ps.setString(PsExecutor.this.parameterIndex, s);
			return null;
		}

		public final Object inVarChar(Object obj, SequenceDataType type)
				throws SQLException {
			return this.inString(obj, type);
		}

		public final Object inText(Object obj) throws SQLException {
			return this.inString(obj, null);
		}

		public final Object inNText(Object obj) throws SQLException {
			return this.inString(obj, null);
		}

		public final Object inEnum(Object userData, EnumType<?> type) {
			throw new UnsupportedOperationException();
		}

		public final Object inQuery(Object userData, QueryStatementDefine type) {
			throw new UnsupportedOperationException();
		}

		public final Object inObject(Object userData, ObjectDataType type)
				throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inNull(Object userData) throws Throwable {
			throw new UnsupportedOperationException();
		}

		public Object inCharacter(Object obj) throws Throwable {
			throw new UnsupportedOperationException();
		}
	}

}
