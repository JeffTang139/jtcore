package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.service.Publish;

/**
 * ������Ԫ�ص���Ϣ
 * 
 * @author Jeff Tang
 * 
 */
public class PublishedElement extends StartupEntry {
	/**
	 * ���ڿռ�
	 */
	protected Space space;
	/**
	 * ����bundle
	 */
	protected BundleStub bundle;
	/**
	 * �ɼ���
	 */
	Publish.Mode publishMode;
}
