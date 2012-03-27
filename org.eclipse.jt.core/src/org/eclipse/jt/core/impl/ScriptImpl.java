package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.def.model.ModelScriptContext;
import org.eclipse.jt.core.def.model.ModelScriptEngine;
import org.eclipse.jt.core.def.model.ScriptDeclare;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.ObjectBuilder;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;


final class ScriptImpl implements ScriptDeclare {
	private final DefineBaseImpl owner;
	private String language;
	private String script;

	@SuppressWarnings("unchecked")
	final Object executeScriptAsGetter(ContextImpl<?, ?, ?> context, Object mo,
	        ModelPropAccessDefineImpl property) {
		if (this.engine == null) {
			throw new UnsupportedOperationException("不支脚本持执行或存在编译错误");
		}
		ModelScriptContext msContext = context.getScriptContext(this.engine);
		return msContext.executeGetter(property, this.prepareData, mo);
	}

	@SuppressWarnings("unchecked")
	final boolean executeScriptAsSetter(ContextImpl<?, ?, ?> context,
	        Object mo, Object value, ModelPropAccessDefineImpl property) {
		if (this.engine == null) {
			return false;
		}
		ModelScriptContext msContext = context.getScriptContext(this.engine);
		msContext.executeSetter(property, this.prepareData, mo, value);
		return true;
	}

	@SuppressWarnings("unchecked")
	final boolean executeScriptAsAction(ContextImpl<?, ?, ?> context,
	        Object mo, Object ao, ModelInvokeDefineImpl trigger,
	        Object triggerAO, Object value, ModelActionDefineImpl invoke) {
		if (this.engine == null) {
			return false;
		}
		ModelScriptContext msContext = context.getScriptContext(this.engine);
		msContext.executeAction(invoke, this.prepareData, mo, ao, trigger,
		        triggerAO, value);
		return true;
	}

	@SuppressWarnings("unchecked")
	final <TMO> boolean executeScriptAsSource(ContextImpl<?, ?, ?> context,
	        Object ao, List<TMO> mos, int offset, int count,
	        ObjectBuilder<TMO> factory, ModelObjSourceDefineImpl source) {
		if (this.engine == null) {
			return false;
		}
		ModelScriptContext msContext = context.getScriptContext(this.engine);
		if (factory == null) {
			factory = (ObjectBuilder<TMO>) source;
		}
		msContext.executeSource(source, this.prepareData, ao, offset, count,
		        mos, factory);
		return true;
	}

	@SuppressWarnings("unchecked")
	final int executeScriptAsSourceCountOf(ContextImpl<?, ?, ?> context,
	        Object ao, ModelObjSourceDefineImpl source) {
		if (this.engine == null) {
			return -1;
		}
		ModelScriptContext msContext = context.getScriptContext(this.engine);
		return msContext.executeSourceCountOf(source, msContext, ao);
	}

	@SuppressWarnings("unchecked")
	final boolean executeScriptAsConstructor(ContextImpl<?, ?, ?> context,
	        Object mo, Object ao, ModelConstructorDefineImpl invoke) {
		if (this.engine == null) {
			return false;
		}
		ModelScriptContext msContext = context.getScriptContext(this.engine);
		msContext.executeConstructor(invoke, this.prepareData, mo, ao);
		return true;
	}

	@SuppressWarnings("unchecked")
	final boolean executeScriptAsChecker(ContextImpl<?, ?, ?> context,
	        Object mo, ModelConstraintDefineImpl constraint,
	        ModelInvokeDefineImpl trigger, Object triggerAO, Object value) {
		if (this.engine == null) {
			return false;
		}
		ModelScriptContext msContext = context.getScriptContext(this.engine);
		msContext.executeChecker(constraint, this.prepareData, mo, trigger,
		        triggerAO, value);
		return true;
	}

	final boolean scriptCallable() {
		return this.engine != null;
	}

	public final boolean hasScript() {
		return this.script != null && this.script.length() > 0;
	}

	public final void setLanguage(String value) {
		this.owner.checkModifiable();
		this.language = value;
	}

	public final void setScript(String value) {
		this.owner.checkModifiable();
		this.script = value;
	}

	public final String getLanguage() {
		return this.language;
	}

	public final String getScript() {
		return this.script;
	}

	ScriptImpl(DefineBaseImpl owner) {
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		this.owner = owner;
	}

	/**
	 * 对应的引擎
	 */
	private ModelScriptEngine<?> engine;
	/**
	 * 脚本的预备数据（编译信息）
	 */
	private Object prepareData;

	private boolean setEngine(ModelScriptContext<?> sc) {
		if (sc != null) {
			this.engine = sc.getEngine();
			return true;
		} else {
			this.engine = null;
			this.prepareData = null;
			return false;
		}
	}

	final void prepareAsGetter(ContextImpl<?, ?, ?> context,
	        ModelPropAccessDefineImpl propAccess) {
		if (this.hasScript()) {

			ModelScriptContext<?> sc = context
			        .tryGetScriptContext(this.language);
			if (this.setEngine(sc)) {
				this.prepareData = sc.prepareGetter(propAccess);
			}
		}
	}

	final void prepareAsSetter(ContextImpl<?, ?, ?> context,
	        ModelPropAccessDefineImpl propAccess) {
		if (this.hasScript()) {
			ModelScriptContext<?> sc = context
			        .tryGetScriptContext(this.language);
			if (this.setEngine(sc)) {
				this.prepareData = sc.prepareSetter(propAccess);
			}
		}
	}

	final void prepareAsAction(ContextImpl<?, ?, ?> context,
	        ModelActionDefineImpl action) {
		if (this.hasScript()) {
			ModelScriptContext<?> sc = context
			        .tryGetScriptContext(this.language);
			if (this.setEngine(sc)) {
				this.prepareData = sc.prepareAction(action);
			}
		}
	}

	final void prepareAsConstructor(ContextImpl<?, ?, ?> context,
	        ModelConstructorDefineImpl constructor) {
		if (this.hasScript()) {
			ModelScriptContext<?> sc = context
			        .tryGetScriptContext(this.language);
			if (this.setEngine(sc)) {
				this.prepareData = sc.prepareConstructor(constructor);
			}
		}
	}

	final void prepareAsConstraint(ContextImpl<?, ?, ?> context,
	        ModelConstraintDefineImpl constraint) {
		if (this.hasScript()) {
			ModelScriptContext<?> sc = context
			        .tryGetScriptContext(this.language);
			if (this.setEngine(sc)) {
				this.prepareData = sc.prepareConstraint(constraint);
			}
		}
	}

	final void prepareAsSource(ContextImpl<?, ?, ?> context,
	        ModelObjSourceDefineImpl source) {
		if (this.hasScript()) {
			ModelScriptContext<?> sc = context
			        .tryGetScriptContext(this.language);
			if (this.setEngine(sc)) {
				this.prepareData = sc.prepareSource(source);
			}
		}
	}

	final void prepareAsSourceCountOf(ContextImpl<?, ?, ?> context,
	        ModelObjSourceDefineImpl source) {
		if (this.hasScript()) {
			ModelScriptContext<?> sc = context
			        .tryGetScriptContext(this.language);
			if (this.setEngine(sc)) {
				this.prepareData = sc.prepareSourceCountOf(source);
			}
		}
	}

	final ModelScriptEngine<?> getEngine() {
		return this.engine;
	}

	final Object getPreparedData() {
		return this.prepareData;
	}

	// /////////////////////////////////
	// //////XML
	// /////////////////////////////////
	static final String xml_element_script = "script";
	static final String xml_attr_language = "language";

	final boolean tryRender(SXElement element, String tagName) {
		if (this.hasScript()) {
			element = element.append(tagName);
			element.setAttribute(xml_attr_language, this.language);
			element.setCDATA(this.script);
			return true;
		}
		return false;
	}

	final boolean tryRender(SXElement element) {
		return this.tryRender(element, xml_element_script);
	}

	final void merge(SXElement element, SXMergeHelper helper) {
		this.language = element.getAttribute(xml_attr_language, this.language);
		this.script = element.getCDATA();
	}
}
