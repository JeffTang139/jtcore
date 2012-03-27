package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.service.UsingDeclarator;
import org.eclipse.jt.core.service.Publish.Mode;


/**
 * 调用器提供器
 * 
 * @author Jeff Tang
 * 
 */
abstract class ServiceInvokeeBase<TObject, TContext, TKey1, TKey2, TKey3> {
	private static final int MASKS_SHIFT = ServiceBase.MAX_TASK_METHODS + 1;
	// 掩码
	static final int MASKS_MASK = -1 << MASKS_SHIFT;
	// 任务
	static final int MASK_TASK = 0 << MASKS_SHIFT;
	// 事件
	static final int MASK_EVENT = 1 << MASKS_SHIFT;
	// 结果
	static final int MASK_RESULT = 2 << MASKS_SHIFT;
	// 列表
	static final int MASK_LIST = 3 << MASKS_SHIFT;
	// 树形结构
	static final int MASK_TREE = 4 << MASKS_SHIFT;
	// 声明器
	static final int MASK_ELEMENT = 5 << MASKS_SHIFT;
	// 资源服务
	static final int MASK_RESOURCE = 6 << MASKS_SHIFT;
	// 定义
	static final int MASK_DEFINE = 7 << MASKS_SHIFT;
	// 类引用
	static final int MASK_ELEMENT_META = 8 << MASKS_SHIFT;
	// 定义脚本
	static final int MASK_DECLARE_SCRIPT = 9 << MASKS_SHIFT;

	static final UnsupportedOperationException buildKeysMessage(String msgHead,
			Class<?> facadeClass, Object key1, Object key2, Object key3,
			Object[] keys) {
		StringBuilder sb = new StringBuilder(msgHead);
		sb.append('[').append(facadeClass.getName());
		sb.append('(').append(key1);
		sb.append(',').append(key2);
		sb.append(',').append(key3);
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				sb.append(',').append(keys[i]);
			}
		}
		return new UnsupportedOperationException(sb.append(')').append(']')
				.toString());
	}

	/**
	 * 返回并发控制器
	 */
	ConcurrentController getConcurrentController() {
		return null;
	}

	final static UnsupportedOperationException noListProviderException(
			Class<?> resultClass, Object key1, Object key2, Object key3) {
		return buildKeysMessage("找不到列表结果提供器", resultClass, key1, key2, key3,
				null);
	}

	final static UnsupportedOperationException noResultProviderException(
			Class<?> resultClass, Object key1, Object key2, Object key3) {
		return buildKeysMessage("找不到结果提供器", resultClass, key1, key2, key3, null);
	}

	final static UnsupportedOperationException noTreeProviderException(
			Class<?> resultClass, Object key1, Object key2, Object key3) {
		return buildKeysMessage("找不到树结果提供器", resultClass, key1, key2, key3,
				null);
	}

	final static UnsupportedOperationException noResourceListException(
			Class<?> facadeClass, Object key1, Object key2, Object key3,
			Object[] keys) {
		return buildKeysMessage("找不到该资源的值列表", facadeClass, key1, key2, key3,
				keys);
	}

	final static UnsupportedOperationException noResourceException(
			Class<?> facadeClass, Object key1, Object key2, Object key3,
			Object[] otherKeys) {
		return buildKeysMessage("找不到资源", facadeClass, key1, key2, key3,
				otherKeys);
	}

	final static UnsupportedOperationException noResourceTreeException(
			Class<?> facadeClass, Object key1, Object key2, Object key3,
			Object[] keys) {
		return buildKeysMessage("找不到该资源的树", facadeClass, key1, key2, key3, keys);
	}

	/**
	 * 注册到位后的相关处理
	 */
	void afterRegInvokeeToSpace(ServiceInvokeeEntry to, Space space,
			ExceptionCatcher catcher) {
	}

	/**
	 * 发布模式,注册时指定
	 */
	Mode publishMode;
	@SuppressWarnings("unchecked")
	ServiceInvokeeBase next;

	/**
	 * 返回所属模块
	 * 
	 * @return
	 */
	abstract ServiceBase<?> getService();

	/**
	 * 比较是否匹配
	 */
	abstract boolean match(Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, int mask);

	/**
	 * 获取标底类型
	 * 
	 * @param typeArgFinder
	 *            模块信息
	 * @return 返回空代表忽略注册
	 */
	abstract Class<?> getTargetClass();

	TObject provide(TContext context) throws Throwable {
		throw new UnsupportedOperationException();
	}

	TObject provide(TContext context, TKey1 key1) throws Throwable {
		throw new UnsupportedOperationException();
	}

	TObject provide(TContext context, TKey1 key1, TKey2 key2) throws Throwable {
		throw new UnsupportedOperationException();
	}

	TObject provide(TContext context, TKey1 key1, TKey2 key2, TKey3 key3)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	void provide(TContext context, List<TObject> results) throws Throwable {
		throw new UnsupportedOperationException();
	}

	void provide(TContext context, TKey1 key1, List<TObject> results)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	void provide(TContext context, TKey1 key1, TKey2 key2, List<TObject> results)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	void provide(TContext context, TKey1 key1, TKey2 key2, TKey3 key3,
			List<TObject> results) throws Throwable {
		throw new UnsupportedOperationException();
	}

	int provide(TContext context, TreeNode<TObject> resultTreeNode)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	int provide(TContext context, TKey1 key1, TreeNode<TObject> resultTreeNode)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	int provide(TContext context, TKey1 key1, TKey2 key2,
			TreeNode<TObject> resultTreeNode) throws Throwable {
		throw new UnsupportedOperationException();
	}

	int provide(TContext context, TKey1 key1, TKey2 key2, TKey3 key3,
			TreeNode<TObject> resultTreeNode) throws Throwable {
		throw new UnsupportedOperationException();
	}

	void prepare(TContext context, TObject task) throws Throwable {
		throw new UnsupportedOperationException();
	}

	void handle(TContext context, TObject task) throws Throwable {
		throw new UnsupportedOperationException();
	}

	void occur(TContext context, TObject event) throws Throwable {
		throw new UnsupportedOperationException();
	}

	/**
	 * 向框架提议将会使用某些调用器<br>
	 * 框架将据此检查，并记录相关错误<br>
	 * 注意：重载该方法只是明确向框架说明自己的需求，但不代表不声明的调用器不使用。<br>
	 * 即，可以不声明
	 */
	protected void using(UsingDeclarator using) {
	}

	// ///////////////////////////////////////////////////////////////
	// /////// 各代理器(broker)使用
	// ///////////////////////////////////////////////////////////////
	/**
	 * 返回对应的资源管理器
	 */
	@SuppressWarnings("unchecked")
	ResourceServiceBase getResourceService() {
		throw new UnsupportedOperationException();
	}

	Space getSpace() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 返回更高级别空间下匹配的代理器
	 */
	ServiceInvokeeBase<TObject, TContext, TKey1, TKey2, TKey3> upperMatchBroker() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	final static ServiceInvokeeBase dummy = new ServiceInvokeeBase() {

		@Override
		ServiceBase getService() {
			throw new UnsupportedOperationException();
		}

		@Override
		Class getTargetClass() {
			throw new UnsupportedOperationException();
		}

		@Override
		boolean match(Class key1Class, Class key2Class, Class key3Class,
				int mask) {
			throw new UnsupportedOperationException();
		}

	};
}
