package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.spi.sql.SQLNotSupportedException;

class SQLExprContext extends SQLVisitorContext {
	public ValueExpression valueExpr;
	public ConditionalExpression conditionalExpr;
	public SQLNameResolver resolver;
	/**
	 * �Ƿ����ʹ�þۼ�����
	 */
	public boolean canUseSetFunc;
	/**
	 * �Ƿ������ھۺϺ�����ʹ��distinct
	 */
	public boolean canUseSetFuncWithDistinct;

	private final Object subQueryOwner;

	public SQLExprContext(SQLVisitorContext root, Object subQueryOwner) {
		super(root);
		this.subQueryOwner = subQueryOwner;
	}

	public SQLExprContext(ObjectQuerier querier, boolean restrict,
			Object subQueryOwner) {
		super(querier, restrict);
		this.subQueryOwner = subQueryOwner;
	}

	public SubQueryImpl newSubQuery() {
		Object subQueryOwner = this.subQueryOwner;
		if (subQueryOwner instanceof ModifyStatementImpl) {
			return ((ModifyStatementImpl) subQueryOwner).newSubQuery();
		} else if (subQueryOwner instanceof SelectImpl<?, ?>) {
			return ((SelectImpl<?, ?>) subQueryOwner).newSubQuery();
		}
		throw new SQLNotSupportedException("ֻ֧����DML����ж����Ӳ�ѯ");
	}

	private void internalBuild(SQLVisitor<SQLExprContext> visitor,
			SQLVisitable expr, SQLNameResolver resolver) {
		SQLNameResolver oldResolver = this.resolver;
		this.resolver = resolver;
		expr.accept(this, visitor);
		this.resolver = oldResolver;
	}

	public ConditionalExpression build(SQLExprVisitor visitor,
			NConditionExpr expr) {
		expr.accept(this, visitor);
		return this.conditionalExpr;
	}

	public ConditionalExpression build(NConditionExpr expr) {
		return build(SQLExprVisitor.VISITOR, expr);
	}

	public ValueExpression build(SQLExprVisitor visitor, NValueExpr expr) {
		expr.accept(this, visitor);
		return this.valueExpr;
	}

	public ValueExpression build(NValueExpr expr) {
		return build(SQLExprVisitor.VISITOR, expr);
	}

	public ConditionalExpression build(SQLExprVisitor visitor,
			NConditionExpr expr, SQLNameResolver resolver) {
		this.internalBuild(visitor, expr, resolver);
		return this.conditionalExpr;
	}

	public ConditionalExpression build(NConditionExpr expr,
			SQLNameResolver resolver) {
		return build(SQLExprVisitor.VISITOR, expr, resolver);
	}

	public ValueExpression build(SQLExprVisitor visitor, NValueExpr expr,
			SQLNameResolver resolver) {
		this.internalBuild(visitor, expr, resolver);
		return this.valueExpr;
	}

	public ValueExpression build(NValueExpr expr, SQLNameResolver resolver) {
		return build(SQLExprVisitor.VISITOR, expr, resolver);
	}
}
