package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.util.ArrayList;

class SqlStringBuffer implements Appendable {
	static final int GROWN = 256;
	private final ArrayList<char[]> list;
	private final int grown;
	private int validCount;
	private int offset;

	public SqlStringBuffer() {
		this.grown = GROWN;
		this.list = new ArrayList<char[]>();
		this.list.add(new char[this.grown]);
		this.validCount = 1;
		this.offset = 0;
	}

	public int size() {
		return (this.validCount - 1) * this.grown + this.offset;
	}

	public void clear() {
		this.validCount = 1;
		this.offset = 0;
	}

	private char[] ensure(int i) {
		if (this.list.size() > i) {
			return this.list.get(i);
		}
		char[] buffer = new char[this.grown];
		this.list.add(buffer);
		return buffer;
	}

	public SqlStringBuffer append(char ch) {
		if (this.offset < this.grown) {
			char[] buffer = this.ensure(this.validCount - 1);
			buffer[this.offset++] = ch;
		} else {
			char[] buffer = this.ensure(this.validCount++);
			buffer[0] = ch;
			this.offset = 1;
		}
		return this;
	}

	public void set(int i, char c) {
		if (this.grown == GROWN) {
			this.ensure(i & (~(GROWN - 1)))[i & (GROWN - 1)] = c;
		} else {
			this.ensure(i / this.grown)[i % this.grown] = c;
		}
	}

	public Appendable append(CharSequence csq) throws IOException {
		if (csq instanceof String) {
			append((String) csq);
		}
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		if (csq instanceof String) {
			append((String) csq, start, end);
		}
		return this;
	}

	public SqlStringBuffer append(String s) {
		return this.append(s, 0, s.length());
	}

	public SqlStringBuffer append(String s, int start, int end) {
		int len = end - start;
		int available = this.grown - this.offset;
		if (available > end - start) {
			char[] buffer = this.ensure(this.validCount - 1);
			s.getChars(start, end, buffer, this.offset);
			this.offset += len;
		} else {
			if (available > 0) {
				char[] buffer = this.ensure(this.validCount - 1);
				s.getChars(start, start + available, buffer, this.offset);
			}
			start += available;
			while (end - start >= this.grown) {
				char[] buffer = this.ensure(this.validCount++);
				s.getChars(start, start + this.grown, buffer, 0);
				start += this.grown;
			}
			if (end > start) {
				char[] buffer = this.ensure(this.validCount++);
				s.getChars(start, end, buffer, 0);
				this.offset = end - start;
			} else {
				this.offset = this.grown;
			}
		}
		return this;
	}

	public SqlStringBuffer append(char[] b, int offset, int length) {
		int i = this.grown - this.offset;
		if (i > length) {
			char[] buffer = this.ensure(this.validCount - 1);
			System.arraycopy(b, offset, buffer, this.offset, length);
			this.offset += length;
		} else {
			if (i > 0) {
				char[] buffer = this.ensure(this.validCount - 1);
				System.arraycopy(b, offset, buffer, this.offset, i);
			}
			while (length - i >= this.grown) {
				char[] buffer = this.ensure(this.validCount++);
				System.arraycopy(b, offset + i, buffer, 0, this.grown);
				i += this.grown;
			}
			if (length > i) {
				char[] buffer = this.ensure(this.validCount++);
				System.arraycopy(b, offset + i, buffer, 0, length - i);
				this.offset = length - i;
			} else {
				this.offset = this.grown;
			}
		}
		return this;
	}

	public void writeTo(char[] buffer, int offset) {
		for (int i = 0, c = this.validCount - 1; i < c; i++) {
			System.arraycopy(this.list.get(i), 0, buffer, offset, this.grown);
			offset += this.grown;
		}
		if (this.offset > 0) {
			System.arraycopy(this.list.get(this.validCount - 1), 0, buffer,
					offset, this.offset);
		}
	}

	@Override
	public String toString() {
		char[] buffer = new char[this.size()];
		this.writeTo(buffer, 0);
		return String.valueOf(buffer);
	}
}
