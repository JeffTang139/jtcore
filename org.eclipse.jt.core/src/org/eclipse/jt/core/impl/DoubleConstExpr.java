package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;


/**
 * 双精度浮点型常量表达式
 * 
 * @author Jeff Tang
 * 
 */
final class DoubleConstExpr extends ConstExpr {

	public static final DoubleConstExpr valueOf(double value) {
		if (value == 0) {
			return DoubleConstExpr.ZERO;
		} else if (value == 1) {
			return DoubleConstExpr.POSITIVE_ONE;
		} else if (value == -1) {
			return DoubleConstExpr.NEGATIVE_ONE;
		}
		return new DoubleConstExpr(value);
	}

	public final static DoubleConstExpr valueOf(String value) {
		return DoubleConstExpr.valueOf(Double.parseDouble(value));
	}

	public final DoubleType getType() {
		return DoubleType.TYPE;
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
		} else if (obj instanceof DoubleConstExpr
				&& ((DoubleConstExpr) obj).value == this.value) {
			return true;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		long bits = Double.doubleToLongBits(this.value);
		return (int) (bits ^ bits >>> 32);
	}

	@Override
	public final String toString() {
		return Double.toString(this.value);
	}

	@Override
	final String getDescription() {
		return "双精度浮点型常量表达式";
	}

	public final static DoubleConstExpr ZERO = new DoubleConstExpr(0d);

	public final static DoubleConstExpr POSITIVE_ONE = new DoubleConstExpr(1d);

	public final static DoubleConstExpr NEGATIVE_ONE = new DoubleConstExpr(-1d);

	final double value;

	DoubleConstExpr(double value) {
		this.value = value;
	}

	DoubleConstExpr(String value) {
		this.value = Convert.toDouble(value);
	}

	DoubleConstExpr(SXElement element) {
		this.value = Convert.toDouble(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitDoubleExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.load(this.value);
	}

}
