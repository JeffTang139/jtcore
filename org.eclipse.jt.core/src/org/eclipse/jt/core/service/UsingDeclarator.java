package org.eclipse.jt.core.service;

import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;

/**
 * 深明使用框架某些调用的接口
 * 
 * @author Jeff Tang
 * 
 */
public interface UsingDeclarator {
	/**
	 * 声明使用某任务极其方法
	 * 
	 * @param taskClass
	 *            任务类
	 * @param method
	 *            第一个方法
	 * @param others
	 *            余下方法
	 */
	public <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> void usingTask(
			Class<TTask> taskClass, TMethod method, TMethod... others);

	public void usingTask(Class<? extends SimpleTask> taskClass);

	/**
	 * 声明使用某资源
	 * 
	 * @param facadeClass
	 *            资源外观类
	 */
	public void usingResource(Class<?> facadeClass);

	public void usingResource(Class<?> facadeClass, Class<?> key);

	public void usingResource(Class<?> facadeClass, Class<?> key1, Class<?> key2);

	public void usingResource(Class<?> facadeClass, Class<?> key1,
			Class<?> key2, Class<?> key3);

	public void usingResource(Class<?> facadeClass, Class<?> key1,
			Class<?> key2, Class<?> key3, Class<?>... otherKeyClasses);

	/**
	 * 声明使用某结果的查询
	 * 
	 * @param resultClass
	 *            结果类
	 */
	public void usingResult(Class<?> resultClass);

	/**
	 * 声明使用某结果的查询
	 * 
	 * @param resultClass
	 *            结果类
	 * @param keyClass
	 *            查询凭据类
	 */
	public void usingResult(Class<?> resultClass, Class<?> keyClass);

	/**
	 * 声明使用某结果的查询
	 * 
	 * @param resultClass
	 *            结果类
	 * @param keyClass
	 *            查询凭据类
	 */
	public void usingResult(Class<?> resultClass, Class<?> key1Class,
			Class<?> key2Class);

	/**
	 * 声明使用某结果的查询
	 * 
	 * @param resultClass
	 *            结果类
	 * @param keyClass
	 *            查询凭据类
	 */
	public void usingResult(Class<?> resultClass, Class<?> key1Class,
			Class<?> key2Class, Class<?> key3Class);

	/**
	 * 声明使用某结果的查询
	 * 
	 * @param resultClass
	 *            结果类
	 */
	public void usingList(Class<?> resultClass);

	/**
	 * 声明使用某结果的查询
	 * 
	 * @param resultClass
	 *            结果类
	 * @param keyClass
	 *            查询凭据类
	 */
	public void usingList(Class<?> resultClass, Class<?> keyClass);

	/**
	 * 声明使用某结果的查询
	 * 
	 * @param resultClass
	 *            结果类
	 * @param keyClass
	 *            查询凭据类
	 */
	public void usingList(Class<?> resultClass, Class<?> key1Class,
			Class<?> key2Class);

	/**
	 * 声明使用某结果的查询
	 * 
	 * @param resultClass
	 *            结果类
	 * @param keyClass
	 *            查询凭据类
	 */
	public void usingList(Class<?> resultClass, Class<?> key1Class,
			Class<?> key2Class, Class<?> key3Class);

	/**
	 * 声明使用某事件监听器
	 * 
	 * @param eventClass
	 *            事件类
	 */
	public void usingEventListener(Class<? extends Event> eventClass);
}
