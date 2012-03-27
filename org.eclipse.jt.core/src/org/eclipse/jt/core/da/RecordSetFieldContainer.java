package org.eclipse.jt.core.da;

import org.eclipse.jt.core.def.Container;
import org.eclipse.jt.core.def.MissingDefineException;
import org.eclipse.jt.core.def.query.QueryColumnDefine;

/**
 * ��¼���ֶ�����
 * 
 * @author Jeff Tang
 * 
 */
public interface RecordSetFieldContainer<TField extends RecordSetField> extends
	Container<TField> {
	
	/**
	 * ���ݲ�ѯ�ж�����Ҽ�¼���ֶ�
	 * 
	 * @param column ��ѯ�ж���
	 * @return �����ж������null
	 * @throws IllegalArgumentException ���ж�����Чʱ�׳��쳣
	 */
	public TField find(QueryColumnDefine column)
			throws IllegalArgumentException;

	/**
	 * ���ݲ�ѯ�ж�����Ҽ�¼���ֶ�
	 * 
	 * @param column ��ѯ�ж���
	 * @return �����ж���
	 * @throws MissingDefineException ���Ҳ����ֶ�ʱ�׳��쳣
	 * @throws IllegalArgumentException �������в���Чʱ�׳��쳣
	 */
	public TField get(QueryColumnDefine column)
			throws MissingDefineException, IllegalArgumentException;
}
