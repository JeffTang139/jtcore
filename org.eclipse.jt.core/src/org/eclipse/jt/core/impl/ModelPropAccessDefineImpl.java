package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.model.ModelFieldDefine;
import org.eclipse.jt.core.def.model.ModelPropAccessDeclare;
import org.eclipse.jt.core.def.model.ModelPropertyDefine;
import org.eclipse.jt.core.impl.ModelServiceBase.ModelPropertyAccessor;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.model.ModelService;

/**
 * 模型属性访问定义 访问属性的优先级依次是脚本、访问器、子段
 * 
 * @author Jeff Tang
 * 
 */
final class ModelPropAccessDefineImpl implements ModelPropAccessDeclare {
	ScriptImpl script;
	@SuppressWarnings("unchecked")
	ModelPropertyAccessor accessor;
	StructFieldDefineImpl field;

	@SuppressWarnings("unchecked")
	final void internalSetAccessor(ModelPropertyAccessor accessor) {
		if (accessor != null && accessor != this.accessor) {
			this.ownerProperty.owner.checkModelServiceMO(accessor);
			this.ownerProperty.checkModelAccessor(accessor, this);
		}
		this.accessor = accessor;
	}

	final boolean isValid() {
		return this.script != null || this.accessor != null
				|| this.field != null;
	}

	final ModelPropertyDefineImpl ownerProperty;

	public final ModelPropertyDefine getPropertyDefine() {
		return this.ownerProperty;
	}

	ModelPropAccessDefineImpl(ModelPropertyDefineImpl ownerProperty) {
		if (ownerProperty == null) {
			throw new NullPointerException();
		}
		this.ownerProperty = ownerProperty;
	}

	public ScriptImpl getScript() {
		if (this.script == null) {
			this.script = new ScriptImpl(this.ownerProperty.owner);
		}
		return this.script;
	}

	public final void setRefField(ModelFieldDefine field) {
		if (field != null) {
			StructFieldDefineImpl f = (StructFieldDefineImpl) field;
			if (f.owner != this.ownerProperty.owner) {
				throw new IllegalArgumentException();
			}
			this.field = f;
		} else {
			this.field = null;
		}
	}

	public final StructFieldDefineImpl getRefField() {
		return this.field;
	}

	@SuppressWarnings("unchecked")
	public final ModelService<?>.ModelPropertyAccessor setAccessor(
			ModelService<?>.ModelPropertyAccessor accessor) {
		ModelPropertyAccessor old = this.accessor;
		this.internalSetAccessor(accessor);
		return (ModelService<?>.ModelPropertyAccessor) old;
	}

	// ////////////////////////////////////////////////
	// /////////////模型访问
	// ///////////////////////////////////////////////
	final boolean scriptCallable() {
		return this.script != null && this.script.scriptCallable();
	}

	// /////////////////////////////////
	// //////XML
	// /////////////////////////////////
	static final String xml_attr_field_ref = "field-ref";
	static final String xml_attr_accessor = "accessor";

	final void renderInto(SXElement element, String tag) {
		if (this.field != null || this.script != null
				&& this.script.hasScript() || this.accessor != null) {
			element = element.append(tag);
			if (this.field != null) {
				element.setString(xml_attr_field_ref, this.field.name);
			}
			if (this.script != null) {
				this.script.tryRender(element);
			}
			if (this.accessor != null) {
				element.setAttribute(xml_attr_accessor, this.accessor
						.getClass().getName());
			}
		}
	}

	final void merge(SXElement element, SXMergeHelper helper) {
		String fieldName = element.getAttribute(xml_attr_field_ref, null);
		if (fieldName != null) {
			this.field = this.ownerProperty.owner.fields.get(fieldName);
		}
		SXElement scriptE = element.firstChild(ScriptImpl.xml_element_script);
		if (scriptE != null) {
			this.getScript().merge(scriptE, helper);
		}
		String className = element.getAttribute(xml_attr_accessor, null);
		if (className != null && className.length() > 0) {
			this.accessor = helper.querier.find(ModelPropertyAccessor.class,
					className);
		}
	}
}
