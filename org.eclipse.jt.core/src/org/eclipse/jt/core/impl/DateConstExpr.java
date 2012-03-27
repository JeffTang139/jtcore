package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DateParser;
import org.eclipse.jt.core.type.GUID;


/**
 * 日期常量表达式
 * 
 * @author Jeff Tang
 * 
 */
final class DateConstExpr extends ConstExpr {

	public static final DateConstExpr valueOf(long value) {
		return new DateConstExpr(value);
	}

	public static final DateConstExpr valueOf(String value) {
		return valueOf(Convert.toDate(value));
	}

	public final DateType getType() {
		return DateType.TYPE;
	}

	public final boolean getBoolean() {
		return Convert.dateToBoolean(this.value);
	}

	public final char getChar() {
		return Convert.toChar(this.value);
	}

	public final byte getByte() {
		return Convert.dateToByte(this.value);
	}

	public final byte[] getBytes() {
		return Convert.dateToBytes(this.value);
	}

	public final long getDate() {
		return this.value;
	}

	public final double getDouble() {
		return Convert.dateToDouble(this.value);
	}

	public final float getFloat() {
		return Convert.dateToFloat(this.value);
	}

	public final GUID getGUID() {
		return Convert.dateToGUID(this.value);
	}

	public final int getInt() {
		return Convert.dateToInt(this.value);
	}

	public final long getLong() {
		return Convert.dateToLong(this.value);
	}

	public final short getShort() {
		return Convert.dateToShort(this.value);
	}

	public final String getString() {
		return Convert.dateToString(this.value);
	}

	public final Object getObject() {
		return Convert.dateToObject(this.value);
	}

	public final boolean isNull() {
		return false;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof DateConstExpr
				&& ((DateConstExpr) obj).value == this.value) {
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
		return "date'"
				+ DateParser.format(this.value,
						DateParser.FORMAT_DATE_TIME_AUTOMS) + "'";
	}

	@Override
	final String getDescription() {
		return "日期常量表达式";
	}

	final long value;

	DateConstExpr(long value) {
		super();
		this.value = value;
	}

	DateConstExpr(String value) {
		super();
		this.value = Convert.toDate(value);
	}

	DateConstExpr(SXElement element) {
		this.value = Convert.toDate(element.getAttribute(xml_attr_value));
	}

	public final <TContext> void visit(OMVisitor<TContext> visitor,
			TContext context) {
		visitor.visitDateExpr(this, context);
	}

	@Override
	final void render(ISqlExprBuffer buffer, TableUsages usages) {
		buffer.loadDate(this.value);
	}

}
