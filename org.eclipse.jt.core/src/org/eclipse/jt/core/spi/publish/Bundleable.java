package org.eclipse.jt.core.spi.publish;

/**
 * 指名与Bundle有关的接口
 * 
 * @author Jeff Tang
 * 
 */
public interface Bundleable {
	/**
	 * 返回所属的Bundle，可能返回null表示不确定
	 */
	public BundleToken getBundle();
}
