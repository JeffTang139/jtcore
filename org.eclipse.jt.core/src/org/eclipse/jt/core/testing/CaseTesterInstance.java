package org.eclipse.jt.core.testing;

import org.eclipse.jt.core.Context;

/**
 * ����������ʵ��<br>
 * ����ͨ��context.getList(CaseTesterInstance.class)����ȡ�б�,<br>
 * ��Ҫ����͹��˵���ָ�����˺ͱȽ���
 * 
 * @author Jeff Tang
 * 
 */
public interface CaseTesterInstance {
	/**
	 * ����
	 */
	public String getCode();

	/**
	 * ����
	 */
	public String getName();

	/**
	 * ����
	 */
	public String getDescription();

	/**
	 * ���ò�������<br>
	 * ��ܻ�׼�������������Ȼ�����CaseTester.testCase����<br>
	 * 
	 * @param context
	 *            ������
	 * @param testContext
	 *            ���������ģ���Ҫ���Կ��ʵ�֣��÷�������ֱ�Ӵ��ݸ�CaseTester.testCase
	 */
	public void test(Context context, TestContext testContext) throws Throwable;
}
