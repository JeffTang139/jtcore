package org.eclipse.jt.core.misc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 简单XML对象解析器
 * 
 * @author Jeff Tang
 * 
 */
public final class SXElementBuilder {
	private final static class XMLTextBuffer {
		private char[] chars = new char[1024 * 4];
		private int count;

		private static boolean isXMLWhitespace(char c) {
			return c == ' ' || c == '\n' || c == '\t' || c == '\r';
		}

		final void append(char[] chars, int start, int length, boolean trimL) {
			if (length > 0) {
				if (this.count == 0 && trimL) {
					while (length > 0 && isXMLWhitespace(chars[start])) {
						start++;
						length--;
					}
					if (length == 0) {
						return;
					}
				}
				int newCount = this.count + length;
				if (newCount > this.chars.length) {
					int newCap = this.chars.length * 2;
					if (newCap < newCount) {
						newCap = newCount;
					}
					char[] newChars = new char[newCap];
					System.arraycopy(this.chars, 0, newChars, 0, this.count);
					this.chars = newChars;
				}
				if (length <= 8) {
					for (int i = this.count; i < newCount; i++, start++) {
						this.chars[i] = chars[start];
					}
				} else {
					System.arraycopy(chars, start, this.chars, this.count,
							length);
				}
				this.count = newCount;
			}
		}

		final void reset() {
			this.count = 0;
		}

		final int trimR() {
			if (this.count > 0) {
				int i = this.count - 1;
				while (i >= 0 && isXMLWhitespace(this.chars[i])) {
					i--;
				}
				return this.count = i + 1;
			} else {
				return 0;
			}
		}

		@Override
		public String toString() {
			return this.count > 0 ? new String(this.chars, 0, this.count) : "";
		}
	}

	private static class Handler extends DefaultHandler implements
			LexicalHandler {
		SXElement current;
		SXElement doc;
		final XMLTextBuffer buffer = new XMLTextBuffer();
		private byte cdataCount;
		private byte textCount;
		private boolean inDTD;
		private boolean inCDATA;

		private final void flushText() {
			if (this.buffer.trimR() > 0) {
				if (this.textCount == 0) {
					this.current.setText(this.buffer.toString());
				}
				this.textCount++;
				this.buffer.reset();
			}
		}

		private final void flushCData() {
			if (this.buffer.count > 0) {
				if (this.cdataCount == 0) {
					this.current.setCDATA(this.buffer.toString());
				}
				this.buffer.reset();
			}
			this.cdataCount++;
		}

		Handler(XMLReader reader) throws SAXException {
			reader.setContentHandler(this);
			reader.setProperty("http://xml.org/sax/properties/lexical-handler",
					this);
		}

		@Override
		public void startDocument() throws SAXException {
			this.current = this.doc = SXElement.newDoc();
		}

		@Override
		public void startElement(String uri, String localName, String name,
				Attributes atts) throws SAXException {
			this.current = this.current.append(name, atts);
			this.cdataCount = 0;
			this.textCount = 0;
		}

		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			this.flushText();
			this.current = this.current.getParent();
			if (this.current == null) {
				throw new SAXException("元素没有开始符<" + name + ">");
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (length > 0) {
				if (this.inCDATA) {
					if (this.cdataCount == 0) {
						this.buffer.append(ch, start, length, false);
					}
				} else if (this.textCount == 0) {
					this.buffer.append(ch, start, length, true);
				}
			}
		}

		public void comment(char[] ch, int start, int length)
				throws SAXException {
			if (!this.inDTD) {
				this.flushText();
			}
		}

		public void startCDATA() throws SAXException {
			this.flushText();
			this.inCDATA = true;
		}

		public void endCDATA() throws SAXException {
			this.flushCData();
			this.inCDATA = false;
		}

		public void startDTD(String name, String publicId, String systemId)
				throws SAXException {
			this.flushText();
			this.inDTD = true;
		}

		public void endDTD() throws SAXException {
			this.inDTD = false;
		}

		public void startEntity(String name) throws SAXException {
		}

		public void endEntity(String name) throws SAXException {
		}
	}

	private final XMLReader reader;
	private final Handler handler;
	private InputSource source;

	public SXElementBuilder() throws SAXException {
		this.reader = XMLReaderFactory.createXMLReader();
		this.handler = new Handler(this.reader);
	}

	public final SXElement build(InputSource in) throws IOException,
			SAXException {
		try {
			this.reader.parse(in);
			return this.handler.doc;
		} finally {
			this.handler.current = null;
			this.handler.doc = null;
		}
	}

	public final SXElement build(File in) throws IOException, SAXException {
		FileInputStream ins = new FileInputStream(in);
		try {
			return this.build(ins);
		} finally {
			ins.close();
		}
	}

	public final SXElement build(URL url) throws IOException, SAXException {
		try {
			this.reader.parse(url.toExternalForm());
			return this.handler.doc;
		} finally {
			this.handler.current = null;
			this.handler.doc = null;
		}
	}

	public final SXElement build(InputStream in) throws IOException,
			SAXException {
		if (this.source == null) {
			this.source = new InputSource(in);
			this.source.setEncoding("UTF-8");
		} else {
			this.source.setByteStream(in);
		}
		try {
			return this.build(this.source);
		} finally {
			this.source.setByteStream(null);
		}
	}

	public final SXElement build(Reader in) throws IOException, SAXException {
		if (this.source == null) {
			this.source = new InputSource(in);
			this.source.setEncoding("UTF-8");
		} else {
			this.source.setCharacterStream(in);
		}
		try {
			return this.build(this.source);
		} finally {
			this.source.setCharacterStream(null);
		}
	}

	public final SXElement build(String xml) throws IOException, SAXException {
		if (xml == null) {
			throw new NullPointerException();
		}
		return this.build(new StringReader(xml));
	}

	public final SXElement build(byte[] utf8Bytes) throws IOException,
			SAXException {
		if (utf8Bytes == null) {
			throw new NullPointerException();
		}
		return this.build(new ByteArrayInputStream(utf8Bytes));
	}
}
