package org.eclipse.jt.core.impl;

import java.util.TreeSet;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;


@SuppressWarnings("unchecked")
final class DOT_TreeSet implements DataObjectTranslator<TreeSet, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_TreeSet() {

	}

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(TreeSet treeSet) {
		return treeSet.toArray();
	}

	public short getVersion() {
		return VERSION;
	}

	public TreeSet recoverObject(TreeSet destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final TreeSet treeSet;
		if (destHint == null) {
			treeSet = new TreeSet();
		} else {
			destHint.clear();
			treeSet = destHint;
		}
		for (Object object : objects) {
			treeSet.add(object);
		}
		return treeSet;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
