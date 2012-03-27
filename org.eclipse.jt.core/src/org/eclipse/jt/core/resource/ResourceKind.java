package org.eclipse.jt.core.resource;

/**
 * 资源类型标记
 * 
 * @author Jeff Tang
 * 
 */
public enum ResourceKind {
	/**
	 * 登陆级单例资源<br>
	 * 多线程访问时，读与读并发，读与写、写与写互斥
	 */
	SINGLETON_IN_LOGIN(false, true, false),
	/**
	 * 全局单例资源<br>
	 * 多线程访问时，读与读并发，读与写、写与写互斥
	 */
	SINGLETON_IN_SITE(true, false, false),
	/**
	 * 全局单例资源(支持集群)<br>
	 * 多线程访问时，读与读并发，读与写、写与写互斥
	 */
	SINGLETON_IN_CLUSTER(true, false, true);
	/**
	 * 全局单例资源<br>
	 * 多线程访问时，读与读并发，读与写、写与写互斥 请使用{@code ResourceKind.SINGLETON_IN_SITE}
	 */
	@Deprecated
	public final static ResourceKind SINGLETON_IN_GLOBAL = SINGLETON_IN_SITE;

	/**
	 * 是否是全局资源
	 */
	public final boolean isGlobal;
	public final boolean inSession;
	public final boolean inCluster;

	ResourceKind(boolean isGlobal, boolean inSession, boolean inCluster) {
		this.isGlobal = isGlobal;
		this.inSession = inSession;
		this.inCluster = inCluster;
	}
}
