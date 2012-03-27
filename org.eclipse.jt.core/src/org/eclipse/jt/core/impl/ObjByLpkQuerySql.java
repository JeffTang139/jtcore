package org.eclipse.jt.core.impl;

final class ObjByLpkQuerySql extends Sql {

	ObjByLpkQuerySql(DBLang lang, MappingQueryStatementImpl statement) {
		statement.validate();
		statement.validateSingleRoot();
		final QuRootTableRef tableRef = (QuRootTableRef) statement
				.rootRelationRef();
		final TableDefineImpl table = tableRef.getTarget();
		table.checkLogicalKeyAvaiable();
		final int c = table.logicalKey.items.size();
		final TableFieldDefineImpl[] keys = new TableFieldDefineImpl[c];
		final ArgumentReserver[] args = new ArgumentReserver[c];
		statement.setRootKeys(keys, args);
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = lang.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		statement.renderFrom(buffer.select(), usages);
		MappingQueryStatementImpl.fillLpkWhere(buffer.select().where(),
				statement.rootRelationRef().getName(), keys, args);
		statement.renderGroupby(buffer.select(), usages);
		statement.renderHaving(buffer.select(), usages);
		statement.renderSelect(buffer.select(), usages);
		this.build(buffer);
	}
}