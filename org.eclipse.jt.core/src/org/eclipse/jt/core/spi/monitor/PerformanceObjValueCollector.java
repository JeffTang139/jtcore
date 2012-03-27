package org.eclipse.jt.core.spi.monitor;

/**
 * ���������ܼ��ֵ�����ӿڣ����ݲ�����ʹ��
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceObjValueCollector<TAttachment, TObject> extends
		PerformanceValueCollector<TAttachment> {
	/**
	 * ��������
	 */
	public void setValue(TObject object);

	public TObject getValue();

	public boolean compareAndSet(TObject expect, TObject update);
}
