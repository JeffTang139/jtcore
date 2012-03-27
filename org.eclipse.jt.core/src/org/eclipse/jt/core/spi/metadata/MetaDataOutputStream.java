package org.eclipse.jt.core.spi.metadata;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.jt.core.impl.MetaDataZipOutputStream;
import org.eclipse.jt.core.misc.SXElement;


/**
 * ԭ���ݴ����
 * 
 * @author Jeff Tang
 * 
 */
public final class MetaDataOutputStream extends FilterOutputStream {
	/**
	 * ����һ������Ŀ�����Ƴ�ͻʱ�׳��쳣�����ҷ��ظ���Ŀ�������<br>
	 * ��������Ч��Ϊ�´ε���<code>newEntry</code>��<code></code>
	 * 
	 * @param pathName
	 *            ��Ŀ���ƣ���"/"�ָ���
	 * @param verion
	 *            �汾�� ��������ԭ���ݶ�Ӧ��RECVER
	 * @param description
	 *            ����
	 */
	public final void newEntry(String pathName, long version, String description) {
		((MetaDataZipOutputStream) super.out).newEntry(pathName, version,
		        description);
	}

	/**
	 * ��SXElement��Ϊ������Ŀд������������ر���Ŀ
	 * 
	 * @param pathName
	 *            ��Ŀ���ƣ���"/"�ָ���
	 * @param version
	 *            ������Ŀ�汾
	 * @param description
	 *            ������Ŀ����
	 * @param metaData
	 *            XML Ԫ����
	 */
	public final void newEntry(String pathName, long version,
	        String description, SXElement metaData) {
		((MetaDataZipOutputStream) super.out).newEntry(pathName, version,
		        description, metaData);
	}

	public MetaDataOutputStream(OutputStream out) {
		super(new MetaDataZipOutputStream(out));
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
