package org.eclipse.jt.core.def.arg;

import org.eclipse.jt.core.def.exp.AssignableExpression;

/**
 * �������ñ��ʽ
 * 
 * @author Jeff Tang
 * 
 */
public interface ArgumentRefExpression extends AssignableExpression {
	/**
	 * ������õĲ�����ṹ�Ӷζ���
	 * 
	 * @return ���ز�����ṹ�Ӷζ���
	 */
	public ArgumentDefine getArgument();
}
