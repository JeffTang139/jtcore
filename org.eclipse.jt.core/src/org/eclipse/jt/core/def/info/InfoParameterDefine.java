package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.def.FieldDefine;

/**
 * ��Ϣ��������ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface InfoParameterDefine extends FieldDefine {
	/**
	 * �����Ϣ����
	 * 
	 * @return ������Ϣ����
	 */
	public InfoDefine getOwner();
}
