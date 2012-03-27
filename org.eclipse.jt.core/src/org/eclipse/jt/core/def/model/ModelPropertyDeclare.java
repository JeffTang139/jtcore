package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.ModifiableContainer;

/**
 * ģ�����Զ���
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelPropertyDeclare extends ModelPropertyDefine,
        ModelInvokeDeclare {
	/**
	 * ���ø����Եĸ�ֵ�Ƿ������ģ��״̬�ı仯
	 */
	public void setStateEffective(boolean value);

	/**
	 * ��ȡ������������Ϣ
	 * 
	 * @return ������������Ϣ
	 */
	public ModelPropAccessDeclare getSetterInfo();

	/**
	 * ��ȡ���Զ�ȡ����Ϣ
	 * 
	 * @return ���ض�ȡ����Ϣ
	 */
	public ModelPropAccessDeclare getGetterInfo();

	/**
	 * �������õ�ģ��
	 */
	public void setModelReference(ModelReferenceDefine value);

	/**
	 * ������������
	 */
	public void setPropertyReference(ModelPropertyDefine value);

	/**
	 * ����ֵ�ı�󴥷��㣬�������������ĵ��û���Լ��
	 * 
	 * @return ���ش����㼯��
	 */
	public ModifiableContainer<? extends InspectPoint> getChangedInspects();

	/**
	 * ��������ֵ�ı�󴥷���
	 * 
	 * @param action
	 *            ���Ըı�󴥷��Ķ���
	 * @return ������
	 */
	public InspectPoint newChangedInspect(ModelActionDefine action);

	/**
	 * ��������ֵ�ı�󴥷���
	 * 
	 * @param constraint
	 *            ���Ըı�󴥷���Լ��
	 * @return ������
	 */
	public InspectPoint newChangedInspect(ModelConstraintDefine constraint);
}
