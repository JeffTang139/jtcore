package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.InvalidExpressionException;
import org.eclipse.jt.core.type.DataType;

final class Expr {

	static final void checkHierarchyPathValue(ValueExpr value) {
		try {
			TableFieldRefImpl fr = (TableFieldRefImpl) value;
			if (!(fr.getType() instanceof VarBinDBType)) {
				throw new IllegalArgumentException("级次谓词的运算体字段类型错误.");
			}
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("级次谓词的运算体不是字段引用表达式.");
		}
	}

	static final DataType checkNonDecimalNumber(String at, ValueExpr expr) {
		DataType type = expr.getType();
		if (type == BooleanType.TYPE || type == ByteType.TYPE
				|| type == ShortType.TYPE || type == IntType.TYPE
				|| type == LongType.TYPE) {
			return type;
		}
		throw new InvalidExpressionException(at.toString(), "整数数值", type);
	}

}
