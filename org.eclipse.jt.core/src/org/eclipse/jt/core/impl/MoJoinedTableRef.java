package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDefine;

/**
 * �������ʹ�õ����ӱ�����
 * 
 * @author Jeff Tang
 */
final class MoJoinedTableRef extends MoJoinedRelationRefImpl<TableDefineImpl>
		implements JoinedTableRef {

	public final TableFieldRefImpl expOf(RelationColumnDefine column) {
		if (column == null) {
			throw relationColumnNull();
		}
		if (column instanceof TableFieldDefineImpl) {
			return new TableFieldRefImpl(this, (TableFieldDefineImpl) column);
		}
		throw notSupportedRelationColumnRefException(this, column);
	}

	public final TableFieldRefImpl expOf(String name) {
		TableFieldDefineImpl field = this.target.fields.find(name);
		if (field == null) {
			throw new IllegalArgumentException("ָ�����ƵĹ�ϵ�ж��岻����.");
		}
		return new TableFieldRefImpl(this, field);
	}

	public final boolean isTableReference() {
		return true;
	}

	public final boolean isQueryReference() {
		return false;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name;
	}

	static final String xml_name = "joined-tableref";

	MoJoinedTableRef(ModifyStatementImpl statement, String name,
			TableDefineImpl target) {
		super(statement, name, target);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitMoJoinedTableRef(this, context);
	}

	@Override
	protected final ISqlJoinedTableRefBuffer renderSelf(
			ISqlRelationRefBuffer buffer, TableUsages usages) {
		return Render.renderJoinedTableRef(this, buffer, usages);
	}

}
