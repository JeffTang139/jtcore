package org.eclipse.jt.core.spi.monitor;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * 性能监控指标值请求凭据<br>
 * 根据请求中添加指标的顺序返回数据List，无效的指标在返回的List中值为null<br>
 * 使用举例:
 * 
 * <pre>
 * final PerformanceValuesQueryBy pvq = new PerformanceValuesQueryBy();
 * pvq.add(...);
 * context.getList(ReadableValue.class, pvq);
 * </pre>
 * 
 * @author Jeff Tang
 * 
 */
@StructClass
public final class PerformanceValuesQueryBy implements
		Iterable<PerformanceValuesQueryBy.PerformanceIndex> {

	/**
	 * 性能指标
	 * 
	 * @author Jeff Tang
	 * 
	 */
	@StructClass
	public static final class PerformanceIndex {
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof PerformanceIndex) {
				final PerformanceIndex pi = (PerformanceIndex) obj;
				return pi.sessionID == this.sessionID
						&& pi.indexID.equals(this.indexID);
			}
			return false;
		}

		/**
		 * 监控指标ID
		 */
		private final GUID indexID;

		/**
		 * 监控指标名称
		 */
		public final GUID getIndexID() {
			return this.indexID;
		}

		/**
		 * 监控会话ID，对于会话级监控指标有效，对于系统级监控指标无意义。
		 */
		private final long sessionID;

		/**
		 * 监控会话ID，对于会话级监控指标有效，对于系统级监控指标无意义。
		 */
		public final long getSessionID() {
			return this.sessionID;
		}

		/**
		 * 监控器容量，获取性能指标值得同时设定监控器容量。<br>
		 * 值大于1时仅对于序列类型有效
		 */
		private int nextCapacity;

		/**
		 * 监控器容量，获取性能指标值得同时设定监控器容量。<br>
		 * 值大于1时仅对于序列类型有效
		 */
		public final int getNextCapacity() {
			return this.nextCapacity;
		}

		public final void setNextCapacity(int capacity) {
			this.nextCapacity = capacity;
		}

		private PerformanceIndex(GUID indexID, long sessionID, int nextCapcity) {
			if (indexID == null) {
				throw new NullArgumentException("indexID");
			}
			this.indexID = indexID;
			this.sessionID = sessionID;
			this.nextCapacity = nextCapcity;
		}
	}

	private final ArrayList<PerformanceIndex> indexes = new ArrayList<PerformanceIndex>();

	/**
	 * 添加监控的指标
	 * 
	 * @param ndexID
	 *            监控指标ID
	 * @param sessionID
	 *            监控会话ID，对于会话级监控指标有效，对于系统级监控指标无意义。
	 * @param nextCapcity
	 *            监控器容量，获取性能指标值得同时设定监控器容量。<br>
	 *            值大于1时仅对于序列类型有效
	 */
	public final PerformanceIndex addPerformanceIndex(GUID indexID,
			long sessionID, int nextCapcity) {
		final PerformanceIndex pi = new PerformanceIndex(indexID, sessionID,
				nextCapcity);
		int i = this.indexes.indexOf(pi);
		if (i >= 0) {
			final PerformanceIndex oldPi = this.indexes.get(i);
			oldPi.setNextCapacity(nextCapcity);
			return oldPi;
		} else {
			this.indexes.add(pi);
		}
		return pi;
	}

	/**
	 * 添加监控的指标
	 * 
	 * @param indexID
	 *            监控指标ID
	 * @param sessionID
	 *            监控会话ID，对于会话级监控指标有效，对于系统级监控指标无意义。
	 */
	public final PerformanceIndex addPerformanceIndex(GUID indexID,
			long sessionID) {
		return this.addPerformanceIndex(indexID, sessionID, 1);
	}

	/**
	 * 添加监控的指标
	 * 
	 * @param indexID
	 *            监控指标ID
	 */
	public final PerformanceIndex addPerformanceIndex(GUID indexID) {
		return this.addPerformanceIndex(indexID, 0l, 1);
	}

	/**
	 * 添加监控的指标
	 * 
	 * @param indexID
	 *            监控指标ID
	 * @param sessionID
	 *            监控会话ID，对于会话级监控指标有效，对于系统级监控指标无意义。
	 * @param nextCapcity
	 *            监控器容量，获取性能指标值得同时设定监控器容量。<br>
	 *            值大于1时仅对于序列类型有效
	 */
	public final PerformanceIndex addPerformanceIndex(GUID indexID,
			int nextCapcity) {
		return this.addPerformanceIndex(indexID, 0l, nextCapcity);
	}

	public final Iterator<PerformanceIndex> iterator() {
		return this.indexes.iterator();
	}

	public final int getPerformanceIndexCount() {
		return this.indexes.size();
	}

	public final PerformanceIndex getPerformanceIndex(int index) {
		return this.indexes.get(index);
	}

	/**
	 * 监控ID，用以区分不同监控端，以避免获取序列指标时不同的监控端相互影响。
	 */
	public final GUID monitorID;

	/**
	 * 构造函数
	 * 
	 * @param monitorID
	 *            监控ID，用以区分不同监控端，以避免获取序列指标时不同的监控端相互影响。
	 */
	public PerformanceValuesQueryBy(GUID monitorID) {
		if (monitorID == null) {
			throw new NullArgumentException("monitorID");
		}
		this.monitorID = monitorID;
	}

}
