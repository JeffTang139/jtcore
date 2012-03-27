package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.type.GUID;

/**
 * �½���ɫ����
 * 
 * <pre>
 * ʹ��ʾ����
 * task = new NewRoleTask(roleID, roleName);
 * task.title = &quot;role title&quot;;
 * task.state = ActorState.DISABLE;
 * task.description = &quot;description string&quot;;
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.spi.auth.NewActorTask
 * @author Jeff Tang 2009-11
 */
public final class NewRoleTask extends NewActorTask {

	/**
	 * �����½���ɫ����
	 * 
	 * @param id
	 *            ��ɫID������Ϊ��
	 * @param name
	 *            ��ɫ��������Ϊ��
	 */
	public NewRoleTask(GUID id, String name) {
		super(id, name);
	}

}
