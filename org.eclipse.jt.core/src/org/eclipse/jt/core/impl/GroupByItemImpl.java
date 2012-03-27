package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.GroupByItemDeclare;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 分组查询实现类
 * 
 * @author Jeff Tang
 * 
 */
public final class GroupByItemImpl extends DefineBaseImpl implements
		GroupByItemDeclare {

	public final void setExpression(ValueExpression value) {
		this.checkModifiable();
		if (value == null) {
			throw new NullArgumentException("分组表达式");
		}
		ValueExpr v = (ValueExpr) value;
		if (SystemVariables.VALIDATE_EXPR_DOMAIN) {
			v.validateDomain(this.owner);
		}
		this.value = v;
	}

	public final ValueExpr getExpression() {
		return this.value;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_groupby;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		this.value.renderInto(element);
	}

	final static String xml_name_groupby = "groupby";

	final SelectImpl<?, ?> owner;

	private ValueExpr value;

	final ValueExpr value() {
		return this.value;
	}

	GroupByItemImpl(SelectImpl<?, ?> owner, ValueExpr value) {
		super();
		if (owner == null || value == null) {
			throw new NullPointerException();
		}
		this.owner = owner;
		// value域在构造前检查
		this.value = value;
	}

	final void cloneTo(SelectImpl<?, ?> owner, ArgumentOwner args) {
		owner.newGroupBy(this.value.clone(owner, args));
	}

}
