package org.eclipse.jt.core.spi.monitor;

/**
 * 性能监控值容器基础接口，数据产生端使用
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceValueCollector<TAttachment> {
	/**
	 * 获得对应的定义
	 * 
	 * @return
	 */
	public PerformanceIndexDefine getDefine();

	/**
	 * 是否有效，有效则说明外部有监控需求
	 */
	public boolean isActived();

	/**
	 * 获取附件
	 */
	public TAttachment getAttachment();

	/**
	 * 设置附件
	 */
	public void setAttachment(TAttachment attachment);

	/**
	 * 数据的过期时间，毫秒。默认为0，表示数据即刻失效。<br>
	 * 需要时设置相应的值减少更新的次数。
	 */
	public long getValueMaxAge();

	/**
	 * 设置数据的过期时间。
	 */
	public void setValueMaxAge(long valueMaxAge);
}
