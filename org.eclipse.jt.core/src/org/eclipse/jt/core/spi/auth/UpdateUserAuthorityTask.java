package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * �����û���Ȩ����<br>
 * �����û��ڲ�ͬ����֯�����£�Ȩ��Ҳ����ͬ�������ڸ����û���Ȩʱ������ָ����֯������
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new UpdateUserAuthorityTask(roleID, orgID, resourceCategoryID);
 * task.authorityResourceTable.add(authorizedResourceItem);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.UpdateActorAuthorityTask
 * @author Jeff Tang 2009-11
 */
public final class UpdateUserAuthorityTask extends UpdateActorAuthorityTask {
	
	/**
	 * �½������û���Ȩ����
	 * 
	 * @param userID
	 *            �û�ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 * @param resourceCategoryID
	 *            ��Դ���ID������Ϊ��
	 */
	public UpdateUserAuthorityTask(GUID userID, GUID orgID, GUID resourceCategoryID) {
		super(userID, orgID, resourceCategoryID);
	}

}
