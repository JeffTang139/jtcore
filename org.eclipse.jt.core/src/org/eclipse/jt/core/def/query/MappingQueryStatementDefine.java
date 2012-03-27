package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.obja.StructDefine;

/**
 * Ӱ���ѯ���壬���Զ���ģ�������ݿ�����ݵ�Ӱ���ϵ�Ĳ�ѯ
 * 
 * @author Jeff Tang
 * 
 */
public interface MappingQueryStatementDefine extends QueryStatementDefine {

	/**
	 * ����ֶζ������ڵ�ģ�Ͷ���
	 * 
	 * @return ����ģ�Ͷ���
	 */
	public StructDefine getMappingTarget();

	/**
	 * �Ƿ��Զ���ʵ���ֶ�
	 * 
	 * @return
	 */
	public boolean isAutoBind();
}
