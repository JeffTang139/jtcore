package org.eclipse.jt.core.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * 可授权资源类别项<br>
 * 可授权资源类别项是一种特殊的可授权资源项。D&A框架为每个可授权资源分类定义了一个虚的根资源项，
 * 即为可授权资源类别项。通过该对象，除了可以获取基本的可授权资源项信息外，还可以获取该类资源的类别ID、操作定义等资源类别相关信息。
 * 
 * @see org.eclipse.jt.core.auth.AuthorizedResourceItem
 * @author Jeff Tang 2009-11
 */
public interface AuthorizedResourceCategoryItem extends AuthorizedResourceItem {

	/**
	 * 获取可授权资源类别ID
	 * @return 返回可授权资源类别ID（GUID）
	 */
	@Deprecated
	public GUID getResourceCategoryID();

	/**
	 * 获取可授权资源类别的操作定义
	 * @return 返回可授权资源类别的操作定义数组
	 */
	public Operation<?>[] getResourceOperations();

}
