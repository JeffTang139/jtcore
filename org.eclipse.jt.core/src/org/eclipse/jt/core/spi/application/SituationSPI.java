package org.eclipse.jt.core.spi.application;

import org.eclipse.jt.core.situation.Situation;
import org.eclipse.jt.core.spi.publish.SpaceToken;

/**
 * Situation的内部编程接口，提供给界面框架使用
 * 
 * @author Jeff Tang
 * 
 */
public interface SituationSPI extends Situation {
	/**
	 * 新建子境况
	 * 
	 * @param space
	 *            境况的空间位置
	 * @return 返回创建的子境况
	 */
	public SituationSPI newSubSituation(SpaceToken space);

	/**
	 * 指定出现了异常，便于后续事务回滚
	 * 
	 * @param e
	 */
	public void exception(Throwable e);

	/**
	 * 提交或回滚事务,释放数据库资源，内存事务资源
	 * 
	 * @return 返回之前的异常对象
	 */
	public Throwable resolveTrans();

	/**
	 * 关闭当前境况
	 */
	public void close();
}
