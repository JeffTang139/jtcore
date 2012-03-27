package org.eclipse.jt.core.spi.monitor;

import org.eclipse.jt.core.def.NamedDefine;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;


/**
 * ���ܼ��ָ��<br>
 * ʹ�þ���: ���ȫ����Чָ�ꡣ
 * 
 * <pre>
 * context.getList(PerformanceIndexDefine.class);
 * </pre>
 * 
 * @author Jeff Tang
 * 
 */
public interface PerformanceIndexDefine extends NamedDefine {
	/**
	 * ���ID
	 */
	public GUID getID();

	/**
	 * ��ȡ��Ӧ��ֵ����<br>
	 * Ŀǰ��֧�֣�
	 * <ul>
	 * <li>TypeFactory.BOOLEAN
	 * <li>TypeFactory.LONG
	 * <li>TypeFactory.DOUBLE
	 * <li>���ֶ�������
	 * </ul>
	 * 
	 */
	public DataType getDataType();

	/**
	 * ָ���ֵ�Ƿ�������
	 */
	public boolean isSequence();

	/**
	 * �Ƿ��ǻỰ������ָ�꣬����Ϊȫ������ָ��
	 */
	public boolean isUnderSession();

	/**
	 * �������
	 * 
	 */
	public interface CommandDefine extends NamedDefine {

	}

	/**
	 * ��ȡָ�����������
	 */
	public NamedElementContainer<? extends CommandDefine> getCommands();
}
