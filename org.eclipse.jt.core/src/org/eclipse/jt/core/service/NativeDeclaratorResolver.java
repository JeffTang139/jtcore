package org.eclipse.jt.core.service;

import org.eclipse.jt.core.impl.DeclaratorBase;

/**
 * 局部声明器的注册器，负责实例化声明器，并做相关的系统处理
 * 
 * @author Jeff Tang
 * 
 */
public interface NativeDeclaratorResolver {
	/**
	 * 实例化声名器并返回
	 * 
	 * @param <TDeclarator>
	 *            声明器类型
	 * @param declaratorClass
	 *            声明器类
	 * @param aditionalArgs
	 *            额外的参数
	 * @return 返回声明器的实例
	 */
	public <TDeclarator extends DeclaratorBase> TDeclarator resolveDeclarator(
			Class<TDeclarator> declaratorClass, Object... aditionalArgs);
}
