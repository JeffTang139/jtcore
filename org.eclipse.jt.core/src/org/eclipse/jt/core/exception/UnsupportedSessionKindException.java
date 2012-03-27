package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.SessionKind;
import org.eclipse.jt.core.SiteState;

/**
 * վ��״̬��֧��ĳ�ֻỰʱ�׳����쳣
 * 
 * @author Jeff Tang
 * 
 */
public final class UnsupportedSessionKindException extends CoreException {
	private static final long serialVersionUID = 1L;
	/**
	 * վ��״̬
	 */
	public final SiteState siteState;
	/**
	 * �Ự״̬
	 */
	public final SessionKind sessionKind;

	public UnsupportedSessionKindException(SiteState siteState,
			SessionKind sessionKind) {
		super("��վ��Ϊ[" + siteState + "]״̬ʱ��֧�ִ���[" + sessionKind + "]���͵ĻỰ");
		this.siteState = siteState;
		this.sessionKind = sessionKind;
	}
}
