package org.eclipse.jt.core.exception;

/**
 * ��֧��Ȩ����Դ�쳣<br>
 * һ���ڷ��ʲ���Ȩ����֤����Դ��ȴ��ͼ������Ȩ����֤�йط���ʱ���׳����쳣��
 * 
 * @see org.eclipse.jt.core.exception.CoreException
 * @author Jeff Tang 2010-01-08
 */
public class UnsupportedAuthorityResourceException extends CoreException {

	private static final long serialVersionUID = 479620252874514960L;

	/**
	 * �׳���֧��Ȩ����Դ�쳣<br>
	 * һ���ڷ��ʲ���Ȩ����֤����Դ��ȴ��ͼ������Ȩ����֤�йط���ʱ���׳����쳣��
	 * 
	 * @param message
	 *            �쳣��Ϣ
	 */
	public UnsupportedAuthorityResourceException(String message) {
		super(message);
	}

	/**
	 * �׳���֧��Ȩ����Դ�쳣<br>
	 * һ���ڷ��ʲ���Ȩ����֤����Դ��ȴ��ͼ������Ȩ����֤�йط���ʱ���׳����쳣��
	 * 
	 * @param resourceFacadeClass
	 *            ��ͼ���ʵ���Դ�����
	 */
	public UnsupportedAuthorityResourceException(Class<?> resourceFacadeClass) {
		super("[" + resourceFacadeClass.toString() + "]����Դ��֧��Ȩ�޲�����");
	}

}
