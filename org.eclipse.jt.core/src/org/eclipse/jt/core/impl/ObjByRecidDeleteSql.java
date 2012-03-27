package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;

final class ObjByRecidDeleteSql extends Sql {

	final ArgumentReserver recid;

	ObjByRecidDeleteSql(DBLang lang, MappingQueryStatementImpl statement) {
		statement.validate();
		statement.checkModifyRootOnly();
		final QuTableRef tableRef = statement.rootRelationRef()
				.castAsTableRef();
		final TableDefineImpl table = tableRef.getTarget();
		final QueryColumnImpl recid = statement.findEqualColumn(tableRef,
				table.f_recid);
		if (recid == null) {
			throw Render.noRecidColumnForTable(statement, table);
		}
		this.recid = new ArgumentReserver(recid.field, recid.field.type);
		final String alias = "T";
		if (table.dbTables.size() == 1) {
			ISqlDeleteBuffer delete = lang.sqlbuffers().delete(
					table.primary.namedb(), alias);
			delete.where().loadField(alias, FIELD_DBNAME_RECID)
					.loadVar(this.recid).eq();
			this.build(delete);
		} else {
			ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
			for (DBTableDefineImpl dbTable : table.dbTables) {
				ISqlDeleteBuffer delete = buffer
						.delete(dbTable.namedb(), alias);
				delete.where().loadField(alias, FIELD_DBNAME_RECID)
						.loadVar(this.recid).eq();
			}
			this.build(buffer);
		}
	}

}