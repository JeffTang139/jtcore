/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteRequestStubBase.java
 * Date 2009-6-12
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class RemoteRequestStubBase implements RemoteRequestStub,
		ReturnReceivable {
	final int id;
	final NetConnection netConnection;
	volatile Throwable exception;

	final RemoteRequest<?> remoteRequest;

	RemoteRequestStubBase(NetConnection netConnection,
			RemoteRequest<?> remoteRequest) {
		if (netConnection == null) {
			throw new NullArgumentException("netConnection");
		}
		if (remoteRequest == null) {
			throw new NullArgumentException("remoteRequest");
		}
		this.netConnection = netConnection;
		this.id = netConnection.nextStubId();
		this.remoteRequest = remoteRequest;
	}

	public final int id() {
		return this.id;
	}

	/**
	 * 获取远程请求的数据包的代码。
	 * 
	 * @return 远程请求的数据包的代码。
	 */
	public final PacketCode requestPacketCode() {
		return this.remoteRequest.getPacketCode();
	}

	final void internalCheckException() {
		if (this.exception != null) {
			throw Utils.tryThrowException(this.exception);
		}
	}

	final void setLocalException(Throwable exception) {
		if (!(this.exception instanceof RemoteException)) {
			this.exception = exception;
		}
	}

	public final Throwable getException() {
		return this.exception;
	}

	public final boolean noException() {
		return this.exception == null;
	}

	public final void setRemoteException(ThrowableAdapter exception) {
		NetNodeInfo cni = this.netConnection.remoteNodeInfo;
		this.exception = new RemoteException(this.remoteRequest, cni
				.getAddress(), cni.getPort(), exception);
	}

	private volatile Thread runner;

	void send() {
		this.runner = Thread.currentThread();
		try {
			this.sendData();
		} catch (Throwable e) {
			this.exception = e;
			this.netConnection.nofityForceCancelHandler(this.id);
			throw Utils.tryThrowException(e);
		} finally {
			this.runner = null;
		}
	}

	abstract void sendData() throws Throwable;

	private final FinishableLinkedList<DataPacket> resultPackets = new FinishableLinkedList<DataPacket>();

	private final InterruptedException waitDataToFinished() {
		synchronized (this.resultPackets) {
			while (!this.resultPackets.finished()) {
				try {
					this.resultPackets.wait();
				} catch (InterruptedException e) {
					ConsoleLog.debugInfo("远程请求线程[%s]在等待远程返回的处理结果传输完毕时被中断：%s",
							Thread.currentThread().getName(), e);
					return e;
				}
			}
		}
		return null;
	}

	final void receiveReturn(DataPacket dataPacket) {
		Assertion.ASSERT(dataPacket.requestId == this.id);
		if (dataPacket.capacity() == 0) {
			this.resultPackets.finish();
			synchronized (this.resultPackets) {
				this.resultPackets.notify();
			}
			return;
		} else {
			Assertion.ASSERT(!this.resultPackets.finished());
		}
		synchronized (this.resultPackets) {
			this.resultPackets.add(dataPacket);
			this.resultPackets.notify();
		}
	}

	final void receiveException(DataPacket exceptionPacket) {
		this.receiveReturn(exceptionPacket); // XXX
	}

	final void syncWork() {
		if (this.exception != null) {
			throw Utils.tryThrowException(this.exception);
		}
		this.runner = Thread.currentThread();

		// receive data
		try {
			RemoteReturn rr = (RemoteReturn) RIUtil.parse(this.netConnection,
					this.resultPackets);
			rr.setReturn(this);
			// waiting here can save some time.
			if (this.waitDataToFinished() != null) // make sure all data is
			// received.
			{
				// 前面反序列化能够成功，说明有效数据是完整的，结束任务，尝试返回结果。
				this.resultPackets.finish();
			}
		} catch (Throwable e) {
			this.exception = e;
		}

		// release stub
		this.netConnection.releaseRemoteRequestStub(this);

		this.runner = null;
		if (this.exception != null) {
			throw Utils.tryThrowException(this.exception);
		}
	}

	final boolean canSyncWork() {
		return this.resultPackets.finished();
	}

	/**
	 * 通知任务失败。
	 * 
	 * @param exception
	 */
	final void fail(Throwable exception) {
		this.exception = exception;
		this.cancel();
	}

	final void cancel() {
		if (this.runner != null) {
			try {
				this.runner.interrupt();
			} catch (Throwable ignore) {
			}
		}
	}
}
