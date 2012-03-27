package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 查询定义中使用的根级表引用
 * 
 * @author Jeff Tang
 * 
 */
final class QuRootTableRef extends QuRootRelationRefImpl<TableDefineImpl>
		implements QuTableRef {

	public final TableFieldRefImpl expOf(RelationColumnDefine column) {
		if (column == null) {
			throw relationColumnNull();
		}
		if (column instanceof TableFieldDefineImpl) {
			return new TableFieldRefImpl(this, (TableFieldDefineImpl) column);
		}
		throw notSupportedRelationColumnRefException(this, column);
	}

	public final TableFieldRefImpl expOf(String relationColumnName) {
		TableFieldDefineImpl field = this.target.fields
				.find(relationColumnName);
		if (field == null) {
			throw notSupportedRelationColumnRefException(this,
					relationColumnName);
		}
		return new TableFieldRefImpl(this, field);
	}

	public final boolean isTableReference() {
		return true;
	}

	public final boolean isQueryReference() {
		return false;
	}

	public final QuRootTableRef castAsTableRef() {
		return this;
	}

	public final QuRootQueryRef castAsQueryRef() {
		throw new ClassCastException();
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "root-tableref";

	QuRootTableRef(SelectImpl<?, ?> owner, String name, TableDefineImpl target,
			QuRootRelationRef prev) {
		super(owner, name, target, prev);
	}

	public final QuTableRef asTableRef() {
		return this;
	}

	@Override
	protected final QuRootTableRef cloneSelfTo(SelectImpl<?, ?> owner,
			ArgumentOwner args) {
		return owner.newTableRef(this.name, this.target);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitQuRootTableRef(this, context);
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setString(xml_attr_table, this.target.name);
	}

	@Override
	final ISqlRelationRefBuffer renderSelf(ISqlSelectBuffer buffer,
			TableUsages usages) {
		return Render.renderTableRef(this, buffer, usages);
	}
}
