package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.monitor.PerformanceObjValueCollector;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;


final class PerformanceObjValueCollectorImpl<TAttachment, TObject> extends
		PerformanceValueCollectorImpl<TAttachment> implements
		PerformanceObjValueCollector<TAttachment, TObject> {
	private final ObjectDataType type;

	PerformanceObjValueCollectorImpl(
			ServiceBase<?>.PerformanceProvider<?> provider) {
		super(provider);
		this.type = (ObjectDataType) provider.declare.dataType;
	}

	private volatile TObject value;
	private final static long field_value_offset = Utils.tryGetFieldOffset(
			PerformanceObjValueCollectorImpl.class, "value");

	@Override
	final void getValue(PerformanceValueRequestEntry request, GUID monitorID) {
		request.value = this.value;
	}

	public void setValue(TObject value) {
		if (value != null && !this.type.isInstance(value)) {
			throw new IllegalArgumentException("value类型错误");
		}
		this.value = value;
		this.lastUpdate = System.currentTimeMillis();
	}

	public final TObject getValue() {
		return this.value;
	}

	public final boolean compareAndSet(TObject expect, TObject update) {
		if (expect != null && !this.type.isInstance(expect)) {
			throw new IllegalArgumentException("expect类型错误");
		}
		if (update != null && !this.type.isInstance(update)) {
			throw new IllegalArgumentException("update类型错误");
		}
		if (field_value_offset == Utils.ILLEGAL_OFFSET) {
			if (this.value == expect) {
				this.value = update;
				this.lastUpdate = System.currentTimeMillis();
				return true;
			}
			return false;
		}
		if (Unsf.unsafe.compareAndSwapObject(this, field_value_offset, expect,
				update)) {
			this.lastUpdate = System.currentTimeMillis();
			return true;
		}
		return false;
	}
}
