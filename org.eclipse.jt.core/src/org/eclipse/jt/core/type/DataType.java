package org.eclipse.jt.core.type;

import org.eclipse.jt.core.type.GUID;

/**
 * ��ֵ��ṹ���ͣ������ڳ�������ͣ������ѯ��
 * 
 * @author Jeff Tang
 * 
 */
public interface DataType extends Type {

	/**
	 * ��ȡ����ID
	 */
	public GUID getID();

	/**
	 * ������Ͷ�Ӧ��Java����
	 * 
	 * @return
	 */
	public Class<?> getJavaClass();

	/**
	 * ��ø����ͣ���Varchar�ĸ�������string��
	 */
	public DataType getRootType();

	/**
	 * �жϸ����Ϳɷ������ݿ��ת��ΪĿ������
	 * 
	 * @param target
	 *            Ŀ������
	 * @return
	 */
	public boolean canDBTypeConvertTo(DataType target);

	/**
	 * ���㵱ǰ���ͱ�Ŀ�����͵ĸ�ֵ����
	 * 
	 * @param another
	 *            �Է�
	 * @return ��ֵ����
	 */
	public AssignCapability isAssignableFrom(DataType source);

	/**
	 * ����Ƿ���ͬ��������,���������ȼ����ߵ�����
	 */
	@Deprecated
	public DataType calcPrecedence(DataType target);

	/**
	 * �Ƿ��Ǵ����
	 */
	public boolean isLOB();

	/**
	 * �Ƿ�����������
	 */
	public boolean isNumber();

	/**
	 * �Ƿ����ַ�������
	 */
	public boolean isString();

	/**
	 * �Ƿ����ֽ���������
	 */
	public boolean isBytes();

	/**
	 * �Ƿ�����������
	 */
	public boolean isArray();

	/**
	 * ���ݿ��ֶζ���Ŀɷ�ʹ�õ�ǰ��������
	 */
	public boolean isDBType();

	/**
	 * ���ص�ǰ���͵���������
	 */
	public ArrayDataType arrayOf();
}
