package org.eclipse.jt.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.jt.core.da.DBAdapter;
import org.eclipse.jt.core.da.DBCommand;
import org.eclipse.jt.core.da.IteratedRecord;
import org.eclipse.jt.core.da.RecordIterateAction;
import org.eclipse.jt.core.da.RecordSet;
import org.eclipse.jt.core.da.RecordState;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.Digester;
import org.eclipse.jt.core.type.Typable;
import org.eclipse.jt.core.type.TypeDetector;


/**
 * 记录集实现类
 * 
 * @author Jeff Tang
 * 
 */
final class RecordSetImpl implements RecordSet, IteratedRecord {

	public final RecordSetImpl getRootType() {
		return this;
	}

	public final int getTupleElementCount() {
		return this.statement.columns.size();
	}

	public final Typable getTupleElementType(int index) {
		return this.statement.columns.get(index).value();
	}

	public final <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> detector, TUserData userData)
			throws UnsupportedOperationException {
		try {
			return detector.inRecordSet(userData);
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final RecordSetFieldContainerImpl getFields() {
		return this.fields;
	}

	public final boolean isEmpty() {
		return this.records.size() == 0;
	}

	public final int getRecordCount() {
		return this.records.size();
	}

	public final boolean first() {
		return this.setRecordIndex(0);
	}

	public final boolean previous() {
		return this.setRecordIndex(this.currentRecordIndex - 1);
	}

	public final boolean next() {
		return this.setRecordIndex(this.currentRecordIndex + 1);
	}

	public final boolean isLast() {
		return this.currentRecordIndex == this.records.size();
	}

	public final boolean absolute(int index) {
		return this.setRecordIndex(index);
	}

	public final boolean relative(int rows) {
		return this.setRecordIndex(this.currentRecordIndex + rows);
	}

	public final int getPosition() {
		return this.currentRecordIndex;
	}

	public final RecordState getRecordState() {
		this.validateCurrentRecord();
		switch (this.currentRecord.getRecordState()) {
		case DynObj.r_new:
			return RecordState.NEW;
		case DynObj.r_new_modified:
			return RecordState.NEW_MODIFIED;
		case DynObj.r_db:
			return RecordState.IN_DB;
		case DynObj.r_db_deleting:
			return RecordState.IN_DB_DELETING;
		case DynObj.r_db_modifing:
			return RecordState.IN_DB_MODIFING;
		default:
			throw new UnsupportedOperationException("无效的记录状态");
		}
	}

	public final void append() {
		DynObj record = this.statement.newRecordObj(DynObj.r_new);
		this.records.add(record);
		this.addModifingRecord(record);
		this.setRecordIndex(this.records.size() - 1);
	}

	public final boolean delete() {
		this.validateCurrentRecord();
		DynObj record = this.records.remove(this.currentRecordIndex);
		switch (record.getRecordState()) {
		case DynObj.r_new:
		case DynObj.r_new_modified:
			this.modifiedRecords.remove(record);
			break;
		case DynObj.r_db_modifing:
			record.setRecordState(DynObj.r_db_deleting);
			break;
		case DynObj.r_db:
			record.setRecordState(DynObj.r_db_deleting);
			this.addModifingRecord(record);
			break;
		default:
			throw new IllegalStateException();
		}
		return this.setRecordIndex(this.currentRecordIndex);
	}

	@SuppressWarnings("fallthrough")
	public final int update(DBAdapter context) throws SQLException {
		if (context == null) {
			throw new NullPointerException();
		}
		if (this.modifiedRecords == null || this.modifiedRecords.isEmpty()) {
			return 0;
		}
		QueryStatementImpl query = this.statement;
		DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
		adapter.checkAccessible();
		CommonExecutor deleter = null;
		CommonExecutor saver = null;
		// RecidsResetter resetter = null;
		int result = 0;
		try {
			for (int i = 0, c = this.modifiedRecords.size(); i < c; i++) {
				DynObj record = this.modifiedRecords.get(i);
				switch (record.getRecordState()) {
				case DynObj.r_new_modified:
				case DynObj.r_new:
					// CORE2.5 废弃
					// try {
					// if (saver == null) {
					// saver = new CommonExecutor(adapter,
					// query.getRowSaveSql(adapter));
					// }
					// if (saver.executeUpdate(record) > 0) {
					// record.setRecordState(DynObj.r_db);
					// result++;
					// }
					// break;
					// } catch (Throwable e) {
					// if (resetter == null) {
					// resetter = new RecidsResetter(adapter, query);
					// }
					// if (!resetter.tryResetRecids(record)) {
					// break;
					// }
					// }
				case DynObj.r_db_modifing:
					if (saver == null) {
						saver = new CommonExecutor(adapter,
								query.getRowSaveSql(adapter));
					}
					// HCL 不能保证返回正确的更新计数
					if (saver.executeUpdate(record) > 0) {
						record.setRecordState(DynObj.r_db);
						result++;
					}
					break;
				case DynObj.r_db_deleting:
					if (deleter == null) {
						deleter = new CommonExecutor(adapter,
								this.statement.getRowDeleteSql(adapter));
					}
					if (deleter.executeUpdate(record) > 0) {
						record.define = null;
						result++;
					}
					break;
				}
			}
			this.modifiedRecords.clear();
		} finally {
			if (saver != null) {
				saver.unuse();
			}
			if (deleter != null) {
				deleter.unuse();
			}
			// if (resetter != null) {
			// resetter.unuse();
			// }
		}
		return result;
	}

	// private static final class RecidsResetter {
	//
	// final PsExecutor<QueryRecidByLpkSql>[] queriers;
	//
	// @SuppressWarnings("unchecked")
	// RecidsResetter(DBAdapterImpl adapter, QueryStatementImpl query) {
	// QueryRecidByLpkSql[] sqls = query.getQueryRecidSqls(adapter);
	// ArrayList<CommonExecutor> queriers = new ArrayList<CommonExecutor>(
	// sqls.length);
	// int refcount = 0;
	// for (QueryRecidByLpkSql sql : sqls) {
	// if (sql.isAvailable()) {
	// queriers.add(new CommonExecutor(adapter, sql));
	// refcount++;
	// }
	//
	// }
	// this.queriers = queriers.toArray(new PsExecutor[queriers.size()]);
	// }
	//
	// final boolean tryResetRecids(DynObj record) throws SQLException {
	// boolean reset = false;
	// for (PsExecutor<QueryRecidByLpkSql> querier : this.queriers) {
	// ResultSet rs = querier.executeQuery(record);
	// try {
	// if (rs.next()) {
	// querier.sql.f_recid.setFieldValueAsGUID(record,
	// GUID.valueOf(rs.getBytes(1)));
	// reset = true;
	// }
	// } finally {
	// rs.close();
	// }
	// }
	// return reset;
	// }
	//
	// final void unuse() {
	// for (PsExecutor<QueryRecidByLpkSql> querier : this.queriers) {
	// querier.unuse();
	// }
	// }
	//
	// }

	public final void reQuery(DBAdapter context, Object... argumetns)
			throws SQLException {
		// TODO 优化，不要再次创建命令和拷贝参数了
		DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(context);
		adapter.checkAccessible();
		DBCommandProxy proxy = adapter.prepareStatement(this.statement);
		try {
			proxy.setArgumentValues(argumetns);
			DBCommandImpl command = proxy.command;
			this.loadRecordSet(command.executor
					.executeQuery(command.argValueObj));
		} finally {
			proxy.unuse();
		}
	}

	public final void reQuery(DBCommand dbCommand) throws SQLException {
		DBCommandImpl c = ((DBCommandProxy) dbCommand).command;
		c.adapter.checkAccessible();
		if (c.statement != this.statement) {
			throw new IllegalArgumentException("命令与数据集不兼容");
		}
		this.loadRecordSet(c.executor.executeQuery(c.argValueObj));
	}

	final QueryStatementImpl statement;

	final RecordSetFieldContainerImpl fields;

	RecordSetImpl(QueryStatementImpl query) {
		this.statement = query;
		int columnSize = query.columns.size();
		this.fields = new RecordSetFieldContainerImpl(columnSize);
		for (int i = 0; i < columnSize; i++) {
			this.fields.add(new RecordSetFieldImpl(this, query.columns.get(i)));
		}
	}

	/**
	 * 返回当前指针指向的记录对象,只做读取属性值操作
	 */
	final DynObj getRecordRead() {
		this.validateCurrentRecord();
		return this.currentRecord;
	}

	/**
	 * 返回当前指针指向的记录对象,并将修改记录对象的列属性
	 */
	final DynObj getRecordWrite() {
		this.validateCurrentRecord();
		// 只修改状态为default的记录对象,appended和modified保持不变
		switch (this.currentRecord.getRecordState()) {
		case DynObj.r_db:
			this.currentRecord.setRecordState(DynObj.r_db_modifing);
			this.addModifingRecord(this.currentRecord);
			break;
		case DynObj.r_new:
			this.currentRecord.setRecordState(DynObj.r_new_modified);
			break;
		}
		return this.currentRecord;
	}

	/**
	 * 填充记录集
	 * 
	 * @param resultSet
	 * @throws SQLException
	 */
	final void loadRecordSet(ResultSet resultSet) throws SQLException {
		try {
			this.records.clear();
			if (this.modifiedRecords != null) {
				this.modifiedRecords.clear();
			}
			ResultSetDynObjReader.readRecords(this.statement, resultSet,
					this.records);
			this.setRecordIndex(-1);
		} finally {
			resultSet.close();
		}
	}

	final void iterateResultSet(ContextImpl<?, ?, ?> context,
			ResultSet resultSet, RecordIterateAction action) throws Throwable {
		try {
			ResultSetDynObjReader reader = new ResultSetDynObjReader(resultSet);
			DynObj record = this.statement.newRecordObj(DynObj.r_db);
			reader.obj = record;
			this.currentRecord = record;
			ArrayList<StructFieldDefineImpl> fields = this.statement.mapping.fields;
			int cSize = fields.size();
			long recordIndex = 0L;
			while (resultSet.next()) {
				reader.columnIndex = 1;
				for (int i = 0; i < cSize; i++) {
					StructFieldDefineImpl field = fields.get(i);
					reader.targetField = field;
					field.type.detect(reader, record);
					reader.columnIndex++;
				}
				if (action.iterate(context, this, recordIndex++)) {
					break;
				}
			}
		} finally {
			resultSet.close();
		}
	}

	private final void validateCurrentRecord() {
		if (this.currentRecord == null) {
			throw new NullPointerException("当前记录为空");
		}
	}

	private final void addModifingRecord(DynObj record) {
		if (this.modifiedRecords == null) {
			this.modifiedRecords = new ArrayList<DynObj>();
		}
		this.modifiedRecords.add(record);
	}

	/**
	 * 设置指针位置,返回是否有效记录
	 * 
	 * @param index
	 * @return
	 */
	private final boolean setRecordIndex(int index) {
		int recCount = this.records.size();
		if (index > recCount) {
			throw new IllegalStateException("游标已经抵达记录集的尾部");
		}
		if (index < 0) {
			this.currentRecord = null;
			this.currentRecordIndex = -1;
			return false;
		} else if (index < recCount) {
			this.currentRecord = this.records.get(index);
			this.currentRecordIndex = index;
			return true;
		} else {
			this.currentRecord = null;
			this.currentRecordIndex = recCount;
			return false;
		}
	}

	public final Object getCurrentRO() {
		return this.currentRecord;
	}

	public final int positionOfRO(Object ro) {
		if (ro == null) {
			throw new NullArgumentException("ro");
		}
		DynObj r = (DynObj) ro;
		if (r.define != this.statement.mapping) {
			throw new IllegalArgumentException("参数ro不属于本数据集");
		}
		return this.records.indexOf(ro);
	}

	public final boolean setCurrentRO(Object ro) {
		int p = this.positionOfRO(ro);
		if (p >= 0) {
			this.setRecordIndex(p);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 当前记录对象
	 */
	private DynObj currentRecord;

	private int currentRecordIndex;

	private final ArrayList<DynObj> records = new ArrayList<DynObj>(0);

	private ArrayList<DynObj> modifiedRecords;

	public void digestType(Digester digester) {
		digester.update(TypeCodeSet.RECORDSET_H);
		this.statement.digestType(digester);
	}
}
