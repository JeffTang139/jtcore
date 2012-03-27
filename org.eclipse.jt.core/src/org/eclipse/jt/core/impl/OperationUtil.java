package org.eclipse.jt.core.impl;

/**
 * ��Դ����������
 * 
 * @author Jeff Tang 2009-12
 */
final class OperationUtil {

	/**
	 * ����Ȩ������<br>
	 * 
	 * <pre>
	 * opMask   = 00000000000000000000000000011001B
	 * authMask = 00000000000000000000001111000011B
	 * </pre>
	 * 
	 * @param opMask
	 *            �����������룬��ʮ��λ��Ч
	 * @return ����Ȩ������
	 */
	public static final int calAuthMask(int opMask) {
		return colAuth(opMask, 3);
	}

	/**
	 * ����Ȩ����<br>
	 * 
	 * <pre>
	 * opMask   = 00000000000000000000000000011001B
	 * auth     = 10B
	 * authCode = 00000000000000000000001010000010B
	 * </pre>
	 * 
	 * @param opMask
	 *            �����������룬��ʮ��λ��Ч
	 * @param auth
	 *            ��Ȩ������λ��Ч
	 * @return ����Ȩ����
	 */
	public static final int calAuthCode(int opMask, int auth) {
		if ((auth & 3) == 0) {
			return 0;
		}
		return colAuth(opMask, auth);
	}

	/**
	 * ���������һ��������ʶλ
	 * 
	 * <pre>
	 * opMask   = 00000000000000000000000000011000B
	 * return   = 4
	 * </pre>
	 * 
	 * @param opMask
	 *            �����������룬��ʮ��λ��Ч
	 * @return ���ز�����һ��������ʶλ
	 */
	public static final int firstOpIndex(int opMask) {
		for (int i = 0; i < 16; i++) {
			if ((1 & opMask) != 0) {
				return i;
			}
		}
		return -1;
	}

	private static final int colAuth(int opMask, int auth) {
		int result = 0;
		int auM = auth & 3;
		int opM = 1;
		for (int i = 0; i < 16; i++) {
			if ((opM & opMask) != 0) {
				result |= auM;
			}
			auM <<= 2;
			opM <<= 1;
		}
		return result;
	}

	private OperationUtil() {
	}

}
