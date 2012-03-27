package org.eclipse.jt.core.impl;

abstract class RepeatWork extends Work {

	private long starttime;
	private final int period;

	RepeatWork(int period) {
		this.period = period;
	}

	@Override
	protected final long getStartTime() {
		return this.starttime;
	}

	@Override
	protected final boolean regeneration() {
		this.starttime = System.currentTimeMillis() + this.period;
		return true;
	}
}
