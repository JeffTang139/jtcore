package org.eclipse.jt.core.invoke;

import java.util.List;

import org.eclipse.jt.core.info.Info;


/**
 * 异步处理句柄
 * 
 * @author Jeff Tang
 * 
 */
public interface AsyncHandle {
	/**
	 * 获得异步执行处理的状态
	 */
	public AsyncState getState();

	/**
	 * 处理进度，0表示还未处理，1表示处理完毕，之间的数表示进度，小于零的数表示中途出现错误
	 * 
	 * @return 返回处理进度
	 */
	public float getProgress();

	/**
	 * 获取已经产生的信息<br>
	 * 必须在启动异步调用时指定关心信息AsyncInfo.isCareInfos()<br>
	 * 
	 * 参阅AsyncInfo
	 */
	public int fetchInfos(List<Info> to);

	/**
	 * 如果处理过程中有异常，则返回该异常，否则返回null
	 * 
	 * @return 返回异常或者null
	 */
	public Throwable getException();

	/**
	 * 尝试取消，不一定能够成功地取消，这取决于实现者是否做了相应的支持
	 */
	public void cancel();
}
