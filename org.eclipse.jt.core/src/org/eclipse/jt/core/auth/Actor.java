package org.eclipse.jt.core.auth;

import org.eclipse.jt.core.impl.InternalActor;
import org.eclipse.jt.core.type.GUID;


/**
 * ������<br>
 * �����߼�Ϊӵ��Ȩ�޵����壬���Ա�����Ȩ�ޡ���ǰ��������Ҫ��Ϊ�û��ͽ�ɫ���֡�
 * 
 * @author Jeff Tang 2009-11
 */
public interface Actor {

	/**
	 * Ĭ����֯����ID
	 */
	public static final GUID GLOBAL_ORG_ID = InternalActor.GLOBAL_ORG_ID;
	
	/**
	 * ��÷�����ID<br>
	 * ������ID��ͬ�ַ������������Ψһ��
	 * 
	 * @return ���ط�����ID�����ؽ��������Ϊ��
	 */
	public GUID getID();

	/**
	 * ����������<br>
	 * ������������ͬ�ַ������������Ψһ��
	 * 
	 * @return ���ط��������ƣ����ؽ��������Ϊ��
	 */
	public String getName();

	/**
	 * ��ȡ�����߱���<br>
	 * һ������£������߱���ֻ������ʾ��
	 * 
	 * @return ���ط����߱��⣬���ؽ��������Ϊ��
	 */
	public String getTitle();

	/**
	 * ��÷����ߵ�״̬
	 * 
	 * @see org.eclipse.jt.core.auth.ActorState
	 * @return ���ط����ߵ�ǰ״̬
	 */
	public ActorState getState();

	/**
	 * ��ȡ���ʵ�������Ϣ
	 * 
	 * @return ���ط����߱��⣬���ؽ������Ϊ��
	 */
	public String getDescription();

	/**
	 * ��ȡ�����߹�������֯������������Ĭ�Ϲ�������֯����
	 * 
	 * @return ���ط����߹�������֯������
	 */
	public int getMappingOrganizationCount();

	/**
	 * ���������Ż�ȡ��Ӧ��������֯����ID
	 * 
	 * @return ���ض�Ӧ��������֯����ID�����<code>getDefaultOrganizationID()</code>
	 *         ���صĽ��Ϊ�գ��˴����صĽ���϶�ҲΪ��
	 */
	public GUID getMappingOrganizationID(int index);

}
