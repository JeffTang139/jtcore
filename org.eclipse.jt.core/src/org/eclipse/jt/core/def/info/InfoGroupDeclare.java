package org.eclipse.jt.core.def.info;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.NamedDeclare;

/**
 * ��Ϣ������
 * 
 * @author Jeff Tang
 * 
 */
public interface InfoGroupDeclare extends InfoGroupDefine, NamedDeclare {
	/**
	 * �õ���������
	 * 
	 * @return ���ز�������
	 */
	public ModifiableNamedElementContainer<? extends InfoDeclare> getInfos();

	public ErrorInfoDeclare newError(String name, String messageFrmt);

	public WarningInfoDeclare newWarning(String name, String messageFrmt);

	public HintInfoDeclare newHint(String name, String messageFrmt);

	/**
	 * �½�������Ϣ��
	 */
	public ProcessInfoDeclare newProcess(String name, String messageFrmt);
}
