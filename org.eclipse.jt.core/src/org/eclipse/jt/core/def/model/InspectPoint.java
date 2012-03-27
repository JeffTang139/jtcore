package org.eclipse.jt.core.def.model;

/**
 * �����㶨�壬�����˶Զ�����Լ���Ĵ���
 * 
 * @author Jeff Tang
 * 
 */
public interface InspectPoint {
	/**
	 * ���Է��ض�Ӧ�Ķ�������
	 * 
	 * @return ���Է��ض�Ӧ�Ķ����������null
	 */
	public ModelActionDefine asAction();

	/**
	 * ���Է��ض�Ӧ��Լ������
	 * 
	 * @return ���Է��ض�Ӧ��Լ���������null;
	 */
	public ModelConstraintDefine asConstraint();
}
