package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.model.ModelService;

/**
 * ģ�����Է���������
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelPropAccessDeclare extends ModelPropAccessDefine {
	/**
	 * ��÷������Ľű�
	 * 
	 * @return ���ؽű�����
	 */
	public ScriptDeclare getScript();

	/**
	 * ���Է�������Ӧ���ֶζ���
	 */
	public void setRefField(ModelFieldDefine field);

	/**
	 * ����ģ�����Է�������<br>
	 * �÷����ṩ������ʱģ�������ʹ�ã�ģ���������в���ʹ��
	 * 
	 * @return ���ؾɵ����Է�����
	 */
	public ModelService<?>.ModelPropertyAccessor setAccessor(
			ModelService<?>.ModelPropertyAccessor accessor);
}
