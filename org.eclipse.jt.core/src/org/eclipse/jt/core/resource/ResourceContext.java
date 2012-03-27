/**
 * 
 */
package org.eclipse.jt.core.resource;

import org.eclipse.jt.core.Context;

public interface ResourceContext<TFacade, TImpl extends TFacade, TKeysHolder>
        extends Context, ResourceModifier<TFacade, TImpl, TKeysHolder> {

	/**
	 * 从THolderFacade类型的资源中移除TFacade类型的资源引用。
	 * 
	 * 如果参数<code>absolutely</code>的值为 <code>false</code>，则只解除引用关系，
	 * 而不删除TFacade类型的资源。否则，如果参数 <code>absolutely</code>的值为<code>true</code>，那么，
	 * 在解除引用关系的同时，也会从根本上删除TFacade类型的资源。
	 * 
	 * 这个操作不会删除THolderFacade类型的资源。
	 * 
	 * @param <THolderFacade>
	 * @param holder
	 * @param absolutely
	 *            是否在解除引用关系时，彻底删除引用资源。
	 */
	public <THolderFacade> void clearResourceReferences(
	        ResourceToken<THolderFacade> holder, boolean absolutely);

	/**
	 * 返回一个在指定分类中维护资源的管理对象，系统默认的category为None.NONE
	 * 
	 * @param category
	 *            指定的资源分类
	 * @return
	 */
	public CategorialResourceModifier<TFacade, TImpl, TKeysHolder> usingResourceCategory(
	        Object category);
}
