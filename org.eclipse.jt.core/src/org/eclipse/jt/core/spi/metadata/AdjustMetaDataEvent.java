package org.eclipse.jt.core.spi.metadata;

import java.util.HashSet;

import org.eclipse.jt.core.invoke.Event;


/**
 * ����Ԫ����֪ͨ�¼�
 * 
 * @author Jeff Tang
 * 
 */
public class AdjustMetaDataEvent extends Event {
	/**
	 * ����ȫ��������վ��IDΪ��ǰվ��ID
	 */
	public final static String ACTION_RESET_SITE_ID = "����վ��ID";
	/**
	 * ����ȫ���������·�����Ϊture
	 */
	public final static String ACTION_PUBLISH_ALL = "ȫ���·�";

	/**
	 * �ж��Ƿ����ĳ���������
	 */
	public final boolean hasAction(String action) {
		return this.actions.contains(action);
	}

	public AdjustMetaDataEvent(String action) {
		this.putAction(action);
	}

	public AdjustMetaDataEvent(String action, String... others) {
		this.putAction(action);
		this.putActions(others);
	}

	public AdjustMetaDataEvent(String[] actions) {
		if (this.putActions(actions) == 0) {
			throw new IllegalArgumentException("û���κε��������Ķ���");
		}
	}

	private final HashSet<String> actions = new HashSet<String>();

	private boolean putAction(String action) {
		if (action == null || action.length() == 0) {
			throw new IllegalArgumentException("���������Ķ���Ϊ��");
		}
		return this.actions.add(action);
	}

	private int putActions(String[] actions) {
		int i = 0;
		if (actions != null && actions.length > 0) {
			for (String action : actions) {
				if (this.putAction(action)) {
					i++;
				}
			}
		}
		return i;
	}
}
