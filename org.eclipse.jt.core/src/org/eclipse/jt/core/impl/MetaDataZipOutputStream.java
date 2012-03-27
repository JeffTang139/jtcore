package org.eclipse.jt.core.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jt.core.misc.SXElement;


public final class MetaDataZipOutputStream extends ZipOutputStream {

	private final static byte[] toBytes(long version) {
		if (version == 0) {
			return null;
		}
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (version & 0xff);
			version >>= 8;
		}
		return b;
	}

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
		ZipEntry ze = new ZipEntry(pathName);
		ze.setComment(description);
		ze.setExtra(toBytes(version));
		try {
			super.putNextEntry(ze);
		} catch (IOException e) {
			throw Utils.tryThrowException(e);
		}
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
		try {
			this.newEntry(pathName, version, description);
			Writer writer = new OutputStreamWriter(this, "UTF-8");
			metaData.render(writer, false);
			writer.flush();
			this.closeEntry();
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	public MetaDataZipOutputStream(OutputStream out) {
		super(out);
		try {
			this.newEntry("METADATA-INF/VERSION-1.0", 0, null);
			this.closeEntry();
		} catch (Throwable e) {
			throw Utils.tryThrowException(e);
		}
	}

	private static void printEntry(MetaDataZipInputStream miStream,
			MetaDataEntryImpl e) throws IOException {
		System.out.print("entry-name:");
		System.out.println(e.getName());
		System.out.print("entry-version:");
		System.out.println(e.getVersion());
		System.out.print("entry-description:");
		System.out.println(e.getDescription());
		System.out.print("entry-size:");
		System.out.println(e.size);
		System.out.println("entry-data:");
		if (e.size > 0) {
			miStream.locateEntry(e);
			Reader reader = new InputStreamReader(miStream, "GBK");
			char[] buf = new char[512];
			for (;;) {
				int size = reader.read(buf);
				if (size > 0) {
					System.out.print(new String(buf, 0, size));
				} else {
					break;
				}
			}
		}
		System.out.println();
		System.out.println("-------------------");
		for (int i = 0, c = e.getSubCount(); i < c; i++) {
			printEntry(miStream, e.getSub(i));
		}
	}

	public static void main(String[] args) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		MetaDataZipOutputStream moStream = new MetaDataZipOutputStream(
				outStream);
		try {
			Writer writer = new OutputStreamWriter(moStream, "GBK");
			moStream.newEntry("a/b/c", 123, "afasfds");
			writer.write("asfasdfdsfasdf");
			writer.write("aer2raf");
			writer.write("25843ujgkmnvla");
			writer.flush();
			moStream.newEntry("a/b", 1234, "afasfds");
			writer.write("asfasdfdsfasdf");
			writer.write("aer2raf");
			writer.write("25843ujgkmnvla");
			writer.flush();
			moStream.newEntry("a/c", 12345, "afasfds");
			writer.write("asfasdfdsfasdf");
			writer.write("aer2raf");
			writer.write("25843ujgkmnvla");
			writer.flush();
		} finally {
			moStream.close();
		}
		MetaDataZipInputStream miStream = new MetaDataZipInputStream(outStream
				.toByteArray());
		try {
			printEntry(miStream, miStream.rootEntry);
		} finally {
			miStream.close();
		}
	}
}
