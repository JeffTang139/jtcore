package org.eclipse.jt.core.apc;

import org.eclipse.jt.core.def.apc.Accessibility;
import org.eclipse.jt.core.def.apc.CheckPointDefine;

/**
 * ����ʵ��
 * 
 * @author Jeff Tang
 * 
 */
public interface CheckPoint {
	/**
	 * �����Ҫ���Ĳ���
	 */
	public CheckPointDefine getDefine();

	/**
	 * ��õ�ǰ����
	 */
	public Scene getScene();

	/**
	 * ���¼��Ľ�����ɼ��ϵͳ�ص�
	 */
	public void updateAccessibility(Accessibility accessibility);
}
