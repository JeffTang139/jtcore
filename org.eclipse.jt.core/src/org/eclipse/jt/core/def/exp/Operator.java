package org.eclipse.jt.core.def.exp;

import org.eclipse.jt.core.impl.OperatorImpl;

/**
 * 运算符基类
 * 
 * <p>
 * 包括算术运算,sql日期函数,sql字符串函数,sql数学函数及其他返回为值表达式的各种运算
 * 
 * @author Jeff Tang
 * 
 */
public interface Operator {

	public static final Operator ADD = OperatorImpl.ADD;

	public static final Operator SUB = OperatorImpl.SUB;

	public static final Operator MUL = OperatorImpl.MUL;

	public static final Operator DIV = OperatorImpl.DIV;

	public static final Operator MINUS = OperatorImpl.MINUS;

	public static final Operator SIMPLE_CASE = OperatorImpl.SIMPLE_CASE;

	public static final Operator STR_CONCAT = OperatorImpl.STR_CONCAT;

	public static final Operator COALESCE = OperatorImpl.COALESCE;

	public static final Operator COUNT_ALL = OperatorImpl.COUNT_ALL;

	public static final Operator COUNT_DISTINCT = OperatorImpl.COUNT_DISTINCT;

	public static final Operator COUNT_ASTERISK = OperatorImpl.COUNT_ASTERISK;

	public static final Operator AVG_ALL = OperatorImpl.AVG_ALL;

	public static final Operator AVG_DISTINCT = OperatorImpl.AVG_DISTINCT;

	public static final Operator SUM_ALL = OperatorImpl.SUM_ALL;

	public static final Operator SUM_DISTINCT = OperatorImpl.SUM_DISTINCT;

	public static final Operator MIN = OperatorImpl.MIN;

	public static final Operator MAX = OperatorImpl.MAX;

	public static final Operator GETDATE = OperatorImpl.GETDATE;

	public static final Operator YEAR_OF = OperatorImpl.YEAR_OF;

	public static final Operator QUARTER_OF = OperatorImpl.QUARTER_OF;

	public static final Operator MONTH_OF = OperatorImpl.MONTH_OF;

	public static final Operator WEEK_OF = OperatorImpl.WEEK_OF;

	public static final Operator DAY_OF = OperatorImpl.DAY_OF;

	public static final Operator DAY_OF_YEAR = OperatorImpl.DAY_OF_YEAR;

	public static final Operator DAY_OF_WEEK = OperatorImpl.DAY_OF_WEEK;

	public static final Operator HOUR_OF = OperatorImpl.HOUR_OF;

	public static final Operator MINUTE_OF = OperatorImpl.MINUTE_OF;

	public static final Operator SECOND_OF = OperatorImpl.SECOND_OF;

	public static final Operator MILLISECOND_OF = OperatorImpl.MILLISECOND_OF;

	public static final Operator ADD_YEAR = OperatorImpl.ADD_YEAR;

	public static final Operator ADD_QUARTER = OperatorImpl.ADD_QUARTER;

	public static final Operator ADD_MONTH = OperatorImpl.ADD_MONTH;

	public static final Operator ADD_WEEK = OperatorImpl.ADD_WEEK;

	public static final Operator ADD_DAY = OperatorImpl.ADD_DAY;

	public static final Operator ADD_HOUR = OperatorImpl.ADD_HOUR;

	public static final Operator ADD_MINUTE = OperatorImpl.ADD_MINUTE;

	public static final Operator ADD_SECOND = OperatorImpl.ADD_SECOND;

	public static final Operator YEAR_DIFF = OperatorImpl.YEAR_DIFF;

	public static final Operator QUARTER_DIFF = OperatorImpl.QUARTER_DIFF;

	public static final Operator MONTH_DIFF = OperatorImpl.MONTH_DIFF;

	public static final Operator DAY_DIFF = OperatorImpl.DAY_DIFF;

	public static final Operator WEEK_DIFF = OperatorImpl.WEEK_DIFF;

	@Deprecated
	public static final Operator HOUR_DIFF = OperatorImpl.HOUR_DIFF;

	@Deprecated
	public static final Operator MINUTE_DIFF = OperatorImpl.MINUTE_DIFF;

	@Deprecated
	public static final Operator SECOND_DIFF = OperatorImpl.SECOND_DIFF;

	public static final Operator IS_LEAP_YEAR = OperatorImpl.IS_LEAP_YEAR;

	public static final Operator IS_LEAP_MONTH = OperatorImpl.IS_LEAP_MONTH;

	public static final Operator IS_LEAP_DAY = OperatorImpl.IS_LEAP_DAY;

	public static final Operator SIN = OperatorImpl.SIN;

	public static final Operator COS = OperatorImpl.COS;

	public static final Operator TAN = OperatorImpl.TAN;

	public static final Operator ASIN = OperatorImpl.ASIN;

	public static final Operator ACOS = OperatorImpl.ACOS;

	public static final Operator ATAN = OperatorImpl.ATAN;

	public static final Operator POWER = OperatorImpl.POWER;

	public static final Operator EXP = OperatorImpl.EXP;

	public static final Operator LOG10 = OperatorImpl.LG;

	public static final Operator SQRT = OperatorImpl.SQRT;

	public static final Operator CEIL = OperatorImpl.CEIL;

	public static final Operator FLOOR = OperatorImpl.FLOOR;

	public static final Operator ROUND = OperatorImpl.ROUND;

	public static final Operator SIGN = OperatorImpl.SIGN;

	public static final Operator ABS = OperatorImpl.ABS;

	public static final Operator CHR = OperatorImpl.CHR;

	public static final Operator NCHR = OperatorImpl.NCHR;

	public static final Operator ASCII = OperatorImpl.EXP;

	public static final Operator LEN = OperatorImpl.LEN;

	public static final Operator INDEXOF = OperatorImpl.INDEXOF;

	public static final Operator UPPER = OperatorImpl.UPPER;

	public static final Operator LOWER = OperatorImpl.LOWER;

	public static final Operator LTRIM = OperatorImpl.LTRIM;

	public static final Operator RTRIM = OperatorImpl.RTRIM;

	public static final Operator TRIM = OperatorImpl.TRIM;

	public static final Operator REPLACE = OperatorImpl.REPLACE;

	public static final Operator SUBSTR = OperatorImpl.SUBSTR;

}
