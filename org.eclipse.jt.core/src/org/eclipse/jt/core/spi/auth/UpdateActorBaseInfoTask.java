package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * ���·����߻�����Ϣ����
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-11
 */
public abstract class UpdateActorBaseInfoTask extends SimpleTask {

	/**
	 * ������ID
	 */
	public final GUID actorID;

	/**
	 * ���º�ķ��������ƣ�Ϊ�ձ�ʾ�����£����������е�������
	 */
	public String name;
	
	/**
	 * ���º�ķ����߱��⣬Ϊ�ձ�ʾ������
	 */
	public String title;

	/**
	 * ���º�ķ�����״̬��Ϊ�ձ�ʾ������
	 */
	public ActorState state;

	/**
	 * ���º�ķ�����������Ϊ�ձ�ʾ������
	 */
	public String description;

	/**
	 * �½����·����߻�����Ϣ����
	 * 
	 * @param actorID
	 *            ������ID������Ϊ��
	 */
	protected UpdateActorBaseInfoTask(GUID actorID) {
		if (actorID == null) {
			throw new NullArgumentException("actorID");
		}
		this.actorID = actorID;
	}

}
