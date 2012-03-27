package org.eclipse.jt.core.spi.setl;

import java.util.Comparator;
import java.util.Set;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.exp.TableFieldRefExpr;
import org.eclipse.jt.core.exception.NullArgumentException;


public interface SETLRptSolutionHelper {
	/**
	 * ����ʱ����Ϣ
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static class rpt_Period implements Comparable<rpt_Period> {
		/**
		 * ʱ�ڵĿ�ʼʱ��
		 */
		public final long start;
		/**
		 * ʱ�ڵı���
		 */
		public final String code;

		@Override
		public final boolean equals(Object obj) {
			return obj instanceof rpt_Period
					&& ((rpt_Period) obj).start == this.start;
		}

		@Override
		public final int hashCode() {
			return (int) (this.start ^ (this.start >>> 32));
		}

		public rpt_Period(long start, String code) {
			if (code == null || code.length() == 0) {
				throw new NullArgumentException("code");
			}
			this.start = start;
			this.code = code;
		}

		public rpt_Period(long end) {
			this.start = end;
			this.code = null;
		}

		public final static Comparator<rpt_Period> comparator = new Comparator<rpt_Period>() {
			public int compare(rpt_Period o1, rpt_Period o2) {
				long d = o1.start - o2.start;
				return d == 0 ? 0 : (d > 0 ? 1 : -1);
			}
		};

		public final int compareTo(rpt_Period o) {
			long d = this.start - o.start;
			return d == 0 ? 0 : (d > 0 ? 1 : -1);
		}
	}

	/**
	 * ��ñ���ά�ȷ�Χ - ʱ��<br>
	 * ʱ��Ҫ��������һ������ʱ��
	 */
	public void rpt_fillPeriods(Context contex, Set<rpt_Period> periods);

	/**
	 * �����ڼ����Ĺ������������걨��Ϊsubstring(period,5,1)='N'
	 */
	public ConditionalExpression buildPeriodFilter(TableFieldRefExpr periodFieldRef);

}
