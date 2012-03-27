package org.eclipse.jt.core.da;

import java.util.List;

import org.eclipse.jt.core.misc.ObjectBuilder;
import org.eclipse.jt.core.type.GUID;


/**
 * ʵ��󶨵����ݿ���ʽӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface ORMAccessor<TEntity> {

	/**
	 * ���ָ����ʵ��
	 * 
	 * @param entity
	 */
	void insert(TEntity entity);

	/**
	 * ���ָ����ʵ��
	 * 
	 * @param entity
	 * @param others
	 */
	void insert(TEntity entity, TEntity... others);

	/**
	 * ���ָ����ʵ��
	 * 
	 * @param entities
	 */
	void insert(TEntity[] entities);

	/**
	 * ���ָ����ʵ��
	 * 
	 * @param entities
	 */
	void insert(Iterable<TEntity> entities);

	/**
	 * ɾ��ָ��recid�ļ�¼
	 * 
	 * @param recid
	 *            ��¼recidֵ
	 * @return �����Ƿ�ɾ���ɹ�
	 */
	boolean delete(GUID recid);

	/**
	 * �Ƚϵ�ǰ���ݿ��ж�Ӧ��ʵ��汾�Ƿ����ڴ�ֵ��ͬ,��ͬ��ɾ��ʵ�壬����ɾ����
	 * 
	 * @param recid
	 *            ��¼recid
	 * @param expectRECVER
	 *            �ڴ��ļ�¼�汾
	 * @return �����Ƿ�ɾ���ɹ�
	 */

	boolean delete(GUID recid, long expectRECVER);

	/**
	 * ɾ��ָ��recid�ļ�¼
	 * 
	 * @param recid
	 *            ��һ����¼recid
	 * @param others
	 *            ���µļ�¼��recid
	 * @return ����ȷ����ɾ���ļ�¼���� @
	 */
	int delete(GUID recid, GUID... others);

	/**
	 * ɾ��ָ��ID�ļ�¼
	 * 
	 * @param recid
	 *            ��һ����¼recid
	 * @param others
	 *            ���µļ�¼��recid
	 * @return ����ȷ����ɾ���ļ�¼����
	 */
	int delete(GUID[] recids);

	/**
	 * ɾ��ָ����ʵ��
	 * 
	 * @param entity
	 * @return ����ɾ���ĸ���,0��1
	 */
	boolean delete(TEntity entity);

	/**
	 * ɾ��ָ����ʵ��
	 * 
	 * @param entity
	 *            ��һ��ʵ��
	 * @param others
	 *            ���µ�ʵ��
	 * @return ����ȷ����ɾ���ļ�¼����
	 */
	int delete(TEntity entity, TEntity... others);

	/**
	 * ɾ��ָ����ʵ��
	 * 
	 * @param entities
	 * @return ����ȷ����ɾ���ļ�¼����
	 */
	int delete(TEntity[] entities);

	/**
	 * ɾ��ָ����ʵ��
	 * 
	 * @param entities
	 * @return ����ȷ����ɾ���ļ�¼����
	 */
	int delete(Iterable<TEntity> entities);

	/**
	 * ���߼�����ֵɾ��ָ��ʵ��
	 * 
	 * <p>
	 * ORM����Ĳ�ѯ���б�������������õ������߼�������,���������õ�Ŀ���������һ�����߼�����
	 * 
	 * @param values
	 * @return
	 */
	int deleteByPKey(Object... keys);

	/**
	 * ����ָ����ʵ��
	 * 
	 * @param entity
	 * @return
	 */
	boolean update(TEntity entity);

	/**
	 * ����ָ����ʵ��
	 * 
	 * @param entity
	 * @param others
	 * @return
	 */
	int update(TEntity entity, TEntity... others);

	/**
	 * ����ָ����ʵ��
	 * 
	 * @param entities
	 * @return
	 */
	int update(TEntity[] entities);

	/**
	 * ����ָ����ʵ��
	 * 
	 * @param entities
	 * @return
	 */
	int update(Iterable<TEntity> entities);

	/**
	 * �Ƚϵ�ǰ���ݿ��ж�Ӧ��ʵ��汾�Ƿ����ڴ�ֵ��ͬ,��ͬ�����ʵ�壬���򲻸���
	 * 
	 * @param entity
	 * @param expectRECVER
	 *            �ڴ����а汾
	 * @return �����Ƿ����
	 */
	boolean update(TEntity entity, long expectRECVER);

	/**
	 * ִ��ORM�����ѯ
	 * 
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public List<TEntity> fetch(Object... argValues);

	/**
	 * ִ��ORM�����ѯ
	 * 
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public List<TEntity> fetch(List<Object> argValues);

	/**
	 * ִ�в�ѯ
	 * 
	 * @param entityFactory
	 *            ʵ���������
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public List<TEntity> fetch(ObjectBuilder<TEntity> entityFactory,
			Object... argValues);

	/**
	 * ִ��ORM�����ѯ
	 * 
	 * @param entityFactory
	 *            ʵ���������
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public List<TEntity> fetch(ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues);

	/**
	 * �޶������з�Χִ�в�ѯ
	 * 
	 * @param offset
	 *            �����е�ƫ��
	 * @param rowCount
	 *            ���ص�����
	 * @param argValues
	 *            ����ֵ
	 * @return
	 */
	public List<TEntity> fetchLimit(long offset, long rowCount,
			Object... argValues);

	/**
	 * �޶������з�Χִ�в�ѯ
	 * 
	 * @param offset
	 *            �����е�ƫ��
	 * @param rowCount
	 *            ���ص�����
	 * @param argValues
	 *            ����ֵ
	 * @return
	 */
	public List<TEntity> fetchLimit(long offset, long rowCount,
			List<Object> argValues);

	/**
	 * �޶������з�Χִ�в�ѯ
	 * 
	 * @param offset
	 *            �����е�ƫ��
	 * @param rowCount
	 *            ���ص�����
	 * @param entityFactory
	 *            ��������
	 * @param argValues
	 *            ����ֵ
	 * @return
	 */
	public List<TEntity> fetchLimit(long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, Object... argValues);

	/**
	 * �޶������з�Χִ�в�ѯ
	 * 
	 * @param offset
	 *            �����е�ƫ��
	 * @param rowCount
	 *            ���ص�����
	 * @param entityFactory
	 *            ��������
	 * @param argValues
	 *            ����ֵ
	 * @return
	 */
	public List<TEntity> fetchLimit(long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues);

	/**
	 * ִ��ORM�����ѯ
	 * 
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public int fetchInto(List<TEntity> into, Object... argValues);

	/**
	 * ִ��ORM�����ѯ
	 * 
	 * @param into
	 * @param argValues
	 * @return
	 */
	public int fetchInto(List<TEntity> into, List<Object> argValues);

	/**
	 * ִ�в�ѯ
	 * 
	 * @param entityFactory
	 *            ʵ���������
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public int fetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, Object... argValues);

	/**
	 * ִ��ORM�����ѯ
	 * 
	 * @param entityFactory
	 *            ʵ���������
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public int fetchInto(List<TEntity> into,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues);

	/**
	 * �޶������з�Χִ�в�ѯ
	 * 
	 * @param offset
	 *            �����е�ƫ��
	 * @param rowCount
	 *            ���ص�����
	 * @param argValues
	 *            ����ֵ
	 * @return
	 */
	public int fetchIntoLimit(List<TEntity> into, long offset, long rowCount,
			Object... argValues);

	/**
	 * �޶������з�Χִ�в�ѯ
	 * 
	 * @param offset
	 *            �����е�ƫ��
	 * @param rowCount
	 *            ���ص�����
	 * @param argValues
	 *            ����ֵ
	 * @return
	 */
	public int fetchIntoLimit(List<TEntity> into, long offset, long rowCount,
			List<Object> argValues);

	/**
	 * �޶������з�Χִ�в�ѯ
	 * 
	 * @param offset
	 *            �����е�ƫ��
	 * @param rowCount
	 *            ���ص�����
	 * @param entityFactory
	 *            ��������
	 * @param argValues
	 *            ����ֵ
	 * @return
	 */
	public int fetchIntoLimit(List<TEntity> into, long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, Object... argValues);

	/**
	 * �޶������з�Χִ�в�ѯ
	 * 
	 * @param offset
	 *            �����е�ƫ��
	 * @param rowCount
	 *            ���ص�����
	 * @param entityFactory
	 *            ��������
	 * @param argValues
	 *            ����ֵ
	 * @return
	 */
	public int fetchIntoLimit(List<TEntity> into, long offset, long rowCount,
			ObjectBuilder<TEntity> entityFactory, List<Object> argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param argValues
	 * @return
	 */
	public int rowCountOf(Object... argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param argValues
	 * @return
	 */
	public int rowCountOf(List<Object> argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param argValues
	 * @return
	 */
	public long rowCountOfL(Object... argValues);

	/**
	 * ��ȡ��ѯ�����������
	 * 
	 * @param argValues
	 * @return
	 */
	public long rowCountOfL(List<Object> argValues);

	/**
	 * ִ�в�ѯ,���ز�ѯ����ĵ�һ��ʵ�����
	 * 
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public TEntity first(Object... argValues);

	/**
	 * ִ�в�ѯ,���ز�ѯ����ĵ�һ��ʵ�����
	 * 
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public TEntity first(List<Object> argValues);

	/**
	 * ִ�в�ѯ,���ز�ѯ����ĵ�һ��ʵ�����
	 * 
	 * @param entityFactory
	 *            ʵ���������
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public TEntity first(ObjectBuilder<TEntity> entityFactory,
			Object... argValues);

	/**
	 * ִ�в�ѯ,���ز�ѯ����ĵ�һ��ʵ�����
	 * 
	 * @param entityFactory
	 *            ʵ���������
	 * @param argValues
	 *            ����ֵ�б�
	 * @return
	 */
	public TEntity first(ObjectBuilder<TEntity> entityFactory,
			List<Object> argValues);

	/**
	 * ��recid����ʵ�����
	 * 
	 * @param recid
	 * @return
	 */
	public TEntity findByRECID(GUID recid);

	/**
	 * ��recid����ʵ�����
	 * 
	 * @param entityFactory
	 *            ʵ���������
	 * @param recid
	 * @return
	 */
	public TEntity findByRECID(ObjectBuilder<TEntity> entityFactory, GUID recid);

	/**
	 * ���߼���������ʵ�����
	 * 
	 * <p>
	 * ORM����Ĳ�ѯ���б�������������õ������߼�������,���������õ�Ŀ���������һ�����߼�����
	 * 
	 * @param keyValues
	 *            �߼�����ֵ,�������ڱ���˳����ֵ
	 * @return
	 */
	public TEntity findByPKey(Object... keyValues);

	/**
	 * ���߼���������ʵ�����
	 * 
	 * <p>
	 * ORM����Ĳ�ѯ���б�������������õ������߼�������,���������õ�Ŀ���������һ�����߼�����
	 * 
	 * @param entityFactory
	 *            ʵ���������
	 * @param pKeyValues
	 *            �߼�����ֵ,�������ڱ���˳����ֵ
	 * @return
	 */
	public TEntity findByPKey(ObjectBuilder<TEntity> entityFactory,
			Object... pKeyValues);

	/**
	 * ����ʹ�ø÷��������������һ��ʹ�ú�Զ�������Ż����ݿ�����<br>
	 * ���ø÷������ᵼ�¶��󲻿��ã�ֻ����ʱ�ͷ����ݿ���Դ @
	 */
	public void unuse();

}
