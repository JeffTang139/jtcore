package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.obja.StructFieldDefine;

/**
 * ��ѯ��䶨�������ж���
 * 
 * @author Jeff Tang
 */
public interface QueryColumnDefine extends SelectColumnDefine {

	/**
	 * ��ȡ�����Ĳ�ѯ��䶨��
	 * 
	 * @return
	 */
	public QueryStatementDefine getOwner();

	/**
	 * ��ȡӳ�䵽���ֶ�,�������Զ�ȡMO,ORMEntity,RO�Ķ�Ӧֵ
	 * 
	 * @return ����ӳ�䵽���ֶ�
	 */
	public StructFieldDefine getMapingField();
}
