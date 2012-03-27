package org.eclipse.jt.core.spi.monitor;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.NamedDeclare;

public interface PerformanceIndexDeclare extends PerformanceIndexDefine,
		NamedDeclare {
	/**
	 * 监控命令
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public interface CommandDeclare extends CommandDefine, NamedDeclare {

	}

	/**
	 * 设置是否是会话级性能指标，否则为全局性能指标
	 */
	public void setIsUnderSession(boolean isUnderSession);

	/**
	 * 获取指标包含的命令
	 */
	public ModifiableNamedElementContainer<? extends CommandDeclare> getCommands();

	/**
	 * 创建新的命令对象
	 * 
	 * @param name
	 *            命令名称
	 */
	public CommandDeclare newCommand(String name);
}
