package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;

/**
 * ֧��with�����
 * 
 * @author Jeff Tang
 * 
 */
public interface WithableDefine {

	/**
	 * ��ȡ��ʱ��������б�
	 * 
	 * @return δ�����򷵻�null
	 */
	public ModifiableNamedElementContainer<? extends DerivedQueryDefine> getWiths();
}
