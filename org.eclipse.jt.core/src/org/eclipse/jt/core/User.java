package org.eclipse.jt.core;

import org.eclipse.jt.core.auth.Actor;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.impl.InternalUser;
import org.eclipse.jt.core.type.GUID;


/**
 * 用户<br>
 * 在参与权限管理时，框架支持一个用户被关联多个组织机构，在不同的组织机构下，用户可以拥有不同的权限。<br>
 * 用户也可以被分配多个角色，用户继承所关联角色的所有权限。
 * 
 * @see org.eclipse.jt.core.auth.Actor
 * @author Jeff Tang
 */
public interface User extends Actor {
	/**
	 * 调试用户名，系统必须带有-Dorg.eclipse.jt.debug=true参数启动，该用户才可以使用。<br>
	 * 该用户接收任何密码，权限无穷大。
	 */
	public final static String USER_NAME_DEBUGGER = "debugger";

	/**
	 * 匿名用户名
	 */
	public final static String USER_NAME_ANONYM = "?";
	/**
	 * 匿名用户
	 */
	public final static User anonym = InternalUser.anonymUser;

	/**
	 * 调试用户名，系统必须带有-Dorg.eclipse.jt.debug=true参数启动，该用户才可以使用。<br>
	 * 该用户接收任何密码，权限无穷大。
	 */
	public final static User debugger = InternalUser.debugger;

	/**
	 * 验证用户密码<br>
	 * 判断给定的密码与用户密码是否匹配。
	 * 
	 * @param password
	 *            明文密码，不能为空对象
	 * @return 匹配返回true，否则返回false
	 */
	public boolean validatePassword(String password);

	public boolean validatePassword(GUID password);

	/**
	 * 获取用户已分配的角色数
	 * 
	 * @return 返回用户已分配的角色数
	 */
	@Deprecated
	public int getAssignedRoleCount();

	/**
	 * 根据索引号获取用户已分配的对应的角色
	 * 
	 * @return 返回用户已分配的对应的角色，返回空表示没有找到对应的角色
	 */
	@Deprecated
	public Role getAssignedRole(int index);

	/**
	 * 获取用户的优先级索引号
	 * 
	 * @return 返回用户的优先级索引号
	 */
	@Deprecated
	public int getPriorityIndex();

}
