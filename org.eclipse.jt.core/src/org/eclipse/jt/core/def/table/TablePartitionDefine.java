package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.DefineBase;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.exception.NoPartitionDefineException;

/**
 * ���������
 * 
 * @author Jeff Tang
 */
public interface TablePartitionDefine extends DefineBase {

	/**
	 * �Ƿ����
	 */
	public boolean isPartitioned();

	/**
	 * ����������
	 * 
	 * @return
	 * @throws NoPartitionDefineException
	 */
	public int getPartitionSuggestion() throws NoPartitionDefineException;

	/**
	 * ��������ķ�������
	 * 
	 * @return
	 * @throws NoPartitionDefineException
	 */
	public int getMaxPartitionCount() throws NoPartitionDefineException;

	/**
	 * �������ֶ�
	 */
	public NamedElementContainer<? extends TableFieldDefine> getPartitionFields();
}
