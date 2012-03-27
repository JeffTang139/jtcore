package org.eclipse.jt.core.def.arg;

import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.type.DataTypable;
import org.eclipse.jt.core.type.DataType;

/**
 * �������ģ���������
 * 
 * @see org.eclipse.jt.core.def.arg.ArgumentableDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface ArgumentableDeclare extends ArgumentableDefine {

	/**
	 * ��ò�������
	 * 
	 * @return ���ز�������
	 */
	public ModifiableNamedElementContainer<? extends ArgumentDeclare> getArguments();

	/**
	 * ����һ������
	 */
	public ArgumentDeclare newArgument(String name, DataType type);

	/**
	 * ����һ������
	 */
	public ArgumentDeclare newArgument(String name, DataTypable typable);

	/**
	 * ����һ������
	 * 
	 * @param sample
	 *            ���ݸ�ֵ�����ƺ����ʹ�����������
	 */
	public ArgumentDeclare newArgument(FieldDefine sample);
}
