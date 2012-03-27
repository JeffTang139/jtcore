package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * �����û�������Ϣ����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new UpdateUserBaseInfoTask(userID);
 * task.title = &quot;update user title&quot;;
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.UpdateActorBaseInfoTask
 * @author Jeff Tang 2009-11
 */
public final class UpdateUserBaseInfoTask extends UpdateActorBaseInfoTask {

	/**
	 * �½������û�������Ϣ����
	 * 
	 * @param roleID
	 *            �û�ID������Ϊ��
	 */
	public UpdateUserBaseInfoTask(GUID userID) {
		super(userID);
	}

}
