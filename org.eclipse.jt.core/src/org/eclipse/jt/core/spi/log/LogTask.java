package org.eclipse.jt.core.spi.log;

import org.eclipse.jt.core.impl.LogTaskInternal;

/**
 * ��־������־������Ҫʵ�ִ��������Ĵ�������ϵͳ�����ʵ���ʱ�����ø�����
 * 
 * @author Jeff Tang
 * 
 */
public final class LogTask extends LogTaskInternal {
	public LogTask(Object logManager) {
		super(logManager);
	}

	/**
	 * ��ȡ��һ����־��Ϣֱ������null��ʾȫ�����أ�����־��¼Ӧ����ֹ
	 */
	@Override
	public final LogEntry nextLogEntry() {
		return super.nextLogEntry();
	}
}
