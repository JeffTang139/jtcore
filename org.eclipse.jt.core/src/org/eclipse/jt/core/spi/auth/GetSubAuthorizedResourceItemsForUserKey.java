package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * Ϊ�û���Ȩʱ����ȡָ������Ȩ��Դ����¼���Դ���б�ļ�<br>
 * �����û��ڲ�ͬ����֯�����£�Ȩ��Ҳ����ͬ�����Ի�ȡ�¼���Դ��ʱ������ָ����֯������<br>
 * ���ؿ���Ȩ��Դ���б�
 * 
 * <pre>
 * ʹ��ʾ����
 * key = new GetSubAuthorizedResourceItemsForUserKey(userID, orgID, resourceCategoryID, currentResID);
 * context.getList(AuthorizedResourceItem.class, key);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.GetSubAuthorizedResourceItemsForActorKey
 * @author Jeff Tang 2009-11
 */
@Deprecated
public final class GetSubAuthorizedResourceItemsForUserKey extends
		GetSubAuthorizedResourceItemsForActorKey {

	/**
	 * �½���ȡָ������Ȩ��Դ����¼���Դ���б�ļ�
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�ձ�ʾȫ����֯����
	 * @param resourceCategoryID
	 *            ��Դ���ID������Ϊ��
	 * @param currentResID
	 *            ��ǰ��Ȩ��Դ���Ϊ��
	 */
	public GetSubAuthorizedResourceItemsForUserKey(GUID userID, GUID orgID,
			GUID resourceCategoryID, GUID currentResID) {
		super(userID, orgID, resourceCategoryID, currentResID);
	}

}
