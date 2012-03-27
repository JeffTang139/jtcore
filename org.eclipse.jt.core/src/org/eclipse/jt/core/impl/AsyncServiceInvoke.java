package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.SiteState;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.info.Info;
import org.eclipse.jt.core.invoke.AsyncHandle;


/**
 * 异步调用基类
 * 
 * @author Jeff Tang
 * 
 */
abstract class AsyncServiceInvoke extends Work implements AsyncHandle, Waitable {

	public final int fetchInfos(List<Info> to) {
		final ContextImpl<?, ?, ?> context = this.context;
		return context != null ? context.fetchInfos(to) : 0;
	}

	@Override
	protected boolean workBeginning() {
		this.context = this.session.newContext(this.occurAt,
				ContextKind.TRANSIENT);
		return true;
	}

	@Override
	protected long getStartTime() {
		if (this.occurAt.site.state == SiteState.INITING) {
			return System.currentTimeMillis() + 500;
		}
		return 0l;
	}

	@Override
	protected void workFinalizing(Throwable e) {
		this.exception = e;
		if (this.context != null) {
			if (e == null) {
				this.finalProgress = 1;
			} else {
				this.finalProgress = -this.context.progress;
			}
			this.context.dispose();
			this.context = null;
		}
	}

	@Override
	public void waitStop(long timeout) throws InterruptedException {
		if (this.occurAt.site.state == SiteState.INITING) {
			throw new UnsupportedOperationException(
					"不支持等站点待初始化期间启动的异步调用，这些调用将在站点初始化完成后才会启动");
		}
		super.waitStop(timeout);
	}

	@Override
	protected final void workCanceling() {
		final ContextImpl<?, ?, ?> context = this.context;
		if (context != null) {
			context.cancel();
		}
	}

	/**
	 * 如果处理过程中有异常，则返回该异常，否则返回null
	 * 
	 * @return 返回异常或者null
	 */
	public final Throwable getException() {
		return this.exception;
	}

	/**
	 * 处理进度，0表示还未处理，1表示处理完毕，之间的数表示进度，小于零的数表示中途出现错误
	 * 
	 * @return 返回处理进度
	 */
	public final float getProgress() {
		ContextImpl<?, ?, ?> context = this.context;
		return context != null ? context.progress : this.finalProgress;
	}

	/**
	 * 执行中未截获的异常
	 */
	private Throwable exception;
	/**
	 * 最终进度
	 */
	private float finalProgress;
	/**
	 * 当前运行的上下文
	 */
	protected ContextImpl<?, ?, ?> context;

	/**
	 * 会话，可能为空
	 */
	SessionImpl session;
	/**
	 * 调用的空间位置
	 */
	final SpaceNode occurAt;

	AsyncServiceInvoke(SessionImpl session, SpaceNode occurAt) {
		if (occurAt == null) {
			throw new NullArgumentException("occurAt");
		}
		this.occurAt = occurAt;
		this.session = session;
	}

	/**
	 * 开始异步处理
	 */
	protected final void beginAsync() {
		this.occurAt.site.application.overlappedManager.postWork(this);
	}

}
