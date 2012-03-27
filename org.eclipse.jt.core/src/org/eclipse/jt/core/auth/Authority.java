package org.eclipse.jt.core.auth;

/**
 * 操作权限<br>
 * D&A权限框架支持四种操作权限，分别为：
 * 
 * <pre>
 * 0 : Undefine 未指定授权，即默认授权；
 * 1 : Allow    允许授权；
 * 2 : Deny     拒绝授权；
 * 3 : Own      拥有授权，即可能权限进行再授权。
 * </pre>
 * 
 * @author Jeff Tang 2009-11
 */
public enum Authority {

	/**
	 * 未指定授权，即默认授权
	 */
	UNDEFINE(0),

	/**
	 * 允许授权
	 */
	ALLOW(1),

	/**
	 * 拒绝授权
	 */
	DENY(2);

	/**
	 * 授权代码<br>
	 * 代码定义如下：
	 * 
	 * <pre>
	 * 0 : Undefine 未指定授权，即默认授权；
	 * 1 : Allow    允许授权；
	 * 2 : Deny     拒绝授权。
	 * </pre>
	 */
	public final int code;

	/**
	 * 权限运算掩码。
	 */
	public static final int MASK = 0x3;

	/**
	 * 根据指定代码获取对应授权定义
	 * 
	 * @param code
	 *            授权代码
	 * @return 返回与代码对应的授权定义
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
