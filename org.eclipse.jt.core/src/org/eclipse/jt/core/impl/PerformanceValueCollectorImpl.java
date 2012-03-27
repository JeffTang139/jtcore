package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDefine;
import org.eclipse.jt.core.spi.monitor.PerformanceValueCollector;
import org.eclipse.jt.core.type.GUID;


/**
 * 监控数据容器
 * 
 * @author Jeff Tang
 * 
 */
abstract class PerformanceValueCollectorImpl<TAttachment> implements
		PerformanceValueCollector<TAttachment> {

	void ensureMonitor(GUID monitorID) {
	}

	/**
	 * 返回false表示需要销毁
	 */
	abstract void getValue(PerformanceValueRequestEntry request, GUID monitorID);

	private TAttachment attachment;

	public final TAttachment getAttachment() {
		return this.attachment;
	}

	public final void setAttachment(TAttachment attachment) {
		this.attachment = attachment;
	};

	final ServiceBase<?>.PerformanceProvider<?> provider;

	PerformanceValueCollectorImpl(ServiceBase<?>.PerformanceProvider<?> provider) {
		if (provider == null) {
			throw new NullArgumentException("provider");
		}
		this.provider = provider;
	}

	public final PerformanceIndexDefine getDefine() {
		return this.provider.declare;
	}

	/**
	 * 最后一次更新时间
	 */
	volatile long lastUpdate;
	/**
	 * 最后一次请求时间，回收前会设置为-1
	 */
	volatile long lastRequired;

	/**
	 * 数据的过期时间，毫秒。过期的数据再有请求时会调用update方法更新
	 */
	volatile long valueMaxAge;

	/**
	 * 数据的过期时间，毫秒。
	 */
	public final long getValueMaxAge() {
		return this.valueMaxAge;
	}

	/**
	 * 设置数据的过期时间。
	 */
	public final void setValueMaxAge(long valueMaxAge) {
		this.valueMaxAge = valueMaxAge;
	}

	final boolean valueExpired(long now) {
		return this.valueMaxAge == 0
				|| this.lastUpdate + this.valueMaxAge < now;
	}

	public final boolean isActived() {
		return this.lastRequired >= 0;
	}

}
