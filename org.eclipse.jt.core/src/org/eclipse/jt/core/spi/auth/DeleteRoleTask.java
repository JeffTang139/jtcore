package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * ɾ����ɫ����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new DeleteRoleTask(roleID);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.DeleteActorTask
 * @author Jeff Tang 2009-11
 */
public final class DeleteRoleTask extends DeleteActorTask {

	/**
	 * �½�ɾ����ɫ����
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 */
	public DeleteRoleTask(GUID roleID) {
		super(roleID);
	}

}
