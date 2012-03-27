package org.eclipse.jt.core.type;

/**
 * 数据类型类
 * 
 * @author Jeff Tang
 * 
 */
public interface Type extends TypeDigestible {
	/**
	 * 获得对应的根类别
	 */
	public Type getRootType();

	/**
	 * 让类型回调detector抽象类，以确认类型的多态信息
	 */
	public <TResult, TUserData> TResult detect(
			TypeDetector<TResult, TUserData> detector, TUserData userData)
			throws UnsupportedOperationException ;
}
