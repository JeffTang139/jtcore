package org.eclipse.jt.core.resource;

import java.util.Comparator;
import java.util.List;

import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.LifeHandle;
import org.eclipse.jt.core.ListQuerier;
import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.TreeNodeFilter;
import org.eclipse.jt.core.TreeQuerier;
import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.misc.MissingObjectException;


/**
 * ��Դ�������ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ResourceQuerier extends ObjectQuerier, ListQuerier,
        TreeQuerier, LifeHandle {
	/**
	 * �����Դ���
	 */
	public Object getCategory();

	/**
	 * ȷ��ָ��������͵���Դ�Ѿ�����ʼ���ˡ�<br/>
	 * ���ָ������Դ��δ��ʼ������������֤�ᴥ�����ʼ�����̣����ڳ�ʼ�����֮�󷵻ء� <br/>
	 * 
	 * ��ʼ������Դ���������Category����ͨ��<code>getCategory()</code>������ȡ�������һ�¡�
	 * 
	 * @param <TFacade>
	 *            ��Դ���������
	 * @param facadeClass
	 *            ��Դ������͵�ʵ��
	 */
	public <TFacade> void ensureResourceInited(Class<TFacade> facadeClass);

	/**
	 * ����������Դ(S��)
	 * 
	 * @param <TFacade>
	 *            ��Դ����۽ӿ�����
	 * @param resourceToken
	 *            ��Դ�ļǺ�
	 * @return ������Դ���������
	 */
	public <TFacade> ResourceHandle<TFacade> lockResourceS(
	        ResourceToken<TFacade> resourceToken);

	/**
	 * �ɸ���������Դ(U��)
	 * 
	 * @param <TFacade>
	 *            ��Դ����۽ӿ�����
	 * @param resourceToken
	 *            ��Դ�ļǺ�
	 * @return ������Դ���������
	 */
	public <TFacade> ResourceHandle<TFacade> lockResourceU(
	        ResourceToken<TFacade> resourceToken);

	/**
	 * ��ȡ��Դ�Ǻš�
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param facadeClass
	 *            ��Դ�������
	 * @return ��Դ�Ǻ�
	 * @throws MissingObjectException
	 *             ���Ҳ�����Ч����Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(Class<TFacade> facadeClass)
	        throws MissingObjectException;

	/**
	 * ��ȡ��Դ�Ǻš�
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key
	 * @return ��Դ�Ǻ�
	 * @throws MissingObjectException
	 *             ���Ҳ�����Ч����Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Class<TFacade> facadeClass, Object key)
	        throws MissingObjectException;

	/**
	 * ��ȡ��Դ�Ǻš�
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 * @param key2
	 * @return ��Դ�Ǻ�
	 * @throws MissingObjectException
	 *             ���Ҳ�����Ч����Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2)
	        throws MissingObjectException;

	/**
	 * ��ȡ��Դ�Ǻš�
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 * @param key2
	 * @param key3
	 * @return ��Դ�Ǻ�
	 * @throws MissingObjectException
	 *             ���Ҳ�����Ч����Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
	        throws MissingObjectException;

	/**
	 * ��ȡ��Դ�Ǻš�
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return ��Դ�Ǻ�
	 * @throws MissingObjectException
	 *             ���Ҳ�����Ч����Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
	        Object... otherKeys) throws MissingObjectException;

	/**
	 * ������Դ�Ǻţ�������Ҳ��������ؿգ�<code>null</code>����
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param facadeClass
	 *            ��Դ�������
	 * @return ��Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Class<TFacade> facadeClass);

	/**
	 * ������Դ�Ǻţ�������Ҳ��������ؿգ�<code>null</code>����
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key
	 * @return ��Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Class<TFacade> facadeClass, Object key);

	/**
	 * ������Դ�Ǻţ�������Ҳ��������ؿգ�<code>null</code>����
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 * @param key2
	 * @return ��Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2);

	/**
	 * ������Դ�Ǻţ�������Ҳ��������ؿգ�<code>null</code>����
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 * @param key2
	 * @param key3
	 * @return ��Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3);

	/**
	 * ������Դ�Ǻţ�������Ҳ��������ؿգ�<code>null</code>����
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return ��Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
	        Object... otherKeys);

	/**
	 * ��ȡ������Դ
	 * 
	 * @param <TFacade>
	 * @param <THolderFacade>
	 * @param facadeClass
	 * @param holderFacadeClass
	 * @return
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Class<TFacade> facadeClass, ResourceToken<THolderFacade> holderToken);

	/**
	 * ��ȡ������Դ
	 * 
	 * @param <TFacade>
	 * @param <THolderFacade>
	 * @param facadeClass
	 * @param holderToken
	 * @param filter
	 *            ������
	 * @return
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Filter<? super TFacade> filter);

	/**
	 * ��ȡ������Դ
	 * 
	 * @param <TFacade>
	 * @param <THolderFacade>
	 * @param facadeClass
	 * @param holderToken
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Comparator<? super TFacade> sortComparator);

	/**
	 * ��ȡ������Դ
	 * 
	 * @param <TFacade>
	 * @param <THolderFacade>
	 * @param facadeClass
	 * @param holderToken
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator);
	
	// -----------------------------------------����ΪȨ�����---------------------------------------------------

	/**
	 * ��ȡָ�����Ͳ��Ҿ���ָ������Ȩ�޵���Դ�Ǻ�
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @return ��Դ�Ǻ�
	 * @throws MissingObjectException
	 *             ���Ҳ�����Ч����Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass)
	        throws MissingObjectException;

	/**
	 * ��ȡָ�����Ͳ��Ҿ���ָ������Ȩ�޵���Դ�Ǻ�
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key
	 *            ��
	 * @return ��Դ�Ǻ�
	 * @throws MissingObjectException
	 *             ���Ҳ�����Ч����Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key) throws MissingObjectException;

	/**
	 * ��ȡָ�����Ͳ��Ҿ���ָ������Ȩ�޵���Դ�Ǻ�
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ��Դ�Ǻ�
	 * @throws MissingObjectException
	 *             ���Ҳ�����Ч����Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2) throws MissingObjectException;

	/**
	 * ��ȡָ�����Ͳ��Ҿ���ָ������Ȩ�޵���Դ�Ǻ�
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ��Դ�Ǻ�
	 * @throws MissingObjectException
	 *             ���Ҳ�����Ч����Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3)
	        throws MissingObjectException;

	/**
	 * ��ȡָ�����Ͳ��Ҿ���ָ������Ȩ�޵���Դ�Ǻ�
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ��Դ�Ǻ�
	 * @throws MissingObjectException
	 *             ���Ҳ�����Ч����Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys)
	        throws MissingObjectException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵���Դ�Ǻţ�������Ҳ��������ؿգ�<code>null</code>��
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @return ��Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass);

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵���Դ�Ǻţ�������Ҳ��������ؿգ�<code>null</code>��
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key
	 *            ��
	 * @return ��Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key);

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵���Դ�Ǻţ�������Ҳ��������ؿգ�<code>null</code>��
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ��Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2);

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵���Դ�Ǻţ�������Ҳ��������ؿգ�<code>null</code>��
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ��Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3);

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵���Դ�Ǻţ�������Ҳ��������ؿգ�<code>null</code>��
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ��Դ�Ǻ�
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys);

	/**
	 * ��ȡָ�����Ͳ��Ҿ���ָ������Ȩ�޵�������Դ
	 * 
	 * @param <TFacade>
	 *            ��Դ��۽ӿ�
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��Դ�������
	 * @param holderFacadeClass
	 *            ������Դ�������
	 * @return ����������Դ
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken);

	/**
	 * ��ȡָ�����Ͳ��Ҿ���ָ������Ȩ�޵�������Դ
	 * 
	 * @param <TFacade>
	 *            ��������Դ�����
	 * @param <THolderFacade>
	 *            ������Դ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �������Դ���������
	 * @param holderToken
	 *            ������Դ��ʶ
	 * @param filter
	 *            ������
	 * @return ����������Դ
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Filter<? super TFacade> filter);

	/**
	 * ��ȡָ�����Ͳ��Ҿ���ָ������Ȩ�޵�������Դ
	 * 
	 * @param <TFacade>
	 *            ��������Դ�����
	 * @param <THolderFacade>
	 *            ������Դ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �������Դ���������
	 * @param holderToken
	 *            ������Դ��ʶ
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return ����������Դ
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Comparator<? super TFacade> sortComparator);

	/**
	 * ��ȡָ�����Ͳ��Ҿ���ָ������Ȩ�޵�������Դ
	 * 
	 * @param <TFacade>
	 *            ��������Դ�����
	 * @param <THolderFacade>
	 *            ������Դ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �������Դ���������
	 * @param holderToken
	 *            ������Դ��ʶ
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return ����������Դ
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator);
	
	// ----------------------ObjectQuerier Override----------------------------------------------
	
	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ľӿڻ����
	 * 
	 * @param <TFacade>
	 *            ���������
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            ��������͵���
	 * @return ���ض����ӿ�
	 * @throws UnsupportedOperationException
	 *             ������������֧����������
	 * @throws MissingObjectException
	 *             ����������֧���������͵���û�з�����Ч�Ķ���
	 */
	public <TFacade> TFacade get(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass) throws UnsupportedOperationException,
	        MissingObjectException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ľӿڻ����
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key
	 *            ��
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade get(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key)
	        throws UnsupportedOperationException, MissingObjectException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ľӿڻ����
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade get(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2)
	        throws UnsupportedOperationException, MissingObjectException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ľӿڻ����
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade get(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException, MissingObjectException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ľӿڻ����
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param keys
	 *            ���б�
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade get(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
	        Object... keys) throws UnsupportedOperationException,
	        MissingObjectException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ľӿڻ����
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade find(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ľӿڻ����
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param facadeClass
	 *            �����������
	 * @param key
	 *            ��
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade find(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ľӿڻ����
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade find(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ľӿڻ����
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade find(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ľӿڻ����
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param keys
	 *            ���б�
	 * @return ���ض����ӿ�
	 */
	public <TFacade> TFacade find(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
	        Object... keys) throws UnsupportedOperationException;
	
	// ----------------------ListQuerier Override----------------------------------------------

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key
	 *            ��
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys);

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key
	 *            ��
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key1, Object key2,
	        Object key3) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key1, Object key2,
	        Object key3, Object... otherKeys);

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key
	 *            ��
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys);

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key
	 *            ��
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ��б�
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ���ض����ӿ��б�
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys);
	
	// ----------------------TreeQuerier Override----------------------------------------------
	
	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass)
	        throws UnsupportedOperationException;
	
	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param key
	 *            ��
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key
	 *            ��
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
	        Object key3) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
	        Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key
	 *            ��
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key
	 *            ��
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facade
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * ����ָ�����Ͳ��Ҿ���ָ������Ȩ�޵Ķ����ӿ������
	 * 
	 * @param <TFacade>
	 *            ������������ͻ�ӿ�����
	 * @param operation
	 *            �������������Դ�Ĳ���
	 * @param facadeClass
	 *            �����������
	 * @param filter
	 *            ������
	 * @param sortComparator
	 *            ����Ƚ���
	 * @param key1
	 *            ��1
	 * @param key2
	 *            ��2
	 * @param key3
	 *            ��3
	 * @param otherKeys
	 *            ������
	 * @return ���ض����ӿ������
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;
	
	// -----------------------------------------����ΪȨ�����---------------------------------------------------
	
}
