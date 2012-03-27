package org.eclipse.jt.core.type;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Date;

import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.impl.Utils;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ValueConvertException;


/**
 * 类型转换器，需要相互间转换的类型包括
 * 
 * <p>
 * Boolean,<br>
 * Byte, <br>
 * Short, <br>
 * Int, <br>
 * Long,<br>
 * Date,<br>
 * Float,<br>
 * Double,<br>
 * Char,<br>
 * String,<br>
 * Byte[],<br>
 * Object,<br>
 * GUID<br>
 * ReadableValue
 * 
 * @author Jeff Tang
 * 
 */
public final class Convert {
	/**
	 * 转换成整形
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回整形
	 */
	public static int toInt(char value) {
		return value;
	}

	public static int toInt(boolean value) {
		return value ? 1 : 0;
	}

	public static int toInt(short value) {
		return value;
	}

	public static int toInt(int value) {
		return value;
	}

	public static int toInt(long value) {
		if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE) {
			return (int) value;
		}
		throw new ValueConvertException();
	}

	public static int dateToInt(long value) {
		if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE) {
			return (int) value;
		}
		throw new ValueConvertException();
	}

	public static int toInt(float value) {
		if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE) {
			return (int) value;
		}
		throw new ValueConvertException();
	}

	public static int toInt(double value) {
		if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE) {
			return (int) value;
		}
		throw new ValueConvertException();
	}

	public static int toInt(byte value) {
		return value;
	}

	public static int toInt(byte value[]) {
		throw new ValueConvertException();

	}

	public static int toInt(String value) {
		return value != null && value.length() != 0 ? Integer.parseInt(value)
				: 0;
	}

	public static int toInt(GUID value) {
		throw new ValueConvertException();
	}

	public static int toInt(ReadableValue value) {
		return value != null ? value.getInt() : 0;
	}

	public static int toInt(Object value) {
		if (value == null) {
			return 0;
		} else if (value instanceof Character) {
			return ((Character) value).charValue();
		} else if (value instanceof Integer || value instanceof Byte
				|| value instanceof Short) {
			return ((Number) value).intValue();
		} else if (value instanceof Long) {
			return toInt(((Long) value).longValue());
		} else if (value instanceof Float) {
			return toInt(((Float) value).floatValue());
		} else if (value instanceof Double) {
			return toInt(((Number) value).doubleValue());
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? 1 : 0;
		} else if (value instanceof String) {
			return toInt(value.toString());
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getInt();
		} else if (value instanceof Date) {
			return dateToInt(((Date) value).getTime());
		} else if (value instanceof Enum<?>) {
			return ((Enum<?>) value).ordinal();
		} else {
			throw new ValueConvertException();
		}
	}

	/**
	 * 转换成双精度
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回浮点数
	 */
	public static double toDouble(char value) {
		return value;
	}

	public static double toDouble(boolean value) {
		return value ? 1.0d : 0.0d;
	}

	public static double toDouble(int value) {
		return value;
	}

	public static double toDouble(short value) {
		return value;
	}

	public static double toDouble(long value) {
		return value;
	}

	public static double dateToDouble(long value) {
		return value;
	}

	public static double toDouble(byte value) {
		return value;
	}

	public static double toDouble(byte[] value) {
		throw new ValueConvertException();
	}

	public static double toDouble(float value) {
		return value;
	}

	public static double toDouble(double value) {
		return value;
	}

	public static double toDouble(String value) {
		return value != null && value.length() != 0 ? Double.parseDouble(value)
				: 0.0d;
	}

	public static double toDouble(GUID value) {
		throw new ValueConvertException();
	}

	public static double toDouble(ReadableValue value) {
		return value != null ? value.getDouble() : 0.0d;
	}

	public static double toDouble(Object value) {
		if (value == null) {
			return 0.0d;
		} else if (value instanceof Character) {
			return ((Character) value).charValue();
		} else if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? 1.0d : 0.0d;
		} else if (value instanceof String) {
			return toDouble(value.toString());
		} else if (value instanceof Date) {
			return ((Date) value).getTime();
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getDouble();
		} else if (value instanceof Enum<?>) {
			return ((Enum<?>) value).ordinal();
		} else {
			throw new ValueConvertException();
		}
	}

	/**
	 * 转换成日期
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回long
	 */
	public static long toDate(char value) {
		return value;
	}

	public static long toDate(boolean value) {
		throw new ValueConvertException();
	}

	public static long toDate(short value) {
		return value;
	}

	public static long toDate(int value) {
		return value;
	}

	public static long toDate(long value) {
		return value;
	}

	public static long dateToDate(long value) {
		return value;
	}

	public static long toDate(byte value) {
		return value;
	}

	public static long toDate(byte[] value) {
		throw new ValueConvertException();
	}

	public static long toDate(float value) {
		return toLong(value);
	}

	public static long toDate(double value) {
		return toLong(value);
	}

	public static long toDate(String value) {
		return value == null || value.length() == 0 ? 0 : DateParser
				.parse(value);
	}

	public static long toDate(GUID value) {
		throw new ValueConvertException();
	}

	public static long toDate(ReadableValue value) {
		return value != null ? value.getDate() : 0L;
	}

	public static long toDate(Object value) {
		if (value == null) {
			return 0L;
		} else if (value instanceof Date) {
			return ((Date) value).getTime();
		} else if (value instanceof Long || value instanceof Byte
				|| value instanceof Short || value instanceof Integer) {
			return ((Number) value).longValue();
		} else if (value instanceof Character) {
			return ((Character) value).charValue();
		} else if (value instanceof Float) {
			return toLong(((Float) value).floatValue());
		} else if (value instanceof Double) {
			return toLong(((Double) value).doubleValue());
		} else if (value instanceof CharSequence) {
			return toDate(value.toString());
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getDate();
		} else {
			throw new ValueConvertException();
		}
	}

	/**
	 * 转换成长整形
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回长整形
	 */

	public static long toLong(char value) {
		return value;
	}

	public static long toLong(boolean value) {
		return value ? 1L : 0L;
	}

	public static long toLong(short value) {
		return value;
	}

	public static long toLong(int value) {
		return value;
	}

	public static long toLong(long value) {
		return value;
	}

	public static long dateToLong(long value) {
		return value;
	}

	public static long toLong(float value) {
		if (Long.MIN_VALUE <= value && value <= Long.MAX_VALUE) {
			return (long) value;
		}
		throw new ValueConvertException();
	}

	public static long toLong(double value) {
		if (Long.MIN_VALUE <= value && value <= Long.MAX_VALUE) {
			return (long) value;
		}
		throw new ValueConvertException();
	}

	public static long toLong(byte value) {
		return value;
	}

	public static long toLong(byte[] value) {
		throw new ValueConvertException();
	}

	public static long toLong(String value) {
		return value != null && value.length() != 0 ? Long.parseLong(value)
				: 0L;
	}

	public static long toLong(GUID value) {
		throw new ValueConvertException();
	}

	public static long toLong(ReadableValue value) {
		return value != null ? value.getLong() : 0L;
	}

	public static long toLong(Object value) {
		if (value == null) {
			return 0L;
		} else if (value instanceof Character) {
			return ((Character) value).charValue();
		} else if (value instanceof Byte || value instanceof Short
				|| value instanceof Integer || value instanceof Long) {
			return ((Number) value).longValue();
		} else if (value instanceof Double) {
			return toLong(((Double) value).doubleValue());
		} else if (value instanceof Float) {
			return toLong(((Float) value).floatValue());
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? 1L : 0L;
		} else if (value instanceof String) {
			return toLong(value.toString());
		} else if (value instanceof Date) {
			return ((Date) value).getTime();
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getLong();
		} else if (value instanceof Enum<?>) {
			return ((Enum<?>) value).ordinal();
		} else {
			throw new ValueConvertException();
		}
	}

	/**
	 * 转换成字符
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回字符
	 */
	public static char toChar(char value) {
		return value;
	}

	public static char toChar(boolean value) {
		return (char) (value ? 1 : 0);
	}

	public static char toChar(short value) {
		if (Character.MIN_VALUE <= value && value <= Character.MAX_VALUE) {
			return (char) value;
		}
		throw new ValueConvertException();
	}

	public static char toChar(int value) {
		if (Character.MIN_VALUE <= value && value <= Character.MAX_VALUE) {
			return (char) value;
		}
		throw new ValueConvertException();
	}

	public static char toChar(long value) {
		if (Character.MIN_VALUE <= value && value <= Character.MAX_VALUE) {
			return (char) value;
		}
		throw new ValueConvertException();
	}

	public static char dateToChar(long value) {
		if (Character.MIN_VALUE <= value && value <= Character.MAX_VALUE) {
			return (char) value;
		}
		throw new ValueConvertException();
	}

	public static char toChar(float value) {
		if (Character.MIN_VALUE <= value && value <= Character.MAX_VALUE) {
			return (char) value;
		}
		throw new ValueConvertException();
	}

	public static char toChar(double value) {
		if (Character.MIN_VALUE <= value && value <= Character.MAX_VALUE) {
			return (char) value;
		}
		throw new ValueConvertException();
	}

	public static char toChar(String value) {
		if (value != null && value.length() == 1) {
			return value.charAt(0);
		}
		throw new ValueConvertException();
	}

	public static char toChar(byte value) {
		if (Character.MIN_VALUE <= value && value <= Character.MAX_VALUE) {
			return (char) value;
		}
		throw new ValueConvertException();
	}

	public static char toChar(byte[] value) {
		throw new ValueConvertException();
	}

	public static char toChar(GUID value) {
		throw new ValueConvertException();
	}

	public static char toChar(ReadableValue value) {
		throw new ValueConvertException();
	}

	public static char toChar(Object value) {
		if (value == null) {
			return 0;
		} else if (value instanceof Character) {
			return ((Character) value).charValue();
		} else if (value instanceof Boolean) {
			return toChar(((Boolean) value).booleanValue());
		} else if (value instanceof Byte) {
			return toChar(((Byte) value).byteValue());
		} else if (value instanceof Short) {
			return toChar(((Short) value).shortValue());
		} else if (value instanceof Integer) {
			return toChar(((Integer) value).intValue());
		} else if (value instanceof Long) {
			return toChar(((Long) value).longValue());
		} else if (value instanceof Float) {
			return toChar(((Float) value).floatValue());
		} else if (value instanceof Double) {
			return toChar(((Double) value).doubleValue());
		} else if (value instanceof String) {
			return toChar(((String) value));
		} else {
			throw new ValueConvertException();
		}
	}

	/**
	 * 转换成字符窜
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回字符串
	 */
	public static String toString(char value) {
		return new String(new char[] { value });
	}

	public static String toString(boolean value) {
		return Boolean.toString(value);
	}

	public static String toString(short value) {
		return Integer.toString(value);
	}

	public static String toString(int value) {
		return Integer.toString(value);
	}

	public static String toString(long value) {
		return Long.toString(value);
	}

	public static String dateToString(long value) {
		return DateParser.format(value);
	}

	public static String toString(float value) {
		return Float.toString(value);
	}

	public static String toString(double value) {
		return Double.toString(value);
	}

	public static String toString(String value) {
		return value;
	}

	public static String toString(byte value) {
		return Integer.toString(value);
	}

	public static String toString(byte[] value) {
		return value == null ? null : value.length == 0 ? "" : Convert
				.bytesToHex(value, false, true);
	}

	public static String toString(GUID value) {
		return value == null ? null : value.toString();
	}

	public static String toString(ReadableValue value) {
		return value == null ? null : value.getString();

	}

	public static String toString(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Character) {
			return ((Character) value).toString();
		} else if (value instanceof Number || value instanceof CharSequence
				|| value instanceof Date || value instanceof GUID
				|| value instanceof Boolean) {
			return value.toString();
		} else if (value instanceof byte[]) {
			return new String((byte[]) value);
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getString();
		} else {
			throw new ValueConvertException();
		}
	}

	/**
	 * 转换成对象
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回对象
	 */
	public static Object toObject(char value) {
		return Character.valueOf(value);
	}

	public static Object toObject(boolean value) {
		return Boolean.valueOf(value);
	}

	public static Object toObject(short value) {
		return Short.valueOf(value);
	}

	public static Object toObject(int value) {
		return Integer.valueOf(value);
	}

	public static Object toObject(long value) {
		return Long.valueOf(value);
	}

	public static Object dateToObject(long value) {
		return new Date(value);
	}

	public static Object toObject(float value) {
		return Float.valueOf(value);
	}

	public static Object toObject(double value) {
		return Double.valueOf(value);
	}

	public static Object toObject(byte value) {
		return Byte.valueOf(value);
	}

	public static Object toObject(byte[] value) {
		return value;
	}

	public static Object toObject(String value) {
		return value;
	}

	public static Object toObject(GUID value) {
		return value;
	}

	public static Object toObject(ReadableValue value) {
		return value;
	}

	public static Object toObject(Object value) {
		return value;
	}

	/**
	 * 转换成Byte
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回Byte
	 */
	public static byte toByte(char value) {
		if (value <= Byte.MAX_VALUE) {
			return (byte) value;
		}
		throw new ValueConvertException();
	}

	public static byte toByte(boolean value) {
		return value ? (byte) 1 : (byte) 0;
	}

	public static byte toByte(short value) {
		if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE) {
			return (byte) value;
		}
		throw new ValueConvertException();
	}

	public static byte toByte(int value) {
		if (Integer.MIN_VALUE <= value && value <= Integer.MAX_VALUE) {
			return (byte) value;
		}
		throw new ValueConvertException();
	}

	public static byte toByte(long value) {
		if (Long.MIN_VALUE <= value && value <= Long.MAX_VALUE) {
			return (byte) value;
		}
		throw new ValueConvertException();

	}

	public static byte dateToByte(long value) {
		if (Long.MIN_VALUE <= value && value <= Long.MAX_VALUE) {
			return (byte) value;
		}
		throw new ValueConvertException();

	}

	public static byte toByte(float value) {
		if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE) {
			return (byte) value;
		}
		throw new ValueConvertException();

	}

	public static byte toByte(double value) {
		if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE) {
			return (byte) value;
		}
		throw new ValueConvertException();

	}

	public static byte toByte(String value) {
		return value != null && value.length() != 0 ? Byte.parseByte(value) : 0;
	}

	public static byte toByte(byte value) {
		return value;
	}

	public static byte toByte(byte[] value) {
		throw new ValueConvertException();
	}

	public static byte toByte(GUID value) {
		throw new ValueConvertException();
	}

	public static byte toByte(ReadableValue value) {
		return value != null ? value.getByte() : null;
	}

	public static byte toByte(Object value) {
		if (value == null) {
			return 0;
		} else if (value instanceof Character) {
			return toByte(((Character) value).charValue());
		} else if (value instanceof Byte) {
			return ((Byte) value).byteValue();
		} else if (value instanceof Short) {
			return toByte(((Short) value).shortValue());
		} else if (value instanceof Integer) {
			return toByte(((Integer) value).intValue());
		} else if (value instanceof Long) {
			return toByte(((Long) value).longValue());
		} else if (value instanceof Float) {
			return toByte(((Float) value).floatValue());
		} else if (value instanceof Double) {
			return toByte(((Double) value).doubleValue());
		} else if (value instanceof Date) {
			return dateToByte(((Date) value).getTime());
		} else if (value instanceof String) {
			return toByte((String) value);
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? (byte) 1 : (byte) 0;
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getByte();
		} else if (value instanceof Enum<?>) {
			return (byte) ((Enum<?>) value).ordinal();
		} else {
			throw new ValueConvertException();
		}
	}

	/**
	 * 转换成Bytes
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回Byte[]
	 */
	public static byte[] toBytes(char value) {
		throw new ValueConvertException();
	}

	public static byte[] toBytes(boolean value) {
		throw new ValueConvertException();
	}

	public static byte[] toBytes(short value) {
		throw new ValueConvertException();
	}

	public static byte[] toBytes(int value) {
		throw new ValueConvertException();
	}

	public static byte[] toBytes(long value) {
		throw new ValueConvertException();
	}

	public static byte[] dateToBytes(long value) {
		throw new ValueConvertException();
	}

	public static byte[] toBytes(float value) {
		throw new ValueConvertException();
	}

	public static byte[] toBytes(double value) {
		throw new ValueConvertException();
	}

	private static final byte[] emptyBytes = {};

	public static byte[] toBytes(String value) {
		if (value == null) {
			return null;
		} else if (value.length() == 0) {
			return emptyBytes;
		} else {
			return hexToBytes(value, 0, value.length());
		}
	}

	public static byte[] toBytes(byte value) {
		throw new ValueConvertException();
	}

	public static byte[] toBytes(byte[] value) {
		return value;
	}

	public static byte[] toBytes(GUID value) {
		return value != null ? value.toBytes() : null;
	}

	public static byte[] toBytes(ReadableValue value) {
		return value != null ? value.getBytes() : null;
	}

	public static byte[] toBytes(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof byte[]) {
			return (byte[]) value;
		} else if (value instanceof GUID) {
			return ((GUID) value).toBytes();
		} else if (value instanceof String) {
			return toBytes((String) value);
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getBytes();
		} else {
			throw new ValueConvertException();
		}
	}

	/**
	 * 转换成GUID
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回GUID
	 */
	public static GUID toGUID(char value) {
		throw new ValueConvertException();
	}

	public static GUID toGUID(boolean value) {
		throw new ValueConvertException();
	}

	public static GUID toGUID(short value) {
		throw new ValueConvertException();
	}

	public static GUID toGUID(int value) {
		throw new ValueConvertException();
	}

	public static GUID toGUID(long value) {
		throw new ValueConvertException();
	}

	public static GUID dateToGUID(long value) {
		throw new ValueConvertException();
	}

	public static GUID toGUID(float value) {
		throw new ValueConvertException();
	}

	public static GUID toGUID(double value) {
		throw new ValueConvertException();
	}

	public static GUID toGUID(String value) {
		return value != null && value.length() != 0 ? GUID.valueOf(value)
				: null;
	}

	public static GUID toGUID(byte value) {
		throw new ValueConvertException();
	}

	public static GUID toGUID(byte[] value) {
		return GUID.valueOf(value);
	}

	public static GUID toGUID(GUID value) {
		return value;
	}

	public static GUID toGUID(ReadableValue value) {
		return value != null ? value.getGUID() : null;
	}

	public static GUID toGUID(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof GUID) {
			return (GUID) value;
		} else if (value instanceof String) {
			return toGUID((String) value);
		} else if (value instanceof byte[]) {
			return toGUID((byte[]) value);
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getGUID();
		} else {
			throw new ValueConvertException();
		}
	}

	/**
	 * 转换成Boolean
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回boolean
	 */
	public static boolean toBoolean(char value) {
		return value != 0;
	}

	public static boolean toBoolean(boolean value) {
		return value;
	}

	public static boolean toBoolean(short value) {
		return value != 0;
	}

	public static boolean toBoolean(int value) {
		return value != 0;
	}

	public static boolean toBoolean(long value) {
		return value != 0L;
	}

	public static boolean dateToBoolean(long value) {
		throw new ValueConvertException();
	}

	public static boolean toBoolean(float value) {
		return value != 0.0;
	}

	public static boolean toBoolean(double value) {
		return value != 0.0;
	}

	public static boolean toBoolean(String value) {
		return value != null && value != "" ? Boolean.parseBoolean(value)
				: false;
	}

	public static boolean toBoolean(byte value) {
		return value != 0;
	}

	public static boolean toBoolean(byte[] value) {
		throw new ValueConvertException();
	}

	public static boolean toBoolean(GUID value) {
		throw new ValueConvertException();
	}

	public static boolean toBoolean(ReadableValue value) {
		return value != null ? value.getBoolean() : false;
	}

	public static boolean toBoolean(Object value) {
		if (value == null) {
			return false;
		} else if (value instanceof Character) {
			return ((Character) value).charValue() != 0;
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		} else if (value instanceof Number) {
			return ((Number) value).doubleValue() != 0.0d;
		} else if (value instanceof String) {
			return Boolean.parseBoolean(value.toString());
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getBoolean();
		} else {
			throw new ValueConvertException();
		}
	}

	final static int h2b_A_10 = 'A' - 10;
	final static int h2b_a_10 = 'a' - 10;

	static int parseChar(String s, int offset) throws ValueConvertException,
			StringIndexOutOfBoundsException {
		char c = s.charAt(offset);
		if (c < '0') {
		} else if (c <= '9') {
			return c - '0';
		} else if (c < 'A') {
		} else if (c <= 'F') {
			return c - h2b_A_10;
		} else if (c < 'a') {
		} else if (c <= 'f') {
			return c - h2b_a_10;
		}
		throw new ValueConvertException("在偏移量" + offset + "处出现无效的十六进制字符'" + c
				+ "'");
	}

	public static byte[] hexToBytes(String s, int offset, int length) {
		if (s == null) {
			throw new NullPointerException();
		}
		if (length < 0 || offset < 0 || s.length() < offset + length) {
			throw new IllegalArgumentException("无效的字符串区间");
		}
		if (length == 0) {
			return emptyBytes;
		}
		int bytesLen = length + 1 >> 1;
		byte[] bytes = new byte[bytesLen];
		int i = 0;
		if ((length & 1) == 1) {
			bytes[i++] = (byte) parseChar(s, offset++);
		}
		while (i < bytesLen) {
			bytes[i++] = (byte) (parseChar(s, offset++) << 4 | Convert
					.parseChar(s, offset++));
		}
		return bytes;
	}

	static void byteToHex(char[] hex, int index, byte b) {
		int h = b >>> 4 & 0xF;
		hex[index] = (char) (h > 9 ? h + h2b_a_10 : h + '0');
		h = b & 0xF;
		hex[index + 1] = (char) (h > 9 ? h + h2b_a_10 : h + '0');
	}

	static void byteToHexUpper(char[] hex, int index, byte b) {
		int h = b >>> 4 & 0xF;
		hex[index] = (char) (h > 9 ? h + h2b_A_10 : h + '0');
		h = b & 0xF;
		hex[index + 1] = (char) (h > 9 ? h + h2b_A_10 : h + '0');
	}

	public static String bytesToHex(byte[] bytes, boolean withPrefix,
			boolean upperCase) {
		if (bytes == null) {
			throw new NullPointerException();
		}
		if (bytes.length == 0) {
			if (withPrefix) {
				return "0x";
			} else {
				return "";
			}
		}
		char[] hex = new char[bytes.length * 2 + (withPrefix ? 2 : 0)];
		int j = 0;
		if (withPrefix) {
			hex[j++] = '0';
			hex[j++] = 'x';
		}
		if (upperCase) {
			for (byte b : bytes) {
				byteToHexUpper(hex, j, b);
				j += 2;
			}
		} else {
			for (byte b : bytes) {
				byteToHex(hex, j, b);
				j += 2;
			}
		}
		return Utils.fastString(hex);
	}

	public static String bytesToBase64(byte[] a) {
		int aLen = a.length;
		if (aLen == 0) {
			return "";
		}
		int numFullGroups = aLen / 3;
		int numBytesInPartialGroup = aLen - 3 * numFullGroups;
		int resultLen = 4 * ((aLen + 2) / 3);
		char[] result = new char[resultLen];
		int inCursor = 0;
		int outCursor = 0;
		for (int i = 0; i < numFullGroups; i++) {
			int byte0 = a[inCursor++] & 0xff;
			int byte1 = a[inCursor++] & 0xff;
			int byte2 = a[inCursor++] & 0xff;
			result[outCursor++] = intToBase64[byte0 >>> 2];
			result[outCursor++] = intToBase64[byte0 << 4 & 0x3f | byte1 >>> 4];
			result[outCursor++] = intToBase64[byte1 << 2 & 0x3f | byte2 >>> 6];
			result[outCursor++] = intToBase64[byte2 & 0x3f];
		}
		if (numBytesInPartialGroup != 0) {
			int byte0 = a[inCursor++] & 0xff;
			result[outCursor++] = intToBase64[byte0 >>> 2];
			if (numBytesInPartialGroup == 1) {
				result[outCursor++] = intToBase64[byte0 << 4 & 0x3f];
				result[outCursor++] = '=';
				result[outCursor++] = '=';
			} else {
				int byte1 = a[inCursor++] & 0xff;
				result[outCursor++] = intToBase64[byte0 << 4 & 0x3f
						| byte1 >>> 4];
				result[outCursor++] = intToBase64[byte1 << 2 & 0x3f];
				result[outCursor++] = '=';
			}
		}
		return Utils.fastString(result);
	}

	public static int bytesToBase64(byte[] a, StringBuilder str) {
		final int aLen = a.length;
		if (aLen == 0) {
			return 0;
		}
		final int numFullGroups = aLen / 3;
		final int resultLen = 4 * ((aLen + 2) / 3);
		str.ensureCapacity(str.length() + resultLen);
		int inCursor = 0;
		for (int i = 0; i < numFullGroups; i++) {
			int byte0 = a[inCursor++] & 0xff;
			int byte1 = a[inCursor++] & 0xff;
			int byte2 = a[inCursor++] & 0xff;
			str.append(intToBase64[byte0 >>> 2]);
			str.append(intToBase64[byte0 << 4 & 0x3f | byte1 >>> 4]);
			str.append(intToBase64[byte1 << 2 & 0x3f | byte2 >>> 6]);
			str.append(intToBase64[byte2 & 0x3f]);
		}
		final int numBytesInPartialGroup = aLen - 3 * numFullGroups;
		if (numBytesInPartialGroup != 0) {
			int byte0 = a[inCursor++] & 0xff;
			str.append(intToBase64[byte0 >>> 2]);
			if (numBytesInPartialGroup == 1) {
				str.append(intToBase64[byte0 << 4 & 0x3f]);
				str.append('=');
				str.append('=');
			} else {
				int byte1 = a[inCursor++] & 0xff;
				str.append(intToBase64[byte0 << 4 & 0x3f | byte1 >>> 4]);
				str.append(intToBase64[byte1 << 2 & 0x3f]);
				str.append('=');
			}
		}
		return resultLen;
	}

	public static byte[] base64ToBytes(CharSequence s) {
		int sLen = s.length();
		int numGroups = sLen / 4;
		if (4 * numGroups != sLen) {
			throw new IllegalArgumentException(
					"String length must be a multiple of four.");
		}
		int missingBytesInLastGroup = 0;
		int numFullGroups = numGroups;
		if (sLen != 0) {
			if (s.charAt(sLen - 1) == '=') {
				missingBytesInLastGroup++;
				numFullGroups--;
			}
			if (s.charAt(sLen - 2) == '=') {
				missingBytesInLastGroup++;
			}
		}
		byte[] result = new byte[3 * numGroups - missingBytesInLastGroup];
		// Translate all full groups from base64 to byte array elements
		int inCursor = 0, outCursor = 0;
		for (int i = 0; i < numFullGroups; i++) {
			int ch0 = base64ToInt[s.charAt(inCursor++)];
			int ch1 = base64ToInt[s.charAt(inCursor++)];
			int ch2 = base64ToInt[s.charAt(inCursor++)];
			int ch3 = base64ToInt[s.charAt(inCursor++)];
			if (ch0 < 0 || ch1 < 0 || ch2 < 0 || ch3 < 0) {
				throw new IllegalArgumentException("Illegal base64 character");
			}
			result[outCursor++] = (byte) (ch0 << 2 | ch1 >>> 4);
			result[outCursor++] = (byte) (ch1 << 4 | ch2 >>> 2);
			result[outCursor++] = (byte) (ch2 << 6 | ch3);
		}
		// Translate partial group, if present
		if (missingBytesInLastGroup != 0) {
			int ch0 = base64ToInt[s.charAt(inCursor++)];
			int ch1 = base64ToInt[s.charAt(inCursor++)];
			if (ch0 < 0 || ch1 < 0) {
				throw new IllegalArgumentException("Illegal base64 character");
			}
			result[outCursor++] = (byte) (ch0 << 2 | ch1 >>> 4);
			if (missingBytesInLastGroup == 1) {
				int ch2 = base64ToInt[s.charAt(inCursor++)];
				if (ch2 < 0) {
					throw new IllegalArgumentException(
							"Illegal base64 character");
				}
				result[outCursor++] = (byte) (ch1 << 4 | ch2 >>> 2);
			}
		}
		// assert inCursor == s.length()-missingBytesInLastGroup;
		// assert outCursor == result.length;
		return result;
	}

	/**
	 * This array is a lookup table that translates 6-bit positive integer index
	 * values into their "Base64 Alphabet" equivalents as specified in Table 1
	 * of RFC 2045.
	 */
	private static final char intToBase64[] = { 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '+', '/' };
	/**
	 * This array is a lookup table that translates unicode characters drawn
	 * from the "Base64 Alphabet" (as specified in Table 1 of RFC 2045) into
	 * their 6-bit positive integer equivalents. Characters that are not in the
	 * Base64 alphabet but fall within the bounds of the array are translated to
	 * -1.
	 */
	private static final byte base64ToInt[] = { -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1,
			-1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
			13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1,
			-1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
			41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };

	public final static Charset utf8 = Charset.forName("UTF8");

	public final static byte[] charsToUTF8(CharSequence chars) {
		ByteBuffer bb = utf8.encode(CharBuffer.wrap(chars));
		byte[] result = new byte[bb.remaining()];
		bb.get(result);
		return result;
	}

	public final static byte[] charsToUTF8(char[] chars) {
		ByteBuffer bb = utf8.encode(CharBuffer.wrap(chars));
		byte[] result = new byte[bb.remaining()];
		bb.get(result);
		return result;
	}

	public final static String UTF8ToString(byte[] bytes) {
		return utf8.decode(ByteBuffer.wrap(bytes)).toString();
	}

	private Convert() {
	}

	/**
	 * 转换成单精度
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回单精度值
	 */
	public static float toFloat(char value) {
		return value;
	}

	public static float toFloat(boolean value) {
		return value ? 1.0f : 0.0f;
	}

	public static float toFloat(short value) {
		return value;
	}

	public static float toFloat(int value) {
		return value;
	}

	public static float toFloat(long value) {
		return value;
	}

	public static float dateToFloat(long value) {
		return value;
	}

	public static float toFloat(float value) {
		return value;
	}

	public static float toFloat(double value) {
		// 可能截取
		return (float) value;
	}

	public static float toFloat(byte value) {
		return value;
	}

	public static float toFloat(byte[] value) {
		throw new ValueConvertException();
	}

	public static float toFloat(String value) {
		return value != null && value.length() != 0 ? Float.parseFloat(value)
				: 0.0f;
	}

	public static float toFloat(GUID value) {
		throw new ValueConvertException();
	}

	public static float toFloat(ReadableValue value) {
		return value != null ? value.getFloat() : 0.0f;
	}

	public static float toFloat(Object value) {
		if (value == null) {
			return 0.0f;
		} else if (value instanceof Character) {
			return ((Character) value).charValue();
		} else if (value instanceof Byte || value instanceof Short
				|| value instanceof Integer || value instanceof Float
				|| value instanceof Long) {
			return ((Number) value).floatValue();
		} else if (value instanceof Double) {
			return toFloat(((Double) value).doubleValue());
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? 1.0f : 0.0f;
		} else if (value instanceof String) {
			return toFloat(value.toString());
		} else if (value instanceof Date) {
			return ((Date) value).getTime();
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getFloat();
		} else {
			throw new ValueConvertException();
		}
	}

	/**
	 * 转换成短整形
	 * 
	 * @param value
	 *            待转换的值
	 * @return 返回短整形
	 */
	public static short toShort(char value) {
		return (short) value;
	}

	public static short toShort(boolean value) {
		return value ? (short) 1 : (short) 0;
	}

	public static short toShort(short value) {
		return value;
	}

	public static short toShort(int value) {
		if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
			return (short) value;
		}
		throw new ValueConvertException();
	}

	public static short toShort(long value) {
		if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
			return (short) value;
		}
		throw new ValueConvertException();
	}

	public static short dateToShort(long value) {
		if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
			return (short) value;
		}
		throw new ValueConvertException();
	}

	public static short toShort(float value) {
		if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
			return (short) value;
		}
		throw new ValueConvertException();
	}

	public static short toShort(double value) {
		if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
			return (short) value;
		}
		throw new ValueConvertException();
	}

	public static short toShort(byte value) {
		return value;
	}

	public static short toShort(byte[] value) {
		throw new ValueConvertException();
	}

	public static short toShort(String value) {
		return value != null && value.length() != 0 ? Short.parseShort(value)
				: 0;
	}

	public static short toShort(GUID value) {
		throw new ValueConvertException();
	}

	public static short toShort(ReadableValue value) {
		return value != null ? value.getShort() : 0;
	}

	public static short toShort(Object value) {
		if (value == null) {
			return 0;
		} else if (value instanceof Character) {
			return toShort(((Character) value).charValue());
		} else if (value instanceof Byte || value instanceof Short) {
			return ((Number) value).shortValue();
		} else if (value instanceof Double || value instanceof Float
				|| value instanceof Long || value instanceof Integer) {
			return toShort(((Number) value).doubleValue());
		} else if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue() ? (short) 1 : (short) 0;
		} else if (value instanceof String) {
			return toShort(value.toString());
		} else if (value instanceof Date) {
			return toShort(((Date) value).getTime());
		} else if (value instanceof ReadableValue) {
			return ((ReadableValue) value).getShort();
		} else {
			throw new ValueConvertException();
		}
	}

	public static Object toType(DataType type, Object from) {
		if (from == null) {
			return null;
		}
		return type.detect(objConverter, from);
	}

	private static final TypeDetector<Object, Object> objConverter = new TypeDetectorBase<Object, Object>() {

		@Override
		public final Object inBoolean(Object userData) {
			if (userData instanceof Boolean) {
				return userData;
			} else {
				return toBoolean(userData);
			}
		}

		@Override
		public Object inByte(Object userData) {
			if (userData instanceof Byte) {
				return userData;
			} else {
				return toByte(userData);
			}
		}

		@Override
		public Object inBytes(Object userData, SequenceDataType type) {
			if (userData instanceof byte[]) {
				return userData;
			} else {
				return toBytes(userData);
			}
		}

		@Override
		public Object inDate(Object userData) {
			if (userData instanceof Long) {
				return userData;
			} else if (userData instanceof Date) {
				return ((Date) userData).getTime();
			} else {
				return toDate(userData);
			}
		}

		@Override
		public Object inDouble(Object userData) {
			if (userData instanceof Double) {
				return userData;
			} else {
				return toDouble(userData);
			}
		}

		@Override
		public Object inEnum(Object userData, EnumType<?> type) {
			if (type.isInstance(userData)) {
				return userData;
			} else if (userData instanceof String) {
				try {
					type.getEnum((String) userData);
				} catch (Throwable e) {
					throw new ValueConvertException(e);
				}
			} else if (userData instanceof Integer || userData instanceof Byte
					|| userData instanceof Short) {
				try {
					type.getEnum(((Number) userData).intValue());
				} catch (Throwable e) {
					throw new ValueConvertException(e);
				}
			}
			throw new ValueConvertException();
		}

		@Override
		public Object inFloat(Object userData) {
			if (userData instanceof Float) {
				return userData;
			} else {
				return toFloat(userData);
			}
		}

		@Override
		public Object inGUID(Object userData) {
			if (userData instanceof GUID) {
				return userData;
			} else {
				return toGUID(userData);
			}
		}

		@Override
		public Object inInt(Object userData) {
			if (userData instanceof Integer) {
				return userData;
			} else {
				return toInt(userData);
			}
		}

		@Override
		public Object inLong(Object userData) {
			if (userData instanceof Long) {
				return userData;
			} else {
				return toLong(userData);
			}
		}

		@Override
		public Object inShort(Object userData) {
			if (userData instanceof Short) {
				return userData;
			} else {
				return toShort(userData);
			}
		}

		@Override
		public Object inString(Object userData, SequenceDataType type) {
			if (userData instanceof String) {
				return userData;
			} else {
				return Convert.toString(userData);
			}
		}

		@Override
		public Object inStruct(Object userData, StructDefine type) {
			if (type.isInstance(userData)) {
				return userData;
			} else {
				Object o = type.tryConvert(userData);
				if (o != null) {
					return o;
				}
				throw new ValueConvertException();
			}
		}

		@Override
		public Object inObject(Object userData, ObjectDataType type)
				throws Throwable {
			return userData;
		}

		@Override
		public Object inUnknown(Object userData) {
			return userData;
		}
	};
}