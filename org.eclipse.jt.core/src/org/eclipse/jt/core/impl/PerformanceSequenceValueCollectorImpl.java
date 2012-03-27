package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.spi.monitor.PerformanceSequenceValueCollector;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ObjectDataType;


final class PerformanceSequenceValueCollectorImpl<TAttachment, TObject> extends
		PerformanceValueCollectorImpl<TAttachment> implements
		PerformanceSequenceValueCollector<TAttachment, TObject> {
	private final ObjectDataType type;

	private final Object lock = new Object();

	private static class Entry {
		Object value;
		Entry prev;
	}

	private static class Monitor {
		final GUID monitorID;
		private int capcity;
		private Entry lastEntry;
		int size;
		Monitor nextMonitor;

		final boolean append(Object value) {
			final Entry last = this.lastEntry;
			if (this.size < this.capcity) {
				final Entry e = new Entry();
				e.value = value;
				if (last != null) {
					e.prev = last.prev;
					last.prev = e;
				} else {
					e.prev = e;
				}
				this.lastEntry = e;
				this.size++;
				return true;
			} else {
				// 重用尾部数据
				last.value = value;
				this.lastEntry = last.prev;
				return false;
			}
		}

		final Entry extract(int nextCapcity) {
			this.capcity = nextCapcity;
			this.size = 0;
			Entry last = this.lastEntry;
			if (last == null) {
				return null;
			} else {
				this.lastEntry = null;
				return last.prev;
			}

		}

		final static Object[] toArray(Entry firstEntry, int size) {
			if (firstEntry == null) {
				return Utils.emptyObjectArray;
			}
			final Object[] objs = new Object[size];
			for (int i = 0; i < size; i++) {
				objs[i] = firstEntry.value;
				final Entry p = firstEntry.prev;
				p.prev = null;// help GC
				firstEntry = p;
			}
			return objs;
		}

		Monitor(GUID monitorID, int capcity) {
			this.monitorID = monitorID;
			this.capcity = capcity;
		}
	}

	private Monitor first;

	@Override
	final void ensureMonitor(GUID monitorID) {
		synchronized (this.lock) {
			Monitor monitor = this.first;
			Monitor last = null;
			while (monitor != null && !monitor.monitorID.equals(monitorID)) {
				last = monitor;
				monitor = monitor.nextMonitor;
			}
			if (monitor == null) {
				// 创建
				monitor = new Monitor(monitorID, Integer.MAX_VALUE);
				if (last == null) {
					monitor.nextMonitor = this.first;
					this.first = monitor;
				}
			}
		}
	}

	@Override
	final void getValue(PerformanceValueRequestEntry request, GUID monitorID) {
		final int size;
		final Entry firstEntry;
		synchronized (this.lock) {
			Monitor monitor = this.first;
			Monitor last = null;
			while (monitor != null && !monitor.monitorID.equals(monitorID)) {
				last = monitor;
				monitor = monitor.nextMonitor;
			}
			final int nextCapcity = request.nextCapcity;
			if (monitor == null) {
				if (nextCapcity > 0) {
					// 创建
					Monitor m = new Monitor(monitorID, nextCapcity);
					if (last == null) {
						m.nextMonitor = this.first;
						this.first = m;
					}
				}
				size = 0;
				firstEntry = null;
			} else {
				if (nextCapcity <= 0) {
					// 销毁
					if (last == null) {
						this.first = monitor.nextMonitor;
					} else {
						last.nextMonitor = monitor.nextMonitor;
					}
					monitor.nextMonitor = null;
				}
				size = monitor.size;
				firstEntry = monitor.extract(nextCapcity);
			}
		}
		request.value = Monitor.toArray(firstEntry, size);
	}

	PerformanceSequenceValueCollectorImpl(
			ServiceBase<?>.PerformanceProvider<?> provider) {
		super(provider);
		this.type = (ObjectDataType) provider.declare.dataType;
	}

	public final boolean append(TObject value) {
		if (value != null && !this.type.isInstance(value)) {
			throw new IllegalArgumentException("value类型错误");
		}
		boolean hasSpace = false;
		synchronized (this.lock) {
			for (Monitor monitor = this.first; monitor != null; monitor = monitor.nextMonitor) {
				hasSpace |= monitor.append(value);
			}
		}
		this.lastUpdate = System.currentTimeMillis();
		return hasSpace;
	}

}
