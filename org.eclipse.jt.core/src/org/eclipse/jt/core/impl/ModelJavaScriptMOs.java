package org.eclipse.jt.core.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.model.ModelDefine;
import org.eclipse.jt.core.misc.ObjectBuilder;
import org.eclipse.jt.core.spi.model.ModelCallScope;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;


/**
 * 模型实例的包装类
 * 
 * @author Jeff Tang
 * 
 */
final class ModelJavaScriptMOs implements Scriptable {
	private final List<Object> mos;
	private final ObjectBuilder<Object> builder;
	private final Context context;
	private final ModelDefine model;

	@SuppressWarnings("unchecked")
	ModelJavaScriptMOs(Context context, ModelDefine model, List<?> mos,
	        ObjectBuilder<?> builder) {
		this.mos = (List<Object>) mos;
		this.builder = (ObjectBuilder<Object>) builder;
		this.context = context;
		this.model = model;
	}

	private Object newMO(Object[] args) {
		Object mo;
		try {
			mo = this.builder.build();
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
		this.mos.add(mo);
		return new ModelJavaScriptMO(this.context, this.model, mo,
		        ModelCallScope.IMPL_SOURCE);
	}

	private void addMO(Object[] args) {
		if (args != null && args.length > 1) {
			Object mjsmo = args[0];
			if (mjsmo instanceof ModelJavaScriptMO) {
				Object mo = ((ModelJavaScriptMO) mjsmo).mo;
				if (!this.mos.contains(mo)) {
					this.mos.add(mo);
				}
			}
		}
	}

	public final void put(String name, Scriptable start, Object value) {
		throw org.mozilla.javascript.Context.reportRuntimeError("模型不支持设置该属性["
		        + name + "]");
	}

	public final void put(int index, Scriptable start, Object value) {
		throw org.mozilla.javascript.Context.reportRuntimeError("模型不支持设置该属性["
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

	private static final Map<String, org.mozilla.javascript.Callable> funcs = new HashMap<String, org.mozilla.javascript.Callable>();

	abstract static class DNAMOSMethod implements Callable {

		DNAMOSMethod(String name) {
			funcs.put(name, this);
		}

		public final Object call(org.mozilla.javascript.Context cx,
		        org.mozilla.javascript.Scriptable scope,
		        org.mozilla.javascript.Scriptable thisObj, Object[] args) {
			return this.doCall((ModelJavaScriptMOs) thisObj, args);
		}

		protected void doVoidCall(ModelJavaScriptMOs mos, Object[] args) {
		}

		protected Object doCall(ModelJavaScriptMOs mos, Object[] args) {
			this.doVoidCall(mos, args);
			return org.mozilla.javascript.Undefined.instance;
		}
	}

	final static DNAMOSMethod newMO = new DNAMOSMethod("newMO") {
		@Override
		protected Object doCall(ModelJavaScriptMOs mos, Object[] args) {
			return mos.newMO(args);
		}
	};
	final static DNAMOSMethod addMO = new DNAMOSMethod("addMO") {
		@Override
		protected void doVoidCall(ModelJavaScriptMOs mos, Object[] args) {
			mos.addMO(args);
		}
	};

	public final Object get(String name, Scriptable start) {
		Object o = funcs.get(name);
		if (o != null) {
			return o;
		}
		return NOT_FOUND;
	}

	public final Object get(int index, Scriptable start) {
		return NOT_FOUND;
	}

	public final String getClassName() {
		return "MOS";
	}

	@SuppressWarnings("unchecked")
	public final Object getDefaultValue(Class hint) {
		return Undefined.instance;
	}

	public final Object[] getIds() {
		return ScriptRuntime.emptyArgs;
	}

	public final boolean has(String name, Scriptable start) {
		return funcs.containsKey(name);
	}

	public final boolean has(int index, Scriptable start) {
		return false;
	}

	public final void setParentScope(Scriptable parent) {
		throw org.mozilla.javascript.Context.reportRuntimeError("不支持父区域设置");
	}

	public final Scriptable getParentScope() {
		return null;
	}

	public final Scriptable getPrototype() {
		return null;
	}

	public final void setPrototype(Scriptable prototype) {
		throw org.mozilla.javascript.Context.reportRuntimeError("不支持设置原形");
	}
}
