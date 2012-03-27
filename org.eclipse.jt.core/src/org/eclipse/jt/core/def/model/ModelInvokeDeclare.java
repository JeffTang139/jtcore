package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.def.arg.ArgumentableDeclare;

/**
 * ģ�͵��ö��壬��ģ�����ԣ�����������֮���ӿ�
 * 
 * @author Jeff Tang
 * @param <TAO>
 *            ���õĲ���ʵ�����ͣ�Object����յĲ���
 */
public abstract interface ModelInvokeDeclare extends ModelInvokeDefine,
		NamedDeclare, ArgumentableDeclare {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDeclare getOwner();

	/**
	 * �����Ƿ���ҪȨ�޿���
	 */
	public void setAuthorizable(boolean value);

	/**
	 * ���ÿ�ʼ֮ǰ�Ĵ����㣬�������������ĵ��û���Լ��
	 * 
	 * @return ���ش����㼯��
	 */
	public ModifiableContainer<? extends InspectPoint> getBeforeInspects();

	/**
	 * ����ǰ������
	 * 
	 * @param action
	 *            ǰ�����Ķ���
	 * @return ������
	 */
	public InspectPoint newBeforeInspect(ModelActionDefine action);

	/**
	 * ����ǰ������
	 * 
	 * @param constraint
	 *            ǰ������Լ��
	 * @return ������
	 */
	public InspectPoint newBeforeInspect(ModelConstraintDefine constraint);

	/**
	 * �������֮��Ĵ����㣬�������������ĵ��û���Լ��
	 * 
	 * @return ���ش����㼯��
	 */
	public ModifiableContainer<? extends InspectPoint> getAfterInspects();

	/**
	 * �����󴥷���
	 * 
	 * @param action
	 *            �󴥷��Ķ���
	 * @return ������
	 */
	public InspectPoint newAfterInspect(ModelActionDefine action);

	/**
	 * �����󴥷���
	 * 
	 * @param constraint
	 *            �󴥷���Լ��
	 * @return ������
	 */
	public InspectPoint newAfterInspect(ModelConstraintDefine constraint);

	/**
	 * �������֮��Ĵ����㣬�������������ĵ���
	 * 
	 * @return ���ش����㼯��
	 */
	public ModifiableContainer<? extends InspectPoint> getFinallyInspects();

	/**
	 * �����󴥷���
	 * 
	 * @param action
	 *            �󴥷��Ķ���
	 * @return ������
	 */
	public InspectPoint newFinallyInspect(ModelActionDefine action);
}
