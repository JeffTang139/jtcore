package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.User;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.def.table.AsTableField;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 用户实体定义<br>
 * 用户实体定义对应于用户物理表的存储结构。
 * 
 * <pre>
 * 列名                类型        空否
 * RECID            GUID       否
 * name             String     否
 * title            String     否
 * state            ActorState 否
 * description      String     是
 * password         GUID       否
 * priorityInfo     int        否
 * </pre>
 * 
 * @see org.eclipse.jt.core.impl.IInternalUser
 * @see org.eclipse.jt.core.impl.CoreAuthActorEntity
 * @author Jeff Tang 2009-12
 */
@StructClass
final class CoreAuthUserEntity extends CoreAuthActorEntity implements User {

	/**
	 * 用户登陆密码，经过MD5加密
	 */
	@AsTableField(isRequired = true)
	public GUID password;

	/**
	 * 用户优先级信息<br>
	 * 前16位存储用户所继承的角色数，后16位存储用户的优先级索引号。
	 */
	@AsTableField(isRequired = true)
	public int priorityInfo;

	/**
	 * 密码字符串长度定义
	 */
	static final int PASSWORD_FIELD_SIZE = 32;

	public final boolean validatePassword(String password) {
		if (password == null) {
			throw new NullArgumentException("password");
		}
		if (GUID.MD5Of(password).equals(this.password)) {
			return true;
		}
		return false;
	}

	public final boolean validatePassword(GUID password) {
		if (password == null) {
			throw new NullArgumentException("password");
		}
		if (password.equals(this.password)) {
			return true;
		}
		return false;
	}

	public final int getPriorityIndex() {
		return this.priorityInfo & PRIORITY_INFO_MASK;
	}

	public final int getAssignedRoleCount() {
		return this.priorityInfo >>> PRIORITY_ROLE_COUNT_SHIFT;
	}

	public final Role getAssignedRole(int index) {
		throw new UnsupportedOperationException();
	}

	final void setPriorityInfo(int assignedRoleCount, int priorityIndex) {
		if (assignedRoleCount <= 0) {
			priorityIndex = 0;
		} else {
			if (priorityIndex < 0) {
				priorityIndex = 0;
			} else if (assignedRoleCount <= priorityIndex) {
				priorityIndex = assignedRoleCount - 1;
			}
		}
		this.priorityInfo = ((assignedRoleCount & PRIORITY_INFO_MASK) << PRIORITY_ROLE_COUNT_SHIFT)
				| (priorityIndex & PRIORITY_INFO_MASK);
		this.roleAssignInfoChanged = true;
	}

	final boolean roleAssignInfoChanged() {
		return this.roleAssignInfoChanged;
	}

	final void resetRoleAssignInfoChanged() {
		this.roleAssignInfoChanged = false;
	}

	private boolean roleAssignInfoChanged = true;

	private static final int PRIORITY_ROLE_COUNT_SHIFT = 16;

	private static final int PRIORITY_INFO_MASK = 0xFFFF;

}
