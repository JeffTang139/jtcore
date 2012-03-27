package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.def.exp.ConditionalExpression;

/**
 * ��ѯ�ṹ����
 * 
 * <p>
 * ��һ��select���Ľṹ.
 * 
 * <p>
 * ���ܶ���orderby�Ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface SelectDefine extends RelationDefine, RelationRefDomainDefine {

	/**
	 * �������Ʋ��ҵ�ǰ��ѯ�ṹ����Ĺ�ϵ����
	 * 
	 * <p>
	 * �������򷵻�null
	 * 
	 * @param name
	 *            ��ϵ��������
	 * @return
	 * @deprecated ʹ��findRelationRef
	 */
	@Deprecated
	public QuRelationRefDefine findReference(String name);

	public QuRelationRefDefine findRelationRef(String name);

	/**
	 * �������ƻ�ȡ��ǰ��ѯ�ṹ����Ĺ�ϵ����
	 * 
	 * <p>
	 * ���������׳��쳣
	 * 
	 * @param name
	 *            ��ϵ��������
	 * @return
	 * @deprecated ʹ��getRelationRef
	 */
	@Deprecated
	public QuRelationRefDefine getReference(String name);

	public QuRelationRefDefine getRelationRef(String name);

	/**
	 * ���ص�ǰ��ѯ�ṹ�ĵ�һ����ϵ���ö���
	 * 
	 * @return
	 */
	public QuRelationRefDefine getRootReference();

	/**
	 * ���ص�ǰ��ѯ�ṹ��������й�ϵ���õ�<strong>�������</strong>�Ŀɵ����ӿ�
	 * 
	 * @return
	 */
	public Iterable<? extends QuRelationRefDefine> getReferences();

	/**
	 * ����й�������
	 * 
	 * <p>
	 * ��where�Ӿ䶨������
	 * 
	 * @return �����й�������,δ�����򷵻�null
	 */
	public ConditionalExpression getCondition();

	/**
	 * ��ȡ���������
	 * 
	 * @return δ�����򷵻�null
	 */
	public Container<? extends GroupByItemDefine> getGroupBys();

	/**
	 * ��ȡ��������
	 * 
	 * @see org.eclipse.jt.core.def.query.GroupByType
	 * 
	 * @return Ĭ��ΪGroupByType.DEFAULT
	 */
	public GroupByType getGroupByType();

	/**
	 * ��ȡ�����������
	 * 
	 * @return δ�����򷵻�null
	 */
	public ConditionalExpression getHaving();

	public SelectColumnDefine findColumn(String columnName);

	public SelectColumnDefine getColumn(String columnName);

	/**
	 * ��ȡ�Ƿ��ų��ظ���
	 * 
	 * <p>
	 * Ĭ��Ϊfalse,�����ų��ظ���
	 */
	public boolean getDistinct();

	/**
	 * �������ֶ��б�
	 * 
	 * @return ���᷵��null
	 */
	public NamedElementContainer<? extends SelectColumnDefine> getColumns();

	/**
	 * ���ؼ������㶨��
	 * 
	 * @return δ�����򷵻�null
	 */
	public Container<? extends SetOperateDefine> getSetOperates();

	/**
	 * ����ȫ��������,����ִ���κβ���,���ؿ�
	 * 
	 * @deprecated
	 */
	@Deprecated
	public Container<? extends OrderByItemDefine> getOrderBys();

}
