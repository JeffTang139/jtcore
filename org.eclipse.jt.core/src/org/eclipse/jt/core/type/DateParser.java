package org.eclipse.jt.core.type;

import org.eclipse.jt.core.type.ValueConvertException;

/**
 * 日期时间字符串解析类
 * 
 * @author Jeff Tang 2009-07-07
 */
public final class DateParser {

	/**
	 * 解析日期时间字符串,并将其转换为JAVA时间<br>
	 * 待解析的日期时间字符串中,各元素(年 月 日 时 分 秒 毫秒)之间 <br>
	 * 需用一个非数字字符隔开,如: <br>
	 * 2000-02-29 <br>
	 * 2000-02-29 10:12:54 <br>
	 * 2000-02-29 10:12:54.338 <br>
	 * 否则,在运行时会抛出无效参数异常.
	 * 
	 * @param Date
	 *            待解析的日期时间字符串
	 * @return 返回解析后得到的长整型JAVA时间
	 */
	public static long parse(final String dateTimeStr) {
		return timeAdjust(computeJavaDateTime(dateTimeStr));
	}

	/**
	 * 解析日期时间字符串,并将其转换为D&A时间(效率较低,不建议频繁使用)<br>
	 * 待解析的日期时间字符串中,各元素(年 月 日 时 分 秒 毫秒)之间 <br>
	 * 需用一个指定的字符隔开,如: <br>
	 * 2000-02-29 <br>
	 * 2000-02-29 10:12:54 <br>
	 * 2000-02-29 10:12:54.338 <br>
	 * 否则,在运行时会抛出无效参数异常.
	 * 
	 * @param Date
	 *            待解析的日期时间字符串
	 * @return 返回解析后得到的长整型D&A时间
	 */
	public static long parseDNADateTime(final String dateTimeStr) {
		return toDNADateTime(parse(dateTimeStr));
	}

	/**
	 * 格式化长整型日期时间<br>
	 * 返回标准日期字符串,如: <br>
	 * 2000-02-29 10:12:54.338 <br>
	 * 
	 * @param dateTime
	 *            待格式化的长整形日期时间,可以是JAVA时间,也可以是D&A时间
	 * @return 返回格式化后得到的日期时间字符串
	 */
	public static String format(long dateTime) {
		return format(dateTime, FORMAT_DATE_TIME_AUTOMS);
	}
	
	private final static int FORMATTYPE_DATE_MASK = 0x4;
	private final static int FORMATTYPE_TIME_MASK = 0x3;
	private final static int FORMATTYPE_MS_MASK = 0x2;
	private final static int FORMATTYPE_AUTOMS_MASK = 0x1;

	public final static int FORMAT_TIME = 0 | FORMATTYPE_AUTOMS_MASK;
	public final static int FORMAT_TIME_MS = 0 | FORMATTYPE_MS_MASK;
	public final static int FORMAT_TIME_AUTOMS = 0 | FORMATTYPE_MS_MASK | FORMATTYPE_AUTOMS_MASK;
	public final static int FORMAT_DATE = 0 | FORMATTYPE_DATE_MASK;
	public final static int FORMAT_DATE_TIME = FORMAT_DATE | FORMAT_TIME;
	public final static int FORMAT_DATE_TIME_MS = FORMAT_DATE | FORMAT_TIME_MS;
	public final static int FORMAT_DATE_TIME_AUTOMS = FORMAT_DATE | FORMAT_TIME_AUTOMS;

	/**
	 * 根据指定的格式,格式化长整型日期时间<br>
	 * 返回标准日期字符串,如: <br>
	 * 2000-02-29 <br>
	 * 2000-02-29 10:12:54<br>
	 * 2000-02-29 10:12:54.338 <br>
	 * 10:12:54 <br>
	 * 10:12:54.338 <br>
	 * 
	 * @param dateTime
	 *            待格式化的长整形日期时间,可以是JAVA时间,也可以是D&A时间
	 * @param formatType
	 *            <li>DateParser.FORMAT_DATE: 只格式化日期部分</li> 
	 *            <li>DateParser.FORMAT_DATE_TIME: 只格式化日期和时间部分</li> 
	 *            <li>DateParser.FORMAT_DATE_TIME_MS: 格式化日期,时间和毫秒部分</li> 
	 *            <li>DateParser.FORMAT_DATE_TIME_AUTOMS: 格式化日期,时间和毫秒部分,毫秒数为0,则不格式化</li>
	 *            <li>DateParser.FORMAT_TIME: 只格式化时间部分</li> 
	 *            <li>DateParser.FORMAT_TIME_MS: 只格式化时间和毫秒部分</li> 
	 *            <li>DateParser.FORMAT_TIME_AUTOMS: 格式化日期,时间和毫秒部分,毫秒数为0,则不格式化</li>
	 * @return 返回格式化后得到的日期时间字符串
	 */
	public static String format(long dateTime, int formatType) {
		if (!isDNADateTime(dateTime)) {
			dateTime = toDNADateTime(dateTime);
		}
		return new String(formatDNADateTime(dateTime, formatType));
		//return Utils.fastString(formatDNADateTime(dateTime, formatType));
	}

	/**
	 * 将长整型表示的时间转换为Java时间
	 * 
	 * @param dateTime
	 *            待转换时间
	 * @return 转换后的Java时间
	 */
	public static long toJavaDateTime(long dateTime) {
		if (!isDNADateTime(dateTime)) {
			return dateTime;
		}
		return timeAdjust(dnaDateTimeToJavaDateTime(dateTime));
	}

	/**
	 * 将长整型表示的时间转换为基本D&A时间
	 * 
	 * @param dateTime
	 *            待转换时间
	 * @return 转换后的D&A时间
	 */
	// ===========================================================================================================
	// D&A基本时间类型存储结构定义
	// __________|< 63
	// --------------------------------------------------------------------------------------
	// 0 >|
	// ┏━━━━━┳━━━━━━┳━━━━━┳━━━━┳━━━┳━━━┳━━━━┳━━━━━━━━━┳━━━┳━━━┳━━━┳━━━━━┓
	// ┃域名　　　┃　类型标志　┃　闰年否　┃　年　　┃　月　┃　日　┃　星期　┃　　　　时区　　　┃　时　┃　分　┃　秒　┃　毫秒　　┃
	// ┣━━━━━╋━━━━━━╋━━━━━╋━━━━╋━━━╋━━━╋━━━━╋━━━━━━━━━╋━━━╋━━━╋━━━╋━━━━━┫
	// ┃长度（位）┃　５　　　　┃　１　　　┃　１４　┃　４　┃　５　┃　３　　┃　　　　５　　　　┃　５　┃　６　┃　６　┃　１０　　┃
	// ┣━━━━━╋━━━━━━╋━━━━━╋━━━━╋━━━╋━━━╋━━━━╋━━━━━━━━━╋━━━╋━━━╋━━━╋━━━━━┫
	// ┃备注　　　┃固定为　　　┃１为闰年　┃　　　　┃　　　┃　　　┃　　　　┃１－１２为东时区　┃　　　┃　　　┃　　　┃　　　　　┃
	// ┃　　　　　┃０１０００　┃０为平年　┃　　　　┃　　　┃　　　┃　　　　┃１４－２５为西时区┃　　　┃　　　┃　　　┃　　　　　┃
	// ┗━━━━━┻━━━━━━┻━━━━━┻━━━━┻━━━┻━━━┻━━━━┻━━━━━━━━━┻━━━┻━━━┻━━━┻━━━━━┛
	// ===========================================================================================================
	public static long toDNADateTime(long dateTime) {
		if (isDNADateTime(dateTime)) {
			return dateTime;
		}
		// D&A时间类型: 01000
		final long baseDNATime = 0L | (0x08L << DNATIME_TIME_TYPE_END_POS);
		dateTime = reverseTimeAdjust(dateTime);
		final int days = (int) (dateTime / 86400000L);
		final int milliSeconds = (int) (dateTime % 86400000L);
		return baseDNATime
		        | ((long) daysToDNADate((dateTime >= 0 || milliSeconds == 0) ? days + 1
		                : days) << DNATIME_DATEFILED_END_POS)
		        | (msToDNATime(milliSeconds));
	}

	/**
	 * 获取D&A时间的时区信息
	 */
	public static int getTimeZone(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_TIMEZONE_END_POS) & DNATIME_TIMEZONE_MASK);
	}

	/**
	 * 判断D&A时间所在的年份是否为闰年
	 */
	public static boolean isLeapYear(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		if ((int) ((dnaDateTime >>> DNATIME_LEAPYEAR_MARK_END_POS) & 0x0001L) == 1) {
			return true;
		}
		return false;
	}

	/**
	 * 获取D&A时间的年份
	 */
	public static int getYear(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_YEAR_END_POS) & DNATIME_YEAR_MASK);
	}

	/**
	 * 获取D&A时间的月份
	 */
	public static int getMonth(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_MONTH_END_POS) & DNATIME_MONTH_MASK);
	}

	/**
	 * 获取D&A时间的日期
	 */
	public static int getDate(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_DATE_END_POS) & DNATIME_DATE_MASK);
	}

	/**
	 * 获取D&A时间的星期
	 */
	public static int getDay(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_DAY_END_POS) & DNATIME_DAY_MASK);
	}

	/**
	 * 获取D&A时间的小时
	 */
	public static int getHour(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_HOUR_END_POS) & DNATIME_HOUR_MASK);
	}

	/**
	 * 获取D&A时间的分
	 */
	public static int getMinute(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_MINUTE_END_POS) & DNATIME_MINUTE_MASK);
	}

	/**
	 * 获取D&A时间的秒
	 */
	public static int getSecond(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_SECOND_END_POS) & DNATIME_SECOND_MASK);
	}

	/**
	 * 获取D&A时间的毫秒
	 */
	public static int getMilliSecond(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) (dnaDateTime & DNATIME_MS_MASK);
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// 保护属性和方法

	static final int[] LEAPYEAR_DAYS_COUNT_FOR_MONTH = new int[] { 0, 0, 31,
	        60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366 };

	static final int[] COMMONYEAR_DAYS_COUNT_FOR_MONTH = new int[] { 0, 0, 31,
	        59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365 };

	static final int[] LEAPYEAR_DAYS_FOR_MONTH = new int[] { 0, 31, 29, 31, 30,
	        31, 30, 31, 31, 30, 31, 30, 31 };

	static final int[] COMMONYEAR_DAYS_FOR_MONTH = new int[] { 0, 31, 28, 31,
	        30, 31, 30, 31, 31, 30, 31, 30, 31 };

	// 中国夏令时开始时间
	static final long[] DAYLIGHT_SAVING_TIME_START = new long[] {
	        -933490800000L, -908780400000L, 515523600000L, 545158800000L,
	        576608400000L, 608662800000L, 640112400000L, 671562000000L };
	// 中国夏令时结束时间
	static final long[] DAYLIGHT_SAVING_TIME_END = new long[] { -923130000000L,
	        -891590400000L, 527007600000L, 558457200000L, 589906800000L,
	        621961200000L, 653410800000L, 684860400000L };

	// //////////////////////////////////////////////////////////////////////////////////
	// 私有属性和方法

	private static int parseInt(final String in, final int len,
	        final int start, final int maxCount, final int minValue,
	        final int maxValue) {
		int p = start;
		int minEnd = start + 1;
		if (minEnd > len) {
			throw new ValueConvertException("数值位数少于" + 1 + "位\t" + start);
		}
		int maxEnd = start + maxCount;
		if (maxEnd > len) {
			maxEnd = len;
		}
		int result = 0;
		while (p < maxEnd) {
			final char tempChar = in.charAt(p);
			if ('0' <= tempChar && tempChar <= '9') {
				result = result * 10 + tempChar - '0';
				p++;
			} else {
				break;
			}
		}
		if (p < minEnd) {
			throw new ValueConvertException("数值位数少于" + 1 + "位\t" + start);
		}
		if (result < minValue || maxValue < result) {
			throw new ValueConvertException("数值超出了正常范围\t" + start);
		}
		return p << 16 | result;
	}

	private static void checkDivi(String s, int len, int position) {
		if (position >= len) {
			throw new ValueConvertException("日期时间不完不整\t");
		}
		final char tempChar = s.charAt(position);
		if ('0' <= tempChar && tempChar <= '9') {
			throw new ValueConvertException("分界符错误,此处分界符应为非数字字符\t"
			        + position);
		}
	}
	
	private static boolean checkNumber(String s, int len, int position) {
		if (position >= len) {
			return false;
		}
		final char tempChar = s.charAt(position);
		if (tempChar < '0' || '9' < tempChar) {
			return false;
		}
		return true;
	}

	private static int computeJavaDate(final int year, final int month,
	        final int date) {
		final int isLeapYear;
		final int[] DAYS_FOR_MONTH;
		final int[] DAYS_COUNT_FOR_MONTH;
		if (0 == year % 4 && 0 != year % 100 || 0 == year % 400) {
			isLeapYear = 1;
			DAYS_FOR_MONTH = LEAPYEAR_DAYS_FOR_MONTH;
			DAYS_COUNT_FOR_MONTH = LEAPYEAR_DAYS_COUNT_FOR_MONTH;
		} else {
			isLeapYear = 0;
			DAYS_FOR_MONTH = COMMONYEAR_DAYS_FOR_MONTH;
			DAYS_COUNT_FOR_MONTH = COMMONYEAR_DAYS_COUNT_FOR_MONTH;
		}
		if (date > DAYS_FOR_MONTH[month]) {
			throw new ValueConvertException(year + "年的" + month + "月,没有" + date
			        + "天");
		}
		// 以公元2000年为基准
		final int tempYear = year - 2000;
		int result = (tempYear / 4) - (tempYear / 100) + (tempYear / 400);
		result = (tempYear * 365 + result) + DAYS_COUNT_FOR_MONTH[month]
		        + (date - 1);
		return year > 2000 ? result + 1 - isLeapYear : result;
	}

	private static long computeJavaDateTime(String dateTimeStr) {
		final int len = dateTimeStr.length();
		int start = parseInt(dateTimeStr, len, 0, 4, 1, 9999);
		checkDivi(dateTimeStr, len, 4);
		final int year = start & 0xFFFF;

		start = parseInt(dateTimeStr, len, 5, 2, 1, 12);
		final int month = start & 0xFFFF;
		start >>= 16;
		checkDivi(dateTimeStr, len, start++);

		start = parseInt(dateTimeStr, len, start, 2, 1, 31);
		final int day = start & 0xFFFF;
		final long days = computeJavaDate(year, month, day) * 86400000L;
		start >>= 16;
		if (start == len) {
			return days;
		}
		checkDivi(dateTimeStr, len, start++);
		if (start == len 
				||!checkNumber(dateTimeStr, len, (dateTimeStr.charAt(start) == ' ' ? ++start : start))) {
			return days;
		}

		start = parseInt(dateTimeStr, len, start, 2, 0, 23);
		final int hour = start & 0xFFFF;
		start >>= 16;
		checkDivi(dateTimeStr, len, start++);

		start = parseInt(dateTimeStr, len, start, 2, 0, 59);
		final int minute = start & 0xFFFF;
		start >>= 16;
		checkDivi(dateTimeStr, len, start++);

		start = parseInt(dateTimeStr, len, start, 2, 0, 59);
		final int second = start & 0xFFFF;

		final long seconds = (hour * 3600 + minute * 60 + second) * 1000;
		start >>= 16;
		if (start == len) {
			return days + seconds;
		}
		checkDivi(dateTimeStr, len, start++);
		if (start == len || !checkNumber(dateTimeStr, len, start)) {
			return days + seconds;
		}
		
		start = parseInt(dateTimeStr, len, start, 3, 0, 999);
		final int milliSecond = start & 0xFFFF;
		start >>= 16;
		if (start == len) {
			return days + seconds + milliSecond;
		}
		checkDivi(dateTimeStr, len, start++);
		return days + seconds + milliSecond;
	}

	private final static byte DNATIME_MARK_END_POS = 62;
	private final static byte DNATIME_TIME_TYPE_END_POS = 59;
	private final static byte DNATIME_DATEFILED_END_POS = 32;
	private final static byte DNATIME_LEAPYEAR_MARK_END_POS = 58;
	private final static byte DNATIME_YEAR_END_POS = 44;
	private final static byte DNATIME_MONTH_END_POS = 40;
	private final static byte DNATIME_DATE_END_POS = 35;
	private final static byte DNATIME_DAY_END_POS = 32;
	private final static byte DNATIME_TIMEZONE_END_POS = 27;
	private final static byte DNATIME_HOUR_END_POS = 22;
	private final static byte DNATIME_MINUTE_END_POS = 16;
	private final static byte DNATIME_SECOND_END_POS = 10;

	private final static int DNATIME_YEAR_MASK = 0x3FFF;
	private final static int DNATIME_MONTH_MASK = 0xF;
	private final static int DNATIME_DATE_MASK = 0x1F;
	private final static int DNATIME_DAY_MASK = 0x7;
	private final static int DNATIME_TIMEZONE_MASK = 0x1F;
	private final static int DNATIME_HOUR_MASK = 0x1F;
	private final static int DNATIME_MINUTE_MASK = 0x3F;
	private final static int DNATIME_SECOND_MASK = 0x3F;
	private final static int DNATIME_MS_MASK = 0x3FF;

	private static long dnaDateTimeToJavaDateTime(long dnaDateTime) {
		final int year = (int) ((dnaDateTime >>> DNATIME_YEAR_END_POS) & 0x3FFF);
		final int isLeapYear;
		final int[] DAYS_COUNT_FOR_MONTH;
		if (((dnaDateTime >>> DNATIME_LEAPYEAR_MARK_END_POS) & 0x1) == 1) {
			isLeapYear = 1;
			DAYS_COUNT_FOR_MONTH = LEAPYEAR_DAYS_COUNT_FOR_MONTH;
		} else {
			isLeapYear = 0;
			DAYS_COUNT_FOR_MONTH = COMMONYEAR_DAYS_COUNT_FOR_MONTH;
		}
		final int tempYear = year - 2000;
		long result = (tempYear / 4) - (tempYear / 100) + (tempYear / 400);
		result = (tempYear * 365 + result)
		        + DAYS_COUNT_FOR_MONTH[(int) ((dnaDateTime >>> DNATIME_MONTH_END_POS) & 0xF)]
		        + (((dnaDateTime >>> DNATIME_DATE_END_POS) & DNATIME_DATE_MASK) - 1);
		if (year > 2000) {
			result = result + 1 - isLeapYear;
		}
		result *= 86400000L;
		return result
		        + (((dnaDateTime >>> DNATIME_HOUR_END_POS) & DNATIME_HOUR_MASK)
		                * 3600
		                + ((dnaDateTime >>> DNATIME_MINUTE_END_POS) & DNATIME_MINUTE_MASK)
		                * 60 + ((dnaDateTime >>> DNATIME_SECOND_END_POS) & DNATIME_SECOND_MASK))
		        * 1000 + (dnaDateTime & DNATIME_MS_MASK);
	}

	/**
	 * 时间调整,包括夏令时调整和基准时间相对调整
	 */
	private static long timeAdjust(long time) {
		int len = DAYLIGHT_SAVING_TIME_START.length - 1;
		// 以2000年为基准
		time += 946656000000L;
		if (time < DAYLIGHT_SAVING_TIME_START[0]
		        || DAYLIGHT_SAVING_TIME_END[len] < time) {
			return time;
		}
		while (0 <= len) {
			if (DAYLIGHT_SAVING_TIME_START[len] <= time
			        && time < DAYLIGHT_SAVING_TIME_END[len]) {
				return time - 3600000L;
			}
			len--;
		}
		return time;
	}

	/**
	 * 反向时间调整,包括夏令时调整和基准时间相对调整
	 */
	private static long reverseTimeAdjust(long javaTime) {
		int len = DAYLIGHT_SAVING_TIME_START.length - 1;
		if (!(javaTime < DAYLIGHT_SAVING_TIME_START[0] || DAYLIGHT_SAVING_TIME_END[len] < javaTime)) {
			while (0 <= len) {
				if (DAYLIGHT_SAVING_TIME_START[len] <= javaTime
				        && javaTime < DAYLIGHT_SAVING_TIME_END[len]) {
					javaTime += 3600000L;
					break;
				}
				len--;
			}
		}
		// 以2000年为基准
		return javaTime - 946656000000L;
	}

	private static int daysToDNADate(int days) {
		final int weekDay = (days + 5) % 7;
		int dnaDate = 0 | (weekDay < 0 ? (7 + weekDay) : weekDay);
		int year;
		// 400年对齐,400年共146097天
		year = (days / 146097) * 400;
		days = days % 146097;
		if (days < 0) {

			days += 146097;
			year += 1600;
		} else {
			year += 2000;
		}
		final byte DNATIME_LEAPYEAR_MARK_REL_POS = 26;
		final byte DNATIME_YEAR_REL_POS = 12;
		final byte DNATIME_MONTH_REL_POS = 8;
		final byte DNATIME_DATE_REL_POS = 3;
		final int[] DAYS_COUNT_FOR_MONTH;
		sym: {
			if (days > 36525) {
				days--;
				// 100年共36524天
				year += days / 36524 * 100;
				days = days % 36524;
				if (days <= 1460) {
					year += days / 365;
					days = days % 365;
					DAYS_COUNT_FOR_MONTH = COMMONYEAR_DAYS_COUNT_FOR_MONTH;
					break sym;
				}
				days++;
			}
			// 4年共1641天
			year += days / 1461 * 4;
			days = days % 1461;
			if (days <= 366) {
				dnaDate = dnaDate | (1 << DNATIME_LEAPYEAR_MARK_REL_POS);
				DAYS_COUNT_FOR_MONTH = LEAPYEAR_DAYS_COUNT_FOR_MONTH;
				break sym;
			}
			days--;
			year += days / 365;
			days = days % 365;
			DAYS_COUNT_FOR_MONTH = COMMONYEAR_DAYS_COUNT_FOR_MONTH;
		}
		if (days == 0) {
			return dnaDate | ((year - 1) << DNATIME_YEAR_REL_POS)
			        | (12 << DNATIME_MONTH_REL_POS)
			        | (31 << DNATIME_DATE_REL_POS);
		}
		int month = (days / 30) + 1;
		if (days <= DAYS_COUNT_FOR_MONTH[month]) {
			month--;
		}
		return dnaDate
		        | (year << DNATIME_YEAR_REL_POS)
		        | (month << DNATIME_MONTH_REL_POS)
		        | ((days - DAYS_COUNT_FOR_MONTH[month]) << DNATIME_DATE_REL_POS);
	}

	private static int msToDNATime(int ms) {
		if (ms < 0) {
			ms += 86400000;
		}
		// 时区 00000 (无时区)
		return 0 | ((ms / 3600000) << DNATIME_HOUR_END_POS)
		        | (((ms % 3600000) / 60000) << DNATIME_MINUTE_END_POS)
		        | (((ms % 60000) / 1000) << DNATIME_SECOND_END_POS)
		        | (ms % 1000);
	}
	
	/**
	 * 格式化D&A日期时间
	 * 
	 * @param dnaDateTime
	 *            待格式化的D&A日期时间
	 * @param formatType
	 *            XXXXXDMA: <br>
	 *            X为填充位,值无效<br>
	 *            D为1时,格式化日期;否则,不格式化日期<br>
	 *            MZ不同时为零时,格式化时间<br>
	 *            M为1时,格式化毫秒;否则,不格式化毫秒<br>
	 *            A为1时,智能格式化毫秒;否则,强制格式化毫秒
	 */
	private static char[] formatDNADateTime(final long dnaDateTime,
	        final int formatType) {
		final boolean fDate;
		byte charlen;
		if ((formatType & FORMATTYPE_DATE_MASK) != 0) {
			charlen = 10;
			fDate = true;
		} else {
			charlen = -1;
			fDate = false;
		}
		final boolean fTime;
		final boolean fMS;
		final int ms;
		if ((formatType & FORMATTYPE_TIME_MASK) != 0) {
			fTime = true;
			if ((formatType & FORMATTYPE_MS_MASK) != 0) {
				ms = ((int) dnaDateTime) & DNATIME_MS_MASK;
				if ((formatType & FORMATTYPE_AUTOMS_MASK) == 0 || ms != 0) {
					charlen += 13;
					fMS = true;
				} else {
					fMS = false;
					charlen += 9;
				}
			} else {
				ms = 0;
				fMS = false;
				charlen += 9;
			}
		} else if (fDate) {
			fMS = fTime = false;
			ms = 0;
		} else {
			throw new ValueConvertException("非法参数");
		}
		final char[] dateTimeArray = new char[charlen];
		int temp;
		int j;
		if (fDate) {
			temp = (int) ((dnaDateTime >>> DNATIME_YEAR_END_POS) & DNATIME_YEAR_MASK);
			dateTimeArray[3] = (char) (temp % 10 + '0');
			temp /= 10;
			dateTimeArray[2] = (char) (temp % 10 + '0');
			temp /= 10;
			dateTimeArray[1] = (char) (temp % 10 + '0');
			temp /= 10;
			dateTimeArray[0] = (char) (temp % 10 + '0');
			dateTimeArray[4] = '-';
			temp = (int) ((dnaDateTime >>> DNATIME_MONTH_END_POS) & DNATIME_MONTH_MASK);
			dateTimeArray[5] = (char) (temp / 10 + '0');
			dateTimeArray[6] = (char) (temp % 10 + '0');
			dateTimeArray[7] = '-';
			temp = (int) ((dnaDateTime >>> DNATIME_DATE_END_POS) & DNATIME_DATE_MASK);
			dateTimeArray[8] = (char) (temp / 10 + '0');
			dateTimeArray[9] = (char) (temp % 10 + '0');
			if (fTime) {
				dateTimeArray[10] = ' ';
				j = 11;
			} else {
				return dateTimeArray;
			}
		} else {
			j = 0;
		}
		temp = (((int) dnaDateTime >>> DNATIME_HOUR_END_POS) & DNATIME_HOUR_MASK);
		dateTimeArray[j++] = (char) (temp / 10 + '0');
		dateTimeArray[j++] = (char) (temp % 10 + '0');
		dateTimeArray[j++] = ':';
		temp = (((int) dnaDateTime >>> DNATIME_MINUTE_END_POS) & DNATIME_MINUTE_MASK);
		dateTimeArray[j++] = (char) (temp / 10 + '0');
		dateTimeArray[j++] = (char) (temp % 10 + '0');
		dateTimeArray[j++] = ':';
		temp = (((int) dnaDateTime >>> DNATIME_SECOND_END_POS) & DNATIME_SECOND_MASK);
		dateTimeArray[j++] = (char) (temp / 10 + '0');
		dateTimeArray[j] = (char) (temp % 10 + '0');
		if (fMS) {
			dateTimeArray[++j] = '.';
			dateTimeArray[++j] = (char) (ms / 100 + '0');
			dateTimeArray[++j] = (char) ((ms % 100) / 10 + '0');
			dateTimeArray[++j] = (char) (ms % 10 + '0');
		}
		return dateTimeArray;
	}

	/**
	 * 判断长整型时间是否为合法的D&A时间<br>
	 * D&A时间定义长整型数的最高两位必须是01
	 */
	private static boolean isDNADateTime(long dateTime) {
		return dateTime >>> DNATIME_MARK_END_POS == 0x1L;
	}

	/**
	 * 检验D&A时间的合法性
	 */
	private static void checkDNADateTime(long dnaDateTime) {
		if (!isDNADateTime(dnaDateTime)) {
			throw new ValueConvertException("非法的D&A时间");
		}
	}

	private DateParser() {
	}

}