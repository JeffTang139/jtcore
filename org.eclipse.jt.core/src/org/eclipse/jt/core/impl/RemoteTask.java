/**
 * Copyright (C) 2007-2009 JeffTang Software Co., Ltd. All rights reserved.
 * 
 * File RemoteTask.java
 * Date 2009-3-13
 */
package org.eclipse.jt.core.impl;

import java.io.IOException;

import org.eclipse.jt.core.None;
import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;


/**
 * 远程任务。
 * 
 * @author Jeff Tang
 * @version 1.0
 */
@StructClass
abstract class RemoteTask implements RemoteRequest<RemoteTaskStubImpl> {

	/**
	 * 获取远程任务的数据包的代码。
	 * 
	 * @return 远程任务的数据包的代码。
	 */
	public final PacketCode getPacketCode() {
		return PacketCode.TASK_REQUEST;
	}

	public RemoteTaskStubImpl newStub(NetConnection netConnection) {
		if (netConnection == null) {
			throw new NullArgumentException("netConnnection");
		}
		return new RemoteTaskStubImpl(netConnection, this);
	}

	@SuppressWarnings("unchecked")
	abstract Task handleTask(ContextImpl<?, ?, ?> context);

	public RemoteReturn execute(ContextImpl<?, ?, ?> context) throws Throwable {
		return (new TaskReturn(this.handleTask(context)));
	}

	public void writeTo(StructuredObjectSerializer serializer)
	        throws IOException, StructDefineNotFoundException {
		serializer.writeDataOnly(this);
	}

	abstract Enum<?> getMethod();

	// ////////////////////////////

	static RemoteTask buildRemoteTask(SimpleTask simpleTask) {
		if (simpleTask == null) {
			throw new NullArgumentException("simpleTask");
		}
		return new SimpleRemoteTask(simpleTask);
	}

	static <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> RemoteTask buildRemoteTask(
	        TTask task, TMethod method) {
		if (task == null) {
			throw new NullArgumentException("task");
		}
		if (method == null) {
			throw new NullArgumentException("method");
		}
		return new CommonRemoteTask<TTask, TMethod>(task, method);
	}

	// ////////////////////////////

	@StructClass
	private static final class SimpleRemoteTask extends RemoteTask {
		final SimpleTask simpleTask;

		private SimpleRemoteTask(SimpleTask simpleTask) {
			this.simpleTask = simpleTask;
		}

		@Override
		final None getMethod() {
			return this.simpleTask.getMethod();
		}

		@SuppressWarnings("unchecked")
		@Override
		final Task handleTask(ContextImpl<?, ?, ?> context) {
			context.handle(this.simpleTask);
			return this.simpleTask;
		}
	}

	@StructClass
	private static final class CommonRemoteTask<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
	        extends RemoteTask {
		final TTask task;
		final TMethod method;

		private CommonRemoteTask(TTask task, TMethod method) {
			this.task = task;
			this.method = method;
		}

		@Override
		final TMethod getMethod() {
			return this.method;
		}

		@SuppressWarnings("unchecked")
		@Override
		final Task handleTask(ContextImpl<?, ?, ?> context) {
			context.handle(this.task, this.method);
			return this.task;
		}
	}
}
