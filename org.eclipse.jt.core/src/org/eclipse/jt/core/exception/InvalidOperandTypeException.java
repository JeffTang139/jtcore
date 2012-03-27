package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.type.DataType;

/**
 * 运算类型错误
 * 
 * @author Jeff Tang
 * 
 */
public final class InvalidOperandTypeException extends CoreException {

	private static final long serialVersionUID = 1L;

	public InvalidOperandTypeException(ValueExpression expr, String expected) {
		super("[" + expr.toString() + "]不是[" + expected + "]类型.");
	}

	public InvalidOperandTypeException(DataType one, DataType other) {
		super("[" + one + "]与[" + other + "]不是匹配的可运算类型.");
	}

}
