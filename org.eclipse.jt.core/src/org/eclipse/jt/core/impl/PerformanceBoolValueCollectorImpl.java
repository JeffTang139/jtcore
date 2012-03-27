package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.monitor.PerformanceBoolValueCollector;
import org.eclipse.jt.core.type.GUID;


final class PerformanceBoolValueCollectorImpl<TAttachment> extends
		PerformanceValueCollectorImpl<TAttachment> implements
		PerformanceBoolValueCollector<TAttachment> {
	PerformanceBoolValueCollectorImpl(
			ServiceBase<?>.PerformanceProvider<?> provider) {
		super(provider);
	}

	private volatile int value;
	private final static long field_value_offset = Utils.tryGetFieldOffset(
			PerformanceBoolValueCollectorImpl.class, "value");

	@Override
	final void getValue(PerformanceValueRequestEntry request, GUID monitorID) {
		request.value = this.value;
	}

	public void setValue(boolean value) {
		this.value = value ? 1 : 0;
		this.lastUpdate = System.currentTimeMillis();
	}

	public final boolean getValue() {
		return this.value != 0;
	}

	public final boolean compareAndSet(boolean e, boolean u) {
		final int expect = e ? 1 : 0;
		final int update = u ? 1 : 0;
		if (field_value_offset == Utils.ILLEGAL_OFFSET) {
			if (this.value == expect) {
				this.value = update;
				this.lastUpdate = System.currentTimeMillis();
				return true;
			}
			return false;
		}
		if (Unsf.unsafe.compareAndSwapInt(this, field_value_offset, expect,
				update)) {
			this.lastUpdate = System.currentTimeMillis();
			return true;
		}
		return false;
	}
}
