package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * 删除访问者组织机构映射任务
 * 
 * @author Jeff Tang 2010-01
 */
public abstract class DeleteActorOrganizationMappingTask extends SimpleTask {

	/**
	 * 访问者ID
	 */
	public final GUID actorID;

	/**
	 * 组织机构ID
	 */
	public final GUID orgID;

	/**
	 * 新建删除访问者组织机构映射任务
	 * 
	 * @param actorID
	 *            访问者ID，不能为空
	 * @param orgID
	 *            组织机构ID，不能为空
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
