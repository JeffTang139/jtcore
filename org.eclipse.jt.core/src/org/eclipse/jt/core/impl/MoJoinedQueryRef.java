package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDefine;

/**
 * 更新语句使用的连接查询引用
 * 
 * @author Jeff Tang
 * 
 */
final class MoJoinedQueryRef extends MoJoinedRelationRefImpl<DerivedQueryImpl>
		implements JoinedQueryRef {

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

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "joined-queryref";

	MoJoinedQueryRef(ModifyStatementImpl statement, String name,
			DerivedQueryImpl target) {
		super(statement, name, target);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitMoJoinedQueryRef(this, context);
	}

	@Override
	protected final ISqlJoinedRelationRefBuffer renderSelf(
			ISqlRelationRefBuffer buffer, TableUsages usages) {
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
