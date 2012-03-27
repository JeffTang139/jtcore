package org.eclipse.jt.core.auth;

/**
 * 角色资源操作<br>
 * 该枚举类中定义了能够对角色资源进行的操作。
 * 
 * <pre>
 * ACCESS:           访问操作
 * UPDATE_BASE_INFO: 修改基本信息操作
 * UPDATE_AUTHORITY: 修改授权操作
 * DELETE:           删除操作
 * ASSIGN:           分配操作
 * </pre>
 * 
 * @see org.eclipse.jt.core.auth.Operation
 * @author Jeff Tang 2009-12
 */
public enum RoleOperation implements Operation<Role> {

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
	},

	MODIFY {
		public int getMask() {
			return 1 << 2;
		}

		public String getTitle() {
			return "修改";
		}
	},

	DELETE {
		public int getMask() {
			return 1 << 3;
		}

		public String getTitle() {
			return "删除";
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
	// return "修改信息";
	// }
	// },
	//	
	// /**
	// * 修改权限操作
	// */
	// UPDATE_AUTHORITY {
	// public int getMask() {
	// return 1 << 2;
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
	// return 1 << 3;
	// }
	//
	// public String getTitle() {
	// return "删除";
	// }
	// },
	//	
	// /**
	// * 分配操作
	// */
	// ASSIGN {
	// public int getMask() {
	// return 1 << 4;
	// }
	//
	// public String getTitle() {
	// return "分配";
	// }
	// }

}
