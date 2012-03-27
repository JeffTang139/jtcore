package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableRelationDefine;

public interface RelationJoinable {

	/**
	 * ��ǰ��ϵ�����������ӹ�ϵ����
	 * 
	 * <p>
	 * ������������������ŵ�����,ʼ�մ�����ߵĹ�ϵ���ô����������ö���.
	 * 
	 * @param table
	 *            ���ӵ�Ŀ�����
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDefine table);

	/**
	 * ��ǰ��ϵ�����������ӹ�ϵ����
	 * 
	 * <p>
	 * ������������������ŵ�����,ʼ�մ�����ߵĹ�ϵ���ô����������ö���.
	 * 
	 * @param table
	 *            ���ӵ�Ŀ�����
	 * @param alias
	 *            ���ӹ�ϵ��������
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDefine table, String alias);

	/**
	 * ��ǰ��ϵ�����������ӹ�ϵ����
	 * 
	 * <p>
	 * ������������������ŵ�����,ʼ�մ�����ߵĹ�ϵ���ô����������ö���.
	 * 
	 * @param table
	 *            ���ӵ�Ŀ���������
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDeclarator table);

	/**
	 * ��ǰ��ϵ�����������ӹ�ϵ����
	 * 
	 * <p>
	 * ������������������ŵ�����,ʼ�մ�����ߵĹ�ϵ���ô����������ö���.
	 * 
	 * @param table
	 *            ���ӵ�Ŀ���������
	 * @param alias
	 *            ���ӹ�ϵ��������
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDeclarator table,
			String alias);

	/**
	 * ��ǰ��ϵ�����������ӹ�ϵ����
	 * 
	 * <p>
	 * ������������������ŵ�����,ʼ�մ�����ߵĹ�ϵ���ô����������ö���.
	 * 
	 * @param sample
	 *            ʹ��ָ�����ϵ�������������Ӽ���������
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableRelationDefine sample);

	/**
	 * ��ǰ��ϵ�����������ӹ�ϵ����
	 * 
	 * <p>
	 * ������������������ŵ�����,ʼ�մ�����ߵĹ�ϵ���ô����������ö���.
	 * 
	 * @param sample
	 *            ʹ��ָ�����ϵ�������������Ӽ���������
	 * @param alias
	 *            ���ӹ�ϵ��������
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableRelationDefine sample,
			String alias);

	/**
	 * ��ǰ��ϵ�����������ӹ�ϵ����
	 * 
	 * <p>
	 * ������������������ŵ�����,ʼ�մ�����ߵĹ�ϵ���ô����������ö���.
	 * 
	 * @param query
	 * @return
	 */
	public JoinedQueryReferenceDeclare newJoin(DerivedQueryDefine query);

	/**
	 * ��ǰ��ϵ�����������ӹ�ϵ����
	 * 
	 * <p>
	 * ������������������ŵ�����,ʼ�մ�����ߵĹ�ϵ���ô����������ö���.
	 * 
	 * @param query
	 * @param name
	 * @return
	 */
	public JoinedQueryReferenceDeclare newJoin(DerivedQueryDefine query,
			String name);

}
