package org.eclipse.jt.core.resource;

/**
 * ��Դ���ͱ��
 * 
 * @author Jeff Tang
 * 
 */
public enum ResourceKind {
	/**
	 * ��½��������Դ<br>
	 * ���̷߳���ʱ�����������������д��д��д����
	 */
	SINGLETON_IN_LOGIN(false, true, false),
	/**
	 * ȫ�ֵ�����Դ<br>
	 * ���̷߳���ʱ�����������������д��д��д����
	 */
	SINGLETON_IN_SITE(true, false, false),
	/**
	 * ȫ�ֵ�����Դ(֧�ּ�Ⱥ)<br>
	 * ���̷߳���ʱ�����������������д��д��д����
	 */
	SINGLETON_IN_CLUSTER(true, false, true);
	/**
	 * ȫ�ֵ�����Դ<br>
	 * ���̷߳���ʱ�����������������д��д��д���� ��ʹ��{@code ResourceKind.SINGLETON_IN_SITE}
	 */
	@Deprecated
	public final static ResourceKind SINGLETON_IN_GLOBAL = SINGLETON_IN_SITE;

	/**
	 * �Ƿ���ȫ����Դ
	 */
	public final boolean isGlobal;
	public final boolean inSession;
	public final boolean inCluster;

	ResourceKind(boolean isGlobal, boolean inSession, boolean inCluster) {
		this.isGlobal = isGlobal;
		this.inSession = inSession;
		this.inCluster = inCluster;
	}
}
