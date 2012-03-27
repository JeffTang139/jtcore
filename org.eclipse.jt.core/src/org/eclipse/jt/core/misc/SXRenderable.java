package org.eclipse.jt.core.misc;

/**
 * 可渲染成XML对象的接口
 * 
 * @author Jeff Tang
 * 
 */
public interface SXRenderable {
	/**
	 * 返回当前节点的XML标记名称
	 * 
	 * @return 返回当前节点的XML标记名称
	 */
	public String getXMLTagName();

	/**
	 * 实现该方法将定义写入XML
	 * 
	 * @param usages
	 *            HCL
	 * @param element
	 *            当前节点元素
	 */
	public void render(SXElement element);
}
