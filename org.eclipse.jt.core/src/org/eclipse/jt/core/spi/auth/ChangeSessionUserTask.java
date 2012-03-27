package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.User;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;

/**
 * �л��Ự�û�����<br>
 * ͨ������£��û���ҵ���߼���ִ�����¼�߼�����ͨ���������û�����Ϊ�Ự�ĵ�ǰ�û���
 * 
 * <pre>
 * ʹ��ʾ����
 * context.handle(new ChangeSessionUserTask(user));
 * </pre>
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-12
 */
public final class ChangeSessionUserTask extends SimpleTask {

	/**
	 * �û�����
	 */
	public final User user;

	/**
	 * �½��л��Ự�û�����
	 * 
	 * @param user
	 *            �û�
	 */
	public ChangeSessionUserTask(User user) {
		if (user == null) {
			throw new NullArgumentException("user");
		}
		this.user = user;
	}

}
