package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.misc.SXElement;


/**
 * 条件表达式实现类
 * 
 * <p>
 * 特指表达式运算结果为逻辑值的表达式
 * 
 * @author Jeff Tang
 * 
 */
abstract class ConditionalExpr extends MetaBase implements
		ConditionalExpression, OMVisitable {

	public final boolean isNot() {
		return this.not;
	}

	public final ConditionalExpr and(ConditionalExpression one,
			ConditionalExpression... others) {
		return new CombinedExpr(false, true, this.concatCondition(true, one,
				others));
	}

	public final ConditionalExpr or(ConditionalExpression one,
			ConditionalExpression... others) {
		return new CombinedExpr(false, false, this.concatCondition(false, one,
				others));
	}

	public final ValueExpr searchedCase(Object returnValue, Object... others) {
		return SearchedCaseExpr.newSearchedCase(this, returnValue, others);
	}

	@Override
	public void render(SXElement element) {
		super.render(element);
		element.setBoolean(xml_attr_not, this.not);
	}

	static final String xml_attr_not = "not";

	/**
	 * 条件表达式是否取反
	 */
	final boolean not;

	ConditionalExpr(boolean not) {
		this.not = not;
	}

	ConditionalExpr(SXElement element) {
		super(element);
		this.not = element.getBoolean(ConditionalExpr.xml_attr_not);
	}

	static final ConditionalExpr loadCondition(SXElement element,
			RelationRefOwner refOwner, ArgumentOwner args) {
		String tagName = element.name;
		if (tagName.equals(PredicateExpr.xml_name_prediacte)) {
			return new PredicateExpr(element, refOwner, args);
		} else if (tagName.equals(CombinedExpr.xml_name_combined)) {
			return new CombinedExpr(element, refOwner, args);
		}
		throw new UnsupportedOperationException("不支持的tagName[" + tagName + "]");
	}

	static final ConditionalExpr[] loadConditions(SXElement first,
			RelationRefOwner refOwner, ArgumentOwner args) {
		ArrayList<ConditionalExpr> conditions = null;
		for (; first != null; first = first.nextSibling()) {
			if (conditions == null) {
				conditions = new ArrayList<ConditionalExpr>();
			}
			conditions.add(loadCondition(first, refOwner, args));
		}
		return conditions != null ? conditions
				.toArray(new ConditionalExpr[conditions.size()]) : null;
	}

	/**
	 * 克隆值表达式
	 * 
	 * @param domain
	 *            表达式所在的关系域(从该域查找关系引用及构造查询模型)
	 * @param args
	 *            参数容器(从该参数查找参数定义)
	 * @return
	 */
	abstract ConditionalExpr clone(RelationRefDomain domain, ArgumentOwner args);

	/**
	 * 复制当前条件表达式
	 * 
	 * <p>
	 * 仅用于从使用TableRelation创造连接时使用
	 * 
	 * @param fromSample
	 * @param from
	 * @param toSample
	 * @param to
	 * 
	 * @return
	 */
	abstract ConditionalExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to);

	private ConditionalExpr[] concatCondition(boolean and,
			ConditionalExpression one, ConditionalExpression[] others) {
		if (one == null) {
			throw new NullPointerException("新增条件表达式为空");
		}
		ArrayList<ConditionalExpr> conditions = new ArrayList<ConditionalExpr>();
		this.concatCondition(and, conditions);
		((ConditionalExpr) one).concatCondition(and, conditions);
		if (others != null && others.length > 0) {
			for (int i = 0; i < others.length; i++) {
				((ConditionalExpr) others[i]).concatCondition(and, conditions);
			}
		}
		return conditions.toArray(new ConditionalExpr[conditions.size()]);
	}

	/**
	 * 收集条件,and & or 条件表达式据此优化
	 * 
	 * @param conditions
	 */
	void concatCondition(boolean and, ArrayList<ConditionalExpr> conditions) {
		conditions.add(this);
	}

	/**
	 * 是否等值谓词表达式
	 */
	protected boolean isEqualsPredicate() {
		return false;
	}

	/**
	 * 查找在当前表达式中,和指定字段引用等值的字段引用表达式,放入结果列表
	 * 
	 * @param relaitonRef
	 * @param column
	 * @param result
	 */
	abstract void fillEqualsRelationColumnRef(RelationRef relaitonRef,
			RelationColumn column, ArrayList<RelationColumnRefImpl> result);

	abstract void render(ISqlExprBuffer buffer, TableUsages usages);

	final void validateDomain(RelationRefDomain domain) {
		this.visit(ExprDomainValidator.INSTANCE, domain);
	}
}
