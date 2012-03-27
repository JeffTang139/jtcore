package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableRelationDefine;

/**
 * ��ѯ������ʹ�õĹ�ϵ���ö���
 * 
 * @see org.eclipse.jt.core.def.query.QuRelationRefDefine
 * 
 * @author Jeff Tang
 */
public interface QuRelationRefDeclare extends QuRelationRefDefine,
		RelationRefDeclare {

	/**
	 * ���ñ������Ƿ�֧�ֽ�����ĸ���
	 * 
	 */
	public void setForUpdate(boolean forUpdate);

	@Deprecated
	public QuTableRefDeclare castAsTableRef();

	@Deprecated
	public QuQueryRefDeclare castAsQueryRef();

	public QuJoinedTableRefDeclare newJoin(TableDefine target);

	public QuJoinedTableRefDeclare newJoin(TableDefine target, String name);

	public QuJoinedTableRefDeclare newJoin(TableDeclarator target);

	public QuJoinedTableRefDeclare newJoin(TableDeclarator target, String name);

	public QuJoinedTableRefDeclare newJoin(TableRelationDefine relation);

	public QuJoinedTableRefDeclare newJoin(TableRelationDefine sample,
			String name);

	public QuJoinedQueryRefDeclare newJoin(DerivedQueryDefine query);

	public QuJoinedQueryRefDeclare newJoin(DerivedQueryDefine query, String name);

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
}
