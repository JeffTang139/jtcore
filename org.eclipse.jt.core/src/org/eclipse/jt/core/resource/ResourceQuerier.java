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
 * 资源请求器接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ResourceQuerier extends ObjectQuerier, ListQuerier,
        TreeQuerier, LifeHandle {
	/**
	 * 获得资源类别
	 */
	public Object getCategory();

	/**
	 * 确保指定外观类型的资源已经被初始化了。<br/>
	 * 如果指定的资源尚未初始化，本方法保证会触发其初始化过程，并在初始化完成之后返回。 <br/>
	 * 
	 * 初始化的资源所属的类别（Category）与通过<code>getCategory()</code>方法获取到的类别一致。
	 * 
	 * @param <TFacade>
	 *            资源的外观类型
	 * @param facadeClass
	 *            资源外观类型的实例
	 */
	public <TFacade> void ensureResourceInited(Class<TFacade> facadeClass);

	/**
	 * 共享锁定资源(S锁)
	 * 
	 * @param <TFacade>
	 *            资源的外观接口类型
	 * @param resourceToken
	 *            资源的记号
	 * @return 返回资源的锁定句柄
	 */
	public <TFacade> ResourceHandle<TFacade> lockResourceS(
	        ResourceToken<TFacade> resourceToken);

	/**
	 * 可更新锁定资源(U锁)
	 * 
	 * @param <TFacade>
	 *            资源的外观接口类型
	 * @param resourceToken
	 *            资源的记号
	 * @return 返回资源的锁定句柄
	 */
	public <TFacade> ResourceHandle<TFacade> lockResourceU(
	        ResourceToken<TFacade> resourceToken);

	/**
	 * 获取资源记号。
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param facadeClass
	 *            资源的外观类
	 * @return 资源记号
	 * @throws MissingObjectException
	 *             查找不到有效的资源记号
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(Class<TFacade> facadeClass)
	        throws MissingObjectException;

	/**
	 * 获取资源记号。
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param facadeClass
	 *            资源的外观类
	 * @param key
	 * @return 资源记号
	 * @throws MissingObjectException
	 *             查找不到有效的资源记号
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Class<TFacade> facadeClass, Object key)
	        throws MissingObjectException;

	/**
	 * 获取资源记号。
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 * @param key2
	 * @return 资源记号
	 * @throws MissingObjectException
	 *             查找不到有效的资源记号
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2)
	        throws MissingObjectException;

	/**
	 * 获取资源记号。
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 * @param key2
	 * @param key3
	 * @return 资源记号
	 * @throws MissingObjectException
	 *             查找不到有效的资源记号
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
	        throws MissingObjectException;

	/**
	 * 获取资源记号。
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return 资源记号
	 * @throws MissingObjectException
	 *             查找不到有效的资源记号
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
	        Object... otherKeys) throws MissingObjectException;

	/**
	 * 查找资源记号，如果查找不到，返回空（<code>null</code>）。
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param facadeClass
	 *            资源的外观类
	 * @return 资源记号
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Class<TFacade> facadeClass);

	/**
	 * 查找资源记号，如果查找不到，返回空（<code>null</code>）。
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param facadeClass
	 *            资源的外观类
	 * @param key
	 * @return 资源记号
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Class<TFacade> facadeClass, Object key);

	/**
	 * 查找资源记号，如果查找不到，返回空（<code>null</code>）。
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 * @param key2
	 * @return 资源记号
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2);

	/**
	 * 查找资源记号，如果查找不到，返回空（<code>null</code>）。
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 * @param key2
	 * @param key3
	 * @return 资源记号
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3);

	/**
	 * 查找资源记号，如果查找不到，返回空（<code>null</code>）。
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 * @param key2
	 * @param key3
	 * @param otherKeys
	 * @return 资源记号
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
	        Object... otherKeys);

	/**
	 * 获取引用资源
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
	 * 获取引用资源
	 * 
	 * @param <TFacade>
	 * @param <THolderFacade>
	 * @param facadeClass
	 * @param holderToken
	 * @param filter
	 *            过滤器
	 * @return
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Filter<? super TFacade> filter);

	/**
	 * 获取引用资源
	 * 
	 * @param <TFacade>
	 * @param <THolderFacade>
	 * @param facadeClass
	 * @param holderToken
	 * @param sortComparator
	 *            排序比较器
	 * @return
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Comparator<? super TFacade> sortComparator);

	/**
	 * 获取引用资源
	 * 
	 * @param <TFacade>
	 * @param <THolderFacade>
	 * @param facadeClass
	 * @param holderToken
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @return
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator);
	
	// -----------------------------------------以上为权限相关---------------------------------------------------

	/**
	 * 获取指定类型并且具有指定操作权限的资源记号
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @return 资源记号
	 * @throws MissingObjectException
	 *             查找不到有效的资源记号
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass)
	        throws MissingObjectException;

	/**
	 * 获取指定类型并且具有指定操作权限的资源记号
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @param key
	 *            键
	 * @return 资源记号
	 * @throws MissingObjectException
	 *             查找不到有效的资源记号
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key) throws MissingObjectException;

	/**
	 * 获取指定类型并且具有指定操作权限的资源记号
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 资源记号
	 * @throws MissingObjectException
	 *             查找不到有效的资源记号
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2) throws MissingObjectException;

	/**
	 * 获取指定类型并且具有指定操作权限的资源记号
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 资源记号
	 * @throws MissingObjectException
	 *             查找不到有效的资源记号
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3)
	        throws MissingObjectException;

	/**
	 * 获取指定类型并且具有指定操作权限的资源记号
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 资源记号
	 * @throws MissingObjectException
	 *             查找不到有效的资源记号
	 */
	<TFacade> ResourceToken<TFacade> getResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys)
	        throws MissingObjectException;

	/**
	 * 查找指定类型并且具有指定操作权限的资源记号，如果查找不到，返回空（<code>null</code>）
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @return 资源记号
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass);

	/**
	 * 查找指定类型并且具有指定操作权限的资源记号，如果查找不到，返回空（<code>null</code>）
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @param key
	 *            键
	 * @return 资源记号
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key);

	/**
	 * 查找指定类型并且具有指定操作权限的资源记号，如果查找不到，返回空（<code>null</code>）
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 资源记号
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2);

	/**
	 * 查找指定类型并且具有指定操作权限的资源记号，如果查找不到，返回空（<code>null</code>）
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 资源记号
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3);

	/**
	 * 查找指定类型并且具有指定操作权限的资源记号，如果查找不到，返回空（<code>null</code>）
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 资源记号
	 */
	<TFacade> ResourceToken<TFacade> findResourceToken(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys);

	/**
	 * 获取指定类型并且具有指定操作权限的引用资源
	 * 
	 * @param <TFacade>
	 *            资源外观接口
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            资源的外观类
	 * @param holderFacadeClass
	 *            引用资源的外观类
	 * @return 返回引用资源
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken);

	/**
	 * 获取指定类型并且具有指定操作权限的引用资源
	 * 
	 * @param <TFacade>
	 *            被引用资源外观类
	 * @param <THolderFacade>
	 *            引用资源外观类
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            请求的资源的外观类型
	 * @param holderToken
	 *            引用资源标识
	 * @param filter
	 *            过滤器
	 * @return 返回引用资源
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Filter<? super TFacade> filter);

	/**
	 * 获取指定类型并且具有指定操作权限的引用资源
	 * 
	 * @param <TFacade>
	 *            被引用资源外观类
	 * @param <THolderFacade>
	 *            引用资源外观类
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            请求的资源的外观类型
	 * @param holderToken
	 *            引用资源标识
	 * @param sortComparator
	 *            排序比较器
	 * @return 返回引用资源
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Comparator<? super TFacade> sortComparator);

	/**
	 * 获取指定类型并且具有指定操作权限的引用资源
	 * 
	 * @param <TFacade>
	 *            被引用资源外观类
	 * @param <THolderFacade>
	 *            引用资源外观类
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            请求的资源的外观类型
	 * @param holderToken
	 *            引用资源标识
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @return 返回引用资源
	 */
	<TFacade, THolderFacade> List<TFacade> getResourceReferences(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        ResourceToken<THolderFacade> holderToken,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator);
	
	// ----------------------ObjectQuerier Override----------------------------------------------
	
	/**
	 * 请求指定类型并且具有指定操作权限的接口或对象
	 * 
	 * @param <TFacade>
	 *            请求的类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            请求的类型的类
	 * @return 返回对象或接口
	 * @throws UnsupportedOperationException
	 *             对象请求器不支持这种类型
	 * @throws MissingObjectException
	 *             对象请求器支持这种类型但是没有返回有效的对象
	 */
	public <TFacade> TFacade get(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass) throws UnsupportedOperationException,
	        MissingObjectException;

	/**
	 * 请求指定类型并且具有指定操作权限的接口或对象
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key
	 *            键
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade get(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key)
	        throws UnsupportedOperationException, MissingObjectException;

	/**
	 * 请求指定类型并且具有指定操作权限的接口或对象
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade get(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2)
	        throws UnsupportedOperationException, MissingObjectException;

	/**
	 * 请求指定类型并且具有指定操作权限的接口或对象
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade get(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException, MissingObjectException;

	/**
	 * 请求指定类型并且具有指定操作权限的接口或对象
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param keys
	 *            键列表
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade get(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
	        Object... keys) throws UnsupportedOperationException,
	        MissingObjectException;

	/**
	 * 查找指定类型并且具有指定操作权限的接口或对象
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade find(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass) throws UnsupportedOperationException;

	/**
	 * 查找指定类型并且具有指定操作权限的接口或对象
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param facadeClass
	 *            被请求的类型
	 * @param key
	 *            键
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade find(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key)
	        throws UnsupportedOperationException;

	/**
	 * 查找指定类型并且具有指定操作权限的接口或对象
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade find(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * 查找指定类型并且具有指定操作权限的接口或对象
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade find(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException;

	/**
	 * 查找指定类型并且具有指定操作权限的接口或对象
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param keys
	 *            键列表
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade find(Operation<? super TFacade> operation,
	        Class<TFacade> facadeClass, Object key1, Object key2, Object key3,
	        Object... keys) throws UnsupportedOperationException;
	
	// ----------------------ListQuerier Override----------------------------------------------

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key
	 *            键
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key) throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2) throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys);

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key
	 *            键
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key1, Object key2,
	        Object key3) throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter, Object key1, Object key2,
	        Object key3, Object... otherKeys);

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key
	 *            键
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys);

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key
	 *            键
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口列表
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 返回对象或接口列表
	 */
	public <TFacade> List<TFacade> getList(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Filter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys);
	
	// ----------------------TreeQuerier Override----------------------------------------------
	
	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass)
	        throws UnsupportedOperationException;
	
	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param key
	 *            键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key) throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2) throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Object key1, Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key
	 *            键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
	        Object key3) throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
	        Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key
	 *            键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key
	 *            键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1, Object key2)
	        throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facade
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * 请求指定类型并且具有指定操作权限的对象或接口树结点
	 * 
	 * @param <TFacade>
	 *            被请求的类类型或接口类型
	 * @param operation
	 *            对请求的类型资源的操作
	 * @param facadeClass
	 *            被请求的类型
	 * @param filter
	 *            过滤器
	 * @param sortComparator
	 *            排序比较器
	 * @param key1
	 *            键1
	 * @param key2
	 *            键2
	 * @param key3
	 *            键3
	 * @param otherKeys
	 *            其它键
	 * @return 返回对象或接口树结点
	 */
	public <TFacade> TreeNode<TFacade> getTreeNode(
	        Operation<? super TFacade> operation, Class<TFacade> facadeClass,
	        TreeNodeFilter<? super TFacade> filter,
	        Comparator<? super TFacade> sortComparator, Object key1,
	        Object key2, Object key3, Object... otherKeys)
	        throws UnsupportedOperationException;
	
	// -----------------------------------------以上为权限相关---------------------------------------------------
	
}
