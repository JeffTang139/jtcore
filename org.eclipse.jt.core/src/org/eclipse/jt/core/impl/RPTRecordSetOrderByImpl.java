package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.da.ext.RPTRecordSetOrderBy;

final class RPTRecordSetOrderByImpl implements RPTRecordSetOrderBy {

	final RPTRecordSetColumnImpl column;
	final boolean isDesc;
	final boolean isNullAsMIN;

	public RPTRecordSetColumnImpl getColumn() {
		return this.column;
	}

	public boolean isDesc() {
		return this.isDesc;
	}

	public boolean isNullAsMIN() {
		return this.isNullAsMIN;
	}

	RPTRecordSetOrderByImpl(RPTRecordSetColumnImpl column, boolean isDesc,
			boolean isNullAsMIN) {
		this.column = column;
		this.isDesc = isDesc;
		this.isNullAsMIN = isNullAsMIN;
	}
}
