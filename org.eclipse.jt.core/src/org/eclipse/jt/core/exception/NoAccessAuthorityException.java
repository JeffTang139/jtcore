package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.auth.Operation;

/**
 * �޷���Ȩ���쳣<br>
 * ����û���ͼ��ĳ����Դ����δ��Ȩ�Ĳ��������׳����쳣��
 * 
 * @see org.eclipse.jt.core.exception.CoreException
 * @author Jeff Tang 2010-01-08
 */
public final class NoAccessAuthorityException extends CoreException {

	private static final long serialVersionUID = 9053494390777139222L;

	/**
	 * �׳��޷���Ȩ���쳣<br>
	 * ����û���ͼ��ĳ����Դ����δ��Ȩ�Ĳ��������׳����쳣��
	 * 
	 * @param message
	 *            �쳣��Ϣ
	 */
	public NoAccessAuthorityException(String message) {
		super(message);
	}

	/**
	 * �׳��޷���Ȩ���쳣<br>
	 * ����û���ͼ��ĳ����Դ����δ��Ȩ�Ĳ��������׳����쳣��
	 * 
	 * @param resourceDescription
	 *            ��Դ��������Ϣ
	 * @param operation
	 *            ����Դ�Ĳ���
	 */
	public NoAccessAuthorityException(String resourceDescription,
			Operation<?> operation) {
		super("��ǰ�û�û�ж���Դ[" + resourceDescription + "]��[" + operation.getTitle()
				+ "]����Ȩ�ޡ�");
	}

}
