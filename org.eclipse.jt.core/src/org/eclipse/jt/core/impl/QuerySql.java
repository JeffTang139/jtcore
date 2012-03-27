package org.eclipse.jt.core.impl;

final class QuerySql extends Sql {

	QuerySql(DBLang lang, QueryStatementBase statement) {
		statement.validate();
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = lang.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		statement.render(buffer.select(), usages);
		statement.renderOrderbys(buffer, usages);
		this.build(buffer);
	}
}