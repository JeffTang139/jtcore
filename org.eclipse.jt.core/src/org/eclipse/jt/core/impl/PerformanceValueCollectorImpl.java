package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDefine;
import org.eclipse.jt.core.spi.monitor.PerformanceValueCollector;
import org.eclipse.jt.core.type.GUID;


/**
 * �����������
 * 
 * @author Jeff Tang
 * 
 */
abstract class PerformanceValueCollectorImpl<TAttachment> implements
		PerformanceValueCollector<TAttachment> {

	void ensureMonitor(GUID monitorID) {
	}

	/**
	 * ����false��ʾ��Ҫ����
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
	 * ���һ�θ���ʱ��
	 */
	volatile long lastUpdate;
	/**
	 * ���һ������ʱ�䣬����ǰ������Ϊ-1
	 */
	volatile long lastRequired;

	/**
	 * ���ݵĹ���ʱ�䣬���롣���ڵ�������������ʱ�����update��������
	 */
	volatile long valueMaxAge;

	/**
	 * ���ݵĹ���ʱ�䣬���롣
	 */
	public final long getValueMaxAge() {
		return this.valueMaxAge;
	}

	/**
	 * �������ݵĹ���ʱ�䡣
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
