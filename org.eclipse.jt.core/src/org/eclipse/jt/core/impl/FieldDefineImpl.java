package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.FieldDeclare;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.ReadableValue;

/**
 * 字段定义接口实现
 * 
 * @author Jeff Tang
 * 
 */
abstract class FieldDefineImpl extends NamedDefineImpl implements FieldDeclare {

	final DataType type;

	ValueExpr defaultValue;

	boolean isKeepValid;

	boolean isReadonly;

	/**
	 * 转化成相应的类型，为空则尝试使用默认值
	 */
	final Object convertWithDefault(Object value) {
		if (value == null) {
			if (this.defaultValue instanceof ReadableValue) {
				value = ((ReadableValue) this.defaultValue).getObject();
			} else {
				return null;
			}
		}
		return Convert.toType(this.type, value);
	}

	public FieldDefineImpl(String name, DataType type) {
		super(name);
		if (type == null) {
			throw new NullPointerException();
		}
		this.type = type;
	}

	public final ValueExpr getDefault() {
		return this.defaultValue;
	}

	public final void setDefault(ValueExpression value) {
		this.checkModifiable();
		this.defaultValue = (ValueExpr) value;
	}

	public final boolean isKeepValid() {
		return this.isKeepValid;
	}

	public final boolean isReadonly() {
		return this.isReadonly;
	}

	public final DataType getType() {
		return this.type;
	}

	public final void setKeepValid(boolean value) {
		this.checkModifiable();
		this.isKeepValid = value;

	}

	public final void setReadonly(boolean value) {
		this.checkModifiable();
		this.isReadonly = value;
	}

	// ------------------------- xml格式化 -------------------------

	static final String xml_attr_type = "type";
	static final String xml_attr_isKeepValid = "keep-valid";
	static final String xml_attr_isReadOnly = "read-only";
	static final String xml_element_default = "default";

	@Override
	public void render(SXElement element) {
		super.render(element);
		element.setAsType(xml_attr_type, this.type);
		element.maskTrue(xml_attr_isKeepValid, this.isKeepValid);
		element.maskTrue(xml_attr_isReadOnly, this.isReadonly);
		if (this.defaultValue != null) {
			this.defaultValue.renderInto(element.append(xml_element_default));
		}
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		this.isKeepValid = element.getBoolean(xml_attr_isKeepValid,
				this.isKeepValid);
		this.isReadonly = element.getBoolean(xml_attr_isReadOnly,
				this.isReadonly);
		SXElement defElement = element.firstChild(xml_element_default);
		if (defElement != null) {
			this.defaultValue = ConstExpr.loadConst(defElement.firstChild());
		}
	}
}
