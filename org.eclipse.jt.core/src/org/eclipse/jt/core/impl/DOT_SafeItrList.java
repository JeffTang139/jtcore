package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.misc.SafeItrList;
import org.eclipse.jt.core.serial.DataObjectTranslator;

@SuppressWarnings("unchecked")
final class DOT_SafeItrList implements
		DataObjectTranslator<SafeItrList, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_SafeItrList() {

	}

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(SafeItrList safeItrList) {
		return safeItrList.toArray();
	}

	public short getVersion() {
		return VERSION;
	}

	public SafeItrList recoverObject(SafeItrList destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final SafeItrList safeItrList;
		if (destHint == null) {
			safeItrList = new SafeItrList(objects.length);
		} else {
			destHint.clear();
			safeItrList = destHint;
		}
		for (Object object : objects) {
			safeItrList.add(object);
		}
		return safeItrList;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
