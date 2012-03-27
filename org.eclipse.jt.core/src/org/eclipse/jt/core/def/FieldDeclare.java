package org.eclipse.jt.core.def;

import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * �����õ��ֶλ��ӿڶ���
 * 
 * @author Jeff Tang
 * 
 */
public interface FieldDeclare extends FieldDefine, NamedDeclare {
	/**
	 * �����Ƿ�Ҫ�󱣳���Ч
	 * 
	 * @param value �Ƿ�Ҫ�󱣳���Ч
	 */
	public void setKeepValid(boolean value);

	/**
	 * ����ֻ������
	 * 
	 * @param value �Ƿ�ֻ��
	 */
	public void setReadonly(boolean value);

	/**
	 * ����Ĭ��ֵ
	 * 
	 * @param exp Ĭ��ֵ���ʽ
	 */
	public void setDefault(ValueExpression exp);
}
