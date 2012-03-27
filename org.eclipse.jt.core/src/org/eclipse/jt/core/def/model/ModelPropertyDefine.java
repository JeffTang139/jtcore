package org.eclipse.jt.core.def.model;

import org.eclipse.jt.core.Context;
import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.type.DataTypable;
import org.eclipse.jt.core.type.GUID;


/**
 * 模型属性定义
 * 
 * @author Jeff Tang
 * 
 */
public interface ModelPropertyDefine extends ModelInvokeDefine, DataTypable {
	/**
	 * 返回该属性的设置是否会引发模型状态的变化
	 * 
	 * @return 返回该属性的设置是否会引发模型状态的变化
	 */
	public boolean isStateEffective();

	/**
	 * 获取属性设置器信息
	 * 
	 * @return 返回设置器信息
	 */
	public ModelPropAccessDefine getSetterInfo();

	/**
	 * 获取属性读取器信息
	 * 
	 * @return 返回读取器信息
	 */
	public ModelPropAccessDefine getGetterInfo();

	/**
	 * 获得引用的模型
	 * 
	 * @return 返回模型引用定义
	 */
	public ModelReferenceDefine getRefModel();

	/**
	 * 获得引用属性
	 * 
	 * @return 返回属性引用定义
	 */
	public ModelPropertyDefine getRefProperty();

	/**
	 * 调用值改变之后触发点，包括触发其他的调用或者约束
	 * 
	 * @return 返回触发点集合
	 */
	public Container<? extends InspectPoint> getChangedInspects();

	// /////////////////////////////////////////////////////////
	// /////////////////////// runtime/////////////////////
	// /////////////////////////////////////////////////////////
	/**
	 * 获取列表属性的值列表
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
