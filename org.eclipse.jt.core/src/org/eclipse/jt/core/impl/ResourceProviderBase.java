package org.eclipse.jt.core.impl;

import java.lang.reflect.Method;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.impl.ResourceServiceBase.KeyPathInfo;
import org.eclipse.jt.core.impl.ResourceServiceBase.ResourceIndexInfo;
import org.eclipse.jt.core.resource.ResourceInserter;


/**
 * 资源提供器基类
 * 
 * @author Jeff Tang
 * 
 */
abstract class ResourceProviderBase<TFacade, TImpl extends TFacade, TKeysHolder, TKey1, TKey2, TKey3> {
	final boolean isProvideOverridden;

	ResourceProviderBase() {
		this.isProvideOverridden = Utils.overridden(
		        this.getProvideMethodBase(), this.getClass(), null);
	}

	abstract Method getProvideMethodBase();

	abstract KeyPathInfo buildKeyPathInfo(
	        ResourceServiceBase<?, ?, ?> fromResourceService,
	        ResourceServiceBase<?, ?, ?> toResourceService, KeyPathInfo parent,
	        ResourceIndexInfo indexInfo);

	abstract byte getKeyCount();

	/**
	 * 根据资源键源返回当前提供器对应的键
	 * 
	 * @param keys
	 *            资源键源
	 * @return 返回当前提供器对应的键
	 */
	protected TKey1 getKey1(TKeysHolder keys) {
		return null;
	}

	/**
	 * 根据资源键源返回当前提供器对应的键
	 * 
	 * @param keys
	 *            资源键源
	 * @return 返回当前提供器对应的键
	 */
	protected TKey2 getKey2(TKeysHolder keys) {
		return null;
	}

	/**
	 * 根据资源键源返回当前提供器对应的键
	 * 
	 * @param keys
	 *            资源键源
	 * @return 返回当前提供器对应的键
	 */
	protected TKey3 getKey3(TKeysHolder keys) {
		return null;
	}

	TKey1 getKey1(
	        ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry) {
		return null;
	}

	TKey2 getKey2(
	        ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry) {
		return null;
	}

	TKey3 getKey3(
	        ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry) {
		return null;
	}

	abstract boolean keysEqual(TKeysHolder keys, TKey1 key1, TKey2 key2,
	        TKey3 key3);

	/**
	 * 新建Index的实例
	 * 
	 * @param group
	 *            对应的资源容器
	 * @return 新创建的Index实例
	 */
	abstract ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> newIndex(
	        ResourceGroup<TFacade, TImpl, TKeysHolder> group);

	/**
	 * 新建IndexEntry的实例
	 * 
	 * @param hash
	 *            - 资源的hash值
	 * @param resourceItem
	 *            - 资源项
	 * @param next
	 *            - 要链接的下一个IndexEntry实例
	 * @return 新创建的IndexEntry实例
	 */
	abstract ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> newIndexEntry(
	        ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> index,
	        int hash,
	        ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
	        ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> next,
	        TKey1 key1, TKey2 key2, TKey3 key3);

	/**
	 * 提供单例索引
	 * 
	 * @param context
	 *            上下文
	 * @param setter
	 *            资源设置器
	 * @throws Throwable
	 *             抛出异常
	 */
	void provide(Context context,
	        ResourceInserter<TFacade, TImpl, TKeysHolder> setter)
	        throws Throwable {
		throw new UnsupportedOperationException();
	}

	/**
	 * 提供资源，根据具体需求，或创建新的，或从其他地方获取（如数据库）
	 * 
	 * @param context
	 *            上下文
	 * @param setter
	 *            设置器
	 * @param key
	 *            对应的键
	 * @throws Throwable
	 *             抛出异常
	 */
	void provide(Context context,
	        ResourceInserter<TFacade, TImpl, TKeysHolder> setter, TKey1 key)
	        throws Throwable {
		throw new UnsupportedOperationException();
	}

	/**
	 * 提供资源，根据具体需求，或创建新的，或从其他地方获取（如数据库）
	 * 
	 * @param context
	 *            上下文
	 * @param setter
	 *            设置器
	 * @param key
	 *            对应的键
	 * @throws Throwable
	 *             抛出异常
	 */
	void provide(Context context,
	        ResourceInserter<TFacade, TImpl, TKeysHolder> setter, TKey1 key1,
	        TKey2 key2) throws Throwable {
		throw new UnsupportedOperationException();
	}

	/**
	 * 提供资源，根据具体需求，或创建新的，或从其他地方获取（如数据库）
	 * 
	 * @param context
	 *            上下文
	 * @param setter
	 *            设置器
	 * @param key
	 *            对应的键
	 * @throws Throwable
	 *             抛出异常
	 */
	void provide(Context context,
	        ResourceInserter<TFacade, TImpl, TKeysHolder> setter, TKey1 key1,
	        TKey2 key2, TKey3 key3) throws Throwable {
		throw new UnsupportedOperationException();
	}
}
