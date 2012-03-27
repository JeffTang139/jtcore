/**
 * 
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.serial.DataObjectTranslator;

public class DOT_SingletonTranslator<TSingleton> implements
		DataObjectTranslator<TSingleton, Object> {
	final static short VERSION = 0x0100;
	final TSingleton singleton;

	public final boolean supportAssign() {
		return false;
	}

	public DOT_SingletonTranslator(TSingleton singleton) {
		this.singleton = singleton;
	}

	public Object toDelegateObject(TSingleton singleton) {
		return null;
	}

	public short getVersion() {
		return VERSION;
	}

	public TSingleton recoverObject(TSingleton destHint, Object object,
			ObjectQuerier querier, short version) {
		return this.singleton;
	}

	public short supportedVerionMin() {
		return VERSION;
	}
}