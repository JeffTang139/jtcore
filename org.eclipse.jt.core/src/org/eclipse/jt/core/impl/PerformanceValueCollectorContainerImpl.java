package org.eclipse.jt.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.impl.ServiceBase.PerformanceProvider;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDefine;
import org.eclipse.jt.core.spi.monitor.PerformanceMonitorStartResult;
import org.eclipse.jt.core.type.GUID;


/**
 * ���ָ��ֵ�ռ�������
 * 
 * @author Jeff Tang
 * 
 */
final class PerformanceValueCollectorContainerImpl {
	private final ReentrantReadWriteLock.ReadLock readLock;
	private final ReentrantReadWriteLock.WriteLock writeLock;
	private HashMap<PerformanceIndexDefineImpl, PerformanceValueCollectorImpl<?>> collectors = new HashMap<PerformanceIndexDefineImpl, PerformanceValueCollectorImpl<?>>();
	private volatile ArrayList<PerformanceValueCollectorImpl<?>> dieds;

	PerformanceValueCollectorContainerImpl() {
		final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		this.readLock = lock.readLock();
		this.writeLock = lock.writeLock();
	}

	/**
	 * �����Ƿ���Ҫ����
	 */
	final boolean collectOld(long maxAge) {
		this.writeLock.lock();
		try {
			if (!this.collectors.isEmpty()) {
				for (Iterator<PerformanceValueCollectorImpl<?>> pvcs = this.collectors
						.values().iterator(); pvcs.hasNext();) {
					final PerformanceValueCollectorImpl<?> pvc = pvcs.next();
					if (pvc.lastRequired < maxAge) {
						if (this.dieds == null) {
							this.dieds = new ArrayList<PerformanceValueCollectorImpl<?>>();
						}
						this.dieds.add(pvc);
						pvcs.remove();
					}
				}
			}
			return this.dieds != null;
		} finally {
			this.writeLock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	final ContextImpl<?, ?, ?> dispose(SessionImpl session,
			ContextImpl<?, ?, ?> context) {
		this.writeLock.lock();
		try {
			ArrayList<PerformanceValueCollectorImpl<?>> dieds = this.dieds;
			this.dieds = null;
			if (!this.collectors.isEmpty()) {
				if (dieds == null) {
					dieds = new ArrayList<PerformanceValueCollectorImpl<?>>();
				}
				dieds.addAll(this.collectors.values());
				this.collectors.clear();
			}
			if (dieds != null && !dieds.isEmpty()) {
				final int size = dieds.size();
				for (int i = 0; i < size; i++) {
					dieds.get(i).lastRequired = -1;
				}
				for (int i = 0; i < size; i++) {
					final PerformanceValueCollectorImpl died = dieds.get(i);
					if (context == null) {
						context = session.newContext(ContextKind.DISPOSER);
					}
					try {
						if (!died.provider.stopMonitor(session, died)) {
							context.stopMoniter(died);
						}
					} catch (Throwable e) {
						session.application.catcher.catchException(e, died);
					}
				}
			}
		} catch (Throwable e) {
			// ����
		} finally {
			this.writeLock.unlock();
		}
		return context;
	}

	/**
	 * �ռ����ڵ��ռ���
	 */
	@SuppressWarnings("unchecked")
	final void gc(SessionImpl session) {
		final ArrayList<PerformanceValueCollectorImpl<?>> dieds;
		this.readLock.lock();
		try {
			dieds = this.dieds;
			this.dieds = null;
		} finally {
			this.readLock.unlock();
		}
		if (!(dieds == null || dieds.isEmpty() || session.disposingOrDisposed())) {
			ContextImpl<?, ?, ?> context = null;
			final int size = dieds.size();
			try {
				for (int i = 0; i < size; i++) {
					dieds.get(i).lastRequired = -1;
				}
				for (int i = 0; i < size; i++) {
					final PerformanceValueCollectorImpl died = dieds.get(i);
					try {
						if (!died.provider.stopMonitor(session, died)) {
							if (context == null) {
								this.writeLock.lock();
								try {
									context = session
											.newContext(ContextKind.TRANSIENT);
								} finally {// �����쳣
									if (context == null) {
										this.writeLock.unlock();
									}
								}
							}
							context.stopMoniter(died);
						}
					} catch (Throwable e) {
						session.application.catcher.catchException(e, died);
					}
				}
			} finally {
				if (context != null) {
					try {
						context.dispose();
					} finally {
						this.writeLock.unlock();
					}
				}
			}
		}
	}

	final PerformanceValueCollectorImpl<?> findCollector(
			PerformanceIndexDefine define) {
		this.readLock.lock();
		try {
			return this.collectors.get(define);
		} finally {
			this.readLock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	final void updateValues(SessionImpl session, GUID monitorID,
			PerformanceValueRequestEntry requests) {
		long now = System.currentTimeMillis();
		ContextImpl<?, ?, ?> context = null;// ��һ������Ҫ������contextʱ����write������ͬһ���Ựͬʱ�������context
		try {
			for (PerformanceValueRequestEntry request = requests; request != null; request = request.nextInSameSession) {
				final PerformanceProvider provider = request.provider;
				PerformanceValueCollectorImpl<?> collector;
				findToUpdate: {
					if (context == null) {
						this.readLock.lock();
						try {
							collector = this.collectors.get(provider.declare);
						} finally {
							this.readLock.unlock();
						}
						if (collector != null) {
							break findToUpdate;
						}
						this.writeLock.lock();
					}
					try {
						collector = this.collectors.get(provider.declare);
						if (collector != null) {
							break findToUpdate;
						}
						// ///////////////����///////////////////
						collector = provider.newCollector();
						collector.lastRequired = now;
						collector.ensureMonitor(monitorID);
						final PerformanceMonitorStartResult pmr = provider
								.startMonitor(session, collector);
						if (pmr == null
								|| pmr == PerformanceMonitorStartResult.COMPLETE) {
							// ����Ҫ����
							collector.getValue(request, monitorID);
							collector.lastRequired = -1;
							continue;
						} else if (pmr == PerformanceMonitorStartResult.NEED_CONTEXT) {
							if (context == null) {
								context = session
										.newContext(ContextKind.TRANSIENT);
							}
							if (context.startMoniter(collector)) {
								// ����Ҫ����
								collector.getValue(request, monitorID);
								collector.lastRequired = -1;
								continue;
							}
						}
						// //////////////����////////////////////
						collector.getValue(request, monitorID);
						collector.lastUpdate = now;
						this.collectors.put(provider.declare, collector);
					} catch (Throwable e) {
						session.application.catcher.catchException(e, provider);
					} finally {
						if (context == null) {
							this.writeLock.unlock();
						}
					}
					continue;
				}
				// //////////////���Ը���////////////////////
				if (collector.valueExpired(now)) {
					try {
						if (!provider.update(session, collector)) {
							if (context == null) {
								this.writeLock.lock();
								try {
									context = session
											.newContext(ContextKind.TRANSIENT);
								} finally {// �����쳣
									if (context == null) {
										this.writeLock.unlock();
									}
								}
							}
							context.updateMoniter(collector);
						}
						collector.lastUpdate = now;
					} catch (Throwable e) {
						session.application.catcher.catchException(e, provider);
						continue;
					}
				}
				// //////////////��ȡֵ////////////////////
				collector.getValue(request, monitorID);
				collector.lastRequired = now;
			}
		} finally {
			if (context != null) {
				try {
					context.dispose();
				} finally {
					this.writeLock.unlock();
				}
			}
		}
	}
}
