package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.Context;

/**
 * ģ�Ͷ�������
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelActionDefine extends ModelInvokeDefine {
	/**
	 * �����Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDefine getScript();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * ִ�ж���
	 * 
	 * @param context
	 *            ������
	 * @param mo
	 *            ģ�Ͷ���
	 */
	public void execute(Context context, Object mo);

	/**
	 * ִ�ж���
	 * 
	 * @param context
	 *            ������
	 * @param mo
	 *            ģ�Ͷ���
	 * @param ao
	 *            ��������
	 */
	public void execute(Context context, Object mo, Object ao);
}
