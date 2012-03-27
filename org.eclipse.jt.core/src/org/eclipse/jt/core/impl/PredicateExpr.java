package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.def.exp.PredicateExpression;
import org.eclipse.jt.core.misc.SXElement;


/**
 * 谓词表达式实现类
 * 
 * @author Jeff Tang
 * 
 */
final class PredicateExpr extends ConditionalExpr implements
		PredicateExpression {

	@Override
	final String getDescription() {
		return "谓词表达式";
	}

	public final ValueExpr get(int index) {
		return this.values[index];
	}

	public final int getCount() {
		return this.values.length;
	}

	public final PredicateImpl getPredicate() {
		return this.predicate;
	}

	public final PredicateExpr not() {
		return new PredicateExpr(this, true);
	}

	@Override
	public final String getXMLTagName() {
		return PredicateExpr.xml_name_prediacte;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setAttribute(PredicateExpr.xml_attr_predicate_type,
				this.predicate.toString());
		for (ValueExpr value : this.values) {
			value.render(element.append(value.getXMLTagName()));
		}
	}

	static final String xml_name_prediacte = "predicate";
	static final String xml_attr_predicate_type = "type";

	/**
	 * 参数列表
	 */
	final ValueExpr[] values;

	/**
	 * 谓词
	 */
	final PredicateImpl predicate;

	/**
	 * 只提供not使用的构造方法
	 */
	private PredicateExpr(PredicateExpr source, boolean not) {
		super(source.not ^ not);
		this.values = source.values;
		this.predicate = source.predicate;
		this.predicate.checkValues(this.values);
	}

	PredicateExpr(boolean not, PredicateImpl predicate, ValueExpr[] values) {
		super(not);
		if (predicate == null) {
			throw new NullPointerException();
		}
		this.predicate = predicate;
		this.values = values;
		predicate.checkValues(values);
	}

	PredicateExpr(boolean not, PredicateImpl predicate, ValueExpr expr) {
		this(not, predicate, new ValueExpr[] { expr });
	}

	PredicateExpr(boolean not, PredicateImpl predicate, ValueExpr expr1,
			ValueExpr expr2) {
		this(not, predicate, new ValueExpr[] { expr1, expr2 });
	}

	PredicateExpr(boolean not, PredicateImpl predicate, ValueExpr expr1,
			ValueExpr expr2, ValueExpr expr3) {
		this(not, predicate, new ValueExpr[] { expr1, expr2, expr3 });
	}

	PredicateExpr(SXElement element, RelationRefOwner refOwner,
			ArgumentOwner args) {
		super(element);
		this.predicate = element.getEnum(PredicateImpl.class,
				xml_attr_predicate_type);
		this.values = ValueExpr
				.loadValues(element.firstChild(), refOwner, args);
		this.predicate.checkValues(this.values);
	}

	@Override
	final ConditionalExpr clone(RelationRefDomain domain, ArgumentOwner args) {
		ValueExpr[] exprs = new ValueExpr[this.values.length];
		for (int i = 0; i < this.values.length; i++) {
			exprs[i] = this.values[i].clone(domain, args);
		}
		return new PredicateExpr(this.not, this.predicate, exprs);
	}

	@Override
	final PredicateExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		ValueExpr[] exps = new ValueExpr[this.values.length];
		for (int i = 0; i < this.values.length; i++) {
			exps[i] = this.values[i].clone(fromSample, from, toSample, to);
		}
		return new PredicateExpr(this.not, this.predicate, exps);
	}

	@Override
	final void fillEqualsRelationColumnRef(RelationRef relaitonRef,
			RelationColumn relationColumn,
			ArrayList<RelationColumnRefImpl> resultList) {
		if (this.isEqualsPredicate()
				&& this.values[0] instanceof TableFieldRefImpl
				&& this.values[1] instanceof TableFieldRefImpl) {
			TableFieldRefImpl fieldRef0 = (TableFieldRefImpl) this.values[0];
			TableFieldRefImpl fieldRef1 = (TableFieldRefImpl) this.values[1];
			if (fieldRef0.tableRef == relaitonRef
					&& fieldRef0.field == relationColumn) {
				if (!resultList.contains(fieldRef1)) {
					resultList.add(fieldRef1);
				}
			} else if (fieldRef1.tableRef == relaitonRef
					&& fieldRef1.field == relationColumn) {
				if (!resultList.contains(fieldRef0)) {
					resultList.add(fieldRef0);
				}
			}
		}
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitPredicateExpr(this, context);
	}

	@Override
	protected final boolean isEqualsPredicate() {
		return !this.not && this.predicate == PredicateImpl.EQUAL_TO;
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		this.predicate.render(buffer, this, usages);
	}

}
