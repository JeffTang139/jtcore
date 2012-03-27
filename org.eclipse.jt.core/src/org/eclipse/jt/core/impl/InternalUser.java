package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 内置用户，系统保留用户
 * 
 * @see org.eclipse.jt.core.User
 * @see org.eclipse.jt.core.impl.InternalActor
 * @author Jeff Tang
 */
public class InternalUser extends InternalActor implements IInternalUser {

	/**
	 * 匿名用户
	 */
	public final static InternalUser anonymUser = new InternalUser(
			GUID.emptyID, USER_NAME_ANONYM, "匿名", "代表所有匿名用户的虚拟用户", false);

	/**
	 * 系统用户
	 */
	public final static InternalUser system = new InternalUser(GUID.valueOf(1,
			0), "system", "系统", "代表系统行为的虚拟用户", true);

	/**
	 * 测试用户
	 */
	public final static InternalUser debugger = new InternalUser(GUID.valueOf(
			2, 0), USER_NAME_DEBUGGER, "测试用户账号", "测试状态下使用的用户账号", true) {

		{
			this.state = Boolean.getBoolean("org.eclipse.jt.debug") ? ActorState.NORMAL
					: ActorState.DISABLE;
		}

		private final ActorState state;

		@Override
		public final ActorState getState() {
			return this.state;
		}

	};

	public final boolean validatePassword(String password) {
		if (password == null) {
			throw new NullArgumentException("password");
		}
		return true;
	}

	public final boolean validatePassword(GUID password) {
		if (password == null) {
			throw new NullArgumentException("password");
		}
		return true;
	}

	public final Role getAssignedRole(int index) {
		return null;
	}

	public final int getAssignedRoleCount() {
		return 0;
	}

	public final int getPriorityIndex() {
		return 0;
	}

	public final long[] getACL(ContextImpl<?, ?, ?> context) {
		return EMPTY_ACL;
	}

	public final long[][] getRoleACLs(ContextImpl<?, ?, ?> context) {
		return EMPTY_ROLE_ACLS;
	}

	private static final long[] EMPTY_ACL = new long[] {};

	private static final long[][] EMPTY_ROLE_ACLS = new long[][] {};

	/**
	 * 构造一个内置用户，系统保留用户
	 * 
	 * @param id
	 *            用户ID
	 * @param name
	 *            用户名称
	 * @param title
	 *            用户标题
	 * @param description
	 *            用户描述信息
	 */
	private InternalUser(GUID id, String name, String title,
			String description, boolean authority) {
		super(id, name, title, description);
		this.authority = authority;
	}

	public final boolean isBuildInUser() {
		return true;
	}

	private final boolean authority;

	final boolean getAuthority() {
		return this.authority;
	}

}
