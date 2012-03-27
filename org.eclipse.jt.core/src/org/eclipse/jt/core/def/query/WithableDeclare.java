package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;

/**
 * ֧��with�����
 * 
 * @author Jeff Tang
 * 
 */
public interface WithableDeclare extends WithableDefine {

	public ModifiableNamedElementContainer<? extends DerivedQueryDeclare> getWiths();

	/**
	 * ʹ��with�Ӿ�,������ʱ�����
	 * 
	 * @param name
	 *            ��ʱ�����������,������������ʱ����������ظ�
	 * @return
	 */
	public DerivedQueryDeclare newWith(String name);
}
