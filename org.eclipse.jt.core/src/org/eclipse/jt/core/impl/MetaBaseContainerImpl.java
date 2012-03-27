package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.misc.SXElement;

/**
 * ����ʵ����
 * 
 * @author Jeff Tang
 * 
 * @param <TDefine>
 */
class MetaBaseContainerImpl<TDefine extends MetaBase> extends
		ContainerImpl<TDefine> {

	private static final long serialVersionUID = 2085965861111706141L;

	public MetaBaseContainerImpl(ContainerListener listener) {
		super(listener);
	}

	public MetaBaseContainerImpl() {
		super();
	}

	// ////////////////////////////////////////
	// /////// XML
	// /////////////////////////////////////////

	/**
	 * ����ǰ�б��Ԫ��ֱ��д��Ŀ��ڵ���
	 * 
	 * @param parent
	 *            ����Element�ڵ�
	 * @param offset
	 *            ����Ŀ�ʼλ��
	 */
	final void renderInto(SXElement parent, int offset) {
		int size = this.size();
		if (offset < size) {
			for (int i = offset; i < size; i++) {
				TDefine e = this.get(i);
				e.render(parent.append(e.getXMLTagName()));
			}
		}
	}

	final void renderInto(SXElement parent, String intoTagName) {
		this.renderInto(parent, intoTagName, 0);
	}

	/**
	 * ����ǰ�б��Ԫ��д��Ŀ��ڵ��ָ���ӽڵ���
	 * 
	 * @param parent
	 *            ����Element�ڵ�
	 * @param intoTagName
	 *            �ڸ����ڵ�����Ҫд��Ľڵ������
	 * @param offset
	 *            ����Ŀ�ʼλ��
	 */
	final void renderInto(SXElement parent, String intoTagName, int offset) {
		int size = this.size();
		if (offset < size) {
			SXElement into = parent.append(intoTagName);
			for (int i = offset; i < size; i++) {
				TDefine e = this.get(i);
				e.render(into.append(e.getXMLTagName()));
			}
		}
	}

	final void renderInto(SXElement parent, String intoTagName, String tagName,
			int offset) {
		int size = this.size();
		if (offset < size) {
			SXElement into = parent.append(intoTagName);
			for (int i = offset; i < size; i++) {
				TDefine e = this.get(i);
				e.render(into.append(tagName));
			}
		}
	}

	/**
	 * ʹ��Ԫ���ݹ�����������������Ԫ����render�����ڵ��ָ���ڵ���
	 * 
	 * @param parent
	 * @param intoTagName
	 * @param filter
	 */
	final void renderInto(SXElement parent, String intoTagName,
			Filter<TDefine> filter) {
		int size = this.size();
		SXElement into = null;
		for (int i = 0; i < size; i++) {
			TDefine e = this.get(i);
			if (filter.accept(e)) {
				if (into == null) {
					into = parent.append(intoTagName);
				}
				e.render(into.append(e.getXMLTagName()));
			}
		}
	}

	final void ensureElementAt(TDefine define, int at) {
		int old = this.indexOf(define);
		if (old != at) {
			this.move(old, at);
		}
	}

}
