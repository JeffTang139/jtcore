package org.eclipse.jt.core.type;

import org.eclipse.jt.core.type.ValueConvertException;

/**
 * ����ʱ���ַ���������
 * 
 * @author Jeff Tang 2009-07-07
 */
public final class DateParser {

	/**
	 * ��������ʱ���ַ���,������ת��ΪJAVAʱ��<br>
	 * ������������ʱ���ַ�����,��Ԫ��(�� �� �� ʱ �� �� ����)֮�� <br>
	 * ����һ���������ַ�����,��: <br>
	 * 2000-02-29 <br>
	 * 2000-02-29 10:12:54 <br>
	 * 2000-02-29 10:12:54.338 <br>
	 * ����,������ʱ���׳���Ч�����쳣.
	 * 
	 * @param Date
	 *            ������������ʱ���ַ���
	 * @return ���ؽ�����õ��ĳ�����JAVAʱ��
	 */
	public static long parse(final String dateTimeStr) {
		return timeAdjust(computeJavaDateTime(dateTimeStr));
	}

	/**
	 * ��������ʱ���ַ���,������ת��ΪD&Aʱ��(Ч�ʽϵ�,������Ƶ��ʹ��)<br>
	 * ������������ʱ���ַ�����,��Ԫ��(�� �� �� ʱ �� �� ����)֮�� <br>
	 * ����һ��ָ�����ַ�����,��: <br>
	 * 2000-02-29 <br>
	 * 2000-02-29 10:12:54 <br>
	 * 2000-02-29 10:12:54.338 <br>
	 * ����,������ʱ���׳���Ч�����쳣.
	 * 
	 * @param Date
	 *            ������������ʱ���ַ���
	 * @return ���ؽ�����õ��ĳ�����D&Aʱ��
	 */
	public static long parseDNADateTime(final String dateTimeStr) {
		return toDNADateTime(parse(dateTimeStr));
	}

	/**
	 * ��ʽ������������ʱ��<br>
	 * ���ر�׼�����ַ���,��: <br>
	 * 2000-02-29 10:12:54.338 <br>
	 * 
	 * @param dateTime
	 *            ����ʽ���ĳ���������ʱ��,������JAVAʱ��,Ҳ������D&Aʱ��
	 * @return ���ظ�ʽ����õ�������ʱ���ַ���
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
	 * ����ָ���ĸ�ʽ,��ʽ������������ʱ��<br>
	 * ���ر�׼�����ַ���,��: <br>
	 * 2000-02-29 <br>
	 * 2000-02-29 10:12:54<br>
	 * 2000-02-29 10:12:54.338 <br>
	 * 10:12:54 <br>
	 * 10:12:54.338 <br>
	 * 
	 * @param dateTime
	 *            ����ʽ���ĳ���������ʱ��,������JAVAʱ��,Ҳ������D&Aʱ��
	 * @param formatType
	 *            <li>DateParser.FORMAT_DATE: ֻ��ʽ�����ڲ���</li> 
	 *            <li>DateParser.FORMAT_DATE_TIME: ֻ��ʽ�����ں�ʱ�䲿��</li> 
	 *            <li>DateParser.FORMAT_DATE_TIME_MS: ��ʽ������,ʱ��ͺ��벿��</li> 
	 *            <li>DateParser.FORMAT_DATE_TIME_AUTOMS: ��ʽ������,ʱ��ͺ��벿��,������Ϊ0,�򲻸�ʽ��</li>
	 *            <li>DateParser.FORMAT_TIME: ֻ��ʽ��ʱ�䲿��</li> 
	 *            <li>DateParser.FORMAT_TIME_MS: ֻ��ʽ��ʱ��ͺ��벿��</li> 
	 *            <li>DateParser.FORMAT_TIME_AUTOMS: ��ʽ������,ʱ��ͺ��벿��,������Ϊ0,�򲻸�ʽ��</li>
	 * @return ���ظ�ʽ����õ�������ʱ���ַ���
	 */
	public static String format(long dateTime, int formatType) {
		if (!isDNADateTime(dateTime)) {
			dateTime = toDNADateTime(dateTime);
		}
		return new String(formatDNADateTime(dateTime, formatType));
		//return Utils.fastString(formatDNADateTime(dateTime, formatType));
	}

	/**
	 * �������ͱ�ʾ��ʱ��ת��ΪJavaʱ��
	 * 
	 * @param dateTime
	 *            ��ת��ʱ��
	 * @return ת�����Javaʱ��
	 */
	public static long toJavaDateTime(long dateTime) {
		if (!isDNADateTime(dateTime)) {
			return dateTime;
		}
		return timeAdjust(dnaDateTimeToJavaDateTime(dateTime));
	}

	/**
	 * �������ͱ�ʾ��ʱ��ת��Ϊ����D&Aʱ��
	 * 
	 * @param dateTime
	 *            ��ת��ʱ��
	 * @return ת�����D&Aʱ��
	 */
	// ===========================================================================================================
	// D&A����ʱ�����ʹ洢�ṹ����
	// __________|< 63
	// --------------------------------------------------------------------------------------
	// 0 >|
	// �������������ש������������ש����������ש��������ש������ש������ש��������ש������������������ש������ש������ש������ש�����������
	// �������������������ͱ�־����������񡡩����ꡡ�������¡������ա��������ڡ�����������ʱ������������ʱ�������֡������롡�������롡����
	// �ǩ����������贈�����������贈���������贈�������贈�����贈�����贈�������贈�����������������贈�����贈�����贈�����贈����������
	// �����ȣ�λ��������������������������������������������������������������������������������������������������������������������������
	// �ǩ����������贈�����������贈���������贈�������贈�����贈�����贈�������贈�����������������贈�����贈�����贈�����贈����������
	// ����ע���������̶�Ϊ����������Ϊ���ꡡ����������������������������������������������Ϊ��ʱ������������������������������������������
	// ������������������������������Ϊƽ�ꡡ������������������������������������������������Ϊ��ʱ����������������������������������������
	// �������������ߩ������������ߩ����������ߩ��������ߩ������ߩ������ߩ��������ߩ������������������ߩ������ߩ������ߩ������ߩ�����������
	// ===========================================================================================================
	public static long toDNADateTime(long dateTime) {
		if (isDNADateTime(dateTime)) {
			return dateTime;
		}
		// D&Aʱ������: 01000
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
	 * ��ȡD&Aʱ���ʱ����Ϣ
	 */
	public static int getTimeZone(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_TIMEZONE_END_POS) & DNATIME_TIMEZONE_MASK);
	}

	/**
	 * �ж�D&Aʱ�����ڵ�����Ƿ�Ϊ����
	 */
	public static boolean isLeapYear(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		if ((int) ((dnaDateTime >>> DNATIME_LEAPYEAR_MARK_END_POS) & 0x0001L) == 1) {
			return true;
		}
		return false;
	}

	/**
	 * ��ȡD&Aʱ������
	 */
	public static int getYear(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_YEAR_END_POS) & DNATIME_YEAR_MASK);
	}

	/**
	 * ��ȡD&Aʱ����·�
	 */
	public static int getMonth(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_MONTH_END_POS) & DNATIME_MONTH_MASK);
	}

	/**
	 * ��ȡD&Aʱ�������
	 */
	public static int getDate(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_DATE_END_POS) & DNATIME_DATE_MASK);
	}

	/**
	 * ��ȡD&Aʱ�������
	 */
	public static int getDay(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_DAY_END_POS) & DNATIME_DAY_MASK);
	}

	/**
	 * ��ȡD&Aʱ���Сʱ
	 */
	public static int getHour(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_HOUR_END_POS) & DNATIME_HOUR_MASK);
	}

	/**
	 * ��ȡD&Aʱ��ķ�
	 */
	public static int getMinute(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_MINUTE_END_POS) & DNATIME_MINUTE_MASK);
	}

	/**
	 * ��ȡD&Aʱ�����
	 */
	public static int getSecond(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) ((dnaDateTime >>> DNATIME_SECOND_END_POS) & DNATIME_SECOND_MASK);
	}

	/**
	 * ��ȡD&Aʱ��ĺ���
	 */
	public static int getMilliSecond(long dnaDateTime) {
		checkDNADateTime(dnaDateTime);
		return (int) (dnaDateTime & DNATIME_MS_MASK);
	}

	// //////////////////////////////////////////////////////////////////////////////////
	// �������Ժͷ���

	static final int[] LEAPYEAR_DAYS_COUNT_FOR_MONTH = new int[] { 0, 0, 31,
	        60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366 };

	static final int[] COMMONYEAR_DAYS_COUNT_FOR_MONTH = new int[] { 0, 0, 31,
	        59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365 };

	static final int[] LEAPYEAR_DAYS_FOR_MONTH = new int[] { 0, 31, 29, 31, 30,
	        31, 30, 31, 31, 30, 31, 30, 31 };

	static final int[] COMMONYEAR_DAYS_FOR_MONTH = new int[] { 0, 31, 28, 31,
	        30, 31, 30, 31, 31, 30, 31, 30, 31 };

	// �й�����ʱ��ʼʱ��
	static final long[] DAYLIGHT_SAVING_TIME_START = new long[] {
	        -933490800000L, -908780400000L, 515523600000L, 545158800000L,
	        576608400000L, 608662800000L, 640112400000L, 671562000000L };
	// �й�����ʱ����ʱ��
	static final long[] DAYLIGHT_SAVING_TIME_END = new long[] { -923130000000L,
	        -891590400000L, 527007600000L, 558457200000L, 589906800000L,
	        621961200000L, 653410800000L, 684860400000L };

	// //////////////////////////////////////////////////////////////////////////////////
	// ˽�����Ժͷ���

	private static int parseInt(final String in, final int len,
	        final int start, final int maxCount, final int minValue,
	        final int maxValue) {
		int p = start;
		int minEnd = start + 1;
		if (minEnd > len) {
			throw new ValueConvertException("��ֵλ������" + 1 + "λ\t" + start);
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
			throw new ValueConvertException("��ֵλ������" + 1 + "λ\t" + start);
		}
		if (result < minValue || maxValue < result) {
			throw new ValueConvertException("��ֵ������������Χ\t" + start);
		}
		return p << 16 | result;
	}

	private static void checkDivi(String s, int len, int position) {
		if (position >= len) {
			throw new ValueConvertException("����ʱ�䲻�겻��\t");
		}
		final char tempChar = s.charAt(position);
		if ('0' <= tempChar && tempChar <= '9') {
			throw new ValueConvertException("�ֽ������,�˴��ֽ��ӦΪ�������ַ�\t"
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
			throw new ValueConvertException(year + "���" + month + "��,û��" + date
			        + "��");
		}
		// �Թ�Ԫ2000��Ϊ��׼
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
	 * ʱ�����,��������ʱ�����ͻ�׼ʱ����Ե���
	 */
	private static long timeAdjust(long time) {
		int len = DAYLIGHT_SAVING_TIME_START.length - 1;
		// ��2000��Ϊ��׼
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
	 * ����ʱ�����,��������ʱ�����ͻ�׼ʱ����Ե���
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
		// ��2000��Ϊ��׼
		return javaTime - 946656000000L;
	}

	private static int daysToDNADate(int days) {
		final int weekDay = (days + 5) % 7;
		int dnaDate = 0 | (weekDay < 0 ? (7 + weekDay) : weekDay);
		int year;
		// 400�����,400�깲146097��
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
				// 100�깲36524��
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
			// 4�깲1641��
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
		// ʱ�� 00000 (��ʱ��)
		return 0 | ((ms / 3600000) << DNATIME_HOUR_END_POS)
		        | (((ms % 3600000) / 60000) << DNATIME_MINUTE_END_POS)
		        | (((ms % 60000) / 1000) << DNATIME_SECOND_END_POS)
		        | (ms % 1000);
	}
	
	/**
	 * ��ʽ��D&A����ʱ��
	 * 
	 * @param dnaDateTime
	 *            ����ʽ����D&A����ʱ��
	 * @param formatType
	 *            XXXXXDMA: <br>
	 *            XΪ���λ,ֵ��Ч<br>
	 *            DΪ1ʱ,��ʽ������;����,����ʽ������<br>
	 *            MZ��ͬʱΪ��ʱ,��ʽ��ʱ��<br>
	 *            MΪ1ʱ,��ʽ������;����,����ʽ������<br>
	 *            AΪ1ʱ,���ܸ�ʽ������;����,ǿ�Ƹ�ʽ������
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
			throw new ValueConvertException("�Ƿ�����");
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
	 * �жϳ�����ʱ���Ƿ�Ϊ�Ϸ���D&Aʱ��<br>
	 * D&Aʱ�䶨�峤�������������λ������01
	 */
	private static boolean isDNADateTime(long dateTime) {
		return dateTime >>> DNATIME_MARK_END_POS == 0x1L;
	}

	/**
	 * ����D&Aʱ��ĺϷ���
	 */
	private static void checkDNADateTime(long dnaDateTime) {
		if (!isDNADateTime(dnaDateTime)) {
			throw new ValueConvertException("�Ƿ���D&Aʱ��");
		}
	}

	private DateParser() {
	}

}