package org.eclipse.jt.core.da;

import org.eclipse.jt.core.def.exp.OperateExpression;
import org.eclipse.jt.core.def.query.SQLFunc;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.BooleanType;
import org.eclipse.jt.core.impl.BytesType;
import org.eclipse.jt.core.impl.DateType;
import org.eclipse.jt.core.impl.DoubleType;
import org.eclipse.jt.core.impl.GUIDType;
import org.eclipse.jt.core.impl.IntType;
import org.eclipse.jt.core.impl.LongType;
import org.eclipse.jt.core.impl.StringType;
import org.eclipse.jt.core.type.AssignCapability;
import org.eclipse.jt.core.type.DataType;

/**
 * DNA-SQL支持函数的规格
 * 
 * @author Jeff Tang
 * 
 */
public enum SQLFuncSpec {

	// ---------------------- 日期函数 ----------------------

	getdate(new SQLFuncPattern("获取服务器端当前日期时间", DateType.TYPE) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xGetDate();
		}
	}),

	yearof(new SQLFuncPattern("获取日期表达式年数", IntType.TYPE, ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xYearOf(values[0]);
		}
	}),

	quarterof(new SQLFuncPattern("返回日期表达式在年中的季度数", IntType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xQuarterOf(values[0]);
		}
	}),

	monthof(
			new SQLFuncPattern("返回日期表达式在年中的月数", IntType.TYPE, ArgumentSpec.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xMonthOf(values[0]);
				}
			}),

	weekof(
			new SQLFuncPattern("返回日期表达式的在年中的周数", IntType.TYPE,
					ArgumentSpec.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xWeekOf(values[0]);
				}
			}),

	dayof(
			new SQLFuncPattern("返回日期表达式的在月中的天数", IntType.TYPE,
					ArgumentSpec.date) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xDayOf(values[0]);
				}
			}),

	dayofyear(new SQLFuncPattern("返回日期表达式的在年中的天数", IntType.TYPE,
			ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xDayOfYear(values[0]);
		}
	}),

	dayofweek(new SQLFuncPattern("返回日期表达式的在周中的天数", IntType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xDayOfWeek(values[0]);
		}
	}),

	hourof(new SQLFuncPattern("返回日期表达式的小时数", IntType.TYPE, ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xHourOf(values[0]);
		}
	}),

	minuteof(new SQLFuncPattern("返回日期表达式的分数", IntType.TYPE, ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xMinuteOf(values[0]);
		}
	}),

	secondof(new SQLFuncPattern("返回日期表达式的秒数", IntType.TYPE, ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSecondOf(values[0]);
		}
	}),

	millisecondof(new SQLFuncPattern("返回日期表达式的毫秒数", IntType.TYPE,
			ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xMillisecondOf(values[0]);
		}
	}),

	addyear(new SQLFuncPattern("日期表达式增加以年为单位的时间间隔", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddYear(values[0], values[1]);
		}
	}),

	addquarter(new SQLFuncPattern("日期表达式增加以季度为单位的时间间隔", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddQuarter(values[0], values[1]);
		}
	}),

	addmonth(new SQLFuncPattern("日期表达式增加以月为单位的时间间隔", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddMonth(values[0], values[1]);
		}
	}),

	addweek(new SQLFuncPattern("日期表达式增加以周为单位的时间间隔", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddWeek(values[0], values[1]);
		}
	}),

	addday(new SQLFuncPattern("日期表达式增加以天为单位的时间间隔", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddDay(values[0], values[1]);
		}
	}),

	addhour(new SQLFuncPattern("日期表达式增加以小时为单位的时间间隔", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddHour(values[0], values[1]);
		}
	}),

	addminute(new SQLFuncPattern("日期表达式增加以分为单位的时间间隔", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddMinute(values[0], values[1]);
		}
	}),

	addsecond(new SQLFuncPattern("日期表达式增加以秒为单位的时间间隔", DateType.TYPE,
			ArgumentSpec.date, ArgumentSpec.interval) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAddSecond(values[0], values[1]);
		}

	}),

	yeardiff(new SQLFuncPattern("计算两个日期的间隔年数", IntType.TYPE,
			ArgumentSpec.startdate, ArgumentSpec.enddate) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xYearDiff(values[0], values[1]);
		}

	}),

	quarterdiff(new SQLFuncPattern("计算两个日期的间隔季度数", IntType.TYPE,
			ArgumentSpec.startdate, ArgumentSpec.enddate) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xQuarterDiff(values[0], values[1]);
		}

	}),

	monthdiff(new SQLFuncPattern("计算两个日期的间隔月数", IntType.TYPE,
			ArgumentSpec.startdate, ArgumentSpec.enddate) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xMonthDiff(values[0], values[1]);
		}

	}),

	daydiff(new SQLFuncPattern("计算两个日期的间隔天数", IntType.TYPE,
			ArgumentSpec.startdate, ArgumentSpec.enddate) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xDayDiff(values[0], values[1]);
		}

	}),

	weekdiff(new SQLFuncPattern("计算两个日期的间隔周数", IntType.TYPE,
			ArgumentSpec.startdate, ArgumentSpec.enddate) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xWeekDiff(values[0], values[1]);
		}

	}),

	// hourdiff(new SQLFuncPattern("计算两个日期的间隔小时数", IntType.TYPE,
	// ArgumentSpec.startdate, ArgumentSpec.enddate) {
	//
	// @Override
	// public OperateExpression expOf(Object[] values) {
	// return SQLFunc.xHourDiff(values[0], values[1]);
	// }
	//
	// }),
	//
	// minutediff(new SQLFuncPattern("计算两个日期的间隔分数", IntType.TYPE,
	// ArgumentSpec.startdate, ArgumentSpec.enddate) {
	//
	// @Override
	// public OperateExpression expOf(Object[] values) {
	// return SQLFunc.xMinuteDiff(values[0], values[1]);
	// }
	//
	// }),
	//
	// seconddiff(new SQLFuncPattern("计算两个日期的间隔秒数", IntType.TYPE,
	// ArgumentSpec.startdate, ArgumentSpec.enddate) {
	//
	// @Override
	// public OperateExpression expOf(Object[] values) {
	// return SQLFunc.xSecondDiff(values[0], values[1]);
	// }
	//
	// }),

	truncyear(new SQLFuncPattern("截取日期到当年第一天的零分零秒", DateType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xTruncYear(values[0]);
		}
	}),

	truncmonth(new SQLFuncPattern("截取日期到当月第一天的零分零秒", DateType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xTruncMonth(values[0]);
		}
	}),

	truncday(new SQLFuncPattern("截取日期到当天的零分零秒", DateType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xTruncDay(values[0]);
		}
	}),

	isleapyear(
			new SQLFuncPattern("日期是否闰年", BooleanType.TYPE, ArgumentSpec.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xIsLeapYear(values[0]);
				}

			}),

	isleapmonth(new SQLFuncPattern("日期是否闰月", BooleanType.TYPE,
			ArgumentSpec.date) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xIsLeapMonth(values[0]);
		}

	}),

	isleapday(
			new SQLFuncPattern("日期是否闰日", BooleanType.TYPE, ArgumentSpec.date) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xIsLeapDay(values[0]);
				}

			}),

	// ---------------------- 数学函数 ----------------------

	sin(new SQLFuncPattern("返回角度(以弧度为单位)的正弦值", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSin(values[0]);
		}

	}),

	cos(new SQLFuncPattern("返回角度(以弧度为单位)的余弦值", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xCos(values[0]);
		}

	}),

	tan(new SQLFuncPattern("返回角度(以弧度为单位)的正切值", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xTan(values[0]);
		}

	}),

	asin(new SQLFuncPattern("返回角度(以弧度为单位)的反正弦值", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAsin(values[0]);
		}

	}),

	acos(new SQLFuncPattern("返回角度(以弧度为单位)的反余弦值", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAcos(values[0]);
		}

	}),

	atan(new SQLFuncPattern("返回角度(以弧度为单位)的反正切值", DoubleType.TYPE,
			ArgumentSpec.radians) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAtan(values[0]);
		}

	}),

	exp(new SQLFuncPattern("返回指定值的指数值", DoubleType.TYPE, ArgumentSpec.power) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xExp(values[0]);
		}

	}),

	power(new SQLFuncPattern("返回指定值的指定幂的值", DoubleType.TYPE, ArgumentSpec.base,
			ArgumentSpec.power) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xPower(values[0], values[1]);
		}

	}),

	ln(new SQLFuncPattern("返回指定值以e为底的对数值,即自然对数", DoubleType.TYPE,
			ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xLn(values[0]);
		}

	}),

	lg(new SQLFuncPattern("返回指定值以10为底的对数值", DoubleType.TYPE,
			ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xLg(values[0]);
		}

	}),

	sqrt(new SQLFuncPattern("返回指定值的平方根", DoubleType.TYPE, ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSqrt(values[0]);
		}

	}),

	ceil(new SQLFuncPattern("返回大于或等于指定值的最小整数", LongType.TYPE,
			ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xCeil(values[0]);
		}

	}),

	floor(new SQLFuncPattern("返回小于或等于指定值的最大整数", LongType.TYPE,
			ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xFloor(values[0]);
		}

	}),

	round(
			new SQLFuncPattern("返回最接近表达式的整数", LongType.TYPE,
					ArgumentSpec.number) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRound(values[0], Integer.valueOf(0));
				}

			}, new SQLFuncPattern("返回舍入到精度的数字", LongType.TYPE,
					ArgumentSpec.number, ArgumentSpec.scale) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xRound(values[0], values[1]);
				}

			}),

	sign(new SQLFuncPattern("返回参数的符号函数值", IntType.TYPE, ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSign(values[0]);
		}

	}),

	abs(new SQLFuncPattern("返回参数的绝对值", DoubleType.TYPE, ArgumentSpec.number) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAbs(values[0]);
		}

	}),

	// ---------------------- 字符串函数 ----------------------

	chr(new SQLFuncPattern("将ASCII代码转换为字符", StringType.TYPE, new ArgumentSpec(
			"ascii", "ascii值", IntType.TYPE)) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xChr(values[0]);
		}

	}),

	nchr(new SQLFuncPattern("返回具有指定的整数代码的Unicode字符", StringType.TYPE,
			new ArgumentSpec("unicode", "unicode值", IntType.TYPE)) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xNchr(values[0]);
		}

	}),

	ascii(new SQLFuncPattern("返回字符表达式中最左侧的字符的ASCII代码值", IntType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xAscii(values[0]);
		}

	}),

	len(
			new SQLFuncPattern("返回指定字符串表达式的字符数", IntType.TYPE,
					ArgumentSpec.string) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLen(values[0]);
				}

			}, new SQLFuncPattern("返回指定二进制字符串表达式的字节数", IntType.TYPE,
					ArgumentSpec.bytes) {

				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xLen(values[0]);
				}

			}),

	indexof(new SQLFuncPattern("返回查找字符串(search)在指定字符串(str)中第一出现的序号(从1开始)",
			IntType.TYPE, ArgumentSpec.string, ArgumentSpec.search) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xIndexOf(values[0], values[1]);
		}
	}, new SQLFuncPattern("返回查找字符串(search)在指定字符串(str)中第一出现的序号(从1开始)",
			IntType.TYPE, ArgumentSpec.string, ArgumentSpec.search,
			ArgumentSpec.start_position) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xIndexOf(values[0], values[1], values[2]);
		}
	}),

	lower(new SQLFuncPattern("将大写字符数据转换为小写字符数据后返回", StringType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xLower(values[0]);
		}

	}),

	upper(new SQLFuncPattern("将小写字符数据转换为大写字符数据后返回", StringType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xUpper(values[0]);
		}

	}),

	ltrim(new SQLFuncPattern("删除字符表达式的前导空格", StringType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xLtrim(values[0]);
		}

	}),

	rtrim(new SQLFuncPattern("删除字符表达式的尾随空格", StringType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xRtrim(values[0]);
		}

	}),

	trim(new SQLFuncPattern("删除字符表达式的前后的空格", StringType.TYPE,
			ArgumentSpec.string) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xTrim(values[0]);
		}

	}),

	substr(new SQLFuncPattern("返回字符串表达式的子串", StringType.TYPE,
			ArgumentSpec.string, ArgumentSpec.start_position) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSubstr(values[0], values[1]);
		}

	}, new SQLFuncPattern("返回字符串表达式的子串", StringType.TYPE, ArgumentSpec.string,
			ArgumentSpec.start_position, ArgumentSpec.length) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSubstr(values[0], values[1], values[2]);
		}

	}, new SQLFuncPattern("返回二进制字符串表达式的子串", BytesType.TYPE, ArgumentSpec.bytes,
			ArgumentSpec.start_position) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSubstr(values[0], values[1]);
		}

	}, new SQLFuncPattern("返回字符串表达式的子串", BytesType.TYPE, ArgumentSpec.bytes,
			ArgumentSpec.start_position, ArgumentSpec.length) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xSubstr(values[0], values[1], values[2]);
		}

	}),

	replace(new SQLFuncPattern("替换原字符串(str)中的所有搜索字符串(search)为新字符串(replace)",
			StringType.TYPE, ArgumentSpec.string, ArgumentSpec.search,
			ArgumentSpec.replace) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xReplace(values[0], values[1], values[2]);
		}
	}),

	to_char(new SQLFuncPattern("数值(number)转换为数据库编码类型的字符串", StringType.TYPE,
			ArgumentSpec.number) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xToChar(values[0]);
		}
	}, new SQLFuncPattern("日期(date)转换为数据库编码类型的字符串", StringType.TYPE,
			ArgumentSpec.date) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xToChar(values[0]);
		}
	}, new SQLFuncPattern("GUID转换为数据库编码类型的字符串", StringType.TYPE,
			new ArgumentSpec("guid", "GUID", GUIDType.TYPE)) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xToChar(values[0]);
		}
	}, new SQLFuncPattern("二进制字符串(bytes)转换为数据库编码类型的字符串", StringType.TYPE,
			new ArgumentSpec("bytes", "二进制字符串", BytesType.TYPE)) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xToChar(values[0]);
		}
	}, new SQLFuncPattern("字符串(str)转换为数据库编码类型的字符串", StringType.TYPE,
			ArgumentSpec.string) {
		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xToChar(values[0]);
		}
	}), new_recid(new SQLFuncPattern("创建GUID类型值", GUIDType.TYPE) {

		@Override
		public OperateExpression expOf(Object[] values) {
			return SQLFunc.xNewRecid();
		}
	}), to_int(
			new SQLFuncPattern("转换为整型数值", IntType.TYPE, ArgumentSpec.string) {
				@Override
				public OperateExpression expOf(Object[] values) {
					return SQLFunc.xToInt(values[0]);
				}
			})

	;

	private SQLFuncSpec(SQLFuncPattern pattern, SQLFuncPattern... patterns) {
		if (patterns == null) {
			this.patterns = new SQLFuncPattern[] { pattern };
		} else {
			SQLFuncPattern[] arr = new SQLFuncPattern[patterns.length + 1];
			int i = 0;
			arr[i++] = pattern;
			for (SQLFuncPattern p : patterns) {
				arr[i++] = p;
			}
			this.patterns = arr;
		}
	}

	private final SQLFuncPattern[] patterns;

	public final SQLFuncPattern accept(DataType[] types) {
		for (SQLFuncPattern p : this.patterns) {
			if (p.accept(types)) {
				return p;
			}
		}
		return null;
	}

	public final SQLFuncPattern getPattern(int i) {
		return this.patterns[i];
	}

	public final int size() {
		return this.patterns.length;
	}

	/**
	 * 函数的参数规格说明
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public static class ArgumentSpec {

		private static final ArgumentSpec date = new ArgumentSpec("date",
				"日期表达式", DateType.TYPE);

		private static final ArgumentSpec interval = new ArgumentSpec(
				"interval", "时间间隔数", LongType.TYPE);

		private static final ArgumentSpec startdate = new ArgumentSpec(
				"startdate", "开始日期", DateType.TYPE);

		private static final ArgumentSpec enddate = new ArgumentSpec("enddate",
				"结束日期", DateType.TYPE);

		private static final ArgumentSpec radians = new ArgumentSpec("radians",
				"弧度", DoubleType.TYPE);

		private static final ArgumentSpec base = new ArgumentSpec("base", "底数",
				DoubleType.TYPE);

		private static final ArgumentSpec power = new ArgumentSpec("power",
				"指数", DoubleType.TYPE);

		private static final ArgumentSpec number = new ArgumentSpec("number",
				"数值", DoubleType.TYPE);

		private static final ArgumentSpec string = new ArgumentSpec("str",
				"字符串", StringType.TYPE);

		private static final ArgumentSpec bytes = new ArgumentSpec("bytes",
				"二进制字符串", BytesType.TYPE);

		private static final ArgumentSpec search = new ArgumentSpec("search",
				"搜索字符串", StringType.TYPE);

		private static final ArgumentSpec start_position = new ArgumentSpec(
				"start_position", "开始位置", LongType.TYPE);

		private static final ArgumentSpec length = new ArgumentSpec("length",
				"截取长度", LongType.TYPE);

		private static final ArgumentSpec scale = new ArgumentSpec("scale",
				"截取精度", LongType.TYPE);

		private static final ArgumentSpec replace = new ArgumentSpec("replace",
				"替换字符串", StringType.TYPE);

		private static final ArgumentSpec[] emptyArray = new ArgumentSpec[] {};

		/**
		 * 默认的标志符
		 */
		public final String str;

		/**
		 * 参数说明
		 */
		public final String description;

		/**
		 * 参数的数据类型
		 */
		public final DataType type;

		private ArgumentSpec(String str, String description, DataType type) {
			this.str = str;
			this.description = description;
			this.type = type;
		}
	}

	public static abstract class SQLFuncPattern {

		/**
		 * 模式的说明
		 */
		public final String description;

		/**
		 * 模式的返回值类型
		 */
		public final DataType type;

		/**
		 * 模式的参数信息
		 */
		public final ArgumentSpec[] args;

		private SQLFuncPattern(String description, DataType type,
				ArgumentSpec... args) {
			this.description = description;
			this.type = type;
			if (args == null) {
				this.args = ArgumentSpec.emptyArray;
			} else {
				this.args = args;
			}
		}

		/**
		 * 函数是否接受指定数据类型列表的参数值
		 * 
		 * @param types
		 * @return
		 */
		public final boolean accept(DataType[] types) {
			if (types == null) {
				throw new NullArgumentException("参数类型列表为空.");
			}
			if (types.length != this.args.length) {
				return false;
			}
			final int c = types.length;
			for (int i = 0; i < c; i++) {
				AssignCapability ac = this.args[i].type
						.isAssignableFrom(types[i]);
				if (ac == AssignCapability.SAME
						|| ac == AssignCapability.IMPLICIT) {
					continue;
				}
				return false;
			}
			return true;
		}

		/**
		 * 构造表达式,调用前确认函数接口指定的参数签名被接受
		 * 
		 * @param values
		 * @return
		 */
		public abstract OperateExpression expOf(Object[] values);

	}

}