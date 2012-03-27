package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.type.DataType;

/**
 * �������ʹ���
 * 
 * @author Jeff Tang
 * 
 */
public final class InvalidOperandTypeException extends CoreException {

	private static final long serialVersionUID = 1L;

	public InvalidOperandTypeException(ValueExpression expr, String expected) {
		super("[" + expr.toString() + "]����[" + expected + "]����.");
	}

	public InvalidOperandTypeException(DataType one, DataType other) {
		super("[" + one + "]��[" + other + "]����ƥ��Ŀ���������.");
	}

}
