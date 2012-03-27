package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;


/**
 * 字节形常量表达式
 * 
 * @author Jeff Tang
 * 
 */
final class ByteConstExpr extends ConstExpr {

	public static final ByteConstExpr valueOf(byte value) {
		return new ByteConstExpr(value);
	}

	public final ByteType getType() {
		return ByteType.TYPE;
	}

	public final boolean getBoolean() {
		return Convert.toBoolean(this.value);
	}

	public final char getChar() {
		return Convert.toChar(this.value);
	}

	public final byte getByte() {
		return this.value;
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
		} else if (obj instanceof ByteConstExpr
				&& ((ByteConstExpr) obj).value == this.value) {
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
		return Byte.toString(this.value);
	}

	final byte value;

	@Override
	final String getDescription() {
		return "字节形常量表达式";
	}

	ByteConstExpr(byte value) {
		this.value = value;
	}

	ByteConstExpr(String value) {
		this.value = Convert.toByte(value);
	}

	ByteConstExpr(ByteConstExpr sample) {
		this.value = sample.value;
	}

	ByteConstExpr(SXElement element) {
		this.value = Convert.toByte(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitByteExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.load(this.value);
	}

}
