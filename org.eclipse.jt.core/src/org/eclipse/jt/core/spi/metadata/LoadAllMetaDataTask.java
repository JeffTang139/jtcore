package org.eclipse.jt.core.spi.metadata;

import java.util.logging.Logger;

import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.invoke.Task;


/**
 * 通知系统装载元数据任务，必须用异步任务调用<br>
 * 装载成功后将切换站点，失败则抛出异常<br>
 * 该异步任务的进度安排如下：<br>
 * [0%..5%] : 新站点启动 <br>
 * (5%..100%] : 装载员数据 <br>
 * 
 * @author Jeff Tang
 * 
 */
public class LoadAllMetaDataTask extends Task<LoadAllMetaDataTask.LoadMode> {
	/**
	 * 装载模式
	 */
	public enum LoadMode {
		/**
		 * 合并元数据
		 */
		MERGE,
		/**
		 * 替换元数据
		 */
		REPLACE,
	}

	/**
	 * 元数据
	 */
	public final byte[] metaData;
	/**
	 * 问题记录器
	 */
	public final Logger logger;

	/**
	 * 重启延迟毫秒数，在任何时期设置都有效
	 */
	private volatile long restartDelay = 30000;

	/**
	 * 重启延迟毫秒数，在任何时期设置都有效
	 */
	public final long getRestartDelay() {
		return this.restartDelay;
	}

	/**
	 * 获得完成时间
	 */
	public volatile long finishTime;

	/**
	 * 重启延迟毫秒数，在任何时期设置都有效
	 */
	public final void setRestartDelay(long restartDelay) {
		synchronized (this) {
			this.restartDelay = restartDelay;
			this.notifyAll();
		}
	}

	public LoadAllMetaDataTask(byte[] metaData, Logger logger) {
		if (metaData == null || metaData.length == 0) {
			throw new NullArgumentException("metaData");
		}
		this.metaData = metaData;
		this.logger = logger;
	}
}
