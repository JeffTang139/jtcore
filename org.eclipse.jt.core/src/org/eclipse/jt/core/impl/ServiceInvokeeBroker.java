package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.service.UsingDeclarator;
import org.eclipse.jt.core.service.Publish.Mode;


/**
 * 调用器代理
 * 
 * @author Jeff Tang
 * 
 * @param <TObject>
 * @param <TContext>
 * @param <TKey1>
 * @param <TKey2>
 * @param <TKey3>
 */
final class ServiceInvokeeBroker<TObject, TContext, TKey1, TKey2, TKey3>
		extends ServiceInvokeeBase<TObject, TContext, TKey1, TKey2, TKey3> {
	private final ServiceInvokeeBase<TObject, TContext, TKey1, TKey2, TKey3> original;

	ServiceInvokeeBroker(
			ServiceInvokeeBase<TObject, TContext, TKey1, TKey2, TKey3> original,
			Mode publishMode) {
		this.original = original;
		this.publishMode = publishMode;
	}

	@Override
	final void afterRegInvokeeToSpace(ServiceInvokeeEntry to, Space space,
			ExceptionCatcher catcher) {
		this.original.afterRegInvokeeToSpace(to, space, catcher);
	}

	@Override
	final Space getSpace() {
		return this.original.getSpace();
	}

	@Override
	final ServiceBase<?> getService() {
		return this.original.getService();
	}

	@Override
	final Class<?> getTargetClass() {
		return this.original.getTargetClass();
	}

	@Override
	final boolean match(Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, int mask) {
		return this.original.match(key1Class, key2Class, key3Class, mask);
	}

	@SuppressWarnings("unchecked")
	@Override
	final ResourceServiceBase getResourceService() {
		return this.original.getResourceService();
	}

	@Override
	final TObject provide(TContext context) throws Throwable {
		return this.original.provide(context);
	}

	@Override
	final TObject provide(TContext context, TKey1 key1) throws Throwable {
		return this.original.provide(context, key1);
	}

	@Override
	final TObject provide(TContext context, TKey1 key1, TKey2 key2)
			throws Throwable {
		return this.original.provide(context, key1, key2);
	}

	@Override
	final TObject provide(TContext context, TKey1 key1, TKey2 key2, TKey3 key3)
			throws Throwable {
		return this.original.provide(context, key1, key2, key3);
	}

	@Override
	final void provide(TContext context, List<TObject> results)
			throws Throwable {
		this.original.provide(context, results);
	}

	@Override
	final void provide(TContext context, TKey1 key1, List<TObject> results)
			throws Throwable {
		this.original.provide(context, key1, results);
	}

	@Override
	final void provide(TContext context, TKey1 key1, TKey2 key2,
			List<TObject> results) throws Throwable {
		this.original.provide(context, key1, key2, results);
	}

	@Override
	final void provide(TContext context, TKey1 key1, TKey2 key2, TKey3 key3,
			List<TObject> results) throws Throwable {
		this.original.provide(context, key1, key2, key3, results);
	}

	@Override
	final void prepare(TContext context, TObject task) throws Throwable {
		this.original.prepare(context, task);
	}

	@Override
	final void handle(TContext context, TObject task) throws Throwable {
		this.original.handle(context, task);
	}

	@Override
	final void occur(TContext context, TObject event) throws Throwable {
		this.original.occur(context, event);
	}

	/**
	 * 向框架提议将会使用某些调用器<br>
	 * 框架将据此检查，并记录相关错误<br>
	 * 注意：重载该方法只是明确向框架说明自己的需求，但不代表不声明的调用器不使用。<br>
	 * 即，可以不声明
	 */
	@Override
	final protected void using(UsingDeclarator using) {
		this.original.using(using);
	}

	@Override
	final ServiceInvokeeBase<TObject, TContext, TKey1, TKey2, TKey3> upperMatchBroker() {
		return this.original.upperMatchBroker();
	}

	@Override
	final int provide(TContext context, TKey1 key1, TKey2 key2, TKey3 key3,
			TreeNode<TObject> resultTreeNode) throws Throwable {
		return this.original.provide(context, key1, key2, key3, resultTreeNode);
	}

	@Override
	final int provide(TContext context, TKey1 key1, TKey2 key2,
			TreeNode<TObject> resultTreeNode) throws Throwable {
		return this.original.provide(context, key1, key2, resultTreeNode);
	}

	@Override
	final int provide(TContext context, TKey1 key1,
			TreeNode<TObject> resultTreeNode) throws Throwable {
		return this.original.provide(context, key1, resultTreeNode);
	}

	@Override
	final int provide(TContext context, TreeNode<TObject> resultTreeNode)
			throws Throwable {
		return this.original.provide(context, resultTreeNode);
	}
}
