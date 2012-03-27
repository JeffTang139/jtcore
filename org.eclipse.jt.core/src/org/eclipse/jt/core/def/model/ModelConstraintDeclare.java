package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.info.InfoDeclare;
import org.eclipse.jt.core.model.ModelService;

/**
 * ģ��Լ������
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelConstraintDeclare extends ModelConstraintDefine,
        InfoDeclare {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDeclare getOwner();

	/**
	 * ������Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDeclare getScript();

	/**
	 * ����ģ��Լ���������<br>
	 * �÷����ṩ������ʱģ�������ʹ�ã�ģ���������в���ʹ��
	 * 
	 * @return ���ؾɵ�Լ�������
	 */
	public ModelService<?>.ModelConstraintChecker setChecker(
	        ModelService<?>.ModelConstraintChecker checker);

}
