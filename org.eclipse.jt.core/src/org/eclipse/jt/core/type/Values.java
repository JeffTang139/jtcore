package org.eclipse.jt.core.type;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jt.core.type.GUID;


public interface Values {

	/**
	 * 获得某个位置的值
	 * 
	 * @param index 位置
	 * @return 返回值
	 */
	public abstract Object getValue(int index);

	public abstract void setValue(int index, Object value);

	public abstract void setValue(int index, ReadableValue value);

	/**
	 * 布尔型
	 * 
	 * @param index
	 * @return 返回值
	 */
	public abstract boolean getBoolean(int index);

	public abstract void setBoolean(int index, boolean value);

	public abstract short getShort(int index);

	public abstract void setShort(int index, short value);

	/**
	 * 获得整数值
	 * 
	 * @param index 位置
	 * @return 返回整数值
	 */
	public abstract int getInt(int index);

	public abstract void setInt(int index, int value);

	/**
	 * 长整形
	 */
	public abstract long getLong(int index);

	public abstract void setLong(int index, long value);

	/**
	 * 时间
	 */
	public abstract long getDate(int index);

	public abstract void setDate(int index, long value);

	public abstract float getFloat(int index);

	public abstract void setFloat(int index, float value);

	/**
	 * 获得双精度数值
	 * 
	 * @param index 位置
	 * @return 返回浮点值
	 */
	public abstract double getDouble(int index);

	public abstract void setDouble(int index, double value);

	/**
	 * 获得字符窜数值
	 * 
	 * @param index 位置
	 * @return 返回字符串
	 */
	public abstract String getString(int index);

	public abstract void setString(int index, String value);

	public abstract byte getByte(int index);

	public abstract void setByte(int index, byte value);

	/**
	 * 字节
	 */
	public abstract byte[] getBytes(int index);

	public abstract void setBytes(int index, byte[] value);

	/**
	 * GUID
	 */
	public abstract GUID getGUID(int index);

	public abstract void setGUID(int index, GUID value);

	/**
	 * 从结果集中装载数据
	 * 
	 * @param index 位置
	 * @param resultSet 结果集
	 * @param columnIndex 结果集列位置
	 * @return 返回从结果集中是否读取的值为空
	 */
	public abstract boolean loadValue(int index, ResultSet resultSet,
			int columnIndex) throws SQLException;
}
