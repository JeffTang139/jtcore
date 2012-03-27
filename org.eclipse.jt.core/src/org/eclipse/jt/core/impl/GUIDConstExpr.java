package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.GUID;


/**
 * GUID常量表达式
 * 
 * @author Jeff Tang
 * 
 */
final class GUIDConstExpr extends ConstExpr {

	public static final GUIDConstExpr valueOf(GUID value) {
		if (value == null) {
			return GUIDConstExpr.NULLGUID;
		}
		return new GUIDConstExpr(value);
	}

	public static final GUIDConstExpr valueOf(String value) {
		return GUIDConstExpr.valueOf(GUID.valueOf(value));
	}

	public final DataTypeBase getType() {
		return GUIDType.TYPE;
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
		return this.value;
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
		} else if (obj instanceof GUIDConstExpr
				&& ((GUIDConstExpr) obj).value.equals(this.value)) {
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
		return "guid'" + this.value.toString(false, false) + "'";
	}

	@Override
	final String getDescription() {
		return "GUID常量表达式";
	}

	public static final GUIDConstExpr NULLGUID = new GUIDConstExpr((GUID) null);

	private final GUID value;

	GUIDConstExpr(GUID value) {
		this.value = value;
	}

	GUIDConstExpr(String value) {
		this.value = Convert.toGUID(value);
	}

	GUIDConstExpr(SXElement element) {
		this.value = Convert.toGUID(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitGUIDExor(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.load(this.value.toBytes());
	}

}
