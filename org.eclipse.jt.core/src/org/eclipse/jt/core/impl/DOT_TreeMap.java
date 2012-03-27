package org.eclipse.jt.core.impl;

import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;


@SuppressWarnings("unchecked")
final class DOT_TreeMap implements DataObjectTranslator<TreeMap, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_TreeMap() {

	}

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(TreeMap treeMap) {
		final Object[] objects = new Object[treeMap.size() * 2];
		int index = 0;
		final Set<Entry> entrySet = treeMap.entrySet();
		for (Entry ent : entrySet) {
			objects[index++] = ent.getKey();
			objects[index++] = ent.getValue();
		}
		return objects;
	}

	public short getVersion() {
		return VERSION;
	}

	public TreeMap recoverObject(TreeMap destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final TreeMap treeMap;
		if (destHint == null) {
			treeMap = new TreeMap();
		} else {
			destHint.clear();
			treeMap = destHint;
		}
		for (int i = 0, c = objects.length; i < c; i += 2) {
			treeMap.put(objects[i], objects[i + 1]);
		}
		return treeMap;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
