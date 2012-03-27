package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;


/**
 * 字符串常量表达式
 * 
 * @author Jeff Tang
 * 
 */
final class StringConstExpr extends ConstExpr {

	public final static StringConstExpr valueOf(String value) {
		if (value == null) {
			return StringConstExpr.NULL;
		} else if (value.length() == 0) {
			return StringConstExpr.EMPTY;
		}
		return new StringConstExpr(value);
	}

	public final DataTypeBase getType() {
		return StringType.TYPE;
	}

	public final boolean getBoolean() {
		return Convert.toBoolean(this.value);
	}

	public final char getChar() {
		return Convert.toChar(this.value);
	}

	public final byte getByte() {
		return Convert.toByte(this.value);
	}

	public final byte[] getBytes() {
		return Convert.toBytes(this.value);
	}

	public final long getDate() {
		return Convert.toDate(this.value);
	}

	public final double getDouble() {
		return Convert.toDouble(this.value);
	}

	public final float getFloat() {
		return Convert.toFloat(this.value);
	}

	public final GUID getGUID() {
		return Convert.toGUID(this.value);
	}

	public final int getInt() {
		return Convert.toInt(this.value);
	}

	public final long getLong() {
		return Convert.toLong(this.value);
	}

	public final short getShort() {
		return Convert.toShort(this.value);
	}

	public final String getString() {
		return this.value;
	}

	public final Object getObject() {
		return Convert.toObject(this.value);
	}

	public final boolean isNull() {
		return false;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof StringConstExpr
				&& ((StringConstExpr) obj).value.equals(this.value)) {
			return true;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public final String toString() {
		return "'" + this.value + "'";
	}

	@Override
	final String getDescription() {
		return "字符串常量表达式";
	}

	public static final StringConstExpr NULL = new StringConstExpr(
			(String) null);

	public static final StringConstExpr EMPTY = new StringConstExpr("");

	final String value;

	StringConstExpr(String value) {
		this.value = value;
	}

	StringConstExpr(SXElement element) {
		this.value = Convert.toString(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitStringExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.loadStr(this.value);
	}

}
