package org.eclipse.jt.core.impl;

import java.util.List;

final class DB2CursorLoopBuffer extends DB2SegmentBuffer implements
		ISqlCursorLoopBuffer {

	final DB2QueryBuffer query;
	final String cursor;
	final boolean forUpdate;

	DB2CursorLoopBuffer(DB2SegmentBuffer scope, String cursor, boolean forUpdate) {
		super(scope);
		this.cursor = cursor;
		this.forUpdate = forUpdate;
		this.query = new DB2QueryBuffer();
	}

	public final DB2QueryBuffer query() {
		return this.query;
	}

	@Override
	public final void writeTo(SqlStringBuffer sql, List<ParameterReserver> args) {
		// HCL Auto-generated method stub
		throw Utils.notImplemented();
	}

}
