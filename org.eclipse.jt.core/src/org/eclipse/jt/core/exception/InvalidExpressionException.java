package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.type.DataType;

public final class InvalidExpressionException extends CoreException {

	private static final long serialVersionUID = 1L;

	public InvalidExpressionException(String o, String expect, DataType type) {
		super("在运算[" + o + "]中,期望运算体类型为[" + expect + "],实际为[" + type.toString()
				+ "]");
	}

	public InvalidExpressionException(String o, int expect, int re) {
		super("在运算[" + o + "]中,期望运算体个数[" + expect + "],实际为[" + re + "]");
	}

	public InvalidExpressionException(int expectedCount, int c) {
		super("参数个数个数错误.期望[" + expectedCount + "],实际[" + c + "].");
	}
}
