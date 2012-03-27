package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.type.DataTypable;

/**
 * ��ѯ���ѡ���ж���
 * 
 * <p>
 * ��ʾһ�������ѯ����������
 * 
 * @author Jeff Tang
 * 
 */
public interface SelectColumnDefine extends RelationColumnDefine, DataTypable {

	/**
	 * ��ȡ�����Ĳ�ѯ����
	 * 
	 * @return ��ѯ����
	 */
	public SelectDefine getOwner();

	/**
	 * �����ж���ı��ʽ
	 * 
	 * @return �����ж���ı��ʽ
	 */
	public ValueExpression getExpression();
}
