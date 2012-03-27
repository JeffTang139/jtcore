package org.eclipse.jt.core.da;

import java.util.List;

import org.eclipse.jt.core.misc.ObjectBuilder;
import org.eclipse.jt.core.type.GUID;


/**
 * 实体绑定的数据库访问接口
 * 
 * @author Jeff Tang
 * 
 */
public interface ORMAccessor<TEntity> {

	/**
	 * 添加指定的实体
	 * 
	 * @param entity
	 */
	void insert(TEntity entity);

	/**
	 * 添加指定的实体
	 * 
	 * @param entity
	 * @param others
	 */
	void insert(TEntity entity, TEntity... others);

	/**
	 * 添加指定的实体
	 * 
	 * @param entities
	 */
	void insert(TEntity[] entities);

	/**
	 * 添加指定的实体
	 * 
	 * @param entities
	 */
	void insert(Iterable<TEntity> entities);

	/**
	 * 删除指定recid的记录
	 * 
	 * @param recid
	 *            记录recid值
	 * @return 返回是否删除成功
	 */
	boolean delete(GUID recid);

	/**
	 * 比较当前数据库中对应的实体版本是否与期待值相同,相同则删除实体，否则不删除。
	 * 
	 * @param recid
	 *            记录recid
	 * @param expectRECVER
	 *            期待的记录版本
	 * @return 返回是否删除成功
	 */

	boolean delete(GUID recid, long expectRECVER);

	/**
	 * 删除指定recid的记录
	 * 
	 * @param recid
	 *            第一个记录recid
	 * @param others
	 *            余下的记录的recid
	 * @return 返回确定的删除的记录条数 @
	 */
	int delete(GUID recid, GUID... others);

	/**
	 * 删除指定ID的记录
	 * 
	 * @param recid
	 *            第一个记录recid
	 * @param others
	 *            余下的记录的recid
	 * @return 返回确定的删除的记录条数
	 */
	int delete(GUID[] recids);

	/**
	 * 删除指定的实体
	 * 
	 * @param entity
	 * @return 返回删除的个数,0或1
	 */
	boolean delete(TEntity entity);

	/**
	 * 删除指定的实体
	 * 
	 * @param entity
	 *            第一个实体
	 * @param others
	 *            余下的实体
	 * @return 返回确定的删除的记录条数
	 */
	int delete(TEntity entity, TEntity... others);

	/**
	 * 删除指定的实体
	 * 
	 * @param entities
	 * @return 返回确定的删除的记录条数
	 */
	int delete(TEntity[] entities);

	/**
	 * 删除指定的实体
	 * 
	 * @param entities
	 * @return 返回确定的删除的记录条数
	 */
	int delete(Iterable<TEntity> entities);

	/**
	 * 按逻辑主键值删除指定实体
	 * 
	 * <p>
	 * ORM定义的查询列中必须包含主表引用的所有逻辑主键列,且主表引用的目标表含有至少一个的逻辑主键
	 * 
	 * @param values
	 * @return
	 */
	int deleteByPKey(Object... keys);

	/**
	 * 更新指定的实体
	 * 
	 * @param entity
	 * @return
	 */
	boolean update(TEntity entity);

	/**
	 * 更新指定的实体
	 * 
	 * @param entity
	 * @param others
	 * @return
	 */
	int update(TEntity entity, TEntity... others);

	/**
	 * 更新指定的实体
	 * 
	 * @param entities
	 * @return
	 */
	int update(TEntity[] entities);

	/**
	 * 更新指定的实体
	 * 
	 * @param entities
	 * @return
	 */
	int update(Iterable<TEntity> entities);

	/**
	 * 比较当前数据库中对应的实体版本是否与期待值相同,相同则更新实体，否则不更新
	 * 
	 * @param entity
	 * @param expectRECVER
	 *            期待的行版本
	 * @return 返回是否更新
	 */
	boolean update(TEntity entity, long expectRECVER);

	/**
	 * 执行ORM定义查询
	 * 
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public List<TEntity> fetch(Object... argValues);

	/**
	 * 执行ORM定义查询
	 * 
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public List<TEntity> fetch(List<Object> argValues);

	/**
	 * 执行查询
	 * 
	 * @param entityFactory
	 *            实体对象构造器
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public List<TEntity> fetch(ObjectBuilder<TEntity> entityFactory,
			Object... argValues);

	/**
	 * 执行ORM定义查询
	 * 
	 * @param entityFactory
	 *            实体对象构造器
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public List<TEntity> fetch(ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues);

	/**
	 * 限定返回行范围执行查询
	 * 
	 * @param offset
	 *            返回行的偏移
	 * @param rowCount
	 *            返回的行数
	 * @param argValues
	 *            参数值
	 * @return
	 */
	public List<TEntity> fetchLimit(long offset, long rowCount,
			Object... argValues);

	/**
	 * 限定返回行范围执行查询
	 * 
	 * @param offset
	 *            返回行的偏移
	 * @param rowCount
	 *            返回的行数
	 * @param argValues
	 *            参数值
	 * @return
	 */
	public List<TEntity> fetchLimit(long offset, long rowCount,
			List<Object> argValues);

	/**
	 * 限定返回行范围执行查询
	 * 
	 * @param offset
	 *            返回行的偏移
	 * @param rowCount
	 *            返回的行数
	 * @param entityFactory
	 *            对象构造器
	 * @param argValues
	 *            参数值
	 * @return
	 */
	public List<TEntity> fetchLimit(long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, Object... argValues);

	/**
	 * 限定返回行范围执行查询
	 * 
	 * @param offset
	 *            返回行的偏移
	 * @param rowCount
	 *            返回的行数
	 * @param entityFactory
	 *            对象构造器
	 * @param argValues
	 *            参数值
	 * @return
	 */
	public List<TEntity> fetchLimit(long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues);

	/**
	 * 执行ORM定义查询
	 * 
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public int fetchInto(List<TEntity> into, Object... argValues);

	/**
	 * 执行ORM定义查询
	 * 
	 * @param into
	 * @param argValues
	 * @return
	 */
	public int fetchInto(List<TEntity> into, List<Object> argValues);

	/**
	 * 执行查询
	 * 
	 * @param entityFactory
	 *            实体对象构造器
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public int fetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, Object... argValues);

	/**
	 * 执行ORM定义查询
	 * 
	 * @param entityFactory
	 *            实体对象构造器
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public int fetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues);

	/**
	 * 限定返回行范围执行查询
	 * 
	 * @param offset
	 *            返回行的偏移
	 * @param rowCount
	 *            返回的行数
	 * @param argValues
	 *            参数值
	 * @return
	 */
	public int fetchIntoLimit(List<TEntity> into, long offset, long rowCount,
			Object... argValues);

	/**
	 * 限定返回行范围执行查询
	 * 
	 * @param offset
	 *            返回行的偏移
	 * @param rowCount
	 *            返回的行数
	 * @param argValues
	 *            参数值
	 * @return
	 */
	public int fetchIntoLimit(List<TEntity> into, long offset, long rowCount,
			List<Object> argValues);

	/**
	 * 限定返回行范围执行查询
	 * 
	 * @param offset
	 *            返回行的偏移
	 * @param rowCount
	 *            返回的行数
	 * @param entityFactory
	 *            对象构造器
	 * @param argValues
	 *            参数值
	 * @return
	 */
	public int fetchIntoLimit(List<TEntity> into, long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, Object... argValues);

	/**
	 * 限定返回行范围执行查询
	 * 
	 * @param offset
	 *            返回行的偏移
	 * @param rowCount
	 *            返回的行数
	 * @param entityFactory
	 *            对象构造器
	 * @param argValues
	 *            参数值
	 * @return
	 */
	public int fetchIntoLimit(List<TEntity> into, long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param argValues
	 * @return
	 */
	public int rowCountOf(Object... argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param argValues
	 * @return
	 */
	public int rowCountOf(List<Object> argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param argValues
	 * @return
	 */
	public long rowCountOfL(Object... argValues);

	/**
	 * 获取查询结果的总行数
	 * 
	 * @param argValues
	 * @return
	 */
	public long rowCountOfL(List<Object> argValues);

	/**
	 * 执行查询,返回查询结果的第一个实体对象
	 * 
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public TEntity first(Object... argValues);

	/**
	 * 执行查询,返回查询结果的第一个实体对象
	 * 
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public TEntity first(List<Object> argValues);

	/**
	 * 执行查询,返回查询结果的第一个实体对象
	 * 
	 * @param entityFactory
	 *            实体对象构造器
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public TEntity first(ObjectBuilder<TEntity> entityFactory,
			Object... argValues);

	/**
	 * 执行查询,返回查询结果的第一个实体对象
	 * 
	 * @param entityFactory
	 *            实体对象构造器
	 * @param argValues
	 *            参数值列表
	 * @return
	 */
	public TEntity first(ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues);

	/**
	 * 按recid查找实体对象
	 * 
	 * @param recid
	 * @return
	 */
	public TEntity findByRECID(GUID recid);

	/**
	 * 按recid查找实体对象
	 * 
	 * @param entityFactory
	 *            实体对象构造器
	 * @param recid
	 * @return
	 */
	public TEntity findByRECID(ObjectBuilder<TEntity> entityFactory, GUID recid);

	/**
	 * 按逻辑主键查找实体对象
	 * 
	 * <p>
	 * ORM定义的查询列中必须包含主表引用的所有逻辑主键列,且主表引用的目标表含有至少一个的逻辑主键
	 * 
	 * @param keyValues
	 *            逻辑主键值,以主键在表中顺序传入值
	 * @return
	 */
	public TEntity findByPKey(Object... keyValues);

	/**
	 * 按逻辑主键查找实体对象
	 * 
	 * <p>
	 * ORM定义的查询列中必须包含主表引用的所有逻辑主键列,且主表引用的目标表含有至少一个的逻辑主键
	 * 
	 * @param entityFactory
	 *            实体对象构造器
	 * @param pKeyValues
	 *            逻辑主键值,以主键在表中顺序传入值
	 * @return
	 */
	public TEntity findByPKey(ObjectBuilder<TEntity> entityFactory,
			Object... pKeyValues);

	/**
	 * 不再使用该访问器，或距离下一次使用很远，用于优化数据库连接<br>
	 * 调用该方法不会导致对象不可用，只是暂时释放数据库资源 @
	 */
	public void unuse();

}
