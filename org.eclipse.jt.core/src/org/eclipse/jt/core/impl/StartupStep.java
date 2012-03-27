package org.eclipse.jt.core.impl;

/**
 * ������
 * 
 * @author Jeff Tang
 * 
 * @param <TTarget>
 *            �������Ŀ��
 */
public interface StartupStep<TTarget extends StartupEntry> {
	/**
	 * �������
	 */
	public String getDescription();

	/**
	 * ������ȼ�
	 */
	public int getPriority();

	/**
	 * ִ��ĳ����������һ��
	 * 
	 * @return ������һ����Ϊnull��ʾ����
	 */
	public StartupStep<TTarget> doStep(ResolveHelper helper, TTarget target)
			throws Throwable;

	/**
	 * �������������ȼ�
	 */
	final static int DECLARATOR_HIGHEST_PRI = -0x500000;
	/**
	 * ģ�ͽű������������ȼ�
	 */
	final static int MODEL_SCRIPT_ENGINE_PRI = -0x400000;
	/**
	 * �����������ȼ�
	 */
	final static int SERVICE_HIGHEST_PRI = -0x300000;

	/**
	 * ��־����׼����Ҫ�ڷ�������֮ǰ������
	 */
	final static int LOGMGR_PREPARE_PRI = PublishedService.service_init
			.getPriority() - 0x20;
	/**
	 * ��־���������
	 */
	final static int LOGMGR_READY_PRI = PublishedService.service_init
			.getPriority() + 0x20;

	/**
	 * ���ݿ�������������ȼ�
	 */
	final static int DATASOURCE_HIGHEST_PRI = -0x150000;
	/**
	 * Servlet�������ȼ�
	 */
	final static int SERVLET_HIGHEST_PRI = -0x100000;
}
