package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.info.InfoKind;
import org.eclipse.jt.core.def.model.ModelConstraintDeclare;
import org.eclipse.jt.core.def.model.ModelInvokeStage;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.ModelServiceBase.ModelConstraintChecker;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;
import org.eclipse.jt.core.model.ModelService;

/**
 * 模型约束定义实现
 * 
 * @author Jeff Tang
 * 
 */
final class ModelConstraintDefineImpl extends InfoDefineImpl implements
        ModelConstraintDeclare, ScriptCompilable {

	public ModelConstraintDefineImpl(ModelDefineImpl owner, String name,
	        InfoKind kind, String messageFormat) {
		super(name, kind, messageFormat);
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		this.owner = owner;
	}

	public final ScriptImpl getScript() {
		if (this.script == null) {
			this.script = new ScriptImpl(this.owner);
		}
		return this.script;
	}

	private ScriptImpl script;

	public final void tryCompileScript(ContextImpl<?, ?, ?> context) {
		if (this.script != null) {
			this.script.prepareAsConstraint(context, this);
		}
	}

	public final ModelDefineImpl owner;

	public final ModelDefineImpl getOwner() {
		return this.owner;
	}

	// ////////////////////////////
	// /// 模型访问
	// ////////////////////////////
	@SuppressWarnings("unchecked")
	ModelConstraintChecker checker;

	@SuppressWarnings("unchecked")
	final void internalSetChecker(ModelConstraintChecker checker) {
		if (checker != null && checker != this.checker) {
			this.owner.checkModelServiceMO(checker);
		}
		this.checker = checker;
	}

	@SuppressWarnings("unchecked")
	final void internalCheck(ContextImpl<?, ?, ?> context, Object mo,
	        ModelInvokeDefineImpl trigger, Object triggerAO, Object value,
	        ModelInvokeStage stage) {
		if (context == null || mo == null || trigger == null
		        || triggerAO == null || stage == null) {
			throw new NullPointerException();
		}
		if ((this.script == null || !this.script.executeScriptAsChecker(
		        context, mo, this, trigger, triggerAO, value))
		        && this.checker != null) {
			SpaceNode occorAtSave = this.checker.getService()
			        .updateContextSpace(context);
			try {
				this.checker.doCheck(context, mo, this, trigger, triggerAO,
				        value, stage);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			} finally {
				occorAtSave.updateContextSpace(context);
			}
		}
	}

	// ////////////////////////////////////////
	// ///XML
	// ////////////////////////////////////////

	static final String xml_element_constraint = "constraint";
	static final String xml_attr_checker = "checker";

	@Override
	public final String getXMLTagName() {
		return xml_element_constraint;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		if (this.script != null) {
			this.script.tryRender(element);
		}
		if (this.checker != null) {
			element.setAttribute(xml_attr_checker, this.checker.getClass()
			        .getName());
		}
	}

	@Override
	final void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		SXElement scriptE = element.firstChild(ScriptImpl.xml_element_script);
		if (scriptE != null) {
			this.getScript().merge(scriptE, helper);
		}
		String className = element.getAttribute(xml_attr_checker, null);
		if (className != null && className.length() > 0) {
			this.checker = helper.querier.find(ModelConstraintChecker.class,
			        className);
		}
	}

	@SuppressWarnings("unchecked")
	public final ModelService<?>.ModelConstraintChecker setChecker(
	        ModelService<?>.ModelConstraintChecker checker) {
		ModelConstraintChecker old = this.checker;
		this.internalSetChecker(checker);
		return (ModelService<?>.ModelConstraintChecker) old;
	}
}
