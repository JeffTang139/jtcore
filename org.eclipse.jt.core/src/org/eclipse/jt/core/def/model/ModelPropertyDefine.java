package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.type.DataTypable;
import org.eclipse.jt.core.type.GUID;


/**
 * ģ�����Զ���
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelPropertyDefine extends ModelInvokeDefine, DataTypable {
	/**
	 * ���ظ����Ե������Ƿ������ģ��״̬�ı仯
	 * 
	 * @return ���ظ����Ե������Ƿ������ģ��״̬�ı仯
	 */
	public boolean isStateEffective();

	/**
	 * ��ȡ������������Ϣ
	 * 
	 * @return ������������Ϣ
	 */
	public ModelPropAccessDefine getSetterInfo();

	/**
	 * ��ȡ���Զ�ȡ����Ϣ
	 * 
	 * @return ���ض�ȡ����Ϣ
	 */
	public ModelPropAccessDefine getGetterInfo();

	/**
	 * ������õ�ģ��
	 * 
	 * @return ����ģ�����ö���
	 */
	public ModelReferenceDefine getRefModel();

	/**
	 * �����������
	 * 
	 * @return �����������ö���
	 */
	public ModelPropertyDefine getRefProperty();

	/**
	 * ����ֵ�ı�֮�󴥷��㣬�������������ĵ��û���Լ��
	 * 
	 * @return ���ش����㼯��
	 */
	public Container<? extends InspectPoint> getChangedInspects();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime/////////////////////
	// /////////////////////////////////////////////////////////
	/**
	 * ��ȡ�б����Ե�ֵ�б�
	 */
	public ListPropertyValue getPropValueAsList(Context context, Object mo);

	public boolean getPropValueAsBoolean(Context context, Object mo);

	public void setPropValueAsBoolean(Context context, Object mo, boolean value);

	public byte getPropValueAsByte(Context context, Object mo);

	public void setPropValueAsByte(Context context, Object mo, byte value);

	public short getPropValueAsShort(Context context, Object mo);

	public void setPropValueAsShort(Context context, Object mo, short value);

	public int getPropValueAsInt(Context context, Object mo);

	public void setPropValueAsInt(Context context, Object mo, int value);

	public long getPropValueAsLong(Context context, Object mo);

	public void setPropValueAsLong(Context context, Object mo, long value);

	public long getPropValueAsDate(Context context, Object mo);

	public void setPropValueAsDate(Context context, Object mo, long value);

	public double getPropValueAsDouble(Context context, Object mo);

	public void setPropValueAsDouble(Context context, Object mo, double value);

	public float getPropValueAsFloat(Context context, Object mo);

	public void setPropValueAsFloat(Context context, Object mo, float value);

	public String getPropValueAsString(Context context, Object mo);

	public void setPropValueAsString(Context context, Object mo, String value);

	public GUID getPropValueAsGUID(Context context, Object mo);

	public void setPropValueAsGUID(Context context, Object mo, GUID value);

	public byte[] getPropValueAsBytes(Context context, Object mo);

	public void setPropValueAsBytes(Context context, Object mo, byte[] value);

	public Object getPropValueAsObject(Context context, Object mo);

	public void setPropValueAsObject(Context context, Object mo, Object value);

}
