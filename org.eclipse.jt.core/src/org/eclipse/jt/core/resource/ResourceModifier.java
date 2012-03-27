/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File CategoryResourceMaster.java
 * Date 2008-11-19
 */
package org.eclipse.jt.core.resource;

import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.exception.DeadLockException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface ResourceModifier<TFacade, TImpl extends TFacade, TKeysHolder>
		extends ResourceQuerier, ResourcePutter<TFacade, TImpl, TKeysHolder> {
	/**
	 * ������Դ����
	 * 
	 * @param resource
	 *            ��Դ���
	 * @param keys
	 *            ��Դ��Դ
	 * @param policy
	 *            ��Դ����Ӳ���
	 */
	public ResourceToken<TFacade> putResource(TImpl resource, TKeysHolder keys,
			ResourceService.WhenExists policy);

	/**
	 * ������Դ����
	 * 
	 * @param resource
	 *            ��Դ���
	 * @param keys
	 *            ��Դ��Դ
	 * @param policy
	 *            ��Դ����Ӳ���
	 */
	public ResourceToken<TFacade> putResource(
			ResourceToken<TFacade> treeParent, TImpl resource,
			TKeysHolder keys, ResourceService.WhenExists policy);

	/**
	 * ��¡��Դ
	 */
	public TImpl cloneResource(ResourceToken<TFacade> token);

	/**
	 * ��¡��Դ
	 * 
	 * @param tryReuse
	 *            ���Ա����õ�ʵ�������ٶ��󴴽��ɱ�������ֵ��Ϊ��
	 */
	public TImpl cloneResource(ResourceToken<TFacade> token, TImpl tryReuse);

	/**
	 * �޸�ĳ��Դ
	 */
	public TImpl modifyResource() throws DeadLockException;

	/**
	 * �޸�ĳ��Դ
	 */
	public <TKey> TImpl modifyResource(TKey key) throws DeadLockException;

	/**
	 * �޸�ĳ��Դ
	 */
	public <TKey1, TKey2> TImpl modifyResource(TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * �޸�ĳ��Դ
	 */
	public <TKey1, TKey2, TKey3> TImpl modifyResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * �޸�ĳ��Դ
	 */
	public <TKey1, TKey2, TKey3> TImpl modifyResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * ʹ��Դ���޸���Ч
	 */
	void postModifiedResource(TImpl modifiedResource);

	/**
	 * ɾ��ĳ��Դ
	 */
	public TImpl removeResource() throws DeadLockException;

	/**
	 * ɾ��ĳ��Դ
	 */
	public <TKey> TImpl removeResource(TKey key) throws DeadLockException;

	/**
	 * ɾ��ĳ��Դ
	 */
	public <TKey1, TKey2> TImpl removeResource(TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * ɾ��ĳ��Դ
	 */
	public <TKey1, TKey2, TKey3> TImpl removeResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * ɾ��ĳ��Դ
	 */
	public <TKey1, TKey2, TKey3> TImpl removeResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * ����Դ��Ϊ��Ч
	 */
	public void invalidResource() throws DeadLockException;

	/**
	 * ����Դ��Ϊ��Ч
	 */
	public <TKey> void invalidResource(TKey key) throws DeadLockException;

	/**
	 * ����Դ��Ϊ��Ч
	 */
	public <TKey1, TKey2> void invalidResource(TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * ����Դ��Ϊ��Ч
	 */
	public <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * ����Դ��Ϊ��Ч
	 */
	public <TKey1, TKey2, TKey3> void invalidResource(TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	// -----------------------------------------����ΪȨ�����---------------------------------------------------
	
	/**
	 * ��¡��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param token
	 *            ��Դ��ʶ
	 * @return ���ؿ�¡����Դ
	 */
	public TImpl cloneResource(
			Operation<? super TFacade> operation, ResourceToken<TFacade> token);

	/**
	 * ��¡��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param token
	 *            ��Դ��ʶ
	 * @param tryReuse
	 *            ���Ա����õ�ʵ�������ٶ��󴴽��ɱ�������ֵ��Ϊ��
	 * @return ���ؿ�¡����Դ
	 */
	public TImpl cloneResource(
			Operation<? super TFacade> operation, ResourceToken<TFacade> token,
			TImpl tryReuse);

	/**
	 * �޸�ĳ��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public TImpl modifyResource( Operation<? super TFacade> operation)
			throws DeadLockException;

	/**
	 * �޸�ĳ��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey> TImpl modifyResource(
			Operation<? super TFacade> operation, TKey key)
			throws DeadLockException;

	/**
	 * �޸�ĳ��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey1, TKey2> TImpl modifyResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * �޸�ĳ��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey1, TKey2, TKey3> TImpl modifyResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * �޸�ĳ��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param keys
	 *            ������
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey1, TKey2, TKey3> TImpl modifyResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * ɾ��ĳ��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public TImpl removeResource( Operation<? super TFacade> operation)
			throws DeadLockException;

	/**
	 * ɾ��ĳ��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey> TImpl removeResource(
			Operation<? super TFacade> operation, TKey key)
			throws DeadLockException;

	/**
	 * ɾ��ĳ��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey1, TKey2> TImpl removeResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * ɾ��ĳ��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey1, TKey2, TKey3> TImpl removeResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * ɾ��ĳ��Դ
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param keys
	 *            ������
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey1, TKey2, TKey3> TImpl removeResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;

	/**
	 * ����Դ��Ϊ��Ч
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public void invalidResource( Operation<? super TFacade> operation)
			throws DeadLockException;

	/**
	 * ����Դ��Ϊ��Ч
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey> void invalidResource(
			Operation<? super TFacade> operation, TKey key)
			throws DeadLockException;

	/**
	 * ����Դ��Ϊ��Ч
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey1, TKey2> void invalidResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2)
			throws DeadLockException;

	/**
	 * ����Դ��Ϊ��Ч
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3) throws DeadLockException;

	/**
	 * ����Դ��Ϊ��Ч
	 * 
	 * @param operation
	 *            Ҫ�����Ȩ�޵Ĳ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param keys
	 *            ������
	 * @throws DeadLockException
	 *             �����쳣
	 */
	public <TKey1, TKey2, TKey3> void invalidResource(
			Operation<? super TFacade> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object... keys) throws DeadLockException;
	
	// -----------------------------------------����ΪȨ�����---------------------------------------------------
	
}
