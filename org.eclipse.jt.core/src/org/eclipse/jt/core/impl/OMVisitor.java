package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.GroupByType;
import org.eclipse.jt.core.impl.UpdateStatementImpl.FieldAssign;

/**
 * 对象模型访问器
 * 
 * @author Jeff Tang
 * 
 */
interface OMVisitor<TContext> {

	void visitBooleanExpr(BooleanConstExpr value, TContext context);

	void visitByteExpr(ByteConstExpr value, TContext context);

	void visitBytesExpr(BytesConstExpr value, TContext context);

	void visitDateExpr(DateConstExpr value, TContext context);

	void visitDoubleExpr(DoubleConstExpr value, TContext context);

	void visitFloatExpr(FloatConstExpr value, TContext context);

	void visitGUIDExor(GUIDConstExpr value, TContext context);

	void visitIntExpr(IntConstExpr value, TContext context);

	void visitLongExpr(LongConstExpr value, TContext context);

	void visitShortExpr(ShortConstExpr value, TContext context);

	void visitStringExpr(StringConstExpr value, TContext context);

	void visitArgumentRefExpr(ArgumentRefExpr expr, TContext context);

	void visitHierarchyOperateExpr(HierarchyOperateExpr expr, TContext context);

	void visitNullExpr(NullExpr expr, TContext context);

	void visitOperateExpr(OperateExpr expr, TContext context);

	void visitSelectColumnRef(SelectColumnRefImpl expr, TContext context);

	void visitQueryColumnRef(QueryColumnRefExpr expr, TContext context);

	void visitTableFieldRef(TableFieldRefImpl expr, TContext context);

	void visitSearchedCase(SearchedCaseExpr expr, TContext context);

	void visitSubQueryExpr(SubQueryExpr expr, TContext context);

	void visitCombinedExpr(CombinedExpr expr, TContext context);

	void visitHierarchyPredicateExpr(HierarchyPredicateExpr expr,
			TContext context);

	void visitPredicateExpr(PredicateExpr expr, TContext context);

	void visitQueyrStatement(QueryStatementBase query, TContext context);

	void visitWith(DerivedQueryImpl with, TContext context);

	void visitOrderby(OrderByItemImpl orderby, TContext context);

	void visitDerivedQuery(DerivedQueryImpl query, TContext context);

	void visitSubQuery(SubQueryImpl subquery, TContext context);

	void visitSelect(SelectImpl<?, ?> select, TContext context);

	void visitQuRootTableRef(QuRootTableRef tableRef, TContext context);

	void visitQuRootQueryRef(QuRootQueryRef queryRef, TContext context);

	void visitQuJoinedTableRef(QuJoinedTableRef tableRef, TContext context);

	void visitQuJoinedQueryRef(QuJoinedQueryRef queryRef, TContext context);

	void visitSelectWhere(ConditionalExpr where, TContext context);

	void visitSelectGroupbyType(GroupByType type, TContext context);

	void visitSelectGroupby(GroupByItemImpl groupby, TContext context);

	void visitSelectHaving(ConditionalExpr having, TContext context);

	void visitSelectColumn(SelectColumnImpl<?, ?> column, TContext context);

	void visitSetOperate(SetOperateImpl set, TContext context);

	void visitMoRootTableRef(MoRootTableRef tableRef, TContext context);

	void visitMoJoinedTableRef(MoJoinedTableRef tableRef, TContext context);

	void visitMoJoinedQueryRef(MoJoinedQueryRef queryRef, TContext context);

	void visitInsertStatement(InsertStatementImpl insert, TContext context);

	void visitInsertValues(DerivedQueryImpl values, TContext context);

	void visitDeleteStatement(DeleteStatementImpl delete, TContext context);

	void visitDeleteWhere(ConditionalExpr where, TContext context);

	void visitUpdateStatement(UpdateStatementImpl update, TContext context);

	void visitUpdateAssign(FieldAssign assign, TContext context);

	void visitUpdateWhere(ConditionalExpr where, TContext context);
}
