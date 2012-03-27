package org.eclipse.jt.core.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.jt.core.TimeRelatedSequence;


/**
 * 序列生成器<br>
 * 序列格式：<br>
 * 1. 63~24，40bit：时间区间，保存毫秒数，精确度为16毫秒。 当序列区间溢出时，会影响时间区间，造成暂时使用超前的时间<br>
 * 2. 23~4，20bit：序列区间，当时间区间更新后序列区间重新从零计数，当序列区间溢出时，将向时间区间进位。<br>
 * （在1毫秒内用尽20位序列区间的情况几乎不会发生,即便发生也不会影响序列的唯一性）<br>
 * 3. 3~0，4bit：集群节点的序号，为了避免不同集群节点（最多16个）产生出相同的序列号
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
	 * 时间左移24位，即有64-24=40位表示毫秒数，约35年才会回绕重复
	 */
	static final int TIME_ZOOM_SHIFT = 24;
	/**
	 * 2009-1-1的时间
	 */
	private static long TIME_2009_1_1 = 0x11f2d6afc00L;
	/**
	 * 回绕时间，用以作回绕标记
	 */
	private static long TIME_CIRCLE = (TIME_2009_1_1 << TIME_ZOOM_SHIFT) >>> TIME_ZOOM_SHIFT;
	/**
	 * 做左移时损失的时间
	 */
	private static long TIME_LOST = TIME_2009_1_1 - TIME_CIRCLE;
	/**
	 * 回绕后调整的时间
	 */
	private static long TIME_LOST2 = TIME_LOST + (1L << (64 - TIME_ZOOM_SHIFT));
	/**
	 * 精度截断到(1024/64)=16毫秒,保留尾部后4位
	 */
	private static final long TIME_PRECISION_MASK = -1L << 4;
	/**
	 * 最低4位表示簇ID
	 */
	private final int clusterIndex;

	public TimeRelatedSequenceImpl(int clusterIndex) {
		if (clusterIndex < 0 || 15 < clusterIndex) {
			throw new IllegalArgumentException("clusterIndex(" + clusterIndex
					+ ") 必须在[0..15]区间范围内!");
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
