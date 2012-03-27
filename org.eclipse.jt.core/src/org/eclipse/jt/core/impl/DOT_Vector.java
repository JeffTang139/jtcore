package org.eclipse.jt.core.impl;

import java.util.Vector;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;


@SuppressWarnings("unchecked")
final class DOT_Vector implements DataObjectTranslator<Vector, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_Vector() {

	}

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(Vector vector) {
		return vector.toArray();
	}

	public short getVersion() {
		return VERSION;
	}

	public Vector recoverObject(Vector destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final Vector vector;
		if (destHint == null) {
			vector = new Vector(objects.length);
		} else {
			destHint.clear();
			vector = destHint;
		}
		for (Object object : objects) {
			vector.add(object);
		}
		return vector;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
