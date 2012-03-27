package org.eclipse.jt.core.impl;

/**
 * ��Ҫ׼���Ľӿ�
 * 
 * @author Jeff Tang
 * 
 */
interface Prepareble {
	/**
	 * ������ݿ�û��׼���þͲ�׼��
	 */
	public boolean ignorePrepareIfDBInvalid();

	/**
	 * ����Ƿ��Ѿ�׼������
	 */
	public boolean isPrepared();

	/**
	 * ȷ��׼��
	 * 
	 * @param context
	 *            ������
	 * @param rePrepared
	 *            �Ƿ�����׼��
	 */
	public void ensurePrepared(ContextImpl<?, ?, ?> context, boolean rePrepared);
}
