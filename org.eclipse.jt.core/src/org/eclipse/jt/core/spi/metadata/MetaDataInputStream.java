package org.eclipse.jt.core.spi.metadata;

import java.io.FilterInputStream;
import java.io.IOException;

import org.eclipse.jt.core.impl.MetaDataZipInputStream;
import org.eclipse.jt.core.misc.SXElement;
import org.xml.sax.SAXException;


/**
 * 原数据解析流
 * 
 * @author Jeff Tang
 * 
 */
public final class MetaDataInputStream extends FilterInputStream {
	/**
	 * 获得根条目
	 */
	public final MetaDataEntry getRootEntry() {
		return ((MetaDataZipInputStream) this.in).rootEntry;
	}

	/**
	 * 定位条目，当前流指向新条目的数据<br>
	 * 该流的有效使用期限为下次调用<code>locateEntry</code>或<code>getEntryAsXML</code>之前。
	 * 
	 * @param entry
	 *            条目
	 */
	public final void locateEntry(MetaDataEntry entry) {
		((MetaDataZipInputStream) this.in).locateEntry(entry);
	}

	/**
	 * 从条目中装载XML对象，获取完成后流的当前条目失效
	 * 
	 * @param entry
	 *            条目
	 * @exception SAXException
	 *                条目不是XML或格式有误
	 */
	public final SXElement getEntryAsXML(MetaDataEntry entry)
	        throws SAXException {
		return ((MetaDataZipInputStream) this.in).getEntryAsXML(entry);
	}

	public MetaDataInputStream(byte[] buf) {
		super(new MetaDataZipInputStream(buf));
	}

	private int usecount;

	public final void use() {
		if (this.usecount != Integer.MIN_VALUE) {
			this.usecount++;
		}
	}

	public final void unuse() throws IOException {
		if (this.usecount > 0 && --this.usecount == 0) {
			this.usecount = Integer.MIN_VALUE;
			super.close();
		}
	}

	@Override
	public final void close() throws IOException {
		if (this.usecount == 0) {
			super.close();
		}
	}
}
