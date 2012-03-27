package org.eclipse.jt.core.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * ����Ȩ��Դ�����<br>
 * ����Ȩ��Դ�������һ������Ŀ���Ȩ��Դ�D&A���Ϊÿ������Ȩ��Դ���ඨ����һ����ĸ���Դ�
 * ��Ϊ����Ȩ��Դ����ͨ���ö��󣬳��˿��Ի�ȡ�����Ŀ���Ȩ��Դ����Ϣ�⣬�����Ի�ȡ������Դ�����ID�������������Դ��������Ϣ��
 * 
 * @see org.eclipse.jt.core.auth.AuthorizedResourceItem
 * @author Jeff Tang 2009-11
 */
public interface AuthorizedResourceCategoryItem extends AuthorizedResourceItem {

	/**
	 * ��ȡ����Ȩ��Դ���ID
	 * @return ���ؿ���Ȩ��Դ���ID��GUID��
	 */
	@Deprecated
	public GUID getResourceCategoryID();

	/**
	 * ��ȡ����Ȩ��Դ���Ĳ�������
	 * @return ���ؿ���Ȩ��Դ���Ĳ�����������
	 */
	public Operation<?>[] getResourceOperations();

}
