package org.eclipse.jt.core.impl;

/**
 * 处理步骤
 * 
 * @author Jeff Tang
 * 
 * @param <TTarget>
 *            被处理的目标
 */
public interface StartupStep<TTarget extends StartupEntry> {
	/**
	 * 获得描述
	 */
	public String getDescription();

	/**
	 * 获得优先级
	 */
	public int getPriority();

	/**
	 * 执行某步并返回下一步
	 * 
	 * @return 返回下一步，为null表示结束
	 */
	public StartupStep<TTarget> doStep(ResolveHelper helper, TTarget target)
			throws Throwable;

	/**
	 * 声明器启动优先级
	 */
	final static int DECLARATOR_HIGHEST_PRI = -0x500000;
	/**
	 * 模型脚本引擎启动优先级
	 */
	final static int MODEL_SCRIPT_ENGINE_PRI = -0x400000;
	/**
	 * 服务启动优先级
	 */
	final static int SERVICE_HIGHEST_PRI = -0x300000;

	/**
	 * 日志服务准备，要在服务启动之前启动。
	 */
	final static int LOGMGR_PREPARE_PRI = PublishedService.service_init
			.getPriority() - 0x20;
	/**
	 * 日志服务就绪。
	 */
	final static int LOGMGR_READY_PRI = PublishedService.service_init
			.getPriority() + 0x20;

	/**
	 * 数据库管理器启动优先级
	 */
	final static int DATASOURCE_HIGHEST_PRI = -0x150000;
	/**
	 * Servlet启动优先级
	 */
	final static int SERVLET_HIGHEST_PRI = -0x100000;
}
