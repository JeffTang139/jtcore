package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.QuJoinedQueryRefDeclare;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 查询定义使用的连接查询引用
 * 
 * @author Jeff Tang
 * 
 */
final class QuJoinedQueryRef extends QuJoinedRelationRefImpl<DerivedQueryImpl>
		implements QuJoinedQueryRefDeclare, QuQueryRef, JoinedQueryRef {

	public final SelectColumnRefImpl expOf(RelationColumnDefine column) {
		if (column == null) {
			throw relationColumnNull();
		}
		if (column instanceof SelectColumnImpl<?, ?>) {
			return new SelectColumnRefImpl(this,
					(SelectColumnImpl<?, ?>) column);
		}
		throw notSupportedRelationColumnRefException(this, column);
	}

	public final SelectColumnRefImpl expOf(String columnName) {
		SelectColumnImpl<?, ?> column = this.target.columns.find(columnName);
		if (column == null) {
			throw notSupportedRelationColumnRefException(this, columnName);
		}
		return new SelectColumnRefImpl(this, column);
	}

	public final boolean isTableReference() {
		return false;
	}

	public final boolean isQueryReference() {
		return true;
	}

	public final QuJoinedTableRef castAsTableRef() {
		throw new ClassCastException();
	}

	public final QuJoinedQueryRef castAsQueryRef() {
		return this;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		this.target.renderInto(element.append(xml_element_query));
	}

	QuJoinedQueryRef(SelectImpl<?, ?> owner, String name,
			DerivedQueryImpl target, QuRelationRef parent) {
		super(owner, name, target, parent);
	}

	static final String xml_name = "joined-queryref";

	public final QuTableRef asTableRef() {
		throw new ClassCastException();
	}

	@Override
	protected final QuJoinedQueryRef cloneSelfTo(QuRelationRef from,
			ArgumentOwner args) {
		if (this.target.isWith) {
			DerivedQueryImpl with = from.getOwner().getWith(this.target.name);
			return from.newJoin(with, this.name);
		} else {
			DerivedQueryImpl query = from.getOwner().newDerivedQuery();
			QuJoinedQueryRef join = from.newJoin(query, this.name);
			this.target.cloneSelectTo(query, args);
			return join;
		}
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitQuJoinedQueryRef(this, context);
	}

	@Override
	final ISqlJoinedRelationRefBuffer renderSelf(ISqlRelationRefBuffer buffer,
			TableUsages usages) {
		if (this.target.isWith) {
			return buffer.joinTable(this.target.name, this.name,
					this.getJoinType());
		} else {
			ISqlJoinedQueryRefBuffer sq = buffer.joinQuery(this.name,
					this.getJoinType());
			this.target.render(sq.select(), usages);
			return sq;
		}
	}
}
