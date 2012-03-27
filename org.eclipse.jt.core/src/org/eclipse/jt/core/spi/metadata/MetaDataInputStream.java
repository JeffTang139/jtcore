package org.eclipse.jt.core.spi.metadata;

import java.io.FilterInputStream;
import java.io.IOException;

import org.eclipse.jt.core.impl.MetaDataZipInputStream;
import org.eclipse.jt.core.misc.SXElement;
import org.xml.sax.SAXException;


/**
 * ԭ���ݽ�����
 * 
 * @author Jeff Tang
 * 
 */
public final class MetaDataInputStream extends FilterInputStream {
	/**
	 * ��ø���Ŀ
	 */
	public final MetaDataEntry getRootEntry() {
		return ((MetaDataZipInputStream) this.in).rootEntry;
	}

	/**
	 * ��λ��Ŀ����ǰ��ָ������Ŀ������<br>
	 * ��������Чʹ������Ϊ�´ε���<code>locateEntry</code>��<code>getEntryAsXML</code>֮ǰ��
	 * 
	 * @param entry
	 *            ��Ŀ
	 */
	public final void locateEntry(MetaDataEntry entry) {
		((MetaDataZipInputStream) this.in).locateEntry(entry);
	}

	/**
	 * ����Ŀ��װ��XML���󣬻�ȡ��ɺ����ĵ�ǰ��ĿʧЧ
	 * 
	 * @param entry
	 *            ��Ŀ
	 * @exception SAXException
	 *                ��Ŀ����XML���ʽ����
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
