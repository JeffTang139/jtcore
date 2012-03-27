package org.eclipse.jt.core.def.query;

/**
 * ��ϵ������
 * 
 * <p>
 * ��ʾ��ǰ����ӵ�ж��ϵ����
 * 
 * @author Jeff Tang
 * 
 */
public interface RelationRefDomainDefine {

	/**
	 * ���ص�ǰ��������������
	 * 
	 * <p>
	 * �����������һ�����ǽṹ�ϵ�ֱ���ϼ���
	 * 
	 * @return
	 */
	RelationRefDomainDefine getDomain();

	/**
	 * �ڵ�ǰ���ڲ���ָ�����ƵĹ�ϵ����
	 * 
	 * @param name
	 * @return
	 */
	RelationRefDefine findRelationRef(String name);

	/**
	 * ��ȡ��ǰ����ָ�����ƵĹ�ϵ����
	 * 
	 * @param name
	 * @return
	 */
	RelationRefDefine getRelationRef(String name);

	/**
	 * �ڵ�ǰ����Ч���������ڲ���ָ�����ƵĹ�ϵ����
	 * 
	 * @param name
	 * @return
	 */
	RelationRefDefine findRelationRefRecursively(String name);

	/**
	 * �����ڵ�ǰ����Ч����������ָ�����ƵĹ�ϵ����
	 * 
	 * @param name
	 * @return
	 */
	RelationRefDefine getRelationRefRecursively(String name);
}
