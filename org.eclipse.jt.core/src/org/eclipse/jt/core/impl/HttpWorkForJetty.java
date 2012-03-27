/**
 * 
 */
package org.eclipse.jt.core.impl;

class HttpWorkForJetty extends Work {
	private Runnable job;
	private ThreadPoolForJetty pool;

	HttpWorkForJetty(ThreadPoolForJetty pool, Runnable job) {
		this.job = job;
		this.pool = pool;
	}

	@Override
	protected final void doWork(WorkingThread thread) {
		try {
			this.job.run();
		} finally {
			this.pool.WorkFinalizing();
			this.job = null;
			this.pool = null;
		}
	}
}