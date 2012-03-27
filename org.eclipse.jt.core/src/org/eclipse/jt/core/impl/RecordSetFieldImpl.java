package org.eclipse.jt.core.impl;

import java.text.Format;

import org.eclipse.jt.core.da.RecordSetField;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;


/**
 * 记录集字段实现类
 * 
 * @author Jeff Tang
 * 
 */
final class RecordSetFieldImpl implements RecordSetField {
	/**
	 * 格式化器
	 */
	private Formater formater;

	/**
	 * 获取格式化文本
	 */
	public final Format getFormat() {
		return this.formater != null ? this.formater.getFormat() : null;
	}

	/**
	 * 设置格式化文本
	 */
	public final void setFormat(Format format) {
		if (this.formater != null) {
			if (format != null) {
				this.formater.setFormat(format);
			} else {
				this.formater = null;
			}
		} else if (format != null) {
			this.formater = new Formater(format);
		}
	}

	public final void parseText(String text) {
		if (this.formater == null) {
			this.setString(text);
		} else {
			this.setObject(this.formater.perse(text));
		}
	}

	public final String formatText() {
		if (this.formater == null) {
			return this.getString();
		} else {
			return this.formater.format(this.getObject());
		}
	}

	public final String getName() {
		return this.column.name;
	}

	public final DataType getType() {
		return this.column.value().getType();
	}

	public final QueryColumnImpl getDefine() {
		return this.column;
	}

	public final boolean getBoolean() {
		return this.column.field.getFieldValueAsBooleanNoCheck(this.owner
				.getRecordRead());
	}

	public final void setBoolean(boolean value) {
		this.column.field.setFieldValueAsBooleanNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final byte getByte() {
		return this.column.field.getFieldValueAsByteNoCheck(this.owner
				.getRecordRead());
	}

	public final void setByte(byte value) {
		this.column.field.setFieldValueAsByteNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final short getShort() {
		return this.column.field.getFieldValueAsShortNoCheck(this.owner
				.getRecordRead());
	}

	public final void setShort(short value) {
		this.column.field.setFieldValueAsShortNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final int getInt() {
		return this.column.field.getFieldValueAsIntNoCheck(this.owner
				.getRecordRead());
	}

	public final void setInt(int value) {
		this.column.field.setFieldValueAsIntNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final long getLong() {
		return this.column.field.getFieldValueAsLongNoCheck(this.owner
				.getRecordRead());
	}

	public final void setLong(long value) {
		this.column.field.setFieldValueAsLongNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final long getDate() {
		return this.column.field.getFieldValueAsDateNoCheck(this.owner
				.getRecordRead());
	}

	public final void setDate(long value) {
		this.column.field.setFieldValueAsDateNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final double getDouble() {
		return this.column.field.getFieldValueAsDoubleNoCheck(this.owner
				.getRecordRead());
	}

	public final void setDouble(double value) {
		this.column.field.setFieldValueAsDoubleNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final float getFloat() {
		return this.column.field.getFieldValueAsFloatNoCheck(this.owner
				.getRecordRead());
	}

	public final void setFloat(float value) {
		this.column.field.setFieldValueAsFloatNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final String getString() {
		return this.column.field.getFieldValueAsStringNoCheck(this.owner
				.getRecordRead());
	}

	public final void setString(String value) {
		this.column.field.setFieldValueAsStringNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final GUID getGUID() {
		return this.column.field.getFieldValueAsGUIDNoCheck(this.owner
				.getRecordRead());
	}

	public final void setGUID(GUID value) {
		this.column.field.setFieldValueAsGUIDNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final byte[] getBytes() {
		return this.column.field.getFieldValueAsBytesNoCheck(this.owner
				.getRecordRead());
	}

	public final void setBytes(byte[] value) {
		this.column.field.setFieldValueAsBytesNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final Object getObject() {
		return this.column.field.getFieldValueAsObjectNoCheck(this.owner
				.getRecordRead());
	}

	public final void setObject(Object value) {
		this.column.field.setFieldValueAsObjectNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final char getChar() {
		return this.column.field.getFieldValueAsCharNoCheck(this.owner
				.getRecordRead());
	}

	public final void setChar(char value) {
		this.column.field.setFieldValueAsCharNoCheck(this.owner
				.getRecordWrite(), value);
	}

	public final boolean isNull() {
		return this.column.field.isFieldValueNullNoCheck(this.owner
				.getRecordRead());
	}

	public final void setNull() {
		this.column.field.setFieldValueNullNoCheck(this.owner
				.getRecordWrite());
	}

	public final void setValue(ReadableValue value) {
		this.column.field.setFieldValueNoCheck(this.owner
				.getRecordWrite(), value);
	}

	/**
	 * 所属于记录集
	 */
	final RecordSetImpl owner;

	/**
	 * 关联的查询列定义
	 */
	final QueryColumnImpl column;

	RecordSetFieldImpl(RecordSetImpl owner, QueryColumnImpl column) {
		this.owner = owner;
		this.column = column;
	}
}
