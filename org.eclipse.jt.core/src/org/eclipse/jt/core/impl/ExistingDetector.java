package org.eclipse.jt.core.impl;

/**
 * ����̽����
 * 
 * @author Jeff Tang
 * 
 * @param <TContainer>
 * @param <TElement>
 * @param <TKey>
 */
interface ExistingDetector<TContainer, TElement, TKey> {

	/**
	 * ��������Ƿ����
	 */
	boolean exists(TContainer container, TKey key, TElement ignore);
}