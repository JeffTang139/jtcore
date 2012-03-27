/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File IndexEntry.java
 * Date 2008-7-16
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.resource.ResourceTokenLink;

/**
 * 索引项
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class ResourceIndexEntry<TFacade, TImpl extends TFacade, TKeysHolder, TKey1, TKey2, TKey3>
		extends ResourceEntry<TFacade, TImpl, TKeysHolder> implements
		ResourceTokenLink<TFacade> {
	final int hash;
	ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> next;

	volatile State state;

	static enum State {
		/**
		 * 目前仅用于标记冗余（记录的键值与实际对象不符）的项。 如果使用些项，则表示对应的IndexEntry已经被标记为删除或者已经删除。
		 */
		REMOEVED;
	}

	ResourceIndexEntry(
			ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> index,
			int hash,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> next) {
		super(index);
		if (resourceItem == null) {
			throw new NullPointerException();
		}
		this.hash = hash;
		this.resourceItem = resourceItem;
		this.next = next;
	}

	@Override
	final ResourceIndexEntry<TFacade, TImpl, TKeysHolder, ?, ?, ?> asIndexEntry() {
		return this;
	}

	/**
	 * 判断给定的键值是否与本Entry中的资源的键值相等
	 * 
	 * @param <TKey1>
	 * @param <TKey2>
	 * @param <TKey3>
	 * @param key1
	 * @param key2
	 * @param key3
	 * @return
	 */
	abstract boolean keysEqual(TKey1 key1, TKey2 key2, TKey3 key3);

	@SuppressWarnings("unchecked")
	final boolean keysEqual(TKeysHolder keys) {
		ResourceProviderBase<?, ?, TKeysHolder, TKey1, TKey2, TKey3> provider = ((ResourceIndex) this.holder).provider;
		return this.keysEqual(provider.getKey1(keys), provider.getKey2(keys),
				provider.getKey3(keys));
	}

	public final ResourceItem<TFacade, TImpl, TKeysHolder> getToken() {
		return this.resourceItem;
	}

	public final ResourceIndexEntry<TFacade, TImpl, TKeysHolder, ?, ?, ?> next() {
		return this.internalNext(null);
	}

	@Override
	@SuppressWarnings("unchecked")
	ResourceIndexEntry<TFacade, TImpl, TKeysHolder, ?, ?, ?> internalNext(
			TransactionImpl transaction) {
		ResourceIndexEntry e = this.next;
		while (e != null) {
			final ResourceItem item = e.resourceItem;
			if (item != null) {
				final Acquirer<?, ?> aq = item.acquirer;
				if ((aq != null && aq.similarTransaction(transaction)) ? item.state == ResourceItem.State.REMOVED
						: item.state == ResourceItem.State.FILLED) {
					e = e.next;
					continue;
				}
			}
			return e;
		}
		return ((ResourceIndex) this.holder).lockNextOf(this, transaction);
	}
}

/**
 * 无键索引项
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class NoneKeyIndexEntry<TFacade, TImpl extends TFacade, TKeysHolder>
		extends
		ResourceIndexEntry<TFacade, TImpl, TKeysHolder, None, None, None> {
	NoneKeyIndexEntry(
			ResourceIndex<TFacade, TImpl, TKeysHolder, None, None, None> index,
			int hash,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			ResourceIndexEntry<TFacade, TImpl, TKeysHolder, None, None, None> next) {
		super(index, hash, resourceItem, next);
	}

	@Override
	final boolean keysEqual(None key1, None key2, None key3) {
		return key1 == null;
	}

	@Override
	final ResourceIndexEntry<TFacade, TImpl, TKeysHolder, ?, ?, ?> internalNext(
			TransactionImpl transaction) {
		return null;
	}
}

/**
 * 单键索引项
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class OneKeyIndexEntry<TFacade, TImpl extends TFacade, TKeysHolder, TKey>
		extends
		ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey, None, None> {
	final TKey key;

	OneKeyIndexEntry(
			ResourceIndex<TFacade, TImpl, TKeysHolder, TKey, None, None> index,
			int hash,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey, None, None> next,
			TKey key) {
		super(index, hash, resourceItem, next);
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		this.key = key;
	}

	@Override
	boolean keysEqual(TKey key1, None key2, None key3) {
		return this.key.equals(key1);
	}
}

/**
 * 双键索引项
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class TwoKeyIndexEntry<TFacade, TImpl extends TFacade, TKeysHolder, TKey1, TKey2>
		extends
		ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> {
	final TKey1 key1;
	final TKey2 key2;

	TwoKeyIndexEntry(
			ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> index,
			int hash,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> next,
			TKey1 key1, TKey2 key2) {
		super(index, hash, resourceItem, next);
		if (key1 == null || key2 == null) {
			throw new NullPointerException("some key is null");
		}
		this.key1 = key1;
		this.key2 = key2;
	}

	@Override
	boolean keysEqual(TKey1 key1, TKey2 key2, None key3) {
		return this.key1.equals(key1) && this.key2.equals(key2);
	}
}

/**
 * 三键索引项
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class ThreeKeyIndexEntry<TFacade, TImpl extends TFacade, TKeysHolder, TKey1, TKey2, TKey3>
		extends
		ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> {
	final TKey1 key1;
	final TKey2 key2;
	final TKey3 key3;

	ThreeKeyIndexEntry(
			ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> index,
			int hash,
			ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
			ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> next,
			TKey1 key1, TKey2 key2, TKey3 key3) {
		super(index, hash, resourceItem, next);
		if (key1 == null || key2 == null || key3 == null) {
			throw new NullPointerException("some key is null");
		}
		this.key1 = key1;
		this.key2 = key2;
		this.key3 = key3;
	}

	@Override
	boolean keysEqual(TKey1 key1, TKey2 key2, TKey3 key3) {
		return this.key1.equals(key1) && this.key2.equals(key2)
				&& this.key3.equals(key3);
	}
}
