package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * ��ȡָ���û���ɫ������Ϣ�ļ�<br>
 * ���ص���ָ�������û�����ĺ��û����̳еĽ�ɫ�б������ȼ�˳����������û�����Ҳ��������
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetRoleAssignInfoForUserKey(userID);
 * context.getList(Actor.class, key);
 * </pre>
 * 
 * @author Jeff Tang 2009-11
 */
public final class GetRoleAssignInfoForUserKey {

	/**
	 * �û�ID
	 */
	public final GUID userID;

	/**
	 * �½���ȡָ���û���ɫ������Ϣ�ļ�
	 * 
	 * @param userID
	 *            �û�ID������Ϊ��
	 */
	public GetRoleAssignInfoForUserKey(GUID userID) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
	}

}
