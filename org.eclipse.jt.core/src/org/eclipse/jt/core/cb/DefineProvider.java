package org.eclipse.jt.core.cb;

import org.eclipse.jt.core.def.MetaElementType;

/**
 * Ԫ�����ṩ��
 * 
 * <p>
 * �ص��ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface DefineProvider {

	/**
	 * �������Ԫ���ݶ��嵽������
	 * 
	 * @param demander
	 * @param type
	 * @param name
	 */
	public void demand(DefineHolder demander, MetaElementType type, String name);
}
