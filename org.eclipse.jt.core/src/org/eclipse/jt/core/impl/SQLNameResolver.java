package org.eclipse.jt.core.impl;

interface SQLNameResolver {
	public <T> T findProvider(Class<T> cls, String name);
}
