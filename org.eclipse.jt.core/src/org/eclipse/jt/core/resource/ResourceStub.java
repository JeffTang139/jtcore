package org.eclipse.jt.core.resource;

import org.eclipse.jt.core.exception.DisposedException;

/**
 * ��Դ���
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            ��Դ���
 */
public interface ResourceStub<TFacade> {
	/**
	 * �����Դģʽ
	 */
	public ResourceKind getKind();

	/**
	 * ͬһ�����Դ����һ���
	 */
	public Object getCategory();

	/**
	 * ��������
	 */
	public Class<TFacade> getFacadeClass();

	/**
	 * ��ø���Դ����۶���
	 * 
	 * @return ������Դ��ָ�Ķ��󣬼�ʵ��Ҫʹ�õĶ���
	 */
	public TFacade getFacade() throws DisposedException;

	/**
	 * ��Чʱ����null
	 */
	public TFacade tryGetFacade();
}
