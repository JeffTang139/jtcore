package org.eclipse.jt.core.resource;

/**
 * ��Դ��������
 * 
 * @author Jeff Tang
 * 
 */
public interface ResourceTokenLink<TFacade> {
	/**
	 * �ڵ��ϵ���Դ��ʶ
	 */
	public ResourceToken<TFacade> getToken();

	/**
	 * ��һ�����ڣ�����null��
	 */
	public ResourceTokenLink<TFacade> next();
}
