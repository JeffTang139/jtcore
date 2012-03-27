package org.eclipse.jt.core.exception;

public final class InvalidOperateException extends CoreException {

	private static final long serialVersionUID = 1L;

	public InvalidOperateException(int expectedCount, int c) {
		super("参数个数个数错误.期望[" + expectedCount + "],实际[" + c + "].");
	}
}
