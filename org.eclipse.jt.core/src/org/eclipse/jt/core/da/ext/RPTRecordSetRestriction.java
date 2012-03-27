package org.eclipse.jt.core.da.ext;

import org.eclipse.jt.core.def.table.TableFieldDefine;

/**
 * ��¼��Լ���������޶���ѯ��Χ
 * 
 * <p>
 * ��¼��Լ�������˶Լ�¼��ÿ������Լ��
 * <ol>
 * <li>RPTRecordSetӵ��Ĭ�ϵ�Լ����ͨ��RPTRecordSet.getDefualtRestriction()���.
 * <li>��Ĭ��Լ����ÿ����Լ��һ��ͨ��RPTRecordSetKey.getDefaultKeyRestriction()��ø�����.
 * <li>RPTRecordSet������ѯ�ֶ�ʱ����ָ��Լ������ָ����ʹ��RPTRecordSet��Ĭ��Լ��.
 * <li>����ֶ�ָ���˶�����Լ������ö���Լ����ֵΪ�յļ�Լ��ʹ��RPTRecordSet��Ĭ��Լ��.
 * <li>����ÿ��Լ����ʹ�õ��������ݿ��ѯ����ҪӦ����ͬԼ�����ֶ�Ӧ��ʹ��ͬһ��Լ������.
 * </ol>
 * 
 * @author Jeff Tang
 * 
 */
public interface RPTRecordSetRestriction {

	/**
	 * �жϱ�Լ���Ƿ�֧��ĳ��
	 * 
	 * @param index
	 *            �������
	 * @return
	 */
	public boolean isKeySupported(int index);

	/**
	 * �жϱ�Լ���Ƿ�֧��ĳ��
	 */
	public boolean isKeySupported(RPTRecordSetKey key);

	/**
	 * ��ȡ��Լ��<br>
	 * RPTRecordSet��Ĭ��Լ����ÿ����Լ��һ��ͨ��RPTRecordSetKey.getDefaultKeyRestriction()
	 * ��ø�����<br>
	 */
	public RPTRecordSetKeyRestriction getKeyRestriction(int index);

	/**
	 * ��ȡ��Լ��<br>
	 * RPTRecordSet��Ĭ��Լ����ÿ����Լ��һ��ͨ��RPTRecordSetKey.getDefaultKeyRestriction(
	 * )��ø�����<br>
	 */
	public RPTRecordSetKeyRestriction getKeyRestriction(RPTRecordSetKey key);

	/**
	 * ���ݼ����ƻ�ü�Լ��
	 */
	public RPTRecordSetKeyRestriction getKeyRestriction(String keyName);

	/**
	 * ���Լ���е�ֵ
	 */
	public void clearMatchValues();

	/**
	 * �½���¼�ֶ�
	 */
	public RPTRecordSetField newField(TableFieldDefine tableField);
}
