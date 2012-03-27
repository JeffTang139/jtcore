package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.Context;

/**
 * ģ�͹���������ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelConstructorDefine extends ModelInvokeDefine {
	/**
	 * �������Ľű�
	 * 
	 * @return ���ؽű��������
	 */
	public ScriptDefine getScript();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * ����ģ��ʵ������
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param ao
	 *            ����
	 * @return ���ع���õ�ģ��ʵ������
	 */
	public Object newMO(Context context, Object ao);

	/**
	 * �޲ι���ģ��ʵ������
	 * 
	 * @param constructor
	 *            ģ�͹�����
	 */
	public Object newMO(Context context);

}
