package org.eclipse.jt.core.spi.monitor;

/**
 * 性能监控提供器启动函数调用结果
 * 
 * @author Jeff Tang
 * 
 */
public enum PerformanceMonitorStartResult {
	/**
	 * 需要调用带上下文版本的启动函数
	 */
	NEED_CONTEXT,
	/**
	 * 启动完成，数据已经填充，不需要系统保持valueCollector和调用后续方法<br>
	 * 该结果适用于提高非持续性监控指标和不需要保持监控状态的监控指标的监控效率
	 */
	COMPLETE,
	/**
	 * 启动完成，数据已经填充，但要求系统保持需要系统保持valueCollector，并定期调用update方法<br>
	 * 该结果适用于需要保持状态，或启动停止监控比较消耗资源的监控指标。
	 */
	KEEP,
}
