package org.eclipse.jt.core.cb;

import org.eclipse.jt.core.def.MetaElementType;

/**
 * 元数据提供器
 * 
 * <p>
 * 回调接口
 * 
 * @author Jeff Tang
 * 
 */
public interface DefineProvider {

	/**
	 * 请求加载元数据定义到容器里
	 * 
	 * @param demander
	 * @param type
	 * @param name
	 */
	public void demand(DefineHolder demander, MetaElementType type, String name);
}
