package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.None;
import org.eclipse.jt.core.SessionKind;
import org.eclipse.jt.core.User;
import org.eclipse.jt.core.invoke.AsyncTask;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.service.AsyncInfo.SessionMode;

/**
 * 异步调用任务接口的实现
 * 
 * @author Jeff Tang
 * 
 * @param <TTask>
 * @param <TMethod>
 */
final class AsyncTaskImpl<TTask extends Task<TMethod>, TMethod extends Enum<TMethod>>
		extends AsyncServiceInvoke implements AsyncTask<TTask, TMethod> {
	private static SessionImpl sessionOf(SessionImpl session,
			InternalAsyncInfo asyncInfo) {
		return asyncInfo != null && asyncInfo.sessionMode != SessionMode.SAME ? null
				: session;
	}

	AsyncTaskImpl(SessionImpl session, SpaceNode occurAt, TTask task,
			ServiceInvokeeBase<TTask, Context, None, None, None> handler,
			InternalAsyncInfo asyncInfo) {
		super(sessionOf(session, asyncInfo), occurAt);
		this.task = task;
		this.handler = handler;
		if (asyncInfo != null) {
			this.startime = asyncInfo.start;
			this.period = asyncInfo.period;
			switch (asyncInfo.sessionMode) {
			case INDIVIDUAL:
				final User user = session.getUser();
				if (user != InternalUser.system) {
					this.individualSessionUser = user;
					break;
				}// 系统用户切换时换成匿名用户
			case INDIVIDUAL_ANONYMOUS:
				this.individualSessionUser = InternalUser.anonymUser;
				break;
			default:
				this.individualSessionUser = null;
			}
			if (this.individualSessionUser != null) {
				occurAt.site.state.checkSessionKind(SessionKind.TRANSIENT);
			}
		} else {
			this.individualSessionUser = null;
		}
		super.beginAsync();
	}

	/**
	 * 周期
	 */
	protected long period;
	/**
	 * 开始时间
	 */
	protected long startime;
	/**
	 * 做为独立会话时的用户
	 */
	private final User individualSessionUser;

	@Override
	protected final boolean workBeginning() {
		final User individualSessionUser = this.individualSessionUser;
		if (individualSessionUser != null) {
			final Site site = this.occurAt.site;
			site.state.checkSessionKind(SessionKind.TRANSIENT);
			this.session = site.application.sessionManager.newSession(
					SessionKind.TRANSIENT, individualSessionUser, null, null);
		}
		return super.workBeginning();
	}

	@Override
	protected final void workFinalizing(Throwable e) {
		super.workFinalizing(e);
		if (this.session != null) {
			switch (this.session.kind) {
			case TRANSIENT:
			case REMOTE:
				this.session.internalDispose(SessionImpl.IGNORE_IF_HAS_CONTEXT);
				break;
			default:
				if (this.individualSessionUser != null) {
					this.session.internalDispose(0l);
				} else {
					return;// 没有被dispose的Session之后还要使用
				}
			}
			this.session = null;
		}
	}

	@Override
	protected final long getStartTime() {
		final long st = super.getStartTime();
		if (st >= this.startime) {
			return st;
		}
		return this.startime;
	}

	@Override
	protected final boolean regeneration() {
		if (this.period > 0) {
			this.startime = System.currentTimeMillis() + this.period;
			return true;
		}
		return false;
	}

	@Override
	protected final ConcurrentController getConcurrentController() {
		return this.handler.getConcurrentController();
	}

	@Override
	public final void waitStop(long timeout) throws InterruptedException {
		if (this.period > 0) {
			throw new UnsupportedOperationException("不支持等待周期性任务");
		}
		super.waitStop(timeout);
	}

	private final ServiceInvokeeBase<TTask, Context, None, None, None> handler;
	private final TTask task;

	@Override
	protected final void workDoing(WorkingThread thread) {
		this.context.serviceHandleTask(this.task, this.handler);
	}

	public final TMethod getMethod() {
		return this.task.getMethod();
	}

	public final TTask getTask() throws IllegalStateException {
		this.checkFinished();
		return this.task;
	}
}
