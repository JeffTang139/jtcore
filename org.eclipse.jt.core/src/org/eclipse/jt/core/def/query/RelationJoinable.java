package org.eclipse.jt.core.def.query;

import org.eclipse.jt.core.def.table.TableDeclarator;
import org.eclipse.jt.core.def.table.TableDefine;
import org.eclipse.jt.core.def.table.TableRelationDefine;

public interface RelationJoinable {

	/**
	 * 当前关系引用增加连接关系引用
	 * 
	 * <p>
	 * 对于连续多个不带括号的连接,始终从最左边的关系引用创建连接引用对象.
	 * 
	 * @param table
	 *            连接的目标表定义
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDefine table);

	/**
	 * 当前关系引用增加连接关系引用
	 * 
	 * <p>
	 * 对于连续多个不带括号的连接,始终从最左边的关系引用创建连接引用对象.
	 * 
	 * @param table
	 *            连接的目标表定义
	 * @param alias
	 *            连接关系引用名称
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDefine table, String alias);

	/**
	 * 当前关系引用增加连接关系引用
	 * 
	 * <p>
	 * 对于连续多个不带括号的连接,始终从最左边的关系引用创建连接引用对象.
	 * 
	 * @param table
	 *            连接的目标表声明器
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDeclarator table);

	/**
	 * 当前关系引用增加连接关系引用
	 * 
	 * <p>
	 * 对于连续多个不带括号的连接,始终从最左边的关系引用创建连接引用对象.
	 * 
	 * @param table
	 *            连接的目标表声明器
	 * @param alias
	 *            连接关系引用名称
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableDeclarator table,
			String alias);

	/**
	 * 当前关系引用增加连接关系引用
	 * 
	 * <p>
	 * 对于连续多个不带括号的连接,始终从最左边的关系引用创建连接引用对象.
	 * 
	 * @param sample
	 *            使用指定表关系定义来构造连接及连接条件
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableRelationDefine sample);

	/**
	 * 当前关系引用增加连接关系引用
	 * 
	 * <p>
	 * 对于连续多个不带括号的连接,始终从最左边的关系引用创建连接引用对象.
	 * 
	 * @param sample
	 *            使用指定表关系定义来构造连接及连接条件
	 * @param alias
	 *            连接关系引用名称
	 * @return
	 */
	public JoinedTableReferenceDeclare newJoin(TableRelationDefine sample,
			String alias);

	/**
	 * 当前关系引用增加连接关系引用
	 * 
	 * <p>
	 * 对于连续多个不带括号的连接,始终从最左边的关系引用创建连接引用对象.
	 * 
	 * @param query
	 * @return
	 */
	public JoinedQueryReferenceDeclare newJoin(DerivedQueryDefine query);

	/**
	 * 当前关系引用增加连接关系引用
	 * 
	 * <p>
	 * 对于连续多个不带括号的连接,始终从最左边的关系引用创建连接引用对象.
	 * 
	 * @param query
	 * @param name
	 * @return
	 */
	public JoinedQueryReferenceDeclare newJoin(DerivedQueryDefine query,
			String name);

}
