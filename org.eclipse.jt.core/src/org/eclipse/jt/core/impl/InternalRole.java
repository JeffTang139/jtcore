package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.GUID;

/**
 * ���ý�ɫ��ϵͳ������ɫ
 * 
 * @see org.eclipse.jt.core.auth.Role
 * @see org.eclipse.jt.core.impl.InternalActor
 * @author Jeff Tang 2009-12
 */
final class InternalRole extends InternalActor implements IInternalRole {

	/**
	 * ����һ�����ý�ɫ��ϵͳ������ɫ
	 * 
	 * @param id
	 *            ��ɫID
	 * @param name
	 *            ��ɫ����
	 * @param title
	 *            ��ɫ����
	 * @param description
	 *            ��ɫ������Ϣ
	 */
	private InternalRole(GUID id, String name, String title, String description) {
		super(id, name, title, description);
	}

}
