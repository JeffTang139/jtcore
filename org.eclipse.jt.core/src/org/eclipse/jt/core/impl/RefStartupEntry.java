package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 引用启动步骤项
 * 
 * @author Jeff Tang
 * 
 * @param <TRef>
 */
class RefStartupEntry<TRef> extends StartupEntry {
	final TRef ref;

	RefStartupEntry(TRef ref) {
		if (ref == null) {
			throw new NullArgumentException("ref");
		}
		this.ref = ref;
	}
}
