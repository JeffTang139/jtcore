package org.eclipse.jt.core.impl;

import java.util.Hashtable;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;


@SuppressWarnings("unchecked")
final class DOT_Hashtable implements DataObjectTranslator<Hashtable, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_Hashtable() {

	}

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(Hashtable hashTable) {
		final Object[] objects = new Object[hashTable.size() * 2];
		int index = 0;
		final Set<Entry> entrySet = hashTable.entrySet();
		for (Entry ent : entrySet) {
			objects[index++] = ent.getKey();
			objects[index++] = ent.getValue();
		}
		return objects;
	}

	public short getVersion() {
		return VERSION;
	}

	public Hashtable recoverObject(Hashtable destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final Hashtable hashTable;
		if (destHint == null) {
			hashTable = new Hashtable(objects.length / 2);
		} else {
			destHint.clear();
			hashTable = destHint;
		}
		for (int i = 0, c = objects.length; i < c; i += 2) {
			hashTable.put(objects[i], objects[i + 1]);
		}
		return hashTable;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}