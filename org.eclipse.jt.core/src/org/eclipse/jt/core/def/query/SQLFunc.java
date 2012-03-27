package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.OperateExpression;
import org.eclipse.jt.core.impl.OperateExpr;
import org.eclipse.jt.core.impl.OperatorImpl;
import org.eclipse.jt.core.impl.ValueExpr;

/**
 * SQL����
 * 
 * @author Jeff Tang
 * 
 */
public class SQLFunc {

	// ---------------------------- ������ ----------------------------

	/**
	 * �������е�����
	 * 
	 * <p>
	 * ����nullֵ,ͬcount(*)
	 * 
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xCount() {
		return OperateExpr.COUNT_ASTERISK;
	}

	/**
	 * �������е�����
	 * 
	 * <p>
	 * ͬcount(all <code>value</code>)
	 * 
	 * @param value
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xCount(Object value) {
		return new OperateExpr(OperatorImpl.COUNT_ALL, ValueExpr.expOf(value));
	}

	/**
	 * �������е�����
	 * 
	 * <p>
	 * ͬcount(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xCountDistinct(Object value) {
		return new OperateExpr(OperatorImpl.COUNT_DISTINCT,
				ValueExpr.expOf(value));
	}

	/**
	 * �������и�ֵ��ƽ��ֵ
	 * 
	 * <p>
	 * ������������,����nullֵ<br>
	 * ͬavg(all <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xAvg(Object value) {
		return new OperateExpr(OperatorImpl.AVG_ALL, ValueExpr.expOf(value));
	}

	/**
	 * �������и�ֵ��ƽ��ֵ
	 * 
	 * <p>
	 * ������������,����nullֵ���ظ�ֵ<br>
	 * ͬavg(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xAvgDistinct(Object value) {
		return new OperateExpr(OperatorImpl.AVG_DISTINCT,
				ValueExpr.expOf(value));
	}

	/**
	 * �������б��ʽ�ĺ�
	 * <p>
	 * ������������,����nullֵ<br>
	 * ͬsum(all <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xSum(Object value) {
		return new OperateExpr(OperatorImpl.SUM_ALL, ValueExpr.expOf(value));
	}

	/**
	 * �������б��ʽ�ĺ�
	 * <p>
	 * ������������,����nullֵ���ظ�ֵ<br>
	 * ͬsum(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xSumDistinct(Object value) {
		return new OperateExpr(OperatorImpl.SUM_DISTINCT,
				ValueExpr.expOf(value));
	}

	/**
	 * ���ر��ʽ�е����ֵ
	 * <p>
	 * ͬmax(value)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xMax(Object value) {
		return new OperateExpr(OperatorImpl.MAX, ValueExpr.expOf(value));
	}

	/**
	 * ���ر��ʽ�е����ֵ
	 * <p>
	 * ͬmin(value)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xMin(Object value) {
		return new OperateExpr(OperatorImpl.MIN, ValueExpr.expOf(value));
	}

	/**
	 * ���ط���ϼƵı�־ֵ
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xGrouping(Object value) {
		ValueExpr expr = ValueExpr.expOf(value);
		return new OperateExpr(OperatorImpl.GROUPING, expr);
	}

	// ---------------------------- ���ں��� ----------------------------

	/**
	 * ��ȡ��ǰϵͳ����ʱ��
	 * 
	 * <p>
	 * ��ȷ������
	 * 
	 * @return ���ڱ��ʽ
	 */
	public static final OperateExpression xGetDate() {
		return OperateExpr.GET_DATE;
	}

	/**
	 * �������ڵ�����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xYearOf(Object date) {
		return new OperateExpr(OperatorImpl.YEAR_OF, ValueExpr.expOf(date));
	}

	/**
	 * �������������еļ�����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xQuarterOf(Object date) {
		return new OperateExpr(OperatorImpl.QUARTER_OF, ValueExpr.expOf(date));
	}

	/**
	 * �������������е�����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xMonthOf(Object date) {
		return new OperateExpr(OperatorImpl.MONTH_OF, ValueExpr.expOf(date));
	}

	/**
	 * �������������е�����
	 * 
	 * <p>
	 * ����Ȼ�ܼ���.������Ϊһ�ܵĵ�һ��.
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xWeekOf(Object date) {
		return new OperateExpr(OperatorImpl.WEEK_OF, ValueExpr.expOf(date));
	}

	/**
	 * �������������е�����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xDayOf(Object date) {
		return new OperateExpr(OperatorImpl.DAY_OF, ValueExpr.expOf(date));
	}

	/**
	 * �������������е�����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xDayOfYear(Object date) {
		return new OperateExpr(OperatorImpl.DAY_OF_YEAR, ValueExpr.expOf(date));
	}

	/**
	 * ������������һ���ڵ����
	 * 
	 * <ul>
	 * <li>������,����<code>1</code>
	 * <li>����һ,����<code>2</code>
	 * <li>���ڶ�,����<code>3</code>
	 * <li>������,����<code>4</code>
	 * <li>������,����<code>5</code>
	 * <li>������,����<code>6</code>
	 * <li>������,����<code>7</code>
	 * </ul>
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xDayOfWeek(Object date) {
		return new OperateExpr(OperatorImpl.DAY_OF_WEEK, ValueExpr.expOf(date));
	}

	/**
	 * �������ڵ�Сʱ��
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xHourOf(Object date) {
		return new OperateExpr(OperatorImpl.HOUR_OF, ValueExpr.expOf(date));
	}

	/**
	 * �������ڵķ���
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xMinuteOf(Object date) {
		return new OperateExpr(OperatorImpl.MINUTE_OF, ValueExpr.expOf(date));
	}

	/**
	 * �������ڵ�����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xSecondOf(Object date) {
		return new OperateExpr(OperatorImpl.SECOND_OF, ValueExpr.expOf(date));
	}

	/**
	 * �������ں�����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xMillisecondOf(Object date) {
		return new OperateExpr(OperatorImpl.MILLISECOND_OF,
				ValueExpr.expOf(date));
	}

	/**
	 * ���ڱ��ʽ��������Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return �������ͱ��ʽ
	 */
	public static final OperateExpression xAddYear(Object date, Object interval) {
		return new OperateExpr(OperatorImpl.ADD_YEAR, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * ���ڱ��ʽ�����Լ���Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return �������ͱ��ʽ
	 */
	public static final OperateExpression xAddQuarter(Object date,
			Object interval) {
		return new OperateExpr(OperatorImpl.ADD_QUARTER, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * ���ڱ��ʽ��������Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return �������ͱ��ʽ
	 */
	public static final OperateExpression xAddMonth(Object date, Object interval) {
		return new OperateExpr(OperatorImpl.ADD_MONTH, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * ���ڱ��ʽ��������Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return �������ͱ��ʽ
	 */
	public static final OperateExpression xAddWeek(Object date, Object interval) {
		return new OperateExpr(OperatorImpl.ADD_WEEK, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * ���ڱ��ʽ��������Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return �������ͱ��ʽ
	 */
	public static final OperateExpression xAddDay(Object date, Object interval) {
		return new OperateExpr(OperatorImpl.ADD_DAY, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * ���ڱ��ʽ������СʱΪ��λ��ʱ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return �������ͱ��ʽ
	 */
	public static final OperateExpression xAddHour(Object date, Object interval) {
		return new OperateExpr(OperatorImpl.ADD_HOUR, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * ���ڱ��ʽ�����Է�Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return �������ͱ��ʽ
	 */
	public static final OperateExpression xAddMinute(Object date,
			Object interval) {
		return new OperateExpr(OperatorImpl.ADD_MINUTE, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * ���ڱ��ʽ��������Ϊ��λ��ʱ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @param interval
	 *            ʱ������,���ͱ��ʽ
	 * @return �������ͱ��ʽ
	 */
	public static final OperateExpression xAddSecond(Object date,
			Object interval) {
		return new OperateExpr(OperatorImpl.ADD_SECOND, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * �����������ڵļ������
	 * 
	 * @param startDate
	 *            ��ʼ�����ڱ��ʽ
	 * @param endDate
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xYearDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(OperatorImpl.YEAR_DIFF,
				ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * �����������ڵļ��������
	 * 
	 * @param startDate
	 *            ��ʼ�����ڱ��ʽ
	 * @param startDate
	 *            ���������ڱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xQuarterDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(OperatorImpl.QUARTER_DIFF,
				ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * �����������ڵļ������
	 * 
	 * @param startDate
	 *            ��ʼ�����ڱ��ʽ
	 * @param startDate
	 *            ���������ڱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xMonthDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(OperatorImpl.MONTH_DIFF,
				ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * �����������ڵļ������
	 * 
	 * <p>
	 * Ĭ������һΪһ�ܵĵ�һ��,����ͨ����������.
	 * 
	 * @param startDate
	 *            ��ʼ�����ڱ��ʽ
	 * @param startDate
	 *            ���������ڱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xWeekDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(OperatorImpl.WEEK_DIFF,
				ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * �����������ڵļ������
	 * 
	 * @param startDate
	 *            ��ʼ�����ڱ��ʽ
	 * @param startDate
	 *            ���������ڱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	public static final OperateExpression xDayDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(OperatorImpl.DAY_DIFF,
				ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * �����������ڵļ��Сʱ��
	 * 
	 * @param startDate
	 *            ��ʼ�����ڱ��ʽ
	 * @param startDate
	 *            ���������ڱ��ʽ
	 * @return ���ͱ��ʽ
	 */
	@Deprecated
	public static final OperateExpression xHourDiff(Object startDate,
			Object endDate) {
		throw new UnsupportedOperationException("��֧��hourdiff����");
		// return new OperateExpr(OperatorImpl.HOUR_DIFF,
		// ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * �����������ڵļ��������
	 * 
	 * @param startDate
	 *            ��ʼ�����ڱ��ʽ
	 * @param startDate
	 *            ���������ڱ��ʽ
	 * @return ���ͱ��ʽ
	 * @deprecated δʵ��
	 */
	@Deprecated
	public static final OperateExpression xMinuteDiff(Object startDate,
			Object endDate) {
		throw new UnsupportedOperationException("��֧��minutediff����");
		// return new OperateExpr(OperatorImpl.MINUTE_DIFF,
		// ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * �����������ڵļ������
	 * 
	 * @param startDate
	 *            ��ʼ�����ڱ��ʽ
	 * @param startDate
	 *            ���������ڱ��ʽ
	 * @return ���ͱ��ʽ
	 * @deprecated δʵ��
	 */
	@Deprecated
	public static final OperateExpression xSecondDiff(Object startDate,
			Object endDate) {
		throw new UnsupportedOperationException("��֧��seconddiff����");
		// return new OperateExpr(OperatorImpl.SECOND_DIFF,
		// ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * ���������������һ���������������
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xTruncYear(Object date) {
		return new OperateExpr(OperatorImpl.TRUNC_YEAR, ValueExpr.expOf(date));
	}

	/**
	 * �������������µ�һ���������������
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xTruncMonth(Object date) {
		return new OperateExpr(OperatorImpl.TRUNC_MONTH, ValueExpr.expOf(date));
	}

	/**
	 * �������ڵ���������������
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xTruncDay(Object date) {
		return new OperateExpr(OperatorImpl.TRUNC_DAY, ValueExpr.expOf(date));
	}

	/**
	 * ���������������Ƿ�Ϊ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xIsLeapYear(Object date) {
		return new OperateExpr(OperatorImpl.IS_LEAP_YEAR, ValueExpr.expOf(date));
	}

	/**
	 * ���������������Ƿ�Ϊ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xIsLeapMonth(Object date) {
		return new OperateExpr(OperatorImpl.IS_LEAP_MONTH,
				ValueExpr.expOf(date));
	}

	/**
	 * ���������Ƿ�Ϊ����
	 * 
	 * @param date
	 *            �������ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xIsLeapDay(Object date) {
		return new OperateExpr(OperatorImpl.IS_LEAP_DAY, ValueExpr.expOf(date));
	}

	// ---------------------------- ��ѧ���� ----------------------------

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)������ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xSin(Object radians) {
		return new OperateExpr(OperatorImpl.SIN, ValueExpr.expOf(radians));
	}

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)������ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xCos(Object radians) {
		return new OperateExpr(OperatorImpl.COS, ValueExpr.expOf(radians));
	}

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)������ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xTan(Object radians) {
		return new OperateExpr(OperatorImpl.TAN, ValueExpr.expOf(radians));
	}

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xAsin(Object radians) {
		return new OperateExpr(OperatorImpl.ASIN, ValueExpr.expOf(radians));
	}

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xAcos(Object radians) {
		return new OperateExpr(OperatorImpl.ACOS, ValueExpr.expOf(radians));
	}

	/**
	 * ���ؽǶ�(�Ի���Ϊ��λ)�ķ�����ֵ
	 * 
	 * @param radians
	 *            (double����)
	 * @return
	 */
	public static final OperateExpression xAtan(Object radians) {
		return new OperateExpr(OperatorImpl.ATAN, ValueExpr.expOf(radians));
	}

	/**
	 * ����ָ��ֵ��ָ��ֵ
	 * 
	 * @param power
	 * @return
	 */
	public static final OperateExpression xExp(Object power) {
		return new OperateExpr(OperatorImpl.EXP, ValueExpr.expOf(power));
	}

	/**
	 * ����ָ��ֵ��ָ���ݵ�ֵ
	 * 
	 * @param base
	 *            ����
	 * @param power
	 *            ָ��
	 * @return
	 */
	public static final OperateExpression xPower(Object base, Object power) {
		return new OperateExpr(OperatorImpl.POWER, ValueExpr.expOf(base),
				ValueExpr.expOf(power));
	}

	/**
	 * ����ָ��ֵ��eΪ�׵Ķ���ֵ,����Ȼ����
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xLn(Object number) {
		return new OperateExpr(OperatorImpl.LN, ValueExpr.expOf(number));
	}

	/**
	 * ����ָ��ֵ��10Ϊ�׵Ķ���ֵ
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xLg(Object number) {
		return new OperateExpr(OperatorImpl.LG, ValueExpr.expOf(number));
	}

	/**
	 * ����ָ��ֵ��ƽ����
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xSqrt(Object number) {
		return new OperateExpr(OperatorImpl.SQRT, ValueExpr.expOf(number));
	}

	/**
	 * ���ش��ڻ����ָ��ֵ����С����
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xCeil(Object number) {
		return new OperateExpr(OperatorImpl.CEIL, ValueExpr.expOf(number));
	}

	/**
	 * ����С�ڻ����ָ��ֵ���������
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xFloor(Object number) {
		return new OperateExpr(OperatorImpl.FLOOR, ValueExpr.expOf(number));
	}

	/**
	 * ������ӽ����ʽ������
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xRound(Object number) {
		return new OperateExpr(OperatorImpl.ROUND, ValueExpr.expOf(number),
				ValueExpr.expOf(0));
	}

	/**
	 * �����ʽ���뵽ָ���ĳ��Ȼ򾫶�
	 * 
	 * @param number
	 * @param length
	 * @return
	 */
	public static final OperateExpression xRound(Object number, Object length) {
		return new OperateExpr(OperatorImpl.ROUND, ValueExpr.expOf(number),
				ValueExpr.expOf(length));
	}

	/**
	 * ���ز����ķ��ź���ֵ
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xSign(Object number) {
		return new OperateExpr(OperatorImpl.SIGN, ValueExpr.expOf(number));
	}

	/**
	 * ���ز����ľ���ֵ
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xAbs(Object number) {
		return new OperateExpr(OperatorImpl.ABS, ValueExpr.expOf(number));
	}

	// ---------------------------- �ַ������� ----------------------------

	/**
	 * ��ASCII����ת��Ϊ�ַ�
	 * 
	 * @param ascii
	 * @return
	 */
	public static final OperateExpression xChr(Object ascii) {
		return new OperateExpr(OperatorImpl.CHR, ValueExpr.expOf(ascii));
	}

	/**
	 * ���ؾ���ָ�������������Unicode�ַ�
	 * 
	 * @param national
	 * @return
	 */
	public static final OperateExpression xNchr(Object national) {
		return new OperateExpr(OperatorImpl.NCHR, ValueExpr.expOf(national));
	}

	/**
	 * �����ַ����ʽ���������ַ���ASCII����ֵ
	 * 
	 * @param str
	 * @return
	 */
	public static final OperateExpression xAscii(Object str) {
		return new OperateExpr(OperatorImpl.ASCII, ValueExpr.expOf(str));
	}

	/**
	 * ����ָ���ַ������ʽ���ַ�����������ַ������ֽ���
	 * 
	 * @param str
	 *            �ַ������ʽ��������ַ������ʽ
	 * @return
	 */
	public static final OperateExpression xLen(Object str) {
		return new OperateExpr(OperatorImpl.LEN, ValueExpr.expOf(str));
	}

	/**
	 * ����д�ַ�����ת��ΪСд�ַ����ݺ󷵻�
	 * 
	 * @param str
	 *            �ַ���
	 * @return
	 */
	public static final OperateExpression xLower(Object str) {
		return new OperateExpr(OperatorImpl.LOWER, ValueExpr.expOf(str));
	}

	/**
	 * ��Сд�ַ�����ת��Ϊ��д�ַ����ݺ󷵻�
	 * 
	 * @param str
	 *            �ַ���
	 * @return
	 */
	public static final OperateExpression xUpper(Object str) {
		return new OperateExpr(OperatorImpl.UPPER, ValueExpr.expOf(str));
	}

	/**
	 * ɾ���ַ����ʽ��ǰ���ո�
	 * 
	 * @param str
	 *            �ַ���
	 * @return
	 */
	public static final OperateExpression xLtrim(Object str) {
		return new OperateExpr(OperatorImpl.LTRIM, ValueExpr.expOf(str));
	}

	/**
	 * ɾ���ַ����ʽ��β��ո�
	 * 
	 * @param str
	 *            �ַ���
	 * @return
	 */
	public static final OperateExpression xRtrim(Object str) {
		return new OperateExpr(OperatorImpl.RTRIM, ValueExpr.expOf(str));
	}

	/**
	 * ɾ���ַ����ʽ��ǰ��Ŀո�
	 * 
	 * @param str
	 *            �ַ���
	 * @return
	 */
	public static final OperateExpression xTrim(Object str) {
		return new OperateExpr(OperatorImpl.TRIM, ValueExpr.expOf(str));
	}

	/**
	 * �����ַ�����ָ�����ʽ�Ŀ�ʼλ��
	 * 
	 * @param str
	 *            �ַ���
	 * @param search
	 *            �����ַ���
	 * @return ��Ŵ�1��ʼ
	 */
	public static final OperateExpression xIndexOf(Object str, Object search) {
		return new OperateExpr(OperatorImpl.INDEXOF, ValueExpr.expOf(str),
				ValueExpr.expOf(search), ValueExpr.expOf(1));
	}

	/**
	 * �����ַ�����ָ�����ʽ�Ŀ�ʼλ��
	 * 
	 * @param str
	 *            �ַ���
	 * @param search
	 *            �����ַ���
	 * @param position
	 *            ��ʼ����λ��
	 * @return ��Ŵ�1��ʼ
	 */
	public static final OperateExpression xIndexOf(Object str, Object search,
			Object position) {
		return new OperateExpr(OperatorImpl.INDEXOF, ValueExpr.expOf(str),
				ValueExpr.expOf(search), ValueExpr.expOf(position));
	}

	/**
	 * �����ַ���������Ʊ��ʽ���Ӵ�
	 * 
	 * @param str
	 *            �ַ�����������ַ������ʽ
	 * @param position
	 *            ��ʼ�ضϵ�λ��
	 * @return
	 */
	public static final OperateExpression xSubstr(Object str, Object position) {
		return new OperateExpr(OperatorImpl.SUBSTR, ValueExpr.expOf(str),
				ValueExpr.expOf(position));
	}

	/**
	 * �����ַ���������Ʊ��ʽ���Ӵ�
	 * 
	 * @param str
	 *            �ַ�����������ַ������ʽ
	 * @param position
	 *            ��ʼ�ضϵ�λ��
	 * @param length
	 *            �ضϵĳ���
	 * @return
	 */
	public static final OperateExpression xSubstr(Object str, Object position,
			Object length) {
		return new OperateExpr(OperatorImpl.SUBSTR, ValueExpr.expOf(str),
				ValueExpr.expOf(position), ValueExpr.expOf(length));
	}

	/**
	 * ʹ���ַ����滻ָ���ַ����е�ƥ���
	 * 
	 * @param str
	 *            Ҫ�������ַ���
	 * @param search
	 *            Ҫ���ҵ��ַ���
	 * @param replacement
	 *            �滻���ַ���
	 * @return
	 */
	public static final OperateExpression xReplace(Object str, Object search,
			Object replacement) {
		return new OperateExpr(OperatorImpl.REPLACE, ValueExpr.expOf(str),
				ValueExpr.expOf(search), ValueExpr.expOf(replacement));
	}

	/**
	 * Ŀ����ʽת��Ϊ�ַ���
	 * 
	 * @param value
	 *            �ַ���,������,����,GUID,��ֵ���ͱ��ʽ
	 * @return
	 */
	public static final OperateExpression xToChar(Object value) {
		return new OperateExpr(OperatorImpl.TO_CHAR, ValueExpr.expOf(value));
	}

	/**
	 * ����RECID
	 * 
	 * <p>
	 * ��ȷ������
	 * 
	 * @return
	 */
	public static final OperateExpression xNewRecid() {
		return OperateExpr.NEW_RECID;
	}

	/**
	 * �ַ���ת��Ϊ����
	 * 
	 * @param str
	 *            �ַ������ʽ
	 * @return
	 */
	public static final OperateExpression xToInt(Object str) {
		return new OperateExpr(OperatorImpl.TO_INT, ValueExpr.expOf(str));
	}

	private SQLFunc() {
	}
}
