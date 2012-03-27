package org.eclipse.jt.core.impl;

import java.util.ArrayList;

final class ObjByRecidsDeleteSql extends Sql {

	final ArrayList<StrongRefParameter> args = new ArrayList<StrongRefParameter>(
			SystemVariables.ORM_BYRECIDS_DELETE);

	ObjByRecidsDeleteSql(DBLang lang, MappingQueryStatementImpl statement) {
		statement.validate();
		statement.checkModifyRootOnly();
		final QuTableRef tableRef = statement.rootRelationRef()
				.castAsTableRef();
		final TableDefineImpl table = tableRef.getTarget();
		if (table.dbTables.size() == 1) {
			ISqlDeleteBuffer delete = lang.sqlbuffers().delete(
					table.primary.namedb(), ALIAS);
			where(delete, ALIAS, table.f_recid.namedb(), this.args);
			this.build(delete);
		} else {
			ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
			for (DBTableDefineImpl dbTable : table.dbTables) {
				ISqlDeleteBuffer delete = buffer
						.delete(dbTable.namedb(), ALIAS);
				where(delete, ALIAS, table.primary.namedb(), this.args);
			}
			this.build(buffer);
		}
	}

	private static final String ALIAS = "T";

	private static final void where(ISqlDeleteBuffer delete, String tableRef,
			String field, ArrayList<StrongRefParameter> args) {
		ISqlExprBuffer where = delete.where();
		where.loadField(tableRef, field);
		int c = 0;
		for (StrongRefParameter arg : args) {
			where.loadVar(arg);
			c++;
		}
		where.predicate(SqlPredicate.IN, c + 1);
	}

}