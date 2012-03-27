package org.eclipse.jt.core.impl;

import java.lang.reflect.Method;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.impl.ResourceServiceBase.KeyPathInfo;
import org.eclipse.jt.core.impl.ResourceServiceBase.ResourceIndexInfo;
import org.eclipse.jt.core.resource.ResourceInserter;


/**
 * ��Դ�ṩ������
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
	 * ������Դ��Դ���ص�ǰ�ṩ����Ӧ�ļ�
	 * 
	 * @param keys
	 *            ��Դ��Դ
	 * @return ���ص�ǰ�ṩ����Ӧ�ļ�
	 */
	protected TKey1 getKey1(TKeysHolder keys) {
		return null;
	}

	/**
	 * ������Դ��Դ���ص�ǰ�ṩ����Ӧ�ļ�
	 * 
	 * @param keys
	 *            ��Դ��Դ
	 * @return ���ص�ǰ�ṩ����Ӧ�ļ�
	 */
	protected TKey2 getKey2(TKeysHolder keys) {
		return null;
	}

	/**
	 * ������Դ��Դ���ص�ǰ�ṩ����Ӧ�ļ�
	 * 
	 * @param keys
	 *            ��Դ��Դ
	 * @return ���ص�ǰ�ṩ����Ӧ�ļ�
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
	 * �½�Index��ʵ��
	 * 
	 * @param group
	 *            ��Ӧ����Դ����
	 * @return �´�����Indexʵ��
	 */
	abstract ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> newIndex(
	        ResourceGroup<TFacade, TImpl, TKeysHolder> group);

	/**
	 * �½�IndexEntry��ʵ��
	 * 
	 * @param hash
	 *            - ��Դ��hashֵ
	 * @param resourceItem
	 *            - ��Դ��
	 * @param next
	 *            - Ҫ���ӵ���һ��IndexEntryʵ��
	 * @return �´�����IndexEntryʵ��
	 */
	abstract ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> newIndexEntry(
	        ResourceIndex<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> index,
	        int hash,
	        ResourceItem<TFacade, TImpl, TKeysHolder> resourceItem,
	        ResourceIndexEntry<TFacade, TImpl, TKeysHolder, TKey1, TKey2, TKey3> next,
	        TKey1 key1, TKey2 key2, TKey3 key3);

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
	void provide(Context context,
	        ResourceInserter<TFacade, TImpl, TKeysHolder> setter)
	        throws Throwable {
		throw new UnsupportedOperationException();
	}

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
	void provide(Context context,
	        ResourceInserter<TFacade, TImpl, TKeysHolder> setter, TKey1 key)
	        throws Throwable {
		throw new UnsupportedOperationException();
	}

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
	void provide(Context context,
	        ResourceInserter<TFacade, TImpl, TKeysHolder> setter, TKey1 key1,
	        TKey2 key2) throws Throwable {
		throw new UnsupportedOperationException();
	}

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
	void provide(Context context,
	        ResourceInserter<TFacade, TImpl, TKeysHolder> setter, TKey1 key1,
	        TKey2 key2, TKey3 key3) throws Throwable {
		throw new UnsupportedOperationException();
	}
}
