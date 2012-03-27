package org.eclipse.jt.core.spi.monitor;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.Return;
import org.eclipse.jt.core.invoke.Task;
import org.eclipse.jt.core.type.GUID;


/**
 * 执行命令任务
 * 
 * @author Jeff Tang
 * 
 */
public class PerformanceCommandTask extends Task<PerformanceCommandTask.Method> {
	/**
	 * 任务方法
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public enum Method {
		/**
		 * 测试其可用性
		 */
		TEST,
		/**
		 * 执行命令
		 */
		EXECUTE,
	}

	/**
	 * 返回是否执行了，或允许执行
	 */
	@Return
	public boolean executed;
	/**
	 * 命令名称
	 */
	public final String commandName;
	/**
	 * 指标ID
	 */
	public final GUID indexID;
	/**
	 * 会话ID
	 */
	public final long sessionID;

	public PerformanceCommandTask(GUID indexID, String commandName,
			long sessionID) {
		if (indexID == null) {
			throw new NullArgumentException("indexID");
		}
		if (commandName == null || commandName.length() == 0) {
			throw new NullArgumentException("commandName");
		}
		this.indexID = indexID;
		this.commandName = commandName;
		this.sessionID = sessionID;

	}

	public PerformanceCommandTask(GUID indexID, String commandName) {
		this(indexID, commandName, 0);
	}
}
