package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.def.exp.CombinedExpression;
import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.misc.SXElement;


/**
 * 联合条件实现类
 * 
 * @author Jeff Tang
 * 
 */
final class CombinedExpr extends ConditionalExpr implements CombinedExpression {

	public final CombinedExpr not() {
		return new CombinedExpr(!this.not, this.and, this.conditions);
	}

	/**
	 * 返回描述
	 */
	@Override
	public final String getDescription() {
		return this.and ? "条件与表达式" : "条件或表达式";
	}

	public final boolean isAnd() {
		return this.and;
	}

	public final ConditionalExpression get(int index) {
		return this.conditions[index];
	}

	public final int getCount() {
		return this.conditions.length;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_combined;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setAttribute(xml_attr_name_isAnd, Boolean.toString(this.and));
		for (ConditionalExpr condition : this.conditions) {
			condition.render(element.append(condition.getXMLTagName()));
		}
	}

	static final String xml_name_combined = "combined";
	static final String xml_attr_name_isAnd = "and";

	/**
	 * 是否是and联合
	 */
	final boolean and;
	/**
	 * 表达式集合
	 */
	final ConditionalExpr[] conditions;

	CombinedExpr(boolean not, boolean and, ConditionalExpr[] conditions) {
		super(not);
		if (conditions == null) {
			throw new NullPointerException();
		}
		if (conditions.length < 2) {
			throw new IllegalArgumentException();
		}
		this.and = and;
		this.conditions = conditions;
	}

	private CombinedExpr(CombinedExpr sample, RelationRef from, RelationRef to,
			RelationRef fromSample, RelationRef toSample) {
		super(sample.not);
		this.and = sample.and;
		this.conditions = new ConditionalExpr[sample.conditions.length];
		for (int i = 0; i < sample.conditions.length; i++) {
			this.conditions[i] = sample.conditions[i].clone(fromSample, from,
					toSample, to);
		}
	}

	CombinedExpr(SXElement element, RelationRefOwner refOwner,
			ArgumentOwner args) {
		super(element);
		this.and = element.getBoolean(CombinedExpr.xml_attr_name_isAnd);
		this.conditions = ConditionalExpr.loadConditions(element.firstChild(),
				refOwner, args);
		if (this.conditions == null) {
			throw new IllegalArgumentException("xml is empty");
		}
	}

	// @Override
	// final void formatSql(SqlBuilder sql) {
	// if (this.not) {
	// sql.append("not(");
	// }
	// this.formatCondition(sql, this.conditions[0]);
	// for (int i = 1; i < this.conditions.length; i++) {
	// sql.nNewline().append(this.and ? "and" : "or").nSpace();
	// this.formatCondition(sql, this.conditions[i]);
	// }
	// if (this.not) {
	// sql.append(')');
	// }
	// }

	// @Deprecated
	// private final void formatCondition(SqlBuilder sql, ConditionalExpr
	// condition) {
	// if (this.and && condition instanceof CombinedExpr && !condition.not) {
	// CombinedExpr combined = (CombinedExpr) condition;
	// if (!combined.and) {
	// sql.lp();
	// condition.formatSql(sql);
	// sql.rp();
	// return;
	// }
	// }
	// condition.formatSql(sql);
	// }

	@Override
	final ConditionalExpr clone(RelationRefDomain domain, ArgumentOwner args) {
		ConditionalExpr[] conditions = new ConditionalExpr[this.conditions.length];
		for (int i = 0; i < this.conditions.length; i++) {
			conditions[i] = this.conditions[i].clone(domain, args);
		}
		return new CombinedExpr(this.not, this.and, conditions);
	}

	@Override
	final CombinedExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		return new CombinedExpr(this, from, to, fromSample, toSample);
	}

	@Override
	final void concatCondition(boolean and,
			ArrayList<ConditionalExpr> conditions) {
		if (this.and == and && !this.not) {
			for (ConditionalExpr condition : this.conditions) {
				conditions.add(condition);
			}
		} else {
			conditions.add(this);
		}
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitCombinedExpr(this, context);
	}

	@Override
	final void fillEqualsRelationColumnRef(RelationRef relaitonRef,
			RelationColumn relationColumn,
			ArrayList<RelationColumnRefImpl> resultList) {
		for (int i = 0; i < this.conditions.length; i++) {
			this.conditions[i].fillEqualsRelationColumnRef(relaitonRef,
					relationColumn, resultList);
		}
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		final ConditionalExpr[] cs = this.conditions;
		for (int i = 0; i < cs.length; i++) {
			cs[i].render(buffer, usages);
		}
		if (this.and) {
			buffer.and(cs.length);
		} else {
			buffer.or(cs.length);
		}
		if (this.not) {
			buffer.not();
		}
	}

}
