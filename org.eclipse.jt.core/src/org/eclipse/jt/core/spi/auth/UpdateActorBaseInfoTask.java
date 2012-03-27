package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * 更新访问者基本信息任务
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-11
 */
public abstract class UpdateActorBaseInfoTask extends SimpleTask {

	/**
	 * 操作者ID
	 */
	public final GUID actorID;

	/**
	 * 更新后的访问者名称，为空表示不更新（不能与已有的重名）
	 */
	public String name;
	
	/**
	 * 更新后的访问者标题，为空表示不更新
	 */
	public String title;

	/**
	 * 更新后的访问者状态，为空表示不更新
	 */
	public ActorState state;

	/**
	 * 更新后的访问者描述，为空表示不更新
	 */
	public String description;

	/**
	 * 新建更新访问者基本信息任务
	 * 
	 * @param actorID
	 *            访问者ID，不能为空
	 */
	protected UpdateActorBaseInfoTask(GUID actorID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
	}

}
