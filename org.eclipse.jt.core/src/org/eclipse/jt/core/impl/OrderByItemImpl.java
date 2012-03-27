package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.OrderByItemDeclare;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 排序规则实现类
 * 
 * @author Jeff Tang
 * 
 */
final class OrderByItemImpl extends DefineBaseImpl implements
		OrderByItemDeclare {

	public final ValueExpression getExpression() {
		return this.value;
	}

	public final boolean isDesc() {
		return this.isDesc;
	}

	public final void setDesc(boolean value) {
		this.checkModifiable();
		this.isDesc = value;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_orderby;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setBoolean(xml_attr_desc, this.isDesc);
		this.value.renderInto(element);
	}

	static final String xml_name_orderby = "orderby";
	static final String xml_attr_desc = "desc";

	final ValueExpr value;

	final SelectImpl<?, ?> owner;

	private boolean isDesc;

	OrderByItemImpl(SelectImpl<?, ?> owner, ValueExpr value) {
		super();
		if (owner == null || value == null) {
			throw new NullPointerException();
		}
		this.owner = owner;
		// value域在构造前检查
		this.value = value;
	}

	final void cloneTo(QueryStatementBase query, ArgumentOwner args) {
		query.newOrderBy(this.value.clone(query, args), this.isDesc);
	}

	final void render(ISqlQueryBuffer buffer, TableUsages usages) {
		if (this.value instanceof QueryColumnRefExpr) {
			QueryColumnRefExpr columnRef = (QueryColumnRefExpr) this.value;
			buffer.newOrder(columnRef.column.name, this.isDesc);
		} else {
			this.value.render(buffer.newOrder(this.isDesc), usages);
		}
	}
}
