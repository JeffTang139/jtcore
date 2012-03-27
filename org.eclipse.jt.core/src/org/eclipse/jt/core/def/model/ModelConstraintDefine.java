package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.info.InfoDefine;

/**
 * ģ��Լ������
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelConstraintDefine extends InfoDefine {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDefine getOwner();

	/**
	 * ������Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDefine getScript();
}
