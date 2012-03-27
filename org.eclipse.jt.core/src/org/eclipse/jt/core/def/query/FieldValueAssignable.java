package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.table.TableFieldDefine;
import org.eclipse.jt.core.type.DataType;

public interface FieldValueAssignable {

	/**
	 * �����ֶεĳ���ֵ
	 * 
	 * @param field
	 *            �ֶζ���
	 * @param value
	 *            ����ֵ
	 */
	public void assignConst(TableFieldDefine field, Object value);

	/**
	 * �����ֶεĲ���ֵ
	 * 
	 * @param field
	 * @param argument
	 */
	public void assignArgument(TableFieldDefine field, ArgumentDefine argument);

	/**
	 * ��ָ�������������͹����������,�������ֶε�ֵΪ�ò���,���ز�������
	 * 
	 * @param field
	 *            ����ı��ֶ�
	 * @param name
	 *            ������
	 * @param type
	 *            ��������
	 * @return
	 */
	public ArgumentDefine assignArgument(TableFieldDefine field, String name,
			DataType type);

	/**
	 * ���ݱ��ֶι����������,�������ֶε�ֵΪ�ò���,���ز�������
	 * 
	 * @param field
	 *            ����ı��ֶ�
	 * @return
	 */
	public ArgumentDefine assignArgument(TableFieldDefine field);

	/**
	 * �����ֶεı��ʽֵ
	 * 
	 * @param field
	 * @param value
	 */
	public void assignExpression(TableFieldDefine field, ValueExpression value);
}
