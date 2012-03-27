package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * ɾ����������֯����ӳ������
 * 
 * @author Jeff Tang 2010-01
 */
public abstract class DeleteActorOrganizationMappingTask extends SimpleTask {

	/**
	 * ������ID
	 */
	public final GUID actorID;

	/**
	 * ��֯����ID
	 */
	public final GUID orgID;

	/**
	 * �½�ɾ����������֯����ӳ������
	 * 
	 * @param actorID
	 *            ������ID������Ϊ��
	 * @param orgID
	 *            ��֯����ID������Ϊ��
	 */
	protected DeleteActorOrganizationMappingTask(GUID actorID, GUID orgID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		if (orgID == null) {
			throw new NullArgumentException("orgID");
		}
		this.actorID = actorID;
		this.orgID = orgID;
	}

}
