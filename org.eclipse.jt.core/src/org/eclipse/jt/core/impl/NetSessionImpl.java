package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Operation;
import org.eclipse.jt.core.invoke.Task;

/**
 * ����Ự��Ϊ��������紫������ȷ���ϲ�Ự�ĵײ�ӿڡ�
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
	 * ����һ��������
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
	 * �Ƿ��Ѿ��ر�
	 */
	public final boolean isClosed() {
		return this.closed;
	}

	/**
	 * �رջỰ��
	 */
	public synchronized void close() {
		if (!this.closed) {
			// TODO
			this.closed = true;
		}
	}
}
