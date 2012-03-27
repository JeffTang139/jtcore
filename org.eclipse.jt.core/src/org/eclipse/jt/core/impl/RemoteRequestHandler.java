/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteRequestHandler.java
 * Date 2009-3-10
 */
package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.SessionKind;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.exception.SessionDisposedException;
import org.eclipse.jt.core.impl.NetConnection.State;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
final class RemoteRequestHandler extends Work {
	/**
	 * 当前运行的上下文
	 */
	protected ContextImpl<?, ?, ?> context;

	/**
	 * 开始异步处理
	 */
	protected final void beginAsync() {
		this.session.application.overlappedManager.postWork(this);
	}

	final NetConnection connection;
	final int id;

	RemoteRequestHandler(NetConnection connection, int requestId) {
		if (connection == null) {
			throw new NullArgumentException("connection");
		}
		this.connection = connection;
		this.id = requestId;

		ApplicationImpl app = connection.netManager.application;

		// TODO .tryLocateSpace(spacePath,spaceSeparator);
		this.occurAt = app.getDefaultSite();
		// TODO
		// long sessionID = 0;
		// SessionImpl session = sessionID == 0 ? null : app.sessionPool
		// .findLogin(sessionID);
		// if (session == null) {
		SessionImpl session = app.sessionManager.newSession(SessionKind.REMOTE,
				InternalUser.anonymUser, null, null);
		// }
		this.session = session;

	}

	public final int id() {
		return this.id;
	}

	private final FinishableLinkedList<DataPacket> data = new FinishableLinkedList<DataPacket>();

	private final void waitDataToFinished() throws InterruptedException {
		synchronized (this.data) {
			while (!this.data.finished()) {
				this.data.wait();
			}
		}
	}

	final void receiveRequestData(DataPacket dataPacket) {
		Assertion.ASSERT(dataPacket.requestId == this.id);
		if (dataPacket.capacity() == 0) {
			this.data.finish();
			synchronized (this.data) {
				this.data.notifyAll();
			}
			return;
		} else {
			Assertion.ASSERT(!this.data.finished());
		}
		synchronized (this.data) {
			this.data.add(dataPacket);
			this.data.notifyAll();
		}
	}

	/**
	 * 主线
	 */
	private final SessionImpl session;
	/**
	 * 调用的空间位置
	 */
	final SpaceNode occurAt;

	@Override
	protected final boolean workBeginning() {
		try {
			this.context = this.session.newContext(this.occurAt,
					ContextKind.TRANSIENT);
			return true;
		} catch (SessionDisposedException e) {
			return false;
		}
	}

	@Override
	protected final void workCanceling() {
		final ContextImpl<?, ?, ?> context = this.context;
		if (context != null) {
			context.cancel();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void workDoing(WorkingThread thread) throws Throwable {
		RemoteReturn result = null;
		RemoteRequest<?> rc;
		try {
			rc = (RemoteRequest) RIUtil.parse(this.connection, this.data);
			result = rc.execute(this.context);
			// waiting here can save some time.
			this.waitDataToFinished(); // make sure all data is received.
		} catch (Throwable e) {
			result = new ExceptionReturn(new ThrowableAdapter(e));
			this.connection.putFailureCominIds(this.id);
		}
		try {
			RIUtil.send(this.id, this.connection, result);
		} catch (Throwable e) {
			if (this.connection.state() == State.READY) {
				if (!(result instanceof ExceptionReturn)) {
					result = new ExceptionReturn(new ThrowableAdapter(e));
					try {
						RIUtil.send(this.id, this.connection, result);
					} catch (Throwable ignore) {
						this.connection.nofityForceCancelStub(this.id, e);
					}
				}
			}
			throw e;
		}
	}

	@Override
	protected void workFinalizing(Throwable e) {
		try {
			if (this.context != null) {
				this.context.dispose();
				this.context = null;
			}
		} finally {
			try {
				this.session.internalDispose(SessionImpl.IGNORE_IF_HAS_CONTEXT);
			} finally {
				this.connection.releaseRemoteRequestHandler(this);
			}
		}
	}
}
