package org.eclipse.jt.core.auth;

/**
 * ����Ȩ��<br>
 * D&AȨ�޿��֧�����ֲ���Ȩ�ޣ��ֱ�Ϊ��
 * 
 * <pre>
 * 0 : Undefine δָ����Ȩ����Ĭ����Ȩ��
 * 1 : Allow    ������Ȩ��
 * 2 : Deny     �ܾ���Ȩ��
 * 3 : Own      ӵ����Ȩ��������Ȩ�޽�������Ȩ��
 * </pre>
 * 
 * @author Jeff Tang 2009-11
 */
public enum Authority {

	/**
	 * δָ����Ȩ����Ĭ����Ȩ
	 */
	UNDEFINE(0),

	/**
	 * ������Ȩ
	 */
	ALLOW(1),

	/**
	 * �ܾ���Ȩ
	 */
	DENY(2);

	/**
	 * ��Ȩ����<br>
	 * ���붨�����£�
	 * 
	 * <pre>
	 * 0 : Undefine δָ����Ȩ����Ĭ����Ȩ��
	 * 1 : Allow    ������Ȩ��
	 * 2 : Deny     �ܾ���Ȩ��
	 * </pre>
	 */
	public final int code;

	/**
	 * Ȩ���������롣
	 */
	public static final int MASK = 0x3;

	/**
	 * ����ָ�������ȡ��Ӧ��Ȩ����
	 * 
	 * @param code
	 *            ��Ȩ����
	 * @return ����������Ӧ����Ȩ����
	 */
	public static final Authority valueOf(int code) {
		switch (code) {
		case 0:
			return UNDEFINE;
		case 1:
			return ALLOW;
		case 2:
			return DENY;
		default:
			throw new IllegalArgumentException("code : [0, 3]");
		}
	}

	private Authority(int code) {
		this.code = code;
	}

}
