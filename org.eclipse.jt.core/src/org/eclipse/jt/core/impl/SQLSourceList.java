package org.eclipse.jt.core.impl;

class SQLSourceList implements SQLNameResolver {
	private SQLNameResolver[] list;

	public SQLSourceList(SQLNameResolver... resolvers) {
		this.list = resolvers;
	}

	public <T> T findProvider(Class<T> cls, String name) {
		for (SQLNameResolver r : this.list) {
			T ref = r.findProvider(cls, name);
			if (ref != null) {
				return ref;
			}
		}
		return null;
	}
}
