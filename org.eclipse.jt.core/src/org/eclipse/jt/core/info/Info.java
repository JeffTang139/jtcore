package org.eclipse.jt.core.info;

import org.eclipse.jt.core.def.info.InfoDefine;
import org.eclipse.jt.core.type.GUID;


/**
 * ����е���Ϣʵ�����
 * 
 * @author Jeff Tang
 * 
 */
public interface Info {
	/**
	 * ����������Ĺ�����Ϣ
	 */
	public ProcessInfo getProcess();

	/**
	 * �����Ϣ��Ķ���
	 */
	public InfoDefine getDefine();

	/**
	 * ��ȡ��ʼʱ��
	 */
	public long getTime();

	/**
	 * ��ȡ����ֵ
	 */
	public Object getParam(int index);

	/**
	 * ���ID����Ϊ��־����ʱʹ�ã���GUID��ʱ�����<br>
	 */
	public GUID getID();
}
