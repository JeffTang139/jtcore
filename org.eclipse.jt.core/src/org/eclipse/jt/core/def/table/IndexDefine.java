package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.def.NamedDefine;

/**
 * �������������
 * 
 * @author Jeff Tang
 * 
 */
public interface IndexDefine extends NamedDefine {

	/**
	 * ����
	 */
	public TableDefine getOwner();

	/**
	 * �Ƿ���Ψһ����
	 */
	public boolean isUnique();

	/**
	 * ������������ֶε�ö����
	 * 
	 * @return �����еĵ�����
	 */
	public ModifiableContainer<? extends IndexItemDefine> getItems();

}
