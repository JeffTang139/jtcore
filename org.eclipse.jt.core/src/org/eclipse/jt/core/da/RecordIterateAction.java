package org.eclipse.jt.core.da;

import org.eclipse.jt.core.Context;

/**
 * ��¼����ĵ�������
 * 
 * @author Jeff Tang
 * 
 */
public interface RecordIterateAction {

	/**
	 * ��¼����ĵ�������
	 * 
	 * @param context
	 *            ��ǰcontext
	 * @param record
	 *            ��ǰ�������ļ�¼����
	 * @param recordIndex
	 *            ��ǰ��������¼��������,��0��ʼ
	 * @return �����Ƿ���ֹ��������.Ϊtrue�򲻻������ȡ��һ�в�ѯ���.
	 * @throws Throwable
	 */
	public boolean iterate(Context context, IteratedRecord record,
			long recordIndex) throws Throwable;
}
