package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.misc.SXElement;

/**
 * 容器实现类
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
	 * 将当前列表各元素直接写到目标节点下
	 * 
	 * @param parent
	 *            父极Element节点
	 * @param offset
	 *            子项的开始位置
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
	 * 将当前列表各元素写到目标节点的指定子节点下
	 * 
	 * @param parent
	 *            父极Element节点
	 * @param intoTagName
	 *            在父级节点下需要写入的节点的名称
	 * @param offset
	 *            子项的开始位置
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
	 * 使用元数据过滤器将满足条件的元数据render到父节点的指定节点下
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
