/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File ServiceInvokerImpl.java
 * Date 2009-4-7
 */
package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.Filter;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.TreeNodeFilter;
import org.eclipse.jt.core.exception.DeadLockException;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.AsyncHandle;
import org.eclipse.jt.core.invoke.AsyncResult;
import org.eclipse.jt.core.invoke.AsyncResultList;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.invoke.AsyncTreeNodeResult;
import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.invoke.MoreKeyOverlappedResult;
import org.eclipse.jt.core.invoke.MoreKeyOverlappedTreeNodeResult;
import org.eclipse.jt.core.invoke.OneKeyOverlappedResult;
import org.eclipse.jt.core.invoke.OneKeyOverlappedResultList;
import org.eclipse.jt.core.invoke.OneKeyOverlappedTreeNodeResult;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResult;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResultList;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedTreeNodeResult;
import org.eclipse.jt.core.invoke.TwoKeyOverlappedResult;
import org.eclipse.jt.core.invoke.TwoKeyOverlappedResultList;
import org.eclipse.jt.core.invoke.TwoKeyOverlappedTreeNodeResult;
import org.eclipse.jt.core.misc.MissingObjectException;
import org.eclipse.jt.core.misc.SortUtil;
import org.eclipse.jt.core.service.AsyncInfo;


/**
 * 基于远程调用实现的服务调用器。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteServiceInvokerImpl implements ServiceInvokerExtend {
	public final boolean isValid() {
		return this.context.isValid();
	}

	public final void checkValid() {
		this.context.checkValid();
	}

	// unsupport resource category.

	final Context context;
	final RemoteLoginInfoImpl remoteLoginInfo;

	RemoteServiceInvokerImpl(Context context,
			RemoteLoginInfoImpl remoteLoginInfo) {
		if (context == null) {
			throw new NullArgumentException("context");
		}
		if (remoteLoginInfo == null) {
			throw new NullArgumentException("remoteLoginInfo");
		}

		this.context = context;
		this.remoteLoginInfo = remoteLoginInfo;

		// make sure connected.
		this.getConnection();
	}

	private NetConnection getConnection() {
		NetConnection nc = this.remoteLoginInfo.getConnection();
		if (nc == null || !nc.isConnected()) {
			throw new UnsupportedOperationException("无法建立连接");
		}
		return nc;
	}

	private final <TResult, TKey> void checkArgsNotNull(
			Class<TResult> resultClass) throws NullArgumentException {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		this.context.checkValid();
	}

	private final <TResult, TKey> void checkArgsNotNull(
			Class<TResult> resultClass, TKey key) throws NullArgumentException {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key == null) {
			throw new NullArgumentException("key");
		}
		this.context.checkValid();
	}

	private final <TResult, TKey1, TKey2> void checkArgsNotNull(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2)
			throws NullArgumentException {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		this.context.checkValid();
	}

	private final <TResult, TKey1, TKey2, TKey3> void checkArgsNotNull(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3)
			throws NullArgumentException {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		this.context.checkValid();
	}

	private final <TResult, TKey1, TKey2, TKey3> void checkArgsNotNull(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3,
			Object... otherKeys) throws NullArgumentException {
		if (resultClass == null) {
			throw new NullArgumentException("resultClass");
		}
		if (key1 == null) {
			throw new NullArgumentException("key1");
		}
		if (key2 == null) {
			throw new NullArgumentException("key2");
		}
		if (key3 == null) {
			throw new NullArgumentException("key3");
		}
		if (otherKeys == null || otherKeys.length == 0) {
			throw new NullArgumentException("otherkeys");
		}
		this.context.checkValid();
	}

	private <TResult> RemoteOverlappedResult<TResult, None, None, None> internalAsyncGet(
			Class<TResult> resultClass) {
		RemoteQuery query = RemoteQuery.buildQuery(resultClass);
		RemoteQueryStubImpl queryStub = this.getConnection().postRequest(query);
		return (new RemoteOverlappedResult<TResult, None, None, None>(query,
				queryStub));
	}

	private <TResult, TKey> RemoteOverlappedResult<TResult, TKey, None, None> internalAsyncGet(
			Class<TResult> resultClass, TKey key) {
		RemoteQuery query = RemoteQuery.buildQuery(resultClass, key);
		RemoteQueryStubImpl queryStub = this.getConnection().postRequest(query);
		return (new RemoteOverlappedResult<TResult, TKey, None, None>(query,
				queryStub));
	}

	private <TResult, TKey1, TKey2> RemoteOverlappedResult<TResult, TKey1, TKey2, None> internalAsyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2) {
		RemoteQuery query = RemoteQuery.buildQuery(resultClass, key, key2);
		RemoteQueryStubImpl queryStub = this.getConnection().postRequest(query);
		return (new RemoteOverlappedResult<TResult, TKey1, TKey2, None>(query,
				queryStub));
	}

	private <TResult, TKey1, TKey2, TKey3> RemoteOverlappedResult<TResult, TKey1, TKey2, TKey3> internalAsyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2, TKey3 key3) {
		RemoteQuery query = RemoteQuery
				.buildQuery(resultClass, key, key2, key3);
		RemoteQueryStubImpl queryStub = this.getConnection().postRequest(query);
		return (new RemoteOverlappedResult<TResult, TKey1, TKey2, TKey3>(query,
				queryStub));
	}

	private <TResult, TKey1, TKey2, TKey3> RemoteOverlappedResult<TResult, TKey1, TKey2, TKey3> internalAsyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2, TKey3 key3,
			Object[] otherKeys) {
		RemoteQuery query = RemoteQuery.buildQuery(resultClass, key, key2,
				key3, otherKeys);
		RemoteQueryStubImpl queryStub = this.getConnection().postRequest(query);
		return (new RemoteOverlappedResult<TResult, TKey1, TKey2, TKey3>(query,
				queryStub));
	}

	public <TResult> AsyncResult<TResult> asyncGet(Class<TResult> resultClass) {
		this.checkArgsNotNull(resultClass);
		return this.internalAsyncGet(resultClass);
	}

	public <TResult, TKey> OneKeyOverlappedResult<TResult, TKey> asyncGet(
			Class<TResult> resultClass, TKey key) {
		this.checkArgsNotNull(resultClass, key);
		return this.internalAsyncGet(resultClass, key);
	}

	public <TResult, TKey1, TKey2> TwoKeyOverlappedResult<TResult, TKey1, TKey2> asyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2) {
		this.checkArgsNotNull(resultClass, key, key2);
		return this.internalAsyncGet(resultClass, key, key2);
	}

	public <TResult, TKey1, TKey2, TKey3> ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> asyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2, TKey3 key3) {
		this.checkArgsNotNull(resultClass, key, key2, key3);
		return this.internalAsyncGet(resultClass, key, key2, key3);
	}

	public <TResult, TKey1, TKey2, TKey3> MoreKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> asyncGet(
			Class<TResult> resultClass, TKey1 key, TKey2 key2, TKey3 key3,
			Object... otherKeys) {
		this.checkArgsNotNull(resultClass, key, key2, key3, otherKeys);
		return this.internalAsyncGet(resultClass, key, key2, key3, otherKeys);
	}

	// /////////////////////////////

	private <TResult> RemoteOverlappedResultList<TResult, None, None, None> internalAsyncGetList(
			Class<TResult> resultClass) {
		RemoteListQuery query = RemoteListQuery.buildListQuery(resultClass);
		RemoteListQueryStubImpl queryStub = this.getConnection().postRequest(
				query);
		return (new RemoteOverlappedResultList<TResult, None, None, None>(
				query, queryStub));
	}

	private <TResult, TKey1> RemoteOverlappedResultList<TResult, TKey1, None, None> internalAsyncGetList(
			Class<TResult> resultClass, TKey1 key1) {
		RemoteListQuery query = RemoteListQuery.buildListQuery(resultClass,
				key1);
		RemoteListQueryStubImpl queryStub = this.getConnection().postRequest(
				query);
		return (new RemoteOverlappedResultList<TResult, TKey1, None, None>(
				query, queryStub));
	}

	private <TResult, TKey1, TKey2> RemoteOverlappedResultList<TResult, TKey1, TKey2, None> internalAsyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2) {
		RemoteListQuery query = RemoteListQuery.buildListQuery(resultClass,
				key1, key2);
		RemoteListQueryStubImpl queryStub = this.getConnection().postRequest(
				query);
		return (new RemoteOverlappedResultList<TResult, TKey1, TKey2, None>(
				query, queryStub));
	}

	private <TResult, TKey1, TKey2, TKey3> RemoteOverlappedResultList<TResult, TKey1, TKey2, TKey3> internalAsyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		RemoteListQuery query = RemoteListQuery.buildListQuery(resultClass,
				key1, key2, key3);
		RemoteListQueryStubImpl queryStub = this.getConnection().postRequest(
				query);
		return (new RemoteOverlappedResultList<TResult, TKey1, TKey2, TKey3>(
				query, queryStub));
	}

	private <TResult, TKey1, TKey2, TKey3> RemoteOverlappedResultList<TResult, TKey1, TKey2, TKey3> internalAsyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3,
			Object[] otherKeys) {
		RemoteListQuery query = RemoteListQuery.buildListQuery(resultClass,
				key1, key2, key3, otherKeys);
		RemoteListQueryStubImpl queryStub = this.getConnection().postRequest(
				query);
		return (new RemoteOverlappedResultList<TResult, TKey1, TKey2, TKey3>(
				query, queryStub));
	}

	public <TResult> AsyncResultList<TResult> asyncGetList(
			Class<TResult> resultClass) {
		this.checkArgsNotNull(resultClass);
		return this.internalAsyncGetList(resultClass);
	}

	public <TResult, TKey1> OneKeyOverlappedResultList<TResult, TKey1> asyncGetList(
			Class<TResult> resultClass, TKey1 key1) {
		this.checkArgsNotNull(resultClass, key1);
		return this.internalAsyncGetList(resultClass, key1);
	}

	public <TResult, TKey1, TKey2> TwoKeyOverlappedResultList<TResult, TKey1, TKey2> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2) {
		this.checkArgsNotNull(resultClass, key1, key2);
		return this.internalAsyncGetList(resultClass, key1, key2);
	}

	public <TResult, TKey1, TKey2, TKey3> ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		this.checkArgsNotNull(resultClass, key1, key2, key3);
		return this.internalAsyncGetList(resultClass, key1, key2, key3);
	}

	public <TResult, TKey1, TKey2, TKey3> ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> asyncGetList(
			Class<TResult> resultClass, TKey1 key1, TKey2 key2, TKey3 key3,
			Object... otherKeys) {
		this.checkArgsNotNull(resultClass, key1, key2, key3, otherKeys);
		return this.internalAsyncGetList(resultClass, key1, key2, key3,
				otherKeys);
	}

	// /////////////////////////////

	private <TFacade> RemoteOverlappedTreeNodeResult<TFacade, None, None, None> internalAsyncGetTreeNode(
			Class<TFacade> facadeClass) {
		RemoteTreeNodeQuery query = RemoteTreeNodeQuery
				.buildTreeNodeQuery(facadeClass);
		RemoteTreeNodeQueryStubImpl queryStub = this.getConnection()
				.postRequest(query);
		return (new RemoteOverlappedTreeNodeResult<TFacade, None, None, None>(
				query, queryStub));
	}

	private <TFacade, TKey> RemoteOverlappedTreeNodeResult<TFacade, TKey, None, None> internalAsyncGetTreeNode(
			Class<TFacade> facadeClass, TKey key) {
		RemoteTreeNodeQuery query = RemoteTreeNodeQuery.buildTreeNodeQuery(
				facadeClass, key);
		RemoteTreeNodeQueryStubImpl queryStub = this.getConnection()
				.postRequest(query);
		return (new RemoteOverlappedTreeNodeResult<TFacade, TKey, None, None>(
				query, queryStub));
	}

	private <TFacade, TKey1, TKey2> RemoteOverlappedTreeNodeResult<TFacade, TKey1, TKey2, None> internalAsyncGetTreeNode(
			Class<TFacade> facadeClass, TKey1 key1, TKey2 key2) {
		RemoteTreeNodeQuery query = RemoteTreeNodeQuery.buildTreeNodeQuery(
				facadeClass, key1, key2);
		RemoteTreeNodeQueryStubImpl queryStub = this.getConnection()
				.postRequest(query);
		return (new RemoteOverlappedTreeNodeResult<TFacade, TKey1, TKey2, None>(
				query, queryStub));
	}

	private <TFacade, TKey1, TKey2, TKey3> RemoteOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3> internalAsyncGetTreeNode(
			Class<TFacade> facadeClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		RemoteTreeNodeQuery query = RemoteTreeNodeQuery.buildTreeNodeQuery(
				facadeClass, key1, key2, key3);
		RemoteTreeNodeQueryStubImpl queryStub = this.getConnection()
				.postRequest(query);
		return (new RemoteOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3>(
				query, queryStub));
	}

	private <TFacade, TKey1, TKey2, TKey3> RemoteOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3> internalAsyncGetTreeNode(
			Class<TFacade> facadeClass, TKey1 key1, TKey2 key2, TKey3 key3,
			Object[] otherKeys) {
		RemoteTreeNodeQuery query = RemoteTreeNodeQuery.buildTreeNodeQuery(
				facadeClass, key1, key2, key3, otherKeys);
		RemoteTreeNodeQueryStubImpl queryStub = this.getConnection()
				.postRequest(query);
		return (new RemoteOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3>(
				query, queryStub));
	}

	public <TFacade> AsyncTreeNodeResult<TFacade> asyncGetTreeNode(
			Class<TFacade> facadeClass) {
		this.checkArgsNotNull(facadeClass);
		return this.internalAsyncGetTreeNode(facadeClass);
	}

	public <TFacade, TKey> OneKeyOverlappedTreeNodeResult<TFacade, TKey> asyncGetTreeNode(
			Class<TFacade> facadeClass, TKey key) {
		this.checkArgsNotNull(facadeClass, key);
		return this.internalAsyncGetTreeNode(facadeClass, key);
	}

	public <TFacade, TKey1, TKey2> TwoKeyOverlappedTreeNodeResult<TFacade, TKey1, TKey2> asyncGetTreeNode(
			Class<TFacade> facadeClass, TKey1 key1, TKey2 key2) {
		this.checkArgsNotNull(facadeClass, key1, key2);
		return this.internalAsyncGetTreeNode(facadeClass, key1, key2);
	}

	public <TFacade, TKey1, TKey2, TKey3> ThreeKeyOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3> asyncGetTreeNode(
			Class<TFacade> facadeClass, TKey1 key1, TKey2 key2, TKey3 key3) {
		this.checkArgsNotNull(facadeClass, key1, key2, key3);
		return this.internalAsyncGetTreeNode(facadeClass, key1, key2, key3);
	}

	public <TFacade, TKey1, TKey2, TKey3> MoreKeyOverlappedTreeNodeResult<TFacade, TKey1, TKey2, TKey3> asyncGetTreeNode(
			Class<TFacade> facadeClass, TKey1 key1, TKey2 key2, TKey3 key3,
			Object... otherKeys) {
		this.checkArgsNotNull(facadeClass, key1, key2, key3, otherKeys);
		return this.internalAsyncGetTreeNode(facadeClass, key1, key2, key3,
				otherKeys);
	}

	// /////////////////////////////

	private <TSimpleTask extends SimpleTask> RemoteAsyncTask<TSimpleTask, None> internalAsyncHandle(
			TSimpleTask task) {
		this.context.checkValid();
		RemoteTask remoteTask = RemoteTask.buildRemoteTask(task);
		RemoteTaskStubImpl stub = this.getConnection().postRequest(remoteTask);
		return (new RemoteAsyncTask<TSimpleTask, None>(remoteTask, stub));
	}

	private <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> RemoteAsyncTask<TTask, TMethod> internalAsyncHandle(
			TTask task, TMethod method) {
		this.context.checkValid();
		RemoteTask remoteTask = RemoteTask.buildRemoteTask(task, method);
		RemoteTaskStubImpl stub = this.getConnection().postRequest(remoteTask);
		return (new RemoteAsyncTask<TTask, TMethod>(remoteTask, stub));
	}

	public <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task, AsyncInfo info) {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		if (info == null) {
			throw new NullArgumentException("info");
		}

		// TODO 实现
		throw new UnsupportedOperationException("尚未实现");
	}

	public <TSimpleTask extends SimpleTask> AsyncTask<TSimpleTask, None> asyncHandle(
			TSimpleTask task) {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		return this.internalAsyncHandle(task);
	}

	public <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method, AsyncInfo info) {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		if (method == null) {
			throw new NullArgumentException("method");
		}
		if (info == null) {
			throw new NullArgumentException("info");
		}

		// TODO 实现
		throw new UnsupportedOperationException("尚未实现");
	}

	public <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> AsyncTask<TTask, TMethod> asyncHandle(
			TTask task, TMethod method) {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		if (method == null) {
			throw new NullArgumentException("method");
		}
		return this.internalAsyncHandle(task, method);
	}

	private RemoteAsyncEvent asyncFire(Event event, boolean wait) {
		this.context.checkValid();
		RemoteEvent remoteEvent = new RemoteEvent(event, wait);
		RemoteEventStubImpl stub = this.getConnection()
				.postRequest(remoteEvent);
		return (new RemoteAsyncEvent(remoteEvent, stub));
	}

	public boolean dispatch(Event event) {
		if (event == null) {
			throw new NullArgumentException("event");
		}
		RemoteAsyncEvent ae = this.asyncFire(event, true);
		ae.waitToFinish();
		if (!ae.noException()) {
			throw Utils.tryThrowException(ae.getException());
		}
		// FIXME 返回远程是否有相应
		return true;
	}

	public float getResistance() {
		return 1; // XXX
	}

	public void handle(SimpleTask task) throws DeadLockException {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		RemoteAsyncTask<SimpleTask, None> asyncTask = this
				.internalAsyncHandle(task);
		asyncTask.waitToFinish();
		if (asyncTask.noException()) {
			SimpleTask returnedTask = asyncTask.getTask();
			// XXX copy values from returnedTask to task.
			StructDefineImpl.getStructDefine(task).assignNoCheckSrc(
					returnedTask, task, new OBJAContext());
		} else {
			throw Utils.tryThrowException(asyncTask.getException());
		}
	}

	public <TMethod extends Enum<TMethod>> void handle(Task<TMethod> task,
			TMethod method) throws DeadLockException {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		if (method == null) {
			throw new NullArgumentException("method");
		}
		RemoteAsyncTask<Task<TMethod>, TMethod> asyncTask = this
				.internalAsyncHandle(task, method);
		asyncTask.waitToFinish();
		if (asyncTask.noException()) {
			Task<?> returnedTask = asyncTask.getTask();
			// XXX copy values from returnedTask to task.
			StructDefineImpl.getStructDefine(task).assignNoCheckSrc(
					returnedTask, task, new OBJAContext());
		} else {
			throw Utils.tryThrowException(asyncTask.getException());
		}
	}

	public AsyncHandle occur(Event event) {
		if (event == null) {
			throw new NullArgumentException("event");
		}
		RemoteAsyncEvent ae = this.asyncFire(event, false);
		ae.waitToFinish();
		if (!ae.noException()) {
			throw Utils.tryThrowException(ae.getException());
		}
		return ae;
	}

	public void waitFor(AsyncHandle one, AsyncHandle... others)
			throws InterruptedException {
		this.context.waitFor(one, others);
	}

	public void waitFor(long timeout, AsyncHandle one, AsyncHandle... others)
			throws InterruptedException {
		this.context.waitFor(timeout, one, others);
	}

	// /////////////////////////////

	private static <TFacade> TreeNode<TFacade> waitGetTreeNode(
			RemoteOverlappedTreeNodeResult<TFacade, ?, ?, ?> treeNodeResult) {
		treeNodeResult.waitToFinish();
		if (treeNodeResult.noException()) {
			return treeNodeResult.getTreeNode();
		} else {
			throw Utils.tryThrowException(treeNodeResult.getException());
		}
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key1, key2, key3, otherKeys);
		return waitGetTreeNode(this.internalAsyncGetTreeNode(facadeClass, key1,
				key2, key3, otherKeys));
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key1, key2, key3);
		return waitGetTreeNode(this.internalAsyncGetTreeNode(facadeClass, key1,
				key2, key3));
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key1, key2);
		return waitGetTreeNode(this.internalAsyncGetTreeNode(facadeClass, key1,
				key2));
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key);
		return waitGetTreeNode(this.internalAsyncGetTreeNode(facadeClass, key));
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass);
		return waitGetTreeNode(this.internalAsyncGetTreeNode(facadeClass));
	}

	// XXX 本地过滤，会带来性能上的降低。
	private static <TData> TreeNode<TData> filterAndSortRecursively(
			TreeNode<TData> treeNode, TreeNodeFilter<? super TData> filter,
			Comparator<? super TData> sortComparator) {
		if (treeNode instanceof TreeNodeImpl<?>) {
			TreeNodeImpl<TData> root = (TreeNodeImpl<TData>) treeNode;
			int absoluteLevel;
			if (root instanceof TreeNodeRoot<?>) {
				absoluteLevel = ((TreeNodeRoot<TData>) root).getAbsoluteLevel();
			} else {
				absoluteLevel = 0;
			}
			root.filterAndSortRecursively(filter, absoluteLevel, 0,
					sortComparator);
			return root;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key1,
				key2, key3, otherKeys), null, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key1,
				key2, key3), null, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key1,
				key2), null, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key),
				null, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass), null,
				sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key1,
				key2, key3, otherKeys), filter, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key1,
				key2, key3), filter, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key1,
				key2), filter, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key),
				filter, null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass), filter,
				null);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key1,
				key2, key3, otherKeys), filter, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key1,
				key2, key3), filter, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key1,
				key2), filter, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass, key),
				filter, sortComparator);
	}

	public <TFacade> TreeNode<TFacade> getTreeNode(Class<TFacade> facadeClass,
			TreeNodeFilter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		return filterAndSortRecursively(this.getTreeNode(facadeClass), filter,
				sortComparator);
	}

	// /////////////////////////////

	private static <TFacade> TFacade waitFind(
			RemoteOverlappedResult<TFacade, ?, ?, ?> result) {
		result.waitToFinish();
		if (result.noException()) {
			return result.getResult();
		} else {
			throw Utils.tryThrowException(result.getException());
		}
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key1, key2, key3, keys);
		return waitFind(this.internalAsyncGet(facadeClass, key1, key2, key3,
				keys));
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key1, key2, key3);
		return waitFind(this.internalAsyncGet(facadeClass, key1, key2, key3));
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key1, key2);
		return waitFind(this.internalAsyncGet(facadeClass, key1, key2));
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key);
		return waitFind(this.internalAsyncGet(facadeClass, key));
	}

	public <TFacade> TFacade find(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass);
		return waitFind(this.internalAsyncGet(facadeClass));
	}

	private static <TFacade> TFacade waitGet(
			RemoteOverlappedResult<TFacade, ?, ?, ?> result) {
		TFacade facade = waitFind(result);
		if (facade == null) {
			throw new MissingObjectException();
		}
		return facade;
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3, Object... keys)
			throws UnsupportedOperationException, MissingObjectException {
		this.checkArgsNotNull(facadeClass, key1, key2, key3, keys);
		return waitGet(this.internalAsyncGet(facadeClass, key1, key2, key3,
				keys));
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException,
			MissingObjectException {
		this.checkArgsNotNull(facadeClass, key1, key2, key3);
		return waitGet(this.internalAsyncGet(facadeClass, key1, key2, key3));
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key1,
			Object key2) throws UnsupportedOperationException,
			MissingObjectException {
		this.checkArgsNotNull(facadeClass, key1, key2);
		return waitGet(this.internalAsyncGet(facadeClass, key1, key2));
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass, Object key)
			throws UnsupportedOperationException, MissingObjectException {
		this.checkArgsNotNull(facadeClass, key);
		return waitGet(this.internalAsyncGet(facadeClass, key));
	}

	public <TFacade> TFacade get(Class<TFacade> facadeClass)
			throws UnsupportedOperationException, MissingObjectException {
		this.checkArgsNotNull(facadeClass);
		return waitGet(this.internalAsyncGet(facadeClass));
	}

	// /////////////////////////////////

	// XXX 本地过滤，会带来性能上的降低。
	private static <TFacade> List<TFacade> filter(List<TFacade> list,
			Filter<? super TFacade> filter) {
		ArrayList<TFacade> filtered = new ArrayList<TFacade>(list.size());
		for (TFacade facade : list) {
			if (filter.accept(facade)) {
				filtered.add(facade);
			}
		}
		if (filtered.size() < (list.size() * 0.5)) {
			filtered.trimToSize();
		}
		return filtered;
	}

	private static <TFacade> List<TFacade> sort(List<TFacade> list,
			Comparator<? super TFacade> sortComparator) {
		SortUtil.sort(list, sortComparator);
		return list;
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3, Object... otherKeys) {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return filter(this.getList(facadeClass, key1, key2, key3, otherKeys),
				filter);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2,
			Object key3) throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return filter(this.getList(facadeClass, key1, key2, key3), filter);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return filter(this.getList(facadeClass, key1, key2), filter);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter, Object key)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return filter(this.getList(facadeClass, key), filter);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		return filter(this.getList(facadeClass), filter);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return sort(filter(this.getList(facadeClass, key1, key2, key3,
				otherKeys), filter), sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return sort(
				filter(this.getList(facadeClass, key1, key2, key3), filter),
				sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return sort(filter(this.getList(facadeClass, key1, key2), filter),
				sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return sort(filter(this.getList(facadeClass, key), filter),
				sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Filter<? super TFacade> filter,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (filter == null) {
			throw new NullArgumentException("filter");
		}
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return sort(filter(this.getList(facadeClass), filter), sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3, Object... otherKeys) {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return sort(this.getList(facadeClass, key1, key2, key3, otherKeys),
				sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1,
			Object key2, Object key3) throws UnsupportedOperationException {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return sort(this.getList(facadeClass, key1, key2, key3), sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key1, Object key2)
			throws UnsupportedOperationException {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return sort(this.getList(facadeClass, key1, key2), sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator, Object key)
			throws UnsupportedOperationException {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return sort(this.getList(facadeClass, key), sortComparator);
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Comparator<? super TFacade> sortComparator)
			throws UnsupportedOperationException {
		if (sortComparator == null) {
			throw new NullArgumentException("sortComparator");
		}
		return sort(this.getList(facadeClass), sortComparator);
	}

	private static <TFacade> List<TFacade> waitGetList(
			RemoteOverlappedResultList<TFacade, ?, ?, ?> resultList) {
		resultList.waitToFinish();
		if (resultList.noException()) {
			return resultList.getResultList();
		} else {
			throw Utils.tryThrowException(resultList.getException());
		}
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3, Object... otherKeys) {
		this.checkArgsNotNull(facadeClass, key1, key2, key3, otherKeys);
		return waitGetList(this.internalAsyncGetList(facadeClass, key1, key2,
				key3, otherKeys));
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2, Object key3)
			throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key1, key2, key3);
		return waitGetList(this.internalAsyncGetList(facadeClass, key1, key2,
				key3));
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key1, Object key2) throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key1, key2);
		return waitGetList(this.internalAsyncGetList(facadeClass, key1, key2));
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass,
			Object key) throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass, key);
		return waitGetList(this.internalAsyncGetList(facadeClass, key));
	}

	public <TFacade> List<TFacade> getList(Class<TFacade> facadeClass)
			throws UnsupportedOperationException {
		this.checkArgsNotNull(facadeClass);
		return waitGetList(this.internalAsyncGetList(facadeClass));
	}
}
