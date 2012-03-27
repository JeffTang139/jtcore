package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.RelationColumnDefine;

/**
 * 更新语句的目标表引用
 * 
 * @author Jeff Tang
 * 
 */
final class MoRootTableRef extends
		MoRelationRefImpl<TableDefineImpl, MoRelationRef, MoRelationRef>
		implements TableRef {

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
			throw new IllegalArgumentException("指定名称的关系列定义不存在.");
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

	static final String xml_name = "root-tableref";

	MoRootTableRef(ModifyStatementImpl owner, String name,
			TableDefineImpl target) {
		super(owner, name, target);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitMoRootTableRef(this, context);
	}

	final void render(ISqlTableRefBuffer buffer, TableUsages usages) {
		MoJoinedRelationRef join = this.getJoins();
		if (join != null) {
			join.render(buffer, usages);
		}
	}

}
