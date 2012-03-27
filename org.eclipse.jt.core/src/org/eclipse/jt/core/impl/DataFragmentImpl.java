package org.eclipse.jt.core.impl;

import sun.misc.Unsafe;

/**
 * ���ݶ�ʵ��
 * 
 * @author Jeff Tang
 */
final class DataFragmentImpl implements DataFragment {

	/**
	 * �������ݶ�
	 * 
	 * @param capacity
	 *            ���ݶγ��ȣ�����Ϊ����
	 */
	DataFragmentImpl(final int capacity) {
		this.fragment = new byte[capacity];
		this.position = 0;
		this.remain = capacity;
	}

	public final int getAvailableOffset() {
		return 0;
	}

	public final int getAvailableLength() {
		return this.fragment.length;
	}

	public final byte[] getBytes() {
		return this.fragment;
	}

	public final int getPosition() {
		return this.position;
	}

	public final void setPosition(final int position) {
		this.seek(position);
	}

	public final int remain() {
		return this.remain;
	}

	public final void limit(final int position) {
		final int oldPosition = this.position;
		if (position < oldPosition
				|| this.getAvailableOffset() + this.getAvailableLength() < position) {
			throw new IllegalArgumentException("position");
		}
		this.remain = position - oldPosition;
	}

	public final byte readByte() {
		final byte result = UNSAFE.getByte(this.fragment,
				START_OFFSET_BYTEARRAY + this.position);
		this.position += 1;
		this.remain -= 1;
		return result;
	}

	public final short readShort() {
		final short result = UNSAFE.getShort(this.fragment,
				START_OFFSET_BYTEARRAY + this.position);
		this.position += 2;
		this.remain -= 2;
		return result;
	}

	public final char readChar() {
		final char result = UNSAFE.getChar(this.fragment,
				START_OFFSET_BYTEARRAY + this.position);
		this.position += 2;
		this.remain -= 2;
		return result;
	}

	public final int readInt() {
		final int result = UNSAFE.getInt(this.fragment, START_OFFSET_BYTEARRAY
				+ this.position);
		this.position += 4;
		this.remain -= 4;
		return result;
	}

	public final float readFloat() {
		final float result = UNSAFE.getFloat(this.fragment,
				START_OFFSET_BYTEARRAY + this.position);
		this.position += 4;
		this.remain -= 4;
		return result;
	}

	public final long readLong() {
		final long result = UNSAFE.getLong(this.fragment,
				START_OFFSET_BYTEARRAY + this.position);
		this.position += 8;
		this.remain -= 8;
		return result;
	}

	public final double readDouble() {
		final double result = UNSAFE.getDouble(this.fragment,
				START_OFFSET_BYTEARRAY + this.position);
		this.position += 8;
		this.remain -= 8;
		return result;
	}

	public final void writeByte(final byte value) {
		UNSAFE.putByte(this.fragment, START_OFFSET_BYTEARRAY + this.position,
				value);
		this.position += 1;
		this.remain -= 1;
	}

	public final void writeShort(final short value) {
		UNSAFE.putShort(this.fragment, START_OFFSET_BYTEARRAY + this.position,
				value);
		this.position += 2;
		this.remain -= 2;
	}

	public final void writeChar(final char value) {
		UNSAFE.putChar(this.fragment, START_OFFSET_BYTEARRAY + this.position,
				value);
		this.position += 2;
		this.remain -= 2;
	}

	public final void writeInt(final int value) {
		UNSAFE.putInt(this.fragment, START_OFFSET_BYTEARRAY + this.position,
				value);
		this.position += 4;
		this.remain -= 4;
	}

	public final void writeFloat(final float value) {
		UNSAFE.putFloat(this.fragment, START_OFFSET_BYTEARRAY + this.position,
				value);
		this.position += 4;
		this.remain -= 4;
	}

	public final void writeLong(final long value) {
		UNSAFE.putLong(this.fragment, START_OFFSET_BYTEARRAY + this.position,
				value);
		this.position += 8;
		this.remain -= 8;
	}

	public final void writeDouble(final double value) {
		UNSAFE.putDouble(this.fragment, START_OFFSET_BYTEARRAY + this.position,
				value);
		this.position += 8;
		this.remain -= 8;
	}

	/**
	 * ��Զ������ݶ�дָ��
	 * 
	 * @param n
	 *            <li>Ϊ����ʱ�����ݶ�дָ����Ե�ǰλ�ú�ǰ��ȥn��λ�ã�</li> <li>
	 *            Ϊ����ʱ�����ݶ�дָ����Ե�ǰλ�ú����ȥn��λ��</li>
	 * @return �����ƶ�ǰ�����ݶ�дָ���λ��
	 */
	final int skip(final int n) {
		return n == 0 ? this.position : this.seek(this.position + n);
	}

	private final int seek(final int position) {
		final int oldPosition = this.position;
		final int oldRemain = this.remain;
		if (position < this.getAvailableOffset()
				|| oldPosition + oldRemain < position) {
			throwIndexOutOfBoundsException(position, this.getAvailableOffset(),
					oldPosition + oldRemain - 1);
		}
		this.position = position;
		this.remain += oldPosition - position;
		return oldPosition;
	}

	private static final void throwIndexOutOfBoundsException(final int index,
			final int startIndex, final int endIndex) {
		throw new IndexOutOfBoundsException(index + "/[" + startIndex + ".."
				+ endIndex + "]");
	}

	private static final Unsafe UNSAFE = Unsf.unsafe;

	private static final long START_OFFSET_BYTEARRAY = UNSAFE
			.arrayBaseOffset(byte[].class);

	final byte[] fragment;

	int position;

	int remain;

}
