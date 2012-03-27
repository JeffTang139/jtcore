package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;


/**
 * 短整形常量表达式
 * 
 * @author Jeff Tang
 * 
 */
final class ShortConstExpr extends ConstExpr {

	public final static ShortConstExpr valueOf(short value) {
		if (value == 0) {
			return ShortConstExpr.ZERO;
		} else if (value == 1) {
			return ShortConstExpr.POSITIVE_ONE;
		} else if (value == -1) {
			return ShortConstExpr.NEGATIVE_ONE;
		}
		return new ShortConstExpr(value);
	}

	public final static ShortConstExpr valueOf(String value) {
		return ShortConstExpr.valueOf(Short.parseShort(value));
	}

	public final DataTypeBase getType() {
		return ShortType.TYPE;
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
		return this.value;
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
		} else if (obj instanceof ShortConstExpr
				&& ((ShortConstExpr) obj).value == this.value) {
			return true;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return this.value;
	}

	@Override
	public final String toString() {
		return Short.toString(this.value);
	}

	@Override
	final String getDescription() {
		return "短整形常量表达式";
	}

	public final static ShortConstExpr ZERO = new ShortConstExpr((short) 0);

	public final static ShortConstExpr POSITIVE_ONE = new ShortConstExpr(
			(short) 1);

	public final static ShortConstExpr NEGATIVE_ONE = new ShortConstExpr(
			(short) -1);

	final short value;

	ShortConstExpr(short value) {
		this.value = value;
	}

	ShortConstExpr(String value) {
		this.value = Convert.toShort(value);
	}

	ShortConstExpr(SXElement element) {
		this.value = Convert.toShort(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitShortExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.load(this.value);
	}

}
