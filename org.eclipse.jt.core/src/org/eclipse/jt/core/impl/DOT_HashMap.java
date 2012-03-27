package org.eclipse.jt.core.impl;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;


@SuppressWarnings("unchecked")
final class DOT_HashMap implements DataObjectTranslator<HashMap, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_HashMap() {

	}

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(HashMap hashMap) {
		final Object[] objects = new Object[hashMap.size() * 2];
		int index = 0;
		final Set<Entry> entrySet = hashMap.entrySet();
		for (Entry ent : entrySet) {
			objects[index++] = ent.getKey();
			objects[index++] = ent.getValue();
		}
		return objects;
	}

	public short getVersion() {
		return VERSION;
	}

	public HashMap recoverObject(HashMap destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final HashMap hashMap;
		if (destHint == null) {
			hashMap = new HashMap(objects.length / 2);
		} else {
			destHint.clear();
			hashMap = destHint;
		}
		for (int i = 0, c = objects.length; i < c; i += 2) {
			hashMap.put(objects[i], objects[i + 1]);
		}
		return hashMap;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
