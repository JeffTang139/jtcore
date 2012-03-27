package org.eclipse.jt.core.impl;

final class QueryTopSql extends Sql {

	final StrongRefParameter top = new StrongRefParameter();

	QueryTopSql(DBLang lang, QueryStatementBase statement) {
		statement.validate();
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = lang.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		statement.render(buffer.select(), usages);
		buffer.limit().loadVar(this.top);
		statement.renderOrderbys(buffer, usages);
		this.build(buffer);
	}
}