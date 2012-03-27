package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;


/**
 * 单精度浮点型常量表达式
 * 
 * @author Jeff Tang
 * 
 */
final class FloatConstExpr extends ConstExpr {

	public final static FloatConstExpr valueOf(float value) {
		if (value == 0) {
			return FloatConstExpr.ZERO;
		} else if (value == 1) {
			return FloatConstExpr.POSITIVE_ONE;
		} else if (value == -1) {
			return FloatConstExpr.NEGATIVE_ONE;
		}
		return new FloatConstExpr(value);
	}

	public final static FloatConstExpr valueOf(String value) {
		return FloatConstExpr.valueOf(Float.parseFloat(value));
	}

	public final DataTypeBase getType() {
		return FloatType.TYPE;
	}

	public final boolean getBoolean() {
		return Convert.toBoolean(this.value);
	}

	public final char getChar() {
		return Convert.toChar(this.value);
	}

	public byte getByte() {
		return Convert.toByte(this.value);
	}

	public byte[] getBytes() {
		return Convert.toBytes(this.value);
	}

	public long getDate() {
		return Convert.toDate(this.value);
	}

	public double getDouble() {
		return Convert.toDouble(this.value);
	}

	public float getFloat() {
		return this.value;
	}

	public GUID getGUID() {
		return Convert.toGUID(this.value);
	}

	public int getInt() {
		return Convert.toInt(this.value);
	}

	public long getLong() {
		return Convert.toLong(this.value);
	}

	public short getShort() {
		return Convert.toShort(this.value);
	}

	public String getString() {
		return Convert.toString(this.value);
	}

	public Object getObject() {
		return Convert.toObject(this.value);
	}

	public boolean isNull() {
		return false;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof FloatConstExpr
				&& ((FloatConstExpr) obj).value == this.value) {
			return true;
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return Float.floatToIntBits(this.value);
	}

	@Override
	public final String toString() {
		return Float.toString(this.value);
	}

	@Override
	final String getDescription() {
		return "单精度浮点型常量表达式";
	}

	public final static FloatConstExpr ZERO = new FloatConstExpr(0f);

	public final static FloatConstExpr POSITIVE_ONE = new FloatConstExpr(1f);

	public final static FloatConstExpr NEGATIVE_ONE = new FloatConstExpr(-1f);

	final float value;

	FloatConstExpr(float value) {
		this.value = value;
	}

	FloatConstExpr(String value) {
		this.value = Convert.toFloat(value);
	}

	FloatConstExpr(SXElement element) {
		this.value = Convert.toFloat(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitFloatExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.load(this.value);
	}

}