package org.eclipse.jt.core.impl;

final class RPTRecordDeleteSql extends Sql {

	final ArgumentReserver recid;

	private static final String alias = "T";

	RPTRecordDeleteSql(DBLang lang, RPTRecordSetTableInfo tableInfo) {
		this.recid = new ArgumentReserver(tableInfo.recidSf,
				tableInfo.recidSf.type);
		if (tableInfo.size() == 1) {
			DBTableDefineImpl dbTable = tableInfo.get(0).dbTable;
			ISqlDeleteBuffer buffer = lang.sqlbuffers().delete(dbTable.name,
					alias);
			delete(buffer, null, dbTable, this.recid);
			this.build(buffer);
		} else if (tableInfo.size() > 1) {
			ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
			for (int i = 0, c = tableInfo.size(); i < c; i++) {
				RPTRecordSetDBTableInfo dbTableInfo = tableInfo.get(i);
				delete(null, buffer, dbTableInfo.dbTable, this.recid);
			}
			this.build(buffer);
		}
	}

	private static final void delete(ISqlDeleteBuffer delete,
			ISqlSegmentBuffer segment, DBTableDefineImpl dbTable,
			StrongRefParameter recid) {
		if (delete == null) {
			delete = segment.delete(dbTable.name, alias);
		}
		delete.where().loadField(alias, TableDefineImpl.FIELD_DBNAME_RECID)
				.loadVar(recid).eq();
	}

}