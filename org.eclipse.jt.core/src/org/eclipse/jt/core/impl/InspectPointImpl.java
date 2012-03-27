package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.model.InspectPoint;
import org.eclipse.jt.core.def.model.ModelInvokeStage;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeDelayAction;
import org.eclipse.jt.core.misc.SXMergeHelper;

/**
 * 触发点实现类
 * 
 * @author Jeff Tang
 * 
 */
final class InspectPointImpl extends MetaBase implements InspectPoint {
	private final ModelActionDefineImpl action;
	private final ModelConstraintDefineImpl constraint;

	final static void delayLoadInspectPoint(ModelDefineImpl model,
	        final MetaBaseContainerImpl<InspectPointImpl> container,
	        final SXElement first, SXMergeHelper helper) {
		helper.addDelayAction(model, new SXMergeDelayAction<ModelDefineImpl>() {
			public void doAction(ModelDefineImpl at, SXMergeHelper helper,
			        SXElement atElement) {
				for (SXElement element = first; element != null; element = element
				        .nextSibling(InspectPointImpl.xml_element_inspectpoint)) {
					String actionName = element.getAttribute(
					        InspectPointImpl.xml_attr_action, null);
					ModelActionDefineImpl action;
					ModelConstraintDefineImpl constraint;
					if (actionName != null) {
						action = at.actions.get(actionName);
						constraint = null;
					} else {
						constraint = at.constraints.get(element,
						        InspectPointImpl.xml_attr_constraint);
						action = null;
					}
					for (int i = 0, c = container.size(); i < c; i++) {
						InspectPointImpl p = container.get(i);
						if (p.action == action || p.constraint == constraint) {
							return;
						}
					}
					InspectPointImpl p = new InspectPointImpl(action,
					        constraint);
					container.add(p);
				}
			}
		});
	}

	private InspectPointImpl(ModelActionDefineImpl action,
	        ModelConstraintDefineImpl constraint) {
		this.action = action;
		this.constraint = constraint;
	}

	InspectPointImpl(ModelActionDefineImpl action) {
		if (action == null) {
			throw new NullPointerException();
		}
		this.action = action;
		this.constraint = null;
	}

	InspectPointImpl(ModelConstraintDefineImpl constraint) {
		if (constraint == null) {
			throw new NullPointerException();
		}
		this.constraint = constraint;
		this.action = null;
	}

	final void inspect(ContextImpl<?, ?, ?> context, Object mo,
	        ModelInvokeDefineImpl trigger, Object triggerAO, Object value,
	        ModelInvokeStage stage) {
		if (this.action != null) {
			this.action.internalExecute(context, mo, null, trigger, triggerAO,
			        value, stage);
		} else {
			this.constraint.internalCheck(context, mo, trigger, triggerAO,
			        value, stage);
		}
	}

	public final ModelActionDefineImpl asAction() {
		return this.action;
	}

	public final ModelConstraintDefineImpl asConstraint() {
		return this.constraint;
	}

	@Override
	final String getDescription() {
		return "触发点";
	}

	// ////////////////////////////////////////
	// /////// XML
	// /////////////////////////////////////////
	static final String xml_element_inspectpoint = "inspect";
	static final String xml_attr_action = "action";
	static final String xml_attr_constraint = "constraint";

	@Override
	public final String getXMLTagName() {
		return xml_element_inspectpoint;
	}

	@Override
	public void render(SXElement element) {
		super.render(element);
		if (this.action != null) {
			element.setAttribute(xml_attr_action, this.action.name);
		} else {
			element.setAttribute(xml_attr_constraint, this.constraint.name);
		}
	}
}
