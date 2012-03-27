package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 查询定义使用的根级查询引用
 * 
 * @author Jeff Tang
 * 
 */
final class QuRootQueryRef extends QuRootRelationRefImpl<DerivedQueryImpl>
		implements QuQueryRef {

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

	public final SelectColumnRefImpl expOf(String relationColumnName) {
		SelectColumnImpl<?, ?> column = this.target.columns
				.find(relationColumnName);
		if (column == null) {
			throw notSupportedRelationColumnRefException(this,
					relationColumnName);
		}
		return new SelectColumnRefImpl(this, column);
	}

	public final boolean isTableReference() {
		return false;
	}

	public final boolean isQueryReference() {
		return true;
	}

	public final QuRootTableRef castAsTableRef() {
		throw new ClassCastException();
	}

	public final QuRootQueryRef castAsQueryRef() {
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

	static final String xml_name = "root-queryref";

	QuRootQueryRef(SelectImpl<?, ?> owner, String name,
			DerivedQueryImpl target, QuRootRelationRef prev) {
		super(owner, name, target, prev);
	}

	@Override
	protected final QuRootQueryRef cloneSelfTo(SelectImpl<?, ?> owner,
			ArgumentOwner args) {
		if (this.target.isWith) {
			DerivedQueryImpl with = owner.getWith(this.target.name);
			return owner.newQueryRef(this.name, with);
		} else {
			DerivedQueryImpl query = owner.newDerivedQuery();
			QuRootQueryRef ref = owner.newQueryRef(this.name, query);
			this.target.cloneSelectTo(query, args);
			return ref;
		}
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitQuRootQueryRef(this, context);
	}

	@Override
	final ISqlRelationRefBuffer renderSelf(ISqlSelectBuffer buffer,
			TableUsages usages) {
		if (this.target.isWith) {
			return buffer.newWithRef(this.target.name, this.name);
		} else {
			ISqlQueryRefBuffer sq = buffer.newQueryRef(this.name);
			this.target.render(sq.select(), usages);
			return sq;
		}
	}

}
