package org.eclipse.jt.core.spi.monitor;

/**
 * ���ܼ���ṩ�������������ý��
 * 
 * @author Jeff Tang
 * 
 */
public enum PerformanceMonitorStartResult {
	/**
	 * ��Ҫ���ô������İ汾����������
	 */
	NEED_CONTEXT,
	/**
	 * ������ɣ������Ѿ���䣬����Ҫϵͳ����valueCollector�͵��ú�������<br>
	 * �ý����������߷ǳ����Լ��ָ��Ͳ���Ҫ���ּ��״̬�ļ��ָ��ļ��Ч��
	 */
	COMPLETE,
	/**
	 * ������ɣ������Ѿ���䣬��Ҫ��ϵͳ������Ҫϵͳ����valueCollector�������ڵ���update����<br>
	 * �ý����������Ҫ����״̬��������ֹͣ��رȽ�������Դ�ļ��ָ�ꡣ
	 */
	KEEP,
}
