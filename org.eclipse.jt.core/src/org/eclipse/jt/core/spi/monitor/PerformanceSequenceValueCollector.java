package org.eclipse.jt.core.spi.monitor;

/**
 * �������������ܼ��ֵ�����ӿڣ����ݲ�����ʹ��
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceSequenceValueCollector<TAttachment, TObject>
		extends PerformanceValueCollector<TAttachment> {
	/**
	 * ׷�����ݣ�����false��ʾ���
	 */
	public boolean append(TObject object);
}
