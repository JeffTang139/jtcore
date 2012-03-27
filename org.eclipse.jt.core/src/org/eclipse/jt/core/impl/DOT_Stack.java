package org.eclipse.jt.core.impl;

import java.util.Stack;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;


@SuppressWarnings("unchecked")
final class DOT_Stack implements DataObjectTranslator<Stack, Object[]> {
	final static short VERSION = 0x0100;

	private DOT_Stack() {

	}

	public final boolean supportAssign() {
		return true;
	}

	public Object[] toDelegateObject(Stack stack) {
		return stack.toArray();
	}

	public short getVersion() {
		return VERSION;
	}

	public Stack recoverObject(Stack destHint, Object[] objects,
			ObjectQuerier querier, short version) {
		final Stack stack;
		if (destHint == null) {
			stack = new Stack();
		} else {
			destHint.clear();
			stack = destHint;
		}
		for (Object object : objects) {
			stack.add(object);
		}
		return stack;
	}

	public short supportedVerionMin() {
		return VERSION;
	}

}
