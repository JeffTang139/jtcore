package org.eclipse.jt.core.def.obja;

import org.eclipse.jt.core.def.exp.AssignableExpression;

/**
 * �������ñ��ʽ
 * 
 * @author Jeff Tang
 * 
 */
public interface StructFieldRefExpression extends AssignableExpression {
	/**
	 * ������õĲ�����ṹ�Ӷζ���
	 * 
	 * @return ���ز�����ṹ�Ӷζ���
	 */
	public StructFieldDefine getField();
}
