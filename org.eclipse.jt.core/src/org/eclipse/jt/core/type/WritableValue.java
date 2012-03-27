package org.eclipse.jt.core.type;

import org.eclipse.jt.core.type.GUID;


/**
 * 可写值接口
 * 
 * @author Jeff Tang
 * 
 */
public interface WritableValue extends DataTypable {
	/**
	 * 设为空值
	 */
	public void setNull();

	/**
	 * 设值
	 */
	public void setObject(Object value);

	/**
	 * 设值
	 */
	public void setValue(ReadableValue value);

	/**
	 * 布尔型
	 */
	public void setBoolean(boolean value);

	/**
	 * 字符型
	 */
	public void setChar(char value);

	/**
	 * 短整形
	 */
	public void setShort(short value);

	/**
	 * 整数值
	 */
	public void setInt(int value);

	/**
	 * 长整型
	 */
	public void setLong(long value);

	/**
	 * 日期时间
	 */
	public void setDate(long value);

	/**
	 * 单精度浮点小数
	 */
	public void setFloat(float value);

	/**
	 * 双精度浮点小数
	 */
	public void setDouble(double value);

	/**
	 * 字符串
	 */
	public void setString(String value);

	/**
	 * 二进制值
	 */
	public void setByte(byte value);

	/**
	 * 二进制数组
	 */
	public void setBytes(byte[] value);

	/**
	 * GUID类型
	 */
	public void setGUID(GUID guid);
}
