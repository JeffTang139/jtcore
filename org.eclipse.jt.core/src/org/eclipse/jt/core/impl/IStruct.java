/**
 * Copyright (C) 2007-2008 JeffTang Software Co., Ltd. All rights reserved.
 *
 * File IStruct.java
 * Date 2008-7-3
 */
package org.eclipse.jt.core.impl;

/**
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public interface IStruct {
	/**
	 * 创建新的实例，并用本实例的数据初始化新的实例。 需要满足的条件是，修改新实例的任何属性都不会影响到本实例的数据。
	 * 
	 * @return 新创建并已经初始化的实例
	 */
	Object cloneInstance();

	/**
	 * 从指定的实例中复制数据（状态）。 复制的结果就能保证：修改参与复制的任何一方的数据，都不影响另一方的数据。
	 * 
	 * @param src
	 */
	void copyFrom(Object src);
}
