package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.model.ModelActionDefine;
import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.def.model.ModelPropertyDefine;
import org.eclipse.jt.core.def.obja.StructFieldDefine;
import org.eclipse.jt.core.spi.model.ModelCallScope;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;


/**
 * ģ��ʵ���İ�װ��
 * 
 * @author Jeff Tang
 * 
 */
final class ModelJavaScriptMO implements Scriptable {
	private final Context context;
	private final ModelDefine model;
	final Object mo;
	private final ModelCallScope callPlace;

	ModelJavaScriptMO(Context context, ModelDefine model, Object mo,
	        ModelCallScope callPlace) {
		this.context = context;
		this.model = model;
		this.mo = mo;
		this.callPlace = callPlace;
	}

	public final void put(String name, Scriptable start, Object value) {
		switch (this.callPlace) {
		case IMPL_GETTER:
		case IMPL_CONSTRAINT:
			throw org.mozilla.javascript.Context
			        .reportRuntimeError("��ģ�����Ի�ȡ���в��������ø�����ֵ[" + name + "]");
		}
		StructFieldDefine field = this.model.getFields().find(name);
		if (field != null) {
			field.setFieldValueAsObject(this.mo, value);
			return;
		}
		ModelPropertyDefine prop = this.model.getProperties().find(name);
		if (prop != null) {
			prop.setPropValueAsObject(this.context, this.mo, value);
			return;
		}
		throw org.mozilla.javascript.Context.reportRuntimeError("ģ�Ͳ�֧�����ø�����["
		        + name + "]");
	}

	public final void put(int index, Scriptable start, Object value) {
		throw org.mozilla.javascript.Context.reportRuntimeError("ģ�Ͳ�֧�����ø�����["
		        + index + "]");
	}

	public final boolean hasInstance(Scriptable instance) {
		return false;
	}

	public final void delete(String name) {
		// DO Nothing
	}

	public final void delete(int index) {
		// DO Nothing
	}

	/**
	 * ��ȡֵ���߷���
	 * 
	 * @param name
	 *            ����
	 * @param fields
	 *            �Ƿ����ֶ�������
	 * @param props
	 *            �Ƿ�������������
	 * @param actions
	 *            �Ƿ��ڶ���������
	 */
	final private Object getMumber(String name, boolean fields, boolean props,
	        boolean actions) {
		if (fields) {
			StructFieldDefine field = this.model.getFields().find(name);
			if (field != null) {
				return field.getFieldValueAsObject(this.mo);
			}
		}
		if (props) {
			ModelPropertyDefine prop = this.model.getProperties().find(name);
			if (prop != null) {
				return prop.getPropValueAsObject(this.context, this.mo);
			}
		}
		if (actions) {
			ModelActionDefine action = this.model.getActions().find(name);
			if (action != null) {
				return new JS_ModelAction(action);
			}
		}
		return NOT_FOUND;
	}

	/**
	 * ģ�Ͷ�����JS�еĴ���
	 * 
	 * @author Jeff Tang
	 * 
	 */
	final static class JS_ModelAction implements Callable {
		final ModelActionDefine action;

		JS_ModelAction(ModelActionDefine action) {
			this.action = action;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof JS_ModelAction
			        && ((JS_ModelAction) obj).action == this.action;
		}

		public final Object call(org.mozilla.javascript.Context cx,
		        org.mozilla.javascript.Scriptable scope,
		        org.mozilla.javascript.Scriptable thisObj, Object[] args) {
			ModelJavaScriptMO jsmo = (ModelJavaScriptMO) thisObj;
			Object ao = this.action.newAO(args);
			this.action.execute(jsmo.context, jsmo, ao);
			return org.mozilla.javascript.Undefined.instance;
		}
	}

	public final Object get(String name, Scriptable start) {
		switch (this.callPlace) {
		case IMPL_GETTER:
		case IMPL_CONSTRAINT:
			return this.getMumber(name, true, true, false);
		case IMPL_ACTION:
		case IMPL_SETTER:
			return this.getMumber(name, true, true, true);
		case OUTER:
			return this.getMumber(name, false, true, true);
		default:
			return NOT_FOUND;
		}
	}

	public final Object get(int index, Scriptable start) {
		return NOT_FOUND;
	}

	public final String getClassName() {
		return this.model.getName();
	}

	@SuppressWarnings("unchecked")
	public final Object getDefaultValue(Class hint) {
		return Undefined.instance;
	}

	public final Object[] getIds() {
		return ScriptRuntime.emptyArgs;
	}

	/**
	 * ��Ա�Ƿ����
	 * 
	 * @param name
	 *            ����
	 * @param fields
	 *            �Ƿ����ֶ�������
	 * @param props
	 *            �Ƿ�������������
	 * @param actions
	 *            �Ƿ��ڶ���������
	 */
	final private boolean hasMumber(String name, boolean fields, boolean props,
	        boolean actions) {
		if (fields && this.model.getFields().find(name) != null) {
			return true;
		}
		if (props && this.model.getProperties().find(name) != null) {
			return true;
		}
		if (actions && this.model.getActions().find(name) != null) {
			return true;
		}
		return false;
	}

	public final boolean has(String name, Scriptable start) {
		switch (this.callPlace) {
		case IMPL_GETTER:
			return this.hasMumber(name, true, true, false);
		case IMPL_ACTION:
		case IMPL_CONSTRUCTOR:
		case IMPL_SETTER:
			return this.hasMumber(name, true, true, true);
		case OUTER:
			return this.hasMumber(name, false, true, true);
		default:
			return false;
		}
	}

	public final boolean has(int index, Scriptable start) {
		return false;
	}

	public final void setParentScope(Scriptable parent) {
		throw org.mozilla.javascript.Context.reportRuntimeError("��֧�ָ���������");
	}

	public final Scriptable getParentScope() {
		return null;
	}

	public final Scriptable getPrototype() {
		return null;
	}

	public final void setPrototype(Scriptable prototype) {
		throw org.mozilla.javascript.Context.reportRuntimeError("��֧������ԭ��");
	}
}
