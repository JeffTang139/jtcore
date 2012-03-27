package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.MetaElement;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.def.query.RelationDefine;
import org.eclipse.jt.core.impl.TableDefineImpl;
import org.eclipse.jt.core.type.TupleType;

/**
 * �߼�����
 * 
 * @author Jeff Tang
 */
public interface TableDefine extends TablePartitionDefine, MetaElement,
		TupleType, RelationDefine {

	/**
	 * DUMMY��,������Oracle�е�dual��.
	 */
	public static final TableDefine DUMMY = TableDefineImpl.DUMMY;

	/**
	 * �Ƿ���ԭ����
	 * 
	 * <p>
	 * ԭ�����ʾͨ��TableDeclarator����̻��ľ�̬�߼���
	 * 
	 * @return
	 */
	public boolean isOriginal();

	/**
	 * ���ر�����б�ʶ�е��ֶζ���
	 * 
	 * @return
	 */
	public TableFieldDefine f_RECID();

	/**
	 * ���ر�����а汾�е��ֶζ���
	 * 
	 * @return
	 */
	public TableFieldDefine f_RECVER();

	/**
	 * ��ȡ��������б�
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends DBTableDefine> getDBTables();

	/**
	 * ��ȡ���������
	 * 
	 * @return
	 */
	public DBTableDefine getPrimaryDBTable();

	public TableFieldDefine findColumn(String columnName);

	public TableFieldDefine getColumn(String columnName);

	/**
	 * ��ȡ�ֶζ����б�
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends TableFieldDefine> getFields();

	/**
	 * ������������б�
	 * 
	 * @return �������������б�
	 */
	public NamedElementContainer<? extends IndexDefine> getIndexes();

	/**
	 * ��ñ��ϵ�����б�
	 * 
	 * @return ���ر��ϵ�����б�
	 */
	public NamedElementContainer<? extends TableRelationDefine> getRelations();

	/**
	 * ��ü��ζ����б�
	 * 
	 * @return ���ؼ��ζ����б�
	 */
	public NamedElementContainer<? extends HierarchyDefine> getHierarchies();

	/**
	 * ��ñ�ķ���
	 */
	public String getCategory();
}
