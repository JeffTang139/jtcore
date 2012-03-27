package org.eclipse.jt.core.resource;

/**
 * 资源子项链节
 * 
 * @author Jeff Tang
 * 
 */
public interface ResourceTokenLink<TFacade> {
	/**
	 * 节点上的资源标识
	 */
	public ResourceToken<TFacade> getToken();

	/**
	 * 下一个链节，或者null。
	 */
	public ResourceTokenLink<TFacade> next();
}
