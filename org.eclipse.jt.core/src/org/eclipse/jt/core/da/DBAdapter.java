package org.eclipse.jt.core.da;

import java.util.List;

import org.eclipse.jt.core.LifeHandle;
import org.eclipse.jt.core.def.obja.StructDefine;
import org.eclipse.jt.core.def.query.DeleteStatementDeclare;
import org.eclipse.jt.core.def.query.InsertStatementDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDeclare;
import org.eclipse.jt.core.def.query.MappingQueryStatementDefine;
import org.eclipse.jt.core.def.query.ModifyStatementDeclarator;
import org.eclipse.jt.core.def.query.ModifyStatementDefine;
import org.eclipse.jt.core.def.query.ORMDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclarator;
import org.eclipse.jt.core.def.query.QueryStatementDeclare;
import org.eclipse.jt.core.def.query.QueryStatementDefine;
import org.eclipse.jt.core.def.query.StatementDeclarator;
import org.eclipse.jt.core.def.query.StatementDeclare;
import org.eclipse.jt.core.def.query.StatementDefine;
import org.eclipse.jt.core.def.query.StoredProcedureDeclarator;
import org.eclipse.jt.core.def.query.StoredProcedureDefine;
import org.eclipse.jt.core.def.query.UpdateStatementDeclare;
import org.eclipse.jt.core.def.table.EntityTableDeclarator;
import org.eclipse.jt.core.def.table.HierarchyDefine;
import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.type.GUID;


/**
 * 数据库适配器<br>
 * 
 * 
 * @author Jeff Tang
 * 
 */
public interface DBAdapter extends LifeHandle {

	/**
	 * 获取新的递增的全局不重复的RECID，用于表记录的插入
	 */
	public GUID newRECID();

	/**
	 * 获得新的递增的本数据库不重复的行版本号
	 */
	public long newRECVER();

	/**
	 * 创建查询定义
	 */
	public QueryStatementDeclare newQueryStatement();

	/**
	 * 以指定查询定义为样本,复制返回一个新的查询定义
	 * 
	 * @param sample
	 * @return
	 */
	public QueryStatementDeclare newQueryStatement(QueryStatementDefine sample);

	/**
	 * 创建插入语句定义
	 * 
	 * @param table
	 * @return
	 */
	public InsertStatementDeclare newInsertStatement(TableDefine table);

	/**
	 * 创建插入语句定义
	 * 
	 * @param table
	 * @return
	 */
	public InsertStatementDeclare newInsertStatement(TableDeclarator table);

	/**
	 * 创建删除语句定义
	 * 
	 * @param table
	 * @return
	 */
	public DeleteStatementDeclare newDeleteStatement(TableDefine table);

	/**
	 * 创建删除语句定义
	 * 
	 * @param table
	 * @return
	 */
	public DeleteStatementDeclare newDeleteStatement(TableDeclarator table);

	/**
	 * 创建更新语句定义
	 * 
	 * @param table
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDefine table);

	/**
	 * 创建更新语句定义
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDefine table,
			String name);

	/**
	 * 创建更新语句定义
	 * 
	 * @param table
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDeclarator table);

	/**
	 * 创建更新语句定义
	 * 
	 * @param table
	 * @param name
	 * @return
	 */
	public UpdateStatementDeclare newUpdateStatement(TableDeclarator table,
			String name);

	/**
	 * 创建ORM查询语句
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			Class<?> entityClass);

	/**
	 * 创建ORM查询语句
	 * 
	 * @param entityClass
	 * @param name
	 * @return
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			Class<?> entityClass, String name);

	/**
	 * 创建ORM查询语句
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			EntityTableDeclarator<?> table);

	/**
	 * 创建ORM查询语句
	 */
	public MappingQueryStatementDeclare newMappingQueryStatement(
			StructDefine model);

	public MappingQueryStatementDeclare newMappingQueryStatement(
			StructDefine model, String name);

	/**
	 * 解析D&ASql
	 * 
	 * @param dnaSql
	 *            D&ASql文本，可以是String/StringBuilder/StringBuffer
	 * @return 解析后的语句对象
	 */
	public StatementDeclare parseStatement(CharSequence dnaSql);

	/**
	 * 解析D&ASql
	 * 
	 * @param dnaSql
	 *            D&ASql文本，可以是String/StringBuilder/StringBuffer
	 * @param clz
	 *            语句定义类型
	 * @return 解析后的语句对象
	 */
	public <TStatement extends StatementDeclare> TStatement parseStatement(
			CharSequence dnaSql, Class<TStatement> clz);

	/**
	 * 执行查询,装载记录集
	 * 
	 * @param query
	 *            查询定义
	 * @param argValues
	 *            参数值
	 * @return 记录集
	 */
	public RecordSet openQuery(QueryStatementDefine query, Object... argValues);

	/**
	 * 执行查询,装载记录集
	 * 
	 * @param query
	 *            查询声明
	 * @param argValues
	 *            参数值
	 * @return 记录集
	 */
	public RecordSet openQuery(QueryStatementDeclarator query,
			Object... argValues);

	/**
	 * 执行带行限定的查询,装载记录集
	 * 
	 * @param query
	 *            查询定义
	 * @param offset
	 *            从指定偏移量开始返回结果,从第1行开始返回则偏移量为0
	 * @param rowCount
	 *            总返回行数
	 * @param argValues
	 *            参数
	 * @return
	 */
	public RecordSet openQueryLimit(QueryStatementDefine query, long offset,
			long rowCount, Object... argValues);

	/**
	 * 执行带行限定的查询,装载记录集
	 * 
	 * @param query
	 *            查询声明
	 * @param offset
	 *            从指定偏移量开始返回结果,从第1行开始返回则偏移量为0
	 * @param rowCount
	 *            总返回行数
	 * @param argValues
	 *            参数
	 * @return
	 */
	public RecordSet openQueryLimit(QueryStatementDeclarator query,
			long offset, long rowCount, Object... argValues);

	/**
	 * 执行查询,使用指定动作遍历结果集
	 * 
	 * @param query
	 * @param action
	 * @param argValues
	 */
	public void iterateQuery(QueryStatementDefine query,
			RecordIterateAction action, Object... argValues);

	/**
	 * 执行查询,使用指定动作遍历结果集
	 * 
	 * @param query
	 * @param action
	 * @param argValues
	 */
	public void iterateQuery(QueryStatementDeclarator query,
			RecordIterateAction action, Object... argValues);

	/**
	 * 执行带行限定的查询,使用指定动作遍历结果集
	 * 
	 * @param query
	 * @param action
	 * @param offset
	 * @param rowCount
	 * @param argValues
	 */
	public void iterateQueryLimit(QueryStatementDefine query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues);

	/**
	 * 执行带行限定的查询,使用指定动作遍历结果集
	 * 
	 * @param query
	 * @param action
	 * @param offset
	 * @param rowCount
	 * @param argValues
	 */
	public void iterateQueryLimit(QueryStatementDeclarator query,
			RecordIterateAction action, long offset, long rowCount,
			Object... argValues);

	/**
	 * 执行查询,返回结果第一行第一列的值
	 * 
	 * @param query
	 *            查询定义
	 * @param argValues
	 *            参数
	 * @return
	 */
	public Object executeScalar(QueryStatementDefine query, Object... argValues);

	/**
	 * 执行查询,返回结果第一行第一列的值
	 * 
	 * @param query
	 *            查询声明
	 * @param argValues
	 *            参数
	 * @return
	 */
	public Object executeScalar(QueryStatementDeclarator query,
			Object... argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param query
	 * @param argValues
	 * @return
	 */
	public int rowCountOf(QueryStatementDefine query, Object... argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param query
	 * @param argValues
	 * @return
	 */
	public int rowCountOf(QueryStatementDeclarator query, Object... argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param query
	 * @param argValues
	 * @return
	 */
	public long rowCountOfL(QueryStatementDefine query, Object... argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param query
	 * @param argValues
	 * @return
	 */
	public long rowCountOfL(QueryStatementDeclarator query, Object... argValues);

	/**
	 * 执行数据库更新语句
	 * 
	 * @param statement
	 *            语句定义
	 * @param argValues
	 *            参数值
	 * @return 更新计数
	 */
	public int executeUpdate(ModifyStatementDefine statement,
			Object... argValues);

	/**
	 * 执行数据库更新语句
	 * 
	 * @param statement
	 * @param argValues
	 * @return
	 */
	public int executeUpdate(ModifyStatementDeclarator<?> statement,
			Object... argValues);

	public void executeUpdate(StoredProcedureDefine procedure,
			Object... argValues);

	public void executeUpdate(StoredProcedureDeclarator procedure,
			Object... argValues);

	/**
	 * 准备数据库语句定义
	 * 
	 * @param statement
	 *            数据库语句声明
	 * @return 数据库执行命令
	 */
	public DBCommand prepareStatement(StatementDefine statement);

	/**
	 * 准备数据库语句定义
	 * 
	 * @param statement
	 *            数据库语句定义
	 * @return 数据库执行命令
	 */
	public DBCommand prepareStatement(StatementDeclarator<?> statement);

	/**
	 * 根据DNASql准备数据库语句定义，用以访问数据库
	 * 
	 * @param DNASql
	 *            D&ASql，类型可以是String,StringBuilder,CharBuffer等
	 * @return 返回数据库访问对象
	 */
	public DBCommand prepareStatement(CharSequence dnaSql);

	/**
	 * 创建实体对象访问器
	 * 
	 * @param <TEntity>
	 *            实体类型
	 * @param orm
	 *            实体映射
	 * @return 返回访问器
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			ORMDeclarator<TEntity> orm);

	/**
	 * 创建实体对象访问器
	 * 
	 * @param <TEntity>
	 * @param entityClass
	 * @param mappingQuery
	 * @return
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			Class<TEntity> entityClass, MappingQueryStatementDefine mappingQuery);

	/**
	 * 创建实体对象访问器
	 * 
	 * @param mappingQuery
	 *            实体查询定义
	 * @return
	 */
	public ORMAccessor<Object> newORMAccessor(
			MappingQueryStatementDefine mappingQuery);

	/**
	 * 创建实体对象访问器
	 * 
	 * @param <TEntity>
	 * @param table
	 *            实体表声明器
	 * @return
	 */
	public <TEntity> ORMAccessor<TEntity> newORMAccessor(
			EntityTableDeclarator<TEntity> table);

	/**
	 * 更新节点在指定级次定义中的父节点
	 * 
	 * @param hierarchy
	 *            级次定义
	 * @param parent
	 *            父节点
	 * @param child
	 *            子节点
	 */
	public void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			GUID child);

	/**
	 * 更新节点在指定级次定义中的父节点
	 * 
	 * @param hierarchy
	 *            级次定义
	 * @param parent
	 *            父节点
	 * @param child
	 *            子节点
	 * @param others
	 *            其他子节点
	 */
	public void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			GUID child, GUID... others);

	/**
	 * 更新节点在指定级次定义中的父节点
	 * 
	 * @param hierarchy
	 *            级次定义
	 * @param parent
	 *            父节点
	 * @param children
	 *            子节点
	 */
	public void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			List<GUID> children);

	/**
	 * 更新节点在指定级次定义中的父节点
	 * 
	 * @param hierarchy
	 *            级次定义
	 * @param parent
	 *            父节点
	 * @param children
	 *            子节点
	 */
	public void hierarchyMoveTo(HierarchyDefine hierarchy, GUID parent,
			Iterable<GUID> children);

}
