package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;

@SuppressWarnings("unchecked")
final class DOT_DnaArrayList implements
		DataObjectTranslator<DnaArrayList, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_DnaArrayList() {

	}

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(DnaArrayList dnaArrayList) {
		return dnaArrayList.toArray();
	}

	public short getVersion() {
		return VERSION;
	}

	public DnaArrayList recoverObject(DnaArrayList destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final DnaArrayList dnaArrayList;
		if (destHint == null) {
			dnaArrayList = new DnaArrayList(objects.length);
		} else {
			destHint.clear();
			dnaArrayList = destHint;
		}
		for (Object object : objects) {
			dnaArrayList.add(object);
		}
		return dnaArrayList;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
