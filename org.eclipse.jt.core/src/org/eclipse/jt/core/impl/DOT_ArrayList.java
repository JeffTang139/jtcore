package org.eclipse.jt.core.impl;

import java.util.ArrayList;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;


@SuppressWarnings("unchecked")
final class DOT_ArrayList implements DataObjectTranslator<ArrayList, Object[]> {
	final static short VERSION = 0x0100;

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(ArrayList arrayList) {
		return arrayList.toArray();
	}

	public short getVersion() {
		return VERSION;
	}

	public ArrayList recoverObject(ArrayList destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final ArrayList arrayList;
		if (destHint == null) {
			arrayList = new ArrayList(objects.length);
		} else {
			destHint.clear();
			arrayList = destHint;
		}
		for (Object object : objects) {
			arrayList.add(object);
		}
		return arrayList;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
