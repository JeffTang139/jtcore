package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.SiteState;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.info.Info;
import org.eclipse.jt.core.invoke.AsyncHandle;


/**
 * �첽���û���
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
					"��֧�ֵ�վ�����ʼ���ڼ��������첽���ã���Щ���ý���վ���ʼ����ɺ�Ż�����");
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
	 * ���������������쳣���򷵻ظ��쳣�����򷵻�null
	 * 
	 * @return �����쳣����null
	 */
	public final Throwable getException() {
		return this.exception;
	}

	/**
	 * ������ȣ�0��ʾ��δ����1��ʾ������ϣ�֮�������ʾ���ȣ�С���������ʾ��;���ִ���
	 * 
	 * @return ���ش������
	 */
	public final float getProgress() {
		ContextImpl<?, ?, ?> context = this.context;
		return context != null ? context.progress : this.finalProgress;
	}

	/**
	 * ִ����δ�ػ���쳣
	 */
	private Throwable exception;
	/**
	 * ���ս���
	 */
	private float finalProgress;
	/**
	 * ��ǰ���е�������
	 */
	protected ContextImpl<?, ?, ?> context;

	/**
	 * �Ự������Ϊ��
	 */
	SessionImpl session;
	/**
	 * ���õĿռ�λ��
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
	 * ��ʼ�첽����
	 */
	protected final void beginAsync() {
		this.occurAt.site.application.overlappedManager.postWork(this);
	}

}
