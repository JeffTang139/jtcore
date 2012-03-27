package org.eclipse.jt.core.resource;

/**
 * 可设置类别的资源请求器
 * 
 * @author Jeff Tang
 * 
 */
public interface CategorialResourceQuerier extends ResourceQuerier {
	/**
	 * 设置资源的类别
	 */
	public void setCategory(Object category);
}
