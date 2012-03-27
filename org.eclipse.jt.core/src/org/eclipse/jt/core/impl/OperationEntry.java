package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Authority;
import org.eclipse.jt.core.auth.Operation;

/**
 * 资源操作项<br>
 * 目标的在于初始化可授权资源时，计算操作中的一些常用量。
 * 
 * @see org.eclipse.jt.core.auth.Operation
 * @author Jeff Tang 2009-12
 */
@SuppressWarnings("unchecked")
final class OperationEntry implements Operation {

	/**
	 * 资源操作
	 */
	public final Operation<?> operation;

	/**
	 * 授权掩码
	 */
	public final int authMask;

	/**
	 * 允许授权编码
	 */
	public final int allowAuthCode;

	/**
	 * 拒绝授权编码
	 */
	public final int denyAuthCode;
	
	final int index;

	/**
	 * 构造一个资源操作项
	 * 
	 * @param operation
	 *            资源操作，不能为空
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
