package org.eclipse.jt.core.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.eclipse.jt.core.Category;
import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.misc.HashUtil;
import org.eclipse.jt.core.misc.SortUtil;
import org.eclipse.jt.core.misc.TypeArgFinder;
import org.eclipse.jt.core.resource.ResourceCategory;
import org.eclipse.jt.core.resource.ResourceContext;
import org.eclipse.jt.core.resource.ResourceInserter;
import org.eclipse.jt.core.resource.ResourceKind;
import org.eclipse.jt.core.resource.ResourceService;
import org.eclipse.jt.core.resource.ResourceService.OperationMap;
import org.eclipse.jt.core.service.Publish;
import org.eclipse.jt.core.service.UsingDeclarator;
import org.eclipse.jt.core.type.GUID;


/**
 * 资源管理器
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            资源外观，即资源实现提供的只读接口
 * @param <TImpl>
 *            资源修改器，既可以用来修改资源的接口或者类型，大部分时候使用资源的实现类型
 * @param <TKeysHolder>
 *            资源键源，既可以从中得到资源的键的值的接口或者类型，大部分时候使用资源的实现类型
 */
public abstract class ResourceServiceBase<TFacade, TImpl extends TFacade, TKeysHolder>
		extends ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>> {
	/**
	 * 默认的过滤器
	 */
	protected boolean defaultAccept(TImpl item) {
		return true;
	}

	/**
	 * 默认的排序比较器
	 */
	protected int defaultSortCompare(TImpl a, TImpl b) {
		return 0;
	}

	private static final Method defaultAccept = Utils.getMethod(
			ResourceServiceBase.class, "defaultAccept", Object.class);
	private static final Method initResources = Utils.getMethod(
			ResourceServiceBase.class, "initResources", Context.class,
			ResourceInserter.class);
	private final static Class<?>[] abstractResourceServiceClasses = { ResourceService.class };
	private static final Method defaultSortCompare = Utils.getMethod(
			ResourceServiceBase.class, "defaultSortCompare", Object.class,
			Object.class);

	// TODO 资源销毁
	// 暂时使用一下HashMap，以后再优化
	// 如果是全局资源将在注册资源类别时创建Group
	private volatile HashMap<Object, Object> groupsByCategory;
	// 如果是全局资源将在服务初始化之前创建
	private volatile ResourceGroup<?, ?, ?> defaultGroup;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	final boolean categorySupported(Object category) {
		this.lock.readLock().lock();
		try {
			if (this.groupsByCategory == null) {
				return category == None.NONE;
			} else {
				return this.groupsByCategory.containsKey(category);
			}
		} finally {
			this.lock.readLock().unlock();
		}
	}

	/**
	 * 注册资源类别。
	 * 
	 * <pre>
	 *  资源的类别必须为以下类型中的一种：
	 *      1. 字符串；
	 *      2. 任何枚举类型；
	 *      3. 原始类型的装箱类型；
	 *      4. org.eclipse.jt.core.type.GUID；
	 *      5. org.eclipse.jt.core.resource.ResourceCategory的实现类。
	 * </pre>
	 * 
	 * @param category
	 *            类别，可以为None.NONE,表示默认的类别
	 */
	@SuppressWarnings("unchecked")
	protected void registerCategory(Object category, String title) {
		if (category == null) {
			throw new NullArgumentException("category");
		}
		if (title == null || title.length() == 0) {
			throw new NullArgumentException("title");
		}
		ResourceGroup<?, ?, ?> defaultG = null;
		this.lock.writeLock().lock();
		try {
			if (category instanceof ResourceCategory) {
				Object cid = ((ResourceCategory) category).getIdentifier();
				if (!ResourceCategory.Helper.isSupportedIdType(cid.getClass())) {
					throw new UnsupportedOperationException("资源类别的标识符的类型不受支持："
							+ cid.getClass());
				}
			} else {
				if (!ResourceCategory.Helper.isSupportedIdType(category
						.getClass())) {
					throw new UnsupportedOperationException("不支持（"
							+ category.getClass() + "）类型的资源类别");
				}
			}
			if (this.groupsByCategory == null) {
				if (category == None.NONE) {
					return;
				} else {
					defaultG = this.defaultGroup;
					this.defaultGroup = null;
				}
				this.groupsByCategory = new HashMap<Object, Object>();
			} else if (this.groupsByCategory.containsKey(category)) {
				return;// 已经注册过
			} else if (category == None.NONE) {
				throw new UnsupportedOperationException("不支持注册None.NONE作为资源类别");
			}
			if (this.kind.isGlobal) {
				this.groupsByCategory.put(category, new ResourceGroup(title,
						null, this, category));
			} else {
				this.groupsByCategory.put(category, title);
			}
		} finally {
			this.lock.writeLock().unlock();
		}
		if (defaultG != null) {
			defaultG.reset(this.site.application.catcher, true);
		}
	}

	/**
	 * 注销资源类别
	 * 
	 * @param category
	 *            类别，可以为null,表示默认的类别
	 */
	@SuppressWarnings("unchecked")
	protected void unRegisterCategory(Object category) {
		if (category == null) {
			throw new NullPointerException();
		}
		final Object group;
		this.lock.writeLock().lock();
		try {
			if (this.groupsByCategory != null) {
				group = this.groupsByCategory.remove(category);
				if (this.groupsByCategory.size() == 0) {
					this.groupsByCategory = null;
					if (this.kind.isGlobal) {
						this.defaultGroup = new ResourceGroup(this.title, null,
								this, None.NONE);
					}
				}
			} else {
				return;
			}
		} finally {
			this.lock.writeLock().unlock();
		}
		if (this.kind.isGlobal) {
			((ResourceGroup<?, ?, ?>) group).reset(
					this.site.application.catcher, true);
		}
	}

	/**
	 * 初始化资源，添加资源
	 * 
	 * @param context
	 *            上下文
	 * @param initializer
	 *            资源初始器
	 */
	protected void initResources(Context context,
			ResourceInserter<TFacade, TImpl, TKeysHolder> initializer)
			throws Throwable {
	}

	/**
	 * 定义初始化所依赖的调用（可选）
	 */
	protected void initResourcesUsing(UsingDeclarator using) {
	}

	/**
	 * 资源销毁时调用
	 * 
	 * @param facade
	 *            资源外观
	 * @param impl
	 *            资源修改器
	 * @param keys
	 *            资源键源
	 */
	protected void disposeResource(TImpl impl, TKeysHolder keys,
			ExceptionCatcher catcher) throws Throwable {
	}

	private final static Method noneKeyProvideBase = Utils.getMethod(
			ResourceServiceBase.SingletonResourceProvider.class, "provide",
			Context.class, ResourceInserter.class);

	protected abstract class SingletonResourceProvider extends
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, None, None, None> {

		@Override
		final Method getProvideMethodBase() {
			return noneKeyProvideBase;
		}

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
		@Override
		protected void provide(Context context,
				ResourceInserter<TFacade, TImpl, TKeysHolder> setter)
				throws Throwable {
			// nothing
		}

		// //////////////////////////////////////
		@Override
		final KeyPathInfo buildKeyPathInfo(
				ResourceServiceBase<?, ?, ?> fromResourceService,
				ResourceServiceBase<?, ?, ?> toResourceService,
				KeyPathInfo parent, ResourceIndexInfo indexInfo) {
			if (parent == null) {
				return toResourceService.ensureKeyPathInfo(fromResourceService,
						null, indexInfo);
			} else {
				return parent.ensureSubKeyPathInfo(null, indexInfo);
			}
		}

		@Override
		final byte getKeyCount() {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jt.core.impl.ResourceProviderBase#newIndex(org.eclipse.jt.core.impl.ResourceGroup)
		 */
		@Override
		final ResourceIndex<TFacade, TImpl, TKeysHolder, None, None, None> newIndex(
				ResourceGroup<TFacade, TImpl, TKeysHolder> group) {
			return new ResourceIndex<TFacade, TImpl, TKeysHolder, None, None, None>(
					group, this);
		}

		@Override
		final ResourceIndexEntry<TFacade, TImpl, TKeysHolder, None, None, None> newIndexEntry(
				ResourceIndex<TFacade, TImpl, TKeysHolder, None, None, None> index,
				int hash,
				ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, None, None, None> next,
				None key1, None key2, None key3) {
			return new NoneKeyIndexEntry<TFacade, TImpl, TKeysHolder>(index,
					hash, resourceItem, next);
		}

		@Override
		final boolean keysEqual(TKeysHolder keys, None key1, None key2,
				None key3) {
			return key1 == null;
		}
	}

	private final static Method oneKeyProvideBase = Utils.getMethod(
			ResourceServiceBase.OneKeyResourceProvider.class, "provide",
			Context.class, ResourceInserter.class, Object.class);

	/**
	 * 有序键资源提供器
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TKey>
	 *            键类型
	 */
	protected abstract class OneKeyResourceProvider<TKey> extends
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey, None, None> {
		@Override
		final Method getProvideMethodBase() {
			return oneKeyProvideBase;
		}

		/**
		 * 根据资源键源返回当前提供器对应的键
		 * 
		 * @param keys
		 *            资源键源
		 * @return 返回当前提供器对应的键
		 */
		@Override
		protected abstract TKey getKey1(TKeysHolder keys);

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
		@Override
		protected void provide(Context context,
				ResourceInserter<TFacade, TImpl, TKeysHolder> setter, TKey key)
				throws Throwable {
			// nothing
		}

		// /////////////////////////////////////////////////////////////
		final Class<?> keyClass;

		protected OneKeyResourceProvider() {
			this.keyClass = TypeArgFinder.get(this.getClass(),
					OneKeyResourceProvider.class, 0);
		}

		@Override
		final KeyPathInfo buildKeyPathInfo(
				ResourceServiceBase<?, ?, ?> fromResourceService,
				ResourceServiceBase<?, ?, ?> toResourceService,
				KeyPathInfo parent, ResourceIndexInfo indexInfo) {
			if (parent == null) {
				return toResourceService.ensureKeyPathInfo(fromResourceService,
						this.keyClass, indexInfo);
			} else {
				return parent.ensureSubKeyPathInfo(this.keyClass, indexInfo);
			}
		}

		@Override
		final byte getKeyCount() {
			return 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jt.core.impl.ResourceProviderBase#newIndex(org.eclipse.jt.core.impl.ResourceGroup)
		 */
		@Override
		final ResourceIndex<TFacade, TImpl, TKeysHolder, TKey, None, None> newIndex(
				ResourceGroup<TFacade, TImpl, TKeysHolder> group) {
			return new ResourceIndex<TFacade, TImpl, TKeysHolder, TKey, None, None>(
					group, this);
		}

		@Override
		final ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey, None, None> newIndexEntry(
				ResourceIndex<TFacade, TImpl, TKeysHolder, TKey, None, None> index,
				int hash,
				ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey, None, None> next,
				TKey key1, None key2, None key3) {
			return new OneKeyIndexEntry<TFacade, TImpl, TKeysHolder, TKey>(
					index, hash, resourceItem, next, key1);
		}

		@Override
		final TKey getKey1(
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey, None, None> entry) {
			return ((OneKeyIndexEntry<TFacade, TImpl, TKeysHolder, TKey>) entry).key;
		}

		@Override
		final boolean keysEqual(TKeysHolder keys, TKey key1, None key2,
				None key3) {
			return this.getKey1(keys).equals(key1);
		}
	}

	/**
	 * 权限支持
	 */
	AuthorizableResourceProvider<?> authorizableResourceProvider;

	final boolean isAuthorizable() {
		return this.authorizableResourceProvider != null
				&& this.authorizableResourceProvider.providerIndex >= 0;
	}

	/**
	 * 标明该资源需要权限授权的资源
	 */
	protected abstract class AuthorizableResourceProvider<TOperationEnum extends Enum<? extends Operation<? super TFacade>>>
			extends OneKeyResourceProvider<GUID> {

		/**
		 * 返回某个资源的标题用于权限设置使用
		 */
		protected abstract String getResourceTitle(TImpl resource,
				TKeysHolder keys);

		final GUID customCategoryID;
		/**
		 * 权限键提供器位置
		 */
		int providerIndex = -1;

		/**
		 * 构造方法
		 * 
		 * @param looseAuthPolicy
		 *            是否应用宽松权限控制策略（默认有权限）
		 */
		protected AuthorizableResourceProvider(GUID customCategoryID,
				boolean looseAuthPolicy) {
			super();
			if (!ResourceServiceBase.this.kind.isGlobal) {
				throw new UnsupportedOperationException("只有全局资源才支持权限验证！资源服务：["
						+ ResourceServiceBase.this.getClass().getName() + "]");
			}
			if (customCategoryID != null && customCategoryID.isEmpty()) {
				throw new IllegalArgumentException("customCategoryID 不可为Empty");
			}
			this.customCategoryID = customCategoryID;
			this.operationEnumClass = TypeArgFinder.get(this.getClass(),
					AuthorizableResourceProvider.class, 0);
			final Object[] enumConstants = this.operationEnumClass
					.getEnumConstants();
			this.operations = new OperationEntry[enumConstants.length];
			for (int i = 0; i < enumConstants.length; i++) {
				this.operations[i] = new OperationEntry(
						(Operation<?>) enumConstants[i], i);
			}
			this.defaultAuth = looseAuthPolicy;
		}

		/**
		 * 操作枚举的类型
		 */
		final Class<?> operationEnumClass;
		/**
		 * 操作对应的权限操作项
		 */
		final OperationEntry[] operations;

		/**
		 * 根据操作获取权限操作掩码，只有高32位有效
		 */
		final long getAuthMask(Enum<?> opEnum) {
			try {
				OperationEntry entry = this.operations[opEnum.ordinal()];
				if (entry.operation == opEnum) {
					return entry.authMask;
				}
			} catch (Throwable e) {
			}
			throw new IllegalArgumentException("资源["
					+ ResourceServiceBase.this.facadeClass + "] 不支持[" + opEnum
					+ "]类型的权限操作");
		}

		final OperationEntry getOperationEntry(Enum<?> opEnum) {
			try {
				OperationEntry entry = this.operations[opEnum.ordinal()];
				if (entry.operation == opEnum) {
					return entry;
				}
			} catch (Throwable e) {
			}
			throw new IllegalArgumentException("资源["
					+ ResourceServiceBase.this.facadeClass + "] 不支持[" + opEnum
					+ "]类型的权限操作");
		}

		boolean defaultAuth;

	}

	protected void beforeAccessAuthorityResource(Context context) {

	}

	protected void endAccessAuthorityResource(Context context) {

	}

	/**
	 * 权限相关，获取操作项
	 */
	final OperationEntry getOperationEntry(Operation<? super TFacade> operation) {
		if (this.authorizableResourceProvider == null) {
			throw new UnsupportedOperationException("资源["
					+ this.facadeClass.getName() + "]为非权限管理资源类别。");
		}
		return this.authorizableResourceProvider
				.getOperationEntry((Enum<?>) operation);
	}

	/**
	 * 权限相关，获取默认授权
	 */
	final boolean getDefaultAuth() {
		if (this.authorizableResourceProvider == null) {
			throw new UnsupportedOperationException("资源["
					+ this.facadeClass.getName() + "]为非权限管理资源类别。");
		}
		return this.authorizableResourceProvider.defaultAuth;
	}

	private final static Method twoKeyProvideBase = Utils.getMethod(
			ResourceServiceBase.TwoKeyResourceProvider.class, "provide",
			Context.class, ResourceInserter.class, Object.class, Object.class);

	/**
	 * 有序键资源提供器
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TKey>
	 *            键类型
	 */
	protected abstract class TwoKeyResourceProvider<TKey1, TKey2>
			extends
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> {
		@Override
		final Method getProvideMethodBase() {
			return twoKeyProvideBase;
		}

		/**
		 * 根据资源键源返回当前提供器对应的键
		 * 
		 * @param keys
		 *            资源键源
		 * @return 返回当前提供器对应的键
		 */
		@Override
		protected abstract TKey1 getKey1(TKeysHolder keys);

		/**
		 * 根据资源键源返回当前提供器对应的键
		 * 
		 * @param keys
		 *            资源键源
		 * @return 返回当前提供器对应的键
		 */
		@Override
		protected abstract TKey2 getKey2(TKeysHolder keys);

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
		@Override
		protected void provide(Context context,
				ResourceInserter<TFacade, TImpl, TKeysHolder> setter,
				TKey1 key1, TKey2 key2) throws Throwable {
			// nothing
		}

		// //////////////////////////////////////////////////////////
		final Class<?> key1Class;
		final Class<?> key2Class;

		protected TwoKeyResourceProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					TwoKeyResourceProvider.class);
			this.key1Class = types[0];
			this.key2Class = types[1];
		}

		@Override
		final KeyPathInfo buildKeyPathInfo(
				ResourceServiceBase<?, ?, ?> fromResourceService,
				ResourceServiceBase<?, ?, ?> toResourceService,
				KeyPathInfo parent, ResourceIndexInfo indexInfo) {
			KeyPathInfo kpi = parent == null ? toResourceService
					.ensureKeyPathInfo(fromResourceService, this.key1Class,
							null) : parent.ensureSubKeyPathInfo(this.key1Class,
					null);
			return kpi.ensureSubKeyPathInfo(this.key2Class, indexInfo);
		}

		@Override
		final byte getKeyCount() {
			return 2;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jt.core.impl.ResourceProviderBase#newIndex(org.eclipse.jt.core.impl.ResourceGroup)
		 */
		@Override
		final ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> newIndex(
				ResourceGroup<TFacade, TImpl, TKeysHolder> group) {
			return new ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None>(
					group, this);
		}

		@Override
		final ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> newIndexEntry(
				ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> index,
				int hash,
				ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> next,
				TKey1 key1, TKey2 key2, None key3) {
			return new TwoKeyIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2>(
					index, hash, resourceItem, next, key1, key2);
		}

		@Override
		final TKey1 getKey1(
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> entry) {
			return ((TwoKeyIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2>) entry).key1;
		}

		@Override
		final TKey2 getKey2(
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> entry) {
			return ((TwoKeyIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2>) entry).key2;
		}

		@Override
		final boolean keysEqual(TKeysHolder keys, TKey1 key1, TKey2 key2,
				None key3) {
			return this.getKey1(keys).equals(key1)
					&& this.getKey2(keys).equals(key2);
		}
	}

	private final static Method threeKeyProvideBase = Utils.getMethod(
			ResourceServiceBase.ThreeKeyResourceProvider.class, "provide",
			Context.class, ResourceInserter.class, Object.class, Object.class);

	/**
	 * 有序键资源提供器
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TKey>
	 *            键类型
	 */
	protected abstract class ThreeKeyResourceProvider<TKey1, TKey2, TKey3>
			extends
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> {
		@Override
		final Method getProvideMethodBase() {
			return threeKeyProvideBase;
		}

		/**
		 * 根据资源键源返回当前提供器对应的键
		 * 
		 * @param keys
		 *            资源键源
		 * @return 返回当前提供器对应的键
		 */
		@Override
		protected abstract TKey1 getKey1(TKeysHolder keys);

		/**
		 * 根据资源键源返回当前提供器对应的键
		 * 
		 * @param keys
		 *            资源键源
		 * @return 返回当前提供器对应的键
		 */
		@Override
		protected abstract TKey2 getKey2(TKeysHolder keys);

		/**
		 * 根据资源键源返回当前提供器对应的键
		 * 
		 * @param keys
		 *            资源键源
		 * @return 返回当前提供器对应的键
		 */
		@Override
		protected abstract TKey3 getKey3(TKeysHolder keys);

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
		@Override
		protected void provide(Context context,
				ResourceInserter<TFacade, TImpl, TKeysHolder> setter,
				TKey1 key1, TKey2 key2, TKey3 key3) throws Throwable {
			// nothing
		}

		// //////////////////////////////////////////////////////////
		final Class<?> key1Class;
		final Class<?> key2Class;
		final Class<?> key3Class;

		protected ThreeKeyResourceProvider() {
			Class<?>[] types = TypeArgFinder.get(this.getClass(),
					ThreeKeyResourceProvider.class);
			this.key1Class = types[0];
			this.key2Class = types[1];
			this.key3Class = types[2];
		}

		@Override
		final KeyPathInfo buildKeyPathInfo(
				ResourceServiceBase<?, ?, ?> fromResourceService,
				ResourceServiceBase<?, ?, ?> toResourceService,
				KeyPathInfo parent, ResourceIndexInfo indexInfo) {
			KeyPathInfo kpi = parent == null ? toResourceService
					.ensureKeyPathInfo(fromResourceService, this.key1Class,
							null) : parent.ensureSubKeyPathInfo(this.key1Class,
					null);
			return kpi.ensureSubKeyPathInfo(this.key2Class, null)
					.ensureSubKeyPathInfo(this.key3Class, indexInfo);
		}

		@Override
		final byte getKeyCount() {
			return 3;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jt.core.impl.ResourceProviderBase#newIndex(org.eclipse.jt.core.impl.ResourceGroup)
		 */
		@Override
		final ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> newIndex(
				ResourceGroup<TFacade, TImpl, TKeysHolder> group) {
			return new ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3>(
					group, this);
		}

		@Override
		final ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> newIndexEntry(
				ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> index,
				int hash,
				ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> next,
				TKey1 key1, TKey2 key2, TKey3 key3) {
			return new ThreeKeyIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3>(
					index, hash, resourceItem, next, key1, key2, key3);
		}

		@Override
		final TKey1 getKey1(
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry) {
			return ((ThreeKeyIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3>) entry).key1;
		}

		@Override
		final TKey2 getKey2(
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry) {
			return ((ThreeKeyIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3>) entry).key2;
		}

		@Override
		final TKey3 getKey3(
				ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> entry) {
			return ((ThreeKeyIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3>) entry).key3;
		}

		@Override
		final boolean keysEqual(TKeysHolder keys, TKey1 key1, TKey2 key2,
				TKey3 key3) {
			return this.getKey1(keys).equals(key1)
					&& this.getKey2(keys).equals(key2)
					&& this.getKey3(keys).equals(key3);
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// ///////
	/**
	 * 所有的资源提供器
	 */
	ResourceProviderBase<TFacade, TImpl, TKeysHolder, ?, ?, ?>[] providers;

	final boolean isOnlyOneKey() {
		return (this.providers == null ? false : (this.providers.length == 1));
	}

	@SuppressWarnings("unchecked")
	private void addResourceProvider(
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, ?, ?, ?> provider) {
		// final String pcName = provider.getClass().getName();
		// final String pcKind;
		// if (provider instanceof
		// ResourceServiceBase.SingletonResourceProvider) {
		// pcKind = "0Key: ";
		// } else if (provider instanceof OneKeyResourceProvider) {
		// pcKind = "1Key: ";
		// } else if (provider instanceof TwoKeyResourceProvider) {
		// pcKind = "2Key: ";
		// } else if (provider instanceof ThreeKeyResourceProvider) {
		// pcKind = "3Key: ";
		// } else {
		// pcKind = "ERROR:";
		// }
		// System.err.println(pcKind.concat(pcName));
		// 设置加权限标识
		if (this.kind.isGlobal
				&& provider instanceof AuthorizableResourceProvider) {
			if (this.authorizableResourceProvider == null) {
				this.authorizableResourceProvider = (AuthorizableResourceProvider) provider;
				this.authorizableResourceProvider.providerIndex = this.providers == null ? 0
						: this.providers.length;
			} else {
				throw new UnsupportedOperationException("资源服务:["
						+ this.getClass().getName() + "]重复定义了权限标示提供器:["
						+ provider.getClass().getName() + "]");
			}
		}
		if (this.providers == null) {
			this.providers = new ResourceProviderBase[] { provider };
		} else {
			int oldL = this.providers.length;
			ResourceProviderBase[] newps = new ResourceProviderBase[oldL + 1];
			System.arraycopy(this.providers, 0, newps, 0, oldL);
			newps[oldL] = provider;
			this.providers = newps;
		}
	}

	/**
	 * 自己对别的资源的引用
	 */
	ResourceReference<?, ?> references;

	private void addResourceReference(ResourceReference<?, ?> reference) {
		if (this.references == null) {
			this.references = reference;
		} else {
			ResourceReference<?, ?> ref = this.references, last = null;
			do {
				if (ref.refFacadeClass == reference.refFacadeClass) {
					reference.next = ref.next;
					if (last == null) {
						this.references = reference;
					} else {
						last.next = reference;
					}
					ref.next = null;
					return;
				}
				last = ref;
				ref = ref.next;
			} while (ref != null);
			last.next = reference;
		}
	}

	@SuppressWarnings("unchecked")
	final <TRefFacade> ResourceReferenceStorage<TRefFacade> newResourceReferenceStorage(
			ResourceItem<?, ?, ?> owner, Class<TRefFacade> refFacadeClass)
			throws IllegalArgumentException {
		if (refFacadeClass == null) {
			throw new NullPointerException();
		}
		for (ResourceReference ref = this.references; ref != null; ref = ref.next) {
			if (ref.refFacadeClass == refFacadeClass) {
				return new ResourceReferenceStorage(owner, ref);
			}
		}
		throw new IllegalArgumentException("没有声明［" + this.facadeClass
				+ "］类型的资源对［" + refFacadeClass + "］类型的资源的引用关系");
	}

	final <TRefFacade> void checkDeclaredResourceReference(
			Class<TRefFacade> refFacadeClass) throws IllegalArgumentException {
		if (refFacadeClass == null) {
			throw new NullPointerException();
		}
		for (ResourceReference<?, ?> ref = this.references; ref != null; ref = ref.next) {
			if (ref.refFacadeClass == refFacadeClass) {
				return;
			}
		}
		throw new IllegalArgumentException("没有声明［" + this.facadeClass
				+ "］类型的资源对［" + refFacadeClass + "］类型的资源的引用关系");
	}

	@SuppressWarnings("unchecked")
	@Override
	boolean tryRegDeclaredClasses(Class<?> serviceClass,
			Class<?> declaredClass, Publish.Mode servicePublishMode,
			ExceptionCatcher catcher) {
		if (super.tryRegDeclaredClasses(serviceClass, declaredClass,
				servicePublishMode, catcher)) {
			return true;
		}
		if (ResourceProviderBase.class.isAssignableFrom(declaredClass)) {
			try {
				ResourceProviderBase<TFacade, TImpl, TKeysHolder, ?, ?, ?> provider = (ResourceProviderBase<TFacade, TImpl, TKeysHolder, ?, ?, ?>) this
						.newObjectInNode(declaredClass, null, null);
				this.addResourceProvider(provider);
				return true;
			} catch (Exception e) {
				catcher.catchException(e, this);
				this.state = ServiceBase.ServiceState.REGISTERERROR;
			}
		} else if (ResourceReference.class.isAssignableFrom(declaredClass)) {
			try {
				final ResourceReference<?, ?> reference = (ResourceReference<?, ?>) this
						.newObjectInNode(declaredClass, null, null);
				if (reference.refResourceService == null) {
					ResourceServiceBase ref = this.space.findResourceService(
							reference.refFacadeClass, InvokeeQueryMode.IN_SITE);
					if (ref == null) {
						throw new IllegalDeclarationException("找不到外观类型为"
								+ reference.refFacadeClass + "的资源服务");
					}
					reference.refResourceService = ref;
					this.addResourceReference(reference);
				}
				if (reference.refByResourceService == null) {// 被引用声明
					final Class<?> refByClass = reference
							.getReferredByFacadeClass();
					ResourceServiceBase refBy = this.space.findResourceService(
							refByClass, InvokeeQueryMode.IN_SITE);
					if (refBy == null) {
						throw new IllegalDeclarationException("找不到外观类型为"
								+ refByClass + "的资源服务");
					}
					reference.refByResourceService = refBy;
					refBy.addResourceReference(reference);
				}
				// if (reference.refFacadeClass == this.facadeClass) {// 这是被引用声明
				// final Class<?> refByClass = reference
				// .getReferredByFacadeClass();
				// ResourceServiceBase refBy = this.space.findResourceService(
				// refByClass, InvokeeQueryMode.IN_SITE);
				// if (refBy == null) {
				// throw new IllegalDeclarationException("找不到外观类型为"
				// + refByClass + "的资源服务");
				// }
				// reference.refByResourceService = refBy;
				// refBy.addResourceReference(reference);
				// } else {
				// ResourceServiceBase ref = this.space.findResourceService(
				// reference.refFacadeClass, InvokeeQueryMode.IN_SITE);
				// if (ref == null) {
				// throw new IllegalDeclarationException("找不到外观类型为"
				// + reference.refFacadeClass + "的资源服务");
				// }
				// reference.refResourceService = ref;
				// this.addResourceReference(reference);
				// }
				return true;
			} catch (Exception e) {
				catcher.catchException(e, this);
				this.state = ServiceBase.ServiceState.REGISTERERROR;
			}
		}
		return false;
	}

	// //////////////////////////////////////////////////////
	// ///////////// 资源参考定义
	// ///////////////////////////////////////////////////

	private final static Method resRefAcceptBase = Utils
			.getMethod(ResourceServiceBase.ResourceReference.class, "accept",
					Object.class);
	private final static Method resRefCompareBase = Utils.getMethod(
			ResourceServiceBase.ResourceReference.class, "compare",
			Object.class, Object.class);

	/**
	 * 资源参考定义
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TRefFacade>
	 *            资源外观类型，指明被引用的资源外观类型
	 * @param <TRefByFacade>
	 *            引用者的资源外观类型
	 */
	protected static abstract class ResourceReference<TRefFacade, TRefByFacade>
			implements Filter<TRefFacade>, Comparator<TRefFacade> {
		final Class<TRefFacade> refFacadeClass;
		private ResourceReference<?, ?> next;

		final boolean supportDftAccept;
		final boolean supportDftCompare;
		/**
		 * 引用其他资源的资源服务
		 */
		ResourceServiceBase<TRefByFacade, ?, ?> refByResourceService;
		/**
		 * 被其他资源服务引用的资源服务
		 */
		ResourceServiceBase<TRefFacade, ?, ?> refResourceService;
		/**
		 * 指向被引用的资源的权限操作项
		 */
		OperationEntry[] refOperationMap;

		final boolean isAuthorityReference() {
			return this.refOperationMap != null;
		}

		@SuppressWarnings("unchecked")
		final void buildResourceRefAuthInfo(OperationMapImpl<?, ?> mapCache) {
			final ResourceServiceBase.AuthorizableResourceProvider refByArp = this.refByResourceService.authorizableResourceProvider;
			if (refByArp == null) {
				return;
			}
			final ResourceServiceBase.AuthorizableResourceProvider refArp = this.refResourceService.authorizableResourceProvider;
			if (refArp == null) {
				return;
			}
			mapCache.setProvider(refByArp, refArp);
			this.authMapOperation((OperationMapImpl) mapCache);
			this.refOperationMap = mapCache.buildMap();

		}

		@SuppressWarnings("unchecked")
		final Class<TRefByFacade> getReferredByFacadeClass() {
			return (Class) TypeArgFinder.get(this.getClass(),
					ResourceReference.class, 1);
		}

		private static final Class<?>[] ignores = { ResourceServiceBase.ResourceReference.class };

		/**
		 * 设置引用者与被引用者之间的权限映射关系，<br>
		 * 通过该借口设置映射关系表示被引用者的权限设置将影响引用者的权限验证
		 */
		protected void authMapOperation(
				OperationMap<TRefByFacade, TRefFacade> operationMap) {
		}

		@SuppressWarnings("unchecked")
		public ResourceReference(
				ResourceServiceBase<TRefFacade, ?, ?> refResourceService,
				ResourceServiceBase<TRefByFacade, ?, ?> refByResourceService) {
			this.refFacadeClass = refResourceService != null ? refResourceService.facadeClass
					: (Class) TypeArgFinder.get(this.getClass(),
							ResourceReference.class, 0);
			this.supportDftAccept = Utils.overridden(resRefAcceptBase, this
					.getClass(), ignores);
			this.supportDftCompare = Utils.overridden(resRefCompareBase, this
					.getClass(), ignores);
			this.refResourceService = refResourceService;
			this.refByResourceService = refByResourceService;
		}

		public boolean accept(TRefFacade item) {
			return true;
		}

		public int compare(TRefFacade a, TRefFacade b) {
			return 0;
		}
	}

	// /**
	// * 资源引用者定义
	// *
	// * @author Jeff Tang
	// * @version 1.0
	// * @param <TReferredByFacade>
	// * 引用本资源的资源的外观类型
	// */
	// protected static abstract class ReferredByResource<TRefFacade,
	// TRefByFacade>
	// extends ResourceReference<TRefFacade, TRefByFacade> {
	//
	// @SuppressWarnings("unchecked")
	// public ReferredByResource(
	// ResourceServiceBase<TRefFacade, ?, ?> refResourceService,
	// ResourceServiceBase<TRefByFacade, ?, ?> refByResourceService,
	// boolean authReference) {
	// super(refResourceService, refByResourceService, authReference);
	// }
	//
	//
	// }

	// //////////////////////////////////////////////////////////////////////////
	// ///////
	// // 以下为内部方法
	// //////////////////////////////////////////////////////////////////////////
	// ///////

	/**
	 * 更新上下文中的当前模块信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	final SpaceNode updateContextSpace(ContextImpl<?, ?, ?> context) {
		SpaceNode occorAt = context.occorAt;
		context.occorAt = this;
		context.occorAtResourceService = (ResourceServiceBase) this;
		return occorAt;
	}

	/**
	 * 外观类
	 */
	final Class<?> facadeClass;
	/**
	 * 实现类
	 */
	final Class<?> implClass;
	/**
	 * 
	 */
	final StructDefineImpl implStruct;
	/**
	 * 健类
	 */
	final Class<?> keysClass;

	/**
	 * 返回该资源管理器的资源所在位置，只有根资源才有返回，子资源存储在父资源的资源项上
	 */
	@SuppressWarnings("unchecked")
	final ResourceGroup ensureResourceGroup(Object category,
			ContextImpl<?, ?, ?> context) {
		if (this.ownerResourceService != null) {
			throw new UnsupportedOperationException("该资源为子资源，不能独立使用");
		}
		if (this.kind.isGlobal) {
			if (category == null) {
				category = None.NONE;
			}
			final ResourceGroup group;
			ensureGroup: {
				ReadLock findLock = this.lock.readLock();
				findLock.lock();
				try {
					if (this.groupsByCategory != null) {
						group = (ResourceGroup) this.groupsByCategory
								.get(category);
					} else if (category == None.NONE) {
						group = this.defaultGroup;
					} else {
						group = null;
					}
					break ensureGroup;
				} finally {
					findLock.unlock();
				}
			}
			if (group == null) {
				throw new IllegalDeclarationException("［" + this.facadeClass
						+ "］类型的资源服务不支持[" + category + "]类别");
			}
			group.ensureInit(context);
			return group;
		} else if (this.kind.inSession) {

			final String title;
			if (this.groupsByCategory != null) {
				title = (String) this.groupsByCategory.get(category);
			} else if (category == None.NONE) {
				title = this.title;
			} else {
				title = null;
			}
			return context.session.resources.ensureResourceGroup(title, this,
					category, context);
		}
		throw new UnsupportedOperationException("不支持的资源类型:" + this.kind);
	}

	@Override
	void doDispose(ContextImpl<?, ?, ?> context) {
		switch (this.state) {
		case DISPOSING:
		case DISPOSED:
			return;
		}
		try {
			if (this.ownerResourceService == null && this.kind.isGlobal) {
				this.lock.writeLock().lock();
				try {
					if (this.groupsByCategory != null) {
						for (Object g : this.groupsByCategory.values()) {
							if (g instanceof ResourceGroup<?, ?, ?>) {
								((ResourceGroup<?, ?, ?>) g).reset(
										context.catcher, true);
							}
						}
					} else {
						this.defaultGroup.reset(context.catcher, true);
					}
				} finally {
					this.groupsByCategory = null;
					this.defaultGroup = null;
					this.lock.writeLock().unlock();
				}
			}
		} finally {
			super.doDispose(context);
		}
	}

	// ///////////////////
	// // 父子资源的信息
	// ///////////////////

	/**
	 * 父资源管理器
	 */
	ResourceServiceBase<?, ?, ?> ownerResourceService;
	/**
	 * 作为子资源时在父资源中的位置
	 */
	private byte indexAsSubResourceService;

	final byte getIndexAsSubResourceService() {
		return this.indexAsSubResourceService;
	}

	/**
	 * 子资源管理器
	 */
	private List<ResourceServiceBase<?, ?, ?>> subResourceServices;

	/**
	 * 设致父资源管理器
	 * 
	 * @param ownerResourceService
	 */
	@Override
	final boolean trySetOwnerResourceService(
			ResourceServiceBase<?, ?, ?> ownerResourceService) {
		if (ownerResourceService == null) {
			throw new NullPointerException();
		}
		for (ResourceServiceBase<?, ?, ?> rm = ownerResourceService; rm != null; rm = rm.ownerResourceService) {
			if (rm == this) {
				throw new IllegalArgumentException();
			}
		}
		this.ownerResourceService = ownerResourceService;
		this.kind = ownerResourceService.kind;

		if (ownerResourceService.subResourceServices != null) {
			ownerResourceService.subResourceServices = new ArrayList<ResourceServiceBase<?, ?, ?>>(
					1);
			this.indexAsSubResourceService = 0;
		} else {
			int i = ownerResourceService.subResourceServices.size();
			if (Byte.MAX_VALUE < i) {
				throw new UnsupportedOperationException("to many sub resource");
			}
			this.indexAsSubResourceService = (byte) i;
		}
		ownerResourceService.subResourceServices.add(this);
		return true;
	}

	@Override
	final boolean tryBuildResourceRefAuthInfo(OperationMapImpl<?, ?> mapCache) {
		if (this.authorizableResourceProvider != null) {
			for (ResourceReference<?, ?> reference = this.references; reference != null; reference = reference.next) {
				reference.buildResourceRefAuthInfo(mapCache);
			}
		}
		return true;
	}

	// ///////////////////
	// // 索引位置的缓存
	// ///////////////////

	/**
	 * 资源索引信息
	 * 
	 * @author Jeff Tang
	 * 
	 */
	static class RestResourceIndexInfo {
		RestResourceIndexInfo(byte resourceIndexIndex,
				RestResourceIndexInfo subIndexInfo, byte subResourceIndex,
				byte keyCount) {
			this.resourceIndexIndex = resourceIndexIndex;
			this.subIndexInfo = subIndexInfo;
			this.subResourceIndex = subResourceIndex;
			this.keyCount = keyCount;
		}

		/**
		 * 本级资源索引的位置
		 */
		final byte resourceIndexIndex;
		/**
		 * 下一个子资源在本资源中的位置
		 */
		final byte subResourceIndex;
		/**
		 * 当前索引的键的个数
		 */
		final byte keyCount;
		/**
		 * 下一个子资源资源索引的信息
		 */
		final RestResourceIndexInfo subIndexInfo;
	}

	final static class ResourceIndexInfo extends RestResourceIndexInfo {
		/**
		 * 该路径信息的起始资源管理器
		 */
		final ResourceServiceBase<?, ?, ?> fromResourceService;

		ResourceIndexInfo(byte resourceIndexIndex,
				RestResourceIndexInfo subIndexInfo, byte subResourceIndex,
				byte keyCount, ResourceServiceBase<?, ?, ?> fromResourceService) {
			super(resourceIndexIndex, subIndexInfo, subResourceIndex, keyCount);
			this.fromResourceService = fromResourceService;
		}
	}

	/**
	 * 键路径信息
	 * 
	 * @author Jeff Tang
	 * 
	 */
	static class KeyPathInfo {
		KeyPathInfo(final Class<?> keyClass, ResourceIndexInfo indexInfo) {
			this.keyClass = keyClass;
			this.indexInfo = indexInfo;
		}

		/**
		 * 键类型
		 */
		final Class<?> keyClass;
		/**
		 * 当前键对应的资源索引信息
		 */
		ResourceIndexInfo indexInfo;
		/**
		 * 下一个同级的键
		 */
		KeyPathInfo next;
		/**
		 * 第一个下级的键
		 */
		KeyPathInfo firstSub;

		/**
		 * 根据键类型生成或返回一个下级节点
		 * 
		 * @param keyClass
		 *            键类型
		 * @param indexInfo
		 *            索引信息
		 * @return 返回下一级节点
		 */
		final KeyPathInfo ensureSubKeyPathInfo(Class<?> keyClass,
				ResourceIndexInfo indexInfo) {
			KeyPathInfo kpi = this.firstSub;
			KeyPathInfo lastKpi = null;
			while (kpi != null && kpi.keyClass != keyClass) {
				lastKpi = kpi;
				kpi = kpi.next;
			}
			if (kpi == null) {
				kpi = new KeyPathInfo(keyClass, indexInfo);
				if (lastKpi != null) {
					lastKpi.next = kpi;
				} else {
					this.firstSub = kpi;
				}
			}
			return kpi;
		}
	}

	private static final class RootKeyPathInfo extends KeyPathInfo {
		RootKeyPathInfo(Class<?> keyClass, ResourceIndexInfo indexInfo,
				ResourceServiceBase<?, ?, ?> fromResourceService) {
			super(keyClass, indexInfo);
			this.fromResourceService = fromResourceService;
		}

		/**
		 * 资源管理器
		 */
		final ResourceServiceBase<?, ?, ?> fromResourceService;
		/**
		 * 下一个根
		 */
		RootKeyPathInfo nextRoot;
	}

	/**
	 * 键路径信息
	 */
	private RootKeyPathInfo keyPathInfos;

	/**
	 * 根据键类型生成或返回一个根节点
	 * 
	 * @param fromResourceService
	 * @param keyClass
	 * @param indexInfo
	 * @return
	 */
	final KeyPathInfo ensureKeyPathInfo(
			ResourceServiceBase<?, ?, ?> fromResourceService,
			Class<?> keyClass, ResourceIndexInfo indexInfo) {
		RootKeyPathInfo rkpi = this.keyPathInfos;
		RootKeyPathInfo lastRkpi = null;
		while (rkpi != null && rkpi.fromResourceService != fromResourceService) {
			lastRkpi = rkpi;
			rkpi = rkpi.nextRoot;
		}
		if (rkpi == null) {
			rkpi = new RootKeyPathInfo(keyClass, indexInfo, fromResourceService);
			if (lastRkpi == null) {
				this.keyPathInfos = rkpi;
			} else {
				lastRkpi.nextRoot = rkpi;
			}
			return rkpi;
		} else {
			KeyPathInfo kpi = rkpi;
			KeyPathInfo lastKpi = null;
			while (kpi != null && kpi.keyClass != keyClass) {
				lastKpi = kpi;
				kpi = kpi.next;
			}
			if (kpi == null) {
				lastKpi.next = kpi = new KeyPathInfo(keyClass, indexInfo);
			} else if (kpi.indexInfo == null && indexInfo != null) {
				// 前者有效
				kpi.indexInfo = indexInfo;
			}
			return kpi;
		}
	}

	@SuppressWarnings("unchecked")
	private final void buildNode(
			final List<ResourceServiceBase<?, ?, ?>> resourceServices,
			final int from) {
		final ResourceServiceBase fromResourceService = resourceServices
				.get(from);
		final ResourceServiceBase toResourceService = resourceServices.get(0);
		final byte[] indexes = new byte[from + 1];
		for (int i = from; i >= 0; i--) {
			if (resourceServices.get(i).providers == null) {
				throw new IllegalStateException("资源服务["
						+ resourceServices.get(i) + "]中没有定义任何资源提供器");
			}
			indexes[i] = (byte) (resourceServices.get(i).providers.length - 1);
		}
		final byte[] indexesBack = indexes.clone();
		whileLable: while (true) {
			for (int currM = from; currM >= 0; currM--) {
				KeyPathInfo kpi = null;
				ResourceServiceBase currentResourceService = resourceServices
						.get(currM);
				ResourceProviderBase provider = currentResourceService.providers[indexes[currM]];
				if (currM > 0) {
					kpi = provider.buildKeyPathInfo(fromResourceService,
							currentResourceService, kpi, null);
				} else {
					RestResourceIndexInfo rrii = null;
					byte lastIndexAsSubResourceService = -1;
					for (int i = 0; i < from; i++) {
						ResourceServiceBase cm = resourceServices.get(i);
						byte resourceIndexIndex = indexes[i];
						rrii = new RestResourceIndexInfo(resourceIndexIndex,
								rrii, lastIndexAsSubResourceService,
								cm.providers[resourceIndexIndex].getKeyCount());
						lastIndexAsSubResourceService = cm.indexAsSubResourceService;
					}
					byte resourceIndexIndex = indexes[from];
					ResourceIndexInfo rii = new ResourceIndexInfo(
							resourceIndexIndex, rrii,
							lastIndexAsSubResourceService,
							toResourceService.providers[resourceIndexIndex]
									.getKeyCount(), fromResourceService);
					provider.buildKeyPathInfo(fromResourceService,
							toResourceService, kpi, rii);
					if (indexes[from] == 0) {
						break whileLable;
					}
					for (int i = 0; i < from; i++) {
						if (indexes[i] == 0) {
							indexes[i] = indexesBack[i];
							indexes[i + 1]--;
						} else {
							break;
						}
					}
					if (from == 0) {
						indexes[from]--;
					}
				}
			}
		}
	}

	/**
	 * 构造键路径信息以及资源索引信息链表
	 */
	@Override
	@SuppressWarnings("unchecked")
	final boolean tryBuildResourceKeyPathInfos(
			List<ResourceServiceBase<?, ?, ?>> resourceServicesCache) {
		resourceServicesCache.clear();
		for (ResourceServiceBase m = this; m != null; m = m.ownerResourceService) {
			resourceServicesCache.add(m);
		}
		for (int i = resourceServicesCache.size() - 1; i >= 0; i--) {
			this.buildNode(resourceServicesCache, i);
		}
		if (this.ownerResourceService == null && this.kind.isGlobal) {
			this.defaultGroup = new ResourceGroup(this.title, null, this,
					None.NONE);
		}
		return true;
	}

	// ////////////////////////////////////////////
	// //// 查找索引路径
	// /////////////////////////////////////////////
	/**
	 * 查找资源索引路径信息
	 */
	final ResourceIndexInfo findIndexInfo(
			ResourceServiceBase<?, ?, ?> fromResourceService, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		RootKeyPathInfo rkpi = this.keyPathInfos;
		// fromResourceService == null代表使用根资源管理器到当前管理器的路径
		// 根管理器就在第一个
		if (fromResourceService != null) {
			while (rkpi != null
					&& rkpi.fromResourceService != fromResourceService) {
				rkpi = rkpi.nextRoot;
			}
		}
		Class<?> keyClass = key1 == null ? null : key1.getClass();
		KeyPathInfo kpi = rkpi;
		while (kpi != null && kpi.keyClass != keyClass) {
			kpi = kpi.next;
		}
		if (kpi == null) {
			return null;
		} else if (key2 == null) {
			return kpi.indexInfo;
		} else {
			keyClass = key2.getClass();
		}
		kpi = kpi.firstSub;
		while (kpi != null && kpi.keyClass != keyClass) {
			kpi = kpi.next;
		}
		if (kpi == null) {
			return null;
		} else if (key3 == null) {
			return kpi.indexInfo;
		} else {
			keyClass = key3.getClass();
		}
		kpi = kpi.firstSub;
		while (kpi != null && kpi.keyClass != keyClass) {
			kpi = kpi.next;
		}
		if (kpi == null) {
			return null;
		} else if (otherKeys == null) {
			return kpi.indexInfo;
		} else {
			kpi = kpi.firstSub;
			for (int i = 0; kpi != null && i < otherKeys.length; i++, kpi = kpi.firstSub) {
				Object key = otherKeys[i];
				if (key == null) {
					return null;
				} else {
					keyClass = key.getClass();
				}
				while (kpi != null && kpi.keyClass != keyClass) {
					kpi = kpi.next;
				}
			}
			return kpi != null ? kpi.indexInfo : null;
		}
	}

	/**
	 * 查找资源索引路径信息
	 */
	final ResourceIndexInfo findIndexInfoByKeyClass(
			ResourceServiceBase<?, ?, ?> fromResourceService,
			Class<?> key1Class, Class<?> key2Class, Class<?> key3Class,
			Class<?>[] otherKeyClasses) {
		RootKeyPathInfo rkpi = this.keyPathInfos;
		// fromResourceService == null代表使用根资源管理器到当前管理器的路径
		// 根管理器就在第一个
		if (fromResourceService != null) {
			while (rkpi != null
					&& rkpi.fromResourceService != fromResourceService) {
				rkpi = rkpi.nextRoot;
			}
		}
		Class<?> keyClass = key1Class;
		KeyPathInfo kpi = rkpi;
		while (kpi != null && kpi.keyClass != keyClass) {
			kpi = kpi.next;
		}
		if (kpi == null) {
			return null;
		} else if (key2Class == null) {
			return kpi.indexInfo;
		} else {
			keyClass = key2Class;
		}
		kpi = kpi.firstSub;
		while (kpi != null && kpi.keyClass != keyClass) {
			kpi = kpi.next;
		}
		if (kpi == null) {
			return null;
		} else if (key3Class == null) {
			return kpi.indexInfo;
		} else {
			keyClass = key3Class;
		}
		kpi = kpi.firstSub;
		while (kpi != null && kpi.keyClass != keyClass) {
			kpi = kpi.next;
		}
		if (kpi == null) {
			return null;
		} else if (otherKeyClasses == null || otherKeyClasses.length == 0) {
			return kpi.indexInfo;
		} else {
			kpi = kpi.firstSub;
			for (int i = 0; kpi != null && i < otherKeyClasses.length; i++, kpi = kpi.firstSub) {
				keyClass = otherKeyClasses[i];
				if (keyClass == null) {
					return null;
				}
				while (kpi != null && kpi.keyClass != keyClass) {
					kpi = kpi.next;
				}
			}
			return kpi != null ? kpi.indexInfo : null;
		}
	}

	/**
	 * 查找资源索引路径信息
	 * 
	 * @param holder
	 *            是fromResourceService的上级ResourceService。
	 */
	final ResourceIndexInfo subFindIndexInfo(
			ResourceServiceBase<?, ?, ?> holder, Object key1, Object key2,
			Object key3, Object[] otherKeys) {
		ResourceServiceBase<?, ?, ?> from = this;
		while (from != null && holder != from.ownerResourceService) {
			from = from.ownerResourceService;
		}
		if (from == null) {
			throw new IllegalArgumentException();
		}
		return this.findIndexInfo(from, key1, key2, key3, otherKeys);
	}

	// ///////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////

	final static ResourceGroup<?, ?, ?>[] emptyResourceGroups = new ResourceGroup[0];

	/**
	 * 根据读取器类型获得子资源管理器
	 * 
	 * @param subResourceReaderClass
	 *            子资源读取器类型
	 * @return 返回子资源读取器类型
	 */
	@SuppressWarnings("unchecked")
	final ResourceGroup<?, ?, ?>[] tryNewSubResourceGroupsAndInit(
			ResourceItem<?, ?, ?> ownerResource, ContextImpl<?, ?, ?> context) {
		if (this.subResourceServices != null) {
			ResourceGroup<?, ?, ?>[] groups = new ResourceGroup<?, ?, ?>[this.subResourceServices
					.size()];
			for (int i = 0; i < groups.length; i++) {
				ResourceServiceBase<?, ?, ?> s = this.subResourceServices
						.get(i);
				groups[i] = new ResourceGroup(s.title, ownerResource, s,
						ownerResource.group.category);
				context.initResources(groups[i]);
			}
			return groups;
		}
		return emptyResourceGroups;
	}

	private final int hash = HashUtil.hash(this);

	/**
	 * 计算包含category的hashcode
	 */
	final int calCategoryHashCode(Object category) {
		if (category == null) {
			return this.hash;
		} else if (category instanceof Category) {
			Category rc = (Category) category;
			Object id = rc.getIdentifier();
			if (id == null) {
				return this.hash;
			} else {
				return this.hash ^ HashUtil.hash(id.hashCode());
			}
		} else {
			return this.hash ^ HashUtil.hash(category.hashCode());
		}
	}

	final GUID calGroupID(Object category) {
		if (category == null || category == None.NONE) {
			if (this.authorizableResourceProvider != null) {
				final GUID groupID = this.authorizableResourceProvider.customCategoryID;
				if (groupID != null) {
					return groupID;
				}
			}
			return GUID.MD5Of(this.getClass().getName());
		} else if (category instanceof GUID) {
			return (GUID) category;
		} else if (category instanceof String) {
			return GUID.MD5Of(this.getClass().getName() + ":"
					+ (String) category);
		}
		return null;
	}

	/**
	 * 类型
	 */
	ResourceKind kind;

	private final boolean needFilter;
	final Comparator<TImpl> defaultSortComparator;

	/**
	 * @return 返回过滤之前的大小
	 */
	final int processList(DnaArrayList<TImpl> list,
			Filter<? super TImpl> filter, Comparator<? super TImpl> comparator,
			boolean hasDefaultSorted) {
		final int oldSize = list.size();
		if (oldSize == 0) {
			return 0;
		}

		tryFilter: {
			int acceptedCount = 0;
			if (this.needFilter) {
				if (filter != null) {
					for (int i = 0; i < oldSize; i++) {
						final TImpl item = list.get(i);
						if (this.defaultAccept(item) && filter.accept(item)) {
							if (acceptedCount != i) {
								list.set(acceptedCount, item);
							}
							acceptedCount++;
						}
					}
				} else {
					for (int i = 0; i < oldSize; i++) {
						final TImpl item = list.get(i);
						if (this.defaultAccept(item)) {
							if (acceptedCount != i) {
								list.set(acceptedCount, item);
							}
							acceptedCount++;
						}
					}
				}
			} else if (filter != null) {
				for (int i = 0; i < oldSize; i++) {
					final TImpl item = list.get(i);
					if (filter.accept(item)) {
						if (acceptedCount != i) {
							list.set(acceptedCount, item);
						}
						acceptedCount++;
					}
				}
			} else {
				break tryFilter;
			}
			if (acceptedCount > 1) {
				list.removeTail(acceptedCount);
			} else if (acceptedCount == 1 && oldSize > 1) {
				final TImpl item = list.get(0);
				list.clear();
				list.add(item);
			} else if (acceptedCount == 0 && oldSize > 0) {
				list.clear();
				return oldSize;
			}
		}

		if (comparator != null) {
			SortUtil.sort(list, comparator);
		} else if (!hasDefaultSorted) {
			comparator = this.defaultSortComparator;
			if (comparator != null) {
				SortUtil.sort(list, comparator);
			}
		}

		return oldSize;
	}

	/**
	 * 是否需要初始化
	 */
	final boolean needInitResource;

	protected ResourceServiceBase(String title, ResourceKind kind) {
		super(title);
		if (kind == null) {
			throw new NullPointerException();
		}
		this.kind = kind;
		Class<?> thisClass = this.getClass();
		this.needInitResource = Utils.overridden(initResources, thisClass,
				abstractResourceServiceClasses);
		this.needFilter = Utils.overridden(defaultAccept, thisClass, null);
		if (Utils.overridden(defaultSortCompare, thisClass, null)) {
			// System.out.println("defaultSortCompare:" + thisClass.getName());
			this.defaultSortComparator = new Comparator<TImpl>() {
				public int compare(TImpl a, TImpl b) {
					return ResourceServiceBase.this.defaultSortCompare(a, b);
				}
			};
		} else {
			this.defaultSortComparator = null;
		}
		Class<?>[] types = TypeArgFinder.get(thisClass,
				ResourceServiceBase.class);
		this.facadeClass = types[0];
		this.implClass = types[1];
		this.keysClass = types[2];
		try {
			this.implStruct = DataTypeBase
					.getStaticStructDefine(this.implClass);
		} catch (Throwable e) {
			throw Utils.tryThrowException(new UnsupportedOperationException(
					"资源实例类型的结构无法支持内存事务", e));
		}
	}
}
