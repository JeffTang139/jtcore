package org.eclipse.jt.core.spi.monitor;

/**
 * ���ܼ��ֵ���������ӿڣ����ݲ�����ʹ��
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceValueCollector<TAttachment> {
	/**
	 * ��ö�Ӧ�Ķ���
	 * 
	 * @return
	 */
	public PerformanceIndexDefine getDefine();

	/**
	 * �Ƿ���Ч����Ч��˵���ⲿ�м������
	 */
	public boolean isActived();

	/**
	 * ��ȡ����
	 */
	public TAttachment getAttachment();

	/**
	 * ���ø���
	 */
	public void setAttachment(TAttachment attachment);

	/**
	 * ���ݵĹ���ʱ�䣬���롣Ĭ��Ϊ0����ʾ���ݼ���ʧЧ��<br>
	 * ��Ҫʱ������Ӧ��ֵ���ٸ��µĴ�����
	 */
	public long getValueMaxAge();

	/**
	 * �������ݵĹ���ʱ�䡣
	 */
	public void setValueMaxAge(long valueMaxAge);
}
