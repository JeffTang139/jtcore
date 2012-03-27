package org.eclipse.jt.core.impl;

/**
 * 存在探测器
 * 
 * @author Jeff Tang
 * 
 * @param <TContainer>
 * @param <TElement>
 * @param <TKey>
 */
interface ExistingDetector<TContainer, TElement, TKey> {

	/**
	 * 检查名称是否存在
	 */
	boolean exists(TContainer container, TKey key, TElement ignore);
}