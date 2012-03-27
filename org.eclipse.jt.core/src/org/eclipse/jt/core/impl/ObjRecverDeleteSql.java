package org.eclipse.jt.core.impl;

final class ObjRecverDeleteSql extends Sql {

	final StructFieldDefineImpl arg_recid;

	final StrongRefParameter recver = new StrongRefParameter();

	private static final String alias = "T";

	ObjRecverDeleteSql(DBLang lang, MappingQueryStatementImpl statement) {
		statement.checkModifyRootOnly();
		final QuRootTableRef tableRef = (QuRootTableRef) statement
				.rootRelationRef();
		final TableDefineImpl table = tableRef.getTarget();
		final QueryColumnImpl recid = statement.findEqualColumn(tableRef,
				tableRef.getTarget().f_recid);
		if (recid == null) {
			throw Render.noRecidColumnForTable(statement, table);
		}
		this.arg_recid = recid.field;
		if (table.dbTables.size() == 1) {
			ISqlDeleteBuffer delete = lang.sqlbuffers().delete(
					table.primary.namedb(), alias);
			this.single(table.primary, delete, null);
			this.build(delete);
		} else {
			ISqlSegmentBuffer segment = lang.sqlbuffers().segment();
			ISqlDeleteBuffer delete = segment.delete(table.primary.namedb(),
					alias);
			this.single(table.primary, delete, null);
			ISqlConditionBuffer ifs = segment.ifThenElse();
			ISqlExprBuffer when = ifs.newWhen();
			when.func(SqlFunction.ROW_COUNT, 0).load(1).eq();
			ISqlSegmentBuffer then = ifs.newThen();
			for (int i = 1, c = table.dbTables.size(); i < c; i++) {
				DBTableDefineImpl dbTable = table.dbTables.get(i);
				this.single(dbTable, null, then);
			}
			this.build(segment);
		}
	}

	private final void single(DBTableDefineImpl dbTable,
			ISqlDeleteBuffer delete, ISqlSegmentBuffer segment) {
		if (delete == null) {
			delete = segment.delete(dbTable.namedb(), alias);
		}
		ISqlExprBuffer where = delete.where();
		where.loadField(alias, TableDefineImpl.FIELD_DBNAME_RECID);
		where.loadVar(new ArgumentReserver(this.arg_recid, this.arg_recid.type));
		where.eq();
		if (dbTable.isPrimary()) {
			where.loadField(alias, TableDefineImpl.FIELD_DBNAME_RECVER);
			where.loadVar(this.recver).eq();
			where.and(2);
		}
	}
}