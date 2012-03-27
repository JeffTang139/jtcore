/**
 * 
 */
package org.eclipse.jt.core.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jt.core.type.GUID;


final class RPTRecordSetTableInfo extends ArrayList<RPTRecordSetDBTableInfo> {

	private static final long serialVersionUID = 1L;
	final StructFieldDefineImpl recidSf;
	final StructFieldDefineImpl recverSf;
	final TableDefineImpl table;
	final TableFieldDefineImpl[] fields;
	final RPTRecordSetKeyImpl[] keys;
	final RPTRecordSetRestrictionImpl restriction;

	final boolean hasKey(RPTRecordSetKeyImpl key) {
		for (RPTRecordSetKeyImpl k : this.keys) {
			if (k == key) {
				return true;
			}
		}
		return false;
	}

	final TableFieldDefineImpl tablefieldOf(RPTRecordSetKeyImpl key) {
		for (int i = 0; i < this.keys.length; i++) {
			if (this.keys[i] == key) {
				return this.fields[i];
			}
		}
		throw new IllegalArgumentException();
	}

	final StructFieldDefineImpl getKeyValueField(int index) {
		RPTRecordSetKeyImpl key = this.keys[index];
		StructFieldDefineImpl sf = this.restriction
				.tryGetKeyRestrictionField(key.index);
		if (sf != null) {
			return sf;
		}
		return key.field;
	}

	final RPTRecordSetFieldImpl newField(TableFieldDefineImpl tableField) {
		DBTableDefineImpl dbTable = tableField.dbTable;
		RPTRecordSetDBTableInfo dbTableInfo = this.findDBTableInfo(dbTable);
		if (dbTableInfo == null) {
			this.add(dbTableInfo = new RPTRecordSetDBTableInfo(dbTable));
		}
		RPTRecordSetFieldImpl field = new RPTRecordSetFieldImpl(
				this.restriction.owner, tableField, this.restriction);
		dbTableInfo.add(field);
		field.owner.fields.add(field);
		return field;
	}

	private final QuRootTableRef newConditionalMainQuery(
			RPTRecordSetRecordReader reader) {
		reader.resetQuery();
		final RPTRecordSetImpl owner = this.restriction.owner;
		final QueryStatementImpl query = new QueryStatementImpl("rpt-m",
				owner.recordStruct);
		final QuRootTableRef tr = query.newReference(this.table);
		final int keyCount = this.fields.length;
		ArrayList<ConditionalExpr> condis = reader.condisCache;
		ConditionalExpr allce = null;
		// ///////KEYS/////////////
		for (int i = 0; i < keyCount; i++) {
			final TableFieldDefineImpl tf = this.fields[i];
			final TableFieldRefImpl fre = new TableFieldRefImpl(tr, tf);
			query.newColumn(fre, tf.name);
			RPTRecordSetKeyRestrictionImpl kr = this.restriction
					.useKeyRestriction(this.keys[i].index, reader);
			if (kr == null) {
				continue;
			}
			final int paramCount = kr.getMatchValueCount();
			if (paramCount > 0) {
				reader.paramCache.ensureCapacity(paramCount);
				kr.fillAsSqlParams(reader.paramCache);
				if (allce != null) {
					condis.add(allce);
					allce = null;
				}
				allce = reader.newInCondi(fre, paramCount);
			}
		}
		if (allce != null) {
			final int condiCount = condis.size() + 1;
			if (condiCount > 1) {
				condis.add(allce);
				ConditionalExpr[] ands = condis
						.toArray(new ConditionalExpr[condiCount]);
				condis.clear();
				allce = new CombinedExpr(false, true, ands);
			}
			query.setCondition(allce);
		}
		int orderbyCount = this.restriction.owner.getOrderByCount();
		if (orderbyCount > 0) {
			for (int i = 0; i < orderbyCount; i++) {
				RPTRecordSetOrderByImpl orderby = this.restriction.owner
						.getOrderBy(i);
				RPTRecordSetColumnImpl column = orderby.column;
				if (column instanceof RPTRecordSetFieldImpl) {
					query.newOrderBy(
							((RPTRecordSetFieldImpl) column).tableField,
							orderby.isDesc);
				} else if (column instanceof RPTRecordSetKeyImpl) {
					RPTRecordSetKeyImpl k = (RPTRecordSetKeyImpl) column;
					for (int keyIdx = 0; keyIdx < this.keys.length; keyIdx++) {
						if (this.keys[keyIdx] == k) {
							query.newOrderBy(this.fields[i], orderby.isDesc);
							break;
						}
					}
				} else {
					throw new UnsupportedOperationException();
				}
			}
		}
		// //////RECVER/////////////
		tr.newColumn(this.table.f_recver);
		// //////RECID/////////////
		tr.newColumn(this.table.f_recid);
		return tr;
	}

	private final QuRootTableRef newConditionalRestQuery(
			RPTRecordSetRecordReader reader) {
		reader.resetQuery();
		final HashMap<Object, DynObj> recidMap = reader.getRecidMap();
		final int paramCount = recidMap.size();
		if (paramCount == 0) {
			throw new IllegalStateException();
		}
		RPTRecordSetImpl owner = this.restriction.owner;
		QueryStatementImpl query = new QueryStatementImpl("rpt-r",
				owner.recordStruct);
		QuRootTableRef tr = query.newReference(this.table);
		reader.paramCache.ensureCapacity(paramCount);
		for (Object param : recidMap.keySet()) {
			reader.paramCache.add(((GUID) param).toBytes());
		}
		final TableFieldRefImpl fre = new TableFieldRefImpl(tr,
				this.table.f_recid);
		query.setCondition(reader.newInCondi(fre, paramCount));
		// //////RECID/////////////
		query.newColumn(fre, "RECID");
		return tr;
	}

	private int loadPart(DBAdapterImpl dbAdapter, QuTableRef ref,
			RPTRecordSetRecordReader reader, boolean isFirstPart,
			boolean isLastPart) throws SQLException {
		QuerySql sql = new QuerySql(dbAdapter.lang,
				(QueryStatementImpl) ref.getOwner());
		CommonExecutor fetcher = new CommonExecutor(dbAdapter, sql);
		fetcher.use(false);
		try {
			int resultCount = 0;
			final int paramCount = reader.paramCache.size();
			int condisUsed = 0;
			do {
				if (condisUsed != 0) {
					fetcher.ps.clearParameters();
				}
				for (int i = 1, c = sql.parameters.size(); i <= c
						&& condisUsed < paramCount; i++, condisUsed++) {
					fetcher.ps.setObject(i, reader.paramCache.get(condisUsed));
				}
				reader.resultSet = dbAdapter.jdbcQuery(fetcher);
				try {
					resultCount += reader.readTablePart(this, isFirstPart,
							isLastPart);
				} finally {
					reader.resultSet.close();
				}
			} while (condisUsed < paramCount);
			return resultCount;
		} finally {
			fetcher.unuse();
			reader.paramCache.clear();
		}
	}

	final void load(DBAdapterImpl dbAdapter, RPTRecordSetRecordReader reader)
			throws SQLException {
		boolean isFirstPart = true;
		QuRootTableRef ref = this.newConditionalMainQuery(reader);
		// ///////VALUES/////////////
		for (int i = 0, c = this.size(); i < c; i++) {
			RPTRecordSetDBTableInfo dbTableInfo = this.get(i);
			int colInDBTable = dbTableInfo.size();
			if (colInDBTable + ref.owner.columns.size() > 1000) {
				if (this.loadPart(dbAdapter, ref, reader, isFirstPart, false) == 0) {
					return;
				}
				isFirstPart = false;
				ref = this.newConditionalRestQuery(reader);
			}
			for (int j = 0; j < colInDBTable; j++) {
				RPTRecordSetFieldImpl field = dbTableInfo.get(j);
				ref.newColumn(field.tableField);
				reader.addDataFieldToCache(field.field);
			}
		}
		this.loadPart(dbAdapter, ref, reader, isFirstPart, true);
	}

	private final RPTRecordSetDBTableInfo findDBTableInfo(
			DBTableDefineImpl dbTable) {
		for (int i = 0, c = this.size(); i < c; i++) {
			RPTRecordSetDBTableInfo tb = this.get(i);
			if (tb.dbTable == dbTable) {
				return tb;
			}
		}
		return null;
	}

	private static final String MD_ORG = "MD_ORG";
	private static final String UNITID = "UNITID";

	private static final void joinUsingRecidReplaceUnitid(
			RPTRecordSetImpl owner, TableDefineImpl table,
			ArrayList<TableFieldDefineImpl> fields,
			ArrayList<RPTRecordSetKeyImpl> keys) {
		fields.add(table.f_recid);
		RPTRecordSetKeyImpl key = owner.findKey(UNITID);
		if (key == null) {
			owner.keys.add(key = new RPTRecordSetKeyImpl(owner, UNITID,
					GUIDType.TYPE));
		}
		keys.add(key);
	}

	RPTRecordSetTableInfo(RPTRecordSetRestrictionImpl restriction,
			TableDefineImpl table) {
		RPTRecordSetImpl owner = restriction.owner;
		this.restriction = restriction;
		int rollbackField = 0;
		int rollbackKey = 0;
		ArrayList<TableFieldDefineImpl> fields = new ArrayList<TableFieldDefineImpl>();
		ArrayList<RPTRecordSetKeyImpl> keys = new ArrayList<RPTRecordSetKeyImpl>();
		if (table.name.equals(MD_ORG)) {
			joinUsingRecidReplaceUnitid(owner, table, fields, keys);
		} else {
			for (int i = 0, c = table.fields.size(); i < c; i++) {
				TableFieldDefineImpl fd = table.fields.get(i);
				if (fd.isPrimaryKey()) {
					rollbackField = owner.recordStruct.fields.size();
					rollbackKey = owner.keys.size();
					keys.add(owner.ensureKey(fd, rollbackField, rollbackKey));
					fields.add(fd);
				}
			}
		}
		if (fields.size() == 0 || keys.size() == 0) {
			throw new IllegalArgumentException("±í[" + table.name + "]Î´°üº¬Ö÷¼ü");
		}
		int keyCount = fields.size();
		this.fields = fields.toArray(new TableFieldDefineImpl[keyCount]);
		this.keys = keys.toArray(new RPTRecordSetKeyImpl[keyCount]);
		this.table = table;
		this.recidSf = owner.recordStruct.newField(table.f_recid.getType());
		this.recverSf = owner.recordStruct.newField(table.f_recver.getType());
	}

}