package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.da.ext.RPTRecordSetKey;
import org.eclipse.jt.core.type.DataType;

final class RPTRecordSetKeyImpl extends RPTRecordSetColumnImpl implements
		RPTRecordSetKey {

	final RPTRecordSetKeyRestrictionImpl defaultKeyRestriction;

	RPTRecordSetKeyImpl(RPTRecordSetImpl owner, TableFieldDefineImpl tableField) {
		// StructFieldµÄnameÍ¬KeyName
		super(owner, owner.keys.size(), owner.recordStruct.newField(tableField));
		this.defaultKeyRestriction = new RPTRecordSetKeyRestrictionImpl(this);
	}

	RPTRecordSetKeyImpl(RPTRecordSetImpl owner, String name, DataType type) {
		super(owner, owner.keys.size(), owner.recordStruct.newField(name, type));
		this.defaultKeyRestriction = new RPTRecordSetKeyRestrictionImpl(this);
	}

	@Override
	public final String toString() {
		return "key:".concat(this.field.name);
	}

	public final String getName() {
		return this.field.name;
	}

	public final RPTRecordSetKeyRestrictionImpl getDefaultKeyRestriction() {
		return this.defaultKeyRestriction;
	}

	public final int addMatchValue(Object keyValue) {
		return this.defaultKeyRestriction.addMatchValue(keyValue);
	}

	public final void clearMatchValues() {
		this.defaultKeyRestriction.clearMatchValues();
	}
}
