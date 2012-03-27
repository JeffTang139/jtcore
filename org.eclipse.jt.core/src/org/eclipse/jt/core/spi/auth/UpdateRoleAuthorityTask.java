package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * ���½�ɫ��Ȩ����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new UpdateRoleAuthorityTask(roleID, orgID, resourceCategoryID);
 * task.authorityResourceTable.add(authorizedResourceItem);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.UpdateActorAuthorityTask
 * @author Jeff Tang 2009-11
 */
public final class UpdateRoleAuthorityTask extends UpdateActorAuthorityTask {

	/**
	 * �½����½�ɫ��Ȩ����
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 * @param orgID
	 *            ��֯����ID����Ϊ�գ�Ϊ�մ���Ĭ�Ϲ�������֯����ID
	 * @param resourceCategoryID
	 *            ��Դ���ID������Ϊ��
	 */
	public UpdateRoleAuthorityTask(GUID roleID, GUID orgID,
			GUID resourceCategoryID) {
		super(roleID, orgID, resourceCategoryID);
	}

}
