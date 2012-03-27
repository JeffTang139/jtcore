package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;

public class DOT_UtilDate implements DataObjectTranslator<java.util.Date, Long> {
	final static short VERSION = 0x0100;

	private DOT_UtilDate() {

	}

	public final boolean supportAssign() {
		return false;
	}

	public Long toDelegateObject(java.util.Date date) {
		return date.getTime();
	}

	public short getVersion() {
		return VERSION;
	}

	public java.util.Date recoverObject(java.util.Date destHint, Long obj,
			ObjectQuerier querier, short version) {
		return new java.util.Date(obj);
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
