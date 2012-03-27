package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.def.NamedDeclare;

/**
 * �����õ��������������
 * 
 * @author Jeff Tang
 * 
 */
public interface IndexDeclare extends IndexDefine, NamedDeclare {

	/**
	 * ����
	 */
	public TableDeclare getOwner();

	/**
	 * �����Ƿ���Ψһ����
	 */
	public void setUnique(boolean value);

	/**
	 * ������������ֶε�ö����
	 * 
	 * @return �����еĵ�����
	 */
	public ModifiableContainer<? extends IndexItemDeclare> getItems();

	/**
	 * ���������ֶ�
	 * 
	 * @param field
	 */
	public IndexItemDeclare addItem(TableFieldDefine field);

	/**
	 * ���������ֶ�
	 * 
	 * @param field
	 * @param desc
	 * @return
	 */
	public IndexItemDeclare addItem(TableFieldDefine field, boolean desc);

}
