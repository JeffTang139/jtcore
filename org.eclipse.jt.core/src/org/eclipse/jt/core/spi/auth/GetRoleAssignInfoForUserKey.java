package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 获取指定用户角色分配信息的键<br>
 * 返回的是指定包含用户自身的和用户所继承的角色列表。按优先级顺序进行排序，用户自身也参与排序。
 * 
 * <pre>
 * 使用示例：
 * key = new GetRoleAssignInfoForUserKey(userID);
 * context.getList(Actor.class, key);
 * </pre>
 * 
 * @author Jeff Tang 2009-11
 */
public final class GetRoleAssignInfoForUserKey {

	/**
	 * 用户ID
	 */
	public final GUID userID;

	/**
	 * 新建获取指定用户角色分配信息的键
	 * 
	 * @param userID
	 *            用户ID，不能为空
	 */
	public GetRoleAssignInfoForUserKey(GUID userID) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
	}

}
