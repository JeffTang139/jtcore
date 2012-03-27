package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.VariableValue;


/**
 * 记录集字段实现类
 * 
 * @author Jeff Tang
 * 
 */
final class StructFieldVisitor implements VariableValue {

	public final DataType getType() {
		return this.targetField.type;
	}

	public final boolean getBoolean() {
		return this.targetField.getFieldValueAsBooleanNoCheck(this.target);
	}

	public final void setBoolean(boolean value) {
		this.targetField.setFieldValueAsBooleanNoCheck(this.target, value);
	}

	public final byte getByte() {
		return this.targetField.getFieldValueAsByteNoCheck(this.target);
	}

	public final void setByte(byte value) {
		this.targetField.setFieldValueAsByteNoCheck(this.target, value);
	}

	public final short getShort() {
		return this.targetField.getFieldValueAsShortNoCheck(this.target);
	}

	public final void setShort(short value) {
		this.targetField.setFieldValueAsShortNoCheck(this.target, value);
	}

	public final int getInt() {
		return this.targetField.getFieldValueAsIntNoCheck(this.target);
	}

	public final void setInt(int value) {
		this.targetField.setFieldValueAsIntNoCheck(this.target, value);
	}

	public final long getLong() {
		return this.targetField.getFieldValueAsLongNoCheck(this.target);
	}

	public final void setLong(long value) {
		this.targetField.setFieldValueAsLongNoCheck(this.target, value);
	}

	public final long getDate() {
		return this.targetField.getFieldValueAsDateNoCheck(this.target);
	}

	public final void setDate(long value) {
		this.targetField.setFieldValueAsDateNoCheck(this.target, value);
	}

	public final double getDouble() {
		return this.targetField.getFieldValueAsDoubleNoCheck(this.target);
	}

	public final void setDouble(double value) {
		this.targetField.setFieldValueAsDoubleNoCheck(this.target, value);
	}

	public final float getFloat() {
		return this.targetField.getFieldValueAsFloatNoCheck(this.target);
	}

	public final void setFloat(float value) {
		this.targetField.setFieldValueAsFloatNoCheck(this.target, value);
	}

	public final String getString() {
		return this.targetField.getFieldValueAsStringNoCheck(this.target);
	}

	public final void setString(String value) {
		this.targetField.setFieldValueAsStringNoCheck(this.target, value);
	}

	public final GUID getGUID() {
		return this.targetField.getFieldValueAsGUIDNoCheck(this.target);
	}

	public final void setGUID(GUID value) {
		this.targetField.setFieldValueAsGUIDNoCheck(this.target, value);
	}

	public final byte[] getBytes() {
		return this.targetField.getFieldValueAsBytesNoCheck(this.target);
	}

	public final void setBytes(byte[] value) {
		this.targetField.setFieldValueAsBytesNoCheck(this.target, value);
	}

	public final Object getObject() {
		return this.targetField.getFieldValueAsObjectNoCheck(this.target);
	}

	public final void setObject(Object value) {
		this.targetField.setFieldValueAsObjectNoCheck(this.target, value);
	}

	public final char getChar() {
		return this.targetField.getFieldValueAsCharNoCheck(this.target);
	}

	public final void setChar(char value) {
		this.targetField.setFieldValueAsCharNoCheck(this.target, value);
	}

	public final boolean isNull() {
		return this.targetField.isFieldValueNullNoCheck(this.target);
	}

	public final void setNull() {
		this.targetField.setFieldValueNullNoCheck(this.target);
	}

	public final void setValue(ReadableValue value) {
		this.targetField.setFieldValueNoCheck(this.target, value);
	}

	/**
	 * 关联的查询列定义
	 */
	private StructFieldDefineImpl targetField;
	private Object target;

	final void reset(StructFieldDefineImpl targetField, Object target) {
		if (target == null) {
			throw new NullArgumentException("target");
		}
		if (targetField == null) {
			throw new NullArgumentException("targetField");
		}
		this.targetField = targetField;
		this.target = target;
	}

	StructFieldVisitor(StructFieldDefineImpl targetField, Object target) {
		this.reset(targetField, target);
	}

	StructFieldVisitor() {
	}
}
