package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.spi.log.LogEntry;

/**
 * 日志任务（内部）
 * 
 * @author Jeff Tang
 * 
 */
public abstract class LogTaskInternal extends SimpleTask {
	private final LogManager logManager;

	public LogTaskInternal(Object logManager) {
		if (logManager == null) {
			throw new NullArgumentException("logManager");
		}
		this.logManager = (LogManager) logManager;
	}

	protected LogEntry nextLogEntry() {
		return this.logManager.loggerGetToLog();
	}
}