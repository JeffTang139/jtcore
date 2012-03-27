package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;


/**
 * 长整形常量表达式
 * 
 * @author Jeff Tang
 * 
 */
final class LongConstExpr extends ConstExpr {

	public static final LongConstExpr valueOf(long value) {
		if (value == 0) {
			return LongConstExpr.ZERO;
		} else if (value == 1) {
			return LongConstExpr.POSITIVE_ONE;
		} else if (value == -1) {
			return LongConstExpr.NEGATIVE_ONE;
		}
		return new LongConstExpr(value);
	}

	public static final LongConstExpr valueOf(String value) {
		return LongConstExpr.valueOf(Long.parseLong(value));
	}

	public final DataTypeBase getType() {
		return LongType.TYPE;
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
		return Convert.toString(this.value);
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
		} else if (obj instanceof LongConstExpr
				&& ((LongConstExpr) obj).value == this.value) {
			return true;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return (int) (this.value ^ this.value >>> 32);
	}

	@Override
	public final String toString() {
		return Long.toString(this.value);
	}

	@Override
	final String getDescription() {
		return "长整形常量表达式";
	}

	public final static LongConstExpr ZERO = new LongConstExpr(0);

	public final static LongConstExpr POSITIVE_ONE = new LongConstExpr(1);

	public final static LongConstExpr NEGATIVE_ONE = new LongConstExpr(-1);

	final long value;

	LongConstExpr(long value) {
		this.value = value;
	}

	LongConstExpr(String value) {
		this.value = Convert.toLong(value);
	}

	LongConstExpr(SXElement element) {
		this.value = Convert.toLong(element.getAttribute(xml_element_const));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitLongExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.load(this.value);
	}

}
