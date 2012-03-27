package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.da.ext.RPTRecordSetColumn;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;


class RPTRecordSetColumnImpl implements RPTRecordSetColumn {

	final int index;
	StructFieldDefineImpl field;
	final RPTRecordSetImpl owner;
	final int generation;

	RPTRecordSetColumnImpl(RPTRecordSetImpl owner, int index,
			StructFieldDefineImpl field) {
		this.owner = owner;
		this.generation = owner.generation;
		this.index = index;
		this.field = field;
	}

	public final int getIndex() {
		return this.index;
	}

	public final boolean getBoolean() {
		return this.field.getFieldValueAsBooleanNoCheck(this.owner
				.getRecordRead());
	}

	public final byte getByte() {
		return this.field
				.getFieldValueAsByteNoCheck(this.owner.getRecordRead());
	}

	public final byte[] getBytes() {
		return this.field.getFieldValueAsBytesNoCheck(this.owner
				.getRecordRead());
	}

	public final char getChar() {
		return this.field
				.getFieldValueAsCharNoCheck(this.owner.getRecordRead());
	}

	public final long getDate() {
		return this.field
				.getFieldValueAsDateNoCheck(this.owner.getRecordRead());
	}

	public final double getDouble() {
		return this.field.getFieldValueAsDoubleNoCheck(this.owner
				.getRecordRead());
	}

	public final float getFloat() {
		return this.field.getFieldValueAsFloatNoCheck(this.owner
				.getRecordRead());
	}

	public final GUID getGUID() {
		return this.field
				.getFieldValueAsGUIDNoCheck(this.owner.getRecordRead());
	}

	public final int getInt() {
		return this.field.getFieldValueAsIntNoCheck(this.owner.getRecordRead());
	}

	public final long getLong() {
		return this.field
				.getFieldValueAsLongNoCheck(this.owner.getRecordRead());
	}

	public final Object getObject() {
		return this.field.getFieldValueAsObjectNoCheck(this.owner
				.getRecordRead());
	}

	public final short getShort() {
		return this.field.getFieldValueAsShortNoCheck(this.owner
				.getRecordRead());
	}

	public final String getString() {
		return this.field.getFieldValueAsStringNoCheck(this.owner
				.getRecordRead());
	}

	public final boolean isNull() {
		return this.field.isFieldValueNullNoCheck(this.owner.getRecordRead());
	}

	public final DataType getType() {
		return this.field.type;
	}

	public void setBoolean(boolean value) {
		this.field.setFieldValueAsBooleanNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setByte(byte value) {
		this.field.setFieldValueAsByteNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setBytes(byte[] value) {
		this.field.setFieldValueAsBytesNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setChar(char value) {
		this.field.setFieldValueAsCharNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setDate(long value) {
		this.field.setFieldValueAsDateNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setDouble(double value) {
		this.field.setFieldValueAsDoubleNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setFloat(float value) {
		this.field.setFieldValueAsFloatNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setGUID(GUID guid) {
		this.field
				.setFieldValueAsGUIDNoCheck(this.owner.getRecordWrite(), guid);
	}

	public void setInt(int value) {
		this.field
				.setFieldValueAsIntNoCheck(this.owner.getRecordWrite(), value);
	}

	public void setLong(long value) {
		this.field.setFieldValueAsLongNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setNull() {
		this.field.setFieldValueNullNoCheck(this.owner.getRecordWrite());
	}

	public void setObject(Object value) {
		this.field.setFieldValueAsObjectNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setShort(short value) {
		this.field.setFieldValueAsShortNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setString(String value) {
		this.field.setFieldValueAsStringNoCheck(this.owner.getRecordWrite(),
				value);
	}

	public void setValue(ReadableValue value) {
		this.field.setFieldValueNoCheck(this.owner.getRecordWrite(), value);
	}
}
