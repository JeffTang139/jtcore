package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.def.MetaElement;
import org.eclipse.jt.core.def.NamedElementContainer;

/**
 * ��Ϣ�鶨��
 * 
 * @author Jeff Tang
 * 
 */
public interface InfoGroupDefine extends MetaElement {
	/**
	 * �õ���������
	 * 
	 * @return ���ز�������
	 */
	public NamedElementContainer<? extends InfoDefine> getInfos();
}
