package org.eclipse.jt.core.impl;

import java.util.List;

import org.eclipse.jt.core.TreeNode;
import org.eclipse.jt.core.misc.ExceptionCatcher;
import org.eclipse.jt.core.service.UsingDeclarator;
import org.eclipse.jt.core.service.Publish.Mode;


/**
 * �������ṩ��
 * 
 * @author Jeff Tang
 * 
 */
abstract class ServiceInvokeeBase<TObject, TContext, TKey1, TKey2, TKey3> {
	private static final int MASKS_SHIFT = ServiceBase.MAX_TASK_METHODS + 1;
	// ����
	static final int MASKS_MASK = -1 << MASKS_SHIFT;
	// ����
	static final int MASK_TASK = 0 << MASKS_SHIFT;
	// �¼�
	static final int MASK_EVENT = 1 << MASKS_SHIFT;
	// ���
	static final int MASK_RESULT = 2 << MASKS_SHIFT;
	// �б�
	static final int MASK_LIST = 3 << MASKS_SHIFT;
	// ���νṹ
	static final int MASK_TREE = 4 << MASKS_SHIFT;
	// ������
	static final int MASK_ELEMENT = 5 << MASKS_SHIFT;
	// ��Դ����
	static final int MASK_RESOURCE = 6 << MASKS_SHIFT;
	// ����
	static final int MASK_DEFINE = 7 << MASKS_SHIFT;
	// ������
	static final int MASK_ELEMENT_META = 8 << MASKS_SHIFT;
	// ����ű�
	static final int MASK_DECLARE_SCRIPT = 9 << MASKS_SHIFT;

	static final UnsupportedOperationException buildKeysMessage(String msgHead,
			Class<?> facadeClass, Object key1, Object key2, Object key3,
			Object[] keys) {
		StringBuilder sb = new StringBuilder(msgHead);
		sb.append('[').append(facadeClass.getName());
		sb.append('(').append(key1);
		sb.append(',').append(key2);
		sb.append(',').append(key3);
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				sb.append(',').append(keys[i]);
			}
		}
		return new UnsupportedOperationException(sb.append(')').append(']')
				.toString());
	}

	/**
	 * ���ز���������
	 */
	ConcurrentController getConcurrentController() {
		return null;
	}

	final static UnsupportedOperationException noListProviderException(
			Class<?> resultClass, Object key1, Object key2, Object key3) {
		return buildKeysMessage("�Ҳ����б����ṩ��", resultClass, key1, key2, key3,
				null);
	}

	final static UnsupportedOperationException noResultProviderException(
			Class<?> resultClass, Object key1, Object key2, Object key3) {
		return buildKeysMessage("�Ҳ�������ṩ��", resultClass, key1, key2, key3, null);
	}

	final static UnsupportedOperationException noTreeProviderException(
			Class<?> resultClass, Object key1, Object key2, Object key3) {
		return buildKeysMessage("�Ҳ���������ṩ��", resultClass, key1, key2, key3,
				null);
	}

	final static UnsupportedOperationException noResourceListException(
			Class<?> facadeClass, Object key1, Object key2, Object key3,
			Object[] keys) {
		return buildKeysMessage("�Ҳ�������Դ��ֵ�б�", facadeClass, key1, key2, key3,
				keys);
	}

	final static UnsupportedOperationException noResourceException(
			Class<?> facadeClass, Object key1, Object key2, Object key3,
			Object[] otherKeys) {
		return buildKeysMessage("�Ҳ�����Դ", facadeClass, key1, key2, key3,
				otherKeys);
	}

	final static UnsupportedOperationException noResourceTreeException(
			Class<?> facadeClass, Object key1, Object key2, Object key3,
			Object[] keys) {
		return buildKeysMessage("�Ҳ�������Դ����", facadeClass, key1, key2, key3, keys);
	}

	/**
	 * ע�ᵽλ�����ش���
	 */
	void afterRegInvokeeToSpace(ServiceInvokeeEntry to, Space space,
			ExceptionCatcher catcher) {
	}

	/**
	 * ����ģʽ,ע��ʱָ��
	 */
	Mode publishMode;
	@SuppressWarnings("unchecked")
	ServiceInvokeeBase next;

	/**
	 * ��������ģ��
	 * 
	 * @return
	 */
	abstract ServiceBase<?> getService();

	/**
	 * �Ƚ��Ƿ�ƥ��
	 */
	abstract boolean match(Class<?> key1Class, Class<?> key2Class,
			Class<?> key3Class, int mask);

	/**
	 * ��ȡ�������
	 * 
	 * @param typeArgFinder
	 *            ģ����Ϣ
	 * @return ���ؿմ������ע��
	 */
	abstract Class<?> getTargetClass();

	TObject provide(TContext context) throws Throwable {
		throw new UnsupportedOperationException();
	}

	TObject provide(TContext context, TKey1 key1) throws Throwable {
		throw new UnsupportedOperationException();
	}

	TObject provide(TContext context, TKey1 key1, TKey2 key2) throws Throwable {
		throw new UnsupportedOperationException();
	}

	TObject provide(TContext context, TKey1 key1, TKey2 key2, TKey3 key3)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	void provide(TContext context, List<TObject> results) throws Throwable {
		throw new UnsupportedOperationException();
	}

	void provide(TContext context, TKey1 key1, List<TObject> results)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	void provide(TContext context, TKey1 key1, TKey2 key2, List<TObject> results)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	void provide(TContext context, TKey1 key1, TKey2 key2, TKey3 key3,
			List<TObject> results) throws Throwable {
		throw new UnsupportedOperationException();
	}

	int provide(TContext context, TreeNode<TObject> resultTreeNode)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	int provide(TContext context, TKey1 key1, TreeNode<TObject> resultTreeNode)
			throws Throwable {
		throw new UnsupportedOperationException();
	}

	int provide(TContext context, TKey1 key1, TKey2 key2,
			TreeNode<TObject> resultTreeNode) throws Throwable {
		throw new UnsupportedOperationException();
	}

	int provide(TContext context, TKey1 key1, TKey2 key2, TKey3 key3,
			TreeNode<TObject> resultTreeNode) throws Throwable {
		throw new UnsupportedOperationException();
	}

	void prepare(TContext context, TObject task) throws Throwable {
		throw new UnsupportedOperationException();
	}

	void handle(TContext context, TObject task) throws Throwable {
		throw new UnsupportedOperationException();
	}

	void occur(TContext context, TObject event) throws Throwable {
		throw new UnsupportedOperationException();
	}

	/**
	 * �������齫��ʹ��ĳЩ������<br>
	 * ��ܽ��ݴ˼�飬����¼��ش���<br>
	 * ע�⣺���ظ÷���ֻ����ȷ����˵���Լ������󣬵������������ĵ�������ʹ�á�<br>
	 * �������Բ�����
	 */
	protected void using(UsingDeclarator using) {
	}

	// ///////////////////////////////////////////////////////////////
	// /////// ��������(broker)ʹ��
	// ///////////////////////////////////////////////////////////////
	/**
	 * ���ض�Ӧ����Դ������
	 */
	@SuppressWarnings("unchecked")
	ResourceServiceBase getResourceService() {
		throw new UnsupportedOperationException();
	}

	Space getSpace() {
		throw new UnsupportedOperationException();
	}

	/**
	 * ���ظ��߼���ռ���ƥ��Ĵ�����
	 */
	ServiceInvokeeBase<TObject, TContext, TKey1, TKey2, TKey3> upperMatchBroker() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	final static ServiceInvokeeBase dummy = new ServiceInvokeeBase() {

		@Override
		ServiceBase getService() {
			throw new UnsupportedOperationException();
		}

		@Override
		Class getTargetClass() {
			throw new UnsupportedOperationException();
		}

		@Override
		boolean match(Class key1Class, Class key2Class, Class key3Class,
				int mask) {
			throw new UnsupportedOperationException();
		}

	};
}
