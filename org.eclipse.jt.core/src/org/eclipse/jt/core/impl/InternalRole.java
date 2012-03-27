package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.GUID;

/**
 * 内置角色，系统保留角色
 * 
 * @see org.eclipse.jt.core.auth.Role
 * @see org.eclipse.jt.core.impl.InternalActor
 * @author Jeff Tang 2009-12
 */
final class InternalRole extends InternalActor implements IInternalRole {

	/**
	 * 构造一个内置角色，系统保留角色
	 * 
	 * @param id
	 *            角色ID
	 * @param name
	 *            角色名称
	 * @param title
	 *            角色标题
	 * @param description
	 *            角色描述信息
	 */
	private InternalRole(GUID id, String name, String title, String description) {
		super(id, name, title, description);
	}

}
