package org.eclipse.jt.core.impl;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.jt.core.exception.NullArgumentException;


/**
 * ×Ö·ûÐòÁÐ¶ÁÈ¡Æ÷
 * 
 * @author Jeff Tang
 * 
 */
final class CharSequenceReader extends Reader {

	private final static class StringReader extends Reader {

		private final String str;
		private int pos;

		StringReader(String str) {
			this.str = str;
		}

		@Override
		public final void close() throws IOException {
		}

		@Override
		public final int read(char[] cbuf, int off, int len) throws IOException {
			if (off < 0 || len < 0 || off + len > cbuf.length) {
				throw new IndexOutOfBoundsException();
			}
			final int remain = this.str.length() - this.pos;
			if (remain <= 0) {
				return -1;
			} else if (len == 0) {
				return 0;
			} else if (len > remain) {
				len = remain;
			}
			this.str.getChars(this.pos, this.pos += len, cbuf, off);
			return len;
		}
	}

	private final static class StringBuilderReader extends Reader {

		private final StringBuilder builder;
		private int pos;

		StringBuilderReader(StringBuilder builder) {
			this.builder = builder;
		}

		@Override
		public final void close() throws IOException {
		}

		@Override
		public final int read(char[] cbuf, int off, int len) throws IOException {
			if (off < 0 || len < 0 || off + len > cbuf.length) {
				throw new IndexOutOfBoundsException();
			}
			final int remain = this.builder.length() - this.pos;
			if (remain <= 0) {
				return -1;
			} else if (len == 0) {
				return 0;
			} else if (len > remain) {
				len = remain;
			}
			this.builder.getChars(this.pos, this.pos += len, cbuf, off);
			return len;
		}
	}

	private final CharSequence chars;
	private int pos;

	final static Reader newReader(CharSequence chars) {
		if (chars instanceof String) {
			return new StringReader((String) chars);
		} else if (chars instanceof StringBuilder) {
			return new StringBuilderReader((StringBuilder) chars);
		} else {
			return new CharSequenceReader(chars);
		}
	}

	private CharSequenceReader(CharSequence chars) {
		if (chars == null) {
			throw new NullArgumentException("chars");
		}
		this.chars = chars;
	}

	@Override
	public final void close() throws IOException {
	}

	@Override
	public final int read(char[] cbuf, int off, int len) throws IOException {
		if (off < 0 || len < 0 || off + len > cbuf.length) {
			throw new IndexOutOfBoundsException();
		}
		final int remain = this.chars.length() - this.pos;
		if (remain <= 0) {
			return -1;
		} else if (len == 0) {
			return 0;
		} else if (len > remain) {
			len = remain;
		}
		for (int i = 0; i < len; i++) {
			cbuf[off++] = this.chars.charAt(this.pos++);
		}
		return len;
	}

}
