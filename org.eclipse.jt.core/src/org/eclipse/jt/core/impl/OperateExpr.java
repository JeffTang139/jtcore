package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.OperateExpression;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.DataType;

/**
 * ������ʽʵ����
 * 
 * @author Jeff Tang
 * 
 */
public class OperateExpr extends ValueExpr implements OperateExpression {

	@Override
	public final String getDescription() {
		return "������ʽ";
	}

	public final ValueExpr get(int index) {
		return this.values[index];
	}

	public final int getCount() {
		return this.values.length;
	}

	public final OperatorImpl getOperator() {
		return this.operator;
	}

	public final DataType getType() {
		return this.type;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_operate;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setAttribute(xml_attr_operator, this.operator.toString());
		for (ValueExpr value : this.values) {
			value.renderInto(element);
		}
	}

	public static final OperateExpr COUNT_ASTERISK = new OperateExpr(
			OperatorImpl.COUNT_ASTERISK);

	public static final OperateExpr GET_DATE = new OperateExpr(
			OperatorImpl.GETDATE);

	public static final OperateExpr NEW_RECID = new OperateExpr(
			OperatorImpl.NEW_RECID);

	static final String xml_name_operate = "operate";
	static final String xml_attr_operator = "operator";

	/**
	 * �����
	 */
	final OperatorImpl operator;

	/**
	 * ����������
	 */
	final DataType type;

	/**
	 * ��������б�
	 */
	final ValueExpr[] values;

	/**
	 * ����������ʽ
	 * 
	 * @param operator
	 *            �����
	 * @param values
	 *            ������
	 */
	OperateExpr(OperatorImpl operator, ValueExpr[] values) {
		this.operator = operator;
		this.values = values;
		this.type = operator.checkValues(values);
	}

	/**
	 * ����������Ϊ�յ�������ʽ
	 */
	private OperateExpr(OperatorImpl operator) {
		this(operator, ValueExpr.emptyArray);
	}

	public OperateExpr(OperatorImpl operator, ValueExpr expr) {
		this(operator, new ValueExpr[] { expr });
	}

	public OperateExpr(OperatorImpl operator, ValueExpr expr1, ValueExpr expr2) {
		this(operator, new ValueExpr[] { expr1, expr2 });
	}

	public OperateExpr(OperatorImpl operator, ValueExpr expr1, ValueExpr expr2,
			ValueExpr expr3) {
		this(operator, new ValueExpr[] { expr1, expr2, expr3 });
	}

	OperateExpr(SXElement element, RelationRefOwner refOwner, ArgumentOwner args) {
		this(element.getEnum(OperatorImpl.class, xml_attr_operator), ValueExpr
				.loadValues(element.firstChild(), refOwner, args));
	}

	@Override
	final OperateExpr clone(RelationRefDomain domain, ArgumentOwner args) {
		ValueExpr[] values = new ValueExpr[this.values.length];
		for (int i = 0; i < this.values.length; i++) {
			values[i] = this.values[i].clone(domain, args);
		}
		return new OperateExpr(this.operator, values);
	}

	@Override
	final OperateExpr clone(RelationRef fromSample, RelationRef from,
			RelationRef toSample, RelationRef to) {
		ValueExpr[] values = new ValueExpr[this.values.length];
		for (int i = 0; i < this.values.length; i++) {
			values[i] = this.values[i].clone(fromSample, from, toSample, to);
		}
		return new OperateExpr(this.operator, values);
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitOperateExpr(this, context);
	}

	/**
	 * �������Ƿ�ȷ��
	 * 
	 * <p>
	 * new_recid, getdate��������Ϊ��ȷ��.
	 * 
	 * @return
	 */
	final boolean isNonDeterministic() {
		return this.operator.isNonDeterministic();
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		for (int i = 0; i < this.values.length; i++) {
			this.values[i].render(buffer, usages);
		}
		this.operator.render(buffer, this);
	}
}
