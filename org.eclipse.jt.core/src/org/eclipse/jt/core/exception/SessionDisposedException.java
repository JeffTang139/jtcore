package org.eclipse.jt.core.exception;

public final class SessionDisposedException extends DisposedException {

	private static final long serialVersionUID = 1L;

	/**
	 * �رյ�������
	 * 
	 * @author Jeff Tang
	 * 
	 */
	public enum SessionDisposedKind {
		/**
		 * һ��ر�
		 */
		NORMAL,
		/**
		 * ���ȵĻỰ�������Ǳ���D&A�������Session��
		 */
		OBSOLETE,
		/**
		 * ��½�û�ʧЧ
		 */
		USERINVALID,
	}

	/**
	 * ���Ǳ���D&A�������Session
	 */
	public final SessionDisposedKind kind;
	/**
	 * ���Ǳ���D&A�������Session������2.5��ǰ�İ汾
	 */
	@Deprecated
	public final boolean obsolete;

	public SessionDisposedException(SessionDisposedKind kind) {
		super("�Ự�Ѿ����ٻ����");
		this.kind = kind;
		this.obsolete = kind == SessionDisposedKind.OBSOLETE;
	}
}
