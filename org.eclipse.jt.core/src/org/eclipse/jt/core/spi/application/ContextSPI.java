package org.eclipse.jt.core.spi.application;

import org.eclipse.jt.core.Context;

public interface ContextSPI extends Context {
	/**
	 * �ύ��ع�����,�ͷ����ݿ���Դ���ڴ�������Դ
	 * 
	 * @return ����֮ǰ���쳣����
	 */
	public Throwable resolveTrans();

	/**
	 * ָ���쳣
	 */
	public void exception(Throwable exception);

	/**
	 * ǿ���ͷ�
	 */
	public void dispose();

	/**
	 * �������������ڿռ�
	 */
	public void updateSpace(String spacePath, char spaceSeparator);
}
