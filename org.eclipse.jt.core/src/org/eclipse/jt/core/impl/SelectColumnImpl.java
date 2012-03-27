package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.query.SelectColumnDeclare;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.DataType;

/**
 * 查询输出列的实现类
 * 
 * <p>
 * 每个查询列表是select子句的一个输出
 * 
 * @author Jeff Tang
 * 
 */
abstract class SelectColumnImpl<TSelect extends SelectImpl<TSelect, TColumn>, TColumn extends SelectColumnImpl<TSelect, TColumn>>
		extends NamedDefineImpl implements SelectColumnDeclare, RelationColumn {

	public final ValueExpr getExpression() {
		return this.value;
	}

	public final void setExpression(ValueExpression value) {
		this.checkModifiable();
		if (value == null) {
			this.value = NullExpr.NULL;
		} else {
			ValueExpr v = (ValueExpr) value;
			if (SystemVariables.VALIDATE_EXPR_DOMAIN) {
				v.validateDomain(this.owner);
			}
			this.value = v;
		}
	}

	public final DataType getType() {
		return this.value.getType();
	}

	public final TSelect getOwner() {
		return this.owner;
	}

	@Override
	public final String getXMLTagName() {
		return xml_name_column;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		this.value.renderInto(element);
	}

	static final String xml_name_column = "column";

	final TSelect owner;

	private ValueExpr value;

	final ValueExpr value() {
		return this.value;
	}

	SelectColumnImpl(TSelect owner, String name, ValueExpr value) {
		super(name);
		if (owner == null || value == null) {
			throw new NullPointerException();
		}
		this.owner = owner;
		this.value = value;
	}

	final TableFieldDefineImpl tryGetTableField() {
		try {
			return ((TableFieldRefImpl) this.value).field;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("当前查询列的表达式不为字段引用表达式");
		}
	}

	void cloneTo(SelectImpl<?, ?> owner, ArgumentOwner args) {
		owner.newColumn(this.name, this.value.clone(owner, args));
	}

}
