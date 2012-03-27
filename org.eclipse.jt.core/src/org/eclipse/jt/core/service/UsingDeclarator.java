package org.eclipse.jt.core.service;

import org.eclipse.jt.core.invoke.Event;
import org.eclipse.jt.core.invoke.SimpleTask;
import org.eclipse.jt.core.invoke.Task;

/**
 * ����ʹ�ÿ��ĳЩ���õĽӿ�
 * 
 * @author Jeff Tang
 * 
 */
public interface UsingDeclarator {
	/**
	 * ����ʹ��ĳ�����䷽��
	 * 
	 * @param taskClass
	 *            ������
	 * @param method
	 *            ��һ������
	 * @param others
	 *            ���·���
	 */
	public <TTask extends Task<TMethod>, TMethod extends Enum<TMethod>> void usingTask(
			Class<TTask> taskClass, TMethod method, TMethod... others);

	public void usingTask(Class<? extends SimpleTask> taskClass);

	/**
	 * ����ʹ��ĳ��Դ
	 * 
	 * @param facadeClass
	 *            ��Դ�����
	 */
	public void usingResource(Class<?> facadeClass);

	public void usingResource(Class<?> facadeClass, Class<?> key);

	public void usingResource(Class<?> facadeClass, Class<?> key1, Class<?> key2);

	public void usingResource(Class<?> facadeClass, Class<?> key1,
			Class<?> key2, Class<?> key3);

	public void usingResource(Class<?> facadeClass, Class<?> key1,
			Class<?> key2, Class<?> key3, Class<?>... otherKeyClasses);

	/**
	 * ����ʹ��ĳ����Ĳ�ѯ
	 * 
	 * @param resultClass
	 *            �����
	 */
	public void usingResult(Class<?> resultClass);

	/**
	 * ����ʹ��ĳ����Ĳ�ѯ
	 * 
	 * @param resultClass
	 *            �����
	 * @param keyClass
	 *            ��ѯƾ����
	 */
	public void usingResult(Class<?> resultClass, Class<?> keyClass);

	/**
	 * ����ʹ��ĳ����Ĳ�ѯ
	 * 
	 * @param resultClass
	 *            �����
	 * @param keyClass
	 *            ��ѯƾ����
	 */
	public void usingResult(Class<?> resultClass, Class<?> key1Class,
			Class<?> key2Class);

	/**
	 * ����ʹ��ĳ����Ĳ�ѯ
	 * 
	 * @param resultClass
	 *            �����
	 * @param keyClass
	 *            ��ѯƾ����
	 */
	public void usingResult(Class<?> resultClass, Class<?> key1Class,
			Class<?> key2Class, Class<?> key3Class);

	/**
	 * ����ʹ��ĳ����Ĳ�ѯ
	 * 
	 * @param resultClass
	 *            �����
	 */
	public void usingList(Class<?> resultClass);

	/**
	 * ����ʹ��ĳ����Ĳ�ѯ
	 * 
	 * @param resultClass
	 *            �����
	 * @param keyClass
	 *            ��ѯƾ����
	 */
	public void usingList(Class<?> resultClass, Class<?> keyClass);

	/**
	 * ����ʹ��ĳ����Ĳ�ѯ
	 * 
	 * @param resultClass
	 *            �����
	 * @param keyClass
	 *            ��ѯƾ����
	 */
	public void usingList(Class<?> resultClass, Class<?> key1Class,
			Class<?> key2Class);

	/**
	 * ����ʹ��ĳ����Ĳ�ѯ
	 * 
	 * @param resultClass
	 *            �����
	 * @param keyClass
	 *            ��ѯƾ����
	 */
	public void usingList(Class<?> resultClass, Class<?> key1Class,
			Class<?> key2Class, Class<?> key3Class);

	/**
	 * ����ʹ��ĳ�¼�������
	 * 
	 * @param eventClass
	 *            �¼���
	 */
	public void usingEventListener(Class<? extends Event> eventClass);
}
