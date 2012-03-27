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
 * ����ר�����ݼ�
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
			throw new NullPointerException("��ǰ��¼Ϊ��");
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
			throw new IllegalArgumentException("��[" + tableField.owner.name
					+ "]�ļ�[" + tableField.name + "]����������ǰ׷�ӵı�ļ����Ͳ���");
		}
		return key;
	}

	// //////////////////////////////////
	// ���ݼ�����
	// //////////////////////////////////
	/**
	 * ��ն���
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
	 * �½���¼�ֶ�
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
			throw new IllegalArgumentException("restriction �����Ѿ�ʧЧ");
		}
		TableFieldDefineImpl tf = (TableFieldDefineImpl) tableField;
		return restriction.newField0(tf);
	}

	/**
	 * �����ֶθ���
	 */
	public final int getFieldCount() {
		return this.fields.size();
	}

	/**
	 * ���ĳλ�õ��ֶ�
	 */
	public final RPTRecordSetFieldImpl getField(int index) {
		return this.fields.get(index);
	}

	// /////////////////////////////////////
	// // �����
	// ////////////////////////////////////
	/**
	 * ��ȡ������
	 */
	public final int getKeyCount() {
		return this.keys.size();
	}

	/**
	 * ��ȡ��
	 */
	public final RPTRecordSetKeyImpl getKey(int index) {
		return this.keys.get(index);
	}

	/**
	 * ���ݼ����Ʋ��Ҽ�
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
			throw new MissingObjectException("�Ҳ�����Ϊ[" + keyName + "]�ļ�");
		}
		return key;
	}

	// /////////////////////////////////////
	// // �������
	// ////////////////////////////////////

	/**
	 * װ�����ݼ�
	 * 
	 * @return ���ؼ�¼����
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
			throw new NullArgumentException("���ݿ�������");
		}
		if (offset < 0 || rowCount <= 0) {
			throw new IllegalArgumentException("�����offset��rowCount");
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
			throw new NullArgumentException("���ݿ�������");
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
			// TODO �ǳ���ʱ������!!!!!!!!!
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
			// key�����һ�γ��ֵ�ti��ӳ��
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
							// ��ʾǰ����ֹ�ti
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
						throw new UnsupportedOperationException("��["
								+ ti.table.name + "]�޷�������������.");
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
				// ������ֶ�����,ֱ����������
				RPTRecordSetFieldImpl rsf = (RPTRecordSetFieldImpl) column;
				qu.newOrderBy(rsf.tableField, orderby.isDesc);
			} else if (column instanceof RPTRecordSetKeyImpl) {
				// ����������
				RPTRecordSetKeyImpl key = (RPTRecordSetKeyImpl) column;
				// ��Լ��˳��,Ϊÿ��������������Լ����������
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
						// ��Ӧ���ߵ��Ĵ����.Լ��֧�ָü�,�����ĳһTableInfo������������,continue��һ��Լ��.
						throw new IllegalArgumentException();
					}
				}
			} else {
				throw new IllegalArgumentException("��֧�ֵ�������.");
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
	 * ��ü�¼����
	 */
	public final int getRecordCount() {
		return this.records.size();
	}

	/**
	 * ��õ�ǰ��¼λ��
	 */
	public final int getCurrentRecordIndex() {
		return this.currentRecordIndex;
	}

	/**
	 * ���õ�ǰ��¼λ��
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
	 * �½���Ŀ��������Ϊ��ǰλ��
	 * 
	 * @return �����¼�¼��λ��
	 */
	public int newRecord() {
		RPTRecord record = this.newRecord(DynObj.r_new);
		this.addModifiedRecord(record);
		this.current = record;
		return this.currentRecordIndex = this.records.size() - 1;
	}

	/**
	 * ɾ����¼
	 */
	public void remove(int recordIndex) {
		if (recordIndex < 0 || recordIndex >= this.records.size()) {
			throw new IllegalArgumentException("��������");
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
	 * ɾ����ǰ��¼
	 */
	public final void removeCurrentRecord() {
		this.remove(this.currentRecordIndex);
	}

	/**
	 * �������ݼ�
	 * 
	 * @return ���ظ��¸���
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
