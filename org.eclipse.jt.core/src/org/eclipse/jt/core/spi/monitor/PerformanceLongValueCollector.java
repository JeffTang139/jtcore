package org.eclipse.jt.core.spi.monitor;

/**
 * 长整型性能监控值容器接口，数据产生端使用
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceLongValueCollector<TAttachment> extends
		PerformanceValueCollector<TAttachment> {
	public void setValue(long value);

	public long getValue();

	public long incValue(long value);

	public boolean compareAndSet(long expect, long update);
}
