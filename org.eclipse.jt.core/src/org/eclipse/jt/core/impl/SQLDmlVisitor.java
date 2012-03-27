package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.arg.ArgumentDeclare;
import org.eclipse.jt.core.def.arg.ArgumentableDeclare;
import org.eclipse.jt.core.spi.sql.SQLNotSupportedException;
import org.eclipse.jt.core.spi.sql.SQLOperandTypeException;
import org.eclipse.jt.core.spi.sql.SQLVariableDuplicateException;
import org.eclipse.jt.core.type.DataType;

abstract class SQLDmlVisitor<T extends SQLVisitorContext> extends
		VisitorBase<T> {
	@Override
	public void visitParamDeclare(T visitorContext, NParamDeclare v) {
		switch (v.modifier) {
		case INOUT:
		case OUT:
			throw new SQLNotSupportedException(v.name.line, v.name.col,
					"�˴�ֻ֧���������");
		}
		DataType type = v.type.getType(visitorContext.querier);
		switch (TypeCategory.typeOf(type)) {
		case VAR:
		case BOTH:
			break;
		default:
			throw new SQLNotSupportedException(v.name.line, v.name.col,
					"��֧�ֲ������� '" + type + "'");
		}
		if (v.defaultValue != null
				&& !isConvertable(v.defaultValue.getType(), type)) {
			throw new SQLOperandTypeException(v.defaultValue.startLine(),
					v.defaultValue.startCol(), "Ĭ��ֵ�������������Ͳ����� '"
							+ v.defaultValue.getType() + "'");
		}
		try {
			StructFieldDefineImpl f = visitorContext.newArgument(
					v.argumentName, type);
			if (v.defaultValue != null) {
				SQLExprContext exprContext = new SQLExprContext(visitorContext,
						null);
				f.setDefault(exprContext.build(v.defaultValue));
			}
		} catch (SQLVariableDuplicateException ex) {
			ex.line = v.name.line;
			ex.col = v.name.col;
			throw ex;
		}
	}

	protected static final boolean isConvertable(DataType src, DataType des) {
		if (src == des) {
			return true;
		}
		if (!src.canDBTypeConvertTo(des)) {
			return false;
		}
		switch (des.isAssignableFrom(src)) {
		case IMPLICIT:
			return true;
		case SAME:
			return true;
		}
		return false;
	}

	protected final void appendArguments(T visitorContext, NDmlDeclare d,
			ArgumentableDeclare s) {
		if (d.params != null) {
			for (NParamDeclare p : d.params) {
				this.visitParamDeclare(visitorContext, p);
			}
		}
		if (visitorContext.getArguments() != null) {
			for (StructFieldDefineImpl f : visitorContext.getArguments()) {
				ArgumentDeclare a = s.newArgument(f.getName(), f.getType());
				a.setDefault(f.getDefault());
			}
		}
	}
}
