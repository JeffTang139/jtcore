package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.monitor.PerformanceLongValueCollector;
import org.eclipse.jt.core.type.GUID;


final class PerformanceLongValueCollectorImpl<TAttachment> extends
		PerformanceValueCollectorImpl<TAttachment> implements
		PerformanceLongValueCollector<TAttachment> {
	PerformanceLongValueCollectorImpl(
			ServiceBase<?>.PerformanceProvider<?> provider) {
		super(provider);
	}

	@Override
	final void getValue(PerformanceValueRequestEntry request, GUID monitorID) {
		request.value = this.value;
	}

	private volatile long value;
	private final static long field_value_offset = Utils.tryGetFieldOffset(
			PerformanceLongValueCollectorImpl.class, "value");

	@Deprecated
	public final void putValue(long longValue) {
		this.setValue(longValue);
	}

	@Deprecated
	public final long getLongValue() {
		return this.value;
	}

	public final void setValue(long value) {
		this.value = value;
		this.lastUpdate = System.currentTimeMillis();
	}

	public final long getValue() {
		return this.value;
	}

	public final long incValue(long value) {
		long newValue;
		if (field_value_offset == Utils.ILLEGAL_OFFSET) {
			this.value = newValue = this.value + value;
		} else {
			long oldValue;
			do {
				oldValue = this.value;
				newValue = oldValue + value;
			} while (!Unsf.unsafe.compareAndSwapLong(this, field_value_offset,
					oldValue, newValue));
		}
		this.lastUpdate = System.currentTimeMillis();
		return newValue;
	}

	public final boolean compareAndSet(long expect, long update) {
		if (field_value_offset == Utils.ILLEGAL_OFFSET) {
			if (this.value == expect) {
				this.value = update;
				this.lastUpdate = System.currentTimeMillis();
				return true;
			}
			return false;
		}
		if (Unsf.unsafe.compareAndSwapLong(this, field_value_offset, expect,
				update)) {
			this.lastUpdate = System.currentTimeMillis();
			return true;
		}
		return false;
	}
}
