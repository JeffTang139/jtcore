package org.eclipse.jt.core.da;

import java.sql.SQLException;

/**
 * ���ݼ��ӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface RecordSet {

	/**
	 * ��¼���Ƿ�Ϊ��
	 */
	public boolean isEmpty();

	/**
	 * ����¼��ָ���ƶ��ĵ���һ��λ��
	 * 
	 * @return �����Ƿ���Чλ��
	 */
	public boolean first();

	/**
	 * ��ָ���ƶ���ǰһ��λ��
	 * 
	 * @return �����Ƿ���Чλ��
	 */
	public boolean previous();

	/**
	 * ��ָ���ƶ�����һ��λ��
	 * 
	 * @return �����Ƿ���Чλ��
	 */
	public boolean next();

	/**
	 * �Ƿ�λ�ڼ�¼�����һ��
	 * 
	 * @return �����Ƿ���Чλ��
	 */
	@Deprecated
	public boolean isLast();

	/**
	 * ���ص�ǰ��¼������
	 */
	public int getRecordCount();

	/**
	 * ����������ƶ�ָ��
	 * 
	 * @param rows
	 * @return
	 */
	public boolean relative(int rows);

	/**
	 * ����¼��ָ���ƶ���ָ�������
	 * 
	 * @param index
	 *            �����,��0��ʼ
	 * @return
	 */
	public boolean absolute(int index);

	/**
	 * ��õ�ǰλ����ţ���0��ʼ
	 */
	public int getPosition();

	/**
	 * ��ü�¼�����Ӧ��λ�ã�����Ѿ�ɾ���򷵻�-1
	 */
	public int positionOfRO(Object ro);

	/**
	 * ��õ�ǰ��¼��״̬�������ǰ��¼��Ч���׳��쳣
	 */
	public RecordState getRecordState();

	/**
	 * ����һ�пռ�¼,
	 */
	public void append();

	/**
	 * ɾ����ǰ��¼��ָ���ƶ�����һ������λ�ã������ɾ�����һ����¼��ָ��λ��ΪEOF
	 */
	public boolean delete();

	/**
	 * ��¼�����ֶμ���
	 * 
	 * @return
	 */
	public RecordSetFieldContainer<? extends RecordSetField> getFields();

	/**
	 * ��õ�ǰ��¼����,��Ϊ�߼�ʹ��<br>
	 * ֱ�Ӳ�����¼���󽫵��¼�¼��modify״̬��һ�¡�֮��������ݿ�ʱ�п��ܻ�ʧЧ��
	 */
	public Object getCurrentRO();

	/**
	 * ����¼�����α�����Ϊĳ��¼������Ҫ��֮ǰ��getCurrentRO()��á�<br>
	 * �÷����ڲ��������¼�б�λ��¼������ڼ�¼���϶�ʱ��Ӱ��Ч��
	 * 
	 * @param ro
	 *            ֮ǰ��getCurrentRO()��õļ�¼����
	 * @return ���ظö����Ƿ�δ��ɾ�����Ѿ���ɾ���򷵻�false�����򷵻�true
	 */
	public boolean setCurrentRO(Object ro);

	/**
	 * �������ݼ����޸�
	 * 
	 * @param adapter
	 *            ���ݿ�������
	 * @return ���ظ���Ӱ�������
	 */
	public int update(DBAdapter adapter) throws SQLException;

	/**
	 * ���´����ݼ�
	 * 
	 * @param adapter
	 *            ���ݿ�������
	 * @param argumetns
	 *            �����б�
	 */
	public void reQuery(DBAdapter adapter, Object... argumetns)
			throws SQLException;

	/**
	 * ���´����ݼ�
	 * 
	 * @param dbCommand
	 *            �򿪸����ݼ�����������ݼ����ݵ��������
	 */
	public void reQuery(DBCommand dbCommand) throws SQLException;
}
