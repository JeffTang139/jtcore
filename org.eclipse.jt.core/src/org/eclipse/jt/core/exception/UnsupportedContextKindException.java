package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.SessionKind;
import org.eclipse.jt.core.SiteState;

/**
 * 站点状态不支持某种上下文时抛出的异常
 * 
 * @author Jeff Tang
 * 
 */
public final class UnsupportedContextKindException extends CoreException {
	private static final long serialVersionUID = 1L;
	/**
	 * 站点状态
	 */
	public final SiteState siteState;
	/**
	 * 上下文状态
	 */
	public final ContextKind contextKind;
	/**
	 * 会话状态
	 */
	public final SessionKind sessionKind;

	public UnsupportedContextKindException(SiteState siteState,
			SessionKind sessionKind, ContextKind contextKind) {
		super("当站点为[" + siteState + "]状态时不支持创建[" + sessionKind + "]类型会话下的["
				+ contextKind + "]类型上下文");
		this.siteState = siteState;
		this.contextKind = contextKind;
		this.sessionKind = sessionKind;
	}
}
