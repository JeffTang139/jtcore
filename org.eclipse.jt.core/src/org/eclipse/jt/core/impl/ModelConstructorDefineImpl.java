package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.model.ModelConstructorDeclare;
import org.eclipse.jt.core.impl.ModelServiceBase.ModelConstructor;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.model.ModelService;

/**
 * 模型构造器定义实现
 * 
 * @author Jeff Tang
 * 
 */
final class ModelConstructorDefineImpl extends ModelInvokeDefineImpl implements
		ModelConstructorDeclare, ScriptCompilable {

	private ScriptImpl script;

	public final void tryCompileScript(ContextImpl<?, ?, ?> context) {
		if (this.script != null) {
			this.script.prepareAsConstructor(context, this);
		}
	}

	public final ScriptImpl getScript() {
		if (this.script == null) {
			this.script = new ScriptImpl(this.owner);
		}
		return this.script;
	}

	ModelConstructorDefineImpl(ModelDefineImpl owner, String name,
			Class<?> aoClass) {
		super(owner, name, aoClass);
	}

	// ////////////////////////////
	// /// 模型访问
	// ////////////////////////////
	@SuppressWarnings("unchecked")
	ModelConstructor constructor;

	@SuppressWarnings("unchecked")
	final void internalSetConstructor(ModelConstructor constructor) {
		if (constructor != null && constructor != this.constructor) {
			this.owner.checkModelServiceMO(constructor);
			if (constructor.aoClass != None.class
					&& !constructor.aoClass.isAssignableFrom(this.getAOClass())) {
				throw new UnsupportedOperationException("模型构造器与模型构造定义的参数不符");
			}
		}
		this.constructor = constructor;
	}

	@SuppressWarnings("unchecked")
	final Object internalConstruct(ContextImpl<?, ?, ?> context, Object ao) {
		if (context == null || ao == null) {
			throw new NullPointerException();
		}
		Object mo = this.owner.newInitedSO();
		super.callBeforeInspects(context, mo, ao, null);
		try {
			if ((this.script == null || !this.script
					.executeScriptAsConstructor(context, mo, ao, this))
					&& this.constructor != null) {
				SpaceNode occorAtSave = this.constructor.getService()
						.updateContextSpace(context);
				try {
					this.constructor.doCreate(context, mo, ao, this);
				} catch (Throwable e) {
					throw Utils.tryThrowException(e);
				} finally {
					occorAtSave.updateContextSpace(context);
				}
			}
			super.callAfterInspects(context, mo, ao, null);
		} finally {
			super.callFinallyInspects(context, mo, ao, null);
		}
		return mo;
	}

	public Object newMO(Context context, Object ao) {
		return this.internalConstruct(ContextImpl.toContext(context), ao);
	}

	public Object newMO(Context context) {
		return this
				.internalConstruct(ContextImpl.toContext(context), None.NONE);
	}

	// //////////////////////////////////////
	// ////////XML
	// //////////////////////////////////////

	static final String xml_element_constructor = "constructor";
	static final String xml_attr_constructor = "constructor";

	@Override
	public final String getXMLTagName() {
		return xml_element_constructor;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		if (this.script != null) {
			this.script.tryRender(element);
		}
		if (this.constructor != null) {
			element.setAttribute(xml_attr_constructor, this.constructor
					.getClass().getName());
		}
	}

	@Override
	final void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		SXElement scriptE = element.firstChild(ScriptImpl.xml_element_script);
		if (scriptE != null) {
			this.getScript().merge(scriptE, helper);
		}
		String className = element.getAttribute(xml_attr_constructor, null);
		if (className != null && className.length() > 0) {
			this.constructor = helper.querier.find(ModelConstructor.class,
					className);
		}
	}

	@SuppressWarnings("unchecked")
	public final ModelService<?>.ModelConstructor<?> setConstructor(
			ModelService<?>.ModelConstructor<?> constructor) {
		ModelConstructor old = this.constructor;
		this.internalSetConstructor(constructor);
		return (ModelService<?>.ModelConstructor<?>) old;
	}
}
