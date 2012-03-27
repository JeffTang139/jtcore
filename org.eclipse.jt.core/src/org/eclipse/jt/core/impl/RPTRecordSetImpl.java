package org.eclipse.jt.core.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jt.core.da.DBAdapter;
import org.eclipse.jt.core.da.ext.RPTRecordSet;
import org.eclipse.jt.core.da.ext.RPTRecordSetColumn;
import org.eclipse.jt.core.da.ext.RPTRecordSetRestriction;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.RPTRecordSetRecordDefine.RPTRecord;
import org.eclipse.jt.core.misc.MissingObjectException;


/**
 * 报表专用数据集
 * 
 * @author Jeff Tang
 * 
 */
public final class RPTRecordSetImpl implements RPTRecordSet {

	public static class FactoryImpl implements Factory {
		public RPTRecordSet newRPTRecordSet() {
			return new RPTRecordSetImpl();
		}
	}

	RPTRecordSetImpl() {
		this.resetRestrictions();
	}

	final void checkCurrentRecordValid() {
		if (this.current == null) {
			throw new NullPointerException("当前记录为空");
		}
	}

	final RPTRecord getRecordRead() {
		this.checkCurrentRecordValid();
		return this.current;
	}

	final RPTRecord getRecordWrite() {
		this.checkCurrentRecordValid();
		switch (this.current.getRecordState()) {
		case DynObj.r_db:
			this.current.setRecordState(DynObj.r_db_modifing);
			this.addModifiedRecord(this.current);
			break;
		case DynObj.r_new:
			this.current.setRecordState(DynObj.r_new_modified);
			break;
		}
		return this.current;
	}

	final void updateRecordMask(int index) {
		if (index < 0) {
			throw new IllegalArgumentException();
		}
		RPTRecord record = this.current;
		record.mask |= (1 << index);
	}

	int generation;

	private RPTRecord current;

	final RPTRecordSetRecordDefine recordStruct = new RPTRecordSetRecordDefine();

	final ArrayList<RPTRecordSetFieldImpl> fields = new ArrayList<RPTRecordSetFieldImpl>();

	final ArrayList<RPTRecordSetKeyImpl> keys = new ArrayList<RPTRecordSetKeyImpl>();

	final RPTRecordSetKeyImpl ensureKey(TableFieldDefineImpl tableField,
			int rollbackFieldCount, int rollbackKeyCount) {
		RPTRecordSetKeyImpl key = this.findKey(tableField.name);
		if (key == null) {
			this.keys.add(key = new RPTRecordSetKeyImpl(this, tableField));
		} else if (key.field.type.getRootType() != tableField.getType()
				.getRootType()) {
			for (int j = this.keys.size() - 1; j >= rollbackKeyCount; j--) {
				this.keys.remove(j);
			}
			for (int j = this.recordStruct.fields.size() - 1; j >= rollbackFieldCount; j--) {
				this.recordStruct.fields.remove(j);
			}
			throw new IllegalArgumentException("表[" + tableField.owner.name
					+ "]的键[" + tableField.name + "]的类型与先前追加的表的键类型不符");
		}
		return key;
	}

	// //////////////////////////////////
	// 数据集定义
	// //////////////////////////////////
	/**
	 * 清空定义
	 */
	public final void reset() {
		this.generation++;
		this.keys.clear();
		this.fields.clear();
		this.records.clear();
		this.recordStruct.reset();
		this.currentRecordIndex = 0;
		this.current = null;
		if (this.orderbys != null) {
			this.orderbys.clear();
		}
		this.resetRestrictions();
	}

	private void resetRestrictions() {
		this.restrictions.clear();
		this.firstRestriction = new RPTRecordSetRestrictionImpl(this);
		this.restrictions.add(this.firstRestriction);
	}

	private final void ensurePrepared() {
		for (int i = 0, c = this.restrictions.size(); i < c; i++) {
			this.restrictions.get(i).ensurePrepared();
		}
		this.recordStruct.prepareAccessInfo();
	}

	RPTRecordSetRestrictionImpl firstRestriction;

	final ArrayList<RPTRecordSetRestrictionImpl> restrictions = new ArrayList<RPTRecordSetRestrictionImpl>(
			1);

	public final RPTRecordSetRestrictionImpl newRestriction() {
		RPTRecordSetRestrictionImpl restriction = new RPTRecordSetRestrictionImpl(
				this);
		this.restrictions.add(restriction);
		return restriction;
	}

	/**
	 * 新建记录字段
	 */
	public final RPTRecordSetFieldImpl newField(TableFieldDefine tableField) {
		return this.newField0(tableField, this.firstRestriction);
	}

	public final RPTRecordSetFieldImpl newField(TableFieldDefine tableField,
			RPTRecordSetRestriction restriction) {
		return this.newField0(tableField,
				(RPTRecordSetRestrictionImpl) restriction);
	}

	private final RPTRecordSetFieldImpl newField0(TableFieldDefine tableField,
			RPTRecordSetRestrictionImpl restriction) {
		if (tableField == null) {
			throw new NullArgumentException("tableField");
		}
		if (restriction == null) {
			throw new NullArgumentException("restriction");
		}
		if (restriction.owner != this) {
			throw new IllegalArgumentException("restriction 对象已经失效");
		}
		TableFieldDefineImpl tf = (TableFieldDefineImpl) tableField;
		return restriction.newField0(tf);
	}

	/**
	 * 返回字段个数
	 */
	public final int getFieldCount() {
		return this.fields.size();
	}

	/**
	 * 获得某位置的字段
	 */
	public final RPTRecordSetFieldImpl getField(int index) {
		return this.fields.get(index);
	}

	// /////////////////////////////////////
	// // 键相关
	// ////////////////////////////////////
	/**
	 * 获取键个数
	 */
	public final int getKeyCount() {
		return this.keys.size();
	}

	/**
	 * 获取键
	 */
	public final RPTRecordSetKeyImpl getKey(int index) {
		return this.keys.get(index);
	}

	/**
	 * 根据键名称查找键
	 */
	public final RPTRecordSetKeyImpl findKey(String keyName) {
		if (keyName == null || keyName.length() == 0) {
			throw new NullArgumentException("keyName");
		}
		for (int i = 0, c = this.keys.size(); i < c; i++) {
			RPTRecordSetKeyImpl key = this.keys.get(i);
			if (key.field.name.equals(keyName)) {
				return key;
			} else
			// !!!
			if (keyName.equals("RECID") && key.field.name.equals("UNITID")) {
				return key;
			}
		}
		return null;
	}

	public final RPTRecordSetKeyImpl getKey(String keyName) {
		RPTRecordSetKeyImpl key = this.findKey(keyName);
		if (key == null) {
			throw new MissingObjectException("找不到名为[" + keyName + "]的键");
		}
		return key;
	}

	// /////////////////////////////////////
	// // 数据相关
	// ////////////////////////////////////

	/**
	 * 装载数据集
	 * 
	 * @return 返回记录个数
	 */
	public final int load(DBAdapter dbAdapter) {
		if (dbAdapter == null) {
			throw new NullArgumentException("dbAdapter");
		}
		try {
			DBAdapterImpl adapter = DBAdapterImpl.toDBAdapter(dbAdapter);
			this.ensurePrepared();
			this.records.clear();
			this.currentRecordIndex = 0;
			this.current = null;
			RPTRecordSetRecordReader reader = new RPTRecordSetRecordReader(this);
			for (int i = 0, c = this.restrictions.size(); i < c; i++) {
				RPTRecordSetRestrictionImpl r = this.restrictions.get(i);
				r.load(adapter, reader);
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
		int recordCount = this.records.size();
		if (recordCount > 0) {
			this.current = this.records.get(0);
		}
		return recordCount;
	}

	public final int load(DBAdapter dbAdapter, int offset, int rowCount) {
		if (dbAdapter == null) {
			throw new NullArgumentException("数据库适配器");
		}
		if (offset < 0 || rowCount <= 0) {
			throw new IllegalArgumentException("错误的offset或rowCount");
		}
		ArrayList<Object> paramValues = new ArrayList<Object>();
		MappingQueryStatementImpl query = this
				.buildSimpleQueryStatement(paramValues);
		try {
			DBAdapterImpl dba = DBAdapterImpl.toDBAdapter(dbAdapter);
			SimpleLimitQuerier querier = new SimpleLimitQuerier(dba, query);
			try {
				return querier.load(this, paramValues, offset, rowCount);
			} finally {
				querier.unuse();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	public final int getRecordCountInDB(DBAdapter dbAdapter) {
		if (dbAdapter == null) {
			throw new NullArgumentException("数据库适配器");
		}
		ArrayList<Object> paramValues = new ArrayList<Object>();
		MappingQueryStatementImpl query = this
				.buildSimpleQueryStatement(paramValues);
		try {
			DBAdapterImpl dba = DBAdapterImpl.toDBAdapter(dbAdapter);
			SimpleRowCountQuerier querier = new SimpleRowCountQuerier(dba,
					query.getQueryRowCountSql(dba));
			try {
				return querier.executeQuery(paramValues);
			} finally {
				querier.unuse();
			}
		} catch (SQLException e) {
			throw Utils.tryThrowException(e);
		}
	}

	private static final class SimpleRowCountQuerier extends
			PsExecutor<QueryRowCountSql> {

		SimpleRowCountQuerier(DBAdapterImpl adapter, QueryRowCountSql sql) {
			super(adapter, sql);
		}

		final int executeQuery(ArrayList<Object> paramValues)
				throws SQLException {
			for (int i = 0, parameterIndex = 1, c = paramValues.size(); i < c; i++) {
				this.ps.setObject(parameterIndex++, paramValues.get(i));
			}
			ResultSet rs = this.ps.executeQuery();
			try {
				if (rs.next()) {
					return rs.getInt(1);
				}
				return 0;
			} finally {
				rs.close();
			}
		}
	}

	private static final class SimpleLimitQuerier extends
			PsExecutor<QueryLimitSql> {

		final MappingQueryStatementImpl statement;

		SimpleLimitQuerier(DBAdapterImpl adapter,
				MappingQueryStatementImpl statement) {
			super(adapter, statement.getQueryLimitSql(adapter));
			this.statement = statement;
		}

		final int load(RPTRecordSetImpl rpt, ArrayList<Object> paramValues,
				int offset, int rowCount) throws SQLException {
			int parameterIndex = 1;
			for (int i = 0, c = paramValues.size(); i < c; i++) {
				this.ps.setObject(parameterIndex++, paramValues.get(i));
			}
			// TODO 非常临时的做法!!!!!!!!!
			this.ps.setInt(parameterIndex++, offset + rowCount);
			this.ps.setInt(parameterIndex++, offset + 1);
			ResultSet rs = this.ps.executeQuery();
			try {
				ResultSetDynObjReader reader = new ResultSetDynObjReader(rs);
				while (rs.next()) {
					reader.readRecord(rpt.newRecord(DynObj.r_db),
							this.statement);
				}
				return rpt.records.size();
			} finally {
				rs.close();
			}
		}
	}

	private final MappingQueryStatementImpl buildSimpleQueryStatement(
			ArrayList<Object> paramValues) {
		MappingQueryStatementImpl qu = new MappingQueryStatementImpl("rpt",
				this.recordStruct);
		final int keyCount = this.keys.size();
		final ArgumentRefExpr dummyArg = new ArgumentRefExpr(
				this.recordStruct.fields.get(0));
		ArrayList<PredicateExpr> inConditions = null;
		for (RPTRecordSetRestrictionImpl rstr : this.restrictions) {
			final HashMap<RPTRecordSetTableInfo, QuTableRef> ti2tr = new HashMap<RPTRecordSetTableInfo, QuTableRef>();
			// key与其第一次出现的ti的映射
			final HashMap<RPTRecordSetKeyImpl, RPTRecordSetTableInfo> key2ti = new HashMap<RPTRecordSetKeyImpl, RPTRecordSetTableInfo>();
			QuRootTableRef tr = null;
			for (RPTRecordSetTableInfo ti : rstr.tables) {
				if (tr == null) {
					tr = qu.newReference(ti.table);
					ti2tr.put(ti, tr);
				} else {
					QuJoinedTableRef join = tr.newJoin(ti.table);
					qu.newColumn(ti.table.f_recid).field = ti.recidSf;
					qu.newColumn(ti.table.f_recver).field = ti.recverSf;
					for (RPTRecordSetKeyImpl key : ti.keys) {
						if (key2ti.containsKey(key)) {
							// 表示前面出现过ti
							RPTRecordSetTableInfo otherTi = key2ti.get(key);
							QuTableRef otherTr = ti2tr.get(otherTi);
							ConditionalExpr condition = join.expOf(
									ti.tablefieldOf(key)).xEq(
									otherTr.expOf(otherTi.tablefieldOf(key)));
							if (join.getJoinCondition() == null) {
								join.setJoinCondition(condition);
							} else {
								join.setJoinCondition(join.getJoinCondition()
										.and(condition));
							}
						}
					}
					if (join.getJoinCondition() == null) {
						throw new UnsupportedOperationException("表["
								+ ti.table.name + "]无法与其它表连接.");
					}
					ti2tr.put(ti, join);
				}
				for (RPTRecordSetKeyImpl key : ti.keys) {
					if (key2ti.containsKey(key)) {
						key2ti.put(key, ti);
					}
				}
			}
			for (int ki = 0; ki < keyCount; ki++) {
				RPTRecordSetKeyRestrictionImpl kr = rstr.useKeyRestriction(ki);
				if (kr != null && kr.getMatchValueCount() > 0) {
					RPTRecordSetKeyImpl key = kr.key;
					RPTRecordSetTableInfo ti = key2ti.get(key);
					QuTableRef tableref = ti2tr.get(ti);
					ValueExpr[] exprs = new ValueExpr[kr.getMatchValueCount() + 1];
					exprs[0] = tableref.expOf(ti.tablefieldOf(key));
					for (int i = 1; i < exprs.length; i++) {
						exprs[i] = dummyArg;
					}
					PredicateExpr predicate = new PredicateExpr(false,
							PredicateImpl.IN, exprs);
					if (inConditions == null) {
						inConditions = new ArrayList<PredicateExpr>();
					}
					inConditions.add(predicate);
					kr.fillAsSqlParams(paramValues);
				}
			}
		}
		if (inConditions != null && inConditions.size() > 0) {
			qu.setCondition(new CombinedExpr(false, true, inConditions
					.toArray(new ConditionalExpr[inConditions.size()])));
		}
		for (RPTRecordSetFieldImpl f : this.fields) {
			qu.newColumn(f.tableField).field = f.field;
		}
		for (RPTRecordSetOrderByImpl orderby : this.orderbys) {
			RPTRecordSetColumnImpl column = orderby.column;
			if (column instanceof RPTRecordSetFieldImpl) {
				// 按输出字段排序,直接增加排序
				RPTRecordSetFieldImpl rsf = (RPTRecordSetFieldImpl) column;
				qu.newOrderBy(rsf.tableField, orderby.isDesc);
			} else if (column instanceof RPTRecordSetKeyImpl) {
				// 按主键排序
				RPTRecordSetKeyImpl key = (RPTRecordSetKeyImpl) column;
				// 按约束顺序,为每个包含该主键的约束增加排序
				restrictionNewOrderby: for (int i = 0, c = this.restrictions
						.size(); i < c; i++) {
					RPTRecordSetRestrictionImpl rstr = this.restrictions.get(i);
					if (rstr.isKeySupported(key)) {
						for (RPTRecordSetTableInfo ti : rstr.tables) {
							if (ti.hasKey(key)) {
								qu.newOrderBy(ti.tablefieldOf(key),
										orderby.isDesc);
							}
							continue restrictionNewOrderby;
						}
						// 不应该走到的代码块.约束支持该键,则必在某一TableInfo中增加上排序,continue下一个约束.
						throw new IllegalArgumentException();
					}
				}
			} else {
				throw new IllegalArgumentException("不支持的排序列.");
			}
		}
		return qu;
	}

	public ArrayList<RPTRecord> records = new ArrayList<RPTRecord>(0);
	private int currentRecordIndex;

	private ArrayList<RPTRecord> modifiedRecords;

	final void addModifiedRecord(RPTRecord record) {
		if (this.modifiedRecords == null) {
			this.modifiedRecords = new ArrayList<RPTRecord>();
		}
		this.modifiedRecords.add(record);
	}

	/**
	 * 获得记录个数
	 */
	public final int getRecordCount() {
		return this.records.size();
	}

	/**
	 * 获得当前记录位置
	 */
	public final int getCurrentRecordIndex() {
		return this.currentRecordIndex;
	}

	/**
	 * 设置当前记录位置
	 */
	public void setCurrentRecordIndex(int recordIndex) {
		this.current = this.records.get(recordIndex);
		this.currentRecordIndex = recordIndex;
	}

	final RPTRecord newRecord(int state) {
		RPTRecord rp = this.recordStruct.newRecord(state);
		this.records.add(rp);
		return rp;
	}

	/**
	 * 新建条目，并设置为当前位置
	 * 
	 * @return 返回新记录的位置
	 */
	public int newRecord() {
		RPTRecord record = this.newRecord(DynObj.r_new);
		this.addModifiedRecord(record);
		this.current = record;
		return this.currentRecordIndex = this.records.size() - 1;
	}

	/**
	 * 删除记录
	 */
	public void remove(int recordIndex) {
		if (recordIndex < 0 || recordIndex >= this.records.size()) {
			throw new IllegalArgumentException("错误的序号");
		}
		DynObj obj = this.records.get(recordIndex);
		obj.setRecordState(DynObj.r_db_deleting);
		this.addModifiedRecord(this.records.remove(recordIndex));
		if (this.records.size() > this.currentRecordIndex) {
			this.current = this.records.get(this.currentRecordIndex);
		} else {
			this.current = null;
		}
	}

	/**
	 * 删除当前记录
	 */
	public final void removeCurrentRecord() {
		this.remove(this.currentRecordIndex);
	}

	/**
	 * 更新数据集
	 * 
	 * @return 返回更新个数
	 */
	public int update(DBAdapter context) {
		if (this.modifiedRecords == null) {
			return 0;
		}
		int resCount = 0;
		int modCount = this.modifiedRecords.size();
		try {
			if (modCount > 0) {
				RPTRecordSetUpdater updater = new RPTRecordSetUpdater(context,
						this);
				try {
					for (int recIndex = modCount - 1; recIndex >= 0; recIndex--) {
						RPTRecord record = this.modifiedRecords.get(recIndex);
						updater.update(record);
						this.modifiedRecords.remove(recIndex);
						resCount++;
					}
				} finally {
					updater.unuse();
				}
			}
			return resCount;
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private ArrayList<RPTRecordSetOrderByImpl> orderbys;

	public final RPTRecordSetOrderByImpl newOrderBy(RPTRecordSetColumn column,
			boolean isDesc, boolean isNullAsMIN) {
		if (column == null) {
			throw new NullArgumentException("column");
		}
		RPTRecordSetColumnImpl col = (RPTRecordSetColumnImpl) column;
		if (col.owner != this || col.generation != this.generation) {
			throw new IllegalArgumentException("column");
		}
		RPTRecordSetOrderByImpl orderBy = new RPTRecordSetOrderByImpl(col,
				isDesc, isNullAsMIN);
		if (this.orderbys == null) {
			this.orderbys = new ArrayList<RPTRecordSetOrderByImpl>();
		}
		this.orderbys.add(orderBy);
		return orderBy;
	}

	public final RPTRecordSetOrderByImpl newOrderBy(RPTRecordSetColumn column,
			boolean isDesc) {
		return this.newOrderBy(column, isDesc, true);
	}

	public final RPTRecordSetOrderByImpl newOrderBy(RPTRecordSetColumn column) {
		return this.newOrderBy(column, false, true);
	}

	public final RPTRecordSetOrderByImpl getOrderBy(int index) {
		if (this.orderbys != null) {
			return this.orderbys.get(index);
		}
		throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
	}

	public final int getOrderByCount() {
		return this.orderbys != null ? this.orderbys.size() : 0;
	}

	public final RPTRecordSetRestrictionImpl getFirstRestriction() {
		return this.firstRestriction;
	}

}
