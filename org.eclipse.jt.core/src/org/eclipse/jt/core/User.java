package org.eclipse.jt.core;

import org.eclipse.jt.core.auth.Actor;
import org.eclipse.jt.core.auth.Role;
import org.eclipse.jt.core.impl.InternalUser;
import org.eclipse.jt.core.type.GUID;


/**
 * �û�<br>
 * �ڲ���Ȩ�޹���ʱ�����֧��һ���û������������֯�������ڲ�ͬ����֯�����£��û�����ӵ�в�ͬ��Ȩ�ޡ�<br>
 * �û�Ҳ���Ա���������ɫ���û��̳���������ɫ������Ȩ�ޡ�
 * 
 * @see org.eclipse.jt.core.auth.Actor
 * @author Jeff Tang
 */
public interface User extends Actor {
	/**
	 * �����û�����ϵͳ�������-Dorg.eclipse.jt.debug=true�������������û��ſ���ʹ�á�<br>
	 * ���û������κ����룬Ȩ�������
	 */
	public final static String USER_NAME_DEBUGGER = "debugger";

	/**
	 * �����û���
	 */
	public final static String USER_NAME_ANONYM = "?";
	/**
	 * �����û�
	 */
	public final static User anonym = InternalUser.anonymUser;

	/**
	 * �����û�����ϵͳ�������-Dorg.eclipse.jt.debug=true�������������û��ſ���ʹ�á�<br>
	 * ���û������κ����룬Ȩ�������
	 */
	public final static User debugger = InternalUser.debugger;

	/**
	 * ��֤�û�����<br>
	 * �жϸ������������û������Ƿ�ƥ�䡣
	 * 
	 * @param password
	 *            �������룬����Ϊ�ն���
	 * @return ƥ�䷵��true�����򷵻�false
	 */
	public boolean validatePassword(String password);

	public boolean validatePassword(GUID password);

	/**
	 * ��ȡ�û��ѷ���Ľ�ɫ��
	 * 
	 * @return �����û��ѷ���Ľ�ɫ��
	 */
	@Deprecated
	public int getAssignedRoleCount();

	/**
	 * ���������Ż�ȡ�û��ѷ���Ķ�Ӧ�Ľ�ɫ
	 * 
	 * @return �����û��ѷ���Ķ�Ӧ�Ľ�ɫ�����ؿձ�ʾû���ҵ���Ӧ�Ľ�ɫ
	 */
	@Deprecated
	public Role getAssignedRole(int index);

	/**
	 * ��ȡ�û������ȼ�������
	 * 
	 * @return �����û������ȼ�������
	 */
	@Deprecated
	public int getPriorityIndex();

}
