package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.invoke.Task;

/**
 * 网络会话，为两点间网络传输数据确定上层会话的底层接口。
 * 
 * @author Jeff Tang
 * 
 */
public class NetSessionImpl {
	final NetNodeImpl netNode;
	private long remoteSessionID;

	final long getRemoteSessionID() {
		return this.remoteSessionID;
	}

	private boolean closed;

	public NetSessionImpl(NetNodeImpl netNode) {
		this.netNode = netNode;
	}

	/**
	 * 创建一个新请求
	 */
	public final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> NetTaskRequestImpl<TTask, TMethod> newRequest(
			TTask task, TMethod method) {
		final NetTaskRequestImpl<TTask, TMethod> request = new NetTaskRequestImpl<TTask, TMethod>(
				this, task, method);
		request.startSendingRequest();
		return request;
	}

	final <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> NetTaskRequestImpl<TTask, TMethod> newRemoteTransactionRequest(
			TTask task, TMethod method, TransactionImpl transaction) {
		final NetTaskRequestImpl<TTask, TMethod> request = new NetTaskRequestImpl<TTask, TMethod>(
				this, task, method, transaction.id);
		request.startSendingRequest();
		return request;
	}

	public final <TResult, TKey1, TKey2, TKey3> NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> newRequest(
			byte resultType, Class<TResult> resultClass,
			Operation<? super TResult> operation, TKey1 key1, TKey2 key2,
			TKey3 key3, Object[] otherKeys) {
		final NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3> request = new NetQueryRequestImpl<TResult, TKey1, TKey2, TKey3>(
				this, resultType, resultClass, operation, key1, key2, key3,
				otherKeys);
		request.startSendingRequest();
		return request;
	}

	/**
	 * 是否已经关闭
	 */
	public final boolean isClosed() {
		return this.closed;
	}

	/**
	 * 关闭会话。
	 */
	public synchronized void close() {
		if (!this.closed) {
			// TODO
			this.closed = true;
		}
	}
}
