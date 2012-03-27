package org.eclipse.jt.core.spi.monitor;

/**
 * ���������ܼ��ֵ�����ӿڣ����ݲ�����ʹ��
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceDoubleValueCollector<TAttachment> extends
		PerformanceValueCollector<TAttachment> {
	/**
	 * ��������
	 */
	public void setValue(double object);

	public double getValue();

	public double incValue(double value);

	public boolean compareAndSet(double expect, double update);
}
