package org.eclipse.jt.core;

import org.eclipse.jt.core.misc.MissingObjectException;

/**
 * 对象请求器抽象接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ObjectQuerier {
	/**
	 * 请求指定类型的接口或对象
	 * 
	 * @param <TFacade> 请求的类型
	 * @param facadeClass 请求的类型的类
	 * @return 返回对象或接口
	 * @throws UnsupportedOperationException 对象请求器不支持这种类型
	 * @throws MissingObjectException 对象请求器支持这种类型但是没有返回有效的对象
	 */
	public <TFacade> TFacade get(Class<TFacade> facadeClass)
			throws UnsupportedOperationException, MissingObjectException;

	/**
	 * 根据类型请求对象或接口
	 * 
	 * @param <TFacade> 被请求的类类型或接口类型
	 * @param facadeClass 被请求的类型
	 * @param key 键
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException;

	/**
	 * 请求对象或接口
	 * 
	 * @param <TFacade> 被请求的类类型或接口类型
	 * @param facadeClass 被请求的类型
	 * @param key1 键1
	 * @param key2 键2
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException,
			MissingObjectException;

	/**
	 * 请求对象或接口
	 * 
	 * @param <TFacade> 被请求的类类型或接口类型
	 * @param facadeClass 被请求的类型
	 * @param key1 键1
	 * @param key2 键2
	 * @param key3 键3
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException,
			MissingObjectException;

	/**
	 * 请求单例
	 * 
	 * @param <TFacade> 被请求的类类型或接口类型
	 * @param facadeClass 被请求的类型
	 * @param keys 键列表
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException, MissingObjectException;

	/**
	 * 查找指定类型的接口或对象
	 * 
	 * @param <TFacade> 被请求的类类型或接口类型
	 * @param facadeClass 被请求的类型
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade find(Class<TFacade> facadeClass)
			throws UnsupportedOperationException;

	/**
	 * 请求对象或接口
	 * 
	 * @param <TFacade> 被请求的类类型或接口类型
	 * @param facadeClass 被请求的类型
	 * @param key 键
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException;

	/**
	 * 请求对象或接口
	 * 
	 * @param <TFacade> 被请求的类类型或接口类型
	 * @param facadeClass 被请求的类型
	 * @param key1 键1
	 * @param key2 键2
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException;

	/**
	 * 请求对象或接口
	 * 
	 * @param <TFacade> 被请求的类类型或接口类型
	 * @param facadeClass 被请求的类型
	 * @param key1 键1
	 * @param key2 键2
	 * @param key3 键3
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException;

	/**
	 * 请求单例
	 * 
	 * @param <TFacade> 被请求的类类型或接口类型
	 * @param facadeClass 被请求的类型
	 * @param key1 键1
	 * @param key2 键2
	 * @param key3 键3
	 * @param keys 键列表
	 * @return 返回对象或接口
	 */
	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException;

	// -----------------------------------------以下为权限相关---------------------------------------------------
	
	

	// -----------------------------------------以上为权限相关---------------------------------------------------
	
}
