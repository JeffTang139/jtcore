package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.DeclareBase;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;

/**
 * 定义实现类
 * 
 * @author Jeff Tang
 * 
 */
public abstract class DefineBaseImpl extends MetaBase implements DeclareBase {

	@Override
	public final String getDescription() {
		return this.description;
	}

	public final void setDescription(String description) {
		this.checkModifiable();
		this.description = Utils.noneNull(description);
	}

	static final String xml_attr_description = "description";

	/**
	 * 定义描述
	 */
	String description;

	DefineBaseImpl() {
		this.description = "";
	}

	/**
	 * 根据例子去构造的构造函数
	 * 
	 * @param sample
	 */
	DefineBaseImpl(DefineBaseImpl sample) {
		super(sample);
		this.description = sample.description;
	}

	DefineBaseImpl(SXElement element) {
		super(element);
		this.description = element.getAttribute(xml_attr_description,
				this.description);
	}

	/**
	 * 检查当前定义是否在可被修改的状态下，否则抛出异常
	 */
	final void checkModifiable() throws IllegalStateException {
		// FIXME
	}

	@Override
	public void render(SXElement element) {
		super.render(element);
		render(this, element);
	}

	static final void render(DefineBaseImpl define, SXElement element) {
		if (define.description != null && define.description.length() > 0) {
			element.setAttribute(xml_attr_description, define.description);
		}
	}

	void merge(SXElement element, SXMergeHelper helper) {
		this.description = element.getAttribute(xml_attr_description,
				this.description);
	}

	static final void merge(DefineBaseImpl define, SXElement element) {
		define.description = element.getAttribute(xml_attr_description,
				define.description);
	}

	void assignFrom(Object sample) {
		this.description = ((DefineBaseImpl) sample).description;
	}

}
