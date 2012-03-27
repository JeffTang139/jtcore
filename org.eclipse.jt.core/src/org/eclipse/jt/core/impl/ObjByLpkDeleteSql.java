package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;

final class ObjByLpkDeleteSql extends Sql {

	final ArgumentReserver[] args;

	ObjByLpkDeleteSql(DBLang lang, MappingQueryStatementImpl statement) {
		statement.validate();
		statement.checkModifyRootOnly();
		final QuRootTableRef tableRef = (QuRootTableRef) statement
				.rootRelationRef();
		final TableDefineImpl table = tableRef.getTarget();
		table.checkLogicalKeyAvaiable();
		final int c = table.logicalKey.items.size();
		final TableFieldDefineImpl[] keys = new TableFieldDefineImpl[c];
		this.args = new ArgumentReserver[c];
		statement.setRootKeys(keys, this.args);
		final String alias = "T";
		if (table.dbTables.size() == 1) {
			final DBTableDefineImpl primary = table.primary;
			ISqlDeleteBuffer delete = lang.sqlbuffers().delete(
					primary.namedb(), alias);
			ISqlExprBuffer where = delete.where();
			MappingQueryStatementImpl.fillLpkWhere(where, alias, keys,
					this.args);
			this.build(delete);
		} else {
			final String VREC = "VRECID";
			ISqlSegmentBuffer buffer = lang.sqlbuffers().segment();
			buffer.declare(VREC, GUIDType.TYPE);
			ISqlSelectIntoBuffer si = buffer.selectInto();
			si.newTable(table.primary.namedb(), alias);
			si.newColumn(VREC).loadField(alias, FIELD_DBNAME_RECID);
			ISqlExprBuffer where = si.where();
			MappingQueryStatementImpl.fillLpkWhere(where, alias, keys,
					this.args);
			ISqlConditionBuffer ifs = buffer.ifThenElse();
			ifs.newWhen().loadVar(VREC).predicate(SqlPredicate.IS_NOT_NULL, 1);
			ISqlSegmentBuffer then = ifs.newThen();
			for (DBTableDefineImpl dbTable : table.dbTables) {
				final String ma = Render.rowModifyAlias(dbTable);
				ISqlDeleteBuffer delete = then.delete(dbTable.namedb(), ma);
				delete.where().loadField(ma, FIELD_DBNAME_RECID).loadVar(VREC)
						.eq();
			}
			this.build(buffer);
		}
		// HCL ¼¶´Î±íÉ¾³ý
	}

}