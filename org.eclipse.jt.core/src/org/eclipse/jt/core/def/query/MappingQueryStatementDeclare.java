package org.eclipse.jt.core.def.query;

/**
 * Ӱ���ѯ���壬���Զ���ģ�������ݿ�����ݵ�Ӱ���ϵ�Ĳ�ѯ
 * 
 * @author Jeff Tang
 * 
 */
public interface MappingQueryStatementDeclare extends
		MappingQueryStatementDefine, QueryStatementDeclare {

	/**
	 * �����Ƿ��Զ���ʵ���ֶ�
	 * 
	 * <p>
	 * ������Ϊ�Զ���ʱ,ϵͳ�������δ�󶨵�ʵ���ֶ�,������󶨵���ͬ���Ƶı��ֶ���
	 * 
	 * @param isAutoBind
	 */
	public void setAutoBind(boolean isAutoBind);

}
