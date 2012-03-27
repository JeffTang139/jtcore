package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * Ϊ��ɫ��Ȩʱ����ȡ���п���Ȩ��Դ�����ļ�<br>
 * �������п���Ȩ��Դ����
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetAuthorizedResCategoryItemForRoleKey(roleID, orgID);
 * context.getList(AuthorityResourceCategoryItem.class, key);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.GetAuthorizedResCategoryItemForActorKey
 * @author Jeff Tang 2009-11
 */
public final class GetAuthorizedResCategoryItemForRoleKey extends
		GetAuthorizedResCategoryItemForActorKey {

	/**
	 * �½���ȡ���п���Ȩ��Դ�����ļ�
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 */
	public GetAuthorizedResCategoryItemForRoleKey(GUID roleID, GUID orgID) {
		super(roleID, orgID);
	}

}
