package org.eclipse.jt.core.impl;

final class QueryLimitSql extends Sql {

	final StrongRefParameter limit = new StrongRefParameter();
	final StrongRefParameter offset = new StrongRefParameter();

	QueryLimitSql(DBLang lang, QueryStatementBase statement) {
		statement.validate();
		TableUsages usages = new TableUsages();
		statement.visit(usages, null);
		ISqlQueryBuffer buffer = lang.sqlbuffers().query();
		statement.renderWiths(buffer, usages);
		statement.render(buffer.select(), usages);
		buffer.limit().loadVar(this.limit);
		buffer.offset().loadVar(this.offset);
		statement.renderOrderbys(buffer, usages);
		this.build(buffer);
	}

}