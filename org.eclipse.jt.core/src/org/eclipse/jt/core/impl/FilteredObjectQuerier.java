package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ObjectQuerier;

abstract class FilteredObjectQuerier extends ObjectQuerierImpl {
	final ObjectQuerier querier;

	FilteredObjectQuerier(ObjectQuerier querier) {
		if (querier == null) {
			throw new NullPointerException();
		}
		this.querier = querier;
	}

	protected abstract boolean isValidFacadeClass(Class<?> facadeClass);

	protected <TFacade> boolean isValidFacade(Class<TFacade> facadeClass,
			TFacade facade) {
		return true;
	}

	private final void checkFacadeClass(Class<?> facadeClass)
			throws UnsupportedOperationException {
		if (!this.isValidFacadeClass(facadeClass)) {
			throw new UnsupportedOperationException();
		}
	}

	private final <TFacade> TFacade checkFacade(Class<TFacade> facadeClass,
			TFacade facade) {
		if (facade != null && !this.isValidFacade(facadeClass, facade)) {
			return null;
		}
		return facade;
	}

	@Override
	public final <TFacade> TFacade find(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		this.checkFacadeClass(facadeClass);
		return this.checkFacade(facadeClass, this.querier.find(facadeClass));
	}

	@Override
	public final <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		this.checkFacadeClass(facadeClass);
		return this.checkFacade(facadeClass, this.querier
				.find(facadeClass, key));
	}

	@Override
	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		this.checkFacadeClass(facadeClass);
		return this.checkFacade(facadeClass, this.querier.find(facadeClass,
				key1, key2));
	}

	@Override
	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		this.checkFacadeClass(facadeClass);
		return this.checkFacade(facadeClass, this.querier.find(facadeClass,
				key1, key2, key3));
	}

	@Override
	public final <TFacade> TFacade find(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException {
		this.checkFacadeClass(facadeClass);
		return this.checkFacade(facadeClass, this.querier.find(facadeClass,
				key1, key2, keys));
	}
}
