package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.def.FieldDeclare;

/**
 * ��Ϣ��������ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface InfoParameterDeclare extends InfoParameterDefine, FieldDeclare {
	/**
	 * �����Ϣ����
	 * 
	 * @return ������Ϣ����
	 */
	public InfoDeclare getOwner();
}
