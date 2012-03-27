package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * ɾ������������
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-11
 */
public abstract class DeleteActorTask extends SimpleTask {

	/**
	 * ������ID
	 */
	public final GUID actorID;

	/**
	 * �½�ɾ������������
	 * 
	 * @param actorID
	 *            ������ID������Ϊ��
	 */
	protected DeleteActorTask(GUID actorID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
	}

}
