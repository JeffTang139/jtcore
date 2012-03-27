package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 获取指定角色的角色分配信息的键<br>
 * 返回继承了指定角色的用户列表。
 * 
 * <pre>
 * 使用示例：
 * key = new GetRoleAssignInfoForRoleKey(roleID);
 * context.getList(User.class, key);
 * </pre>
 * 
 * @author Jeff Tang 2009-11
 */
public final class GetRoleAssignInfoForRoleKey {

	/**
	 * 角色ID
	 */
	public final GUID roleID;

	/**
	 * 新建获取指定角色的角色分配信息的键
	 * 
	 * @param roleID
	 *            角色ID，不能为空
	 */
	public GetRoleAssignInfoForRoleKey(GUID roleID) {
		if (roleID == null) {
			throw new NullArgumentException("roleID");
		}
		this.roleID = roleID;
	}

}
