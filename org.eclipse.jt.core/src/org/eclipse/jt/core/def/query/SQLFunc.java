package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.OperateExpression;
import org.eclipse.jt.core.impl.OperateExpr;
import org.eclipse.jt.core.impl.OperatorImpl;
import org.eclipse.jt.core.impl.ValueExpr;

/**
 * SQL函数
 * 
 * @author Jeff Tang
 * 
 */
public class SQLFunc {

	// ---------------------------- 集函数 ----------------------------

	/**
	 * 返回组中的项数
	 * 
	 * <p>
	 * 包括null值,同count(*)
	 * 
	 * @return 整型表达式
	 */
	public static final OperateExpression xCount() {
		return OperateExpr.COUNT_ASTERISK;
	}

	/**
	 * 返回组中的项数
	 * 
	 * <p>
	 * 同count(all <code>value</code>)
	 * 
	 * @param value
	 * @return 整型表达式
	 */
	public static final OperateExpression xCount(Object value) {
		return new OperateExpr(OperatorImpl.COUNT_ALL, ValueExpr.expOf(value));
	}

	/**
	 * 返回组中的项数
	 * 
	 * <p>
	 * 同count(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return 整型表达式
	 */
	public static final OperateExpression xCountDistinct(Object value) {
		return new OperateExpr(OperatorImpl.COUNT_DISTINCT,
				ValueExpr.expOf(value));
	}

	/**
	 * 返回组中各值的平均值
	 * 
	 * <p>
	 * 仅用于数字列,忽略null值<br>
	 * 同avg(all <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xAvg(Object value) {
		return new OperateExpr(OperatorImpl.AVG_ALL, ValueExpr.expOf(value));
	}

	/**
	 * 返回组中各值的平均值
	 * 
	 * <p>
	 * 仅用于数字列,忽略null值及重复值<br>
	 * 同avg(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xAvgDistinct(Object value) {
		return new OperateExpr(OperatorImpl.AVG_DISTINCT,
				ValueExpr.expOf(value));
	}

	/**
	 * 返回组中表达式的和
	 * <p>
	 * 仅用于数字列,忽略null值<br>
	 * 同sum(all <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xSum(Object value) {
		return new OperateExpr(OperatorImpl.SUM_ALL, ValueExpr.expOf(value));
	}

	/**
	 * 返回组中表达式的和
	 * <p>
	 * 仅用于数字列,忽略null值及重复值<br>
	 * 同sum(distinct <code>value</code>)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xSumDistinct(Object value) {
		return new OperateExpr(OperatorImpl.SUM_DISTINCT,
				ValueExpr.expOf(value));
	}

	/**
	 * 返回表达式中的最大值
	 * <p>
	 * 同max(value)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xMax(Object value) {
		return new OperateExpr(OperatorImpl.MAX, ValueExpr.expOf(value));
	}

	/**
	 * 返回表达式中的最大值
	 * <p>
	 * 同min(value)
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xMin(Object value) {
		return new OperateExpr(OperatorImpl.MIN, ValueExpr.expOf(value));
	}

	/**
	 * 返回分组合计的标志值
	 * 
	 * @param value
	 * @return
	 */
	public static final OperateExpression xGrouping(Object value) {
		ValueExpr expr = ValueExpr.expOf(value);
		return new OperateExpr(OperatorImpl.GROUPING, expr);
	}

	// ---------------------------- 日期函数 ----------------------------

	/**
	 * 获取当前系统日期时间
	 * 
	 * <p>
	 * 不确定函数
	 * 
	 * @return 日期表达式
	 */
	public static final OperateExpression xGetDate() {
		return OperateExpr.GET_DATE;
	}

	/**
	 * 返回日期的年数
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xYearOf(Object date) {
		return new OperateExpr(OperatorImpl.YEAR_OF, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期在年中的季度数
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xQuarterOf(Object date) {
		return new OperateExpr(OperatorImpl.QUARTER_OF, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期在年中的月数
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xMonthOf(Object date) {
		return new OperateExpr(OperatorImpl.MONTH_OF, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期在年中的周数
	 * 
	 * <p>
	 * 以自然周计算.星期天为一周的第一天.
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xWeekOf(Object date) {
		return new OperateExpr(OperatorImpl.WEEK_OF, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期在月中的天数
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xDayOf(Object date) {
		return new OperateExpr(OperatorImpl.DAY_OF, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期在年中的天数
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xDayOfYear(Object date) {
		return new OperateExpr(OperatorImpl.DAY_OF_YEAR, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期与在一周内的序号
	 * 
	 * <ul>
	 * <li>星期天,返回<code>1</code>
	 * <li>星期一,返回<code>2</code>
	 * <li>星期二,返回<code>3</code>
	 * <li>星期三,返回<code>4</code>
	 * <li>星期四,返回<code>5</code>
	 * <li>星期五,返回<code>6</code>
	 * <li>星期六,返回<code>7</code>
	 * </ul>
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xDayOfWeek(Object date) {
		return new OperateExpr(OperatorImpl.DAY_OF_WEEK, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期的小时数
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xHourOf(Object date) {
		return new OperateExpr(OperatorImpl.HOUR_OF, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期的分数
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xMinuteOf(Object date) {
		return new OperateExpr(OperatorImpl.MINUTE_OF, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期的秒数
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xSecondOf(Object date) {
		return new OperateExpr(OperatorImpl.SECOND_OF, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期毫秒数
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xMillisecondOf(Object date) {
		return new OperateExpr(OperatorImpl.MILLISECOND_OF,
				ValueExpr.expOf(date));
	}

	/**
	 * 日期表达式增加以年为单位的时间间隔
	 * 
	 * @param date
	 *            日期类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期类型表达式
	 */
	public static final OperateExpression xAddYear(Object date, Object interval) {
		return new OperateExpr(OperatorImpl.ADD_YEAR, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * 日期表达式增加以季度为单位的时间间隔
	 * 
	 * @param date
	 *            日期类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期类型表达式
	 */
	public static final OperateExpression xAddQuarter(Object date,
			Object interval) {
		return new OperateExpr(OperatorImpl.ADD_QUARTER, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * 日期表达式增加以月为单位的时间间隔
	 * 
	 * @param date
	 *            日期类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期类型表达式
	 */
	public static final OperateExpression xAddMonth(Object date, Object interval) {
		return new OperateExpr(OperatorImpl.ADD_MONTH, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * 日期表达式增加以周为单位的时间间隔
	 * 
	 * @param date
	 *            日期类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期类型表达式
	 */
	public static final OperateExpression xAddWeek(Object date, Object interval) {
		return new OperateExpr(OperatorImpl.ADD_WEEK, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * 日期表达式增加以天为单位的时间间隔
	 * 
	 * @param date
	 *            日期类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期类型表达式
	 */
	public static final OperateExpression xAddDay(Object date, Object interval) {
		return new OperateExpr(OperatorImpl.ADD_DAY, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * 日期表达式增加以小时为单位的时间间隔
	 * 
	 * @param date
	 *            日期类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期类型表达式
	 */
	public static final OperateExpression xAddHour(Object date, Object interval) {
		return new OperateExpr(OperatorImpl.ADD_HOUR, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * 日期表达式增加以分为单位的时间间隔
	 * 
	 * @param date
	 *            日期类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期类型表达式
	 */
	public static final OperateExpression xAddMinute(Object date,
			Object interval) {
		return new OperateExpr(OperatorImpl.ADD_MINUTE, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * 日期表达式增加以秒为单位的时间间隔
	 * 
	 * @param date
	 *            日期类型表达式
	 * @param interval
	 *            时间间隔数,整型表达式
	 * @return 日期类型表达式
	 */
	public static final OperateExpression xAddSecond(Object date,
			Object interval) {
		return new OperateExpr(OperatorImpl.ADD_SECOND, ValueExpr.expOf(date),
				ValueExpr.expOf(interval));
	}

	/**
	 * 计算两个日期的间隔年数
	 * 
	 * @param startDate
	 *            开始的日期表达式
	 * @param endDate
	 * @return 整型表达式
	 */
	public static final OperateExpression xYearDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(OperatorImpl.YEAR_DIFF,
				ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * 计算两个日期的间隔季度数
	 * 
	 * @param startDate
	 *            开始的日期表达式
	 * @param startDate
	 *            结束的日期表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xQuarterDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(OperatorImpl.QUARTER_DIFF,
				ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * 计算两个日期的间隔月数
	 * 
	 * @param startDate
	 *            开始的日期表达式
	 * @param startDate
	 *            结束的日期表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xMonthDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(OperatorImpl.MONTH_DIFF,
				ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * 计算两个日期的间隔周数
	 * 
	 * <p>
	 * 默认星期一为一周的第一天,可以通过参数设置.
	 * 
	 * @param startDate
	 *            开始的日期表达式
	 * @param startDate
	 *            结束的日期表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xWeekDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(OperatorImpl.WEEK_DIFF,
				ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * 计算两个日期的间隔天数
	 * 
	 * @param startDate
	 *            开始的日期表达式
	 * @param startDate
	 *            结束的日期表达式
	 * @return 整型表达式
	 */
	public static final OperateExpression xDayDiff(Object startDate,
			Object endDate) {
		return new OperateExpr(OperatorImpl.DAY_DIFF,
				ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * 计算两个日期的间隔小时数
	 * 
	 * @param startDate
	 *            开始的日期表达式
	 * @param startDate
	 *            结束的日期表达式
	 * @return 整型表达式
	 */
	@Deprecated
	public static final OperateExpression xHourDiff(Object startDate,
			Object endDate) {
		throw new UnsupportedOperationException("不支持hourdiff函数");
		// return new OperateExpr(OperatorImpl.HOUR_DIFF,
		// ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * 计算两个日期的间隔分钟数
	 * 
	 * @param startDate
	 *            开始的日期表达式
	 * @param startDate
	 *            结束的日期表达式
	 * @return 整型表达式
	 * @deprecated 未实现
	 */
	@Deprecated
	public static final OperateExpression xMinuteDiff(Object startDate,
			Object endDate) {
		throw new UnsupportedOperationException("不支持minutediff函数");
		// return new OperateExpr(OperatorImpl.MINUTE_DIFF,
		// ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * 计算两个日期的间隔秒数
	 * 
	 * @param startDate
	 *            开始的日期表达式
	 * @param startDate
	 *            结束的日期表达式
	 * @return 整型表达式
	 * @deprecated 未实现
	 */
	@Deprecated
	public static final OperateExpression xSecondDiff(Object startDate,
			Object endDate) {
		throw new UnsupportedOperationException("不支持seconddiff函数");
		// return new OperateExpr(OperatorImpl.SECOND_DIFF,
		// ValueExpr.expOf(startDate), ValueExpr.expOf(endDate));
	}

	/**
	 * 返回日期所在年第一天的零分零秒的日期
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return
	 */
	public static final OperateExpression xTruncYear(Object date) {
		return new OperateExpr(OperatorImpl.TRUNC_YEAR, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期所在月第一天的零分零秒的日期
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return
	 */
	public static final OperateExpression xTruncMonth(Object date) {
		return new OperateExpr(OperatorImpl.TRUNC_MONTH, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期当天零分零秒的日期
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return
	 */
	public static final OperateExpression xTruncDay(Object date) {
		return new OperateExpr(OperatorImpl.TRUNC_DAY, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期所在年是否为闰年
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return
	 */
	public static final OperateExpression xIsLeapYear(Object date) {
		return new OperateExpr(OperatorImpl.IS_LEAP_YEAR, ValueExpr.expOf(date));
	}

	/**
	 * 返回日期所在月是否为闰月
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return
	 */
	public static final OperateExpression xIsLeapMonth(Object date) {
		return new OperateExpr(OperatorImpl.IS_LEAP_MONTH,
				ValueExpr.expOf(date));
	}

	/**
	 * 返回日期是否为闰日
	 * 
	 * @param date
	 *            日期类型表达式
	 * @return
	 */
	public static final OperateExpression xIsLeapDay(Object date) {
		return new OperateExpr(OperatorImpl.IS_LEAP_DAY, ValueExpr.expOf(date));
	}

	// ---------------------------- 数学函数 ----------------------------

	/**
	 * 返回角度(以弧度为单位)的正弦值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xSin(Object radians) {
		return new OperateExpr(OperatorImpl.SIN, ValueExpr.expOf(radians));
	}

	/**
	 * 返回角度(以弧度为单位)的余弦值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xCos(Object radians) {
		return new OperateExpr(OperatorImpl.COS, ValueExpr.expOf(radians));
	}

	/**
	 * 返回角度(以弧度为单位)的正切值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xTan(Object radians) {
		return new OperateExpr(OperatorImpl.TAN, ValueExpr.expOf(radians));
	}

	/**
	 * 返回角度(以弧度为单位)的反正弦值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xAsin(Object radians) {
		return new OperateExpr(OperatorImpl.ASIN, ValueExpr.expOf(radians));
	}

	/**
	 * 返回角度(以弧度为单位)的反余弦值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xAcos(Object radians) {
		return new OperateExpr(OperatorImpl.ACOS, ValueExpr.expOf(radians));
	}

	/**
	 * 返回角度(以弧度为单位)的反正切值
	 * 
	 * @param radians
	 *            (double类型)
	 * @return
	 */
	public static final OperateExpression xAtan(Object radians) {
		return new OperateExpr(OperatorImpl.ATAN, ValueExpr.expOf(radians));
	}

	/**
	 * 返回指定值的指数值
	 * 
	 * @param power
	 * @return
	 */
	public static final OperateExpression xExp(Object power) {
		return new OperateExpr(OperatorImpl.EXP, ValueExpr.expOf(power));
	}

	/**
	 * 返回指定值的指定幂的值
	 * 
	 * @param base
	 *            底数
	 * @param power
	 *            指数
	 * @return
	 */
	public static final OperateExpression xPower(Object base, Object power) {
		return new OperateExpr(OperatorImpl.POWER, ValueExpr.expOf(base),
				ValueExpr.expOf(power));
	}

	/**
	 * 返回指定值以e为底的对数值,即自然对数
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xLn(Object number) {
		return new OperateExpr(OperatorImpl.LN, ValueExpr.expOf(number));
	}

	/**
	 * 返回指定值以10为底的对数值
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xLg(Object number) {
		return new OperateExpr(OperatorImpl.LG, ValueExpr.expOf(number));
	}

	/**
	 * 返回指定值的平方根
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xSqrt(Object number) {
		return new OperateExpr(OperatorImpl.SQRT, ValueExpr.expOf(number));
	}

	/**
	 * 返回大于或等于指定值的最小整数
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xCeil(Object number) {
		return new OperateExpr(OperatorImpl.CEIL, ValueExpr.expOf(number));
	}

	/**
	 * 返回小于或等于指定值的最大整数
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xFloor(Object number) {
		return new OperateExpr(OperatorImpl.FLOOR, ValueExpr.expOf(number));
	}

	/**
	 * 返回最接近表达式的整数
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xRound(Object number) {
		return new OperateExpr(OperatorImpl.ROUND, ValueExpr.expOf(number),
				ValueExpr.expOf(0));
	}

	/**
	 * 将表达式舍入到指定的长度或精度
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
	 * 返回参数的符号函数值
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xSign(Object number) {
		return new OperateExpr(OperatorImpl.SIGN, ValueExpr.expOf(number));
	}

	/**
	 * 返回参数的绝对值
	 * 
	 * @param number
	 * @return
	 */
	public static final OperateExpression xAbs(Object number) {
		return new OperateExpr(OperatorImpl.ABS, ValueExpr.expOf(number));
	}

	// ---------------------------- 字符串函数 ----------------------------

	/**
	 * 将ASCII代码转换为字符
	 * 
	 * @param ascii
	 * @return
	 */
	public static final OperateExpression xChr(Object ascii) {
		return new OperateExpr(OperatorImpl.CHR, ValueExpr.expOf(ascii));
	}

	/**
	 * 返回具有指定的整数代码的Unicode字符
	 * 
	 * @param national
	 * @return
	 */
	public static final OperateExpression xNchr(Object national) {
		return new OperateExpr(OperatorImpl.NCHR, ValueExpr.expOf(national));
	}

	/**
	 * 返回字符表达式中最左侧的字符的ASCII代码值
	 * 
	 * @param str
	 * @return
	 */
	public static final OperateExpression xAscii(Object str) {
		return new OperateExpr(OperatorImpl.ASCII, ValueExpr.expOf(str));
	}

	/**
	 * 返回指定字符串表达式的字符数或二进制字符串的字节数
	 * 
	 * @param str
	 *            字符串表达式或二进制字符串表达式
	 * @return
	 */
	public static final OperateExpression xLen(Object str) {
		return new OperateExpr(OperatorImpl.LEN, ValueExpr.expOf(str));
	}

	/**
	 * 将大写字符数据转换为小写字符数据后返回
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static final OperateExpression xLower(Object str) {
		return new OperateExpr(OperatorImpl.LOWER, ValueExpr.expOf(str));
	}

	/**
	 * 将小写字符数据转换为大写字符数据后返回
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static final OperateExpression xUpper(Object str) {
		return new OperateExpr(OperatorImpl.UPPER, ValueExpr.expOf(str));
	}

	/**
	 * 删除字符表达式的前导空格
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static final OperateExpression xLtrim(Object str) {
		return new OperateExpr(OperatorImpl.LTRIM, ValueExpr.expOf(str));
	}

	/**
	 * 删除字符表达式的尾随空格
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static final OperateExpression xRtrim(Object str) {
		return new OperateExpr(OperatorImpl.RTRIM, ValueExpr.expOf(str));
	}

	/**
	 * 删除字符表达式的前后的空格
	 * 
	 * @param str
	 *            字符串
	 * @return
	 */
	public static final OperateExpression xTrim(Object str) {
		return new OperateExpr(OperatorImpl.TRIM, ValueExpr.expOf(str));
	}

	/**
	 * 返回字符串中指定表达式的开始位置
	 * 
	 * @param str
	 *            字符串
	 * @param search
	 *            查找字符串
	 * @return 序号从1开始
	 */
	public static final OperateExpression xIndexOf(Object str, Object search) {
		return new OperateExpr(OperatorImpl.INDEXOF, ValueExpr.expOf(str),
				ValueExpr.expOf(search), ValueExpr.expOf(1));
	}

	/**
	 * 返回字符串中指定表达式的开始位置
	 * 
	 * @param str
	 *            字符串
	 * @param search
	 *            查找字符串
	 * @param position
	 *            开始查找位置
	 * @return 序号从1开始
	 */
	public static final OperateExpression xIndexOf(Object str, Object search,
			Object position) {
		return new OperateExpr(OperatorImpl.INDEXOF, ValueExpr.expOf(str),
				ValueExpr.expOf(search), ValueExpr.expOf(position));
	}

	/**
	 * 返回字符串或二进制表达式的子串
	 * 
	 * @param str
	 *            字符串或二进制字符串表达式
	 * @param position
	 *            开始截断的位置
	 * @return
	 */
	public static final OperateExpression xSubstr(Object str, Object position) {
		return new OperateExpr(OperatorImpl.SUBSTR, ValueExpr.expOf(str),
				ValueExpr.expOf(position));
	}

	/**
	 * 返回字符串或二进制表达式的子串
	 * 
	 * @param str
	 *            字符串或二进制字符串表达式
	 * @param position
	 *            开始截断的位置
	 * @param length
	 *            截断的长度
	 * @return
	 */
	public static final OperateExpression xSubstr(Object str, Object position,
			Object length) {
		return new OperateExpr(OperatorImpl.SUBSTR, ValueExpr.expOf(str),
				ValueExpr.expOf(position), ValueExpr.expOf(length));
	}

	/**
	 * 使用字符串替换指定字符串中的匹配段
	 * 
	 * @param str
	 *            要搜索的字符串
	 * @param search
	 *            要查找的字符串
	 * @param replacement
	 *            替换的字符串
	 * @return
	 */
	public static final OperateExpression xReplace(Object str, Object search,
			Object replacement) {
		return new OperateExpr(OperatorImpl.REPLACE, ValueExpr.expOf(str),
				ValueExpr.expOf(search), ValueExpr.expOf(replacement));
	}

	/**
	 * 目标表达式转换为字符串
	 * 
	 * @param value
	 *            字符串,二进制,日期,GUID,数值类型表达式
	 * @return
	 */
	public static final OperateExpression xToChar(Object value) {
		return new OperateExpr(OperatorImpl.TO_CHAR, ValueExpr.expOf(value));
	}

	/**
	 * 创建RECID
	 * 
	 * <p>
	 * 不确定函数
	 * 
	 * @return
	 */
	public static final OperateExpression xNewRecid() {
		return OperateExpr.NEW_RECID;
	}

	/**
	 * 字符串转换为整型
	 * 
	 * @param str
	 *            字符串表达式
	 * @return
	 */
	public static final OperateExpression xToInt(Object str) {
		return new OperateExpr(OperatorImpl.TO_INT, ValueExpr.expOf(str));
	}

	private SQLFunc() {
	}
}
