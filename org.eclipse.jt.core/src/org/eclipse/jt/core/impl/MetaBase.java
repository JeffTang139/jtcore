package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXRenderable;

/**
 * 元数据基类
 * 
 * @author Jeff Tang
 * 
 */
public abstract class MetaBase implements SXRenderable {
	/**
	 * 返回描述
	 */
	abstract String getDescription();

	/**
	 * 返回当前节点的XML标记名称
	 * 
	 * @return 返回当前节点的XML标记名称
	 */
	public abstract String getXMLTagName();

	/**
	 * 子类重载该方法将定义写入XML
	 * 
	 * @param element
	 *            当前节点元素
	 */
	public void render(SXElement element) {
		// do nothing
	}

	/**
	 * 将该节点的XML插入到指定的元素内部
	 * 
	 * @param parent
	 *            父节点
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
