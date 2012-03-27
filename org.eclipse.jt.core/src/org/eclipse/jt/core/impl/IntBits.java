package org.eclipse.jt.core.impl;

/**
 * @author Jeff Tang
 * 
 */
final class IntBits {

	private int bits;

	final boolean isEmpty() {
		return this.bits == 0;
	}

	final void set(int index) {
		checkRange(index);
		this.bits |= 1 << index;
	}

	final boolean get(int index) {
		checkRange(index);
		return (this.bits & (1 << index)) != 0;
	}

	final void clear() {
		this.bits = 0;
	}

	final int cardinality() {
		return Integer.bitCount(this.bits);
	}

	final int nextSetBit(int fromIndex) {
		int mask = 0;
		for (int i = fromIndex; i < 32; i++) {
			mask = 1 << i;
			if ((this.bits & mask) > 0) {
				return i;
			}
		}
		return -1;
	}

	final void or(IntBits bits) {
		this.bits |= bits.bits;
	}

	private static final void checkRange(int index) {
		if (index < 0 || index > 31) {
			throw new IndexOutOfBoundsException();
		}
	}

}
