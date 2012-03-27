package org.eclipse.jt.core.spi.monitor;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * ���ܼ��ָ��ֵ����ƾ��<br>
 * �������������ָ���˳�򷵻�����List����Ч��ָ���ڷ��ص�List��ֵΪnull<br>
 * ʹ�þ���:
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
	 * ����ָ��
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
		 * ���ָ��ID
		 */
		private final GUID indexID;

		/**
		 * ���ָ������
		 */
		public final GUID getIndexID() {
			return this.indexID;
		}

		/**
		 * ��ػỰID�����ڻỰ�����ָ����Ч������ϵͳ�����ָ�������塣
		 */
		private final long sessionID;

		/**
		 * ��ػỰID�����ڻỰ�����ָ����Ч������ϵͳ�����ָ�������塣
		 */
		public final long getSessionID() {
			return this.sessionID;
		}

		/**
		 * �������������ȡ����ָ��ֵ��ͬʱ�趨�����������<br>
		 * ֵ����1ʱ����������������Ч
		 */
		private int nextCapacity;

		/**
		 * �������������ȡ����ָ��ֵ��ͬʱ�趨�����������<br>
		 * ֵ����1ʱ����������������Ч
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
	 * ��Ӽ�ص�ָ��
	 * 
	 * @param ndexID
	 *            ���ָ��ID
	 * @param sessionID
	 *            ��ػỰID�����ڻỰ�����ָ����Ч������ϵͳ�����ָ�������塣
	 * @param nextCapcity
	 *            �������������ȡ����ָ��ֵ��ͬʱ�趨�����������<br>
	 *            ֵ����1ʱ����������������Ч
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
	 * ��Ӽ�ص�ָ��
	 * 
	 * @param indexID
	 *            ���ָ��ID
	 * @param sessionID
	 *            ��ػỰID�����ڻỰ�����ָ����Ч������ϵͳ�����ָ�������塣
	 */
	public final PerformanceIndex addPerformanceIndex(GUID indexID,
			long sessionID) {
		return this.addPerformanceIndex(indexID, sessionID, 1);
	}

	/**
	 * ��Ӽ�ص�ָ��
	 * 
	 * @param indexID
	 *            ���ָ��ID
	 */
	public final PerformanceIndex addPerformanceIndex(GUID indexID) {
		return this.addPerformanceIndex(indexID, 0l, 1);
	}

	/**
	 * ��Ӽ�ص�ָ��
	 * 
	 * @param indexID
	 *            ���ָ��ID
	 * @param sessionID
	 *            ��ػỰID�����ڻỰ�����ָ����Ч������ϵͳ�����ָ�������塣
	 * @param nextCapcity
	 *            �������������ȡ����ָ��ֵ��ͬʱ�趨�����������<br>
	 *            ֵ����1ʱ����������������Ч
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
	 * ���ID���������ֲ�ͬ��ضˣ��Ա����ȡ����ָ��ʱ��ͬ�ļ�ض��໥Ӱ�졣
	 */
	public final GUID monitorID;

	/**
	 * ���캯��
	 * 
	 * @param monitorID
	 *            ���ID���������ֲ�ͬ��ضˣ��Ա����ȡ����ָ��ʱ��ͬ�ļ�ض��໥Ӱ�졣
	 */
	public PerformanceValuesQueryBy(GUID monitorID) {
		if (monitorID == null) {
			throw new NullArgumentException("monitorID");
		}
		this.monitorID = monitorID;
	}

}
