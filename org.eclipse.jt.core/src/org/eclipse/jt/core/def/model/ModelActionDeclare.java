package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.model.ModelService;

/**
 * ģ�Ͷ�������ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelActionDeclare extends ModelActionDefine,
		ModelInvokeDeclare {
	/**
	 * �����Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDeclare getScript();

	/**
	 * ����ģ�Ͷ�����������<br>
	 * �÷����ṩ������ʱģ�������ʹ�ã�ģ���������в���ʹ��
	 * 
	 * @return ���ؾɵĴ�����
	 */
	public ModelService<?>.ModelActionHandler<?> setHandler(
			ModelService<?>.ModelActionHandler<?> handler);
}
