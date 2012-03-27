package org.eclipse.jt.core.exception;

import org.eclipse.jt.core.ContextKind;
import org.eclipse.jt.core.SessionKind;
import org.eclipse.jt.core.SiteState;

/**
 * վ��״̬��֧��ĳ��������ʱ�׳����쳣
 * 
 * @author Jeff Tang
 * 
 */
public final class UnsupportedContextKindException extends CoreException {
	private static final long serialVersionUID = 1L;
	/**
	 * վ��״̬
	 */
	public final SiteState siteState;
	/**
	 * ������״̬
	 */
	public final ContextKind contextKind;
	/**
	 * �Ự״̬
	 */
	public final SessionKind sessionKind;

	public UnsupportedContextKindException(SiteState siteState,
			SessionKind sessionKind, ContextKind contextKind) {
		super("��վ��Ϊ[" + siteState + "]״̬ʱ��֧�ִ���[" + sessionKind + "]���ͻỰ�µ�["
				+ contextKind + "]����������");
		this.siteState = siteState;
		this.contextKind = contextKind;
		this.sessionKind = sessionKind;
	}
}
