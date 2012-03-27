package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;

/**
 * ��Դ������<br>
 * Ŀ������ڳ�ʼ������Ȩ��Դʱ����������е�һЩ��������
 * 
 * @see org.eclipse.jt.core.auth.Operation
 * @author Jeff Tang 2009-12
 */
@SuppressWarnings("unchecked")
final class OperationEntry implements Operation {

	/**
	 * ��Դ����
	 */
	public final Operation<?> operation;

	/**
	 * ��Ȩ����
	 */
	public final int authMask;

	/**
	 * ������Ȩ����
	 */
	public final int allowAuthCode;

	/**
	 * �ܾ���Ȩ����
	 */
	public final int denyAuthCode;
	
	final int index;

	/**
	 * ����һ����Դ������
	 * 
	 * @param operation
	 *            ��Դ����������Ϊ��
	 */
	OperationEntry(Operation<?> operation, int index) {
		this.operation = operation;
		final int opMask = operation.getMask();
		this.authMask = OperationUtil.calAuthMask(opMask);
		this.allowAuthCode = OperationUtil.calAuthCode(opMask,
				Authority.ALLOW.code);
		this.denyAuthCode = OperationUtil.calAuthCode(opMask,
				Authority.DENY.code);
		this.index = index;
	}

	public final String getTitle() {
		return this.operation.getTitle();
	}

	public final int getMask() {
		return this.operation.getMask();
	}

}
