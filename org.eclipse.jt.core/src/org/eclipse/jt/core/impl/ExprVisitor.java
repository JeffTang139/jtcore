package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.GroupByType;
import org.eclipse.jt.core.impl.UpdateStatementImpl.FieldAssign;

/**
 * 所有非表达式对象的以标准遍历行为实现
 * 
 * @author Jeff Tang
 * 
 * @param <TContext>
 */
abstract class ExprVisitor<TContext> implements OMVisitor<TContext> {

	public void visitSelect(SelectImpl<?, ?> select, TContext context) {
		if (select.rootRelationRef() != null) {
			for (QuRelationRef relationRef : select.rootRelationRef()) {
				relationRef.visit(this, context);
			}
		}
		if (select.where != null) {
			this.visitSelectWhere(select.where, context);
		}
		if (select.groupbys != null && select.groupbys.size() > 0) {
			for (int i = 0, c = select.groupbys.size(); i < c; i++) {
				this.visitSelectGroupby(select.groupbys.get(i), context);
			}
		}
		if (select.having != null) {
			this.visitSelectHaving(select.having, context);
		}
		for (int i = 0, c = select.columns.size(); i < c; i++) {
			this.visitSelectColumn(select.columns.get(i), context);
		}
		if (select.sets != null) {
			for (int i = 0, c = select.sets.size(); i < c; i++) {
				this.visitSetOperate(select.sets.get(i), context);
			}
		}
	}

	public void visitWith(DerivedQueryImpl with, TContext context) {
		this.visitSelect(with, context);
	}

	public void visitOrderby(OrderByItemImpl orderby, TContext context) {
		orderby.value.visit(this, context);
	}

	public void visitQueyrStatement(QueryStatementBase query, TContext context) {
		if (query.withs != null && query.withs.size() > 0) {
			for (int i = 0, c = query.withs.size(); i < c; i++) {
				this.visitWith(query.withs.get(i), context);
			}
		}
		this.visitSelect(query, context);
		if (query.orderbys != null && query.orderbys.size() > 0) {
			for (int i = 0, c = query.orderbys.size(); i < c; i++) {
				this.visitOrderby(query.orderbys.get(i), context);
			}
		}
	}

	public void visitDerivedQuery(DerivedQueryImpl query, TContext context) {
		this.visitSelect(query, context);
	}

	public void visitSubQuery(SubQueryImpl subquery, TContext context) {
		this.visitSelect(subquery, context);
	}

	public void visitQuRootTableRef(QuRootTableRef tableRef, TContext context) {

	}

	public void visitQuRootQueryRef(QuRootQueryRef queryRef, TContext context) {
		this.visitDerivedQuery(queryRef.getTarget(), context);
	}

	public void visitQuJoinedTableRef(QuJoinedTableRef tableRef,
			TContext context) {
		tableRef.condition.visit(this, context);
	}

	public void visitQuJoinedQueryRef(QuJoinedQueryRef queryRef,
			TContext context) {
		this.visitDerivedQuery(queryRef.getTarget(), context);
		queryRef.condition.visit(this, context);
	}

	public void visitSelectWhere(ConditionalExpr where, TContext context) {
		where.visit(this, context);
	}

	public void visitSelectGroupbyType(GroupByType type, TContext context) {
	}

	public void visitSelectGroupby(GroupByItemImpl groupby, TContext context) {
		groupby.value().visit(this, context);
	}

	public void visitSelectHaving(ConditionalExpr having, TContext context) {
		having.visit(this, context);
	}

	public void visitSelectColumn(SelectColumnImpl<?, ?> column,
			TContext context) {
		column.value().visit(this, context);
	}

	public void visitSetOperate(SetOperateImpl set, TContext context) {
		set.target.visit(this, context);
	}

	public void visitMoRootTableRef(MoRootTableRef tableRef, TContext context) {
	}

	public void visitMoJoinedTableRef(MoJoinedTableRef tableRef,
			TContext context) {
		tableRef.getJoinCondition().visit(this, context);
	}

	public void visitMoJoinedQueryRef(MoJoinedQueryRef queryRef,
			TContext context) {
		queryRef.getJoinCondition().visit(this, context);
		queryRef.target.visit(this, context);
	}

	public void visitInsertStatement(InsertStatementImpl insert,
			TContext context) {
		this.visitMoRootTableRef(insert.moTableRef, context);
		this.visitInsertValues(insert.values, context);
	}

	public void visitInsertValues(DerivedQueryImpl values, TContext context) {
		values.visit(this, context);
	}

	public void visitDeleteStatement(DeleteStatementImpl delete,
			TContext context) {
		for (MoRelationRef relationRef : delete.moTableRef) {
			relationRef.visit(this, context);
		}
		if (delete.condition != null) {
			this.visitDeleteWhere(delete.condition, context);
		}
	}

	public void visitDeleteWhere(ConditionalExpr where, TContext context) {
		where.visit(this, context);
	}

	public void visitUpdateStatement(UpdateStatementImpl update,
			TContext context) {
		for (MoRelationRef relationRef : update.moTableRef) {
			relationRef.visit(this, context);
		}
		for (int i = 0; i < update.assigns.size(); i++) {
			this.visitUpdateAssign(update.assigns.get(i), context);
		}
		if (update.condition != null) {
			this.visitDeleteWhere(update.condition, context);
		}
	}

	public void visitUpdateAssign(FieldAssign assign, TContext context) {
		assign.value().visit(this, context);
	}

	public void visitUpdateWhere(ConditionalExpr where, TContext context) {
		where.visit(this, context);
	}
}
