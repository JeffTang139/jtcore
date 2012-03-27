package org.eclipse.jt.core.spi.monitor;


/**
 * ���������ܼ��ֵ�����ӿڣ����ݲ�����ʹ��
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceBoolValueCollector<TAttachment> extends
		PerformanceValueCollector<TAttachment> {

	public void setValue(boolean value);

	public boolean getValue();

	public boolean compareAndSet(boolean expect, boolean update);
}
