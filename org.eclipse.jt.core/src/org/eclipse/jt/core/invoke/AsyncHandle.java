package org.eclipse.jt.core.invoke;

import java.util.List;

import org.eclipse.jt.core.info.Info;


/**
 * �첽������
 * 
 * @author Jeff Tang
 * 
 */
public interface AsyncHandle {
	/**
	 * ����첽ִ�д����״̬
	 */
	public AsyncState getState();

	/**
	 * ������ȣ�0��ʾ��δ����1��ʾ������ϣ�֮�������ʾ���ȣ�С���������ʾ��;���ִ���
	 * 
	 * @return ���ش������
	 */
	public float getProgress();

	/**
	 * ��ȡ�Ѿ���������Ϣ<br>
	 * �����������첽����ʱָ��������ϢAsyncInfo.isCareInfos()<br>
	 * 
	 * ����AsyncInfo
	 */
	public int fetchInfos(List<Info> to);

	/**
	 * ���������������쳣���򷵻ظ��쳣�����򷵻�null
	 * 
	 * @return �����쳣����null
	 */
	public Throwable getException();

	/**
	 * ����ȡ������һ���ܹ��ɹ���ȡ������ȡ����ʵ�����Ƿ�������Ӧ��֧��
	 */
	public void cancel();
}
