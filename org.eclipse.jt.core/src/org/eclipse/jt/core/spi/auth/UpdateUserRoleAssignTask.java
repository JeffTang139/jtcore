package org.eclipse.jt.core.spi.auth;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * �����û���ɫ��������
 * 
 * <pre>
 * task = new UpdateUserRoleAssignTask(userID);
 * task.assignRoleIDList.add(roleID);
 * task.priorityIndex = 1;
 * context.handle(task);
 * </pre>
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-11
 */
public final class UpdateUserRoleAssignTask extends SimpleTask {

	/**
	 * �û�ID
	 */
	public final GUID userID;

	/**
	 * Ϊ�û�����Ľ�ɫID�б�<br>
	 * Խ�ȼ�������ȼ�Խ�ߡ�
	 */
	public final List<GUID> assignActorIDList = new ArrayList<GUID>();

	/**
	 * �½������û���ɫ��������
	 * 
	 * @param userID
	 *            �û�ID
	 */
	public UpdateUserRoleAssignTask(GUID userID) {
		if (userID == null) {
			throw new NullArgumentException("userID");
		}
		this.userID = userID;
	}

}
