package org.eclipse.jt.core.service;

import org.eclipse.jt.core.impl.DeclaratorBase;

/**
 * �ֲ���������ע����������ʵ������������������ص�ϵͳ����
 * 
 * @author Jeff Tang
 * 
 */
public interface NativeDeclaratorResolver {
	/**
	 * ʵ����������������
	 * 
	 * @param <TDeclarator>
	 *            ����������
	 * @param declaratorClass
	 *            ��������
	 * @param aditionalArgs
	 *            ����Ĳ���
	 * @return ������������ʵ��
	 */
	public <TDeclarator extends DeclaratorBase> TDeclarator resolveDeclarator(
			Class<TDeclarator> declaratorClass, Object... aditionalArgs);
}
