package org.eclipse.jt.core.spi.monitor;


/**
 * 浮点型性能监控值容器接口，数据产生端使用
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceBoolValueCollector<TAttachment> extends
		PerformanceValueCollector<TAttachment> {

	public void setValue(boolean value);

	public boolean getValue();

	public boolean compareAndSet(boolean expect, boolean update);
}
