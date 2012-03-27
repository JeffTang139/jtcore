package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

final class QueryColumnRefExpr extends ValueExpr {

	public final DataType getType() {
		return this.column.getType();
	}

	@Override
	final ValueExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	@Override
	final ValueExpr clone(RelationRefDomain domain, ArgumentOwner args) {
		QueryStatementBase query = (QueryStatementBase) domain;
		return new QueryColumnRefExpr(query.getColumn(this.column.name));
	}

	@Override
	final String getDescription() {
		return "查询语句列引用";
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "querycolumn-ref";

	final QueryColumnImpl column;

	QueryColumnRefExpr(QueryColumnImpl column) {
		this.column = column;
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		throw new UnsupportedOperationException();
	}

	public <TContext> void visit(OMVisitor<TContext> visitor, TContext context) {
		visitor.visitQueryColumnRef(this, context);
	}

}
