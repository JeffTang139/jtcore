package org.eclipse.jt.core.spi.setl;

import java.util.SortedMap;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.exp.TableFieldRefExpr;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.GUID;


/**
 * SETL�ⲿ�����ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface SETLExternalHelper extends SETLSAXParseReporter {
	/**
	 * ʵ���ʶ����
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public interface EntityPaths {
		public void put(String path);
	}

	/**
	 * �������ñ��ö�Ӧ�����ð�����
	 */
	public SETLEntityRefHelper getEntityRefHelper(Context contex,
			TableDefine refTable);

	public SETLRptSolutionHelper getRptSolutionHelper(Context contex,
			GUID rptSolutionID);

	/**
	 * ����λ��Ϣ
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static class rpt_Unit {
		/**
		 * ��λID
		 */
		public final GUID id;
		/**
		 * ��λ����
		 */
		public final String code;
		/**
		 * ����λ
		 */
		public final rpt_Unit parent;
		/**
		 * �����ڲ�ʹ��
		 */
		public Object reserved;

		public rpt_Unit(GUID id, String code, rpt_Unit parent) {
			if (code == null || code.length() == 0) {
				throw new NullArgumentException("code");
			}
			if (id == null) {
				throw new NullArgumentException("id");
			}
			this.id = id;
			this.code = code;
			this.parent = parent;
		}
	}

	/**
	 * ��ñ���ά�ȷ�Χ - ��λ<br>
	 * 
	 */
	public void rpt_fillUnits(Context contex, SortedMap<GUID, rpt_Unit> units);

	/**
	 * ��ñ���λ�ֶ�
	 */
	public TableFieldDefine rpt_getUnitDimField(Context contex,
			TableDefine rptTable);

	/**
	 * ��ñ����ڼ��ֶ�
	 */
	public TableFieldDefine rpt_getPeriodDimField(Context contex,
			TableDefine rptTable);

	public interface rpt_Periods {
		/**
		 * ����ÿ�ڵĿ�ʼʱ��ͱ���
		 * 
		 * @param startTime
		 *            ��ʼʱ��
		 * @param periodCode
		 *            ʱ�ڱ���
		 */
		public void addPeriod(long startTime, String periodCode);

		/**
		 * ����ȫ���ڼ�Ľ���ʱ��
		 * 
		 * @param endTime
		 */
		public void setEndTime(long endTime);
	}

	public ConditionalExpression rpt_fillPeriodsAndBuildTypeCondition(
			Context context, int periodType, rpt_Periods periods,
			TableFieldRefExpr periodFieldRef);

	public int rpt_getPeriodType(Context context, GUID rptSolutionID);
}
