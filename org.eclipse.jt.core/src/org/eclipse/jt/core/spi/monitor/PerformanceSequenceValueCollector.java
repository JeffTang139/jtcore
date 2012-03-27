package org.eclipse.jt.core.spi.monitor;

/**
 * 对象序列型性能监控值容器接口，数据产生端使用
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceSequenceValueCollector<TAttachment, TObject>
		extends PerformanceValueCollector<TAttachment> {
	/**
	 * 追加数据，返回false表示溢出
	 */
	public boolean append(TObject object);
}
