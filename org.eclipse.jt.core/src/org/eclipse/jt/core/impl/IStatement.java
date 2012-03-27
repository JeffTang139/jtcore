package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.query.StatementDeclare;

/**
 * �����ڲ��ӿ�
 * 
 * @author Jeff Tang
 * 
 */
interface IStatement extends StatementDeclare, ArgumentOwner, Prepareble {

	/**
	 * ��ȡ����sql
	 * 
	 * @param dbAdapter
	 * @return
	 */
	Sql getSql(DBAdapterImpl dbAdapter);

	/**
	 * ���ز�������Ľṹ����
	 * 
	 * @return
	 */
	StructDefineImpl getArgumentsDefine();

	NamedDefineContainerImpl<StructFieldDefineImpl> getArguments();
}
