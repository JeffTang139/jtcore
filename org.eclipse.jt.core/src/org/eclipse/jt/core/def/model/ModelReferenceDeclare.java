package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.NamedDeclare;

/**
 * ģ�͹�ϵ����
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelReferenceDeclare extends ModelReferenceDefine,
		NamedDeclare {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDeclare getOwner();
}
