package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.invoke.ThreeKeyOverlappedResult;
import org.eclipse.jt.core.misc.MissingObjectException;

/**
 * 异步请求实现
 * 
 * @author Jeff Tang
 * 
 */
final class AsyncResultImpl<TResult, TKey1, TKey2, TKey3> extends
        AsyncServiceInvoke implements
        ThreeKeyOverlappedResult<TResult, TKey1, TKey2, TKey3> {
	AsyncResultImpl(
	        SessionImpl session,
	        SpaceNode occurAt,
	        Class<TResult> resultClass,
	        TKey1 key1,
	        TKey2 key2,
	        TKey3 key3,
	        ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> resultProvider) {
		super(session, occurAt);
		if (resultProvider == null || resultClass == null) {
			throw new NullPointerException();
		}
		this.resultClass = resultClass;
		this.key1 = key1;
		this.key2 = key2;
		this.key3 = key3;
		this.resultProvider = resultProvider;
		super.beginAsync();
	}

	private final Class<TResult> resultClass;
	private final TKey1 key1;
	private final TKey2 key2;
	private final TKey3 key3;
	private final ServiceInvokeeBase<TResult, Context, TKey1, TKey2, TKey3> resultProvider;
	private volatile TResult result;

	@Override
	protected final ConcurrentController getConcurrentController() {
		return this.resultProvider.getConcurrentController();
	}

	@Override
	protected final void workDoing(WorkingThread thread) {
		this.result = this.context.serviceProvideResult(this.resultProvider,
		        this.key1, this.key2, this.key3);
	}

	public final boolean isNull() throws IllegalStateException {
		this.checkFinished();
		return this.result == null;
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

	public final TResult getResult() throws IllegalStateException,
	        MissingObjectException {
		this.checkFinished();
		if (this.result == null) {
			throw new MissingObjectException("无法根据[" + this.key1 + ','
			        + this.key2 + ',' + this.key3 + "]请求到[" + this.resultClass
			        + "]");
		}
		return this.result;
	}

	public final Class<TResult> getResultClass() {
		return this.resultClass;
	}
}
