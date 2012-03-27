package org.eclipse.jt.core.impl;

/**
 * �ö�������Ϊ��ֵ�Ĺ�ϣ��
 * 
 * @author Jeff Tang
 * @version 1.0
 */
public class ShortHashMap<T> {

	static class Entry {
		final short key;
		Object value;
		Entry next;

		Entry(short key, Object value, Entry next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}
	}
}
