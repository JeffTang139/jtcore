package org.eclipse.jt.core.exception;

/**
 * 不支持权限资源异常<br>
 * 一般在访问不需权限验证的资源，却试图调用与权限验证有关方法时，抛出此异常。
 * 
 * @see org.eclipse.jt.core.exception.CoreException
 * @author Jeff Tang 2010-01-08
 */
public class UnsupportedAuthorityResourceException extends CoreException {

	private static final long serialVersionUID = 479620252874514960L;

	/**
	 * 抛出不支持权限资源异常<br>
	 * 一般在访问不需权限验证的资源，却试图调用与权限验证有关方法时，抛出此异常。
	 * 
	 * @param message
	 *            异常信息
	 */
	public UnsupportedAuthorityResourceException(String message) {
		super(message);
	}

	/**
	 * 抛出不支持权限资源异常<br>
	 * 一般在访问不需权限验证的资源，却试图调用与权限验证有关方法时，抛出此异常。
	 * 
	 * @param resourceFacadeClass
	 *            试图访问的资源外观类
	 */
	public UnsupportedAuthorityResourceException(Class<?> resourceFacadeClass) {
		super("[" + resourceFacadeClass.toString() + "]类资源不支持权限操作。");
	}

}
