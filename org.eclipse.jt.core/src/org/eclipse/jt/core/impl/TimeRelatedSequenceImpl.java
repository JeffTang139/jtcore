package org.eclipse.jt.core.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jt.core.TimeRelatedSequence;


/**
 * ����������<br>
 * ���и�ʽ��<br>
 * 1. 63~24��40bit��ʱ�����䣬�������������ȷ��Ϊ16���롣 �������������ʱ����Ӱ��ʱ�����䣬�����ʱʹ�ó�ǰ��ʱ��<br>
 * 2. 23~4��20bit���������䣬��ʱ��������º������������´���������������������ʱ������ʱ�������λ��<br>
 * ����1�������þ�20λ�������������������ᷢ��,���㷢��Ҳ����Ӱ�����е�Ψһ�ԣ�<br>
 * 3. 3~0��4bit����Ⱥ�ڵ����ţ�Ϊ�˱��ⲻͬ��Ⱥ�ڵ㣨���16������������ͬ�����к�
 * 
 * @author Jeff Tang
 * 
 */
public class TimeRelatedSequenceImpl extends AtomicLong implements
		TimeRelatedSequence {
	final static int hash(long seq) {
		return (int) ((seq >>> (TIME_ZOOM_SHIFT + 4)) ^ (seq >>> 4));
	}

	private static final long serialVersionUID = 1L;
	/**
	 * ʱ������24λ������64-24=40λ��ʾ��������Լ35��Ż�����ظ�
	 */
	static final int TIME_ZOOM_SHIFT = 24;
	/**
	 * 2009-1-1��ʱ��
	 */
	private static long TIME_2009_1_1 = 0x11f2d6afc00L;
	/**
	 * ����ʱ�䣬���������Ʊ��
	 */
	private static long TIME_CIRCLE = (TIME_2009_1_1 << TIME_ZOOM_SHIFT) >>> TIME_ZOOM_SHIFT;
	/**
	 * ������ʱ��ʧ��ʱ��
	 */
	private static long TIME_LOST = TIME_2009_1_1 - TIME_CIRCLE;
	/**
	 * ���ƺ������ʱ��
	 */
	private static long TIME_LOST2 = TIME_LOST + (1L << (64 - TIME_ZOOM_SHIFT));
	/**
	 * ���Ƚضϵ�(1024/64)=16����,����β����4λ
	 */
	private static final long TIME_PRECISION_MASK = -1L << 4;
	/**
	 * ���4λ��ʾ��ID
	 */
	private final int clusterIndex;

	public TimeRelatedSequenceImpl(int clusterIndex) {
		if (clusterIndex < 0 || 15 < clusterIndex) {
			throw new IllegalArgumentException("clusterIndex(" + clusterIndex
					+ ") ������[0..15]���䷶Χ��!");
		}
		this.clusterIndex = clusterIndex;
		this.set(this.nextTimeFirst());
	}

	private final long nextTimeFirst() {
		return ((System.currentTimeMillis() & TIME_PRECISION_MASK) << TIME_ZOOM_SHIFT)
				+ this.clusterIndex;
	}

	public final long last() {
		return super.get();
	}

	public final long next() {
		long nextTimeFirst = this.nextTimeFirst();
		long last, next;
		// no lock no wait
		do {
			last = super.get();
			next = last + 0x10;
			if (next < nextTimeFirst) {
				next = nextTimeFirst;
			}
		} while (!super.compareAndSet(last, next));
		return next;
	}

	public final static long timeOf(long seq) {
		long timePart = ((seq >>> TIME_ZOOM_SHIFT) & TIME_PRECISION_MASK);
		if (timePart > TIME_CIRCLE) {
			return timePart + TIME_LOST;
		} else {
			return timePart + TIME_LOST2;
		}
	}

	static class HelperImpl implements Helper {
		public final long timeOf(long seq) {
			return TimeRelatedSequenceImpl.timeOf(seq);
		}
	}

	public static final HelperImpl helper = new HelperImpl();
}
