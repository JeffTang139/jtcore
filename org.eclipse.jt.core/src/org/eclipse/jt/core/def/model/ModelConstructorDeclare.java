package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.model.ModelService;

/**
 * ģ�͹���������ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelConstructorDeclare extends ModelConstructorDefine,
		ModelInvokeDeclare {
	/**
	 * �������Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDeclare getScript();

	/**
	 * ����ģ�͹�������<br>
	 * �÷����ṩ������ʱģ�������ʹ�ã�ģ���������в���ʹ��
	 * 
	 * @return ���ؾɵĹ�����
	 */
	public ModelService<?>.ModelConstructor<?> setConstructor(
			ModelService<?>.ModelConstructor<?> constructor);

}
