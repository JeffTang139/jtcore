package org.eclipse.jt.core.impl;

final class QueryRowCountSql extends Sql {

	QueryRowCountSql(DBLang lang, QueryStatementBase statement) {
		statement.validate();
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = lang.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		ISqlSelectBuffer select = buffer.select().newQueryRef("N").select();
		if (statement.distinct || statement.sets != null) {
			statement.render(select, usages);
		} else {
			statement.renderFrom(select, usages);
			statement.renderWhere(select, usages);
			statement.renderGroupby(select, usages);
			statement.renderHaving(select, usages);
			select.newColumn("MO").load(1);
			statement.renderUnion(select, usages);
		}
		buffer.select().newColumn("C").func(SqlFunction.COUNT, 0);
		this.build(buffer);
	}
}