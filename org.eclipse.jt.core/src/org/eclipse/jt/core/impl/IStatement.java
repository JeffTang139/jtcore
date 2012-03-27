package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.StatementDeclare;

/**
 * 语句的内部接口
 * 
 * @author Jeff Tang
 * 
 */
interface IStatement extends StatementDeclare, ArgumentOwner, Prepareble {

	/**
	 * 获取语句的sql
	 * 
	 * @param dbAdapter
	 * @return
	 */
	Sql getSql(DBAdapterImpl dbAdapter);

	/**
	 * 返回参数定义的结构对象
	 * 
	 * @return
	 */
	StructDefineImpl getArgumentsDefine();

	NamedDefineContainerImpl<StructFieldDefineImpl> getArguments();
}
