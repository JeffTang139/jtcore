/**
 * 
 */
package org.eclipse.jt.core.impl;

/**
 * Ϊ����Դ���ö����
 * 
 * @author Jeff Tang
 * 
 */
interface FieldValueAccessor {

	public Object internalGet(Object so);

	public void internalSet(Object so, Object value);
}