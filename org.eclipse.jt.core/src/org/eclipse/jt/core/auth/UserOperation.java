package org.eclipse.jt.core.auth;

import org.eclipse.jt.core.User;

/**
 * 用户资源操作<br>
 * 该枚举类中定义了能够对用户资源进行的操作。
 * 
 * <pre>
 * ACCESS:             访问操作
 * UPDATE_BASE_INFO:   修改基本信息操作
 * UPDATE_PASSWORD:    修改密码操作
 * UPDATE_ROLE_ASSIGN: 修改角色分配操作
 * UPDATE_AUTHORITY:   修改授权操作
 * DELETE:             删除操作
 * </pre>
 * 
 * @see org.eclipse.jt.core.auth.Operation
 * @author Jeff Tang 2009-12
 */
public enum UserOperation implements Operation<User> {

	/**
	 * 访问操作
	 */
	ACCESS {
		public int getMask() {
			return 1 << 0;
		}

		public String getTitle() {
			return "访问";
		}
	}
	// ,
	//
	// /**
	// * 修改基本信息操作
	// */
	// UPDATE_BASE_INFO {
	// public int getMask() {
	// return 1 << 1;
	// }
	//
	// public String getTitle() {
	// return "修改基本信息";
	// }
	// },
	//
	// /**
	// * 修改密码操作
	// */
	// UPDATE_PASSWORD {
	// public int getMask() {
	// return 1 << 2;
	// }
	//
	// public String getTitle() {
	// return "修改密码";
	// }
	// },
	//
	// /**
	// * 修改角色分配操作
	// */
	// UPDATE_ROLE_ASSIGN {
	// public int getMask() {
	// return 1 << 3;
	// }
	//
	// public String getTitle() {
	// return "修改角色分配";
	// }
	// },
	//
	// /**
	// * 修改权限操作
	// */
	// UPDATE_AUTHORITY {
	// public int getMask() {
	// return 1 << 4;
	// }
	//
	// public String getTitle() {
	// return "修改权限";
	// }
	// },
	//
	// /**
	// * 删除操作
	// */
	// DELETE {
	// public int getMask() {
	// return 1 << 5;
	// }
	//
	// public String getTitle() {
	// return "删除";
	// }
	// }

}
