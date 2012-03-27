package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * ɾ���û�����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new DeleteUserTask(userID);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.DeleteActorTask
 * @author Jeff Tang 2009-11
 */
public final class DeleteUserTask extends DeleteActorTask {

	/**
	 * �½�ɾ���û�����
	 * 
	 * @param roleID
	 *            �û�ID������Ϊ��
	 */
	public DeleteUserTask(GUID userID) {
		super(userID);
	}

}
