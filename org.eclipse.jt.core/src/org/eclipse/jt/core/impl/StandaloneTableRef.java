package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.query.DerivedQueryDefine;
import org.eclipse.jt.core.def.query.RelationColumnDefine;
import org.eclipse.jt.core.def.query.RelationRefDefine;
import org.eclipse.jt.core.def.table.HierarchyDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableRelationDefine;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 简单的表引用的实现类
 * 
 * <p>
 * 不支持使用连接链表
 * 
 * @author Jeff Tang
 * 
 */
abstract class StandaloneTableRef extends RelationRefImpl<TableDefineImpl>
		implements TableRef {

	public final TableFieldRefImpl expOf(RelationColumnDefine column) {
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

	StandaloneTableRef(String name, TableDefineImpl target) {
		super(name, target);
	}

	StandaloneTableRef(TableRelationDefineImpl sample, ObjectQuerier querier) {
		super(sample.name, (TableDefineImpl) querier.get(TableDefine.class,
				sample.target.name));
	}

	public final JoinedTableRef newJoin(TableDeclarator table, String name) {
		throw new UnsupportedOperationException();
	}

	public final JoinedTableRef newJoin(TableDeclarator table) {
		throw new UnsupportedOperationException();
	}

	public final JoinedTableRef newJoin(TableDefine table, String name) {
		throw new UnsupportedOperationException();
	}

	public final JoinedTableRef newJoin(TableDefine table) {
		throw new UnsupportedOperationException();
	}

	public final JoinedTableRef newJoin(TableRelationDefine sample, String name) {
		throw new UnsupportedOperationException();
	}

	public final JoinedTableRef newJoin(TableRelationDefine sample) {
		throw new UnsupportedOperationException();
	}

	public final JoinedQueryRef newJoin(DerivedQueryDefine query) {
		throw new UnsupportedOperationException();
	}

	public final JoinedQueryRef newJoin(DerivedQueryDefine query, String name) {
		throw new UnsupportedOperationException();
	}

	static final TableRef ensureHierarchyForTableRef(
			RelationRefDefine relationRef, HierarchyDefine hierarchy) {
		if (!(relationRef instanceof TableRef)) {
			throw new UnsupportedOperationException("关系引用["
					+ relationRef.getName() + "]不是表引用,不能使用级次运算.");
		}
		TableRef tableRef = (TableRef) relationRef;
		if (tableRef.getTarget() != hierarchy.getOwner()) {
			throw new IllegalArgumentException("关系引用的级次运算错误:级次定义["
					+ hierarchy.getName() + "]不属于当表引用的目标表["
					+ tableRef.getTarget().getName() + "].");
		}
		return tableRef;
	}

	public <TContext> void visit(OMVisitor<TContext> visitor, TContext context) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void render(SXElement element) {
		super.render(element);
		element.setString(xml_attr_table, this.target.name);
	}

	@Override
	void assignFrom(Object sample) {
		super.assignFrom(sample);
		StandaloneTableRef s = (StandaloneTableRef) sample;
		if (!this.target.name.equals(s.target.name)) {
			throw new UnsupportedOperationException(
					"table relation target migrated.");
		}
	}

}
