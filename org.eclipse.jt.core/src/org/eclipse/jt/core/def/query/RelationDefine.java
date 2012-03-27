package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * ��ϵ��Ԫ����
 * 
 * <p>
 * ��ϵ��Ԫ���弴Ϊ��ά����ʽ�����ݽṹ��Ԫ���ݶ���
 * 
 * @author Jeff Tang
 * 
 */
public interface RelationDefine extends NamedDefine {

	/**
	 * ����ָ�����ƵĹ�ϵ�ж���
	 * 
	 * @param columnName
	 * @return ���ع�ϵ�ж����null
	 */
	public RelationColumnDefine findColumn(String columnName);

	/**
	 * ��ȡָ�����ƵĹ�ϵ�ж���
	 * 
	 * @param columnName
	 * @return ���ع�ϵ�ж�����׳��쳣
	 */
	public RelationColumnDefine getColumn(String columnName);
}
