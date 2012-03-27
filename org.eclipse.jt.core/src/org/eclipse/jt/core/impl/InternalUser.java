package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * �����û���ϵͳ�����û�
 * 
 * @see org.eclipse.jt.core.User
 * @see org.eclipse.jt.core.impl.InternalActor
 * @author Jeff Tang
 */
public class InternalUser extends InternalActor implements IInternalUser {

	/**
	 * �����û�
	 */
	public final static InternalUser anonymUser = new InternalUser(
			GUID.emptyID, USER_NAME_ANONYM, "����", "�������������û��������û�", false);

	/**
	 * ϵͳ�û�
	 */
	public final static InternalUser system = new InternalUser(GUID.valueOf(1,
			0), "system", "ϵͳ", "����ϵͳ��Ϊ�������û�", true);

	/**
	 * �����û�
	 */
	public final static InternalUser debugger = new InternalUser(GUID.valueOf(
			2, 0), USER_NAME_DEBUGGER, "�����û��˺�", "����״̬��ʹ�õ��û��˺�", true) {

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
	 * ����һ�������û���ϵͳ�����û�
	 * 
	 * @param id
	 *            �û�ID
	 * @param name
	 *            �û�����
	 * @param title
	 *            �û�����
	 * @param description
	 *            �û�������Ϣ
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
