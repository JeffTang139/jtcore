package org.eclipse.jt.core.def.table;

import org.eclipse.jt.core.def.MetaElement;
import org.eclipse.jt.core.def.NamedElementContainer;
import org.eclipse.jt.core.def.query.RelationDefine;
import org.eclipse.jt.core.impl.TableDefineImpl;
import org.eclipse.jt.core.type.TupleType;

/**
 * 逻辑表定义
 * 
 * @author Jeff Tang
 */
public interface TableDefine extends TablePartitionDefine, MetaElement,
		TupleType, RelationDefine {

	/**
	 * DUMMY表,类似于Oracle中的dual表.
	 */
	public static final TableDefine DUMMY = TableDefineImpl.DUMMY;

	/**
	 * 是否是原生表
	 * 
	 * <p>
	 * 原生表表示通过TableDeclarator代码固化的静态逻辑表
	 * 
	 * @return
	 */
	public boolean isOriginal();

	/**
	 * 返回表定义的行标识列的字段定义
	 * 
	 * @return
	 */
	public TableFieldDefine f_RECID();

	/**
	 * 返回表定义的行版本列的字段定义
	 * 
	 * @return
	 */
	public TableFieldDefine f_RECVER();

	/**
	 * 获取物理表定义列表
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends DBTableDefine> getDBTables();

	/**
	 * 获取主物理表定义
	 * 
	 * @return
	 */
	public DBTableDefine getPrimaryDBTable();

	public TableFieldDefine findColumn(String columnName);

	public TableFieldDefine getColumn(String columnName);

	/**
	 * 获取字段定义列表
	 * 
	 * @return
	 */
	public NamedElementContainer<? extends TableFieldDefine> getFields();

	/**
	 * 获得索引定义列表
	 * 
	 * @return 返回索引定义列表
	 */
	public NamedElementContainer<? extends IndexDefine> getIndexes();

	/**
	 * 获得表关系定义列表
	 * 
	 * @return 返回表关系定义列表
	 */
	public NamedElementContainer<? extends TableRelationDefine> getRelations();

	/**
	 * 获得级次定义列表
	 * 
	 * @return 返回级次定义列表
	 */
	public NamedElementContainer<? extends HierarchyDefine> getHierarchies();

	/**
	 * 获得表的分类
	 */
	public String getCategory();
}
