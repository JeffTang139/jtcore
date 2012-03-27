package org.eclipse.jt.core.spi.monitor;

/**
 * 浮点型性能监控值容器接口，数据产生端使用
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceDoubleValueCollector<TAttachment> extends
		PerformanceValueCollector<TAttachment> {
	/**
	 * 设置数据
	 */
	public void setValue(double object);

	public double getValue();

	public double incValue(double value);

	public boolean compareAndSet(double expect, double update);
}
