package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.da.DBAdapter;
import org.eclipse.jt.core.impl.RPTRecordSetRecordDefine.RPTRecord;


final class RPTRecordSetUpdater {

	final DBAdapter context;
	final DBAdapterImpl adapter;
	final RPTRecordSetImpl recordSet;

	private final RPTRecordSetTableInfo[] tables;
	private CommonExecutor[] savers;
	private CommonExecutor[] deleters;

	RPTRecordSetUpdater(DBAdapter context, RPTRecordSetImpl recordSet)
			throws Throwable {
		this.context = context;
		this.adapter = DBAdapterImpl.toDBAdapter(context);
		this.recordSet = recordSet;
		ArrayList<RPTRecordSetTableInfo> tables = new ArrayList<RPTRecordSetTableInfo>();
		for (RPTRecordSetRestrictionImpl r : recordSet.restrictions) {
			tables.addAll(r.tables);
		}
		this.tables = tables.toArray(new RPTRecordSetTableInfo[tables.size()]);
	}

	final void update(RPTRecord record) {
		switch (record.getRecordState()) {
		case DynObj.r_new_modified:
		case DynObj.r_new:
		case DynObj.r_db_modifing:
			if (this.savers == null) {
				ArrayList<CommonExecutor> list = new ArrayList<CommonExecutor>();
				for (RPTRecordSetTableInfo tableInfo : this.tables) {
					list.add(new CommonExecutor(this.adapter,
							new RPTRecordSaveSql(this.adapter.lang, tableInfo)));
				}
				this.savers = list.toArray(new CommonExecutor[list.size()]);
			}
			for (int i = 0, c = this.tables.length; i < c; i++) {
				RPTRecordSetTableInfo table = this.tables[i];
				if ((record.mask & (1 << table.restriction.index)) == 0) {
					continue;
				}
				aTable: {
					for (RPTRecordSetKeyImpl key : table.keys) {
						if (!table.restriction.tryUpdateKeyFieldValueIfNull(
								record, key.index)) {
							break aTable;
						}
					}
					// recid,recverÎª¿ÕÔòÌîÖµ
					if (table.recidSf.isFieldValueNullNoCheck(record)) {
						table.recidSf.setFieldValueAsGUIDNoCheck(record,
								this.context.newRECID());
					}
					table.recverSf.setFieldValueAsLongNoCheck(record,
							this.context.newRECVER());
					this.savers[i].executeUpdate(record);
				}
			}
			record.setRecordState(DynObj.r_db);
			break;
		case DynObj.r_db_deleting:
			if (this.deleters == null) {
				ArrayList<CommonExecutor> list = new ArrayList<CommonExecutor>();
				for (RPTRecordSetTableInfo tableInfo : this.tables) {
					list.add(new CommonExecutor(
							this.adapter,
							new RPTRecordDeleteSql(this.adapter.lang, tableInfo)));
				}
				this.deleters = list.toArray(new CommonExecutor[list.size()]);
			}
			for (int i = 0, c = this.tables.length; i < c; i++) {
				RPTRecordSetTableInfo table = this.tables[i];
				if (table.recidSf.isFieldValueNullNoCheck(record)) {
					return;
				}
				this.deleters[i].executeUpdate(record);
			}
			break;
		}
	}

	final void unuse() {
		if (this.savers != null) {
			for (CommonExecutor p : this.savers) {
				p.unuse();
			}
		}
		if (this.deleters != null) {
			for (CommonExecutor p : this.deleters) {
				p.unuse();
			}
		}
	}

}
