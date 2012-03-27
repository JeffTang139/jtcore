package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXRenderable;

/**
 * Ԫ���ݻ���
 * 
 * @author Jeff Tang
 * 
 */
public abstract class MetaBase implements SXRenderable {
	/**
	 * ��������
	 */
	abstract String getDescription();

	/**
	 * ���ص�ǰ�ڵ��XML�������
	 * 
	 * @return ���ص�ǰ�ڵ��XML�������
	 */
	public abstract String getXMLTagName();

	/**
	 * �������ظ÷���������д��XML
	 * 
	 * @param element
	 *            ��ǰ�ڵ�Ԫ��
	 */
	public void render(SXElement element) {
		// do nothing
	}

	/**
	 * ���ýڵ��XML���뵽ָ����Ԫ���ڲ�
	 * 
	 * @param parent
	 *            ���ڵ�
	 */
	final void renderInto(SXElement parent) {
		this.render(parent.append(this.getXMLTagName()));
	}

	MetaBase(SXElement element) {
		// do nothing
	}

	MetaBase() {
		// do nothing
	}

	MetaBase(MetaBase sample) {
		// do nothing
	}

}
