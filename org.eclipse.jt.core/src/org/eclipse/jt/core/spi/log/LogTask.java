package org.eclipse.jt.core.spi.log;

import org.eclipse.jt.core.impl.LogTaskInternal;

/**
 * 日志任务，日志服务需要实现处理该任务的处理器，系统会在适当的时机调用该任务。
 * 
 * @author Jeff Tang
 * 
 */
public final class LogTask extends LogTaskInternal {
	public LogTask(Object logManager) {
		super(logManager);
	}

	/**
	 * 获取下一条日志信息直道返回null表示全部返回，则日志记录应该终止
	 */
	@Override
	public final LogEntry nextLogEntry() {
		return super.nextLogEntry();
	}
}
