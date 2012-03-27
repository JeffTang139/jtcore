package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.model.InspectPoint;
import org.eclipse.jt.core.def.model.ModelActionDefine;
import org.eclipse.jt.core.def.model.ModelConstraintDefine;
import org.eclipse.jt.core.def.model.ModelInvokeDeclare;
import org.eclipse.jt.core.def.model.ModelInvokeStage;
import org.eclipse.jt.core.def.model.ModelInvokeValidity;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;

/**
 * 模型调用的基础类
 * 
 * @author Jeff Tang
 * 
 */
abstract class ModelInvokeDefineImpl extends ArgumentableImpl implements
		ModelInvokeDeclare {
	public final boolean ignorePrepareIfDBInvalid() {
		return false;
	}

	boolean isAuthorizable;
	final ModelDefineImpl owner;
	private MetaBaseContainerImpl<InspectPointImpl> finallyInspects;
	private MetaBaseContainerImpl<InspectPointImpl> afterInspects;
	private MetaBaseContainerImpl<InspectPointImpl> beforeInspects;

	final boolean hasBeforeInspects() {
		return this.beforeInspects != null && this.beforeInspects.size() > 0;
	}

	final boolean hasAfterInspects() {
		return this.beforeInspects != null && this.beforeInspects.size() > 0;
	}

	final boolean hasFinallyInspects() {
		return this.beforeInspects != null && this.beforeInspects.size() > 0;
	}

	final void callBeforeInspects(ContextImpl<?, ?, ?> context, Object mo,
			Object ao, Object value) {
		if (this.beforeInspects != null) {
			for (int i = 0, c = this.beforeInspects.size(); i < c; i++) {
				InspectPointImpl ipi = this.beforeInspects.get(i);
				ipi.inspect(context, mo, this, ao, value,
						ModelInvokeStage.BEFORE);
			}
		}
	}

	final void callAfterInspects(ContextImpl<?, ?, ?> context, Object mo,
			Object ao, Object value) {
		if (this.afterInspects != null) {
			for (int i = 0, c = this.afterInspects.size(); i < c; i++) {
				InspectPointImpl ipi = this.afterInspects.get(i);
				ipi.inspect(context, mo, this, ao, value,
						ModelInvokeStage.AFTER);
			}
		}
	}

	final void callFinallyInspects(ContextImpl<?, ?, ?> context, Object mo,
			Object ao, Object value) {
		if (this.finallyInspects != null) {
			for (int i = 0, c = this.finallyInspects.size(); i < c; i++) {
				InspectPointImpl ipi = this.finallyInspects.get(i);
				ipi.inspect(context, mo, this, ao, value,
						ModelInvokeStage.FINALLY);
			}
		}
	}

	public ModelInvokeDefineImpl(ModelDefineImpl owner, String name,
			Class<?> aoClass) {
		super(name, aoClass);
		if (owner == null || aoClass == null) {
			throw new NullPointerException();
		}
		this.owner = owner;
	}

	public final ModelDefineImpl getOwner() {
		return this.owner;
	}

	public final boolean isAuthorizable() {
		return this.isAuthorizable;
	}

	public final void setAuthorizable(boolean value) {
		this.checkModifiable();
		this.isAuthorizable = value;
	}

	public final MetaBaseContainerImpl<InspectPointImpl> getAfterInspects() {
		if (this.afterInspects == null) {
			this.afterInspects = new MetaBaseContainerImpl<InspectPointImpl>();
		}
		return this.afterInspects;
	}

	public final MetaBaseContainerImpl<InspectPointImpl> getBeforeInspects() {
		if (this.beforeInspects == null) {
			this.beforeInspects = new MetaBaseContainerImpl<InspectPointImpl>();
		}
		return this.beforeInspects;
	}

	public final MetaBaseContainerImpl<InspectPointImpl> getFinallyInspects() {
		if (this.beforeInspects == null) {
			this.beforeInspects = new MetaBaseContainerImpl<InspectPointImpl>();
		}
		return this.beforeInspects;
	}

	public final InspectPointImpl newAfterInspect(ModelActionDefine a) {
		ModelActionDefineImpl action = (ModelActionDefineImpl) a;
		if (action == this || action.owner != this.owner) {
			throw new IllegalArgumentException();
		}
		InspectPointImpl point = new InspectPointImpl(action);
		this.getAfterInspects().add(point);
		return point;
	}

	public final InspectPointImpl newAfterInspect(ModelConstraintDefine c) {
		ModelConstraintDefineImpl constraint = (ModelConstraintDefineImpl) c;
		if (constraint.owner != this.owner) {
			throw new IllegalArgumentException();
		}
		InspectPointImpl point = new InspectPointImpl(constraint);
		this.getAfterInspects().add(point);
		return point;
	}

	public final InspectPointImpl newBeforeInspect(ModelActionDefine a) {
		ModelActionDefineImpl action = (ModelActionDefineImpl) a;
		if (action == this || action.owner != this.owner) {
			throw new IllegalArgumentException();
		}
		InspectPointImpl point = new InspectPointImpl(action);
		this.getBeforeInspects().add(point);
		return point;
	}

	public final InspectPoint newBeforeInspect(ModelConstraintDefine c) {
		ModelConstraintDefineImpl constraint = (ModelConstraintDefineImpl) c;
		if (constraint.owner != this.owner) {
			throw new IllegalArgumentException();
		}
		InspectPointImpl point = new InspectPointImpl(constraint);
		this.getBeforeInspects().add(point);
		return point;
	}

	public final InspectPointImpl newFinallyInspect(ModelActionDefine a) {
		ModelActionDefineImpl action = (ModelActionDefineImpl) a;
		if (action == this || action.owner != this.owner) {
			throw new IllegalArgumentException();
		}
		InspectPointImpl point = new InspectPointImpl(action);
		this.getFinallyInspects().add(point);
		return point;
	}

	public ModelInvokeValidity getValidity(Context context, Object mo) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	// /////////////////////////////////
	// //////XML
	// /////////////////////////////////
	static final String xml_element_afterInspects = "afters";
	static final String xml_element_beforeInspects = "befores";
	static final String xml_element_finallyInspects = "finallys";

	@Override
	public void render(SXElement element) {
		super.render(element);
		if (this.beforeInspects != null) {
			this.beforeInspects.renderInto(element, xml_element_beforeInspects,
					0);
		}
		if (this.afterInspects != null) {
			this.afterInspects
					.renderInto(element, xml_element_afterInspects, 0);
		}
		if (this.finallyInspects != null) {
			this.finallyInspects.renderInto(element,
					xml_element_finallyInspects, 0);
		}
	}

	@Override
	void merge(SXElement element, SXMergeHelper helper) {
		super.merge(element, helper);
		SXElement inspect = element.firstChild(xml_element_afterInspects,
				InspectPointImpl.xml_element_inspectpoint);
		if (inspect != null) {
			InspectPointImpl.delayLoadInspectPoint(this.owner, this
					.getAfterInspects(), inspect, helper);
		}
		inspect = element.firstChild(xml_element_beforeInspects,
				InspectPointImpl.xml_element_inspectpoint);
		if (inspect != null) {
			InspectPointImpl.delayLoadInspectPoint(this.owner, this
					.getBeforeInspects(), inspect, helper);
		}
		inspect = element.firstChild(xml_element_finallyInspects,
				InspectPointImpl.xml_element_inspectpoint);
		if (inspect != null) {
			InspectPointImpl.delayLoadInspectPoint(this.owner, this
					.getFinallyInspects(), inspect, helper);
		}
	}

}
