package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * ��ȡָ����ɫ�Ľ�ɫ������Ϣ�ļ�<br>
 * ���ؼ̳���ָ����ɫ���û��б�
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetRoleAssignInfoForRoleKey(roleID);
 * context.getList(User.class, key);
 * </pre>
 * 
 * @author Jeff Tang 2009-11
 */
public final class GetRoleAssignInfoForRoleKey {

	/**
	 * ��ɫID
	 */
	public final GUID roleID;

	/**
	 * �½���ȡָ����ɫ�Ľ�ɫ������Ϣ�ļ�
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 */
	public GetRoleAssignInfoForRoleKey(GUID roleID) {
		if (roleID == null) {
			throw new NullArgumentException("roleID");
		}
		this.roleID = roleID;
	}

}
