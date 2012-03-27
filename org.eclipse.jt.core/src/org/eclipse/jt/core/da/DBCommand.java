package org.eclipse.jt.core.da;

import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.obja.DynamicObject;
import org.eclipse.jt.core.def.query.StatementDefine;

/**
 * 数据库命令接口
 * 
 * <p>
 * 保留了数据库资源的可重用的访问接口.如果需要多次执行同一语句,构造此对象的执行效率优于直接调用DBAdapter的各方法.
 * 
 * @author Jeff Tang
 * 
 */
public interface DBCommand {

	/**
	 * 返回数据库语句定义
	 */
	public StatementDefine getStatement();

	/**
	 * 获取参数对象
	 * 
	 * @return 返回参数对象
	 */
	public DynamicObject getArgumentsObj();

	/**
	 * 设置参数值
	 * 
	 * @param argValues
	 *            参数值
	 */
	public void setArgumentValues(Object... argValues);

	/**
	 * 设置指定参数的值
	 * 
	 * @param argIndex
	 *            参数的序号，从0开始
	 * @param argValue
	 *            参数值
	 */
	public void setArgumentValue(int argIndex, Object argValue);

	/**
	 * 设置指定参数的值
	 * 
	 * @param arg
	 *            参数定义
	 * @param argValue
	 *            参数值
	 */
	public void setArgumentValue(ArgumentDefine arg, Object argValue);

	/**
	 * 执行语句,并返回的影响行数
	 * 
	 * @return 返回执行的影响结果的个数,目标表为多物理表时的返回无意义
	 */
	public int executeUpdate();

	/**
	 * 执行查询,装载结果集
	 * 
	 * <p>
	 * 查询结果将一次性装入到记录集中.当查询返回行较多时,建议使用带行限定的查询或者迭代结果集的查询接口
	 * 
	 * @return 查询记录集
	 */
	public RecordSet executeQuery();

	/**
	 * 执行带行限定的查询,装载记录集
	 * 
	 * @param offset
	 *            从指定偏移量开始装载结果行.从第1行开始返回则偏移量为0
	 * @param rowCount
	 *            装载的总行数(最大行数)
	 * @return 查询记录集
	 */
	public RecordSet executeQueryLimit(long offset, long rowCount);

	/**
	 * 执行查询,使用指定动作遍历结果集
	 * 
	 * <p>
	 * 不会将结果集一次性装入内存,用于返回行较大的查询
	 * 
	 * @param action
	 *            查询结果的遍历动作
	 */
	public void iterateQuery(RecordIterateAction action);

	/**
	 * 执行带行限定的查询,使用指定动作遍历结果集
	 * 
	 * @param action
	 *            查询结果的遍历动作
	 * @param offset
	 *            从指定偏移量开始遍历结果行.从第1行开始返回则偏移量为0
	 * @param rowCount
	 *            遍历的总行数(最大行数)
	 */
	public void iterateQueryLimit(RecordIterateAction action, long offset,
			long rowCount);

	/**
	 * 执行查询,返回结果第一行第一列的值
	 * 
	 * @return
	 */
	public Object executeScalar();

	/**
	 * 返回查询结果的行数
	 * 
	 * @return
	 */
	public int rowCountOf();

	/**
	 * 返回查询结果的行数
	 * 
	 * @return
	 */
	public long rowCountOfL();

	/**
	 * 不再使用该访问器，或距离下一次使用很远，用于优化数据库连接<br>
	 * 使用完后强烈建议调用，但没有必放在finally块中<br>
	 * 调用该方法不会导致对象不可用，只是暂时释放数据库资源 @
	 */
	void unuse();
}
