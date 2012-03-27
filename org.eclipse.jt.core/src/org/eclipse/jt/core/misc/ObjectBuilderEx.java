package org.eclipse.jt.core.misc;

/**
 * ��������չ�ӿ�
 * 
 * @author Jeff Tang
 * 
 * @param <TObject> ��������
 * @param <TUserData> �û���������
 */
public interface ObjectBuilderEx<TObject, TUserData> {
	public TObject build(TUserData userData) throws Throwable;
}
