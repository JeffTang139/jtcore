package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * �����û���������
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new UpdateUserPasswordTask(userID, newPassword);
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-11
 */
public final class UpdateUserPasswordTask extends SimpleTask {

	/**
	 * �û�ID
	 */
	public final GUID userID;

	/**
	 * ������
	 */
	public final String newPassword;

	/**
	 * �½������û���������
	 * 
	 * @param userID
	 *            �û�ID������Ϊ��
	 * @param newPassword
	 *            �����룬����Ϊ��
	 */
	public UpdateUserPasswordTask(GUID userID, String newPassword) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
		if (newPassword == null || newPassword.length() == 0) {
			this.newPassword = "";
		} else {
			this.newPassword = newPassword;
		}
	}

}
