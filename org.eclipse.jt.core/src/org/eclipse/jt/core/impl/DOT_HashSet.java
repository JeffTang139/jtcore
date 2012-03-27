package org.eclipse.jt.core.impl;

import java.util.HashSet;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;


@SuppressWarnings("unchecked")
final class DOT_HashSet implements DataObjectTranslator<HashSet, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_HashSet() {

	}

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(HashSet hashSet) {
		return hashSet.toArray();
	}

	public short getVersion() {
		return VERSION;
	}

	public HashSet recoverObject(HashSet destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final HashSet hashSet;
		if (destHint == null) {
			hashSet = new HashSet(objects.length);
		} else {
			destHint.clear();
			hashSet = destHint;
		}
		for (Object object : objects) {
			hashSet.add(object);
		}
		return hashSet;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
