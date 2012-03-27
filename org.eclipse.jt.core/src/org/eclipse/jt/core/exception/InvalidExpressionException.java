package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.type.DataType;

public final class InvalidExpressionException extends CoreException {

	private static final long serialVersionUID = 1L;

	public InvalidExpressionException(String o, String expect, DataType type) {
		super("������[" + o + "]��,��������������Ϊ[" + expect + "],ʵ��Ϊ[" + type.toString()
				+ "]");
	}

	public InvalidExpressionException(String o, int expect, int re) {
		super("������[" + o + "]��,�������������[" + expect + "],ʵ��Ϊ[" + re + "]");
	}

	public InvalidExpressionException(int expectedCount, int c) {
		super("����������������.����[" + expectedCount + "],ʵ��[" + c + "].");
	}
}
