package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.ModifiableNamedElementContainer;
import org.eclipse.jt.core.def.exp.TableFieldRefExpr;
import org.eclipse.jt.core.def.query.RelationDeclare;
import org.eclipse.jt.core.type.DataType;

/**
 * �߼�����
 * 
 * @see org.eclipse.jt.core.def.table.TableDefine
 * 
 * @author Jeff Tang
 * 
 */
public interface TableDeclare extends TableDefine, TablePartitionDeclare,
		RelationDeclare {

	public ModifiableNamedElementContainer<? extends DBTableDeclare> getDBTables();

	public DBTableDeclare getPrimaryDBTable();

	/**
	 * �����������
	 * 
	 * <p>
	 * �߼���������32�������
	 * 
	 * @param name
	 *            ����������ƣ�ͬ���ݿ��д���������
	 * @return
	 */
	public DBTableDeclare newDBTable(String name);

	public TableFieldDeclare findColumn(String columnName);

	public TableFieldDeclare getColumn(String columnName);

	public ModifiableNamedElementContainer<? extends TableFieldDeclare> getFields();

	/**
	 * �����߼������ֶζ���
	 * 
	 * @param name
	 *            �ֶζ�������
	 * @param type
	 *            �ֶζ�������
	 * @return
	 * @see org.eclipse.jt.core.type.TypeFactory;
	 */
	public TableFieldDeclare newPrimaryField(String name, DataType type);

	/**
	 * �����ֶζ���
	 * 
	 * 
	 * @param name
	 *            �ֶζ�������
	 * @param type
	 *            �ֶζ�������
	 * @return
	 * @see org.eclipse.jt.core.type.TypeFactory;
	 */
	public TableFieldDeclare newField(String name, DataType type);

	/**
	 * �����ֶζ��壬�洢��ָ�����������
	 * 
	 * @param name
	 *            �ֶζ�������
	 * @param type
	 *            �ֶζ�������
	 * @param dbTable
	 *            �洢������������ڵ�ǰ�߼���
	 * @return
	 * @see org.eclipse.jt.core.type.TypeFactory;
	 */
	public TableFieldDeclare newField(String name, DataType type,
			DBTableDefine dbTable);

	public ModifiableNamedElementContainer<? extends IndexDeclare> getIndexes();

	/**
	 * �����������������������
	 * 
	 * @param name
	 *            �������ƣ������ݿ�������������������һ��
	 * @return
	 */
	public IndexDeclare newIndex(String name);

	/**
	 * ������������
	 * 
	 * @param name
	 *            �������ƣ������ݿ�������������������һ��
	 * @param field
	 *            �����ֶ�
	 * @return
	 */
	public IndexDeclare newIndex(String name, TableFieldDefine field);

	/**
	 * ������������
	 * 
	 * @param name
	 *            �������ƣ������ݿ�������������������һ��
	 * @param field
	 *            �����ֶ�
	 * @param others
	 *            ���������ֶ�
	 * @return
	 */
	public IndexDeclare newIndex(String name, TableFieldDefine field,
			TableFieldDefine... others);

	public ModifiableNamedElementContainer<? extends TableRelationDeclare> getRelations();

	/**
	 * ���ӱ��ϵ����
	 * 
	 * @param name
	 *            ���ϵ����
	 * @param target
	 *            ���ϵ��Ŀ���
	 * @param type
	 *            ���ϵ����
	 * @return
	 */
	public TableRelationDeclare newRelation(String name, TableDefine target,
			TableRelationType type);

	/**
	 * ���ӱ��ϵ����
	 * 
	 * @param name
	 *            ���ϵ����
	 * @param target
	 *            ���ϵ��Ŀ���
	 * @param type
	 *            ���ϵ����
	 * @return
	 */
	public TableRelationDeclare newRelation(String name,
			TableDeclarator target, TableRelationType type);

	/**
	 * ���ӵ�ֵ���ϵ����
	 * 
	 * @param name
	 *            ���ϵ����
	 * @param selfField
	 *            ��ֵ�����ڱ�����ֶ�
	 * @param target
	 *            ���ϵ��Ŀ���
	 * @param targetField
	 *            ��ֵ������Ŀ�����ֶ�
	 * @param type
	 *            ���ϵ����
	 * @return
	 */
	public TableRelationDeclare newRelation(String name,
			TableFieldDefine selfField, TableDefine target,
			TableFieldDefine targetField, TableRelationType type);

	/**
	 * ���ӵ�ֵ���ϵ����
	 * 
	 * @param name
	 *            ���ϵ����
	 * @param selfField
	 *            ��ֵ�����ڱ�����ֶ�
	 * @param target
	 *            ���ϵ��Ŀ���
	 * @param targetField
	 *            ��ֵ������Ŀ�����ֶ�
	 * @param type
	 *            ���ϵ����
	 * @return
	 */
	public TableRelationDeclare newRelation(String name,
			TableFieldDefine selfField, TableDeclarator target,
			TableFieldDefine targetField, TableRelationType type);

	public ModifiableNamedElementContainer<? extends HierarchyDeclare> getHierarchies();

	/**
	 * �������ζ���
	 * 
	 * <p>
	 * �߼���������32�����ζ���
	 * 
	 * @param name
	 *            ���ζ�������
	 * @param maxlevel
	 *            ���ε�������
	 */
	public HierarchyDeclare newHierarchy(String name, int maxlevel);

	/**
	 * ����Ŀ¼
	 * 
	 * @param category
	 */
	public void setCategory(String category);

	/**
	 * �����ֶ����ñ��ʽ
	 * 
	 * <p>
	 * �ñ��ʽֻ���ڱ��ϵ��������ʹ��,�������κ���ɾ�Ĳ������ʹ�á�
	 * 
	 * @param field
	 *            ���ڵ�ǰ����ֶζ���
	 * @return
	 */
	public TableFieldRefExpr expOf(TableFieldDefine field);
}
