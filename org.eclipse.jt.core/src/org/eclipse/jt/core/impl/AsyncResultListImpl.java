package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResultList;


/**
 * 异步列表请求实现
 * 
 * @author Jeff Tang
 * 
 */
final class AsyncResultListImpl<TResult, TKey1, TKey2, TKey3> extends
        AsyncServiceInvoke implements
        ThreeKeyOverlappedResultList<TResult, TKey1, TKey2, TKey3> {
	AsyncResultListImpl(
	        SessionImpl session,
	        SpaceNode occurAt,
	        Class<TResult> resultClass,
	        TKey1 key1,
	        TKey2 key2,
	        TKey3 key3,
	        ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> resultListProvider) {
		super(session, occurAt);
		if (resultListProvider == null || resultClass == null) {
			throw new NullPointerException();
		}
		this.resultClass = resultClass;
		this.key1 = key1;
		this.key2 = key2;
		this.key3 = key3;
		this.resultListProvider = resultListProvider;
		super.beginAsync();
	}

	private final Class<TResult> resultClass;
	private final TKey1 key1;
	private final TKey2 key2;
	private final TKey3 key3;
	private final ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> resultListProvider;
	private volatile List<TResult> resultList;

	@Override
	protected final ConcurrentController getConcurrentController() {
		return this.resultListProvider.getConcurrentController();
	}

	@Override
	protected final void workDoing(WorkingThread thread) {
		this.context.serviceProvideList(
		        this.resultList = new ArrayList<TResult>(),
		        this.resultListProvider, this.key1, this.key2, this.key3);
	}

	public final TKey1 getKey1() {
		return this.key1;
	}

	public final TKey2 getKey2() {
		return this.key2;
	}

	public final TKey3 getKey3() {
		return this.key3;
	}

	public final List<TResult> getResultList() throws IllegalStateException {
		this.checkFinished();
		return this.resultList;
	}

	public final Class<TResult> getResultClass() {
		return this.resultClass;
	}
}
