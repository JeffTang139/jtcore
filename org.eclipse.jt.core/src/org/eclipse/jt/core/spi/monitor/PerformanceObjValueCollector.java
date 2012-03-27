package org.eclipse.jt.core.spi.monitor;

/**
 * 对象型性能监控值容器接口，数据产生端使用
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceObjValueCollector<TAttachment, TObject> extends
		PerformanceValueCollector<TAttachment> {
	/**
	 * 设置数据
	 */
	public void setValue(TObject object);

	public TObject getValue();

	public boolean compareAndSet(TObject expect, TObject update);
}
