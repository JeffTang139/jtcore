package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.misc.MissingObjectException;

abstract class ObjectQuerierImpl implements ObjectQuerier {

	public <TFacade> TFacade find(Class<TFacade> facadeClass)
	        throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
	        throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
	        Object key2) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
	        Object key2, Object key3, Object... keys)
	        throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass)
	        throws UnsupportedOperationException, MissingObjectException {
		TFacade r = this.find(facadeClass);
		if (r == null) {
			throw missing(facadeClass, null, null, null, null);
		}
		return r;
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key)
	        throws UnsupportedOperationException, MissingObjectException {
		TFacade r = this.find(facadeClass, key);
		if (r == null) {
			throw missing(facadeClass, key, null, null, null);
		}
		return r;
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
	        Object key2) throws UnsupportedOperationException,
	        MissingObjectException {
		TFacade r = this.find(facadeClass, key1, key2);
		if (r == null) {
			throw missing(facadeClass, key1, key2, null, null);
		}
		return r;
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException,
	        MissingObjectException {
		TFacade r = this.find(facadeClass, key1, key2, key3);
		if (r == null) {
			throw missing(facadeClass, key1, key2, key3, null);
		}
		return r;
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
	        Object key2, Object key3, Object... keys)
	        throws UnsupportedOperationException, MissingObjectException {
		TFacade r = this.find(facadeClass, key1, key2, key3, keys);
		if (r == null) {
			throw missing(facadeClass, key1, key2, key3, keys);
		}
		return r;
	}

	private static final MissingObjectException missing(Class<?> facadeClass,
	        Object key1, Object key2, Object key3, Object[] keys) {
		StringBuilder sb = new StringBuilder("找不到外观为[");
		sb.append(facadeClass.getName()).append(']');
		if (key1 != null) {
			sb.append("键为(").append(key1);
			if (key2 != null) {
				sb.append(',').append(key2);
				if (key3 != null) {
					sb.append(',').append(key3);
					if (keys != null) {
						for (int i = 0; i < keys.length; i++) {
							sb.append(',').append(keys[i]);
						}
					}
				}
			}
			sb.append(')');
		}
		sb.append("的对象");
		throw new MissingObjectException(sb.toString());
	}
}
