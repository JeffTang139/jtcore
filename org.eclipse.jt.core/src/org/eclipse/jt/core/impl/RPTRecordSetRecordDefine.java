package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.type.DataType;

final class RPTRecordSetRecordDefine extends StructDefineImpl {

	final static class RPTRecord extends DynObj {
		int hash;
		RPTRecord nextSameHash;
		long mask;
	}

	RPTRecordSetRecordDefine() {
		super("rpt-record", RPTRecord.class);
	}

	@Override
	String structTypeNamePrefix() {
		throw new UnsupportedOperationException();
	}

	final void reset() {
		this.fields.clear();
		this.clearAccessInfo();
	}

	final StructFieldDefineImpl newField(DataType type) {
		return super.newField(Integer.toString(this.fields.size()), type);
	}

	final RPTRecord newRecord(int recordState) {
		RPTRecord record = new RPTRecord();
		record.masks = recordState;
		this.prepareSONoCheck(record);
		return record;
	}
}