package org.eclipse.jt.core.impl;

import static org.eclipse.jt.core.impl.TableDefineImpl.FIELD_DBNAME_RECID;

final class ObjByRecidQuerySql extends Sql {

	ArgumentReserver recid;

	ObjByRecidQuerySql(DBLang lang, MappingQueryStatementImpl statement) {
		statement.validate();
		final QuRootTableRef tableRef = (QuRootTableRef) statement
				.rootRelationRef();
		final TableDefineImpl table = tableRef.getTarget();
		final QueryColumnImpl recid = statement.findColumn(tableRef,
				table.f_recid);
		if (recid == null) {
			throw Render.noRecidColumnForTable(statement, table);
		}
		this.recid = new ArgumentReserver(recid.field, table.f_recid.getType());
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = lang.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		statement.renderFrom(buffer.select(), usages);
		ISqlExprBuffer where = buffer.select().where();
		where.loadField(statement.rootRelationRef().getName(),
				FIELD_DBNAME_RECID).loadVar(this.recid).eq();
		statement.renderGroupby(buffer.select(), usages);
		statement.renderHaving(buffer.select(), usages);
		statement.renderSelect(buffer.select(), usages);
		this.build(buffer);
	}
}