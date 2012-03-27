package org.eclipse.jt.core.spi.metadata;

import java.util.logging.Logger;

import org.eclipse.jt.core.invoke.Event;


/**
 * װ�ز�����Ϣ���¼�<br>
 * 1. ������¼�ʱ����ͨ��<code>mergeOrReplace()<code>�ж�װ��ģʽ���Ǻϲ����Ǹ��ǡ�<br>
 * 2. ����<code>getMetaStream().getRootEntry()</code>���������·����ҵ��Լ���Ҫ��entry<br>
 * 3. ����<code>getMetaStream().locateEntry(entry)</code>��λĳ����Ŀ����
 * <code>getMetaStream().getEntryAsXML(entry)<code>ֱ�ӻ��XML<br>
 * 4. ����Ӧ��װ�ش������ֹ������׳��쳣<br>
 * 5. ����Ǹ��ǲ�������ܻ���Ҫɾ��������û�ж�ϵͳ���е�Ԫ���ݶ���<br>
 * 6. ����¼������˳�
 * 
 * @author Jeff Tang
 * 
 */
public final class LoadMetaDataEvent extends Event {
	private final boolean mergeOrReplace;

	private final MetaDataInputStream metaStream;
	private final Logger logger;

	/**
	 * ָʾ��ȡ�ϲ�װ�ػ��Ǹ���װ��
	 * 
	 * @return true��ʾ�ϲ�װ�أ�false��ʾ����װ��
	 */
	public final boolean mergeOrReplace() {
		return this.mergeOrReplace;
	}

	/**
	 * ��ȡ��Ϣ��¼�������Լ�¼��������еĴ���;���ȵȡ�
	 */
	public final Logger getLogger() {
		return this.logger;
	}

	/**
	 * ���Ԫ������
	 */
	public final MetaDataInputStream getMetaStream() {
		return this.metaStream;
	}

	public LoadMetaDataEvent(boolean mergeOrReplace, byte[] buf, Logger logger) {
		this.mergeOrReplace = mergeOrReplace;
		this.metaStream = new MetaDataInputStream(buf);
		this.logger = logger;
	}
}