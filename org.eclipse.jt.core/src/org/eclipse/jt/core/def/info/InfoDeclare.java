package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.NamedDeclare;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.Typable;

/**
 * ��Ϣ����ӿڣ�������ϵͳ�����쳣����־����Ϣ��֧�ֶ����ԡ�
 */
public interface InfoDeclare extends InfoDefine, NamedDeclare {
	/**
	 * ���ø�ʽ����Ϣ�ı�<br>
	 * ������"{������}"��ʽ�����ڸ�ʽ���ı���
	 */
	public void setMessage(String value);

	/**
	 * ��ȡ�Ƿ���Ҫ��¼��־
	 */
	public void setNeedLog(boolean value);

	/**
	 * ��ȡ�Ƿ���Ҫͨ���û�
	 */
	public boolean setReportToUser(boolean value);

	/**
	 * ��������
	 */
	public ModifiableNamedElementContainer<? extends InfoParameterDeclare> getParameters();

	/**
	 * ��������
	 */
	public InfoParameterDeclare newParameter(String name, DataType type);

	/**
	 * ��������
	 */

	public InfoParameterDeclare newParameter(String name, Typable typable);

	/**
	 * ��������
	 */
	public InfoParameterDeclare newParameter(FieldDefine sample);
}
