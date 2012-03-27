package org.eclipse.jt.core.spi.auth;

import org.eclipse.jt.core.auth.ActorState;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.type.GUID;


/**
 * �½�����������
 * 
 * @see org.eclipse.jt.core.invoke.SimpleTask
 * @author Jeff Tang 2009-11
 */
public abstract class NewActorTask extends SimpleTask {

	/**
	 * ������ID
	 */
	public final GUID id;

	/**
	 * ����������
	 */
	public final String name;

	/**
	 * �����߱��⣬Ϊ��ʱĬ��Ϊ����������
	 */
	public String title;

	/**
	 * ������״̬��Ϊ��ʱĬ��Ϊ����״̬
	 */
	public ActorState state;

	/**
	 * ��������������Ϊ��
	 */
	public String description;

	/**
	 * �½�����������
	 * 
	 * @param id
	 *            ������ID������Ϊ��
	 * @param name
	 *            ���������ƣ�����Ϊ��
	 */
	protected NewActorTask(GUID id, String name) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		this.id = id;
		this.name = name;
	}

}
