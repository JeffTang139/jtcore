package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;


/**
 * 布尔型常量表达式
 * 
 * @author Jeff Tang
 * 
 */
final class BooleanConstExpr extends ConstExpr {

	public static final BooleanConstExpr valueOf(boolean value) {
		if (value) {
			return BooleanConstExpr.TRUE;
		}
		return BooleanConstExpr.FALSE;
	}

	public static final BooleanConstExpr valueOf(String value) {
		if (value.equals("0") || value.equals("false")) {
			return BooleanConstExpr.FALSE;
		} else if (value.equals("1") || value.equals("true")) {
			return BooleanConstExpr.TRUE;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public final BooleanType getType() {
		return BooleanType.TYPE;
	}

	public final boolean getBoolean() {
		return this.value;
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
		if (obj instanceof BooleanConstExpr
				&& ((BooleanConstExpr) obj).value == this.value) {
			return true;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return (this.value ? Boolean.TRUE : Boolean.FALSE).hashCode();
	}

	@Override
	public final String toString() {
		return Boolean.toString(this.value);
	}

	@Override
	final String getDescription() {
		return "布尔型常量表达式";
	}

	public static final BooleanConstExpr TRUE = new BooleanConstExpr(true);

	public static final BooleanConstExpr FALSE = new BooleanConstExpr(false);

	final boolean value;

	private BooleanConstExpr(boolean value) {
		this.value = value;
	}

	BooleanConstExpr(SXElement element) {
		this.value = Convert.toBoolean(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitBooleanExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.load(this.value);
	}

}
