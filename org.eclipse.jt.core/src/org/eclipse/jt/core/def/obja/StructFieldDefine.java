package org.eclipse.jt.core.def.obja;

import org.eclipse.jt.core.def.FieldDefine;
import org.eclipse.jt.core.type.GUID;
import org.eclipse.jt.core.type.ReadableValue;
import org.eclipse.jt.core.type.WritableValue;


/**
 * 结构字段定义
 * 
 * @author Jeff Tang
 * 
 */
public interface StructFieldDefine extends FieldDefine {
	public StructDefine getOwner();

	/**
	 * 返回是否是状态字段，参与序列化克隆比较等
	 * 
	 * @return 返回是否是状态字段
	 */
	public boolean isStateField();

	public void setFieldValueAsBoolean(Object so, boolean value);

	public void setFieldValueAsBoolean(DynamicObject dynSO, boolean value);

	public boolean getFieldValueAsBoolean(DynamicObject dynSO);

	public boolean getFieldValueAsBoolean(Object so);

	public void setFieldValueAsByte(Object so, byte value);

	public void setFieldValueAsByte(DynamicObject dynSO, byte value);

	public byte getFieldValueAsByte(DynamicObject dynSO);

	public byte getFieldValueAsByte(Object so);

	public void setFieldValueAsShort(Object so, short value);

	public void setFieldValueAsShort(DynamicObject dynSO, short value);

	public short getFieldValueAsShort(DynamicObject dynSO);

	public short getFieldValueAsShort(Object so);

	public void setFieldValueAsInt(Object so, int value);

	public void setFieldValueAsInt(DynamicObject dynSO, int value);

	public int getFieldValueAsInt(DynamicObject dynSO);

	public int getFieldValueAsInt(Object so);

	public void setFieldValueAsLong(Object so, long value);

	public void setFieldValueAsLong(DynamicObject dynSO, long value);

	public long getFieldValueAsLong(DynamicObject dynSO);

	public long getFieldValueAsLong(Object so);

	public void setFieldValueAsDate(Object so, long value);

	public void setFieldValueAsDate(DynamicObject dynSO, long value);

	public long getFieldValueAsDate(DynamicObject dynSO);

	public long getFieldValueAsDate(Object so);

	public void setFieldValueAsDouble(Object so, double value);

	public void setFieldValueAsDouble(DynamicObject dynSO, double value);

	public double getFieldValueAsDouble(DynamicObject dynSO);

	public double getFieldValueAsDouble(Object so);

	public void setFieldValueAsFloat(Object so, float value);

	public void setFieldValueAsFloat(DynamicObject dynSO, float value);

	public float getFieldValueAsFloat(DynamicObject dynSO);

	public float getFieldValueAsFloat(Object so);

	public void setFieldValueAsString(Object so, String value);

	public void setFieldValueAsString(DynamicObject dynSO, String value);

	public String getFieldValueAsString(DynamicObject dynSO);

	public String getFieldValueAsString(Object so);

	public void setFieldValueAsGUID(Object so, GUID value);

	public void setFieldValueAsGUID(DynamicObject dynSO, GUID value);

	public GUID getFieldValueAsGUID(DynamicObject dynSO);

	public GUID getFieldValueAsGUID(Object so);

	public void setFieldValueAsBytes(Object so, byte[] value);

	public void setFieldValueAsBytes(DynamicObject dynSO, byte[] value);

	public byte[] getFieldValueAsBytes(DynamicObject dynSO);

	public byte[] getFieldValueAsBytes(Object so);

	public void setFieldValueAsObject(Object so, Object value);

	public void setFieldValueAsObject(DynamicObject dynSO, Object value);

	public Object getFieldValueAsObject(DynamicObject dynSO);

	public Object getFieldValueAsObject(Object so);

	public void setFieldValueAsChar(Object so, char value);

	public void setFieldValueAsChar(DynamicObject dynSO, char value);

	public char getFieldValueAsChar(DynamicObject dynSO);

	public char getFieldValueAsChar(Object so);

	public void setFieldValue(Object so, ReadableValue value);

	public void setFieldValue(DynamicObject dynSO, ReadableValue value);

	public void assignFieldValueTo(Object so, WritableValue target);

	public void assignFieldValueTo(DynamicObject dynSO, WritableValue target);

	public void loadFieldDefaultValue(DynamicObject dynSO);

	public void loadFieldDefaultValue(Object so);

	public boolean isFieldValueNull(DynamicObject dynSO);

	public boolean isFieldValueNull(Object so);

	public void setFieldValueNull(DynamicObject dynSO);

	public void setFieldValueNull(Object so);
}
