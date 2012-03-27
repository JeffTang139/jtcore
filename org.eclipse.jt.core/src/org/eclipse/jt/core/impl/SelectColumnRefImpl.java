package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.SelectColumnRefExpr;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.DataType;

/**
 * 查询列引用表达式
 * 
 * @author Jeff Tang
 * 
 */
final class SelectColumnRefImpl extends RelationColumnRefImpl implements
		SelectColumnRefExpr {

	@Override
	public final QueryRef getReference() {
		return this.queryRef;
	}

	@Override
	public final SelectColumnImpl<?, ?> getColumn() {
		return this.column;
	}

	public final DataType getType() {
		return this.column.getType();
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_columnref;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setString(xml_attr_queryref, this.queryRef.getName());
		element.setString(xml_attr_column, this.column.name);
	}

	@Override
	public final String toString() {
		return this.getReference().getName() + "." + this.column.getName();
	}

	@Override
	final String getDescription() {
		return "查询列引用表达式";
	}

	static final String xml_name_columnref = "query-column-ref";
	static final String xml_attr_queryref = "query-ref";
	static final String xml_attr_column = "column";

	final QueryRef queryRef;

	final SelectColumnImpl<?, ?> column;

	SelectColumnRefImpl(QueryRef reference, SelectColumnImpl<?, ?> column) {
		if (reference.getTarget() != column.owner) {
			throw new IllegalArgumentException("查询输出列定义[" + column.name
					+ "]不属于查询引用的目标查询定义[" + reference.getTarget().name + "]");
		}
		this.queryRef = reference;
		this.column = column;
	}

	SelectColumnRefImpl(SXElement element, RelationRefOwner refOwner) {
		RelationRef relationRef = refOwner.findRelationRef(element
				.getString(xml_attr_queryref));
		if (relationRef == null) {
			throw new NullPointerException();
		}
		if (!(relationRef instanceof QueryRef)) {
			throw new IllegalArgumentException();
		}
		this.queryRef = (QueryRef) relationRef;
		this.column = this.queryRef.getTarget().columns.get(element
				.getShort(xml_attr_column));
	}

	// final void formatSql(SqlBuilder sql) {
	// sql.appendId(this.queryRef.getName()).nPeriod().appendId(
	// this.column.name);
	// }

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitSelectColumnRef(this, context);
	}

	@Override
	final SelectColumnRefImpl clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		throw new UnsupportedOperationException();
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.loadField(this.queryRef.getName(), this.column.name);
	}
}
