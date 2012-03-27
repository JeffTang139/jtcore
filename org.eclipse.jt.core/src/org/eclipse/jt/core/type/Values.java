package org.eclipse.jt.core.type;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.jt.core.type.GUID;


public interface Values {

	/**
	 * ���ĳ��λ�õ�ֵ
	 * 
	 * @param index λ��
	 * @return ����ֵ
	 */
	public abstract Object getValue(int index);

	public abstract void setValue(int index, Object value);

	public abstract void setValue(int index, ReadableValue value);

	/**
	 * ������
	 * 
	 * @param index
	 * @return ����ֵ
	 */
	public abstract boolean getBoolean(int index);

	public abstract void setBoolean(int index, boolean value);

	public abstract short getShort(int index);

	public abstract void setShort(int index, short value);

	/**
	 * �������ֵ
	 * 
	 * @param index λ��
	 * @return ��������ֵ
	 */
	public abstract int getInt(int index);

	public abstract void setInt(int index, int value);

	/**
	 * ������
	 */
	public abstract long getLong(int index);

	public abstract void setLong(int index, long value);

	/**
	 * ʱ��
	 */
	public abstract long getDate(int index);

	public abstract void setDate(int index, long value);

	public abstract float getFloat(int index);

	public abstract void setFloat(int index, float value);

	/**
	 * ���˫������ֵ
	 * 
	 * @param index λ��
	 * @return ���ظ���ֵ
	 */
	public abstract double getDouble(int index);

	public abstract void setDouble(int index, double value);

	/**
	 * ����ַ�����ֵ
	 * 
	 * @param index λ��
	 * @return �����ַ���
	 */
	public abstract String getString(int index);

	public abstract void setString(int index, String value);

	public abstract byte getByte(int index);

	public abstract void setByte(int index, byte value);

	/**
	 * �ֽ�
	 */
	public abstract byte[] getBytes(int index);

	public abstract void setBytes(int index, byte[] value);

	/**
	 * GUID
	 */
	public abstract GUID getGUID(int index);

	public abstract void setGUID(int index, GUID value);

	/**
	 * �ӽ������װ������
	 * 
	 * @param index λ��
	 * @param resultSet �����
	 * @param columnIndex �������λ��
	 * @return ���شӽ�������Ƿ��ȡ��ֵΪ��
	 */
	public abstract boolean loadValue(int index, ResultSet resultSet,
			int columnIndex) throws SQLException;
}
