package org.eclipse.jt.core.def;

/**
 * 名称标识的定义的容器接口
 * 
 * @author Jeff Tang
 * 
 * @param <TDefine>
 */
public interface NamedElementContainer<TElement extends Namable> extends
        Container<TElement> {
	/**
	 * 根据名称查找定义
	 * 
	 * @param name
	 *            定义名称
	 * @return 找到则返回定义接口，否则返回null
	 */
	public TElement find(String name);

	/**
	 * 根据名称得到定义
	 * 
	 * @param name
	 *            定义名称
	 * @return 找到则返回定义接口，否则抛出异常
	 * @throws MissingDefineException
	 *             找不到定义时抛出异常
	 */
	public TElement get(String name) throws MissingDefineException;
}
