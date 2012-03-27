package org.eclipse.jt.core.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.arg.ArgumentableDefine;
import org.eclipse.jt.core.def.info.ErrorInfoDefine;
import org.eclipse.jt.core.def.model.ModelActionDefine;
import org.eclipse.jt.core.def.model.ModelConstraintDefine;
import org.eclipse.jt.core.def.model.ModelConstructorDefine;
import org.eclipse.jt.core.def.model.ModelInvokeDefine;
import org.eclipse.jt.core.def.model.ModelObjSourceDefine;
import org.eclipse.jt.core.def.model.ModelPropAccessDefine;
import org.eclipse.jt.core.def.model.ModelPropertyDefine;
import org.eclipse.jt.core.def.model.ModelScriptContext;
import org.eclipse.jt.core.def.model.ScriptDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.ObjectBuilder;
import org.eclipse.jt.core.spi.model.ModelCallScope;
import org.mozilla.javascript.Function;


/**
 * JavaScript 模型脚本上下文
 * 
 * @author Jeff Tang
 * 
 */
final class ModelJavaScriptContext implements
        ModelScriptContext<org.mozilla.javascript.Function>,
        org.mozilla.javascript.Scriptable {
	/**
	 * 引擎
	 */
	private final ModelJavaScriptEngine engine;
	/**
	 * DNA上下文
	 */
	private final Context context;
	/**
	 * JavaScript执行器上下文
	 */
	private org.mozilla.javascript.Context jsContext;

	ModelJavaScriptContext(ModelJavaScriptEngine engine, Context context) {
		if (engine == null) {
			throw new NullArgumentException("engine");
		}
		if (context == null) {
			throw new NullArgumentException("context");
		}
		this.engine = engine;
		this.context = context;
		this.jsContext = org.mozilla.javascript.ContextFactory.getGlobal()
		        .enterContext();
	}

	private static final Object[] aoAsValues(ArgumentableDefine argsDefine,
	        Object ao, int reserved) {
		Container<? extends ArgumentDefine> args = argsDefine.getArguments();
		int l = args.size() + reserved;
		Object[] array = new Object[l];
		for (int i = reserved, j = 0; i < l; j++, i++) {
			array[i] = args.get(j).getFieldValueAsObject(ao);
		}
		return array;
	}

	public final void executeAction(ModelActionDefine actionDefine,
	        org.mozilla.javascript.Function preparedData, Object mo, Object ao,
	        ModelInvokeDefine trigger, Object triggerAO, Object value) {
		// context,$arguments,$value -- 3
		Object[] args = aoAsValues(actionDefine, ao, 3);
		args[0] = this;
		args[1] = ModelJavaScriptAO.allocJSAO(triggerAO, trigger);
		args[2] = value;
		ModelJavaScriptMO jsmo = new ModelJavaScriptMO(this.context,
		        actionDefine.getOwner(), mo, ModelCallScope.IMPL_ACTION);
		preparedData.call(this.jsContext, jsmo, jsmo, args);
	}

	public final void executeChecker(ModelConstraintDefine constraint,
	        org.mozilla.javascript.Function preparedData, Object mo,
	        ModelInvokeDefine trigger, Object triggerAO, Object value) {
		ModelJavaScriptMO jsmo = new ModelJavaScriptMO(this.context, constraint
		        .getOwner(), mo, ModelCallScope.IMPL_CONSTRAINT);
		// context,constraint,$arguments,$value -- 4
		Object[] args = new Object[] { this, constraint,
		        ModelJavaScriptAO.allocJSAO(triggerAO, trigger), value };
		preparedData.call(this.jsContext, jsmo, jsmo, args);
	}

	public final void executeConstructor(ModelConstructorDefine constructor,
	        org.mozilla.javascript.Function preparedData, Object mo, Object ao) {
		// context -- 1
		Object[] args = aoAsValues(constructor, ao, 1);
		ModelJavaScriptMO jsmo = new ModelJavaScriptMO(this.context,
		        constructor.getOwner(), mo, ModelCallScope.IMPL_CONSTRUCTOR);
		args[0] = this;
		preparedData.call(this.jsContext, jsmo, jsmo, args);
	}

	public final Object executeGetter(ModelPropAccessDefine propAccess,
	        org.mozilla.javascript.Function preparedData, Object mo) {
		ModelPropertyDefine property = propAccess.getPropertyDefine();
		// context --1
		Object[] args = aoAsValues(property, null, 1);
		ModelJavaScriptMO jsmo = new ModelJavaScriptMO(this.context, property
		        .getOwner(), mo, ModelCallScope.IMPL_GETTER);
		args[0] = this;
		return preparedData.call(this.jsContext, jsmo, jsmo, args);
	}

	public final void executeSetter(ModelPropAccessDefine propAccess,
	        org.mozilla.javascript.Function preparedData, Object mo,
	        Object value) {
		ModelPropertyDefine property = propAccess.getPropertyDefine();
		// context,value --2
		Object[] args = aoAsValues(property, null, 2);
		ModelJavaScriptMO jsmo = new ModelJavaScriptMO(this.context, property
		        .getOwner(), mo, ModelCallScope.IMPL_SETTER);
		args[0] = this;
		args[1] = value;
		preparedData.call(this.jsContext, jsmo, jsmo, args);
	}

	public final <TMO> void executeSource(ModelObjSourceDefine sourceDefine,
	        Function preparedData, Object ao, int offset, int count,
	        List<TMO> mos, ObjectBuilder<TMO> factory) {
		// context,offset,count,out --4
		Object[] args = aoAsValues(sourceDefine, ao, 4);
		args[0] = this;
		args[1] = offset;
		args[2] = count;
		args[3] = new ModelJavaScriptMOs(this.context, sourceDefine.getOwner(),
		        mos, factory);
		preparedData.call(this.jsContext, this, this, args);
	}

	public final int executeSourceCountOf(ModelObjSourceDefine sourceDefine,
	        Function preparedData, Object ao) {
		// context --1
		Object[] args = aoAsValues(sourceDefine, ao, 1);
		args[0] = this;
		Object r = preparedData.call(this.jsContext, this, this, args);
		if (r instanceof Number) {
			return ((Number) r).intValue();
		} else {
			return -1;
		}
	}

	public final ModelJavaScriptEngine getEngine() {
		return this.engine;
	}

	private final org.mozilla.javascript.Function compileFunction(
	        String funcPrefix, String funcName, String reservedArgs,
	        ArgumentableDefine argsDefine, ScriptDefine script) {
		String funcBody = script.getScript();
		StringBuilder builder = new StringBuilder(funcBody.length() + 128);
		builder.append("function ").append(funcPrefix).append(funcName).append(
		        '(');
		builder.append(reservedArgs);
		if (argsDefine != null) {
			Container<? extends ArgumentDefine> args = argsDefine
			        .getArguments();
			for (int i = 0, c = args.size(); i < c; i++) {
				builder.append(',');
				builder.append(args.get(i).getName());
			}
		}
		builder.append("){\r\n");
		builder.append(funcBody);
		builder.append("\r\n}");
		return this.jsContext.compileFunction(this.engine.getScope(), builder
		        .toString(), funcName, 0, null);
	}

	public final org.mozilla.javascript.Function prepareAction(
	        ModelActionDefine actionDefine) {
		return this.compileFunction("do_", actionDefine.getName(),
		        "context,$arguments,$value", actionDefine, actionDefine
		                .getScript());
	}

	public final org.mozilla.javascript.Function prepareConstraint(
	        ModelConstraintDefine constraint) {
		return this.compileFunction("check_", constraint.getName(),
		        "context,constraint,$arguments,$value", null, constraint
		                .getScript());
	}

	public final org.mozilla.javascript.Function prepareConstructor(
	        ModelConstructorDefine constructor) {
		return this.compileFunction("new_", constructor.getName(), "context",
		        constructor, constructor.getScript());
	}

	public final org.mozilla.javascript.Function prepareGetter(
	        ModelPropAccessDefine propAccess) {
		ModelPropertyDefine pd = propAccess.getPropertyDefine();
		return this.compileFunction("get_", pd.getName(), "context", pd,
		        propAccess.getScript());
	}

	public final org.mozilla.javascript.Function prepareSetter(
	        ModelPropAccessDefine propAccess) {
		ModelPropertyDefine pd = propAccess.getPropertyDefine();
		return this.compileFunction("set_", pd.getName(), "context,value", pd,
		        propAccess.getScript());
	}

	public final org.mozilla.javascript.Function prepareSource(
	        ModelObjSourceDefine source) {
		return this.compileFunction("fetch_", source.getName(),
		        "context,offset,count,out", source, source.getScript());
	}

	public final org.mozilla.javascript.Function prepareSourceCountOf(
	        ModelObjSourceDefine source) {
		return this.compileFunction("countof_", source.getName(), "context",
		        source, source.getMOCountOfScript());
	}

	public final void release() {
		try {
			if (this.jsContext != null
			        && this.jsContext == org.mozilla.javascript.Context
			                .getCurrentContext()) {
				org.mozilla.javascript.Context.exit();
			}
		} finally {
			this.jsContext = null;
		}
	}

	public final void delete(String name) {
		// DoNothing
	}

	public final void delete(int index) {
		// DoNothing
	}

	public final Object get(String name, org.mozilla.javascript.Scriptable start) {
		Object o = funcs.get(name);
		if (o != null) {
			return o;
		}
		return NOT_FOUND;
	}

	public final Object get(int index, org.mozilla.javascript.Scriptable start) {
		return NOT_FOUND;
	}

	public final String getClassName() {
		return "Context";
	}

	@SuppressWarnings("unchecked")
	public final Object getDefaultValue(Class hint) {
		return org.mozilla.javascript.Undefined.instance;
	}

	public final Object[] getIds() {
		return Utils.emptyObjectArray;
	}

	public final org.mozilla.javascript.Scriptable getParentScope() {
		return null;
	}

	public final org.mozilla.javascript.Scriptable getPrototype() {
		return null;
	}

	public final boolean has(String name,
	        org.mozilla.javascript.Scriptable start) {
		return funcs.containsKey(name);
	}

	public final boolean has(int index, org.mozilla.javascript.Scriptable start) {
		return false;
	}

	public final boolean hasInstance(org.mozilla.javascript.Scriptable instance) {
		return false;
	}

	public final void put(String name, org.mozilla.javascript.Scriptable start,
	        Object value) {
		throw org.mozilla.javascript.Context
		        .reportRuntimeError("上下文不支持设置该静态属性[" + name + "]");
	}

	public final void put(int index, org.mozilla.javascript.Scriptable start,
	        Object value) {
		throw org.mozilla.javascript.Context
		        .reportRuntimeError("上下文不支持设置该静态属性[" + index + "]");
	}

	public final void setParentScope(org.mozilla.javascript.Scriptable parent) {
		throw org.mozilla.javascript.Context.reportRuntimeError("不支持父区域设置");
	}

	public final void setPrototype(org.mozilla.javascript.Scriptable prototype) {
		throw org.mozilla.javascript.Context.reportRuntimeError("不支持设置原形");
	}

	private static final Map<String, org.mozilla.javascript.Callable> funcs = new HashMap<String, org.mozilla.javascript.Callable>();

	/**
	 * NDA上下文方法的基类
	 * 
	 * @author Jeff Tang
	 * 
	 */
	abstract static class DNAContextMethod implements
	        org.mozilla.javascript.Callable {

		DNAContextMethod(String name) {
			funcs.put(name, this);
		}

		public final Object call(org.mozilla.javascript.Context cx,
		        org.mozilla.javascript.Scriptable scope,
		        org.mozilla.javascript.Scriptable thisObj, Object[] args) {
			return this
			        .doCall(((ModelJavaScriptContext) thisObj).context, args);
		}

		protected void doVoidCall(Context context, Object[] args) {
		}

		protected Object doCall(Context context, Object[] args) {
			this.doVoidCall(context, args);
			return org.mozilla.javascript.Undefined.instance;
		}
	}

	final static DNAContextMethod report = new DNAContextMethod("report") {

		@Override
		protected final void doVoidCall(Context context, Object[] args) {
			if (args.length == 0) {
				return;
			}
			ErrorInfoDefine inforEn = (ErrorInfoDefine) args[0];
			switch (args.length) {
			case 1:
				context.reportError(inforEn);
				break;
			case 2:
				context.reportError(inforEn, args[1]);
				break;
			case 3:
				context.reportError(inforEn, args[1], args[2]);
				break;
			case 4:
				context.reportError(inforEn, args[1], args[2], args[3]);
				break;
			default:
				Object[] others = new Object[args.length - 4];
				System.arraycopy(args, 4, others, 0, others.length);
				context.reportError(inforEn, args[1], args[2], args[3], others);
				break;
			}
		}
	};

}
