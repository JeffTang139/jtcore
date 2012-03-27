package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.apc.CheckPointDefine;
import org.eclipse.jt.core.def.arg.ArgumentableDefine;

/**
 * ģ�͵��ö��壬��ģ�����ԣ�����������֮���ӿ�
 * 
 * @author Jeff Tang
 * @param <TAO>
 *            ���õĲ���ʵ�����ͣ�Object����յĲ���
 */
public abstract interface ModelInvokeDefine extends NamedDefine,
        ArgumentableDefine, CheckPointDefine {
	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public ModelDefine getOwner();

	/**
	 * ����Ƿ���ҪȨ�޿���
	 * 
	 * @return �����Ƿ�Ȩ�޿���
	 */
	public boolean isAuthorizable();

	/**
	 * ���ÿ�ʼ֮ǰ�ļ��㣬�������������ĵ��û���Լ��
	 * 
	 * @return ���ؼ��㼯��
	 */
	public Container<? extends InspectPoint> getBeforeInspects();

	/**
	 * �������֮��ļ��㣬�������������ĵ��û���Լ��
	 * 
	 * @return ���ؼ��㼯��
	 */
	public Container<? extends InspectPoint> getAfterInspects();

	/**
	 * �������֮��ļ��㣬�������������ĵ���
	 * 
	 * @return ���ؼ��㼯��
	 */
	public Container<? extends InspectPoint> getFinallyInspects();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime /////////////////////////
	// /////////////////////////////////////////////////////////

	/**
	 * ��õ��õ���Ч��
	 * 
	 * @param context
	 *            ������
	 * @param mo
	 *            ģ�Ͷ���
	 * @return ������Ч��
	 */
	public ModelInvokeValidity getValidity(Context context, Object mo);
}
