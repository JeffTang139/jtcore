package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.invoke.Task;

final class NetTaskRequestImpl<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
		extends NetRequestImpl implements AsyncTask<TTask, TMethod> {
	@StructClass
	final static class RemoteTaskData<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> {
		final TTask task;
		final TMethod method;
		final int transactionID;

		RemoteTaskData(TTask task, TMethod method, int transactionID) {
			this.task = task;
			this.method = method;
			this.transactionID = transactionID;
		}
	}

	private final RemoteTaskData<TTask, TMethod> data;

	@Override
	public final Object getDataObject() {
		return this.data;
	}

	public final TMethod getMethod() {
		return this.data.method;
	}

	public final TTask getTask() throws IllegalStateException {
		this.checkFinished();
		return this.data.task;
	}

	NetTaskRequestImpl(NetSessionImpl session, TTask task, TMethod method,
			int transactionID) {
		super(session);
		this.data = new RemoteTaskData<TTask, TMethod>(task, method,
				transactionID);
	}

	NetTaskRequestImpl(NetSessionImpl session, TTask task, TMethod method) {
		this(session, task, method, 0);
	}
}