package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.model.ModelInvokeDefine;
import org.eclipse.jt.core.def.obja.StructFieldDefine;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;


/**
 * 参数的包装类
 * 
 * @author Jeff Tang
 * 
 */
final class ModelJavaScriptAO implements Scriptable {
	final Object ao;
	final NamedElementContainer<? extends ArgumentDefine> args;

	private ModelJavaScriptAO(Object ao,
	        NamedElementContainer<? extends ArgumentDefine> args) {
		this.ao = ao;
		this.args = args;
	}

	private final static ModelJavaScriptAO empty = new ModelJavaScriptAO(null, null);

	static Scriptable allocJSAO(Object ao, ModelInvokeDefine invokee) {
		if (ao == null || ao == None.NONE || invokee == null) {
			return empty;
		}
		NamedElementContainer<? extends ArgumentDefine> args = invokee
		        .getArguments();
		if (args.isEmpty()) {
			return empty;
		}
		return new ModelJavaScriptAO(ao, args);
	}

	public final void delete(String name) {
		// Nothig
	}

	public final void delete(int index) {
		// Nothig
	}

	public final Object get(String name, Scriptable start) {
		if (this.args != null) {
			StructFieldDefine field = this.args.find(name);
			if (field != null) {
				return field.getFieldValueAsObject(this.ao);
			}
		}
		return NOT_FOUND;
	}

	public final Object get(int index, Scriptable start) {
		if (0 <= index && this.args != null && index < this.args.size()) {
			return this.args.get(index).getFieldValueAsObject(this.ao);
		}
		return NOT_FOUND;
	}

	public final String getClassName() {
		return "args";
	}

	@SuppressWarnings("unchecked")
	public final Object getDefaultValue(Class hint) {
		return Undefined.instance;
	}

	public final Object[] getIds() {
		return Utils.emptyObjectArray;
	}

	public final Scriptable getParentScope() {
		return null;
	}

	public final Scriptable getPrototype() {
		return null;
	}

	public boolean has(String name, Scriptable start) {
		return this.args != null && this.args.find(name) != null;
	}

	public boolean has(int index, Scriptable start) {
		return 0 <= index && this.args != null && index < this.args.size();
	}

	public boolean hasInstance(Scriptable instance) {
		return false;
	}

	public final void put(String name, Scriptable start, Object value) {
		if (this.args != null) {
			StructFieldDefine field = this.args.find(name);
			if (field != null) {
				field.setFieldValueAsObject(this.ao, value);
				return;
			}
		}
		throw Context.reportRuntimeError("参数不支持设置该属性[" + name + "]");
	}

	public final void put(int index, Scriptable start, Object value) {
		if (0 <= index && this.args != null && index < this.args.size()) {
			this.args.get(index).setFieldValueAsObject(this.ao, value);
		}
		throw Context.reportRuntimeError("参数不支持设置该属性[" + index + "]");
	}

	public final void setParentScope(Scriptable parent) {
		throw Context.reportRuntimeError("不支持父区域设置");
	}

	public final void setPrototype(Scriptable prototype) {
		throw Context.reportRuntimeError("不支持设置原形");
	}
}
