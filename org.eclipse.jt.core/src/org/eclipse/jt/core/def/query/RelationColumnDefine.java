package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.NamedDefine;

/**
 * ��ϵ�ж���
 * 
 * @author Jeff Tang
 * 
 */
public interface RelationColumnDefine extends NamedDefine {

	/**
	 * ��ȡ�����Ĺ�ϵ����
	 * 
	 * @return ��ϵ����
	 */
	public RelationDefine getOwner();
}
