package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.InvalidValueExprDomainException;

final class ExprDomainValidator extends TraversedExprVisitor<RelationRefDomain> {

	static final ExprDomainValidator INSTANCE = new ExprDomainValidator();

	private ExprDomainValidator() {
	}

	@Override
	public final void visitSelectColumnRef(SelectColumnRefImpl expr,
			RelationRefDomain domain) {
		if (expr.queryRef != domain.findRelationRefRecursively(expr.queryRef
				.getName())) {
			throw new InvalidValueExprDomainException(expr);
		}
	}

	@Override
	public void visitTableFieldRef(TableFieldRefImpl expr,
			RelationRefDomain domain) {
		if (expr.tableRef != domain.findRelationRefRecursively(expr.tableRef
				.getName())) {
			throw new InvalidValueExprDomainException(expr);
		}
	}

	@Override
	public void visitSubQueryExpr(SubQueryExpr expr, RelationRefDomain domain) {
		if (expr.subquery.domain != domain) {
			throw new InvalidValueExprDomainException(expr);
		}
		super.visitSubQueryExpr(expr, domain);
	}

	@Override
	public void visitDerivedQuery(DerivedQueryImpl query,
			RelationRefDomain domain) {
		super.visitSelect(query, query);
	}

	@Override
	public void visitSubQuery(SubQueryImpl subquery, RelationRefDomain domain) {
		super.visitSelect(subquery, subquery);
	}

	@Override
	public void visitQueryColumnRef(QueryColumnRefExpr expr,
			RelationRefDomain domain) {
		if (expr.column.owner != domain) {
			throw new IllegalArgumentException();
		}
	}
}