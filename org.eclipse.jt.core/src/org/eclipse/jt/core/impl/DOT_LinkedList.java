package org.eclipse.jt.core.impl;

import java.util.LinkedList;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;


@SuppressWarnings("unchecked")
final class DOT_LinkedList implements
		DataObjectTranslator<LinkedList, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_LinkedList() {

	}

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(LinkedList linkedList) {
		return linkedList.toArray();
	}

	public short getVersion() {
		return VERSION;
	}

	public LinkedList recoverObject(LinkedList destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final LinkedList linkedList;
		if (destHint == null) {
			linkedList = new LinkedList();
		} else {
			destHint.clear();
			linkedList = destHint;
		}
		for (Object object : objects) {
			linkedList.add(object);
		}
		return linkedList;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
