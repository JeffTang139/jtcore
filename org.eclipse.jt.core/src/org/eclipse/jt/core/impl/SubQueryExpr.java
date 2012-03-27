package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.SubQueryDefine;
import org.eclipse.jt.core.def.query.SubQueryExpression;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.DataType;

/**
 * 字查询表达式实现类
 * 
 * @author Jeff Tang
 * 
 */
final class SubQueryExpr extends ValueExpr implements SubQueryExpression {

	@Override
	final String getDescription() {
		return "子查询表达式";
	}

	public final DataType getType() {
		return this.subquery.columns.get(0).getType();
	}

	public final SubQueryDefine getSubQuery() {
		return this.subquery;
	}

	@Override
	public final String getXMLTagName() {
		return SubQueryExpr.xml_name_subquery;
	}

	@Override
	public final void render(SXElement element) {
		throw new UnsupportedOperationException();
	}

	static final String xml_name_subquery = "subquery";

	final SubQueryImpl subquery;

	SubQueryExpr(SubQueryImpl subquery) {
		this.subquery = subquery;
	}

	@Override
	final SubQueryExpr clone(RelationRefDomain domain, ArgumentOwner args) {
		SubQueryImpl target = new SubQueryImpl(domain);
		this.subquery.cloneSelectTo(target, args);
		return target.newExpression();
	}

	@Override
	final ValueExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitSubQueryExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		ISqlSelectBuffer sb = buffer.subQuery();
		this.subquery.render(sb, usages);
	}
}
