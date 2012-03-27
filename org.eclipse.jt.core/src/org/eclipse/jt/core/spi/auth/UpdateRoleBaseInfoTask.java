package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * ���½�ɫ������Ϣ����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new UpdateRoleBaseInfoTask(roleID);
 * task.title = &quot;update role title&quot;;
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.UpdateActorBaseInfoTask
 * @author Jeff Tang 2009-11
 */
public final class UpdateRoleBaseInfoTask extends UpdateActorBaseInfoTask {

	/**
	 * �½����½�ɫ������Ϣ����
	 * 
	 * @param roleID
	 *            ��ɫID������Ϊ��
	 */
	public UpdateRoleBaseInfoTask(GUID roleID) {
		super(roleID);
	}

}
