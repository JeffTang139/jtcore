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
 * ��Դ������
 * 
 * @author Jeff Tang
 * 
 * @param <TFacade>
 *            ��Դ��ۣ�����Դʵ���ṩ��ֻ���ӿ�
 * @param <TImpl>
 *            ��Դ�޸������ȿ��������޸���Դ�Ľӿڻ������ͣ��󲿷�ʱ��ʹ����Դ��ʵ������
 * @param <TKeysHolder>
 *            ��Դ��Դ���ȿ��Դ��еõ���Դ�ļ���ֵ�Ľӿڻ������ͣ��󲿷�ʱ��ʹ����Դ��ʵ������
 */
public abstract class ResourceServiceBase<TFacade, TImpl extends TFacade, TKeysHolder>
		extends ServiceBase<ResourceContext<TFacade, TImpl, TKeysHolder>> {
	/**
	 * Ĭ�ϵĹ�����
	 */
	protected boolean defaultAccept(TImpl item) {
		return true;
	}

	/**
	 * Ĭ�ϵ�����Ƚ���
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

	// TODO ��Դ����
	// ��ʱʹ��һ��HashMap���Ժ����Ż�
	// �����ȫ����Դ����ע����Դ���ʱ����Group
	private volatile HashMap<Object, Object> groupsByCategory;
	// �����ȫ����Դ���ڷ����ʼ��֮ǰ����
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
	 * ע����Դ���
	 * 
	 * <pre>
	 *  ��Դ��������Ϊ���������е�һ�֣�
	 *      1. �ַ�����
	 *      2. �κ�ö�����ͣ�
	 *      3. ԭʼ���͵�װ�����ͣ�
	 *      4. org.eclipse.jt.core.type.GUID��
	 *      5. org.eclipse.jt.core.resource.ResourceCategory��ʵ���ࡣ
	 * </pre>
	 * 
	 * @param category
	 *            ��𣬿���ΪNone.NONE,��ʾĬ�ϵ����
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
					throw new UnsupportedOperationException("��Դ���ı�ʶ�������Ͳ���֧�֣�"
							+ cid.getClass());
				}
			} else {
				if (!ResourceCategory.Helper.isSupportedIdType(category
						.getClass())) {
					throw new UnsupportedOperationException("��֧�֣�"
							+ category.getClass() + "�����͵���Դ���");
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
				return;// �Ѿ�ע���
			} else if (category == None.NONE) {
				throw new UnsupportedOperationException("��֧��ע��None.NONE��Ϊ��Դ���");
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
	 * ע����Դ���
	 * 
	 * @param category
	 *            ��𣬿���Ϊnull,��ʾĬ�ϵ����
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
	 * ��ʼ����Դ�������Դ
	 * 
	 * @param context
	 *            ������
	 * @param initializer
	 *            ��Դ��ʼ��
	 */
	protected void initResources(Context context,
			ResourceInserter<TFacade, TImpl, TKeysHolder> initializer)
			throws Throwable {
	}

	/**
	 * �����ʼ���������ĵ��ã���ѡ��
	 */
	protected void initResourcesUsing(UsingDeclarator using) {
	}

	/**
	 * ��Դ����ʱ����
	 * 
	 * @param facade
	 *            ��Դ���
	 * @param impl
	 *            ��Դ�޸���
	 * @param keys
	 *            ��Դ��Դ
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
		 * �ṩ��������
		 * 
		 * @param context
		 *            ������
		 * @param setter
		 *            ��Դ������
		 * @throws Throwable
		 *             �׳��쳣
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
	 * �������Դ�ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TKey>
	 *            ������
	 */
	protected abstract class OneKeyResourceProvider<TKey> extends
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey, None, None> {
		@Override
		final Method getProvideMethodBase() {
			return oneKeyProvideBase;
		}

		/**
		 * ������Դ��Դ���ص�ǰ�ṩ����Ӧ�ļ�
		 * 
		 * @param keys
		 *            ��Դ��Դ
		 * @return ���ص�ǰ�ṩ����Ӧ�ļ�
		 */
		@Override
		protected abstract TKey getKey1(TKeysHolder keys);

		/**
		 * �ṩ��Դ�����ݾ������󣬻򴴽��µģ���������ط���ȡ�������ݿ⣩
		 * 
		 * @param context
		 *            ������
		 * @param setter
		 *            ������
		 * @param key
		 *            ��Ӧ�ļ�
		 * @throws Throwable
		 *             �׳��쳣
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
	 * Ȩ��֧��
	 */
	AuthorizableResourceProvider<?> authorizableResourceProvider;

	final boolean isAuthorizable() {
		return this.authorizableResourceProvider != null
				&& this.authorizableResourceProvider.providerIndex >= 0;
	}

	/**
	 * ��������Դ��ҪȨ����Ȩ����Դ
	 */
	protected abstract class AuthorizableResourceProvider<TOperationEnum extends Enum<? extends Operation<? super TFacade>>>
			extends OneKeyResourceProvider<GUID> {

		/**
		 * ����ĳ����Դ�ı�������Ȩ������ʹ��
		 */
		protected abstract String getResourceTitle(TImpl resource,
				TKeysHolder keys);

		final GUID customCategoryID;
		/**
		 * Ȩ�޼��ṩ��λ��
		 */
		int providerIndex = -1;

		/**
		 * ���췽��
		 * 
		 * @param looseAuthPolicy
		 *            �Ƿ�Ӧ�ÿ���Ȩ�޿��Ʋ��ԣ�Ĭ����Ȩ�ޣ�
		 */
		protected AuthorizableResourceProvider(GUID customCategoryID,
				boolean looseAuthPolicy) {
			super();
			if (!ResourceServiceBase.this.kind.isGlobal) {
				throw new UnsupportedOperationException("ֻ��ȫ����Դ��֧��Ȩ����֤����Դ����["
						+ ResourceServiceBase.this.getClass().getName() + "]");
			}
			if (customCategoryID != null && customCategoryID.isEmpty()) {
				throw new IllegalArgumentException("customCategoryID ����ΪEmpty");
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
		 * ����ö�ٵ�����
		 */
		final Class<?> operationEnumClass;
		/**
		 * ������Ӧ��Ȩ�޲�����
		 */
		final OperationEntry[] operations;

		/**
		 * ���ݲ�����ȡȨ�޲������룬ֻ�и�32λ��Ч
		 */
		final long getAuthMask(Enum<?> opEnum) {
			try {
				OperationEntry entry = this.operations[opEnum.ordinal()];
				if (entry.operation == opEnum) {
					return entry.authMask;
				}
			} catch (Throwable e) {
			}
			throw new IllegalArgumentException("��Դ["
					+ ResourceServiceBase.this.facadeClass + "] ��֧��[" + opEnum
					+ "]���͵�Ȩ�޲���");
		}

		final OperationEntry getOperationEntry(Enum<?> opEnum) {
			try {
				OperationEntry entry = this.operations[opEnum.ordinal()];
				if (entry.operation == opEnum) {
					return entry;
				}
			} catch (Throwable e) {
			}
			throw new IllegalArgumentException("��Դ["
					+ ResourceServiceBase.this.facadeClass + "] ��֧��[" + opEnum
					+ "]���͵�Ȩ�޲���");
		}

		boolean defaultAuth;

	}

	protected void beforeAccessAuthorityResource(Context context) {

	}

	protected void endAccessAuthorityResource(Context context) {

	}

	/**
	 * Ȩ����أ���ȡ������
	 */
	final OperationEntry getOperationEntry(Operation<? super TFacade> operation) {
		if (this.authorizableResourceProvider == null) {
			throw new UnsupportedOperationException("��Դ["
					+ this.facadeClass.getName() + "]Ϊ��Ȩ�޹�����Դ���");
		}
		return this.authorizableResourceProvider
				.getOperationEntry((Enum<?>) operation);
	}

	/**
	 * Ȩ����أ���ȡĬ����Ȩ
	 */
	final boolean getDefaultAuth() {
		if (this.authorizableResourceProvider == null) {
			throw new UnsupportedOperationException("��Դ["
					+ this.facadeClass.getName() + "]Ϊ��Ȩ�޹�����Դ���");
		}
		return this.authorizableResourceProvider.defaultAuth;
	}

	private final static Method twoKeyProvideBase = Utils.getMethod(
			ResourceServiceBase.TwoKeyResourceProvider.class, "provide",
			Context.class, ResourceInserter.class, Object.class, Object.class);

	/**
	 * �������Դ�ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TKey>
	 *            ������
	 */
	protected abstract class TwoKeyResourceProvider<TKey1, TKey2>
			extends
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey1, TKey2, None> {
		@Override
		final Method getProvideMethodBase() {
			return twoKeyProvideBase;
		}

		/**
		 * ������Դ��Դ���ص�ǰ�ṩ����Ӧ�ļ�
		 * 
		 * @param keys
		 *            ��Դ��Դ
		 * @return ���ص�ǰ�ṩ����Ӧ�ļ�
		 */
		@Override
		protected abstract TKey1 getKey1(TKeysHolder keys);

		/**
		 * ������Դ��Դ���ص�ǰ�ṩ����Ӧ�ļ�
		 * 
		 * @param keys
		 *            ��Դ��Դ
		 * @return ���ص�ǰ�ṩ����Ӧ�ļ�
		 */
		@Override
		protected abstract TKey2 getKey2(TKeysHolder keys);

		/**
		 * �ṩ��Դ�����ݾ������󣬻򴴽��µģ���������ط���ȡ�������ݿ⣩
		 * 
		 * @param context
		 *            ������
		 * @param setter
		 *            ������
		 * @param key
		 *            ��Ӧ�ļ�
		 * @throws Throwable
		 *             �׳��쳣
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
	 * �������Դ�ṩ��
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TKey>
	 *            ������
	 */
	protected abstract class ThreeKeyResourceProvider<TKey1, TKey2, TKey3>
			extends
			ResourceProviderBase<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> {
		@Override
		final Method getProvideMethodBase() {
			return threeKeyProvideBase;
		}

		/**
		 * ������Դ��Դ���ص�ǰ�ṩ����Ӧ�ļ�
		 * 
		 * @param keys
		 *            ��Դ��Դ
		 * @return ���ص�ǰ�ṩ����Ӧ�ļ�
		 */
		@Override
		protected abstract TKey1 getKey1(TKeysHolder keys);

		/**
		 * ������Դ��Դ���ص�ǰ�ṩ����Ӧ�ļ�
		 * 
		 * @param keys
		 *            ��Դ��Դ
		 * @return ���ص�ǰ�ṩ����Ӧ�ļ�
		 */
		@Override
		protected abstract TKey2 getKey2(TKeysHolder keys);

		/**
		 * ������Դ��Դ���ص�ǰ�ṩ����Ӧ�ļ�
		 * 
		 * @param keys
		 *            ��Դ��Դ
		 * @return ���ص�ǰ�ṩ����Ӧ�ļ�
		 */
		@Override
		protected abstract TKey3 getKey3(TKeysHolder keys);

		/**
		 * �ṩ��Դ�����ݾ������󣬻򴴽��µģ���������ط���ȡ�������ݿ⣩
		 * 
		 * @param context
		 *            ������
		 * @param setter
		 *            ������
		 * @param key
		 *            ��Ӧ�ļ�
		 * @throws Throwable
		 *             �׳��쳣
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
	 * ���е���Դ�ṩ��
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
		// ���ü�Ȩ�ޱ�ʶ
		if (this.kind.isGlobal
				&& provider instanceof AuthorizableResourceProvider) {
			if (this.authorizableResourceProvider == null) {
				this.authorizableResourceProvider = (AuthorizableResourceProvider) provider;
				this.authorizableResourceProvider.providerIndex = this.providers == null ? 0
						: this.providers.length;
			} else {
				throw new UnsupportedOperationException("��Դ����:["
						+ this.getClass().getName() + "]�ظ�������Ȩ�ޱ�ʾ�ṩ��:["
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
	 * �Լ��Ա����Դ������
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
		throw new IllegalArgumentException("û��������" + this.facadeClass
				+ "�����͵���Դ�ԣ�" + refFacadeClass + "�����͵���Դ�����ù�ϵ");
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
		throw new IllegalArgumentException("û��������" + this.facadeClass
				+ "�����͵���Դ�ԣ�" + refFacadeClass + "�����͵���Դ�����ù�ϵ");
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
						throw new IllegalDeclarationException("�Ҳ����������Ϊ"
								+ reference.refFacadeClass + "����Դ����");
					}
					reference.refResourceService = ref;
					this.addResourceReference(reference);
				}
				if (reference.refByResourceService == null) {// ����������
					final Class<?> refByClass = reference
							.getReferredByFacadeClass();
					ResourceServiceBase refBy = this.space.findResourceService(
							refByClass, InvokeeQueryMode.IN_SITE);
					if (refBy == null) {
						throw new IllegalDeclarationException("�Ҳ����������Ϊ"
								+ refByClass + "����Դ����");
					}
					reference.refByResourceService = refBy;
					refBy.addResourceReference(reference);
				}
				// if (reference.refFacadeClass == this.facadeClass) {// ���Ǳ���������
				// final Class<?> refByClass = reference
				// .getReferredByFacadeClass();
				// ResourceServiceBase refBy = this.space.findResourceService(
				// refByClass, InvokeeQueryMode.IN_SITE);
				// if (refBy == null) {
				// throw new IllegalDeclarationException("�Ҳ����������Ϊ"
				// + refByClass + "����Դ����");
				// }
				// reference.refByResourceService = refBy;
				// refBy.addResourceReference(reference);
				// } else {
				// ResourceServiceBase ref = this.space.findResourceService(
				// reference.refFacadeClass, InvokeeQueryMode.IN_SITE);
				// if (ref == null) {
				// throw new IllegalDeclarationException("�Ҳ����������Ϊ"
				// + reference.refFacadeClass + "����Դ����");
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
	// ///////////// ��Դ�ο�����
	// ///////////////////////////////////////////////////

	private final static Method resRefAcceptBase = Utils
			.getMethod(ResourceServiceBase.ResourceReference.class, "accept",
					Object.class);
	private final static Method resRefCompareBase = Utils.getMethod(
			ResourceServiceBase.ResourceReference.class, "compare",
			Object.class, Object.class);

	/**
	 * ��Դ�ο�����
	 * 
	 * @author Jeff Tang
	 * 
	 * @param <TRefFacade>
	 *            ��Դ������ͣ�ָ�������õ���Դ�������
	 * @param <TRefByFacade>
	 *            �����ߵ���Դ�������
	 */
	protected static abstract class ResourceReference<TRefFacade, TRefByFacade>
			implements Filter<TRefFacade>, Comparator<TRefFacade> {
		final Class<TRefFacade> refFacadeClass;
		private ResourceReference<?, ?> next;

		final boolean supportDftAccept;
		final boolean supportDftCompare;
		/**
		 * ����������Դ����Դ����
		 */
		ResourceServiceBase<TRefByFacade, ?, ?> refByResourceService;
		/**
		 * ��������Դ�������õ���Դ����
		 */
		ResourceServiceBase<TRefFacade, ?, ?> refResourceService;
		/**
		 * ָ�����õ���Դ��Ȩ�޲�����
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
		 * �����������뱻������֮���Ȩ��ӳ���ϵ��<br>
		 * ͨ���ý������ӳ���ϵ��ʾ�������ߵ�Ȩ�����ý�Ӱ�������ߵ�Ȩ����֤
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
	// * ��Դ�����߶���
	// *
	// * @author Jeff Tang
	// * @version 1.0
	// * @param <TReferredByFacade>
	// * ���ñ���Դ����Դ���������
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
	// // ����Ϊ�ڲ�����
	// //////////////////////////////////////////////////////////////////////////
	// ///////

	/**
	 * �����������еĵ�ǰģ����Ϣ
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
	 * �����
	 */
	final Class<?> facadeClass;
	/**
	 * ʵ����
	 */
	final Class<?> implClass;
	/**
	 * 
	 */
	final StructDefineImpl implStruct;
	/**
	 * ����
	 */
	final Class<?> keysClass;

	/**
	 * ���ظ���Դ����������Դ����λ�ã�ֻ�и���Դ���з��أ�����Դ�洢�ڸ���Դ����Դ����
	 */
	@SuppressWarnings("unchecked")
	final ResourceGroup ensureResourceGroup(Object category,
			ContextImpl<?, ?, ?> context) {
		if (this.ownerResourceService != null) {
			throw new UnsupportedOperationException("����ԴΪ����Դ�����ܶ���ʹ��");
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
				throw new IllegalDeclarationException("��" + this.facadeClass
						+ "�����͵���Դ����֧��[" + category + "]���");
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
		throw new UnsupportedOperationException("��֧�ֵ���Դ����:" + this.kind);
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
	// // ������Դ����Ϣ
	// ///////////////////

	/**
	 * ����Դ������
	 */
	ResourceServiceBase<?, ?, ?> ownerResourceService;
	/**
	 * ��Ϊ����Դʱ�ڸ���Դ�е�λ��
	 */
	private byte indexAsSubResourceService;

	final byte getIndexAsSubResourceService() {
		return this.indexAsSubResourceService;
	}

	/**
	 * ����Դ������
	 */
	private List<ResourceServiceBase<?, ?, ?>> subResourceServices;

	/**
	 * ���¸���Դ������
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
	// // ����λ�õĻ���
	// ///////////////////

	/**
	 * ��Դ������Ϣ
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
		 * ������Դ������λ��
		 */
		final byte resourceIndexIndex;
		/**
		 * ��һ������Դ�ڱ���Դ�е�λ��
		 */
		final byte subResourceIndex;
		/**
		 * ��ǰ�����ļ��ĸ���
		 */
		final byte keyCount;
		/**
		 * ��һ������Դ��Դ��������Ϣ
		 */
		final RestResourceIndexInfo subIndexInfo;
	}

	final static class ResourceIndexInfo extends RestResourceIndexInfo {
		/**
		 * ��·����Ϣ����ʼ��Դ������
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
	 * ��·����Ϣ
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
		 * ������
		 */
		final Class<?> keyClass;
		/**
		 * ��ǰ����Ӧ����Դ������Ϣ
		 */
		ResourceIndexInfo indexInfo;
		/**
		 * ��һ��ͬ���ļ�
		 */
		KeyPathInfo next;
		/**
		 * ��һ���¼��ļ�
		 */
		KeyPathInfo firstSub;

		/**
		 * ���ݼ��������ɻ򷵻�һ���¼��ڵ�
		 * 
		 * @param keyClass
		 *            ������
		 * @param indexInfo
		 *            ������Ϣ
		 * @return ������һ���ڵ�
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
		 * ��Դ������
		 */
		final ResourceServiceBase<?, ?, ?> fromResourceService;
		/**
		 * ��һ����
		 */
		RootKeyPathInfo nextRoot;
	}

	/**
	 * ��·����Ϣ
	 */
	private RootKeyPathInfo keyPathInfos;

	/**
	 * ���ݼ��������ɻ򷵻�һ�����ڵ�
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
				// ǰ����Ч
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
				throw new IllegalStateException("��Դ����["
						+ resourceServices.get(i) + "]��û�ж����κ���Դ�ṩ��");
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
	 * �����·����Ϣ�Լ���Դ������Ϣ����
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
	// //// ��������·��
	// /////////////////////////////////////////////
	/**
	 * ������Դ����·����Ϣ
	 */
	final ResourceIndexInfo findIndexInfo(
			ResourceServiceBase<?, ?, ?> fromResourceService, Object key1,
			Object key2, Object key3, Object[] otherKeys) {
		RootKeyPathInfo rkpi = this.keyPathInfos;
		// fromResourceService == null����ʹ�ø���Դ����������ǰ��������·��
		// �����������ڵ�һ��
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
	 * ������Դ����·����Ϣ
	 */
	final ResourceIndexInfo findIndexInfoByKeyClass(
			ResourceServiceBase<?, ?, ?> fromResourceService,
			Class<?> key1Class, Class<?> key2Class, Class<?> key3Class,
			Class<?>[] otherKeyClasses) {
		RootKeyPathInfo rkpi = this.keyPathInfos;
		// fromResourceService == null����ʹ�ø���Դ����������ǰ��������·��
		// �����������ڵ�һ��
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
	 * ������Դ����·����Ϣ
	 * 
	 * @param holder
	 *            ��fromResourceService���ϼ�ResourceService��
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
	 * ���ݶ�ȡ�����ͻ������Դ������
	 * 
	 * @param subResourceReaderClass
	 *            ����Դ��ȡ������
	 * @return ��������Դ��ȡ������
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
	 * �������category��hashcode
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
	 * ����
	 */
	ResourceKind kind;

	private final boolean needFilter;
	final Comparator<TImpl> defaultSortComparator;

	/**
	 * @return ���ع���֮ǰ�Ĵ�С
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
	 * �Ƿ���Ҫ��ʼ��
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
					"��Դʵ�����͵Ľṹ�޷�֧���ڴ�����", e));
		}
	}
}
