package org.eclipse.jt.core.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * 可授权资源项<br>
 * 在进行权限管理时使用，用户通过获取可授权资源项列表，得到已有的授权信息，再通过修改提交可授权资源项，修改授权。
 * 
 * @author Jeff Tang 2009-11
 */
public interface AuthorizedResourceItem {

	public GUID getID();
	
	/**
	 * 获取可授权资源项标题
	 * 
	 * @return 返回可授权资源项标题
	 */
	public String getTitle();

	/**
	 * 获取可授权资源项中对指定操作的授权信息
	 * 
	 * @param operation
	 *            指定操作
	 * @return 返回可授权资源项中对指定操作的授权信息
	 */
	public Authority getAuthority(Operation<?> operation);

	/**
	 * 设置可授权资源项对指定操作的授权
	 * 
	 * @param operation
	 *            指定操作
	 * @param authority
	 *            授权信息
	 */
	public void setAuthority(Operation<?> operation, Authority authority);

}
