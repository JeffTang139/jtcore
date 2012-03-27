package org.eclipse.jt.core.spi.metadata;

import java.io.OutputStream;

import org.eclipse.jt.core.invoke.Event;


/**
 * �ռ���ȡ������Ŀ�¼� <br>
 * 1. �����Լ������Ԫ���ݶ������Σ�<br>
 * 1.1. ����
 * <code>getMetaStream().newEntry("yourfolder/youentry1",RECVER,"XYZ")</code>
 * ��������Ŀ�����������ʹ�ø������л���������<br>
 * 1.2. ֱ��ʹ��
 * <code>getMetaStream().newEntry("yourfolder/youentry1",RECVER,"XYZ",xml)</code>
 * ��������Ŀ<br>
 * 2. �����ȡ����
 * 
 * @author Jeff Tang
 * 
 */
public final class ExtractMetaDataEvent extends Event {
	private final MetaDataOutputStream metaStream;
	private final boolean extractAll;

	/**
	 * ���ԭ���ݴ����
	 */
	public final MetaDataOutputStream getMetaStream() {
		return this.metaStream;
	}

	/**
	 * ����Ƿ��ռ�ȫ�����������ǽ����·�����
	 */
	public final boolean extractAll() {
		return this.extractAll;
	}

	/**
	 * ���췽���������ռ��·���ȫ������ ���¼�
	 * 
	 * @param out
	 *            Ŀ����
	 * @param isExtractAll
	 *            �Ƿ��ռ��·�����
	 */
	public ExtractMetaDataEvent(OutputStream out, boolean isExtractAll) {
		this.metaStream = new MetaDataOutputStream(out);
		this.extractAll = isExtractAll;
	}

	/**
	 * ���췽���������ռ��·��������¼�
	 * 
	 * @param out
	 *            Ŀ����
	 */
	public ExtractMetaDataEvent(OutputStream out) {
		this(out, false);
	}

}