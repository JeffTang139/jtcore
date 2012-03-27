package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * Ϊ�û���Ȩʱ����ȡ���п���Ȩ��Դ�����ļ�<br>
 * �����û��ڲ�ͬ����֯�����£�Ȩ��Ҳ����ͬ�����Ի�ȡ���п���Ȩ��Դ�����ʱ������ָ����֯������<br>
 * �������п���Ȩ��Դ����
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetAuthorizedResCategoryItemForUserKey(userID, orgID);
 * context.getList(AuthorityResourceCategoryItem.class, key);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.GetAuthorizedResCategoryItemForActorKey
 * @author Jeff Tang 2009-11
 */
public final class GetAuthorizedResCategoryItemForUserKey extends
		GetAuthorizedResCategoryItemForActorKey {

	/**
	 * �½���ȡ���п���Ȩ��Դ�����ļ�
	 * 
	 * @param userID
	 *            �û�ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 */
	public GetAuthorizedResCategoryItemForUserKey(GUID userID, GUID orgID) {
		super(userID, orgID);
	}

}
