/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File AbstractAsyncHandle.java
 * Date 2009-4-7
 */
package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.info.Info;
import org.eclipse.jt.core.invoke.AsyncHandle;
import org.eclipse.jt.core.invoke.AsyncState;


/**
 * 远程异步处理句柄。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
abstract class RemoteAsyncHandle implements AsyncHandle, Waitable {
	final RemoteRequestStubImpl remoteRequestStub;

	public int fetchInfos(List<Info> to) {
		return 0;
		// TODO
	};

	RemoteAsyncHandle(RemoteRequestStubImpl remoteRequestStub) {
		if (remoteRequestStub == null) {
			throw new NullArgumentException("remoteRequestStub");
		}
		this.remoteRequestStub = remoteRequestStub;
		this.state = AsyncState.PROCESSING;
	}

	// /////////////////////////

	/**
	 * 等待远程处理执行结束。
	 */
	final void waitToFinish() {
		boolean error = false;
		try {
			this.remoteRequestStub.syncWork();
		} catch (Throwable e) {
			error = true;
			throw Utils.tryThrowException(e);
		} finally {
			synchronized (this.forWAIT) {
				this.forWAIT.notifyAll();
				switch (this.state) {
				case CANCELING:
					this.state = AsyncState.CANCELED;
					if (error) {
						this.tempProgress = -0.5f;
					}
					break;
				case PROCESSING:
					if (error) {
						this.state = AsyncState.ERROR;
						this.tempProgress = -1;
					} else {
						this.state = AsyncState.FINISHED;
						this.tempProgress = 1;
					}
					break;
				}
			}
		}
	}

	final boolean noException() {
		return this.remoteRequestStub.noException();
	}

	final void internalCheckStateForResultOK() {
		// TODO check state
	}

	// /////////////////////////

	public void cancel() {
		this.tryFinish();
		synchronized (this.forWAIT) {
			switch (this.state) {
			case CANCELED:
			case CANCELING:
			case ERROR:
			case FINISHED:
				return;
			}
			this.state = AsyncState.CANCELING;
		}
		// XXX
		this.remoteRequestStub.cancel();
	}

	public Throwable getException() {
		return this.remoteRequestStub.getException();
	}

	private float tempProgress = 0; // XXX

	private volatile AsyncState state;

	public float getProgress() {
		this.tryFinish();
		return this.tempProgress;
	}

	private void tryFinish() {
		synchronized (this.forWAIT) {
			if (!this.remoteRequestStub.canSyncWork()) {
				return;
			}
		}
		this.waitToFinish();
	}

	public AsyncState getState() {
		this.tryFinish();
		return this.state;
	}

	// /////////////////////////////

	private final Object forWAIT = new Object();

	public void waitStop(long timeout) throws InterruptedException {
		synchronized (this.forWAIT) {
			this.wait(timeout);
		}
	}
}
