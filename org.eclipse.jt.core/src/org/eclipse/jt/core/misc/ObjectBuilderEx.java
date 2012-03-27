package org.eclipse.jt.core.misc;

/**
 * 对象构造扩展接口
 * 
 * @author Jeff Tang
 * 
 * @param <TObject> 对象类型
 * @param <TUserData> 用户数据类型
 */
public interface ObjectBuilderEx<TObject, TUserData> {
	public TObject build(TUserData userData) throws Throwable;
}
