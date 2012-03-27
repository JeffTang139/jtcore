package org.eclipse.jt.core.impl;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.ZipInputStream;

import org.eclipse.jt.core.misc.SXElement;
import org.eclipse.jt.core.misc.SXElementBuilder;
import org.eclipse.jt.core.spi.metadata.MetaDataEntry;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public final class MetaDataZipInputStream extends ZipInputStream {

	/**
	 * 元数据内存流
	 * 
	 * @author Jeff Tang
	 * 
	 */
	private final static class MetaDataByteArrayInputStream extends
			PushbackInputStream {
		private final String getUTF8String(int off, int len) {
			byte[] b = this.buf;
			// First, count the number of characters in the sequence
			int count = 0;
			int max = off + len;
			int i = off;
			while (i < max) {
				int c = b[i++] & 0xff;
				switch (c >> 4) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					// 0xxxxxxx
					count++;
					break;
				case 12:
				case 13:
					// 110xxxxx 10xxxxxx
					if ((b[i++] & 0xc0) != 0x80) {
						throw new IllegalArgumentException();
					}
					count++;
					break;
				case 14:
					// 1110xxxx 10xxxxxx 10xxxxxx
					if (((b[i++] & 0xc0) != 0x80) || ((b[i++] & 0xc0) != 0x80)) {
						throw new IllegalArgumentException();
					}
					count++;
					break;
				default:
					// 10xxxxxx, 1111xxxx
					throw new IllegalArgumentException();
				}
			}
			if (i != max) {
				throw new IllegalArgumentException();
			}
			// Now decode the characters...
			char[] cs = new char[count];
			i = 0;
			while (off < max) {
				int c = b[off++] & 0xff;
				switch (c >> 4) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					// 0xxxxxxx
					cs[i++] = (char) c;
					break;
				case 12:
				case 13:
					// 110xxxxx 10xxxxxx
					cs[i++] = (char) (((c & 0x1f) << 6) | (b[off++] & 0x3f));
					break;
				case 14:
					// 1110xxxx 10xxxxxx 10xxxxxx
					int t = (b[off++] & 0x3f) << 6;
					cs[i++] = (char) (((c & 0x0f) << 12) | t | (b[off++] & 0x3f));
					break;
				default:
					// 10xxxxxx, 1111xxxx
					throw new IllegalArgumentException();
				}
			}
			return new String(cs, 0, count);
		}

		private final int read16(int pos) {
			return (this.buf[pos] & 0xff) | ((this.buf[pos + 1] & 0xff) << 8);
		}

		private final int read32(int pos) {
			return (this.buf[pos] & 0xff) | ((this.buf[pos + 1] & 0xff) << 8)
					| ((this.buf[pos + 2] & 0xff) << 16)
					| ((this.buf[pos + 3] & 0xff) << 24);
		}

		// 定位第一个CEN
		private int getFirstCENOffset() {
			final byte s1 = (byte) ((ENDSIG >>> 24) & 0xff);
			final byte s2 = (byte) ((ENDSIG >>> 16) & 0xff);
			final byte s3 = (byte) ((ENDSIG >>> 8) & 0xff);
			final byte s4 = (byte) (ENDSIG & 0xff);
			for (int pos = this.count - ENDHDR + 3, end = Math.max(3,
					pos - 0xFFFF); pos >= end; pos--) {
				if (this.buf[pos] == s1 && this.buf[--pos] == s2
						&& this.buf[--pos] == s3 && this.buf[--pos] == s4) {
					final int offset = this.read32(pos + ENDOFF);
					if (0 < offset && offset < pos
							&& this.read32(offset) == CENSIG) {
						return offset;
					}
				}
			}
			return -1;
		}

		// 读取一个CEN
		private int readCEN(MetaDataEntryImpl root, int pos) {
			if (this.read32(pos) != CENSIG) {
				return -1;
			}
			final int namel = this.read16(pos + CENNAM);
			final int extral = this.read16(pos + CENEXT);
			final int commentl = this.read16(pos + CENCOM);
			final int offset = this.read32(pos + CENOFF);
			final int size = this.read32(pos + CENLEN);
			pos += CENHDR;
			final String name = this.getUTF8String(pos, namel);
			if (name.length() == 0) {
				return -1;
			}
			pos += namel;
			final long version;
			if (extral == 8) {
				version = (this.read32(pos) & 0xffffffffl)
						| (this.read32(pos + 4) << 32);
			} else {
				version = 0;
			}
			pos += extral;
			final String comment = this.getUTF8String(pos, commentl);
			pos += commentl;
			final MetaDataEntryImpl entry = root.ensureEntry(name);
			if (entry.offset < 0) {
				entry.description = comment;
				entry.version = version;
				entry.offset = offset;
				entry.size = size;
			}
			return pos;
		}

		// 提取参数条目
		final void extractRootEntry(MetaDataEntryImpl root) {
			int pos = this.getFirstCENOffset();
			while (pos > 0) {
				pos = this.readCEN(root, pos);
			}
		}

		private int count;

		protected MetaDataByteArrayInputStream(byte[] buf) {
			super(null, 1);
			super.buf = buf;
			this.count = buf.length;
		}

		@Override
		public int read() throws IOException {
			return (this.pos < this.count) ? (this.buf[this.pos++] & 0xff) : -1;
		}

		@Override
		public int read(byte b[]) throws IOException {
			return this.read(b, 0, b.length);
		}

		@Override
		public int read(byte b[], int off, int len) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if ((off < 0) || (off > b.length) || (len < 0)
					|| ((off + len) > b.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			}
			if (this.pos >= this.count) {
				return -1;
			}
			if (this.pos + len > this.count) {
				len = this.count - this.pos;
			}
			if (len <= 0) {
				return 0;
			}
			System.arraycopy(this.buf, this.pos, b, off, len);
			this.pos += len;
			return len;
		}

		@Override
		public long skip(long n) throws IOException {
			if (this.pos + n > this.count) {
				n = this.count - this.pos;
			}
			if (n < 0) {
				return 0;
			}
			this.pos += n;
			return n;
		}

		@Override
		public void unread(byte[] b, int off, int len) throws IOException {
			if (len > this.pos) {
				throw new IOException("Push back buffer is full");
			}
			this.pos -= len;
		}

		@Override
		public void unread(int b) throws IOException {
			if (this.pos == 0) {
				throw new IOException("Push back buffer is full");
			}
			this.pos--;
		}

		@Override
		public int available() {
			return this.count - this.pos;
		}

		@Override
		public void close() throws IOException {
		}

		final void seek(int pos) {
			if (pos < 0 || this.count <= pos) {
				throw new IndexOutOfBoundsException();
			}
			super.pos = pos;
		}
	}

	public final void locateEntry(MetaDataEntry entry) {
		MetaDataEntryImpl e = (MetaDataEntryImpl) entry;
		try {
			super.closeEntry();
			((MetaDataByteArrayInputStream) super.in).seek(e.offset);
			this.getNextEntry();
		} catch (IOException e1) {
			throw Utils.tryThrowException(e1);
		}
	}

	private SXElementBuilder xmlBuilder;
	private InputSource xmlSource;

	private boolean ignoreClose;

	@Override
	public final void close() throws IOException {
		if (!this.ignoreClose) {
			super.close();
		}
	}

	public final SXElement getEntryAsXML(MetaDataEntry entry)
			throws SAXException {
		this.ignoreClose = true;
		try {
			this.locateEntry(entry);
			if (this.xmlBuilder == null) {
				this.xmlBuilder = new SXElementBuilder();
				this.xmlSource = new InputSource(this);
				this.xmlSource.setEncoding("UTF-8");
			}
			return this.xmlBuilder.build(this.xmlSource);
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		} finally {
			this.ignoreClose = false;
		}
	}

	public final MetaDataEntryImpl rootEntry;

	private final static InputStream dummy = new FilterInputStream(null) {
	};

	public MetaDataZipInputStream(byte[] buf) {
		super(dummy);
		final MetaDataByteArrayInputStream stream = new MetaDataByteArrayInputStream(
				buf);
		this.rootEntry = new MetaDataEntryImpl("meta");
		stream.extractRootEntry(this.rootEntry);
		super.in = stream;
	}
}
