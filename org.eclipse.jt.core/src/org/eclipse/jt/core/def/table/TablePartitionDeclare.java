package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.exception.NoPartitionDefineException;

/**
 * ���������
 * 
 * @author Jeff Tang
 * 
 */
public interface TablePartitionDeclare extends TablePartitionDefine {

	public ModifiableNamedElementContainer<? extends TableFieldDefine> getPartitionFields();

	/**
	 * ���÷����ֶ�
	 * 
	 * @param field
	 * @param others
	 */
	public void setPartitionFields(TableFieldDefine field,
			TableFieldDefine... others);

	/**
	 * ���ӷ����ֶ�
	 * 
	 * @param field
	 * @param others
	 */
	public void addPartitionField(TableFieldDefine field,
			TableFieldDefine... others);

	/**
	 * ���÷�����������
	 * 
	 * @param suggestion
	 * @throws NoPartitionDefineException
	 */
	public void setParitionSuggestion(int suggestion)
			throws NoPartitionDefineException;

	/**
	 * ���ñ��������������
	 * 
	 * <p>
	 * Ĭ��0,��Ϊ��ǰ���ݿ���֧�ֵ����������
	 * 
	 * @param maxPartitionCount
	 * @throws NoPartitionDefineException
	 */
	public void setMaxPartitionCount(int maxPartitionCount)
			throws NoPartitionDefineException;
}
