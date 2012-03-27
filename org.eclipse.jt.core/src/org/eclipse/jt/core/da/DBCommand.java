package org.eclipse.jt.core.da;

import org.eclipse.jt.core.def.arg.ArgumentDefine;
import org.eclipse.jt.core.def.obja.DynamicObject;
import org.eclipse.jt.core.def.query.StatementDefine;

/**
 * ���ݿ�����ӿ�
 * 
 * <p>
 * ���������ݿ���Դ�Ŀ����õķ��ʽӿ�.�����Ҫ���ִ��ͬһ���,����˶����ִ��Ч������ֱ�ӵ���DBAdapter�ĸ�����.
 * 
 * @author Jeff Tang
 * 
 */
public interface DBCommand {

	/**
	 * �������ݿ���䶨��
	 */
	public StatementDefine getStatement();

	/**
	 * ��ȡ��������
	 * 
	 * @return ���ز�������
	 */
	public DynamicObject getArgumentsObj();

	/**
	 * ���ò���ֵ
	 * 
	 * @param argValues
	 *            ����ֵ
	 */
	public void setArgumentValues(Object... argValues);

	/**
	 * ����ָ��������ֵ
	 * 
	 * @param argIndex
	 *            ��������ţ���0��ʼ
	 * @param argValue
	 *            ����ֵ
	 */
	public void setArgumentValue(int argIndex, Object argValue);

	/**
	 * ����ָ��������ֵ
	 * 
	 * @param arg
	 *            ��������
	 * @param argValue
	 *            ����ֵ
	 */
	public void setArgumentValue(ArgumentDefine arg, Object argValue);

	/**
	 * ִ�����,�����ص�Ӱ������
	 * 
	 * @return ����ִ�е�Ӱ�����ĸ���,Ŀ���Ϊ�������ʱ�ķ���������
	 */
	public int executeUpdate();

	/**
	 * ִ�в�ѯ,װ�ؽ����
	 * 
	 * <p>
	 * ��ѯ�����һ����װ�뵽��¼����.����ѯ�����н϶�ʱ,����ʹ�ô����޶��Ĳ�ѯ���ߵ���������Ĳ�ѯ�ӿ�
	 * 
	 * @return ��ѯ��¼��
	 */
	public RecordSet executeQuery();

	/**
	 * ִ�д����޶��Ĳ�ѯ,װ�ؼ�¼��
	 * 
	 * @param offset
	 *            ��ָ��ƫ������ʼװ�ؽ����.�ӵ�1�п�ʼ������ƫ����Ϊ0
	 * @param rowCount
	 *            װ�ص�������(�������)
	 * @return ��ѯ��¼��
	 */
	public RecordSet executeQueryLimit(long offset, long rowCount);

	/**
	 * ִ�в�ѯ,ʹ��ָ���������������
	 * 
	 * <p>
	 * ���Ὣ�����һ����װ���ڴ�,���ڷ����нϴ�Ĳ�ѯ
	 * 
	 * @param action
	 *            ��ѯ����ı�������
	 */
	public void iterateQuery(RecordIterateAction action);

	/**
	 * ִ�д����޶��Ĳ�ѯ,ʹ��ָ���������������
	 * 
	 * @param action
	 *            ��ѯ����ı�������
	 * @param offset
	 *            ��ָ��ƫ������ʼ���������.�ӵ�1�п�ʼ������ƫ����Ϊ0
	 * @param rowCount
	 *            ������������(�������)
	 */
	public void iterateQueryLimit(RecordIterateAction action, long offset,
			long rowCount);

	/**
	 * ִ�в�ѯ,���ؽ����һ�е�һ�е�ֵ
	 * 
	 * @return
	 */
	public Object executeScalar();

	/**
	 * ���ز�ѯ���������
	 * 
	 * @return
	 */
	public int rowCountOf();

	/**
	 * ���ز�ѯ���������
	 * 
	 * @return
	 */
	public long rowCountOfL();

	/**
	 * ����ʹ�ø÷��������������һ��ʹ�ú�Զ�������Ż����ݿ�����<br>
	 * ʹ�����ǿ�ҽ�����ã���û�бط���finally����<br>
	 * ���ø÷������ᵼ�¶��󲻿��ã�ֻ����ʱ�ͷ����ݿ���Դ @
	 */
	void unuse();
}
