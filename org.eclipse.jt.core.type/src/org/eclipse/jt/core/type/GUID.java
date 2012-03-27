package org.eclipse.jt.core.type;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.BufferUnderflowException;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.MessageDigest;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.Comparator;

import sun.misc.Unsafe;

/**
 * GUID
 * 
 * @author Jeff Tang
 * 
 */
public final class GUID extends Object implements Serializable,
        Comparable<GUID> {
	public final static Comparator<GUID> comparator = new Comparator<GUID>() {
		public int compare(GUID o1, GUID o2) {
			return o1.compareTo(o2);
		}
	};

	/**
	 * �ж��Ƿ���ͬ
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof GUID) {
			GUID one = (GUID) obj;
			return this.leastSigBits == one.leastSigBits
			        && this.mostSigBits == one.mostSigBits;
		}
		return false;
	}

	public boolean isEmpty() {
		return this.leastSigBits == 0L && this.mostSigBits == 0L;
	}

	/**
	 * �ж��Ƿ���ͬ
	 */
	public boolean equals(GUID id) {
		return (id == this)
		        || (id != null && this.leastSigBits == id.leastSigBits && this.mostSigBits == id.mostSigBits);
	}

	public byte[] toBytes() {
		return this.toBytes(null);
	}

	public byte[] toBytes(byte[] bytes) {
		if (bytes == null || bytes.length != 16) {
			bytes = new byte[16];
		}
		long sb = this.leastSigBits;
		for (int i = 15; i >= 8; i--) {
			bytes[i] = (byte) sb;
			sb >>>= 8;
		}
		sb = this.mostSigBits;
		for (int i = 7; i >= 0; i--) {
			bytes[i] = (byte) sb;
			sb >>>= 8;
		}
		return bytes;
	}

	public void toBytes(final byte[] buf, final int off) {
		if (buf == null) {
			throw new NullPointerException("buf");
		}
		final int blen = buf.length;
		if (off < 0 || off > blen) {
			throw new IndexOutOfBoundsException("buf's length: " + blen
			        + ", off: " + off);
		}
		if (blen - off < 16) {
			throw new BufferUnderflowException();
		}

		long sb = this.leastSigBits;
		for (int i = off + 15; i >= off + 8; i--) {
			buf[i] = (byte) sb;
			sb >>>= 8;
		}
		sb = this.mostSigBits;
		for (int i = off + 7; i >= off; i--) {
			buf[i] = (byte) sb;
			sb >>>= 8;
		}
	}

	/**
	 * �ж��Ƿ���ͬ
	 */
	public static boolean equals(GUID id1, GUID id2) {
		if (id1 == id2) {
			return true;
		}
		return id1 != null && id2 != null
		        && id1.leastSigBits == id2.leastSigBits
		        && id1.mostSigBits == id2.mostSigBits;
	}

	/**
	 * ����hashCode
	 */
	@Override
	public final int hashCode() {
		return (int) ((this.mostSigBits >>> 32) ^ this.mostSigBits
		        ^ (this.leastSigBits >>> 32) ^ this.leastSigBits);
	}

	public final int compareTo(GUID val) {
		return (this.mostSigBits < val.mostSigBits ? -1
		        : (this.mostSigBits > val.mostSigBits ? 1
		                : (this.leastSigBits < val.leastSigBits ? -1
		                        : (this.leastSigBits > val.leastSigBits ? 1 : 0))));
	}

	/**
	 * ��λ64λ
	 */
	private final long mostSigBits;

	/**
	 * ��λ64λ
	 */
	public final long getMostSigBits() {
		return this.mostSigBits;
	}

	/**
	 * ��λ64λ
	 */
	private final long leastSigBits;

	/**
	 * ��λ64λ
	 */
	public final long getLeastSigBits() {
		return this.leastSigBits;
	}

	@Override
	public final String toString() {
		return this.toString(false, true);
	}

	private final static int h2b_A_10 = 'A' - 10;
	private final static int h2b_a_10 = 'a' - 10;

	private static int parseChar(String s, int offset)
	        throws ValueConvertException, StringIndexOutOfBoundsException {
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
		throw new ValueConvertException("��ƫ����" + offset + "��������Ч��ʮ�������ַ�'" + c
		        + "'");
	}

	private static void byteToHex(char[] hex, int index, byte b,
	        boolean upperCase) {
		int h = b >>> 4 & 0xF;
		if (upperCase) {
			hex[index] = (char) (h > 9 ? h + h2b_A_10 : h + '0');
			h = b & 0xF;
			hex[index + 1] = (char) (h > 9 ? h + h2b_A_10 : h + '0');
		} else {
			hex[index] = (char) (h > 9 ? h + h2b_a_10 : h + '0');
			h = b & 0xF;
			hex[index + 1] = (char) (h > 9 ? h + h2b_a_10 : h + '0');
		}
	}
	
	private static void appendTo(Appendable hex, long l, boolean upperCase)
			throws IOException {
		int b = (int) (l >>> 56);
		int h = b >>> 4 & 0xf;
		if (upperCase) {
			hex.append((char) (h <= 9 ? h + 48 : h + 55));
			h = b & 0xf;
			hex.append((char) (h <= 9 ? h + 48 : h + 55));
		} else {
			hex.append((char) (h <= 9 ? h + 48 : h + 87));
			h = b & 0xf;
			hex.append((char) (h <= 9 ? h + 48 : h + 87));
		}
	}


	public final String toString(boolean withPrefix, boolean upperCase) {
		int j = withPrefix ? 34 : 32;
		char[] hex = new char[j];
		if (withPrefix) {
			hex[0] = '0';
			hex[1] = 'x';
		}
		long sb = this.leastSigBits;
		j -= 2;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb = this.mostSigBits;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		j -= 2;
		sb >>>= 8;
		byteToHex(hex, j, (byte) sb, upperCase);
		return UnsafeString.fastString(hex);
	}
	
    public final void appendTo(Appendable hex)
    {
        appendTo(hex, false, true);
    }

    public final void appendTo(Appendable hex, boolean withPrefix, boolean upperCase)
    {
        if(hex == null)
            throw new NullPointerException("hex is null");
        try
        {
            if(withPrefix)
            {
                hex.append('0');
                hex.append(upperCase ? 'X' : 'x');
            }
            long sb = mostSigBits;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb = leastSigBits;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
            sb <<= 8;
            appendTo(hex, sb, upperCase);
        }
        catch(Throwable e)
        {
            UnsafeString.unsafe.throwException(e);
        }
    }


	/**
	 * ��ID
	 */
	public static final GUID emptyID = new GUID(0, 0);

	/**
	 * ��������long����ID
	 * 
	 * @param mostSigBits
	 *            ��λ
	 * @param leastSigBits
	 *            ��λ
	 * @return ����ID
	 */
	public static GUID valueOf(final long mostSigBits, final long leastSigBits) {
		if (mostSigBits == 0 && leastSigBits == 0) {
			return emptyID;
		}
		return new GUID(mostSigBits, leastSigBits);
	}

	/**
	 * ����byte���鴴��ID
	 * 
	 * @param bytes
	 *            ������ֵ
	 * @return ����ID
	 */
	public static GUID valueOf(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		int length = bytes.length;
		if (length == 0) {
			return emptyID;
		}
		int c = length > 8 ? 8 : length;
		int index = 0;
		long msb = bytes[index++] & 0xff;
		while (index < c) {
			msb = (msb << 8) | (bytes[index++] & 0xff);
		}
		long lsb;
		if (index < length) {
			lsb = bytes[index++] & 0xff;
			while (index < length) {
				lsb = (lsb << 8) | (bytes[index++] & 0xff);
			}
		} else {
			lsb = 0;
		}
		if (msb == 0 && lsb == 0) {
			return emptyID;
		}
		return new GUID(msb, lsb);
	}

	/**
	 * ����ָ�����ֽڻ������д�ָ��λ�ÿ�ʼ��16���ֽڣ�������ֵ����GUIDʵ����
	 * 
	 * ����<code>GUID.emptyID</code>�����ᴴ����ʵ����
	 * 
	 * @param buf
	 *            �ֽڻ�����
	 * @param off
	 *            �����Ŀ�ʼλ��
	 * @return ��������GUIDʵ��
	 * @throws NullPointerException
	 *             ָ���Ļ�����Ϊ�գ�<code>null</code>��
	 * @throws IndexOutOfBoundsException
	 *             ָ���Ŀ�ʼƫ��λ��Խ��
	 * @throws BufferUnderflowException
	 *             ָ���Ļ������д�ָ���Ŀ�ʼλ��֮������ݲ����Դ���GUIDʵ��
	 */
	public static GUID valueOf(final byte[] buf, int off) {
		if (buf == null) {
			throw new NullPointerException("buf");
		}
		final int blen = buf.length;
		if (off < 0 || off > blen) {
			throw new IndexOutOfBoundsException("buf's length: " + blen
			        + ", off: " + off);
		}
		if (blen - off < 16) {
			throw new BufferUnderflowException();
		}

		int end = off + 8;
		long msb = buf[off++] & 0xff;
		while (off < end) {
			msb = (msb << 8) | (buf[off++] & 0xff);
		}
		end = off + 8;
		long lsb = buf[off++] & 0xff;
		while (off < end) {
			lsb = (lsb << 8) | (buf[off++] & 0xff);
		}
		if (msb == 0 && lsb == 0) {
			return emptyID;
		}
		return new GUID(msb, lsb);
	}

	private static class MD5Digest {
		private static Charset UTF8 = Charset.forName("UTF8");
		private final MessageDigest md5;
		private final CharsetEncoder encoder;

		final GUID digest(CharSequence message) {
			if (message == null) {
				throw new IllegalArgumentException("message is null");
			}
			if (message.length() == 0) {
				return GUID.emptyID;
			}
			try {
				this.md5.update(this.encoder.encode(CharBuffer.wrap(message)));
			} catch (CharacterCodingException e) {
				UnsafeString.unsafe.throwException(e);
				return null;
			}
			return GUID.valueOf(this.md5.digest());
		}
		
		final byte[] digestTo16Bytes(CharSequence message)
        {
            if(message == null)
                throw new IllegalArgumentException("message is null");
            if(message.length() == 0)
                return new byte[16];
            java.nio.ByteBuffer bb;
            try
            {
                bb = encoder.encode(CharBuffer.wrap(message));
            }
            catch(CharacterCodingException e)
            {
                UnsafeString.unsafe.throwException(e);
                return null;
            }
            md5.update(bb);
            return md5.digest();
        }

		final long digestToLong(CharSequence message) {
			if (message == null) {
				throw new IllegalArgumentException("message is null");
			}
			if (message.length() == 0) {
				return 0;
			}
			try {
				this.md5.update(this.encoder.encode(CharBuffer.wrap(message)));
			} catch (CharacterCodingException e) {
				UnsafeString.unsafe.throwException(e);
				return 0;
			}
			final byte[] buf = this.md5.digest();
			long msb = buf[0] & 0xff;
			for (int i = 1; i < 8; i++) {
				msb = (msb << 8) | (buf[i] & 0xff);

			}
			return msb;
		}

		MD5Digest() {
			MessageDigest md5;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (Throwable e) {
				UnsafeString.unsafe.throwException(e);
				md5 = null;
			}
			this.md5 = md5;
			this.encoder = UTF8.newEncoder();
			this.encoder.onMalformedInput(CodingErrorAction.IGNORE);
			this.encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
		}
	}

	public static void main(String[] args) {
		System.out.println(MD5Of("abc"));
		System.out.println(MD5Of("abc"));
		System.out.println(MD5Of("abc"));
		System.out.println(MD5Of("abc"));
		
		final byte[] bytes = MD5Of("abc").toBytes();
		bytes[0] = 0;
		bytes[1] |= 0x80;// ������
		System.out.println(GUID.valueOf(bytes));
		
		
	}
	
	public static byte[] MD5BytesOf(CharSequence message)
    {
        return ((MD5Digest)md5s.get()).digestTo16Bytes(message);
    }

	private static ThreadLocal<MD5Digest> md5s = new ThreadLocal<MD5Digest>() {
		@Override
		protected MD5Digest initialValue() {
			return new MD5Digest();
		};
	};

	/**
	 * �����ı�����UTF8���룬����MD5�㷨ɢ�еõ�GUID
	 * 
	 * @param message
	 *            Ϊnullʱ�׳��쳣��""ʱ����GUID.EMPTY
	 * @return
	 */
	public static GUID MD5Of(CharSequence message) {
		return md5s.get().digest(message);
	}

	public static GUID MD5Of(String message) {
		return md5s.get().digest(message);
	}

	public static long mostSigBitsMD5Of(CharSequence message) {
		return md5s.get().digestToLong(message);
	}

	private static long hexToLong(String str, int start) {
		long temp = parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		temp = temp << 4 | parseChar(str, start++);
		return temp << 4 | parseChar(str, start++);
	}

	private static GUID tryParse(String str, boolean throwException) {
		if (str == null) {
			return null;
		}
		int strl = str.length();
		if (strl == 32) {
			try {
				return valueOf(hexToLong(str, 0), hexToLong(str, 16));
			} catch (ValueConvertException e) {
				if (throwException) {
					throw e;
				}
			}
		}
		if (throwException) {
			throw new ValueConvertException("GUID�ı���ʽ����:" + str);
		}
		return null;
	}

	/**
	 * ����GUID�����Ч�򷵻�null
	 */
	public static GUID tryValueOf(String str) {
		return tryParse(str, false);
	}

	/**
	 * ����GUID�����Ч�򷵷����쳣
	 */
	public static GUID valueOf(String str) {
		return tryParse(str, true);
	}

	private final static byte[] bytes16 = new byte[16];
	private final static byte[] bytes8 = new byte[8];

	/**
	 * ���������ID
	 * 
	 * @return �������ID����
	 */
	public static GUID randomID() {
		long msb, lsb;
		synchronized (numberGenerator) {
			numberGenerator.nextBytes(bytes16);
			msb = bytes16[0] & 0xff;
			for (int i = 1; i < 8; i++) {
				msb = (msb << 8) | (bytes16[i] & 0xff);
			}
			lsb = bytes16[8] & 0xff;
			for (int i = 9; i < 16; i++) {
				lsb = (lsb << 8) | (bytes16[i] & 0xff);
			}
		}
		return new GUID(msb, lsb);

	}

	public static long randomLong() {
		synchronized (numberGenerator) {
			numberGenerator.nextBytes(bytes8);
			long msb = bytes8[0] & 0xff;
			for (int i = 1; i < 8; i++) {
				msb = (msb << 8) | (bytes8[i] & 0xff);
			}
			return msb;
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// ////////////////////
	// //////////////////�������ڲ�����/////////////////////////////////////////////////
	// ////////////////
	// //////////////////////////////////////////////////////////////////////////
	// ////////////////////
	private static final long serialVersionUID = 2686938417664634277L;
	private static final SecureRandom numberGenerator = new SecureRandom();

	/**
	 * ���캯��
	 * 
	 * @param mostSigBits
	 * @param leastSigBits
	 */
	private GUID(long mostSigBits, long leastSigBits) {
		this.mostSigBits = mostSigBits;
		this.leastSigBits = leastSigBits;
	}

	/*
	 * Added by LRJ. Mar 6th, 2008.
	 * 
	 * ���� emptyID ��Ψһ�ԡ�
	 */
	private Object readResolve() {
		if (this.leastSigBits == 0 || this.mostSigBits == 0) {
			return GUID.emptyID;
		} else {
			return this;
		}
	}

	private static class UnsafeString {
		static final Unsafe unsafe = getUnsafe();

		static final long stringValueOffset = tryGetFieldOffset(String.class,
		        "value");
		static final long stringCountOffset = tryGetFieldOffset(String.class,
		        "count");

		static final long ILLEGAL_OFFSET = -1;

		private static Unsafe getUnsafe() {
			Unsafe us;
			try {
				final Field f = Unsafe.class.getDeclaredField("theUnsafe");
				java.security.AccessController
				        .doPrivileged(new PrivilegedAction<Object>() {
					        public Object run() {
						        f.setAccessible(true);
						        return null;
					        }
				        });
				us = (Unsafe) f.get(null);
			} catch (Throwable e) {
				us = null;
			}
			return us;
		}

		private static long tryGetFieldOffset(Class<?> clazz, String fieldName) {
			if (unsafe != null) {
				try {
					return unsafe.objectFieldOffset(clazz
					        .getDeclaredField(fieldName));
				} catch (Throwable e) {
					return ILLEGAL_OFFSET;
				}
			}
			return ILLEGAL_OFFSET;
		}

		private static boolean ���Բ������Ƴ����øñ���Ϊfinal = false;

		static String fastString(char[] chars) {
			int charCount = chars.length;
			if (charCount == 0) {
				return "";
			}
			if (stringCountOffset != ILLEGAL_OFFSET
			        && stringValueOffset != ILLEGAL_OFFSET) {
				String s = "���Բ����Ըĸ��ַ���".substring(0, 0);
				unsafe.putInt(s, stringCountOffset, charCount);
				unsafe.putObject(s, stringValueOffset, chars);
				if (���Բ������Ƴ����øñ���Ϊfinal) {
					���Բ������Ƴ����øñ���Ϊfinal = false;
				}
				return s;
			} else {
				throw new IllegalAccessError();
			}
		}
	}
}