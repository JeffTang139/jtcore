package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.query.RelationRefDefine;

/**
 * �����ýӿ�
 * 
 * <p>
 * �̳�����ϵ���ö���,��ʾĿ������Ϊ����Ĺ�ϵ����.
 * 
 * @see org.eclipse.jt.core.def.query.RelationRefDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface TableReferenceDefine extends RelationRefDefine {

	/**
	 * ��ȡĿ���߼���
	 */
	public TableDefine getTarget();

}