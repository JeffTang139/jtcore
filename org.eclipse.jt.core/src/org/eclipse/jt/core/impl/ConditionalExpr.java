package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.misc.SXElement;


/**
 * �������ʽʵ����
 * 
 * <p>
 * ��ָ���ʽ������Ϊ�߼�ֵ�ı��ʽ
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
	 * �������ʽ�Ƿ�ȡ��
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
		throw new UnsupportedOperationException("��֧�ֵ�tagName[" + tagName + "]");
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
	 * ��¡ֵ���ʽ
	 * 
	 * @param domain
	 *            ���ʽ���ڵĹ�ϵ��(�Ӹ�����ҹ�ϵ���ü������ѯģ��)
	 * @param args
	 *            ��������(�Ӹò������Ҳ�������)
	 * @return
	 */
	abstract ConditionalExpr clone(RelationRefDomain domain, ArgumentOwner args);

	/**
	 * ���Ƶ�ǰ�������ʽ
	 * 
	 * <p>
	 * �����ڴ�ʹ��TableRelation��������ʱʹ��
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
			throw new NullPointerException("�����������ʽΪ��");
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
	 * �ռ�����,and & or �������ʽ�ݴ��Ż�
	 * 
	 * @param conditions
	 */
	void concatCondition(boolean and, ArrayList<ConditionalExpr> conditions) {
		conditions.add(this);
	}

	/**
	 * �Ƿ��ֵν�ʱ��ʽ
	 */
	protected boolean isEqualsPredicate() {
		return false;
	}

	/**
	 * �����ڵ�ǰ���ʽ��,��ָ���ֶ����õ�ֵ���ֶ����ñ��ʽ,�������б�
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
