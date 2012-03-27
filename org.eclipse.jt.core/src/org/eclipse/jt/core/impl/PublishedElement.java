package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.service.Publish;

/**
 * 发布的元素的信息
 * 
 * @author Jeff Tang
 * 
 */
public class PublishedElement extends StartupEntry {
	/**
	 * 所在空间
	 */
	protected Space space;
	/**
	 * 所在bundle
	 */
	protected BundleStub bundle;
	/**
	 * 可见性
	 */
	Publish.Mode publishMode;
}
