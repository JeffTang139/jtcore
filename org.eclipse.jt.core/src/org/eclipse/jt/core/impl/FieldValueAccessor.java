/**
 * 
 */
package org.eclipse.jt.core.impl;

/**
 * 为了资源引用而设计
 * 
 * @author Jeff Tang
 * 
 */
interface FieldValueAccessor {

	public Object internalGet(Object so);

	public void internalSet(Object so, Object value);
}