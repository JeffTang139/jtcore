package org.eclipse.jt.core.impl;

/**
 * 资源操作工具类
 * 
 * @author Jeff Tang 2009-12
 */
final class OperationUtil {

	/**
	 * 换算权限掩码<br>
	 * 
	 * <pre>
	 * opMask   = 00000000000000000000000000011001B
	 * authMask = 00000000000000000000001111000011B
	 * </pre>
	 * 
	 * @param opMask
	 *            操作定义掩码，后十六位有效
	 * @return 返回权限掩码
	 */
	public static final int calAuthMask(int opMask) {
		return colAuth(opMask, 3);
	}

	/**
	 * 换算权限码<br>
	 * 
	 * <pre>
	 * opMask   = 00000000000000000000000000011001B
	 * auth     = 10B
	 * authCode = 00000000000000000000001010000010B
	 * </pre>
	 * 
	 * @param opMask
	 *            操作定义掩码，后十六位有效
	 * @param auth
	 *            授权，后两位有效
	 * @return 返回权限码
	 */
	public static final int calAuthCode(int opMask, int auth) {
		if ((auth & 3) == 0) {
			return 0;
		}
		return colAuth(opMask, auth);
	}

	/**
	 * 计算操作第一个操作标识位
	 * 
	 * <pre>
	 * opMask   = 00000000000000000000000000011000B
	 * return   = 4
	 * </pre>
	 * 
	 * @param opMask
	 *            操作定义掩码，后十六位有效
	 * @return 返回操作第一个操作标识位
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
