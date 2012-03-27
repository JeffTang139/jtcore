package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.model.ModelReferenceDeclare;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 模型引用定义实现
 * 
 * @author Jeff Tang
 * 
 */
final class ModelReferenceDefineImpl extends NamedDefineImpl implements
        ModelReferenceDeclare {

	public ModelReferenceDefineImpl(ModelDefineImpl owner, String name,
	        ModelDefineImpl target) {
		super(name);
		if (target == null || owner == null) {
			throw new NullPointerException();
		}
		this.target = target;
		this.owner = owner;
	}

	public final ModelDefineImpl getTarget() {
		return this.target;
	}

	public final ModelDefineImpl getOwner() {
		return this.owner;
	}

	final ModelDefineImpl target;
	final ModelDefineImpl owner;
	// //////////////////
	// / XML
	// //////////////////
	static final String xml_element_reference = "reference";
	static final String xml_attr_target = "target";
	static final String xml_attr_target_author = "target-author";

	@Override
	public final String getXMLTagName() {
		return xml_element_reference;
	}

	@Override
	public final void render(SXElement element) {
		super.render(element);
		element.setString(xml_attr_target, this.target.name);
	}
}
