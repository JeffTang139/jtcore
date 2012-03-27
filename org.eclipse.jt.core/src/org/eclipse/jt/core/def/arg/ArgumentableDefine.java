package org.eclipse.jt.core.def.arg;

import org.eclipse.jt.core.def.DefineBase;
import org.eclipse.jt.core.def.NamedElementContainer;

/**
 * �������ģ���������
 * 
 * @author Jeff Tang
 * 
 */
public interface ArgumentableDefine extends DefineBase {
	/**
	 * ��ò�������
	 * 
	 * @return ���ز�������
	 */
	public NamedElementContainer<? extends ArgumentDefine> getArguments();

	/**
	 * ��ȡ�����������
	 * 
	 * @return ���ز���ʵ��������
	 */
	public Class<?> getAOClass();

	/**
	 * ������������
	 * 
	 * @return �����½��Ĳ�������
	 */
	public Object newAO();

	/**
	 * ��ֵ�б�ת���ɶ�Ӧ�Ĳ�������
	 */
	public Object newAO(Object... args);

}
