package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * ɾ����ɫ��֯����ӳ������
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new DeleteRoleOrganizationMappingTask(roleID, orgID);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.DeleteActorOrganizationMappingTask
 * @author Jeff Tang 2010-01
 */
public final class DeleteRoleOrganizationMappingTask extends
		DeleteActorOrganizationMappingTask {

	/**
	 * �½�ɾ����ɫ��֯����ӳ������
	 * 
	 * @param actorID
	 *            ��ɫID������Ϊ��
	 * @param orgID
	 *            ��֯����ID������Ϊ��
	 */
	public DeleteRoleOrganizationMappingTask(GUID roleID, GUID orgID) {
		super(roleID, orgID);
	}

}
