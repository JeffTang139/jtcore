package org.eclipse.jt.core.impl;

import java.lang.reflect.Array;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.ValueConvertException;


/**
 * 格式化器
 * 
 * @author Jeff Tang
 * 
 */
final class Formater {
	private StringBuffer sb;
	private Format format;
	private FieldPosition fp;
	private ParsePosition pp;

	public final Format getFormat() {
		return this.format;
	}

	public final void setFormat(Format format) {
		if (format == null) {
			throw new NullArgumentException("format");
		}
		this.format = format;
	}

	public final String format(Object obj) {
		if (this.sb == null) {
			this.sb = new StringBuffer();
		} else {
			this.sb.setLength(0);
		}
		this.format(obj, this.sb);
		String text = this.sb.toString();
		return text;
	}

	public final void format(Object obj, StringBuffer toAppendTo) {
		if (this.fp == null) {
			this.fp = new FieldPosition(0);
		}
		this.format.format(obj, toAppendTo, this.fp);
	}

	public final Object perse(String text) {
		if (this.pp == null) {
			this.pp = new ParsePosition(0);
		} else {
			this.pp.setIndex(0);
			this.pp.setErrorIndex(-1);
		}
		Object p = this.format.parseObject(text, this.pp);
		if (this.pp.getErrorIndex() >= 0) {
			throw new ValueConvertException("格式错误:" + text);
		}
		if (p.getClass().isArray()) {
			return Array.get(p, 0);
		}
		return p;
	}

	public Formater(Format format) {
		this.setFormat(format);
	}
}
