package org.eclipse.jt.core.serial;

import org.eclipse.jt.core.ObjectQuerier;

/**
 * ����ת�������������л��Ϳ�¡���ݶ���<br>
 * ��ĳЩ�����޷��ﵽD&A���л�Ҫ��ʱ��ע��ר�еĶ���ת�����������л�������
 * 
 * @author Jeff Tang
 * 
 */
public interface DataObjectTranslator<TSourceObject, TDelegateObject> {

	/**
	 * ��ȡ��ǰ�Զ������л����汾
	 */
	public short getVersion();

	/**
	 * ��С֧�ֵ����л��汾
	 */
	public short supportedVerionMin();

	/**
	 * �Ƿ�֧�ָ��ƶ���
	 */
	public boolean supportAssign();

	/**
	 * ��ȡ�����л����ݶ���
	 */
	public TDelegateObject toDelegateObject(TSourceObject obj);

	/**
	 * ��ԭ�����ػ�ԭ��Ķ���
	 */
	public TSourceObject recoverObject(TSourceObject destHint,
			TDelegateObject delegate, ObjectQuerier querier, short serialVersion);

}
