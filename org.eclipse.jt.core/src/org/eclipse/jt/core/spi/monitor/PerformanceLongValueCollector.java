package org.eclipse.jt.core.spi.monitor;

/**
 * ���������ܼ��ֵ�����ӿڣ����ݲ�����ʹ��
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceLongValueCollector<TAttachment> extends
		PerformanceValueCollector<TAttachment> {
	public void setValue(long value);

	public long getValue();

	public long incValue(long value);

	public boolean compareAndSet(long expect, long update);
}
