package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.AuthorizedResourceCategoryItem;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.type.GUID;


/**
 * 可授权资源类别项实现类<br>
 * 可授权资源类别项是一个特殊的可授权资源项，是系统为每类可授权资源定义的虚拟的根资源项。
 * 
 * @see org.eclipse.jt.core.auth.AuthorizedResourceCategoryItem
 * @see org.eclipse.jt.core.impl.AuthResItemImpl
 * @author Jeff Tang 2009-12
 */
final class AuthResCategoryItemImpl extends AuthResItemImpl implements
		AuthorizedResourceCategoryItem {

	public final GUID getResourceCategoryID() {
		return this.itemGUID;
	}

	public final Operation<?>[] getResourceOperations() {
		return this.operations;
	}
	
	/**
	 * 构造一个可授权资源类别项
	 * 
	 * @param group
	 *            资源类对应的资源组
	 */
	AuthResCategoryItemImpl(ResourceGroup<?, ?, ?> group, int authCode) {
		super(group.id, group.groupID, group.categoryTitle, authCode);
		this.operations = group.resourceService.authorizableResourceProvider.operations;
	}

	/**
	 * 资源资源操作定义
	 */
	final Operation<?>[] operations;

}
