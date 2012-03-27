package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;

final class DOT_SqlDate implements DataObjectTranslator<java.sql.Date, Long> {
	final static short VERSION = 0x0100;

	private DOT_SqlDate() {

	}

	public final boolean supportAssign() {
		return false;
	}

	public Long toDelegateObject(java.sql.Date date) {
		return date.getTime();
	}

	public short getVersion() {
		return VERSION;
	}

	public java.sql.Date recoverObject(java.sql.Date destHint, Long obj,
			ObjectQuerier querier, short version) {
		return new java.sql.Date(obj);
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
