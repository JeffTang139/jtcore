package org.eclipse.jt.core.def.obja;

import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.exception.NullArgumentException;
import org.eclipse.jt.core.type.ObjectDataType;
import org.eclipse.jt.core.type.TupleType;

/**
 * �ṹ����
 * 
 * @author Jeff Tang
 * 
 */
public interface StructDefine extends NamedDefine, ObjectDataType, TupleType {
	/**
	 * ����ֶζ����б�
	 * 
	 * @return �����ֶζ����б�
	 */
	public NamedElementContainer<? extends StructFieldDefine> getFields();

	/**
	 * ����ת��
	 * 
	 * @param obj
	 *            ��Ҫ��ת���Ķ��󣬲���Ϊ��
	 * @return ����null��ʾת��ʧ��
	 * @exception NullArgumentException
	 *                objΪnull
	 */
	public Object tryConvert(Object convertFrom) throws NullArgumentException;

	/**
	 * ����Ƿ��ǽṹ�����Ӧ�Ķ���ʵ��
	 */
	public boolean isInstance(Object obj);
}
