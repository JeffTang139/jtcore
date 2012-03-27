package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * 删除访问者任务
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-11
 */
public abstract class DeleteActorTask extends SimpleTask {

	/**
	 * 访问者ID
	 */
	public final GUID actorID;

	/**
	 * 新建删除访问者任务
	 * 
	 * @param actorID
	 *            访问者ID，不能为空
	 */
	protected DeleteActorTask(GUID actorID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
	}

}
