package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.DeclareBase;
import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXMergeHelper;

/**
 * ����ʵ����
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
	 * ��������
	 */
	String description;

	DefineBaseImpl() {
		this.description = "";
	}

	/**
	 * ��������ȥ����Ĺ��캯��
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
	 * ��鵱ǰ�����Ƿ��ڿɱ��޸ĵ�״̬�£������׳��쳣
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
