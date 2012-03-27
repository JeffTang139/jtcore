package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.monitor.PerformanceDoubleValueCollector;
import org.eclipse.jt.core.type.GUID;


final class PerformanceDoubleValueCollectorImpl<TAttachment> extends
		PerformanceValueCollectorImpl<TAttachment> implements
		PerformanceDoubleValueCollector<TAttachment> {
	PerformanceDoubleValueCollectorImpl(
			ServiceBase<?>.PerformanceProvider<?> provider) {
		super(provider);
	}

	private volatile double value;
	private final static long field_value_offset = Utils.tryGetFieldOffset(
			PerformanceDoubleValueCollectorImpl.class, "value");

	@Override
	final void getValue(PerformanceValueRequestEntry request, GUID monitorID) {
		request.value = this.value;
	}

	@Deprecated
	public void putValue(double doubleValue) {
		this.setValue(doubleValue);
	}

	public void setValue(double value) {
		this.value = value;
		this.lastUpdate = System.currentTimeMillis();
	}

	@Deprecated
	public final double getDoubleValue() {
		return this.value;
	}

	public final double getValue() {
		return this.value;
	}

	public final double incValue(double value) {
		double newValue;
		if (field_value_offset == Utils.ILLEGAL_OFFSET) {
			this.value = newValue = this.value + value;
		} else {
			double oldValue;
			do {
				oldValue = this.value;
				newValue = oldValue + value;
			} while (!Unsf.unsafe.compareAndSwapLong(this, field_value_offset,
					Double.doubleToRawLongBits(oldValue), Double
							.doubleToRawLongBits(newValue)));
		}
		this.lastUpdate = System.currentTimeMillis();
		return newValue;
	}

	public final boolean compareAndSet(double expect, double update) {
		if (field_value_offset == Utils.ILLEGAL_OFFSET) {
			if (this.value == expect) {
				this.value = update;
				this.lastUpdate = System.currentTimeMillis();
				return true;
			}
			return false;
		}
		if (Unsf.unsafe.compareAndSwapLong(this, field_value_offset, Double
				.doubleToRawLongBits(expect), Double
				.doubleToRawLongBits(update))) {
			this.lastUpdate = System.currentTimeMillis();
			return true;
		}
		return false;
	}
}
