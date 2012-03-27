package org.eclipse.jt.core.misc;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.jt.core.ObjectQuerier;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.impl.Utils;
import org.eclipse.jt.core.type.Convert;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.Type;
import org.eclipse.jt.core.type.TypeFactory;
import org.xml.sax.Attributes;


/**
 * ��XMLԪ��
 * 
 * @author Jeff Tang
 * 
 */
public final class SXElement {
	/**
	 * ת�����ı�д��Ŀ��
	 * 
	 * @param into
	 *            Ŀ��
	 * @param indent
	 *            �Ƿ�ʹ������������
	 */
	public final void render(Appendable into, boolean indent)
			throws IOException {
		if (into == null) {
			throw new NullArgumentException("into");
		}
		this.internalRender(into, new RenderHelper(indent));
	}

	/**
	 * ת�����ı�
	 * 
	 * @return
	 */
	@Override
	public final String toString() {
		return this.internalRender(true).toString();
	}

	/**
	 * ת�����ı�
	 * 
	 * @param indent
	 *            �Ƿ�ʹ������������
	 * @return
	 */
	public final String toString(boolean indent) {
		return this.internalRender(indent).toString();
	}

	/**
	 * ת����byte���飨ʹ��UTF8���룩
	 * 
	 * @param indent
	 *            �Ƿ�ʹ������������
	 */
	public final byte[] toUTF8(boolean indent) {
		return Convert.charsToUTF8(this.internalRender(indent));
	}

	/**
	 * ת����byte���飨ʹ��UTF8���룩
	 */
	public final byte[] toUTF8() {
		return Convert.charsToUTF8(this.internalRender(true));
	}

	/**
	 * �½�һ���ĵ�Ԫ��
	 */
	public static SXElement newDoc() {
		return new SXElement(null, "xml", null);
	}

	/**
	 * �½�һ�������ֵ�Ԫ��
	 * 
	 * @param name
	 *            Ԫ������
	 */
	public static SXElement newElement(String name) {
		return new SXElement(null, name, null);
	}

	/**
	 * Ԫ�ص�����
	 */
	public final String name;

	/**
	 * ��ø�Ԫ�ض���
	 */
	public final SXElement getParent() {
		return this.parent;
	}

	/**
	 * ��ø�Ԫ���µ�CDATA���ݣ�Ŀǰ��֧��һ����
	 */
	public final String getCDATA() {
		return this.cdata;
	}

	/**
	 * ɾ����Ԫ�أ�ǰԪ��
	 */
	public final SXElement removeNextSibling() {
		SXElement parent = this.parent;
		if (parent == null) {
			SXElement next = this.nextSibling;
			if (next != null) {
				this.nextSibling = next.nextSibling;
			}
			return next;
		}
		if (parent.lastChild != this) {// �������һ��
			SXElement next = this.nextSibling;
			this.nextSibling = next.nextSibling;
			if (parent.lastChild == next) {
				parent.lastChild = this;
			}
			next.parent = null;
			next.nextSibling = null;
			return next;
		}
		return null;
	}

	/**
	 * ɾ����Ԫ�أ�ǰԪ��
	 */
	public final SXElement removeNextSibling(String name) {
		SXElement parent = this.parent;
		SXElement lastN = this;
		SXElement next = this.nextSibling;
		SXElement end;
		if (parent != null) {
			SXElement last = parent.lastChild;
			if (last == this) {
				return null;
			} else {
				end = last.nextSibling;
			}
		} else {// ��Ԫ��ʹ��
			if (next == null) {
				return null;
			}
			end = null;
		}
		if (name == null || name.length() == 0) {
			lastN.nextSibling = next.nextSibling;
			if (parent != null && parent.lastChild == next) {
				parent.lastChild = lastN;
			}
			next.parent = null;
			next.nextSibling = null;
			return next;
		}
		do {
			if (next.name.equals(name)) {
				lastN.nextSibling = next.nextSibling;
				if (parent != null && parent.lastChild == next) {
					parent.lastChild = lastN;
				}
				next.parent = null;
				next.nextSibling = null;
				return next;
			}
			lastN = next;
			next = next.nextSibling;
		} while (next != end);
		return null;
	}

	/**
	 * ���ñ�Ԫ���µ�CDATAĿǰ��֧��һ��
	 */
	public final void setCDATA(String cdata) {
		this.cdata = cdata != null && cdata.length() == 0 ? null : cdata;
	}

	/**
	 * ��ȡԪ�ص�ֵ�ı�
	 */
	public final String getText() {
		return this.text;
	}

	/**
	 * ���ø�Ԫ�ص�ֵ�ı�
	 * 
	 * @param text
	 */
	public final void setText(String text) {
		this.text = text == null || text.length() == 0 ? "" : text;
	}

	/**
	 * ��ȡ���Եĸ���
	 * 
	 * @return
	 */
	public final int getAttrCount() {
		return this.attrCount;
	}

	/**
	 * ��ȡĳ���ֵ�����ֵ
	 * 
	 * @param name
	 *            ������
	 */
	public final String getAttribute(String name) {
		return this.getAttribute(name, "");
	}

	/**
	 * ��������ֵ��ֵΪ��ʱʹ�ÿ��ַ���
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public final void setAttrWithEmptyStr(String name, String value) {
		this.internalSetAttribute(name, value, true);
	}

	/**
	 * ��������ֵ��ֵΪ��ʱɾ��������
	 * 
	 * @param name
	 *            ��������
	 * @param value
	 *            ����ֵ
	 */
	public final void setAttribute(String name, String value) {
		this.internalSetAttribute(name, value, false);
	}

	/**
	 * ��ȡĳλ�õ���������
	 * 
	 * @param index
	 *            λ��
	 */
	public final String getAttrName(int index) {
		if (index < 0 || this.attrCount <= index) {
			throw new IndexOutOfBoundsException();
		}
		return this.attrs[index << 1];
	}

	/**
	 * ��ȡĳλ�õ�����ֵ
	 * 
	 * @param index
	 *            λ��
	 */
	public final String getAttribute(int index) {
		if (index < 0 || this.attrCount <= index) {
			throw new IndexOutOfBoundsException();
		}
		return this.attrs[(index << 1) + 1];
	}

	/**
	 * ��ȡĳ�������Ե�λ��
	 * 
	 * @param name
	 *            ������
	 */
	public final int attrIndexOf(String name) {
		if (name == null || name.length() == 0) {
			throw new NullPointerException();
		}
		for (int i = 0, c = this.attrCount * 2; i < c; i += 2) {
			String an = this.attrs[i];
			if (an.equals(name)) {
				return i / 2;
			}
		}
		return -1;
	}

	/**
	 * ��ȡĳ���Ƶ�����ֵ�������ڸ������򷵻�Ĭ��ֵ
	 * 
	 * @param name
	 *            ��������
	 * @param defaultAttribute
	 *            Ĭ�ϵ�����ֵ
	 */
	public final String getAttribute(String name, String defaultAttribute) {
		if (name == null || name.length() == 0) {
			throw new NullArgumentException("name");
		}
		for (int i = 0, c = this.attrCount * 2; i < c; i += 2) {
			String an = this.attrs[i];
			if (an.equals(name)) {
				return this.attrs[i + 1];
			}
		}
		return defaultAttribute;
	}

	/**
	 * ��ò�����������ֵ
	 * 
	 * @param name
	 *            ������
	 */
	public final boolean getBoolean(String name) {
		return Convert.toBoolean(this.getAttribute(name, null));
	}

	/**
	 * ��ò�����������ֵ�������ڸ������򷵻�Ĭ��ֵ
	 * 
	 * @param name
	 *            ������
	 * @param defaultAttribute
	 *            Ĭ��ֵ
	 */
	public final boolean getBoolean(String name, boolean defaultValue) {
		String attr = this.getAttribute(name, null);
		return attr != null && attr.length() > 0 ? Convert.toBoolean(attr)
				: defaultValue;
	}

	/**
	 * ���ò�����������ֵ
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public final void setBoolean(String name, boolean value) {
		if (value) {
			this.setAttribute(name, "true");
		} else {
			this.setAttribute(name, "false");
		}
	}

	public final void setTrue(String name, boolean value) {
		if (value) {
			this.setAttribute(name, "true");
		}
	}

	/**
	 * ���ò�����������ֵ��������valueΪ��ʱ������
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public final void maskTrue(String name, boolean value) {
		if (value) {
			this.setAttribute(name, "true");
		}
	}

	/**
	 * ���byte���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 */
	public final byte getByte(String name) {
		return Convert.toByte(this.getAttribute(name, null));
	}

	/**
	 * ���byte��������ֵ�������ڸ������򷵻�Ĭ��ֵ
	 * 
	 * @param name
	 *            ������
	 * @param defaultValue
	 *            Ĭ��ֵ
	 */
	public final byte getByte(String name, byte defaultValue) {
		String attrV = this.getAttribute(name, null);
		try {
			return attrV != null && attrV.length() > 0 ? Byte.parseByte(attrV)
					: defaultValue;
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	/**
	 * ����byte���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public final void setByte(String name, byte value) {
		this.setAttribute(name, Convert.toString(value));
	}

	/**
	 * ���char���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 */
	public final char getChar(String name) {
		return Convert.toChar(this.getAttribute(name, null));
	}

	/**
	 * ���char��������ֵ�������ڸ������򷵻�Ĭ��ֵ
	 * 
	 * @param name
	 *            ������
	 * @param defaultValue
	 *            Ĭ��ֵ
	 */
	public final char getChar(String name, char defaultValue) {
		String attr = this.getAttribute(name, null);
		return attr != null && attr.length() > 0 ? attr.charAt(0)
				: defaultValue;
	}

	/**
	 * ����char���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public final void setChar(String name, char value) {
		this.setAttribute(name, Convert.toString(value));
	}

	/**
	 * ����byte[]���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public final void setBytes(String name, byte[] value) {
		this.setAttribute(name, Convert.toString(value));
	}

	/**
	 * ���byte[]���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 */
	public final byte[] getBytes(String name) {
		return Convert.toBytes(this.getAttribute(name, null));
	}

	/**
	 * ���short���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 */
	public final short getShort(String name) {
		return Convert.toShort(this.getAttribute(name, null));
	}

	/**
	 * ����short���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public final void setShort(String name, short value) {
		this.setAttribute(name, Convert.toString(value));
	}

	/**
	 * ���int���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 */
	public final int getInt(String name) {
		return Convert.toInt(this.getAttribute(name, null));
	}

	/**
	 * ���int��������ֵ�������ڸ������򷵻�Ĭ��ֵ
	 * 
	 * @param name
	 *            ������
	 * @param defaultValue
	 *            Ĭ��ֵ
	 */
	public final int getInt(String name, int defaultValue) {
		String attr = this.getAttribute(name, null);
		try {
			return attr != null && attr.length() > 0 ? Integer.parseInt(attr)
					: defaultValue;
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	/**
	 * ����int���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public final void setInt(String name, int value) {
		this.setAttribute(name, Convert.toString(value));
	}

	/**
	 * ���long���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 */
	public final long getLong(String name) {
		return Convert.toLong(this.getAttribute(name, null));
	}

	/**
	 * ���long��������ֵ�������ڸ������򷵻�Ĭ��ֵ
	 * 
	 * @param name
	 *            ������
	 * @param defaultValue
	 *            Ĭ��ֵ
	 */
	public final long getLong(String name, long defaultValue) {
		final String attr = this.getAttribute(name, null);
		try {
			return attr != null && attr.length() > 0 ? Long.parseLong(attr)
					: defaultValue;
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	/**
	 * ����long���͵�����ֵ
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public final void setLong(String name, long value) {
		this.setAttribute(name, Convert.toString(value));
	}

	public final float getFloat(String name) {
		return Float.parseFloat(this.getAttribute(name, null));
	}

	public final void setFloat(String name, float value) {
		this.setAttribute(name, Convert.toString(value));
	}

	public final double getDouble(String name) {
		return Convert.toDouble(this.getAttribute(name, null));
	}

	public final void setDouble(String name, double value) {
		this.setAttribute(name, Convert.toString(value));
	}

	public final void setDate(String name, long value) {
		this.setAttribute(name, Convert.dateToString(value));
	}

	public final long getDate(String name) {
		return Convert.toDate(this.getAttribute(name, null));
	}

	public final void setString(String name, String value) {
		this.setAttribute(name, value);
	}

	public final String getString(String name) {
		return this.getAttribute(name, null);
	}

	public final void setGUID(String name, GUID value) {
		this.setAttribute(name, Convert.toString(value));
	}

	public final GUID getGUID(String name) {
		return Convert.toGUID(this.getAttribute(name, null));
	}

	public final GUID getGUID(String name, GUID defaultValue) {
		final String attr = this.getAttribute(name, null);
		try {
			return attr != null && attr.length() > 0 ? Convert.toGUID(attr)
					: defaultValue;
		} catch (Throwable e) {
			return defaultValue;
		}
	}

	/**
	 * ��ȡ����ֵ��Ϊ����ֵ
	 * 
	 * @param name
	 *            ������
	 * @param querier
	 *            ����������
	 */
	public final DataType getAsType(String name, ObjectQuerier querier) {
		String attr = this.getAttribute(name, null);
		if (attr == null) {
			throw new NullPointerException("���ͱ�ʶΪ��");
		}
		return TypeFactory.typeOf(attr, querier);
	}

	/**
	 * ��ȡ����ֵ��Ϊ����ֵ���Ҳ����򷵻�Ĭ��ֵ
	 * 
	 * @param name
	 *            ������
	 * @param querier
	 *            ����������
	 * @param defaultValue
	 *            Ĭ������
	 */
	public final DataType getAsType(String name, ObjectQuerier querier,
			DataType defaultValue) {
		String attr = this.getAttribute(name, null);
		if (attr == null || attr.length() == 0) {
			return defaultValue;
		}
		DataType dt = TypeFactory.typeOf(attr, querier);
		return dt != TypeFactory.UNKNOWN ? dt : defaultValue;
	}

	/**
	 * ��������������ֵ
	 * 
	 * @param name
	 *            ������
	 * @param value
	 *            ����ֵ
	 */
	public final void setAsType(String name, Type value) {
		this.setAttribute(name, value.toString());
	}

	public final <TEnum extends Enum<TEnum>> TEnum getEnum(
			Class<TEnum> enumType, String name, TEnum defaultV) {
		String v = this.getAttribute(name, null);
		if (v == null || v.length() == 0) {
			return defaultV;
		}
		try {
			return Enum.valueOf(enumType, v);
		} catch (Throwable e) {
			return defaultV;
		}
	}

	public final <TEnum extends Enum<TEnum>> TEnum getEnum(
			Class<TEnum> enumType, String name) {
		return Enum.valueOf(enumType, this.getAttribute(name, null));
	}

	public final void setEnum(String name, Enum<?> value) {
		if (value != null) {
			this.setAttribute(name, value.name());
		}
	}

	/**
	 * ���ĳ���Ƶ�һ����Ԫ��
	 * 
	 * @param name
	 *            ��Ԫ�����ƣ�null����ַ�����ʾ���Ե�һ����Ԫ��
	 * @return
	 */
	public final SXElement firstChild(String name) {
		SXElement lastChild = this.lastChild;
		if (lastChild == null) {
			return null;
		}
		if (name == null || name.length() == 0) {
			return lastChild.nextSibling;
		}
		SXElement first = lastChild.nextSibling;
		SXElement one = first;
		do {
			if (one.name.equals(name)) {
				return one;
			}
			one = one.nextSibling;
		} while (one != first);
		return null;
	}

	/**
	 * ���ĳ���Ƶ�һ����Ԫ��
	 * 
	 * @param name1
	 *            ��һ�����ƣ�null����ַ�����ʾ���Ե�һ����Ԫ��
	 * @param name2
	 *            �ڶ������ƣ�null����ַ�����ʾ���Ե�һ����Ԫ��
	 * @return
	 */
	public SXElement firstChild(String name1, String name2) {
		SXElement descendant = this.firstChild(name1);
		return descendant != null ? descendant.firstChild(name2) : null;
	}

	/**
	 * ���ĳ���Ƶ�һ����Ԫ��
	 * 
	 * @param name1
	 *            ��һ�����ƣ�null����ַ�����ʾ�ü����Ե�һ����Ԫ��
	 * @param name2
	 *            �ڶ������ƣ�null����ַ�����ʾ�ü����Ե�һ����Ԫ��
	 * @param names
	 *            ʣ�༶�����ƣ�����ĳ��Ϊnull����ַ�����ʾ�ü����Ե�һ����Ԫ��
	 * @return
	 */
	public SXElement firstChild(String name1, String name2, String... names) {
		SXElement descendant = this.firstChild(name1);
		if (descendant != null) {
			descendant = descendant.firstChild(name2);
			if (names != null) {
				for (int i = 0; i < names.length; i++) {
					if (descendant == null) {
						return null;
					}
					descendant = descendant.firstChild(names[i]);
				}
			}
			return descendant;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Iterable<SXElement> getChildren(String name) {
		SXElement first = this.firstChild(name);
		if (first == null) {
			return (Iterable) EmptyIterable.emptyIterable;
		} else {
			return new SXElementIterable(first, name);
		}
	}

	@SuppressWarnings("unchecked")
	public Iterable<SXElement> getChildren(String name1, String name2) {
		SXElement first = this.firstChild(name1, name2);
		if (first == null) {
			return (Iterable) EmptyIterable.emptyIterable;
		} else {
			return new SXElementIterable(first, name2);
		}
	}

	@SuppressWarnings("unchecked")
	public Iterable<SXElement> getChildren(String name1, String name2,
			String... names) {
		SXElement first = this.firstChild(name1, name2, names);
		if (first == null) {
			return (Iterable) EmptyIterable.emptyIterable;
		} else {
			String cname;
			if (names != null && names.length > 0) {
				cname = names[names.length - 1];
			} else {
				cname = name2;
			}
			return new SXElementIterable(first, cname);
		}
	}

	public final SXElement firstChild() {
		SXElement lastChild = this.lastChild;
		return lastChild == null ? null : lastChild.nextSibling;
	}

	@Override
	public final SXElement clone() {
		return new SXElement(null, this);
	}

	/**
	 * �Ƴ��Լ��ĵ�һ����Ԫ��
	 * 
	 * @return ���ر��Ƴ���Ԫ��
	 */
	public final SXElement removeFirstChild() {
		final SXElement lastChild = this.lastChild;
		if (lastChild == null) {
			return null;
		}
		SXElement firstChild = lastChild.nextSibling;
		if (lastChild == firstChild) {
			this.lastChild = null;
		} else {
			lastChild.nextSibling = firstChild.nextSibling;
		}
		firstChild.parent = null;
		firstChild.nextSibling = null;
		return firstChild;
	}

	public final boolean removeChildren() {
		SXElement lastChild = this.lastChild;
		if (lastChild == null) {
			return false;
		}
		this.lastChild = null;
		for (;;) {
			final SXElement next = lastChild.nextSibling;
			if (next == null) {
				return true;
			}
			lastChild.nextSibling = null;// help GC
			lastChild.parent = null;
			lastChild = next;
		}
	}

	public final SXElement nextSibling(String name) {
		SXElement parent = this.parent;
		SXElement next;
		SXElement end;
		if (parent != null) {
			SXElement last;
			last = parent.lastChild;
			if (last == this) {
				return null;
			} else {
				end = last.nextSibling;
			}
			next = this.nextSibling;
		} else {
			next = this.nextSibling;
			if (next == null) {
				return null;
			}
			end = null;
		}
		if (name == null || name.length() == 0) {
			return next;
		}
		do {
			if (next.name.equals(name)) {
				return next;
			}
			next = next.nextSibling;
		} while (next != end);
		return null;
	}

	public final SXElement nextSibling() {
		SXElement parent = this.parent;
		if (parent == null || parent.lastChild != this) {
			return this.nextSibling;
		}
		return null;
	}

	public final SXElement append(String name) {
		return new SXElement(this, name, null);
	}

	/**
	 * ��ĳԪ�������Լ�����Ԫ��
	 * 
	 * @param newChild
	 *            ��Ҫ����ӵ���Ԫ��
	 */
	public final void append(SXElement newChild) {
		if (newChild == null) {
			throw new NullArgumentException("newChild");
		}
		SXElement p = this;
		while (p != null && p != newChild) {
			p = p.parent;
		}
		if (p != null) {
			throw new IllegalArgumentException("��֧�ֽ��Լ����뵽�Լ����¼�Ԫ����");
		}
		SXElement cp = newChild.parent;
		if (cp != null) {// ��ԭ�и�Ԫ����ժ��
			if (cp == this) {
				return;
			}
			SXElement first = cp.lastChild.nextSibling;
			if (first == newChild) {
				cp.removeFirstChild();
			} else {
				SXElement c = first.nextSibling;
				SXElement prior = first;
				while (c != first && c != newChild) {
					prior = c;
					c = c.nextSibling;
				}
				prior.removeNextSibling();
			}
		}
		SXElement last = this.lastChild;
		if (last != null) {
			newChild.nextSibling = last.nextSibling;
			last.nextSibling = newChild;
		} else {
			newChild.nextSibling = newChild;
		}
		this.lastChild = newChild;
		newChild.parent = this;
	}

	/**
	 * ��ĳԪ�ز������Լ�����һ��Ԫ��
	 * 
	 * @param newSibling
	 *            ��Ҫ�������Ԫ��
	 */
	public final void insertAfter(SXElement newSibling) {
		if (newSibling == null) {
			throw new NullArgumentException("newSibling");
		}
		if (newSibling == this) {
			return;
		}
		SXElement p = this.parent;
		while (p != null && p != newSibling) {
			p = p.parent;
		}
		if (p != null) {
			throw new IllegalArgumentException("��֧�ֽ��Լ����뵽�Լ����¼�Ԫ����");
		}
		SXElement cp = newSibling.parent;
		if (cp != null) {// ��ԭ�и�Ԫ����ժ��
			if (cp == this) {
				return;
			}
			SXElement first = cp.lastChild.nextSibling;
			if (first == newSibling) {
				cp.removeFirstChild();
			} else {
				SXElement c = first.nextSibling;
				SXElement prior = first;
				while (c != first && c != newSibling) {
					prior = c;
					c = c.nextSibling;
				}
				prior.removeNextSibling();
			}
		}
		newSibling.nextSibling = this.nextSibling;
		this.nextSibling = newSibling;
		newSibling.parent = this.parent;
	}

	// /////////////////////////////////////////////
	private SXElement nextSibling;
	// ��
	private SXElement lastChild;
	private String[] attrs;
	private String cdata;
	private String text;

	private int attrCount;
	private SXElement parent;

	private SXElement(SXElement parent, SXElement from) {
		this.parent = parent;
		this.name = from.name;
		this.attrCount = from.attrCount;
		this.attrs = from.attrs.clone();
		this.cdata = from.cdata;
		this.text = from.text;
		final SXElement fromLastChild = from.lastChild;
		if (fromLastChild != null) {
			SXElement clonedChild = this.lastChild = new SXElement(this,
					fromLastChild);
			for (SXElement fromChild = fromLastChild.nextSibling; fromChild != fromLastChild; fromChild = fromChild.nextSibling) {
				clonedChild.nextSibling = new SXElement(this, fromChild);
				clonedChild = clonedChild.nextSibling;
			}
			clonedChild.nextSibling = this.lastChild;
		}
	}

	private SXElement(SXElement parent, String name, Attributes attrs) {
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("Ԫ�����Ʋ���Ϊ��");
		}
		int attrC = attrs != null ? attrs.getLength() : 0;
		if (attrC > 0) {
			int j = 0;
			this.attrs = new String[attrC * 2];
			for (int i = 0; i < attrC; i++) {
				this.attrs[j++] = attrs.getQName(i);
				String v = attrs.getValue(i);
				this.attrs[j++] = v == null ? "" : (v.length() == 0 ? "" : v);
			}
			this.attrCount = j / 2;
		}
		this.name = name;
		this.text = "";
		this.parent = parent;
		if (parent != null) {
			SXElement last = parent.lastChild;
			if (last != null) {
				this.nextSibling = last.nextSibling;
				last.nextSibling = this;
			} else {
				this.nextSibling = this;
			}
			parent.lastChild = this;
		}
	}

	private final void internalSetAttribute(String name, String value,
			boolean withEmptyStr) {
		if (name == null || name.length() == 0) {
			throw new NullPointerException();
		}
		if (value != null && value.length() == 0) {
			value = withEmptyStr ? "" : null;
		}
		int c = this.attrCount * 2;
		for (int i = 0; i < c; i += 2) {
			String an = this.attrs[i];
			if (an.equals(name)) {
				if (value != null) {
					this.attrs[i + 1] = value;
				} else {
					int cc = c - i - 2;
					if (cc > 0) {
						System.arraycopy(this.attrs, i + 2, this.attrs, i, cc);
					}
					this.attrCount--;
				}
				return;
			}
		}
		if (value == null) {
			return;
		}
		// new attr
		if (c == 0) {
			this.attrs = new String[8];
			this.attrs[0] = name;
			this.attrs[1] = value;
		} else if (c < this.attrs.length) {
			this.attrs[c] = name;
			this.attrs[c + 1] = value;
		} else {
			String[] newAttrs = new String[c * 2];
			System.arraycopy(this.attrs, 0, newAttrs, 0, c);
			newAttrs[c] = name;
			newAttrs[c + 1] = value;
			this.attrs = newAttrs;
		}
		this.attrCount++;
	}

	private static class SXElementIterator implements Iterator<SXElement> {

		public boolean hasNext() {
			return this.current != null;
		}

		public SXElement next() {
			SXElement curr = this.current;
			if (curr == null) {
				throw new NoSuchElementException();
			}
			this.current = curr.nextSibling(this.match);
			return curr;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private SXElement current;
		private final String match;

		SXElementIterator(SXElement from, String match) {
			this.current = from;
			this.match = match;
		}
	}

	private static class SXElementIterable implements Iterable<SXElement> {
		private final SXElement from;
		private final String match;

		SXElementIterable(SXElement from, String match) {
			this.from = from;
			this.match = match;
		}

		public Iterator<SXElement> iterator() {
			return new SXElementIterator(this.from, this.match);
		}
	}

	private final StringBuilder internalRender(boolean indent) {
		StringBuilder xml = new StringBuilder();
		try {
			this.internalRender(xml, new RenderHelper(indent));
			return xml;
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	final SXElement append(String name, Attributes attrs) {
		return new SXElement(this, name, attrs);
	}

	private static void appendEscapedValue(Appendable into, String str)
			throws IOException {
		int i = 0, start = 0, length = str.length();

		while (i < length) {
			char ch = str.charAt(i);
			String esc;
			switch (ch) {
			case '<':
				esc = "&lt;";
				break;
			case '>':
				esc = "&gt;";
				break;
			case '\"':
				esc = "&quot;";
				break;
			case '&':
				esc = "&amp;";
				break;
			case '\r':
				esc = "&#xD;";
				break;
			case '\t':
				esc = "&#x9;";
				break;
			case '\n':
				esc = "&#xA;";
				break;
			default:
				esc = null;
			}
			if (esc != null) {
				if (start < i) {
					into.append(str, start, i);
				}
				start = i + 1;
				into.append(esc);
			}
			i++;
		}
		if (start == 0) {
			into.append(str);
		} else if (start < length) {
			into.append(str, start, length);
		}
	}

	private static class RenderHelper {
		RenderHelper(boolean indent) {
			this.deep = indent ? 0 : Integer.MIN_VALUE;
		}

		int deep;

		final void newLine(Appendable into) throws IOException {
			if (this.deep >= 0) {
				into.append('\r').append('\n');
				for (int i = 0; i < this.deep; i++) {
					into.append('\t');
				}
			}
		}
	}

	private final void internalRender(Appendable into, RenderHelper helper)
			throws IOException {
		if (this.parent == null) {
			into.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			SXElement last = this.lastChild;
			if (last == null) {
				return;
			}
			SXElement first = last.nextSibling;
			SXElement one = first;
			helper.deep++;
			do {
				helper.newLine(into);
				one.internalRender(into, helper);
				one = one.nextSibling;
			} while (one != first);
			helper.deep--;
		} else {
			into.append('<');
			into.append(this.name);
			int c = this.attrCount << 1;
			int attrL = 0;
			if (helper.deep >= 0) {
				for (int i = 0; i < c; i++) {
					attrL += this.attrs[i++].length() + this.attrs[i].length()
							+ 4;
				}
			}
			if (attrL <= 80) {
				for (int i = 0; i < c; i++) {
					into.append(' ');
					into.append(this.attrs[i++]);
					into.append('=').append('\"');
					appendEscapedValue(into, this.attrs[i]);
					into.append('\"');
				}
			} else {
				helper.deep++;
				for (int i = 0; i < c; i++) {
					helper.newLine(into);
					into.append(this.attrs[i++]);
					into.append('=').append('\"');
					appendEscapedValue(into, this.attrs[i]);
					into.append('\"');
				}
				helper.deep--;
				helper.newLine(into);
			}
			boolean newLine = this.text.length() == 0;
			SXElement last = this.lastChild;
			if (last != null || this.cdata != null || !newLine) {
				into.append('>');
				if (!newLine) {
					appendEscapedValue(into, this.text);
				}
				if (this.cdata != null) {
					if (newLine) {
						helper.deep++;
						helper.newLine(into);
					}
					into.append("<![CDATA[");
					if (this.cdata.length() > 0) {
						into.append(this.cdata);
					}
					into.append("]]>");
					if (newLine) {
						helper.deep--;
					}
				}
				if (last != null) {
					SXElement first = last.nextSibling;
					SXElement one = first;
					helper.deep++;
					do {
						helper.newLine(into);
						one.internalRender(into, helper);
						one = one.nextSibling;
					} while (one != first);
					helper.deep--;
					newLine = true;
				}
				if (newLine) {
					helper.newLine(into);
				}
				into.append('<').append('/').append(this.name).append('>');
			} else {
				into.append('/').append('>');
			}
		}
	}

	public final void appendEntity(SXRenderable entity) {
		entity.render(this.append(entity.getXMLTagName()));
	}

	public final void appendEntity(String parentTagName, SXRenderable entity) {
		entity
				.render(this.append(parentTagName).append(
						entity.getXMLTagName()));
	}

	public void appendEntitys(String parentTagName,
			List<? extends SXRenderable> entities, int start) {
		int size = entities.size();
		if (start < size) {
			SXElement into = this.append(parentTagName);
			for (int i = start; i < size; i++) {
				SXRenderable e = entities.get(i);
				e.render(into.append(e.getXMLTagName()));
			}
		}
	}

	public void appendEntitys(String parentTagName, SXRenderable[] entities,
			int start) {
		int size = entities.length;
		if (start < size) {
			SXElement into = this.append(parentTagName);
			for (int i = start; i < size; i++) {
				SXRenderable e = entities[i];
				e.render(into.append(e.getXMLTagName()));
			}
		}
	}

	public void appendEntitys(List<? extends SXRenderable> entities, int start) {
		int size = entities.size();
		if (start < size) {
			for (int i = start; i < size; i++) {
				SXRenderable e = entities.get(i);
				e.render(this.append(e.getXMLTagName()));
			}
		}
	}

	public void appendEntitys(SXRenderable[] entities, int start) {
		int size = entities.length;
		if (start < size) {
			for (int i = start; i < size; i++) {
				SXRenderable e = entities[i];
				e.render(this.append(e.getXMLTagName()));
			}
		}
	}

	public final void renderInto(String parentTagName,
			List<? extends SXRenderable> entities, int start,
			String entityTagName) {
		int size = entities.size();
		if (start < size) {
			SXElement into = this.append(parentTagName);
			for (int i = start; i < size; i++) {
				SXRenderable e = entities.get(i);
				e.render(into.append(entityTagName));
			}
		}
	}

	public final void renderInto(String parentTagName, SXRenderable[] entities,
			int start, String entityTagName) {
		int size = entities.length;
		if (start < size) {
			SXElement into = this.append(parentTagName);
			for (int i = start; i < size; i++) {
				SXRenderable e = entities[i];
				e.render(into.append(entityTagName));
			}
		}
	}
}
