package org.eclipse.jt.core.resource;

import org.eclipse.jt.core.auth.Operation;

/**
 * 资源设置器接口。
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            资源外观，即资源实现提供的只读接口
 * @param <TImpl>
 *            资源实现类型，既可以用来修改资源的接口或者类型，大部分时候使用资源的实现类型
 * @param <TKeysHolder>
 *            资源键源，既可以从中得到资源的键的值的接口或者类型，大部分时候使用资源的实现类型
 */
public interface ResourcePutter<TFacade, TImpl extends TFacade, TKeysHolder> {
	/**
	 * 获得资源类别
	 */
	public Object getCategory();

	/**
	 * 将资源对象设置到资源服务中。
	 * <p>
	 * 这里默认资源的实现对象<code>resource</code>同时也实现了<code>TFacade</code>和
	 * <code>TKeysHolder</code>类型。
	 * <p>
	 * 如果与对象<code>resource</code>含有相同键的同类型资源已经存在于资源服务中，这里会把原存在对象覆盖。
	 * 
	 * @param resource
	 *            待设置的资源的实现对象
	 * @return 返回设置的资源对象在资源服务中对应的标记
	 */
	public ResourceToken<TFacade> putResource(TImpl resource);

	/**
	 * 将资源对象设置到资源服务中。
	 * <p>
	 * 这里默认资源的实现对象<code>resource</code>同时也实现了<code>TFacade</code>接口类型。
	 * <p>
	 * 如果在资源服务中已经存在与键组<code>keys</code>对应的资源对象，这里会把原存在对象覆盖。
	 * 
	 * @param resource
	 *            待设置的资源的实现对象
	 * @param keys
	 *            待设置的资源对象对应的键组
	 * @return 返回设置的资源对象在资源服务中对应的标记
	 */
	public ResourceToken<TFacade> putResource(TImpl resource, TKeysHolder keys);

	/**
	 * 将资源对象设置到资源服务中，同时指定其父结点对象在资源服务中的标记，返回新添加的对象在资源服务中的标记。
	 * <p>
	 * 将TImpl类型的资源<code>resource</code>对象添加到资源服务中时，这里默认对象<code>resource</code>
	 * 同时也实现了<code>TFacade</code>和<code>TKeysHolder</code>类型。
	 * <p>
	 * 如果与对象<code>resource</code>含有相同键的同类型资源已经存在于资源服务中，这里会把原存在对象覆盖。
	 * 并且，如果原存在的对象的父节点如果不是指定的<code>treeParent</code>，这里也会把<code>resource</code>
	 * 重新置为<code>treeParent</code>的子节点。
	 * 
	 * @param parentToken
	 *            待设置的资源对象在资源服务中的父节点的标记
	 * @param resource
	 *            待设置的资源的实现对象
	 * @return 返回设置的资源对象在资源服务中对应的标记
	 */
	public ResourceToken<TFacade> putResource(
			ResourceToken<TFacade> treeParent, TImpl resource);

	/**
	 * 将资源对象设置到资源服务中，同时指定其对应的键组以及父结点对象在资源服务中的标记，返回新添加的对象在资源服务中的标记。
	 * <p>
	 * 将TImpl类型的资源<code>resource</code>对象添加到资源服务中时，这里默认对象<code>resource</code>
	 * 同时也实现了<code>TFacade</code>接口类型。
	 * <p>
	 * 如果在资源服务中已经存在与键组<code>keys</code>对应的资源对象，这里会把原存在对象覆盖。
	 * 并且，如果原存在的对象的父节点如果不是指定的<code>treeParent</code>，这里也会把<code>resource</code>
	 * 重新置为<code>treeParent</code>的子节点。
	 * 
	 * @param parentToken
	 *            待设置的资源对象在资源服务中的父节点的标记
	 * @param resource
	 *            待设置的资源的实现对象
	 * @param keys
	 *            待设置的资源对象对应的键组
	 * @return 返回设置的资源对象在资源服务中对应的标记
	 */
	public ResourceToken<TFacade> putResource(
			ResourceToken<TFacade> treeParent, TImpl resource, TKeysHolder keys);

	/**
	 * 设置资源对象在资源服务中的树形结构上的父子关系。
	 * <p>
	 * 这个操作会把指定的资源对象<code>child</code>设置为指定的父节点<code>treeParent</code>的子节点。
	 * <p>
	 * 如果资源对象<code>child</code>在树形结构中已经有父节点，且不是<code>treeParent</code>，则把
	 * <code>child</code>移动到新指定的<code>treeParent</code>节点下。
	 * 
	 * @param treeParent
	 *            新的父节点的资源标记
	 * @param child
	 *            要设置的子节点的资源标记
	 */
	void putResource(ResourceToken<TFacade> treeParent,
			ResourceToken<TFacade> child);

	/**
	 * 设置资源服务中的资源对象之间的引用关系。
	 * <p>
	 * 这个操作会把资源对象<code>reference</code>放到指定资源对象<code>holder</code>中。
	 * <p>
	 * 这个操作不会影响<code>reference</code>的其它设置，也就是说，如果<code>reference</code>
	 * 已经存在于其它holder中，这里也不会将<code>reference</code>从那些holder中移除。
	 * 
	 * @param <THolderFacade>
	 *            保持<code>reference</code>引用的资源的外观类型
	 * @param holder
	 *            引用的保持者
	 * @param reference
	 *            引用对象
	 */
	<THolderFacade> void putResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<THolderFacade> void putResourceReferenceBy(ResourceToken<TFacade> holder,
			ResourceToken<THolderFacade> reference);

	/**
	 * 移除资源服务中的资源对象之间的引用关系。
	 * <p>
	 * 这个操作只解除资源对象之间的引用关系，并不从资源服务中删除资源对象。
	 * 
	 * @param <THolderFacade>
	 *            保持<code>reference</code>引用的资源的外观类型
	 * @param holder
	 *            引用的保持者
	 * @param reference
	 *            引用对象
	 */
	<THolderFacade> void removeResourceReference(
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<TReferenceFacade> void removeResourceReferenceBy(
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference);

	// -----------------------------------------以下为权限相关---------------------------------------------------

	/**
	 * 移除资源服务中的资源对象之间的引用关系。
	 * <p>
	 * 这个操作只解除资源对象之间的引用关系，并不从资源服务中删除资源对象。
	 * 
	 * @param operation
	 *            要求具有权限的操作
	 * @param <THolderFacade>
	 *            保持<code>reference</code>引用的资源的外观类型
	 * @param holder
	 *            引用的保持者
	 * @param reference
	 *            引用对象
	 */
	<THolderFacade> void removeResourceReference(
			Operation<? super TFacade> operation,
			ResourceToken<THolderFacade> holder,
			ResourceToken<TFacade> reference);

	<TReferenceFacade> void removeResourceReferenceBy(
			Operation<? super TReferenceFacade> operation,
			ResourceToken<TFacade> holder,
			ResourceToken<TReferenceFacade> reference);

	// -----------------------------------------以上为权限相关---------------------------------------------------

}