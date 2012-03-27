package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.da.ext.RPTRecordSetField;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;


final class RPTRecordSetFieldImpl extends RPTRecordSetColumnImpl implements
		RPTRecordSetField {

	@Override
	public final String toString() {
		return "field:".concat(this.field.name);
	}

	final RPTRecordSetRestrictionImpl restriction;
	final TableFieldDefineImpl tableField;

	public final TableFieldDefineImpl getTableField() {
		return this.tableField;
	}

	public final RPTRecordSetRestrictionImpl getRestriction() {
		return this.restriction;
	}

	RPTRecordSetFieldImpl(RPTRecordSetImpl owner,
			TableFieldDefineImpl tableField,
			RPTRecordSetRestrictionImpl restriction) {
		super(owner, owner.fields.size(), owner.recordStruct
				.newField(tableField.getType()));
		this.tableField = tableField;
		this.restriction = restriction;
	}

	private final void updateRecordMask() {
		this.restriction.owner.updateRecordMask(this.restriction.index);
	}

	@Override
	public final void setBoolean(boolean value) {
		this.updateRecordMask();
		super.setBoolean(value);
	}

	@Override
	public final void setByte(byte value) {
		this.updateRecordMask();
		super.setByte(value);
	}

	@Override
	public final void setBytes(byte[] value) {
		this.updateRecordMask();
		super.setBytes(value);
	}

	@Override
	public final void setChar(char value) {
		this.updateRecordMask();
		super.setChar(value);
	}

	@Override
	public final void setDate(long value) {
		this.updateRecordMask();
		super.setDate(value);
	}

	@Override
	public final void setDouble(double value) {
		this.updateRecordMask();
		super.setDouble(value);
	}

	@Override
	public final void setFloat(float value) {
		this.updateRecordMask();
		super.setFloat(value);
	}

	@Override
	public final void setGUID(GUID guid) {
		this.updateRecordMask();
		super.setGUID(guid);
	}

	@Override
	public final void setInt(int value) {
		this.updateRecordMask();
		super.setInt(value);
	}

	@Override
	public final void setLong(long value) {
		this.updateRecordMask();
		super.setLong(value);
	}

	@Override
	public final void setNull() {
		this.updateRecordMask();
		super.setNull();
	}

	@Override
	public final void setObject(Object value) {
		this.updateRecordMask();
		super.setObject(value);
	}

	@Override
	public final void setShort(short value) {
		this.updateRecordMask();
		super.setShort(value);
	}

	@Override
	public final void setString(String value) {
		this.updateRecordMask();
		super.setString(value);
	}

	@Override
	public final void setValue(ReadableValue value) {
		this.updateRecordMask();
		super.setValue(value);
	}

}
