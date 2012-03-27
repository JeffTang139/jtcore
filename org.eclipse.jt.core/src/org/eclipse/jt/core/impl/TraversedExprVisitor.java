package org.eclipse.jt.core.impl;

/**
 * 非终结表达式对象以标准遍历行为实现,终结表达式对象访问行为为空
 * 
 * @author Jeff Tang
 * 
 * @param <TContext>
 */
abstract class TraversedExprVisitor<TContext> extends ExprVisitor<TContext> {

	public void visitCombinedExpr(CombinedExpr expr, TContext context) {
		for (int i = 0; i < expr.conditions.length; i++) {
			expr.conditions[i].visit(this, context);
		}
	}

	public void visitHierarchyOperateExpr(HierarchyOperateExpr expr,
			TContext context) {
		expr.level.visit(this, context);
	}

	public void visitHierarchyPredicateExpr(HierarchyPredicateExpr expr,
			TContext context) {
		if (expr.level != null) {
			expr.level.visit(this, context);
		}
	}

	public void visitOperateExpr(OperateExpr expr, TContext context) {
		for (int i = 0; i < expr.values.length; i++) {
			expr.values[i].visit(this, context);
		}
	}

	public void visitPredicateExpr(PredicateExpr expr, TContext context) {
		for (int i = 0; i < expr.values.length; i++) {
			expr.values[i].visit(this, context);
		}
	}

	public void visitSearchedCase(SearchedCaseExpr expr, TContext context) {
		expr.whenCondition.visit(this, context);
		expr.returnValue.visit(this, context);
		if (expr.otherWhens != null) {
			for (int i = 0; i < expr.otherWhens.length; i++) {
				expr.otherWhens[i].visit(this, context);
				expr.otherReturns[i].visit(this, context);
			}
		}
		if (expr.defaultValue != null) {
			expr.defaultValue.visit(this, context);
		}
	}

	public void visitSubQueryExpr(SubQueryExpr expr, TContext context) {
		this.visitSubQuery(expr.subquery, context);
	}

	public void visitArgumentRefExpr(ArgumentRefExpr expr, TContext context) {
	}

	public void visitBooleanExpr(BooleanConstExpr value, TContext context) {
	}

	public void visitByteExpr(ByteConstExpr value, TContext context) {
	}

	public void visitBytesExpr(BytesConstExpr value, TContext context) {
	}

	public void visitDateExpr(DateConstExpr value, TContext context) {
	}

	public void visitDoubleExpr(DoubleConstExpr value, TContext context) {
	}

	public void visitFloatExpr(FloatConstExpr value, TContext context) {
	}

	public void visitGUIDExor(GUIDConstExpr value, TContext context) {
	}

	public void visitIntExpr(IntConstExpr value, TContext context) {
	}

	public void visitLongExpr(LongConstExpr value, TContext context) {
	}

	public void visitNullExpr(NullExpr expr, TContext context) {
	}

	public void visitSelectColumnRef(SelectColumnRefImpl expr, TContext context) {
	}

	public void visitShortExpr(ShortConstExpr value, TContext context) {
	}

	public void visitStringExpr(StringConstExpr value, TContext context) {
	}

	public void visitTableFieldRef(TableFieldRefImpl expr, TContext context) {
	}

	public void visitQueryColumnRef(QueryColumnRefExpr expr, TContext context) {
	}

}
