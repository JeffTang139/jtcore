package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * ɾ���û���֯����ӳ������
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new DeleteUserOrganizationMappingTask(userID, orgID);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.DeleteActorOrganizationMappingTask
 * @author Jeff Tang 2010-01
 */
public final class DeleteUserOrganizationMappingTask extends
		DeleteActorOrganizationMappingTask {

	/**
	 * �½�ɾ���û���֯����ӳ������
	 * 
	 * @param actorID
	 *            �û�ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID������Ϊ��
	 */
	public DeleteUserOrganizationMappingTask(GUID userID, GUID orgID) {
		super(userID, orgID);
	}

}
