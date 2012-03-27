package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.ModifiableContainer;
import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.exp.ValueExpression;

/**
 * ��ѯ��䶨��
 * 
 * <p>
 * ����һ����ִ�еĲ�ѯ���ṹ
 * 
 * @see org.eclipse.jt.core.def.query.QueryStatementDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface QueryStatementDeclare extends QueryStatementDefine,
		SelectDeclare, StatementDeclare, WithableDeclare {

	/**
	 * ʹ��������ѯ�ṹ,���쵱ǰ��ѯ���ĵ�����ѯ����
	 * 
	 * @param sample
	 * @return
	 */
	public DerivedQueryDeclare newDerivedQuery(SelectDefine sample);

	public ModifiableNamedElementContainer<? extends QueryColumnDeclare> getColumns();

	public QueryColumnDeclare newColumn(RelationColumnDefine field);

	public QueryColumnDeclare newColumn(RelationColumnDefine field, String alias);

	public QueryColumnDeclare newColumn(ValueExpression expr, String alias);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @return �����������б�
	 */
	public ModifiableContainer<? extends OrderByItemDeclare> getOrderBys();

	/**
	 * �����������
	 * 
	 * <p>
	 * ���������union֮�����
	 * 
	 * @param field
	 *            �������ݵĹ�ϵ��
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column);

	/**
	 * �����������
	 * 
	 * <p>
	 * ���������union֮�����
	 * 
	 * @param field
	 *            �������ݵĹ�ϵ��
	 * @param isDesc
	 *            �Ƿ���
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(RelationColumnDefine column,
			boolean isDesc);

	/**
	 * �����������
	 * 
	 * <p>
	 * ���������union֮�����
	 * 
	 * @param value
	 *            �������ݵı��ʽ
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(ValueExpression value);

	/**
	 * �����������
	 * 
	 * <p>
	 * ���������union֮�����
	 * 
	 * @param value
	 *            �������ݵı��ʽ
	 * @param isDesc
	 *            �Ƿ���
	 * @return
	 */
	public OrderByItemDeclare newOrderBy(ValueExpression value, boolean isDesc);

}
