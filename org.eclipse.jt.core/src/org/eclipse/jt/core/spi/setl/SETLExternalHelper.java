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
 * SETL外部帮助接口
 * 
 * @author Jeff Tang
 * 
 */
public interface SETLExternalHelper extends SETLSAXParseReporter {
	/**
	 * 实体标识容器
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public interface EntityPaths {
		public void put(String path);
	}

	/**
	 * 根据引用表获得对应的引用帮助器
	 */
	public SETLEntityRefHelper getEntityRefHelper(Context contex,
			TableDefine refTable);

	public SETLRptSolutionHelper getRptSolutionHelper(Context contex,
			GUID rptSolutionID);

	/**
	 * 报表单位信息
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static class rpt_Unit {
		/**
		 * 单位ID
		 */
		public final GUID id;
		/**
		 * 单位编码
		 */
		public final String code;
		/**
		 * 父单位
		 */
		public final rpt_Unit parent;
		/**
		 * 留作内部使用
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
	 * 获得报表维度范围 - 单位<br>
	 * 
	 */
	public void rpt_fillUnits(Context contex, SortedMap<GUID, rpt_Unit> units);

	/**
	 * 获得报表单位字段
	 */
	public TableFieldDefine rpt_getUnitDimField(Context contex,
			TableDefine rptTable);

	/**
	 * 获得报表期间字段
	 */
	public TableFieldDefine rpt_getPeriodDimField(Context contex,
			TableDefine rptTable);

	public interface rpt_Periods {
		/**
		 * 设置每期的开始时间和编码
		 * 
		 * @param startTime
		 *            开始时间
		 * @param periodCode
		 *            时期编码
		 */
		public void addPeriod(long startTime, String periodCode);

		/**
		 * 设置全部期间的结束时间
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
