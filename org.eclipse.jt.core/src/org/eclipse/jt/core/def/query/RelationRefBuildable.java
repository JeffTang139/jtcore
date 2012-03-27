package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableReferenceDeclare;

/**
 * ���Թ��������ӵĹ�ϵ����
 * 
 * @author Jeff Tang
 * 
 */
public interface RelationRefBuildable {

	/**
	 * ���������
	 * 
	 * @param table
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDefine table);

	/**
	 * ���������
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDefine table, String name);

	/**
	 * ���������
	 * 
	 * @param table
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDeclarator table);

	/**
	 * ���������
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public TableReferenceDeclare newReference(TableDeclarator table, String name);

	/**
	 * �����ѯ����
	 * 
	 * @param query
	 * @return
	 */
	public QueryReferenceDeclare newReference(DerivedQueryDefine query);

	/**
	 * �����ѯ����
	 * 
	 * @param query
	 * @param name
	 * @return
	 */
	public QueryReferenceDeclare newReference(DerivedQueryDefine query,
			String name);
}
