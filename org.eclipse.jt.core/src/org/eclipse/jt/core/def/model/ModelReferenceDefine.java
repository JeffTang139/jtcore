package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * ģ�͹�ϵ����
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelReferenceDefine extends NamedDefine {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDefine getOwner();

	/**
	 * ��ȡĿ��ģ��
	 * 
	 * @return ����Ŀ��ģ��
	 */
	public ModelDefine getTarget();
}
