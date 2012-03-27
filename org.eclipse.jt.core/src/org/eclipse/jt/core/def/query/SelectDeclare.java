package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.exp.ConditionalExpression;
import org.eclipse.jt.core.def.exp.RelationColumnRefExpr;
import org.eclipse.jt.core.def.exp.ValueExpression;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;

/**
 * �����ѯ�ṹ����
 * 
 * @see org.eclipse.jt.core.def.query.SelectDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface SelectDeclare extends SelectDefine, RelationDeclare,
		RelationRefBuildable, RelationRefDomainDeclare {

	@Deprecated
	public QuRelationRefDeclare findReference(String name);

	@Deprecated
	public QuRelationRefDeclare getReference(String name);

	public QuRelationRefDeclare findRelationRef(String name);

	public QuRelationRefDeclare getRelationRef(String name);

	public QuRelationRefDeclare getRootReference();

	public Iterable<? extends QuRelationRefDeclare> getReferences();

	/**
	 * ��ѯ�ṹ��ӹ�ϵ����
	 * 
	 * @param table
	 *            ����
	 * @return
	 */
	public QuTableRefDeclare newReference(TableDefine table);

	/**
	 * ��ѯ�ṹ��ӹ�ϵ����
	 * 
	 * @param table
	 *            ����
	 * @param name
	 *            ���ñ���.�ظ��򳬳����ݿ�����,�ᱻ������.�������Ʋ�����25���ַ�.
	 * @return
	 */
	public QuTableRefDeclare newReference(TableDefine table, String name);

	/**
	 * ��ѯ�ṹ��ӹ�ϵ����
	 * 
	 * @param table
	 *            ��������
	 * @return
	 */
	public QuTableRefDeclare newReference(TableDeclarator table);

	/**
	 * ��ѯ�ṹ��ӹ�ϵ����
	 * 
	 * @param table
	 *            ��������
	 * @param name
	 *            ���ñ���.�ظ��򳬳����ݿ�����,�ᱻ������.�������Ʋ�����25���ַ�.
	 * @return
	 */
	public QuTableRefDeclare newReference(TableDeclarator table, String name);

	/**
	 * ��ѯ�ṹ��ӹ�ϵ����
	 * 
	 * @param query
	 *            ������ѯ����
	 * @return
	 */
	public QuQueryRefDeclare newReference(DerivedQueryDefine query);

	/**
	 * ��ѯ�ṹ��ӹ�ϵ����
	 * 
	 * @param query
	 *            ������ѯ����
	 * @param name
	 *            ���ñ���.�ظ��򳬳����ݿ�����,�ᱻ������.�������Ʋ�����25���ַ�.
	 * @return
	 */
	public QuQueryRefDeclare newReference(DerivedQueryDefine query, String name);

	/**
	 * �����ֶ����ñ��ʽ
	 * 
	 * <p>
	 * ʹ�õ�һ��ָ���ϵԪ���������,�������ϵ�����ñ��ʽ
	 * <p>
	 * �˷��������Ͻ�,������ʹ��<code>QuRelationRefDeclare</code>��<code>expOf</code>
	 * �����������ֶ����ñ��ʽ
	 */
	public RelationColumnRefExpr expOf(RelationColumnDefine column);

	/**
	 * ���ò�ѯ�й�������
	 */
	public void setCondition(ConditionalExpression where);

	/**
	 * ���÷������
	 * 
	 * @see org.eclipse.jt.core.def.query.GroupByType
	 * 
	 * @param type
	 *            �������
	 */
	public void setGroupByType(GroupByType type);

	public ModifiableContainer<? extends GroupByItemDeclare> getGroupBys();

	/**
	 * ���ӷ�����
	 * 
	 * @param value
	 *            �������ݵı��ʽ
	 * @return
	 */
	public GroupByItemDeclare newGroupBy(ValueExpression value);

	/**
	 * ���ӷ�����
	 * 
	 * @param field
	 *            �������ݵ��ֶ�
	 * @return
	 */
	public GroupByItemDeclare newGroupBy(RelationColumnDefine column);

	/**
	 * ���÷����������
	 */
	public void setHaving(ConditionalExpression having);

	/**
	 * �����Ƿ��ų��ظ���
	 */
	public void setDistinct(boolean distinct);

	public SelectColumnDeclare findColumn(String columnName);

	public SelectColumnDeclare getColumn(String columnName);

	/**
	 * ���������б�
	 * 
	 * @return �����ֶ��б�
	 */
	public ModifiableNamedElementContainer<? extends SelectColumnDeclare> getColumns();

	/**
	 * ���Ӳ�ѯ�����
	 * 
	 * @param field
	 *            ��ѯ��Ϊָ���߼�����
	 * @return
	 */
	public SelectColumnDeclare newColumn(RelationColumnDefine field);

	/**
	 * ���Ӳ�ѯ�����
	 * 
	 * @param field
	 *            ����ֶ�
	 * @param alias
	 *            ����.�ظ��򳬳����ݿ�����,�ᱻ������.�������Ʋ�����25���ַ�.
	 * @return
	 */
	public SelectColumnDeclare newColumn(RelationColumnDefine field,
			String alias);

	/**
	 * ���Ӳ�ѯ�����
	 * 
	 * @param expression
	 *            ����б��ʽ
	 * @return
	 */
	public SelectColumnDeclare newColumn(ValueExpression expression);

	/**
	 * ���Ӳ�ѯ�����
	 * 
	 * @param expression
	 *            ����б��ʽ
	 * @param alias
	 *            ����.�ظ��򳬳����ݿ�����,�ᱻ������.�������Ʋ�����25���ַ�.
	 * @return
	 */
	public SelectColumnDeclare newColumn(ValueExpression expression,
			String alias);

	public ModifiableContainer<? extends SetOperateDeclare> getSetOperates();

	/**
	 * ������,�������ظ���
	 * 
	 * <p>
	 * ���������ɵ�ǰ��ѯ�ṹ��<code>newDerivedQuery()</code>��������.
	 * 
	 * @param query
	 */
	public void union(DerivedQueryDefine query);

	/**
	 * ������,�����ظ���
	 * 
	 * <p>
	 * ���������ɵ�ǰ��ѯ�ṹ��<code>newDerivedQuery()</code>��������.
	 * 
	 * @param query
	 */
	public void unionAll(DerivedQueryDefine query);

	/**
	 * �����Ӳ�ѯ����
	 * 
	 * @return
	 */
	public SubQueryDeclare newSubQuery();

	/**
	 * ���쵼����ѯ����,���ڹ���from�Ӳ�ѯ�Լ�union
	 * 
	 * @return
	 */
	public DerivedQueryDeclare newDerivedQuery();

	/**
	 * ����ȫ��������,����ִ���κβ���,���ؿ�
	 * 
	 * @deprecated
	 */
	@Deprecated
	public ModifiableContainer<? extends OrderByItemDeclare> getOrderBys();

	/**
	 * ����ȫ��������,����ִ���κβ���,���ؿ�
	 * 
	 * @deprecated
	 */
	@Deprecated
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column);

	/**
	 * ����ȫ��������,����ִ���κβ���,���ؿ�
	 * 
	 * @deprecated
	 */
	@Deprecated
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column,
			boolean isDesc);

	/**
	 * ����ȫ��������,����ִ���κβ���,���ؿ�
	 * 
	 * @deprecated
	 */
	@Deprecated
	public OrderByItemDeclare newOrderBy(ValueExpression value);

	/**
	 * ����ȫ��������,����ִ���κβ���,���ؿ�
	 * 
	 * @deprecated
	 */
	@Deprecated
	public OrderByItemDeclare newOrderBy(ValueExpression value, boolean isDesc);
}
