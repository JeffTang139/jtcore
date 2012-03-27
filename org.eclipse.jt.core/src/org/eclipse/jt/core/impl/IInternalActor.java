package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.auth.Actor;
import org.eclipse.jt.core.type.GUID;


/**
 * �ڲ������߽ӿ�
 * 
 * @see org.eclipse.jt.core.auth.Actor
 * @author Jeff Tang 2010-01
 */
interface IInternalActor extends Actor {

	/**
	 * �жϷ����Ƿ�֧��Ȩ��
	 * 
	 * @return ���������֧��Ȩ�޷���true�����򷵻�false
	 */
	public boolean supportAuthority();

	/**
	 * �жϷ������Ƿ������ָ����֯������ӳ���ϵ
	 * S
	 * @param orgID
	 *            ��֯����ID������Ϊ��
	 * @return ������ڷ���true�����򷵻�false
	 */
	public boolean hasMappingOrg(GUID orgID);

	public long[] getOperationACL(ContextImpl<?, ?, ?> context, GUID orgID);

	public long[] getAccreditACL(ContextImpl<?, ?, ?> context, GUID orgID);

	public long[][] getOperationACLs(ContextImpl<?, ?, ?> context, GUID orgID);

	public long[][] getAccreditACLs(ContextImpl<?, ?, ?> context, GUID orgID);

}
